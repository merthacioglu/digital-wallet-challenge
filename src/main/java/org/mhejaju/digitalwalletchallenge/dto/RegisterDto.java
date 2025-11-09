package org.mhejaju.digitalwalletchallenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.mhejaju.digitalwalletchallenge.constants.Regex;
import org.mhejaju.digitalwalletchallenge.constants.ValidationMessages;

@Schema(description = "Request to register a new customer account")
public record RegisterDto(
        @Schema(description = "Customer's first name", example = "John", required = true, minLength = 2, maxLength = 50)
        @Size(min = 2, max = 50, message = ValidationMessages.NAME_SIZE_MISMATCH)
        @NotEmpty(message = ValidationMessages.NAME_REQUIRED)
        String name,

        @Schema(description = "Customer's last name", example = "Doe", required = true, minLength = 2, maxLength = 50)
        @Size(min = 2, max = 50, message = ValidationMessages.SURNAME_SIZE_MISMATCH)
        @NotEmpty(message = ValidationMessages.SURNAME_REQUIRED)
        String surname,

        @Schema(description = "Turkish Identity Number (11 digits)", example = "10195827811", required = true, pattern = "^[0-9]{11}$")
        @Pattern(regexp = Regex.TR_IDENTITY_NO_REGEX, message = ValidationMessages.TR_IDENTITY_NO_INVALID)
        @NotEmpty(message = ValidationMessages.TR_IDENTITY_NO_REQUIRED)
        String trIdentityNo,

        @Schema(description = "Customer's email address", example = "john.doe@example.com", required = true, format = "email")
        @Email(message = ValidationMessages.EMAIL_INVALID)
        @NotEmpty(message = ValidationMessages.EMAIL_REQUIRED)
        String email,

        @Schema(description = "Customer's password", example = "SecurePass123!", required = true)
        String password
) {
}