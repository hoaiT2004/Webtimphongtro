package com.example.btl_ltw.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private long parentCommentId;

    private String username;

    private String avatar;

    private String content;

    private LocalDateTime commentTime;

    private long room_id;
}
