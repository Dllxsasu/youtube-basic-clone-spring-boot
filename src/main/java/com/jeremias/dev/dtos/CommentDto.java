package com.jeremias.dev.dtos;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
	@NotBlank
    private String commentText;
    @NotBlank
    private String commentAuthor;
    @Min(value = 0)
    private int likeCount;
    @Min(value = 0)
    private int disLikeCount;
}
