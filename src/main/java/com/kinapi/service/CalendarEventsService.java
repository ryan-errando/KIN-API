package com.kinapi.service;

import com.kinapi.common.dto.CalendarEventsDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.common.entity.CalendarEvents;
import com.kinapi.common.entity.FamilyMembers;
import com.kinapi.common.entity.Users;
import com.kinapi.common.repository.CalendarEventsRepository;
import com.kinapi.common.util.UserAuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalendarEventsService {
    private final CalendarEventsRepository calendarEventsRepository;

    public BaseResponse getAllEvents(){
        try{
            Users user = UserAuthHelper.getUser();
            FamilyMembers member = user.getFamilyMembers();

            log.info("[getAllEvents] {} is trying to get all calendar events", user.getName());
            List<CalendarEvents> events = calendarEventsRepository.findAllByFamilyGroup(user.getFamilyMembers().getGroup());

            log.info("[getAllEvents] Fetching all events from \"{}\" family group", member.getGroup().getGroupName());
            List<CalendarEventsDto> eventList = new ArrayList<>();
            for(CalendarEvents event: events){
                eventList.add(CalendarEventsDto.builder()
                        .eventId(event.getId().toString())
                        .createdById(member.getId().toString())
                        .createdBy(user.getName())
                        .title(event.getTitle())
                        .description(event.getDescription())
                        .location(event.getLocation())
                        .startTime(event.getStartTime().toString())
                        .endTime(event.getEndTime().toString())
                        .allDay(event.getAllDay())
                        .eventType(event.getEventType())
                        .color(event.getColor())
                        .priorityLevel(event.getPriorityLevel())
                        .build());
            }
            return BaseResponse.builder()
                    .status(HttpStatus.OK.value())
                    .code(HttpStatus.OK)
                    .data(eventList)
                    .message("Success retrieving all calendar events")
                    .build();
        }catch (Exception e){
            log.info("[getAllEvents] Failed retrieving all calendar events");
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Failed retrieving all calendar events")
                    .build();
        }
    }
}
