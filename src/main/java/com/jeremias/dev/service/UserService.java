package com.jeremias.dev.service;

import java.util.Set;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.Jwt;
import com.jeremias.dev.dtos.VideoDto;
import com.jeremias.dev.exception.YoutubeCloneException;
import com.jeremias.dev.model.User;
import com.jeremias.dev.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

    public void addVideo(VideoDto videoDto) {
        
    	var currentUser = getCurrentUser();
        currentUser.addToVideoHistory(videoDto.getVideoId());
        userRepository.save(currentUser);
    }

    public Set<String> getHistory(String id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new YoutubeCloneException("Cannot Find User with ID - " + id));
        return user.getVideoHistory();
    }

    public void addToLikedVideos(String videoId) {
        var user = getCurrentUser();
        user.addToLikedVideos(videoId);
        userRepository.save(user);
    }

    public void removeFromLikedVideos(String videoId) {
        var user = getCurrentUser();
        //se va al metodo para remover de los videos que te gustan, hay un set dentro del model user
        user.removeFromLikedVideos(videoId);
        userRepository.save(user);
    }

    public void addToDisLikedVideo(String videoId) {
        var user = getCurrentUser();
        user.addToDisLikedVideo(videoId);
        userRepository.save(user);
    }

    public void removeFromDisLikedVideo(String videoId) {
        var user = getCurrentUser();
        user.removeFromDisLikedVideo(videoId);
        userRepository.save(user);
    }

    public boolean ifLikedVideo(String videoId) {
        return getCurrentUser().getLikedVideos().stream().anyMatch(id -> id.equals(videoId));
    }

    public boolean ifDisLikedVideo(String videoId) {
        return getCurrentUser().getDisLikedVideos().stream().anyMatch(id -> id.equals(videoId));
    }

    private User getCurrentUser() {
    	//Obtemos de la authentificaion el token
    	//SecuritContexColder =SpringContenedorSegurida obtemos el contexto actual, obtemos el Authentficate
    	//con el getPrincipal obtemos el objeto USER
    	//de aqui jalamos el sub donde se encuentra la clave tokens
        String sub = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaim("sub");
        //buscamos que se encuentre o caso contratio lanzamos un thowError, para informar que no se pudo encontrar el usuario
        return userRepository.findBySub(sub).orElseThrow(() -> new YoutubeCloneException("Cannot find user with sub - " + sub));
    }

    public Set<String> getLikedVideos(String userId) {
        //Obtemos a list<string> donde se encuentramos una lsita de videos
    	var user = userRepository.findById(userId).orElseThrow(() -> new YoutubeCloneException("Invalid user - " + userId));
        return user.getLikedVideos();
    }

    public void subscribeUser(String userId) {
    	//UserID cuenta a suscribir
    	//Obtemos nuestro usuuario como sabemos del Claims(SUb) <= token, y la busquedad
        var currentUser = getCurrentUser();
        ///Añadimos el id de la cuenta a suscribirnos 
        currentUser.addToSubscribedUsers(userId);
        //lo buscamos si no lanzamos a throwError
        var subscribedToUser = userRepository.findById(userId).orElseThrow(() -> new YoutubeCloneException("Invalid User - " + userId));
       //nos añadimos como suscrimos a la cuenta que queremos suscribirmons
        subscribedToUser.addToSubscribers(subscribedToUser.getId());
        // save ambos campos
        userRepository.save(currentUser);
        userRepository.save(subscribedToUser);
    }
}
