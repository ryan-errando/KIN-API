package com.kinapi.service;

import com.kinapi.common.dto.TopicHistoryDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.common.entity.TopicHistory;
import com.kinapi.common.entity.Users;
import com.kinapi.common.repository.TopicHistoryRepository;
import com.kinapi.common.repository.UserRepository;
import com.kinapi.common.specification.TopicHistorySpecification;
import com.kinapi.common.util.UserAuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TopicHistoryService {
    private final TopicHistoryRepository topicHistoryRepository;
    private final UserRepository userRepository;

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
}
