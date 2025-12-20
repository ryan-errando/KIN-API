package com.kinapi.service;

import com.kinapi.common.entity.BaseResponse;
import com.kinapi.common.entity.FamilyMembers;
import com.kinapi.common.entity.Roles;
import com.kinapi.common.entity.Users;
import com.kinapi.common.repository.FamilyMembersRepository;
import com.kinapi.common.repository.RolesRepository;
import com.kinapi.common.util.UserAuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RolesService {

    private final RolesRepository rolesRepository;
    private final FamilyMembersRepository familyMembersRepository;

    public BaseResponse getAllRoles(){
        log.info("[RolesService] fetching all roles...");
        List<Roles> roles = rolesRepository.findAll();
        return BaseResponse.builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK)
                .data(roles)
                .message("Successfully get all roles")
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public BaseResponse addRole(String role) {
        try{
            Users user = UserAuthHelper.getUser();
            FamilyMembers familyMembers = user.getFamilyMembers();

            if(familyMembers == null){
                throw new Exception("User is not on a family");
            }

            familyMembers.setRole(role);
            familyMembersRepository.save(familyMembers);

            return BaseResponse.builder()
                    .status(HttpStatus.OK.value())
                    .code(HttpStatus.OK)
                    .message("Successfully added role")
                    .build();

        } catch (Exception e) {
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }
}
