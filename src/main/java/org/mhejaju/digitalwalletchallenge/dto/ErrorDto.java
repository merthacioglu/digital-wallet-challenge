package org.mhejaju.digitalwalletchallenge.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ErrorDto(String apiPath,
                       int statusCode,
                       String message,

                       @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
                       LocalDateTime timestamp) {
}
