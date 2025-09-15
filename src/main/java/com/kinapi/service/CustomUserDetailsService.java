package com.kinapi.service;

import com.kinapi.common.entity.Users;
import com.kinapi.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(username).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        return new User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}