package com.kinapi.controller;

import com.kinapi.common.entity.BaseResponse;
import com.kinapi.service.TopicHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("kin-api")
@RequiredArgsConstructor
public class TopicHistoryController {
    private final TopicHistoryService topicHistoryService;

    @GetMapping("/get-topic-history/{id}")
    public ResponseEntity<BaseResponse> getTopicHistory(
            @PathVariable UUID id
    ){
        BaseResponse response = topicHistoryService.getAllTopicHistory(id);
        return new ResponseEntity<>(response, response.code());
    }

    @PostMapping("/add-topic-history")
    public ResponseEntity<BaseResponse> addTopicHistory(
            @RequestBody String topic,
            @RequestParam UUID categoryId
    ){
        BaseResponse response = topicHistoryService.addTopicHistory(topic, categoryId);
        return new ResponseEntity<>(response, response.code());
    }
}
