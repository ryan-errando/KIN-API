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
public class GroupReflectionSummarizationDto implements Serializable {
    @JsonProperty("name")
    private String name;
    @JsonProperty("mood")
    private String mood;
    @JsonProperty("reflection")
    private String reflection;
}
