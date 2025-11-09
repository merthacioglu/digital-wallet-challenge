package org.mhejaju.digitalwalletchallenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.mhejaju.digitalwalletchallenge.constants.ValidationMessages;

@Schema(description = "Request to change transaction status (approve or deny)")
public record TransactionStatusChangeRequestDto(
        @Schema(description = "Unique identifier of the transaction", example = "381d7a69-e6d7-401b-8cd5-6c0a394b2d6a", required = true)
        @NotNull(message = ValidationMessages.TRANSACTION_ID_REQUIRED)
        String transactionId,

        @Schema(description = "New status for the transaction (APPROVED or DENIED)", example = "APPROVED", required = true)
        @NotNull(message = ValidationMessages.STATUS_REQUIRED)
        @Pattern(regexp = "^(APPROVED|DENIED)$", message = ValidationMessages.STATUS_INVALID)
        String status
) {

}
