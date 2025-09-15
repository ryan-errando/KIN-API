package com.kinapi.service;

import com.kinapi.common.dto.TopicHistoryDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.common.entity.TopicHistory;
import com.kinapi.common.entity.Users;
import com.kinapi.common.repository.TopicHistoryRepository;
import com.kinapi.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public BaseResponse getAllTopicHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users user = userRepository.findByEmail(email).orElse(null);

        List<TopicHistory> topicHistoryList = topicHistoryRepository.findByUserId(user.getId());
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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            Users user = userRepository.findByEmail(email).orElse(null);

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
            }
            return BaseResponse.builder()
                    .code(HttpStatus.OK)
                    .status(HttpStatus.OK.value())
                    .message("Added topic history successfully")
                    .build();
        }catch (Exception e){
            return BaseResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
        }
    }
}
