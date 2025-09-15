package com.kinapi.service;

import com.kinapi.common.entity.BaseResponse;
import com.kinapi.common.entity.TopicCategory;
import com.kinapi.common.repository.TopicCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TopicCategoryService {
    private final TopicCategoryRepository topicCategoryRepository;

    public BaseResponse getAllTopic() {
        log.info("[TopicCategoryService] fetching all topic category..");
        List<TopicCategory> topicCategoryList = topicCategoryRepository.findAll();
        return BaseResponse.builder()
                .code(HttpStatus.OK)
                .status(HttpStatus.OK.value())
                .message("Getting all topic category list")
                .data(topicCategoryList)
                .build();
    }

}
