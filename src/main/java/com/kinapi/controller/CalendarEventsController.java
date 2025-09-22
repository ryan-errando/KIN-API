package com.kinapi.controller;

import com.kinapi.common.entity.BaseResponse;
import com.kinapi.service.CalendarEventsService;
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
public class CalendarEventsController {
    private final CalendarEventsService calendarEventsService;

    @GetMapping("/get-all-events")
    public ResponseEntity<BaseResponse> getAllEvents() {
        BaseResponse response = calendarEventsService.getAllEvents();
        return new ResponseEntity<>(response, response.code());
    }
}
