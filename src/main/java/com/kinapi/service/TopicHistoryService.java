package com.kinapi.service;

import com.kinapi.common.dto.AddTopicHistoryDto;
import com.kinapi.common.dto.TopicHistoryResponseDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.common.entity.TopicHistory;
import com.kinapi.common.entity.Users;
import com.kinapi.common.repository.TopicCategoryRepository;
import com.kinapi.common.repository.TopicHistoryRepository;
import com.kinapi.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TopicHistoryService {
    private final TopicHistoryRepository topicHistoryRepository;
    private final UserRepository userRepository;
    private final TopicCategoryRepository topicCategoryRepository;

    public BaseResponse getAllTopicHistory(UUID id) {
        List<TopicHistory> topicHistoryList = topicHistoryRepository.findByUserId(id);
        List<TopicHistoryResponseDto> topicHistoryResponseDtoList = topicHistoryList.stream()
                .map(th -> TopicHistoryResponseDto.builder()
                        .topicText(th.getTopicText())
                        .topicCategory(th.getCategory())
                        .build())
                .toList();
        return BaseResponse.builder()
                .code(HttpStatus.OK)
                .status(HttpStatus.OK.value())
                .message("Getting user topic history")
                .data(topicHistoryResponseDtoList)
                .build();
    }

    public BaseResponse addTopicHistory(AddTopicHistoryDto addTopicHistoryDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            Users user = userRepository.findByEmail(email).orElse(null);
            if(addTopicHistoryDto == null || user == null) {throw new RuntimeException("Category or User is not valid or can't be found");}
            TopicHistory topicHistory = TopicHistory.builder()
                    .category(addTopicHistoryDto.getTopicCategory())
                    .user(user)
                    .topicText(addTopicHistoryDto.getTopicText())
                    .build();
            topicHistoryRepository.save(topicHistory);
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
