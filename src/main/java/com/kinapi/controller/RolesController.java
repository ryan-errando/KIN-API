package com.kinapi.controller;

import com.kinapi.common.entity.BaseResponse;
import com.kinapi.service.RolesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("kin-api")
@RequiredArgsConstructor
public class RolesController {
    private final RolesService rolesService;

    @GetMapping("/roles")
    public ResponseEntity<BaseResponse> getRoles() {
        BaseResponse response = rolesService.getAllRoles();
        return new ResponseEntity<>(response, response.code());
    }

    @PostMapping("/add-roles")
    public ResponseEntity<BaseResponse> addRoles(@RequestParam String role) {
        BaseResponse response = rolesService.addRole(role);
        return new ResponseEntity<>(response, response.code());
    }
}
