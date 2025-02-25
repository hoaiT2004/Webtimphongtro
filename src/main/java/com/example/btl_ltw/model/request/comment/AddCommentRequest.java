package com.example.btl_ltw.model.request.comment;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddCommentRequest {

    private String username;

    private String content;

    private String commentTime;

    private String room_id;
}
