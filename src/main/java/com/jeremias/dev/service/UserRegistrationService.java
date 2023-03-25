package com.jeremias.dev.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jeremias.dev.dtos.UserInfoDTO;
import com.jeremias.dev.model.User;
import com.jeremias.dev.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {
	 private final UserRepository userRepository;

	    public void register(UserInfoDTO userInfoDTO) {
	    	//verificar si ya existe la cuenta en la bd
	        Optional<User> existingUserOpt = userRepository.findByEmailAddress(userInfoDTO.getEmail());
	        //Si existe todo okey
	        if (existingUserOpt.isPresent()) {
	            userInfoDTO.setId(existingUserOpt.get().getId());
	            return;
	        }
	        var user = new User();
	        //Guardamos nuestros datos una vez objetino la data
	        user.setSub(userInfoDTO.getSub());
	        user.setEmailAddress(userInfoDTO.getEmail());
	        user.setFirstName(userInfoDTO.getGivenName());
	        user.setLastName(userInfoDTO.getFamilyName());
	        user.setFullName(userInfoDTO.getName());
	        user.setPicture(userInfoDTO.getPicture());

	        
	        userRepository.save(user);
	    }
}
