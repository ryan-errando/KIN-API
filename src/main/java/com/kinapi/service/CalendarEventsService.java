package com.kinapi.service;

import com.kinapi.common.dto.AddCalendarEventsDto;
import com.kinapi.common.dto.CalendarEventsDto;
import com.kinapi.common.dto.SetCalendarEventCompletedDto;
import com.kinapi.common.dto.UpdateCalendarEventsDto;
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
import org.springframework.transaction.annotation.Transactional;

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
                            .priorityLevel(event.getPriorityLevel())
                            .assignedTo(event.getAssignedTo())
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
                    .priorityLevel(addCalendarEventsDto.getPriorityLevel())
                    .assignedTo(addCalendarEventsDto.getAssignedTo())
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

    @Transactional(rollbackFor = Exception.class)
    public BaseResponse updateCalendarEventsDto(UpdateCalendarEventsDto requestDto) {
        try{
            log.info("[updateCalendarEventsDto] Updating calendar event");
            Optional<CalendarEvents> eventsOptional = calendarEventsRepository.findById(requestDto.getId());

            if(eventsOptional.isEmpty()){
                throw new Exception("Calendar events is not found");
            }

            CalendarEvents events = eventsOptional.get();

            events.setTitle(requestDto.getTitle());
            events.setDescription(requestDto.getDescription());
            events.setLocation(requestDto.getLocation());
            events.setStartTime(requestDto.getStartTime());
            events.setEndTime(requestDto.getEndTime());
            events.setEventType(requestDto.getEventType());
            events.setPriorityLevel(requestDto.getPriorityLevel());
            events.setAssignedTo(requestDto.getAssignedTo());

            calendarEventsRepository.save(events);

            log.info("[updateCalendarEventsDto] Calendar event updated successfully");
            return BaseResponse.builder()
                    .status(HttpStatus.OK.value())
                    .code(HttpStatus.OK)
                    .message("Successfully updated event")
                    .build();

        }catch (Exception e){
            log.error("[updateCalendarEventsDto] Failed to update calendar events: {}", e.getMessage());
            return BaseResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Failed updating event: " + e.getMessage())
                    .build();
        }
    }
}
