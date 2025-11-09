package org.mhejaju.digitalwalletchallenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.mhejaju.digitalwalletchallenge.constants.ValidationMessages;

import java.math.BigDecimal;

@Schema(description = "Request to withdraw funds from a wallet")
public record WithdrawDto(
        @Schema(description = "Amount to withdraw", example = "500.00", required = true, minimum = "0.01", maximum = "10000.00")
        @NotNull(message = ValidationMessages.AMOUNT_REQUIRED)
        @DecimalMin(value = "0.01", message = ValidationMessages.ERR_MIN_AMOUNT)
        @DecimalMax(value = "10000.00", message = ValidationMessages.ERR_MAX_AMOUNT)
        BigDecimal amount,

        @Schema(description = "Unique identifier of the wallet to withdraw from", example = "ebed7406-0593-4e01-bd7b-7f5abee2315f", required = true)
        @NotNull(message = ValidationMessages.WALLET_ID_REQUIRED)
        String walletId,

        @Schema(description = "Type of destination (IBAN or PAYMENT)", example = "IBAN", required = true, allowableValues = {"IBAN", "PAYMENT"})
        @NotNull(message = ValidationMessages.DESTINATION_TYPE_REQUIRED)
        @Pattern(regexp = "^(IBAN|PAYMENT)$", message = ValidationMessages.DESTINATION_TYPE_INVALID)
        String destinationType,

        @Schema(description = "IBAN or Payment ID to send funds to", example = "TR330006100519786457841326", required = true)
        @NotNull(message = ValidationMessages.IBAN_OR_PAYMENT_ID_REQUIRED)
        String destination
) {
}