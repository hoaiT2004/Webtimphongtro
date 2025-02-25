package com.example.btl_ltw.controller;

import com.example.btl_ltw.model.dto.UserDto;
import com.example.btl_ltw.model.request.comment.AddCommentRequest;
import com.example.btl_ltw.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public String addComment(@ModelAttribute AddCommentRequest request) {
        commentService.addComment(request);
        return "redirect:/api/room?room_id="+request.getRoom_id();
    }

    @GetMapping("/remove")
    public String removeComment(@RequestParam(name="commentId") Integer commentId
                                , @RequestParam(name="roomId") Integer roomId) {
        commentService.removeComment(commentId);
        return "redirect:/api/room?room_id="+roomId;
    }

    @GetMapping("/confirmEdit")
    public String confirmEdit(@RequestParam(name="commentId") Integer commentId,
                                @RequestParam(name="roomId") Integer roomId) {
        return "redirect:/api/room?room_id="+roomId+"&commentId="+commentId;
    }

    @PostMapping("/edit")
    public String editComment(@RequestParam(name="content") String content,
                              @RequestParam(name="commentId") Integer commentId,
                              @RequestParam(name="roomId") Integer roomId) {
        commentService.editComment(content, Long.valueOf(commentId));
        return "redirect:/api/room?room_id="+roomId;
    }
}
