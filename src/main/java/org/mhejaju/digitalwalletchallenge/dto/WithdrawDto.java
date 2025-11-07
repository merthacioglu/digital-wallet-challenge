package org.mhejaju.digitalwalletchallenge.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.mhejaju.digitalwalletchallenge.constants.ValidationMessages;

import java.math.BigDecimal;

public record WithdrawDto(
        @NotNull(message = ValidationMessages.AMOUNT_REQUIRED)
        @DecimalMin(value = "0.01", message = ValidationMessages.MINIMUM_AMOUNT_ERROR)
        @DecimalMax(value = "10000.00", message = ValidationMessages.MAXIMUM_AMOUNT_ERROR)
        BigDecimal amount,

        @NotNull(message = ValidationMessages.WALLET_ID_REQUIRED)
        String walletId,

        @NotNull(message = ValidationMessages.DESTINATION_TYPE_REQUIRED)
        @Pattern(regexp = "^(IBAN|PAYMENT)$", message = ValidationMessages.DESTINATION_TYPE_FORMAT_ERROR)
        String destinationType,

        @NotNull(message = ValidationMessages.IBAN_OR_PAYMENT_ID_REQUIRED)
        String destination
) {
}
