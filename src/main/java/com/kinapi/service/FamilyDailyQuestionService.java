package com.kinapi.service;

import com.kinapi.common.dto.FamilyDailyQuestionDto;
import com.kinapi.common.dto.GeneratedQuestionDto;
import com.kinapi.common.entity.*;
import com.kinapi.common.repository.DailyQuestionRepository;
import com.kinapi.common.repository.FamilyDailyQuestionRepository;
import com.kinapi.common.repository.FamilyMembersRepository;
import com.kinapi.common.util.UserAuthHelper;
import com.kinapi.service.openai.OpenAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FamilyDailyQuestionService {
    private final FamilyDailyQuestionRepository familyDailyQuestionRepository;
    private final DailyQuestionRepository dailyQuestionRepository;
    private final FamilyMembersRepository familyMembersRepository;
    private final OpenAIService openAIService;

    public BaseResponse getFamilyDailyQuestions() {
        try{
            log.info("[getFamilyDailyQuestions] Fetching data for family group daily questions");
            Users user = UserAuthHelper.getUser();

            if(user.getFamilyMembers() == null){
                throw new RuntimeException("User is not in a family group");
            }

            FamilyGroups familyGroups = user.getFamilyMembers().getGroup();

            syncLatestQuestionMemberCount(familyGroups);

            List<FamilyDailyQuestion> familyDailyQuestions = familyDailyQuestionRepository.findByFamilyGroups(familyGroups);
            List<FamilyDailyQuestionDto> response = new ArrayList<>();
            for(FamilyDailyQuestion item :  familyDailyQuestions){
                response.add(
                        FamilyDailyQuestionDto.builder()
                                .id(item.getId().toString())
                                .questionText(item.getDailyQuestion().getQuestion())
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

    /**
     * Gets today's daily question for the user's family group.
     * If no question exists for today and conditions are met (reset time passed + previous question completed),
     * generates a new question.
     */
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse getTodayDailyQuestion() {
        try {
            Users user = UserAuthHelper.getUser();
            if(user.getFamilyMembers() == null){
                throw new RuntimeException("User is not in a family group");
            }

            FamilyGroups familyGroup = user.getFamilyMembers().getGroup();
            LocalTime resetTime = familyGroup.getResetTime();
            LocalDateTime now = LocalDateTime.now();

            log.info("[getTodayDailyQuestion] Getting daily question for group: {}, resetTime: {}", familyGroup.getGroupName(), resetTime);

            syncLatestQuestionMemberCount(familyGroup);

            LocalDate questionDate = calculateQuestionDate(now, resetTime);
            log.info("[getTodayDailyQuestion] Calculated question date: {}", questionDate);

            Optional<FamilyDailyQuestion> existingQuestion = findQuestionForDate(familyGroup, questionDate);

            if(existingQuestion.isPresent()) {
                log.info("[getTodayDailyQuestion] Found existing question for date: {}", questionDate);
                return buildQuestionResponse(existingQuestion.get());
            }

            // No question exists for today, check if we can generate a new one
            if(canGenerateNewQuestion(familyGroup, questionDate)) {
                log.info("[getTodayDailyQuestion] Generating new question for date: {}", questionDate);
                FamilyDailyQuestion newQuestion = generateNewQuestion(familyGroup);
                return buildQuestionResponse(newQuestion);
            } else {
                log.warn("[getTodayDailyQuestion] Cannot generate new question yet. Previous question not completed or reset time not passed.");
                return BaseResponse.builder()
                        .status(HttpStatus.OK.value())
                        .code(HttpStatus.OK)
                        .message("No daily question need to be generated for now")
                        .build();
            }

        } catch (Exception e) {
            log.error("[getTodayDailyQuestion] Error getting today's daily question", e);
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Failed getting today's daily question: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Calculates the question date based on current time and reset time.
     * If current time is before reset time, question date is yesterday.
     * Otherwise, question date is today.
     */
    private LocalDate calculateQuestionDate(LocalDateTime now, LocalTime resetTime) {
        if(resetTime == null) {
            resetTime = LocalTime.of(0, 0);
        }

        if(now.toLocalTime().isBefore(resetTime)) {
            return now.toLocalDate().minusDays(1);
        }
        return now.toLocalDate();
    }

    /**
     * Finds a question for the specified date by checking if the assigned date falls on that date
     */
    private Optional<FamilyDailyQuestion> findQuestionForDate(FamilyGroups familyGroup, LocalDate questionDate) {
        Optional<FamilyDailyQuestion> latestQuestion = familyDailyQuestionRepository.findTopByFamilyGroupsOrderByAssignedDateDesc(familyGroup);

        if(latestQuestion.isEmpty()) {
            return Optional.empty();
        }

        FamilyDailyQuestion question = latestQuestion.get();
        LocalDate assignedDate = question.getAssignedDate().toLocalDate();

        if(assignedDate.equals(questionDate)) {
            return Optional.of(question);
        }

        return Optional.empty();
    }

    /**
     * Checks if a new question can be generated.
     * Conditions:
     * 1. No previous question exists (first time) OR
     * 2. Previous question is completed (all members answered) AND question date is after previous question date
     */
    private boolean canGenerateNewQuestion(FamilyGroups familyGroup, LocalDate newQuestionDate) {
        Optional<FamilyDailyQuestion> previousQuestion = familyDailyQuestionRepository.findTopByFamilyGroupsOrderByAssignedDateDesc(familyGroup);

        if(previousQuestion.isEmpty()) {
            log.info("[canGenerateNewQuestion] No previous question found. Can generate first question.");
            return true;
        }

        FamilyDailyQuestion prevQ = previousQuestion.get();
        LocalDate prevQuestionDate = prevQ.getAssignedDate().toLocalDate();

        if(!newQuestionDate.isAfter(prevQuestionDate)) {
            log.info("[canGenerateNewQuestion] New question date {} is not after previous question date {}", newQuestionDate, prevQuestionDate);
            return false;
        }

        if(!prevQ.getIsCompleted()) {
            log.info("[canGenerateNewQuestion] Previous question is not completed yet. answeredCount: {}, totalMembers: {}", prevQ.getAnsweredCount(), prevQ.getTotalMembers());
            return false;
        }

        log.info("[canGenerateNewQuestion] All conditions met. Can generate new question.");
        return true;
    }

    /**
     * Generates a new daily question using OpenAI and saves it to the database
     */
    private FamilyDailyQuestion generateNewQuestion(FamilyGroups familyGroup) {
        List<FamilyDailyQuestion> recentQuestions = familyDailyQuestionRepository.findTop15ByFamilyGroupsOrderByAssignedDateDesc(familyGroup);

        List<String> previousQuestionTexts = recentQuestions.stream()
                .map(fdq -> fdq.getDailyQuestion().getQuestion())
                .toList();

        log.info("[generateNewQuestion] Fetched {} recent questions to avoid duplicates", previousQuestionTexts.size());

        GeneratedQuestionDto generatedQuestion = openAIService.generateFamilyDailyQuestion(previousQuestionTexts).block();

        if(generatedQuestion == null) {
            throw new RuntimeException("Failed to generate question from OpenAI");
        }

        log.info("[generateNewQuestion] Generated question: {}", generatedQuestion.getQuestion());
        DailyQuestion dailyQuestion = DailyQuestion.builder()
                .question(generatedQuestion.getQuestion())
                .build();
        dailyQuestion = dailyQuestionRepository.save(dailyQuestion);

        long memberCount = familyMembersRepository.countByGroup(familyGroup);

        FamilyDailyQuestion familyDailyQuestion = FamilyDailyQuestion.builder()
                .familyGroups(familyGroup)
                .dailyQuestion(dailyQuestion)
                .totalMembers((int) memberCount)
                .answeredCount(0)
                .isCompleted(false)
                .build();

        familyDailyQuestion = familyDailyQuestionRepository.save(familyDailyQuestion);
        log.info("[generateNewQuestion] Saved new family daily question with ID: {}", familyDailyQuestion.getId());

        return familyDailyQuestion;
    }

    /**
     * Builds the response for a daily question
     */
    private BaseResponse buildQuestionResponse(FamilyDailyQuestion question) {
        FamilyDailyQuestionDto responseDto = FamilyDailyQuestionDto.builder()
                .id(question.getId().toString())
                .questionText(question.getDailyQuestion().getQuestion())
                .totalMember(question.getTotalMembers())
                .answeredCount(question.getAnsweredCount())
                .isCompleted(question.getIsCompleted())
                .assignedDate(question.getAssignedDate())
                .build();

        return BaseResponse.builder()
                .status(HttpStatus.OK.value())
                .code(HttpStatus.OK)
                .message("Successfully retrieved today's daily question")
                .data(responseDto)
                .build();
    }

    private void syncLatestQuestionMemberCount(FamilyGroups familyGroup) {
        try {
            Optional<FamilyDailyQuestion> latestQuestion = familyDailyQuestionRepository.findTopByFamilyGroupsOrderByAssignedDateDesc(familyGroup);

            if (latestQuestion.isEmpty()) {
                log.debug("[syncLatestQuestionMemberCount] No latest question found for group: {}", familyGroup.getGroupName());
                return;
            }

            FamilyDailyQuestion question = latestQuestion.get();
            long actualMemberCount = familyMembersRepository.countByGroup(familyGroup);

            if (question.getTotalMembers() == actualMemberCount) {
                log.debug("[syncLatestQuestionMemberCount] Member count already in sync for question {}", question.getId());
                return;
            }

            int oldTotalMembers = question.getTotalMembers();
            question.setTotalMembers((int) actualMemberCount);

            boolean wasCompleted = question.getIsCompleted();
            boolean isNowCompleted = question.getAnsweredCount() >= actualMemberCount;
            question.setIsCompleted(isNowCompleted);

            familyDailyQuestionRepository.save(question);

            log.info("[syncLatestQuestionMemberCount] Synced question {} member count: {} -> {}, completion: {} -> {}", question.getId(), oldTotalMembers, actualMemberCount, wasCompleted, isNowCompleted);

        } catch (Exception e) {
            log.error("[syncLatestQuestionMemberCount] Error syncing member count: {}", e.getMessage(), e);
        }
    }
}
