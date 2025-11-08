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
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyQuestionResponseDto implements Serializable {
    @JsonProperty("question_id")
    private String questionId;
    @JsonProperty("question_message")
    private String questionMessage;
    @JsonProperty("is_completed")
    private Boolean isCompleted;
    @JsonProperty("responses")
    private List<Responses> responses;

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Responses implements Serializable {
        @JsonProperty("response_id")
        private String responseId;
        @JsonProperty("name")
        private String name;
        @JsonProperty("mood_value")
        private Integer moodValue;
        @JsonProperty("response")
        private String response;
        @JsonProperty("created_at")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        private LocalDateTime createdAt;
        @JsonProperty("updated_at")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        private LocalDateTime updatedAt;
    }
}
