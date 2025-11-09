package org.mhejaju.digitalwalletchallenge.dto;

public record AuthenticationDto(
        String authToken,
        String refreshToken) {
}
