package org.mhejaju.digitalwalletchallenge.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record WalletResponseDto(
        String walletId,
        String walletName,
        String customer,
        String currency,
        boolean activeForWithdraw,
        boolean activeForShopping,
        BigDecimal balance,
        BigDecimal usableBalance
) {
}
