package com.kinapi.controller;

import com.kinapi.common.entity.BaseResponse;
import com.kinapi.service.FamilyDailyQuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("kin-api")
@RequiredArgsConstructor
public class FamilyDailyQuestionController {
    private final FamilyDailyQuestionService familyDailyQuestionService;

    @GetMapping("/get-family-daily-questions")
    public ResponseEntity<BaseResponse> getFamilyDailyQuestions() {
        BaseResponse response = familyDailyQuestionService.getFamilyDailyQuestions();
        return new ResponseEntity<>(response, response.code());
    }

}
