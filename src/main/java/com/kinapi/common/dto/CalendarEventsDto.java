package com.kinapi.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalendarEventsDto implements Serializable {
    @JsonProperty("event_id")
    private String eventId;
    @JsonProperty("created_by_id")
    private String createdById;
    @JsonProperty("created_by")
    private String createdBy;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("location")
    private String location;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("end_time")
    private String endTime;
    @JsonProperty("is_all_day")
    private Boolean allDay;
    @JsonProperty("event_type")
    private String eventType;
    @JsonProperty("color")
    private String color;
    @JsonProperty("priority_level")
    private Integer priorityLevel;
}
