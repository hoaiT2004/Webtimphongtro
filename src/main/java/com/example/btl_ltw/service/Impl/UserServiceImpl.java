package com.example.btl_ltw.service.Impl;

import com.example.btl_ltw.config.MyUserDetails;
import com.example.btl_ltw.entity.Role;
import com.example.btl_ltw.entity.User;
import com.example.btl_ltw.model.dto.UserDto;
import com.example.btl_ltw.model.request.user.*;
import com.example.btl_ltw.model.response.user.*;
import com.example.btl_ltw.repository.RoleRepository;
import com.example.btl_ltw.repository.UserRepository;
import com.example.btl_ltw.service.FileService;
import com.example.btl_ltw.service.UserService;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileService fileService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username).orElseThrow(() -> new RuntimeException("Could not find user by username"));
        Role role = roleRepository.findById(user.getRole_id()).orElse(null);
        return new MyUserDetails(user, role);
    }

    @Override
    public UserDto findUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null) return null;
        return UserDto.toDto(user);
    }

    @Override
    @Transactional
    public ChangePasswordResponse changePassword(ChangePasswordRequest request, String username) {
        UserDetails userDetails = loadUserByUsername(username);
        if (request.getPassword().equals(userDetails.getPassword())) {
            throw new InvalidParameterException("Wrong password");
        }
        userRepository.updatePassword(passwordEncoder.encode(request.getNewPassword()), username);
        return new ChangePasswordResponse(username);
    }

    @Override
    @Transactional
    public CreateNewPasswordResponse createNewPassword(CreateNewPasswordRequest request) {
        userRepository.updatePassword(passwordEncoder.encode(request.getNewPassword()), request.getUsername());
        return new CreateNewPasswordResponse(request.getUsername());
    }

    @Override
    public Page<UserDto> getAllUserForAdmin(Pageable pageable) {
        Page<User> userPage = userRepository.findAllUserExpectAdmin(pageable);
        // Chuyển đổi Page<User> sang Page<UserDto>
        Page<UserDto> userDtoPage = userPage.map(UserDto::toDto);
        return userDtoPage;
    }

    @Override
    public List<UserDto> getAllUser() {
        List<User> users = userRepository.findAllUser();
        return UserDto.toDto(users);
    }

    @Override
    public String findUsernameById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("asd"));
        return user.getUsername();
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        User user1 = userRepository.findUserByUsername(registerRequest.getUsername()).orElse(null) ;
        if (user1 != null) throw new EntityExistsException("Account existed!");

        var user = User.builder()
                .username(registerRequest.getUsername().toLowerCase())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullname(registerRequest.getFullname())
                .email(registerRequest.getEmail())
                .tel(registerRequest.getTel())
                .role_id(Long.parseLong(registerRequest.getRole_id()))
                .linkAvatar("https://res.cloudinary.com/hoaptit/image/upload/v1714322737/samples/people/bicycle.jpg")
                .build();
        userRepository.save(user);
        return new RegisterResponse(user.getId());
    }

    @Override
    @Transactional
    public ChangeInfoResponse changeInfo(ChangeInfoRequest request) {
        userRepository.updateInfoUser(request.getTel(), request.getFullname(), request.getUsername());
        return new ChangeInfoResponse(request.getUsername());
    }

    @Override
    @Transactional
    public ChangeAvatarResponse changeAvatar(ChangeAvatarRequest request) throws ExecutionException, InterruptedException {
        CompletableFuture<String> futureUrl = fileService.uploadFile(request.getFile());
        String linkAvatar = futureUrl.get();
        userRepository.updateAvatarUser(linkAvatar, request.getUsername());
        return new ChangeAvatarResponse(request.getUsername());
    }

    @Override
    public UserDto getUserById(long id) {
        return UserDto.toDto(userRepository.findById(id).orElse(null));
    }


}
