package org.mhejaju.digitalwalletchallenge.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.mhejaju.digitalwalletchallenge.constants.ValidationMessages;

public record TransactionStatusChangeRequestDto(
        @NotNull(message = ValidationMessages.TRANSACTION_ID_REQUIRED)
        String transactionId,
        @NotNull(message = ValidationMessages.STATUS_REQUIRED)
        @Pattern(regexp = "^(APPROVED|DENIED)$", message = ValidationMessages.STATUS_FORMAT_ERROR)
        String status
) {

}
