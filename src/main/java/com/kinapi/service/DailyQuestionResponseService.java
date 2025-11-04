package com.kinapi.service;

import com.kinapi.common.dto.AnswerDailyQuestionDto;
import com.kinapi.common.dto.DailyQuestionResponseDto;
import com.kinapi.common.dto.UpdateDailyQuestionResponseDto;
import com.kinapi.common.entity.*;
import com.kinapi.common.repository.DailyQuestionResponseRepository;
import com.kinapi.common.repository.FamilyDailyQuestionRepository;
import com.kinapi.common.util.UserAuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyQuestionResponseService {
    private final DailyQuestionResponseRepository dailyQuestionResponseRepository;
    private final FamilyDailyQuestionRepository familyDailyQuestionRepository;

    public BaseResponse getDailyQuestionResponse(UUID questionId){
        try{
            Optional<FamilyDailyQuestion> familyDailyQuestionOptional = familyDailyQuestionRepository.findById(questionId);

            if(familyDailyQuestionOptional.isEmpty()){
                log.info("[getDailyQuestionResponse] Question id is invalid");
                throw new RuntimeException("Unable to find question with given id");
            }

            FamilyDailyQuestion familyDailyQuestion = familyDailyQuestionOptional.get();

            List<DailyQuestionResponseDto.Responses> questionResponses = new ArrayList<>();

            if(familyDailyQuestion.getIsCompleted()){
                log.info("[getDailyQuestionResponse] Question completed - showing all responses");
                if(familyDailyQuestion.getDailyQuestionResponses() != null){
                    List<DailyQuestionResponse> groupResponses = familyDailyQuestion.getDailyQuestionResponses();
                    for(DailyQuestionResponse item : groupResponses){
                        questionResponses.add(
                                DailyQuestionResponseDto.Responses.builder()
                                        .responseId(item.getId().toString())
                                        .moodValue(item.getMoodValue())
                                        .response(item.getResponse())
                                        .createdAt(item.getCreatedAt())
                                        .updatedAt(item.getUpdatedAt())
                                        .build()
                        );
                    }
                }
            } else {
                log.info("[getDailyQuestionResponse] Question not completed - showing only user's response");
                Users user = UserAuthHelper.getUser();
                FamilyMembers userFamilyMember = user.getFamilyMembers();
                Optional<DailyQuestionResponse> dailyQuestionResponseOptional = dailyQuestionResponseRepository.findByFamilyDailyQuestionAndFamilyMembers(familyDailyQuestion, userFamilyMember);

                if(dailyQuestionResponseOptional.isEmpty()){
                    log.info("[getDailyQuestionResponse] User has not answered this question yet");
                } else {
                    DailyQuestionResponse dailyQuestionResponse = dailyQuestionResponseOptional.get();
                    questionResponses.add(
                            DailyQuestionResponseDto.Responses.builder()
                                    .responseId(dailyQuestionResponse.getId().toString())
                                    .moodValue(dailyQuestionResponse.getMoodValue())
                                    .response(dailyQuestionResponse.getResponse())
                                    .createdAt(dailyQuestionResponse.getCreatedAt())
                                    .updatedAt(dailyQuestionResponse.getUpdatedAt())
                                    .build()
                    );
                }
            }

            return BaseResponse.builder()
                    .status(HttpStatus.OK.value())
                    .code(HttpStatus.OK)
                    .message("Successfully retrieve question responses")
                    .data(
                            DailyQuestionResponseDto.builder()
                                    .questionId(questionId.toString())
                                    .questionMessage("still use static after question entity already published")
                                    .isCompleted(familyDailyQuestion.getIsCompleted())
                                    .responses(questionResponses)
                                    .build()
                    )
                    .build();

        }catch (Exception e){
            log.error("[getDailyQuestionResponse] Error in retrieving question responses", e);
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Failed retrieving question responses: " + e.getMessage())
                    .build();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public BaseResponse answerDailyQuestion(AnswerDailyQuestionDto requestDto) {
        try{
            Users user = UserAuthHelper.getUser();
            if(user.getFamilyMembers() == null){
                throw new RuntimeException("User is not in a group");
            }

            FamilyMembers userFamilyMember = user.getFamilyMembers();
            Optional<FamilyDailyQuestion> familyDailyQuestionOptional = familyDailyQuestionRepository.findById(requestDto.getFamilyQuestionId());
            if(familyDailyQuestionOptional.isEmpty()){
                throw new RuntimeException("Unable to find question with given id");
            }

            FamilyDailyQuestion familyDailyQuestion = familyDailyQuestionOptional.get();

            DailyQuestionResponse dailyQuestionResponse = DailyQuestionResponse.builder()
                    .familyMembers(userFamilyMember)
                    .familyDailyQuestion(familyDailyQuestion)
                    .response(requestDto.getResponse())
                    .moodValue(requestDto.getMoodValue())
                    .build();

            dailyQuestionResponseRepository.save(dailyQuestionResponse);

            return BaseResponse.builder()
                    .status(HttpStatus.OK.value())
                    .code(HttpStatus.OK)
                    .message("Successfully save question response")
                    .build();

        }catch (Exception e){
            log.error("[answerDailyQuestion] Error when saving question responses", e);
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Failed saving question response: " + e.getMessage())
                    .build();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public BaseResponse editDailyQuestionResponse(UpdateDailyQuestionResponseDto requestDto) {
        try{
            Optional<DailyQuestionResponse> dailyQuestionResponseOptinal = dailyQuestionResponseRepository.findById(requestDto.getResponseId());
            if(dailyQuestionResponseOptinal.isEmpty()){
                throw new RuntimeException("Unable to find question with given id");
            }

            DailyQuestionResponse dailyQuestionResponse = dailyQuestionResponseOptinal.get();
            dailyQuestionResponse.setResponse(requestDto.getResponse());

            dailyQuestionResponseRepository.save(dailyQuestionResponse);

            return BaseResponse.builder()
                    .status(HttpStatus.OK.value())
                    .code(HttpStatus.OK)
                    .message("Successfully update question response")
                    .build();

        }catch (Exception e){
            log.error("[editDailyQuestionResponse] Error when editing question response", e);
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Failed updating question response: " + e.getMessage())
                    .build();
        }
    }
}
