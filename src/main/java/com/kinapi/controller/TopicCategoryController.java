package com.kinapi.controller;

import com.kinapi.common.entity.BaseResponse;
import com.kinapi.service.TopicCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("kin-api")
@RequiredArgsConstructor
public class TopicCategoryController {
    private final TopicCategoryService topicCategoryService;

    @GetMapping("/get-topic-list")
    public ResponseEntity<BaseResponse> getTopicList(){
        BaseResponse response = topicCategoryService.getAllTopic();
        return new ResponseEntity<>(response, response.code());
    }
}
