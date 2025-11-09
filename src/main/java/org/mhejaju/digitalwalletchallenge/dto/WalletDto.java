package org.mhejaju.digitalwalletchallenge.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.mhejaju.digitalwalletchallenge.constants.ValidationMessages;

@Schema(
        name = "Wallet Information",
        description = "Data transfer object for creating a new wallet")
public record WalletDto(
        @Schema(
                description = "Name of the wallet",
                example = "My Savings Wallet",
                minLength = 2,
                maxLength = 50
        )
        @Size(min = 2, max = 50, message = ValidationMessages.NAME_SIZE_MISMATCH)
        @NotEmpty(message = ValidationMessages.NAME_REQUIRED)
        String walletName,


        @Schema(
                description = "Currency type for the wallet",
                example = "TRY",
                allowableValues = {"USD", "TRY", "EUR"}
        )
        @NotNull(message = ValidationMessages.CURRENCY_REQUIRED)
        @Pattern(regexp = "^(USD|TRY|EUR)$", message = ValidationMessages.CURRENCY_INVALID) //TODO: find a generic solution against new currencies to be added
        String currency,

        @Schema(
                description = "Whether the wallet is active for shopping transactions",
                example = "true"
        )
        boolean activeForShopping,

        @Schema(
                description = "Whether the wallet is active for withdrawal transactions",
                example = "true"
        )
        boolean activeForWithdraw
) {
}
