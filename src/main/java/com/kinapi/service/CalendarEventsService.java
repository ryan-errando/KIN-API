package com.kinapi.service;

import com.kinapi.common.dto.CalendarEventsDto;
import com.kinapi.common.dto.GetCalendarEventsDto;
import com.kinapi.common.dto.SetCalendarEventCompletedDto;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalendarEventsService {
    private final CalendarEventsRepository calendarEventsRepository;

    public BaseResponse getEvents(GetCalendarEventsDto getCalendarEventsDto) {
        try{
            Users user = UserAuthHelper.getUser();
            FamilyMembers member = null;

            if (user != null) {
                member = user.getFamilyMembers();
            }else{
                log.error("[getAllEvent] User not found");
                return BaseResponse.builder()
                        .code(HttpStatus.CONFLICT)
                        .status(HttpStatus.CONFLICT.value())
                        .message("User not found, Auth Error")
                        .data(null)
                        .build();
            }

            if(member != null){
                log.info("[getAllEvents] {} is trying to get all calendar events", user.getName());
                List<CalendarEvents> events = calendarEventsRepository.findAllByFamilyGroup(user.getFamilyMembers().getGroup(), getCalendarEventsDto.getStartTime(), getCalendarEventsDto.getEndTime());

                log.info("[getAllEvents] Fetching all events from \"{}\" family group", member.getGroup().getGroupName());
                List<CalendarEventsDto> eventList = new ArrayList<>();
                for (CalendarEvents event : events) {
                    eventList.add(CalendarEventsDto.builder()
                            .eventId(event.getId())
                            .createdById(event.getCreatedBy().getId())
                            .title(event.getTitle())
                            .description(event.getDescription())
                            .location(event.getLocation())
                            .startTime(event.getStartTime())
                            .endTime(event.getEndTime())
                            .isCompleted(event.getIsCompleted())
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
            }else{
                log.error("[getAllEvent] Failed to add new event");
                return BaseResponse.builder()
                        .code(HttpStatus.CONFLICT)
                        .status(HttpStatus.CONFLICT.value())
                        .message("User is not in a family group")
                        .data(null)
                        .build();
            }

        }catch (Exception e){
            log.info("[getAllEvents] Failed retrieving all calendar events");
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Failed retrieving all calendar events")
                    .build();
        }
    }

    public BaseResponse addEvent(CalendarEventsDto calendarEventsDto) {
        try {
            Users user = UserAuthHelper.getUser();
            if (user == null) {
                log.error("[addEvent] User not found or not authenticated");
                return BaseResponse.builder()
                        .code(HttpStatus.UNAUTHORIZED)
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message("User not found, authentication error")
                        .data(null)
                        .build();
            }

            FamilyMembers member = user.getFamilyMembers();
            if (member == null) {
                log.error("[addEvent] User {} is not in a family group", user.getName());
                return BaseResponse.builder()
                        .code(HttpStatus.CONFLICT)
                        .status(HttpStatus.CONFLICT.value())
                        .message("User is not in a family group")
                        .data(null)
                        .build();
            }

            LocalDateTime startTime = calendarEventsDto.getStartTime();
            LocalDateTime endTime   = calendarEventsDto.getEndTime();

            CalendarEvents calendarEvent = CalendarEvents.builder()
                    .createdBy(member)
                    .title(calendarEventsDto.getTitle())
                    .description(calendarEventsDto.getDescription())
                    .location(calendarEventsDto.getLocation())
                    .startTime(startTime)
                    .endTime(endTime)
                    .isCompleted(calendarEventsDto.getIsCompleted() != null ? calendarEventsDto.getIsCompleted() : false)
                    .eventType(calendarEventsDto.getEventType())
                    .color(calendarEventsDto.getColor())
                    .priorityLevel(calendarEventsDto.getPriorityLevel())
                    .build();

            calendarEventsRepository.save(calendarEvent);

            log.info("[addEvent] User {} successfully added a new event: {}", user.getName(), calendarEvent.getTitle());
            return BaseResponse.builder()
                    .code(HttpStatus.CREATED)
                    .status(HttpStatus.CREATED.value())
                    .message("Successfully added event")
                    .data(calendarEvent)
                    .build();

        } catch (Exception e) {
            log.error("[addEvent] Internal server error while adding event", e);
            return BaseResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error adding event, internal server error")
                    .data(null)
                    .build();
        }
    }

    public BaseResponse setCalendarEventCompleted(SetCalendarEventCompletedDto setCalendarEventCompletedDto) {
        try{
            Users user = UserAuthHelper.getUser();
            FamilyMembers member = null;

            if (user != null) {
                member = user.getFamilyMembers();
            }else{
                log.error("[setCalendarEventCompleted] User not found");
                return BaseResponse.builder()
                        .code(HttpStatus.CONFLICT)
                        .status(HttpStatus.CONFLICT.value())
                        .message("User not found, Auth Error")
                        .data(null)
                        .build();
            }

            if(member != null){
                log.info("[setCalendarEventCompleted] {} is trying to get all calendar events", user.getName());
                int result = calendarEventsRepository.setCalendarEventCompleted(setCalendarEventCompletedDto.getEventId(), user.getFamilyMembers().getGroup());

                return BaseResponse.builder()
                        .status(HttpStatus.OK.value())
                        .code(HttpStatus.OK)
                        .data(result)
                        .message("Success setting event completed")
                        .build();
            }else{
                log.error("[setCalendarEventCompleted] Failed setting event to completed");
                return BaseResponse.builder()
                        .code(HttpStatus.CONFLICT)
                        .status(HttpStatus.CONFLICT.value())
                        .message("User is not in a family group")
                        .data(null)
                        .build();
            }

        }catch (Exception e){
            log.info("[setCalendarEventCompleted] Failed setting event to completed");
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Failed setting event to completed")
                    .build();
        }
    }
}
