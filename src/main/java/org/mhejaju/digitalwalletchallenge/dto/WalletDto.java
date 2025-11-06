package org.mhejaju.digitalwalletchallenge.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mhejaju.digitalwalletchallenge.constants.ValidationMessages;
import org.mhejaju.digitalwalletchallenge.entities.enums.Currency;

public record WalletDto(
        @Size(min = 2, max = 50, message = ValidationMessages.NAME_SIZE_MISMATCH)
        @NotEmpty(message = ValidationMessages.NAME_REQUIRED)
        String walletName,

        @NotNull(message = ValidationMessages.INCORRECT_CURRENCY_ERROR)
        Currency currency,
        boolean activeForShopping,
        boolean activeForWithdraw
) {
}
