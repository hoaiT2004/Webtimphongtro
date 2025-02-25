package com.example.btl_ltw.model.request.user;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class ChangePasswordRequest {
    private String password, newPassword;
}

