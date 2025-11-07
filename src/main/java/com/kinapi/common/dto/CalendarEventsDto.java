package com.kinapi.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalendarEventsDto implements Serializable {

    @JsonProperty("event_id")
    private UUID eventId;

    @JsonProperty("created_by_id")
    private UUID createdById;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("location")
    private String location;

    @JsonProperty("start_time")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime startTime;

    @JsonProperty("end_time")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime endTime;

    @JsonProperty("is_completed")
    private Boolean isCompleted;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("color")
    private String color;

    @JsonProperty("priority_level")
    private Integer priorityLevel;

    @JsonProperty("assigned_to")
    private String assignedTo;
}
