package com.kinapi.service;

import com.kinapi.common.dto.FamilyDailyQuestionDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.common.entity.FamilyDailyQuestion;
import com.kinapi.common.entity.FamilyGroups;
import com.kinapi.common.entity.Users;
import com.kinapi.common.repository.FamilyDailyQuestionRepository;
import com.kinapi.common.util.UserAuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FamilyDailyQuestionService {
    private final FamilyDailyQuestionRepository familyDailyQuestionRepository;

    public BaseResponse getFamilyDailyQuestions() {
        try{
            log.info("[getFamilyDailyQuestions] Fetching data for family group daily questions");
            Users user = UserAuthHelper.getUser();

            if(user.getFamilyMembers() == null){
                throw new RuntimeException("User is not in a family group");
            }

            FamilyGroups familyGroups = user.getFamilyMembers().getGroup();
            List<FamilyDailyQuestion> familyDailyQuestions = familyDailyQuestionRepository.findByFamilyGroups(familyGroups);
            List<FamilyDailyQuestionDto> response = new ArrayList<>();
            for(FamilyDailyQuestion item :  familyDailyQuestions){
                response.add(
                        FamilyDailyQuestionDto.builder()
                                .id(item.getId().toString())
                                .questionText(item.getDailyQuestions().getQuestion())
                                .totalMember(item.getTotalMembers())
                                .answeredCount(item.getAnsweredCount())
                                .isCompleted(item.getIsCompleted())
                                .assignedDate(item.getAssignedDate())
                                .build()
                );
            }

            log.info("[getFamilyDailyQuestions] Successfully fetching {} family daily questions data", familyDailyQuestions.size());
            return BaseResponse.builder()
                    .status(HttpStatus.OK.value())
                    .code(HttpStatus.OK)
                    .message("Successfully retrieving family group daily questions")
                    .data(response)
                    .build();

        }catch (Exception e){
            log.error("[getFamilyDailyQuestions] Failed fetching family group daily questions: {}", e.getMessage());
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Failed fetching family group daily questions due to: " + e.getMessage())
                    .build();
        }
    }
}
