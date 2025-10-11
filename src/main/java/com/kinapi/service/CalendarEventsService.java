package com.kinapi.service;

import com.kinapi.common.dto.AddCalendarEventsDto;
import com.kinapi.common.dto.CalendarEventsDto;
import com.kinapi.common.dto.SetCalendarEventCompletedDto;
import com.kinapi.common.entity.BaseResponse;
import com.kinapi.common.entity.CalendarEvents;
import com.kinapi.common.entity.FamilyMembers;
import com.kinapi.common.entity.Users;
import com.kinapi.common.repository.CalendarEventsRepository;
import com.kinapi.common.specification.CalendarEventsSpecification;
import com.kinapi.common.util.UserAuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalendarEventsService {
    private final CalendarEventsRepository calendarEventsRepository;

    public BaseResponse getEvents(LocalDateTime startTime, LocalDateTime endTime) {
        try{
            Users user = UserAuthHelper.getUser();
            FamilyMembers member = user.getFamilyMembers();

            if(member != null){
                log.info("[getEvents] {} is trying to get all calendar events", user.getName());
                Specification<CalendarEvents> spec = CalendarEventsSpecification.filterByFamilyGroup(member.getGroup())
                        .and(CalendarEventsSpecification.filterByDateTimeRange(startTime, endTime));
                Sort sort = Sort.by(Sort.Direction.ASC, "startTime");
                List<CalendarEvents> events = calendarEventsRepository.findAll(spec, sort);

                log.info("[getEvents] Fetching all events from \"{}\" family group", member.getGroup().getGroupName());
                List<CalendarEventsDto> eventList = new ArrayList<>();
                for (CalendarEvents event : events) {
                    eventList.add(CalendarEventsDto.builder()
                            .eventId(event.getId())
                            .createdById(event.getCreatedBy().getId())
                            .createdBy(event.getCreatedBy().getUser().getName())
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
                log.error("[getEvents] Failed to add new event");
                throw new Exception("User is not in a family group");
            }

        }catch (Exception e){
            log.info("[getEvents] Failed retrieving all calendar events: {}", e.getMessage());
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    public BaseResponse addEvent(AddCalendarEventsDto addCalendarEventsDto) {
        try {
            Users user = UserAuthHelper.getUser();
            FamilyMembers member = user.getFamilyMembers();

            if (member == null) {
                log.error("[addEvent] User {} is not in a family group", user.getName());
                throw new Exception("User is not in a family group");
            }

            CalendarEvents calendarEvent = CalendarEvents.builder()
                    .createdBy(member)
                    .title(addCalendarEventsDto.getTitle())
                    .description(addCalendarEventsDto.getDescription())
                    .location(addCalendarEventsDto.getLocation())
                    .startTime(addCalendarEventsDto.getStartTime())
                    .endTime(addCalendarEventsDto.getEndTime())
                    .eventType(addCalendarEventsDto.getEventType())
                    .color(addCalendarEventsDto.getColor())
                    .priorityLevel(addCalendarEventsDto.getPriorityLevel())
                    .build();

            calendarEventsRepository.save(calendarEvent);

            log.info("[addEvent] User {} successfully added a new event: {}", user.getName(), calendarEvent.getTitle());
            return BaseResponse.builder()
                    .code(HttpStatus.CREATED)
                    .status(HttpStatus.CREATED.value())
                    .message("Successfully added event")
                    .build();

        } catch (Exception e) {
            log.error("[addEvent] Internal server error while adding event: {}", e.getMessage());
            return BaseResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    public BaseResponse setCalendarEventCompleted(SetCalendarEventCompletedDto setCalendarEventCompletedDto) {
        try{
            Users user = UserAuthHelper.getUser();
            FamilyMembers member = user.getFamilyMembers();

            if(member != null){
                log.info("[setCalendarEventCompleted] {} is trying to get all calendar events", user.getName());
                Optional<CalendarEvents> optionalCalendarEvents = calendarEventsRepository.findById(setCalendarEventCompletedDto.getEventId());

                if(optionalCalendarEvents.isEmpty()){
                    throw new Exception("Calendar events is not found");
                }

                CalendarEvents calendarEvents = optionalCalendarEvents.get();
                calendarEvents.setIsCompleted(setCalendarEventCompletedDto.getIsComplete());
                calendarEventsRepository.save(calendarEvents);

                return BaseResponse.builder()
                        .status(HttpStatus.OK.value())
                        .code(HttpStatus.OK)
                        .message("Success setting event completed")
                        .build();
            }else{
                log.error("[setCalendarEventCompleted] Failed setting event to completed");
                throw new Exception("User is not in a family group");
            }

        }catch (Exception e){
            log.info("[setCalendarEventCompleted] Failed setting event to completed: {}", e.getMessage());
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }
}
