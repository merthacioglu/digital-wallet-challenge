package org.mhejaju.digitalwalletchallenge.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Error response containing details about the error that occurred")
public record ErrorDto(
        @Schema(description = "API path where the error occurred", example = "uri=/api/v1/deposit")
        String apiPath,

        @Schema(description = "HTTP status code of the error", example = "404")
        int statusCode,

        @Schema(description = "Detailed error message", example = "No wallet with id: 381d7a69-e6d7-401b-8cd5-6c0a394b2d6 found belonging to the user with TR Identity Number: 10195827811")
        String message,

        @Schema(description = "Timestamp when the error occurred", example = "2025-11-09T15:45:30")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp
) {
}