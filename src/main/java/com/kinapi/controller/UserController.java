package com.kinapi.controller;

import com.kinapi.common.dto.LoginDto;
import com.kinapi.common.dto.UserRegisterDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("kin-api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/add-user")
    public ResponseEntity<BaseResponse> addUser(
            @RequestBody UserRegisterDto userRegisterDto
    ){
        BaseResponse respone = userService.addUser(userRegisterDto);
        return new ResponseEntity<>(respone, respone.code());
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(
            @RequestBody LoginDto loginDto
    ) {
        BaseResponse response = userService.authenticateUser(loginDto);
        return new ResponseEntity<>(response, response.code());
    }

    @GetMapping("/profile")
    public ResponseEntity<BaseResponse> getUserProfile() {
        BaseResponse response = userService.getUserProfile();
        return new ResponseEntity<>(response, response.code());
    }

}
