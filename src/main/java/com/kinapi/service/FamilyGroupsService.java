package com.kinapi.service;

import com.kinapi.common.dto.CreateFamilyGroupsDto;
import com.kinapi.common.dto.FamilyGroupDetailDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.common.entity.FamilyGroups;
import com.kinapi.common.entity.FamilyMembers;
import com.kinapi.common.entity.Users;
import com.kinapi.common.repository.FamilyGroupsRepository;
import com.kinapi.common.repository.FamilyMembersRepository;
import com.kinapi.common.repository.UserRepository;
import com.kinapi.common.util.UserAuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FamilyGroupsService {
    private final FamilyGroupsRepository familyGroupsRepository;
    private final FamilyMembersRepository familyMembersRepository;
    private final UserRepository userRepository;

    public BaseResponse createNewFamilyGroup(CreateFamilyGroupsDto createFamilyGroupsDto) {
        try{
            Users user = UserAuthHelper.getUser();
            log.info("[createNewFamilyGroup] Creating new family group for user with email: {}", user.getEmail());

            if(user.getIsInGroup() == true){
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

            FamilyMembers familyMembers = FamilyMembers.builder()
                    .user(user)
                    .group(familyGroups)
                    .build();
            familyMembersRepository.save(familyMembers);

            user.setIsInGroup(true);
            userRepository.save(user);

            log.info("[createNewFamilyGroup] \"{}\" successfully created", familyGroups.getGroupName());

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

    public BaseResponse getFamilyGroupDetail() {
        Users user = UserAuthHelper.getUser();
        FamilyGroups familyGroups = user.getFamilyMembers().getGroup();
        List<FamilyMembers> familyMembersList = familyGroups.getFamilyMembers();

        List<FamilyGroupDetailDto.FamilyMember> memberList = new ArrayList<>();

        for(FamilyMembers item : familyMembersList){
            memberList.add(
                    FamilyGroupDetailDto.FamilyMember.builder()
                            .id(item.getId().toString())
                            .name(item.getUser().getName())
                            .email(item.getUser().getEmail())
                            .dob(item.getUser().getDob().toString())
                            .role(item.getRole())
                            .groupCreator(ObjectUtils.isEmpty(item.getUser().getFamilyGroups()) ? false : true)
                            .build()
            );
        }

        FamilyGroupDetailDto response = FamilyGroupDetailDto.builder()
                .familyGroupName(familyGroups.getGroupName())
                .invitationCode(familyGroups.getInvitationCode())
                .resetTime(familyGroups.getResetTime().toString())
                .familyMemberList(memberList)
                .build();

        return BaseResponse.builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK)
                .message("Successfully get family group detail")
                .data(response)
                .build();
    }

    public BaseResponse deleteFamilyGroup() {
        try{
            Users user = UserAuthHelper.getUser();
            FamilyGroups familyGroups = user.getFamilyGroups();

            if(familyGroups != null){
                List<FamilyMembers> allMembers = familyGroups.getFamilyMembers();
                for(FamilyMembers member : allMembers) {
                    Users memberUser = member.getUser();
                    memberUser.setIsInGroup(false);
                    memberUser.setFamilyMembers(null);
                    if(memberUser.getFamilyGroups() != null) {
                        memberUser.setFamilyGroups(null);
                    }
                    userRepository.save(memberUser);
                    log.info("[deleteFamilyGroup] Deleting {} from the group", memberUser.getName());
                }

                log.info("[deleteFamilyGroup] Successfully deleted family group");
                familyGroupsRepository.delete(familyGroups);
            }else{
                throw new RuntimeException("User is not the group creator");
            }

            return BaseResponse.builder()
                    .status(HttpStatus.OK.value())
                    .code(HttpStatus.OK)
                    .message("Successfully deleted family group")
                    .build();

        }catch (Exception e){
            log.error("[deleteFamilyGroup] Error deleting family group: {}", e.getMessage(), e);
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
