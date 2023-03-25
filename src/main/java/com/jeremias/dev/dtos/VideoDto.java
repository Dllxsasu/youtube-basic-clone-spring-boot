package com.jeremias.dev.dtos;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.jeremias.dev.enums.videoStatus;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoDto {
	private String videoId;
    @NotBlank
    private String userId;
    @NotBlank
    private String videoName;
    @NotBlank
    private String description;
    @Size(min = 1)
    private List<String> tags;
    private videoStatus videoStatus;
    @NotBlank
    private String url;
    @NotBlank
    private String thumbnailUrl;
    @Min(value = 0)
    private int likeCount;
    @Min(value = 0)
    private int dislikeCount;
}
