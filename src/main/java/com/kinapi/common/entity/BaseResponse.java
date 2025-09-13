package com.kinapi.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.io.Serializable;


@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record BaseResponse (
        Integer status,
        @JsonIgnore
        HttpStatus code,
        String message,
        Object data
) implements Serializable {
}
