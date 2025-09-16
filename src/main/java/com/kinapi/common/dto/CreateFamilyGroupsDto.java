package com.kinapi.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateFamilyGroupsDto implements Serializable {
    @JsonProperty("family_group_name")
    private String familyName;
    @JsonProperty("reset_time")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime resetTime;
}
