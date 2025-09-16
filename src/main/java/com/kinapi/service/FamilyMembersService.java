package com.kinapi.service;

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

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FamilyMembersService {
    private final FamilyGroupsRepository familyGroupsRepository;
    private final FamilyMembersRepository familyMembersRepository;
    private final UserRepository userRepository;

    public BaseResponse joinFamilyGroup(String invitationcode){
        try{
            Users user = UserAuthHelper.getUser();
            if(user.getIsInGroup() == true){
                throw new RuntimeException("User already in a family group");
            }

            FamilyGroups familyGroups = familyGroupsRepository.findByInvitationCode(invitationcode).orElse(null);
            if(familyGroups == null){
                throw new RuntimeException("No family group found for invitation code: " + invitationcode);
            }

            FamilyMembers familyMembers = FamilyMembers.builder()
                    .user(user)
                    .group(familyGroups)
                    .build();
            familyMembersRepository.save(familyMembers);

            user.setIsInGroup(true);
            userRepository.save(user);

            log.info("[joinFamilyGroup] successfully joined \"{}\" family group", familyGroups.getGroupName());

            return BaseResponse.builder()
                    .status(HttpStatus.OK.value())
                    .code(HttpStatus.OK)
                    .message("Successfully joined family group")
                    .build();

        }catch (Exception e){
            log.error("[joinFamilyGroup] Error joining family group: {}", e.getMessage(), e);
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    public BaseResponse leaveFamilyGroup(){
        try{
            Users user = UserAuthHelper.getUser();
            if(user.getFamilyGroups() == null){
                log.info("[leaveFamilyGroup] user is not the group creator");
                FamilyMembers familyMember = user.getFamilyMembers();

                user.setIsInGroup(false);
                user.setFamilyMembers(null);
                userRepository.save(user);

                familyMembersRepository.delete(familyMember);
                log.info("[leaveFamilyGroup] user with email {} successfully leave the family group", user.getEmail());
                return BaseResponse.builder()
                        .status(HttpStatus.OK.value())
                        .code(HttpStatus.OK)
                        .message("Successfully leaved the family group")
                        .build();
            } else {
                throw new RuntimeException("User is the group creator");
            }
        }catch (Exception e){
            log.info("[leaveFamilyGroup] Cannot leave the family group: {}", e.getMessage(), e);
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    public BaseResponse removeFamilyMember(UUID familyMemberId){
        try{
            Users user = UserAuthHelper.getUser();
            if(user.getFamilyGroups() != null){
                log.info("[removeFamilyMember] User is the group creator, permitted to remove the member");
                FamilyMembers familyMember = familyMembersRepository.findById(familyMemberId).orElse(null);

                log.info("[removeFamilyMember] Removing {} from the family group", familyMember.getUser().getName());

                Users memberUser = familyMember.getUser();
                memberUser.setIsInGroup(false);
                memberUser.setFamilyMembers(null);
                userRepository.save(memberUser);

                familyMembersRepository.delete(familyMember);

                return BaseResponse.builder()
                        .status(HttpStatus.OK.value())
                        .code(HttpStatus.OK)
                        .message("Successfully removed the family member")
                        .build();
            }else{
                throw new RuntimeException("User is not the group creator");
            }
        }catch (Exception e){
            log.info("[removeFamilyMember] Cannot remove the family member: {}", e.getMessage(), e);
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }
}
