package com.kinapi.controller;

import com.kinapi.common.entity.BaseResponse;
import com.kinapi.service.RolesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("kin-api")
@RequiredArgsConstructor
public class UserController {

    private final RolesService rolesService;
    
    @GetMapping("/roles")
    public ResponseEntity<BaseResponse> getRoles() {
        BaseResponse response = rolesService.getAllRoles();
        return new ResponseEntity<>(response, response.code());
    }

    @GetMapping("/test")
    public ResponseEntity<BaseResponse> test() {
        BaseResponse response = rolesService.getAllRoles();
        return new ResponseEntity<>(response, response.code());
    }
}
