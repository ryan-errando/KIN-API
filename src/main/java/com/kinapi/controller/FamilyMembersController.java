package com.kinapi.controller;

import com.kinapi.common.entity.BaseResponse;
import com.kinapi.service.FamilyMembersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("kin-api")
@RequiredArgsConstructor
public class FamilyMembersController {
    private final FamilyMembersService familyMembersService;

    @PostMapping("/join-family-group")
    public ResponseEntity<BaseResponse> joinFamilyGroup(
            @RequestParam(name = "invitation_code") String invitationCode
    ){
        BaseResponse response = familyMembersService.joinFamilyGroup(invitationCode);
        return new ResponseEntity<>(response, response.code());
    }

    @DeleteMapping("/leave-group")
    public ResponseEntity<BaseResponse> leaveFamilyGroup(){
        BaseResponse response = familyMembersService.leaveFamilyGroup();
        return new ResponseEntity<>(response, response.code());
    }

    @DeleteMapping("/remove-family-member")
    public ResponseEntity<BaseResponse> removeFamilyMember(
            @RequestParam(name = "family_member_id") UUID familyMemberId
    ){
        BaseResponse response = familyMembersService.removeFamilyMember(familyMemberId);
        return new ResponseEntity<>(response, response.code());
    }
}
