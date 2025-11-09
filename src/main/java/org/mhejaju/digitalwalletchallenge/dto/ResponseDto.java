package org.mhejaju.digitalwalletchallenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard API response object containing status information")
public record ResponseDto(
        @Schema(
                description = "HTTP status code of the response",
                example = "201"
        )
        int statusCode,

        @Schema(
                description = "Human-readable message describing the response",
                example = "Wallet created successfully"
        )
        String message) {

}
