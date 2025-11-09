package org.mhejaju.digitalwalletchallenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response containing JWT tokens")
public record AuthenticationDto(
        @Schema(
                description = "JWT access token for authenticating API requests",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        String authToken,

        @Schema(
                description = "JWT refresh token for obtaining new access tokens",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        String refreshToken
) {
}