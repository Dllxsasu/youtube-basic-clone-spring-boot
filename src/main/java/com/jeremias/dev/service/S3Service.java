package com.jeremias.dev.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.jeremias.dev.exception.YoutubeCloneException;

import lombok.RequiredArgsConstructor;

@Service

public class S3Service implements FileService {
	
	//Seteamos el bucketname
		@Value("${application.bucket.name}")
	 	public  String BUCKET_NAME = "bucketsasudemoaws";
		
	    private  final AmazonS3 awsS3Client;
	    //hacemos el autowired por constructor
	    @Autowired
		public S3Service(AmazonS3 awsS3Client) {
			this.awsS3Client = awsS3Client;
		}

	    @Override
	    public String upload(MultipartFile file) {
	    	//Obtenemos la extencion del archivo
	    	String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
	        ///Creamos una clave unica para el archivo con uuid y aparte agremaos la exstencion del archivo
	    	var key = String.format("%s.%s", UUID.randomUUID(), extension);
	        //Objecto para poner dentro del bucket, como sabemos un objecto(es un file) dentro del bucket
	    	var metadata = new ObjectMetadata();
	    	//seteamos las cositas para subir al s3 
	        metadata.setContentLength(file.getSize());
	        metadata.setContentType(file.getContentType());
	        try {
	        	awsS3Client.putObject(BUCKET_NAME, key, file.getInputStream(), metadata);
	        } catch (IOException e) {
	            throw new YoutubeCloneException("An Exception Occurred while uploading file");
	        }
	        //Seteamos el name del bucket
	        awsS3Client.setObjectAcl(BUCKET_NAME, key, CannedAccessControlList.PublicRead);
	        //enviamos y nos retarnos la url ps
	        return awsS3Client.getUrl(BUCKET_NAME, key).getPath();
	    }

		@Override
		public void deleteFile(String fileName) {
			// TODO Auto-generated method stub
			
		}
}
