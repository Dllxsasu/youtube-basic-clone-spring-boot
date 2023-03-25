package com.jeremias.dev.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	//Tenemos 2 metodos que se va a ultizar, y para mover los archivos en ec3
	
	String upload(MultipartFile file);
    void deleteFile(String fileName);

}
