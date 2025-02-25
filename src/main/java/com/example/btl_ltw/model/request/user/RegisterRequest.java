package com.example.btl_ltw.model.request.user;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class RegisterRequest {

    @Size(min = 5, max = 20, message = "Username must be at least 5 characters long and most 20 characters long")
    private String username;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$", message = "Password must contain both letters and numbers")
    private String password;

    @Pattern(regexp = "^.+@gmail.com$", message = "Email must contain @gmail.com on the bottom")
    private String email;

    @Size(min = 5, max = 50, message = "Fullname must be at least 5 characters long and most 50 characters long")
    private String fullname;

    @Length(max = 12, min = 10)
    private String tel;

    private String role_id;
}

