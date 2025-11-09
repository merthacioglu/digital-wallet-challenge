package org.mhejaju.digitalwalletchallenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.mhejaju.digitalwalletchallenge.constants.ValidationMessages;

import java.math.BigDecimal;

@Schema(description = "Deposit request data transfer object")
public record DepositDto(

        @Schema(
                description = "Amount to deposit (must be between 0.01 and 10000.00)",
                example = "500.00",
                minimum = "0.01",
                maximum = "10000.00"
        )
        @NotNull(message = ValidationMessages.AMOUNT_REQUIRED)
        @DecimalMin(value = "0.01", message = ValidationMessages.ERR_MIN_AMOUNT)
        @DecimalMax(value = "10000.00", message = ValidationMessages.ERR_MAX_AMOUNT)
        BigDecimal amount,

        @Schema(
                description = "Unique identifier of the wallet to deposit into",
                example = "ebed7406-0593-4e01-bd7b-7f5abee2315f"
        )
        @NotNull(message = ValidationMessages.WALLET_ID_REQUIRED)
        String walletId,


        @Schema(
                description = "Type of deposit source (IBAN or PAYMENT)",
                example = "IBAN",
                allowableValues = {"IBAN", "PAYMENT"}
        )
        @NotNull(message = ValidationMessages.SOURCE_TYPE_REQUIRED)
        @Pattern(regexp = "^(IBAN|PAYMENT)$", message = ValidationMessages.SOURCE_TYPE_INVALID)
        String sourceType,

        @Schema(
                description = "Source identifier - IBAN number or payment ID depending on sourceType",
                example = "TR330006100519786457841326"
        )
        @NotNull(message = ValidationMessages.IBAN_OR_PAYMENT_ID_REQUIRED)
        String source
) {
}
