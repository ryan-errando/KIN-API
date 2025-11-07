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

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FamilyDailyQuestionDto implements Serializable {
    @JsonProperty("id")
    private String id;
    @JsonProperty("question_text")
    private String questionText;
    @JsonProperty("total_member")
    private Integer totalMember;
    @JsonProperty("answered_count")
    private Integer answeredCount;
    @JsonProperty("assigned_date")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime assignedDate;
    @JsonProperty("is_completed")
    private Boolean isCompleted;
}
