package com.jeremias.dev.mapper;

import org.springframework.stereotype.Service;

import com.jeremias.dev.dtos.VideoDto;
import com.jeremias.dev.model.Video;

@Service
public class VideoMapper {
	
	public VideoDto mapToDto(Video video) {
        return VideoDto.builder()
                .videoId(video.getId())
                .url(video.getUrl())
                .description(video.getDescription())
                .tags(video.getTags())
                .videoName(video.getTitle())
                .videoStatus(video.getVideoStatus())
                .userId(video.getUserId())
                .thumbnailUrl(video.getThumbnailUrl())
                .likeCount(video.getLikes().get())
                .dislikeCount(video.getDisLikes().get())
                .build();
    }
}
