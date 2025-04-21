package com.example.btl_ltw.model.dto;

import com.example.btl_ltw.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentDto {

    private long id;

    private String username;

    private String avatar;

    private String content;

    private String commentTime;

    private long room_id;

    private long parentCommentId;

    public static CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        var commentDto = CommentDto.builder()
                .id(comment.getId())
                .room_id(comment.getRoom_id())
                .commentTime(formatter.format(comment.getCommentTime()))
                .content(comment.getContent())
                .username(comment.getUsername())
                .avatar(comment.getAvatar())
                .parentCommentId(comment.getParentCommentId())
                .build();
        return commentDto;
    }

    public static Comment toComment(CommentDto commentDto) {
        if (commentDto == null) {
            return null;
        }
        var comment = Comment.builder()
                .id(commentDto.getId())
                .room_id(commentDto.getRoom_id())
                .commentTime(LocalDateTime.parse(commentDto.getCommentTime()))
                .content(commentDto.getContent())
                .username(commentDto.getUsername())
                .avatar(commentDto.getAvatar())
                .parentCommentId(commentDto.getParentCommentId())
                .build();
        return comment;
    }

    public static List<CommentDto> toDto(List<Comment> comments) {
        return comments.stream()
                .map(CommentDto::toDto)
                .collect(Collectors.toList());
    }

    public static List<Comment> toComment(List<CommentDto> commentDtos) {
        return commentDtos.stream()
                .map(CommentDto::toComment)
                .collect(Collectors.toList());
    }
}
