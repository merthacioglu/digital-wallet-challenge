package org.mhejaju.digitalwalletchallenge.dto;

        import io.swagger.v3.oas.annotations.media.Schema;
        import jakarta.validation.constraints.Email;
        import jakarta.validation.constraints.NotEmpty;
        import org.mhejaju.digitalwalletchallenge.constants.ValidationMessages;

        @Schema(description = "Request to authenticate a customer")
        public record LoginDto(
                @Schema(description = "Customer's email address", example = "john.doe@example.com", required = true, format = "email")
                @Email(message = ValidationMessages.EMAIL_INVALID)
                @NotEmpty(message = ValidationMessages.EMAIL_REQUIRED)
                String email,

                @Schema(description = "Customer's password", example = "SecurePass123!", required = true)
                @NotEmpty(message = ValidationMessages.PASSWORD_REQUIRED)
                String password
        ) {
        }