package com.kinapi.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kinapi.common.entity.FamilyMembers;
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

    // Optional: name of the creator (for display in API response)
    @JsonProperty("created_by")
    private FamilyMembers createdBy;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("location")
    private String location;

    @JsonProperty("start_time")
    private LocalDateTime startTime;

    @JsonProperty("end_time")
    private LocalDateTime endTime;

    @JsonProperty("is_completed")
    private Boolean isCompleted;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("color")
    private String color;

    @JsonProperty("priority_level")
    private Integer priorityLevel;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
