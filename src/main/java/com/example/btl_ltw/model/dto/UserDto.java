package com.example.btl_ltw.model.dto;

import com.example.btl_ltw.entity.User;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {

    public long id;

    private String username;

    private String password;

    private String fullname;

    private String tel;

    private String email;

    private long role_id;

    private String linkAvatar;

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullname(user.getFullname())
                .password(user.getPassword())
                .tel(user.getTel())
                .role_id(user.getRole_id())
                .username(user.getUsername())
                .linkAvatar(user.getLinkAvatar())
                .build();
    }

    public static User toUser(UserDto user) {
        if (user == null) {
            return null;
        }
        return User.builder()
                .email(user.getEmail())
                .fullname(user.getFullname())
                .password(user.getPassword())
                .tel(user.getTel())
                .role_id(user.getRole_id())
                .username(user.getUsername())
                .linkAvatar(user.getLinkAvatar())
                .build();
    }

    public static List<UserDto> toDto(List<User> users) {
        return users.stream()
                .map(UserDto::toDto)
                .collect(Collectors.toList());
    }

    public static List<User> toUser(List<UserDto> userDtos) {
        return userDtos.stream()
                .map(UserDto::toUser)
                .collect(Collectors.toList());
    }

}
