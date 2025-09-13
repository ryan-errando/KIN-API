package com.kinapi.service;

import com.kinapi.common.dto.UserRegisterDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.common.entity.Users;
import com.kinapi.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public BaseResponse addUser(UserRegisterDto userRegisterDto) {
        Users checkUser = userRepository.findByEmail(userRegisterDto.getEmail());
        if(checkUser == null){
            Users user = Users.builder()
                    .name(userRegisterDto.getName())
                    .password(userRegisterDto.getPassword())
                    .email(userRegisterDto.getEmail())
                    .dob(userRegisterDto.getDob())
                    .build();
            userRepository.save(user);
            return BaseResponse.builder()
                    .code(HttpStatus.CREATED)
                    .status(HttpStatus.CREATED.value())
                    .message("Successfully added user")
                    .data(user)
                    .build();
        } else {
            return BaseResponse.builder()
                    .code(HttpStatus.CONFLICT)
                    .status(HttpStatus.CONFLICT.value())
                    .message("Existing user already exists")
                    .data(null)
                    .build();
        }
    }
}
