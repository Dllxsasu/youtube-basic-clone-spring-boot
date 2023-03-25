package com.jeremias.dev.model;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Min;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
	 @Id
	    private String id;
	    private String text;
	    private String author;
	    @Min(value = 0)
	    private AtomicInteger likeCount = new AtomicInteger(0);
	    @Min(value = 0)
	    private AtomicInteger disLikeCount = new AtomicInteger(0);

	    public int likeCount() {
	        return likeCount.get();
	    }

	    public int disLikeCount() {
	        return disLikeCount.get();
	    }
}
