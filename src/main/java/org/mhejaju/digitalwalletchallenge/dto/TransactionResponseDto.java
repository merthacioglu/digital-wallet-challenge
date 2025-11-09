package org.mhejaju.digitalwalletchallenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@Schema(description = "Transaction response containing transaction details")
public record TransactionResponseDto(
        @Schema(description = "Unique identifier of the wallet", example = "ebed7406-0593-4e01-bd7b-7f5abee2315f")
        String walletId,

        @Schema(description = "IBAN or payment account identifier of the opposite party", example = "TR330006100519786457841326")
        String oppositeParty,

        @Schema(description = "Type of the opposite party (IBAN or PAYMENT)", example = "IBAN")
        String oppositePartyType,

        @Schema(description = "Transaction type (DEPOSIT or WITHDRAW)", example = "DEPOSIT")
        String type,

        @Schema(description = "Transaction status (PENDING, APPROVED, or DENIED)", example = "APPROVED")
        String status,

        @Schema(description = "Transaction amount", example = "500.00")
        BigDecimal amount

) {
}
