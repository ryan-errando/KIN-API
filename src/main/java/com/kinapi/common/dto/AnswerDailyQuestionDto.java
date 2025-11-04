package com.kinapi.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnswerDailyQuestionDto implements Serializable {
    @JsonProperty("family_question_id")
    private UUID familyQuestionId;
    @JsonProperty("response")
    private String response;
    @JsonProperty("mood_value")
    private Integer moodValue;
}
