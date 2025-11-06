package org.mhejaju.digitalwalletchallenge.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.mhejaju.digitalwalletchallenge.constants.Regex;
import org.mhejaju.digitalwalletchallenge.constants.ValidationMessages;

public record LoginDto(
        @Email(message = ValidationMessages.EMAIL_FORMAT_ERROR)
        @NotEmpty(message = ValidationMessages.EMAIL_REQUIRED)
        String email,

        @Pattern(
                regexp = Regex.PASSWORD_REGEX,
                message = ValidationMessages.PASSWORD_FORMAT_ERROR
        )
        @NotEmpty(message = ValidationMessages.PASSWORD_REQUIRED)
        String password) {
}
