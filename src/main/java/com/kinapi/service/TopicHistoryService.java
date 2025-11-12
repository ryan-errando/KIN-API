package com.kinapi.service;

import com.kinapi.common.dto.GeneratedQuestionDto;
import com.kinapi.common.dto.GeneratedQuestionsDto;
import com.kinapi.common.dto.TopicHistoryDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.common.entity.TopicCategory;
import com.kinapi.common.entity.TopicHistory;
import com.kinapi.common.entity.Users;
import com.kinapi.common.repository.TopicCategoryRepository;
import com.kinapi.common.repository.TopicHistoryRepository;
import com.kinapi.common.repository.UserRepository;
import com.kinapi.common.specification.TopicHistorySpecification;
import com.kinapi.common.util.UserAuthHelper;
import com.kinapi.service.openai.OpenAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class TopicHistoryService {
    private final TopicHistoryRepository topicHistoryRepository;
    private final UserRepository userRepository;
    private final OpenAIService openAIService;
    private final TopicCategoryRepository topicCategoryRepository;

    public BaseResponse getAllTopicHistory(List<String> categories, Boolean favorite) {
        Users user = UserAuthHelper.getUser();
        log.info("[TopicHistoryService] Get topic history for user: {} with categories: {} and favorite: {}", user.getEmail(), categories, favorite);

        Specification<TopicHistory> spec = TopicHistorySpecification.belongsToUser(user.getId())
                .and(TopicHistorySpecification.hasCategories(categories))
                .and(TopicHistorySpecification.isFavorite(favorite));

        List<TopicHistory> topicHistoryList = topicHistoryRepository.findAll(spec);
        List<TopicHistoryDto> topicHistoryResponseDtoList = topicHistoryList.stream()
                .map(th -> TopicHistoryDto.builder()
                        .topicText(th.getTopicText())
                        .topicCategory(th.getCategory())
                        .topicIsFavorite(th.getIsFavorite())
                        .build())
                .toList();
        return BaseResponse.builder()
                .code(HttpStatus.OK)
                .status(HttpStatus.OK.value())
                .message("Getting user topic history")
                .data(topicHistoryResponseDtoList)
                .build();
    }

    public BaseResponse addTopicHistory(List<TopicHistoryDto> topicHistoryDtos) {
        try {
            Users user = UserAuthHelper.getUser();
            log.info("\n[TopicHistoryService] Add topic history for user: {}", user.getEmail());

            if(user == null) {throw new RuntimeException("User can't be found");}

            for (TopicHistoryDto item : topicHistoryDtos) {
                if(item.getTopicCategory() == null || item.getTopicText() == null || item.getTopicIsFavorite() == null) {
                    throw new RuntimeException("Request is not valid");
                }
                TopicHistory topicHistory = TopicHistory.builder()
                        .category(item.getTopicCategory())
                        .user(user)
                        .topicText(item.getTopicText())
                        .isFavorite(item.getTopicIsFavorite())
                        .build();
                topicHistoryRepository.save(topicHistory);
                log.info("\n[TopicHistoryService] Add topic history...\nTopic: {}\nCategory: {}", item.getTopicText(), item.getTopicCategory());
            }
            return BaseResponse.builder()
                    .code(HttpStatus.OK)
                    .status(HttpStatus.OK.value())
                    .message("Added topic history successfully")
                    .build();
        }catch (Exception e){
            log.error("[TopicHistoryService] Add topic history failed: {}", e.getMessage(), e);
            return BaseResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
        }
    }

    public BaseResponse generateMultipleQuestions(String category) {
        try {
            Users user = UserAuthHelper.getUser();
            log.info("[TopicHistoryService] Generate multiple questions for category: {} by user: {}", category, user.getEmail());

            List<TopicHistory> recentHistory = topicHistoryRepository.findAll(TopicHistorySpecification.belongsToUser(user.getId()));
            List<String> previousQuestions = recentHistory.stream()
                    .map(TopicHistory::getTopicText)
                    .toList();

            GeneratedQuestionsDto result = openAIService.generateMultipleFamilyQuestions(category, previousQuestions).block();

            log.info("[TopicHistoryService] Successfully generated {} questions for category: {}", result.getQuestions().size(), category);

            return BaseResponse.builder()
                    .code(HttpStatus.OK)
                    .status(HttpStatus.OK.value())
                    .message("Successfully generated 4 family questions")
                    .data(result)
                    .build();
        } catch (Exception e) {
            log.error("[TopicHistoryService] Failed to generate multiple questions: {}", e.getMessage(), e);
            return BaseResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to generate questions: " + e.getMessage())
                    .build();
        }
    }

    public BaseResponse generateRandomQuestion() {
        try {
            Users user = UserAuthHelper.getUser();
            log.info("[TopicHistoryService] Generate random question for user: {}", user.getEmail());

            List<TopicCategory> categories = topicCategoryRepository.findAll();
            if (categories.isEmpty()) {
                throw new RuntimeException("No categories found in database");
            }

            Random random = new Random();
            TopicCategory randomCategory = categories.get(random.nextInt(categories.size()));
            String selectedCategory = randomCategory.getCategory();

            log.info("[TopicHistoryService] Randomly selected category: {}", selectedCategory);

            List<TopicHistory> recentHistory = topicHistoryRepository.findAll(TopicHistorySpecification.belongsToUser(user.getId()));
            List<String> previousQuestions = recentHistory.stream()
                    .map(TopicHistory::getTopicText)
                    .toList();

            GeneratedQuestionDto result = openAIService.generateSingleFamilyQuestion(selectedCategory, previousQuestions).block();

            log.info("[TopicHistoryService] Successfully generated question for category: {}", selectedCategory);

            return BaseResponse.builder()
                    .code(HttpStatus.OK)
                    .status(HttpStatus.OK.value())
                    .message("Successfully generated random family question")
                    .data(result)
                    .build();
        } catch (Exception e) {
            log.error("[TopicHistoryService] Failed to generate random question: {}", e.getMessage(), e);
            return BaseResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to generate question: " + e.getMessage())
                    .build();
        }
    }
}
