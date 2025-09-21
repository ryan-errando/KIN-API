package com.kinapi.service;

import com.kinapi.common.dto.LoginDto;
import com.kinapi.common.dto.LoginResponseDto;
import com.kinapi.common.dto.UserProfileDto;
import com.kinapi.common.dto.UserRegisterDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.common.entity.Users;
import com.kinapi.common.repository.UserRepository;
import com.kinapi.common.util.DateHelper;
import com.kinapi.common.util.JwtUtil;
import com.kinapi.common.util.UserAuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public BaseResponse addUser(UserRegisterDto userRegisterDto) {
        log.debug("\n[addUser] Adding new user...\nname: {}\nemail: {}", userRegisterDto.getName(), userRegisterDto.getEmail());
        Users checkUser = userRepository.findByEmail(userRegisterDto.getEmail()).orElse(null);
        if(checkUser == null){
            Users user = Users.builder()
                    .name(userRegisterDto.getName())
                    .password(passwordEncoder.encode(userRegisterDto.getPassword()))
                    .email(userRegisterDto.getEmail())
                    .dob(userRegisterDto.getDob())
                    .build();
            userRepository.save(user);
            log.info("[addUser] Successfully add a new user");
            return BaseResponse.builder()
                    .code(HttpStatus.CREATED)
                    .status(HttpStatus.CREATED.value())
                    .message("Successfully added user")
                    .data(user)
                    .build();
        } else {
            log.error("[addUser] Failed to add new user");
            return BaseResponse.builder()
                    .code(HttpStatus.CONFLICT)
                    .status(HttpStatus.CONFLICT.value())
                    .message("Existing user already exists")
                    .data(null)
                    .build();
        }
    }

    public BaseResponse authenticateUser(LoginDto loginDto) {
        try {
            log.info("[authenticateUser] Authenticating user with email: {}", loginDto.getEmail());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getPassword()
                    )
            );

            Users user = userRepository.findByEmail(loginDto.getEmail()).orElse(null);
            String jwtToken = jwtUtil.generateToken(user.getEmail());

            LoginResponseDto loginResponse = new LoginResponseDto(jwtToken, user.getEmail());

            return BaseResponse.builder()
                    .code(HttpStatus.OK)
                    .status(HttpStatus.OK.value())
                    .message("Login successful")
                    .data(loginResponse)
                    .build();
        } catch (Exception e) {
            log.error("Authentication failed for email: {}, error: {}", loginDto.getEmail(), e.getMessage(), e);
            return BaseResponse.builder()
                    .code(HttpStatus.UNAUTHORIZED)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid credentials")
                    .data(null)
                    .build();
        }
    }

    public BaseResponse getUserProfile() {
        try {
            Users user = UserAuthHelper.getUser();
            log.info("[getUserProfile] Getting user profile for email: {}",  user.getEmail());

            if (user != null) {
                return BaseResponse.builder()
                        .code(HttpStatus.OK)
                        .status(HttpStatus.OK.value())
                        .message("User profile retrieved successfully")
                        .data(UserProfileDto.builder()
                                .userId(user.getId().toString())
                                .email(user.getEmail())
                                .name(user.getName())
                                .dob(DateHelper.formatToddMMyyyy(user.getDob()))
                                .avatarUrl(user.getAvatarUrl())
                                .build())
                        .build();
            } else {
                return BaseResponse.builder()
                        .code(HttpStatus.NOT_FOUND)
                        .status(HttpStatus.NOT_FOUND.value())
                        .message("User not found")
                        .data(null)
                        .build();
            }
        } catch (Exception e) {
            log.error("[getUserProfile] Failed getting user profile due to: {}", e.getMessage(), e);
            return BaseResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error retrieving user profile")
                    .data(null)
                    .build();
        }
    }

    public BaseResponse logout() {
        try {
            log.info("[logout] Logout user...");
            SecurityContextHolder.clearContext();
            return BaseResponse.builder()
                    .code(HttpStatus.OK)
                    .status(HttpStatus.OK.value())
                    .message("Logout successful")
                    .data(null)
                    .build();
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage(), e);
            return BaseResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error during logout")
                    .data(null)
                    .build();
        }
    }
}
