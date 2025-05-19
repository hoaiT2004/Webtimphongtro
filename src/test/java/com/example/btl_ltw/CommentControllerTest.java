//package com.example.btl_ltw;
//
//import com.example.btl_ltw.controller.CommentController;
//import com.example.btl_ltw.model.request.comment.AddCommentRequest;
//import com.example.btl_ltw.service.CommentService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(CommentController.class)
//class CommentControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private CommentService commentService;
//
//    @Test
//    void addComment() throws Exception {
//        mockMvc.perform(post("/api/comment/add")
//                        .param("room_id", "1")
//                        .param("content", "Nội dung test"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/api/room?room_id=1"));
//
//        Mockito.verify(commentService).addComment(Mockito.any(AddCommentRequest.class));
//    }
//
//    @Test
//    void removeComment() throws Exception {
//        mockMvc.perform(get("/api/comment/remove")
//                        .param("commentId", "10")
//                        .param("roomId", "5"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/api/room?room_id=5"));
//
//        Mockito.verify(commentService).removeComment(10);
//    }
//
//    @Test
//    void confirmEdit() throws Exception {
//        mockMvc.perform(get("/api/comment/confirmEdit")
//                        .param("commentId", "20")
//                        .param("roomId", "3"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/api/room?room_id=3&commentId=20"));
//    }
//
//    @Test
//    void editComment() throws Exception {
//        mockMvc.perform(post("/api/comment/edit")
//                        .param("content", "Nội dung chỉnh sửa")
//                        .param("commentId", "12")
//                        .param("roomId", "7"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/api/room?room_id=7"));
//
//        Mockito.verify(commentService).editComment("Nội dung chỉnh sửa", 12L);
//    }
//}
