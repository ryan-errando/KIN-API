package com.kinapi.controller;

import com.kinapi.common.dto.AnswerDailyQuestionDto;
import com.kinapi.common.dto.UpdateDailyQuestionResponseDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.service.DailyQuestionResponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("kin-api")
@RequiredArgsConstructor
public class DailyQuestionResponseController {

    private final DailyQuestionResponseService dailyQuestionResponseService;

    @GetMapping("/daily-question-response/{id}")
    public ResponseEntity<BaseResponse> getDailyQuestionResponse(
            @PathVariable UUID id
    ) {
        BaseResponse response = dailyQuestionResponseService.getDailyQuestionResponse(id);
        return new ResponseEntity<>(response, response.code());
    }

    @PostMapping("/answer-daily-question-response")
    public ResponseEntity<BaseResponse> answerDailyQuestion(
            @Valid @RequestBody AnswerDailyQuestionDto answerDailyQuestionDto
    ) {
        BaseResponse response = dailyQuestionResponseService.answerDailyQuestion(answerDailyQuestionDto);
        return new ResponseEntity<>(response, response.code());
    }

    @PutMapping("/edit-daily-question-response")
    public ResponseEntity<BaseResponse> editDailyQuestionResponse(
            @Valid @RequestBody UpdateDailyQuestionResponseDto updateDailyQuestionResponseDto
    ) {
        BaseResponse response = dailyQuestionResponseService.editDailyQuestionResponse(updateDailyQuestionResponseDto);
        return new ResponseEntity<>(response, response.code());
    }
}
