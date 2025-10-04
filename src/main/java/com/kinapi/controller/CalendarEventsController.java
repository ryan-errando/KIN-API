package com.kinapi.controller;

import com.kinapi.common.dto.CalendarEventsDto;
import com.kinapi.common.dto.GetCalendarEventsDto;
import com.kinapi.common.dto.SetCalendarEventCompletedDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.service.CalendarEventsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("kin-api")
@RequiredArgsConstructor
public class CalendarEventsController {
    private final CalendarEventsService calendarEventsService;

    @PostMapping("/get-events")
    public ResponseEntity<BaseResponse> getAllEvents(
            @Valid @RequestBody GetCalendarEventsDto getCalendarEventsDto
    ) {
        BaseResponse response = calendarEventsService.getEvents(getCalendarEventsDto);
        return new ResponseEntity<>(response, response.code());
    }

    @PostMapping("/add-event")
    public ResponseEntity<BaseResponse> createNewCalendarEvent(
            @Valid @RequestBody CalendarEventsDto calendarEventsDto
    ) {
        BaseResponse response = calendarEventsService.addEvent(calendarEventsDto);
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
