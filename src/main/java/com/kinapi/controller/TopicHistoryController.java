package com.kinapi.controller;

import com.kinapi.common.dto.TopicHistoryDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.service.TopicHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("kin-api")
@RequiredArgsConstructor
public class TopicHistoryController {
    private final TopicHistoryService topicHistoryService;

    @GetMapping("/get-topic-history")
    public ResponseEntity<BaseResponse> getTopicHistory(
            @RequestParam(required = false, name = "category") List<String> category,
            @RequestParam(required = false, name = "favorite") Boolean favorite
    ){
        BaseResponse response = topicHistoryService.getAllTopicHistory(category, favorite);
        return new ResponseEntity<>(response, response.code());
    }

    @PostMapping("/add-topic-history")
    public ResponseEntity<BaseResponse> addTopicHistory(
            @Valid @RequestBody List<TopicHistoryDto> topicHistoryDto
    ){
        BaseResponse response = topicHistoryService.addTopicHistory(topicHistoryDto);
        return new ResponseEntity<>(response, response.code());
    }
}
