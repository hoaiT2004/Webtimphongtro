package com.example.btl_ltw.service;

import java.util.List;
import com.example.btl_ltw.model.dto.CommentDto;
import com.example.btl_ltw.model.request.comment.AddCommentRequest;

public interface CommentService {

    List<CommentDto> getAllCommentsByRoom_id(long room_id);

    void addComment(AddCommentRequest request);

    void removeComment(Integer commentId);

    void editComment(String content, Long commentId);
}
