package org.mhejaju.digitalwalletchallenge.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.mhejaju.digitalwalletchallenge.constants.Regex;
import org.mhejaju.digitalwalletchallenge.constants.ValidationMessages;

public record RegisterDto(
        @Size(min = 2, max = 50, message = ValidationMessages.NAME_SIZE_MISMATCH)
        @NotEmpty(message = ValidationMessages.NAME_REQUIRED)
        String name,
        @Size(min = 2, max = 50, message = ValidationMessages.SURNAME_SIZE_MISMATCH)
        @NotEmpty(message = ValidationMessages.SURNAME_REQUIRED)
        String surname,

        @Pattern(regexp = Regex.TR_IDENTITY_NO_REGEX, message = ValidationMessages.TR_IDENTITY_NO_FORMAT_ERROR)
        @NotEmpty(message = ValidationMessages.TR_IDENTITY_NO_REQUIRED)
        String trIdentityNo,

        @Email(message = ValidationMessages.EMAIL_FORMAT_ERROR)
        @NotEmpty(message = ValidationMessages.EMAIL_REQUIRED)
        String email,


        @Pattern(
                regexp = Regex.PASSWORD_REGEX,
                message = ValidationMessages.PASSWORD_FORMAT_ERROR
        )
        @NotEmpty(message = ValidationMessages.PASSWORD_REQUIRED)
        String password,
        String repeatPassword) {
}

