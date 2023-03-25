package com.jeremias.dev.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeremias.dev.dtos.UserInfoDTO;
import com.jeremias.dev.exception.YoutubeCloneException;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserValidationService {
	//Seteamos info del user
	@Value("${auth0.userinfo}")
    private String userInfoEndpoint;
	
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    public UserInfoDTO validate(String authorizationHeader) {
    	//verificamos si el header 
        if (authorizationHeader.startsWith("Bearer ")) {
        	//Obtenemos el token
            String token = authorizationHeader.substring(7);
            //Creamos nuestro request
            var request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(userInfoEndpoint))
                    .setHeader("Authorization", String.format("Bearer %s", token))
                    .build();

            try {
            	//Mandamos aqui hacemos la solicitud al endpoint para obtener la info del usuario
                HttpResponse<String> responseString = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
               //mapeamos el json obtenido y a userInfoDto
                return objectMapper.readValue(responseString.body(), UserInfoDTO.class);
            } catch (Exception exception) {
                throw new YoutubeCloneException("Exception Occurred when validating user", exception);
            }
        } else {
            throw new YoutubeCloneException("Invalid Access Token");
        }
    }
}
