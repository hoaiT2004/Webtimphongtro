package com.example.btl_ltw.service;

import com.example.btl_ltw.model.dto.UserDto;
import com.example.btl_ltw.model.request.user.*;
import com.example.btl_ltw.model.response.user.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.concurrent.ExecutionException;

public interface UserService {

    RegisterResponse register(RegisterRequest request);

    ChangeInfoResponse changeInfo(ChangeInfoRequest request);

    ChangeAvatarResponse changeAvatar(ChangeAvatarRequest request) throws ExecutionException, InterruptedException;

    UserDto getUserById(long id);


    UserDto findUserByUsername(String username);

    ChangePasswordResponse changePassword(ChangePasswordRequest request, String username);

    CreateNewPasswordResponse createNewPassword(CreateNewPasswordRequest request);

    Page<UserDto> getAllUserForAdmin(Pageable pageable);

    List<UserDto> getAllUser();

    String findUsernameById(long userId);
}
