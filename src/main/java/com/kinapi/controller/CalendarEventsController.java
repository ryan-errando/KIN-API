package com.kinapi.controller;

import com.kinapi.common.dto.AddCalendarEventsDto;
import com.kinapi.common.dto.CalendarEventsDto;
import com.kinapi.common.dto.SetCalendarEventCompletedDto;
import com.kinapi.common.dto.UpdateCalendarEventsDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.service.CalendarEventsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("kin-api")
@RequiredArgsConstructor
public class CalendarEventsController {
    private final CalendarEventsService calendarEventsService;

    @GetMapping("/get-events")
    public ResponseEntity<BaseResponse> getEvents(
            @RequestParam(required = false, name = "start_time") @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false, name = "end_time") @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime endTime
    ) {
        BaseResponse response = calendarEventsService.getEvents(startTime, endTime);
        return new ResponseEntity<>(response, response.code());
    }

    @PostMapping("/add-event")
    public ResponseEntity<BaseResponse> createNewCalendarEvent(
            @Valid @RequestBody AddCalendarEventsDto addCalendarEventsDto
    ) {
        BaseResponse response = calendarEventsService.addEvent(addCalendarEventsDto);
        return new ResponseEntity<>(response, response.code());
    }

    @PutMapping("/update-event")
    public ResponseEntity<BaseResponse> updateCalendarEvent(
            @Valid @RequestBody UpdateCalendarEventsDto updateCalendarEventsDto
    ){
        BaseResponse response = calendarEventsService.updateCalendarEventsDto(updateCalendarEventsDto);
        return new ResponseEntity<>(response, response.code());
    }

    @PostMapping("/set-event-completed")
    public ResponseEntity<BaseResponse> setCalendarEventCompleted(
            @Valid @RequestBody SetCalendarEventCompletedDto setCalendarEventCompletedDto
            ) {
        BaseResponse response = calendarEventsService.setCalendarEventCompleted(setCalendarEventCompletedDto);
        return new ResponseEntity<>(response, response.code());
    }
}
