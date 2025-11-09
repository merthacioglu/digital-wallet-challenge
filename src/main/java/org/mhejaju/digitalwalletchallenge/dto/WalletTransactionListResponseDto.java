package org.mhejaju.digitalwalletchallenge.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Schema(description = "Wallet transaction list response containing wallet balance and transaction history")
public record WalletTransactionListResponseDto(
        @Schema(description = "Name of the wallet", example = "Test Wallet 2")
        String walletName,

        @Schema(description = "Total balance in the wallet", example = "1900.00")
        BigDecimal balance,

        @Schema(description = "Usable balance available for withdrawals", example = "400.00")
        BigDecimal usableBalance,

        @ArraySchema(schema = @Schema(implementation = TransactionResponseDto.class))
        @Schema(description = "List of transactions associated with the wallet")
        List<TransactionResponseDto> transactions
) {
}