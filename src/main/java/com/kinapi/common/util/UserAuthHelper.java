package com.kinapi.common.util;

import com.kinapi.common.entity.Users;
import com.kinapi.common.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserAuthHelper {

    private static UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        UserAuthHelper.userRepository = userRepository;
    }

    public static Users getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            log.info("[getUser] No authentication found in SecurityContext");
            return null;
        }

        String email = authentication.getName();
        Users user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.info("[getUser] User not found for email: {}", email);
        }
        return user;
    }
}
