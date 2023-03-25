package com.jeremias.dev.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jeremias.dev.dtos.CommentDto;
import com.jeremias.dev.model.Comment;
@Service
public class CommentMapper {
	public Comment mapFromDto(CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getCommentText())
                .author(commentDto.getCommentAuthor())
                .build();
    }

    public List<CommentDto> mapToDtoList(List<Comment> comments) {
        return comments.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public CommentDto mapToDto(Comment comment) {
        return CommentDto.builder()
                .commentText(comment.getText())
                .commentAuthor(comment.getAuthor())
                .likeCount(comment.likeCount())
                .disLikeCount(comment.disLikeCount())
                .build();
    }
}
