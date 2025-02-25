package com.example.btl_ltw.repository;

import com.example.btl_ltw.entity.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment AS c " +
            "WHERE c.room_id = :room_id " +
            "ORDER BY c.commentTime desc")
    List<Comment> getCommentsByRoom_id(@Param("room_id") long room_id);

    @Query("UPDATE Comment AS c " +
            "SET c.content = :content " +
            "WHERE c.id = :commentId")
    @Modifying
    @Transactional
    void updateCommentById(@Param("content") String content, @Param("commentId") Long commentId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.room_id = :room_id")
    void deleteCommentsByRoom_id(@Param("room_id") long room_id);

}
