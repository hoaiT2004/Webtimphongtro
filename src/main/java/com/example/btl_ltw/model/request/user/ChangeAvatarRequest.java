package com.example.btl_ltw.model.request.user;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class ChangeAvatarRequest {
    private String username;
    private MultipartFile file;
}

