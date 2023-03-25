package com.jeremias.dev;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;

@SpringBootApplication(exclude = {
				
})
public class YoutubeCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(YoutubeCloneApplication.class, args);
	}

}
