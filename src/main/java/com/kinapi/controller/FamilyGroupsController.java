package com.kinapi.controller;

import com.kinapi.common.dto.CreateFamilyGroupsDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.service.FamilyGroupsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("kin-api")
@RequiredArgsConstructor
public class FamilyGroupsController {
    private final FamilyGroupsService familyGroupsService;

    @PostMapping("/create-new-family-group")
    public ResponseEntity<BaseResponse> createNewFamilyGroup(
            @Valid @RequestBody CreateFamilyGroupsDto createFamilyGroupsDto
    ) {
        BaseResponse response = familyGroupsService.createNewFamilyGroup(createFamilyGroupsDto);
        return new ResponseEntity<>(response, response.code());
    }

    @PostMapping("/join-family-group")
    public ResponseEntity<BaseResponse> joinFamilyGroup(
        @RequestParam(name = "invitation_code") String invitationCode
    ){
        BaseResponse response = familyGroupsService.joinFamilyGroup(invitationCode);
        return new ResponseEntity<>(response, response.code());
    }

    @GetMapping("/family-group-detail")
    public ResponseEntity<BaseResponse> getFamilyGroupDetail(){
        BaseResponse response = familyGroupsService.getFamilyGroupDetail();
        return new ResponseEntity<>(response, response.code());
    }
}
