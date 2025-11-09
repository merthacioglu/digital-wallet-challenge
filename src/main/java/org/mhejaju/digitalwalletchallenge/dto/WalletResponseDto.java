package org.mhejaju.digitalwalletchallenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Schema(description = "Wallet response data transfer object")
@Builder
public record WalletResponseDto(
        @Schema(description = "Unique wallet identifier (UUID)", example = "ebed7406-0593-4e01-bd7b-7f5abee2315f")
        String walletId,

        @Schema(description = "Name of the wallet", example = "Test Wallet 2")
        String walletName,

        @Schema(description = "Full name of the customer who owns the wallet", example = "John Doe")
        String customer,

        @Schema(description = "Wallet currency code", example = "TRY")
        String currency,

        @Schema(description = "Indicates if wallet is active for withdrawal operations", example = "true")
        boolean activeForWithdraw,

        @Schema(description = "Indicates if wallet is active for shopping operations", example = "true")
        boolean activeForShopping,

        @Schema(description = "Total balance in the wallet", example = "1900.00")
        BigDecimal balance,

        @Schema(description = "Available balance that can be used for transactions", example = "400.00")
        BigDecimal usableBalance
) {
}
