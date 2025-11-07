package org.mhejaju.digitalwalletchallenge.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.mhejaju.digitalwalletchallenge.constants.ValidationMessages;

public record WalletDto(
        @Size(min = 2, max = 50, message = ValidationMessages.NAME_SIZE_MISMATCH)
        @NotEmpty(message = ValidationMessages.NAME_REQUIRED)
        String walletName,

        @NotNull(message = ValidationMessages.CURRENCY_REQUIRED)
        @Pattern(regexp = "^(USD|TRY|EUR)$", message = ValidationMessages.INCORRECT_CURRENCY_ERROR) //TODO: find a generic solution against new currencies to be added
        String currency,
        boolean activeForShopping,
        boolean activeForWithdraw
) {
}
