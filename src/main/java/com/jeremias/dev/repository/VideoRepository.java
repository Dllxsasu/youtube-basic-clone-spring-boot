package com.jeremias.dev.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jeremias.dev.model.Video;

public interface VideoRepository extends MongoRepository<Video, String>  {
	 List<Video> findByUserId(String userId);

	    List<Video> findByTagsIn(List<String> tags);

	    List<Video> findByIdIn(Set<String> likedVideos);
}
