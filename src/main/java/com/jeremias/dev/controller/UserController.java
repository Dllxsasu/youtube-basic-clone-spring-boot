package com.jeremias.dev.controller;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.jeremias.dev.dtos.UserInfoDTO;
import com.jeremias.dev.service.UserRegistrationService;
import com.jeremias.dev.service.UserService;
import com.jeremias.dev.service.UserValidationService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	//recordarmos que estamos obteniendo los beans a traves del constructur con el lombo argsContructor
	private final UserService userService;
    private final UserValidationService userValidationService;
    private final UserRegistrationService userRegistrationService;

    @GetMapping("{id}/history")
    @ResponseStatus(HttpStatus.OK)
    public Set<String> userHistory(@PathVariable String id) {
        return userService.getHistory(id);
    }

    @GetMapping("validate")
    @ResponseStatus(HttpStatus.OK)
    public UserInfoDTO registerUser(HttpServletRequest httpServletRequest) {
    	//Obtenemos la data desde el endpointInfo, y devolvemos como 
        var userInfoDTO = userValidationService.validate(httpServletRequest.getHeader("Authorization"));
        
        userRegistrationService.register(userInfoDTO);
        return userInfoDTO;
    }

    @PostMapping("subscribe/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void subscribeUser(@PathVariable String userId) {
        userService.subscribeUser(userId);
    }
}
