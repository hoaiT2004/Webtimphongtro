package com.example.btl_ltw.service.Impl;

import com.example.btl_ltw.entity.Comment;
import com.example.btl_ltw.entity.User;
import com.example.btl_ltw.model.dto.CommentDto;
import com.example.btl_ltw.model.request.comment.AddCommentRequest;
import com.example.btl_ltw.repository.CommentRepository;
import com.example.btl_ltw.repository.UserRepository;
import com.example.btl_ltw.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<CommentDto> getAllParentCommentsByRoom_id(long room_id) {
        List<Comment> comments = commentRepository.getParentCommentsByRoom_id(room_id);
        return CommentDto.toDto(comments);
    }

    @Override
    public List<CommentDto> getAllSubCommentsByRoom_id(long room_id) {
        List<Comment> comments = commentRepository.getSubCommentsByRoom_id(room_id);
        return CommentDto.toDto(comments);
    }

    @Override
    @Transactional
    public void addComment(AddCommentRequest request) {
        // Định dạng thời gian theo kiểu "yyyy-MM-dd HH:mm:ss"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Chuyển đổi commentTime từ String sang LocalDateTime
        LocalDateTime commentTime = LocalDateTime.parse(request.getCommentTime(), formatter);

        User user = userRepository.findUserByUsername(request.getUsername()).orElse(null);

        Comment comment = Comment.builder()
                .username(request.getUsername())
                .avatar(user.getLinkAvatar())
                .room_id(Long.parseLong(request.getRoom_id()))
                .content(request.getContent())
                .commentTime(commentTime)
                .build();
        if (request.getParentCommentId() != 0) {
            comment.setParentCommentId(request.getParentCommentId());
        }
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void removeComment(Integer commentId) {
        commentRepository.deleteById(Long.valueOf(commentId));
    }

    @Override
    @Transactional
    public void editComment(String content, Long commentId) {
        commentRepository.updateCommentById(content, commentId);
    }
}
