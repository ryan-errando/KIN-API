package com.kinapi.service;

import com.kinapi.common.dto.CreateFamilyGroupsDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.common.entity.FamilyGroups;
import com.kinapi.common.entity.Users;
import com.kinapi.common.repository.FamilyGroupsRepository;
import com.kinapi.common.util.UserAuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.security.SecureRandom;
import java.time.LocalTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class FamilyGroupsService {
    private final FamilyGroupsRepository familyGroupsRepository;

    public BaseResponse createNewFamilyGroup(CreateFamilyGroupsDto createFamilyGroupsDto) {
        try{
            Users user = UserAuthHelper.getUser();
            log.info("[createNewFamilyGroup] Creating new family group for user with email: {}", user.getEmail());

            if(user.getIsInGroup() != false){
                throw new RuntimeException("User already in a family group");
            }

            String invitationCode;
            log.info("[createNewFamilyGroup] Generating unique code...");
            do {
                invitationCode = generateInvitationCode();
            } while (familyGroupsRepository.existsByInvitationCode(invitationCode));

            FamilyGroups familyGroups = FamilyGroups.builder()
                    .groupName(createFamilyGroupsDto.getFamilyName())
                    .invitationCode(invitationCode)
                    .createdBy(user)
                    .resetTime(ObjectUtils.isEmpty(createFamilyGroupsDto.getResetTime()) ? LocalTime.of(18, 0) : createFamilyGroupsDto.getResetTime())
                    .build();
            familyGroupsRepository.save(familyGroups);
            log.info("[createNewFamilyGroup] {} successfully created", familyGroups.getGroupName());

            return BaseResponse.builder()
                    .status(HttpStatus.OK.value())
                    .code(HttpStatus.OK)
                    .message("Successfully created family group")
                    .data(invitationCode)
                    .build();

        }catch (Exception e){
            log.error("[createNewFamilyGroup] Error creating new family group: {}", e.getMessage(), e);
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    private static String generateInvitationCode(){
        String characters = "9ABC0DEF1GH2IJK3LMN4OPQ5RST6UVW7XYZ8";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {code.append(characters.charAt(random.nextInt(characters.length())));}
        return code.toString();
    }
}
