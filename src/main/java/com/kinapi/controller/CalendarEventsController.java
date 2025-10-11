package com.kinapi.controller;

import com.kinapi.common.dto.AddCalendarEventsDto;
import com.kinapi.common.dto.CalendarEventsDto;
import com.kinapi.common.dto.SetCalendarEventCompletedDto;
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

    @PostMapping("/set-event-completed")
    public ResponseEntity<BaseResponse> setCalendarEventCompleted(
            @Valid @RequestBody SetCalendarEventCompletedDto setCalendarEventCompletedDto
            ) {
        BaseResponse response = calendarEventsService.setCalendarEventCompleted(setCalendarEventCompletedDto);
        return new ResponseEntity<>(response, response.code());
    }

    // TODO: IMPLEMENT PKE TRY CATCH -> BIAR TW KLO AD HIT ERROR DI API GABAKAL RUSAK DI FE
    // create event -> buat dto baru CreateCalendarEventsDto copy structure aja biar formatnya sama

    // update event -> pke PUT/POST mapping approach by UUID(event_id) trs create body tembak ke endpoint.
    // find entity by id
    // pake set buat ganti value
    // save

    // delete event -> lgsg pke repo delete by ID
}
