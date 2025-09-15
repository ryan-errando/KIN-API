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
public class TopicHistoryDto implements Serializable {
    @JsonProperty("topic_text")
    private String topicText;
    @JsonProperty("topic_category")
    private String topicCategory;
    @JsonProperty("topic_is_favorite")
    private Boolean topicIsFavorite;
}
