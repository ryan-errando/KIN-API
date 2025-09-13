package com.kinapi.service;

import com.kinapi.common.entity.BaseResponse;
import com.kinapi.common.entity.Roles;
import com.kinapi.common.repository.RolesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RolesService {

    private final RolesRepository rolesRepository;

    public BaseResponse getAllRoles(){
        List<Roles> roles = rolesRepository.findAll();
        return BaseResponse.builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK)
                .data(roles)
                .message("Successfully get all roles")
                .build();
    }

    public BaseResponse test(){
//        List<Roles> roles = rolesRepository.findAll();
        return BaseResponse.builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK)
                .data(null)
                .message("HALO TEST 123")
                .build();
    }
}
