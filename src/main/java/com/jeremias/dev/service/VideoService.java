package com.jeremias.dev.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jeremias.dev.dtos.CommentDto;
import com.jeremias.dev.dtos.UploadVideoResponse;
import com.jeremias.dev.dtos.VideoDto;
import com.jeremias.dev.enums.videoStatus;
import com.jeremias.dev.exception.YoutubeCloneException;
import com.jeremias.dev.mapper.CommentMapper;
import com.jeremias.dev.mapper.VideoMapper;
import com.jeremias.dev.model.Video;
import com.jeremias.dev.repository.VideoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final S3Service s3Service;
    private final UserService userService;
    private final VideoMapper videoMapper;
    private final CommentMapper commentMapper;
    
    public UploadVideoResponse uploadVideo(MultipartFile file, String userId) {
    	//Subimos el file a s3 y nos devolvera una URI
        String url = s3Service.upload(file);
        var video = new Video();
        //Seteamos la URL
        video.setUrl(url);
        //fail-fast
        Objects.requireNonNull(userId);
        
        video.setUserId(userId);
        videoRepository.save(video);
        //devolvemos el nuevo video subido con su URL
        return new UploadVideoResponse(video.getId(), url);
    }
    public String uploadThumbnail(MultipartFile file, String videoId) {
    	//Se obtenie el video 
        var video = getVideoById(videoId);
        
        String url = s3Service.upload(file);
        
        video.setThumbnailUrl(url);
        
        videoRepository.save(video);
        //seteamos todo
        return url;
    }
    

    public List<VideoDto> getAllVideosByChannel(String userId) {
    	
        List<Video> videos = videoRepository.findByUserId(userId);
        //buscamos por Usuario  y obtemos todo, de ahi lo hacemos stream, convertimos a Dto y devolvemos la lista
        return videos.stream()
                .map(videoMapper::mapToDto)
                .collect(Collectors.toList());
        
    }
    public VideoDto editVideoMetadata(VideoDto videoMetaDataDto) {
    	//Obtemos el video
        var video = getVideoById(videoMetaDataDto.getVideoId());
        //Seteamos los campos que deseamos cambiar
        video.setTitle(videoMetaDataDto.getVideoName());
        video.setDescription(videoMetaDataDto.getDescription());
        video.setUrl(videoMetaDataDto.getUrl());
        // Ignore Channel ID as it should not be possible to change the Channel of a Video
        video.setTags(videoMetaDataDto.getTags());
        video.setVideoStatus(videoMetaDataDto.getVideoStatus());
        //Ingnoramos los campos que no debemos cambiar como son las vistas,likes,etc.
        // View Count is also ignored as its calculated independently
        videoRepository.save(video);
        //map to Dto 
        return videoMapper.mapToDto(video);
    }

    public void deleteVideo(String id) {
    	//Obtemos la url Del video
        String videoUrl = getVideo(id).getUrl();
        //Eliminamos el video de s3 
        s3Service.deleteFile(videoUrl);
    }

    public List<VideoDto> getSuggestedVideos(String userId) {
    	//Obtemos los videos que nos gustan, guardamos el id de sus videos recordar 
        Set<String> likedVideos = userService.getLikedVideos(userId);
        //Una vez obtenidos el id buscamos en videos y obtemos la lista de videtos
        List<Video> likedVideoList = videoRepository.findByIdIn(likedVideos);
        ///Obtemos tags de los videos que nos gustan, obtenido los tagas de esos videos  
        List<String> tags = likedVideoList.stream()
        		//oBtenemos la lsita de tagas 
                .map(Video::getTags)
                //Convertimos la 'matris' de tags que tenemos en una lista
                .flatMap(List::stream)
                //convertimos todo a lista 
                .collect(Collectors.toList());
        		//Buscamos los videos que tengan esos tags
        return videoRepository.findByTagsIn(tags)
                .stream()
                //devolvemos  max 5
                .limit(5)
                //mapeamos
                .map(videoMapper::mapToDto)
                .collect(Collectors.toList());
    }
    public List<VideoDto> getAllVideos() {
    	//Obtemos todos los videos
        return videoRepository.findAll()
                .stream()
                //Filtramos que solo sean videos publicos
                .filter(video -> videoStatus.PUBLIC.equals(video.getVideoStatus()))
                .map(videoMapper::mapToDto)
                .collect(Collectors.toList());
    }
    
    
    public VideoDto getVideo(String id) {
        var videoDto = videoMapper.mapToDto(getVideoById(id));
        // This method is called when the Get Video Metadata API is called, which is usually called when user clicks on
        // a video, hence we will increase the view count of the video.
        increaseViewCount(videoDto);
        return videoDto;
    }
    private void increaseViewCount(VideoDto videoDto) {
        var videoById = getVideoById(videoDto.getVideoId());
        //Incrementamos el numero de vista con atomicInter
        //The primary use of AtomicInteger is when you are in a multithreaded context 
        //and you need to perform thread safe operations on an integer without using synchronized
        //en este caso nosotros lo utilizamos porque hay la posibilidad de que varios usuarios al mismo tiempo vean el video
        videoById.increaseViewCount();
        videoRepository.save(videoById);
    }

    private Video getVideoById(String id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new YoutubeCloneException("Cannot find Video with ID - " + id));
    }
    public VideoDto like(String videoId) {
        var video = getVideoById(videoId);
        //verificamos si le dio like y removemos dislike o like o en ultimo caso aumentamos el lke
        if (userService.ifLikedVideo(videoId)) {
            video.decreaseLikeCount();
            userService.removeFromLikedVideos(videoId);
        } else if (userService.ifDisLikedVideo(videoId)) {
            video.decreaseDisLikeCount();
            userService.removeFromDisLikedVideo(videoId);
        } else {
            video.increaseLikeCount();
            userService.addToLikedVideos(videoId);
        }
        videoRepository.save(video);
        return videoMapper.mapToDto(video);
    }

    public VideoDto dislike(String videoId) {
    	//the same as before
        var video = getVideoById(videoId);

        if (userService.ifDisLikedVideo(videoId)) {
            video.decreaseDisLikeCount();
            userService.removeFromDisLikedVideo(videoId);
        } else if (userService.ifLikedVideo(videoId)) {
            video.decreaseLikeCount();
            userService.removeFromLikedVideos(videoId);
        } else {
            video.increaseDisLikeCount();
            userService.addToDisLikedVideo(videoId);
        }
        videoRepository.save(video);
        return videoMapper.mapToDto(video);
    }

    public void addComment(CommentDto commentDto, String videoId) {
    	//Buscamos x id
        var video = getVideoById(videoId);
        var comment = commentMapper.mapFromDto(commentDto);
        video.addComment(comment);
        //añadimos y guardamos
        videoRepository.save(video);
    }

    public List<CommentDto> getAllComments(String videoId) {
    	//obtemos todos los comentarios
        return videoRepository.findById(videoId)
                .stream()
                //convertimos la lista en una lista<DTO>., c 
                .map(video -> commentMapper.mapToDtoList(video.getComments()))
                //verificamos si esta vacio o empty
                .findAny()
                //Caso sea asi le devolvemos una lista vacia
                .orElse(Collections.emptyList());
    }
}
