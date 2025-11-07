package org.mhejaju.digitalwalletchallenge.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record WalletTransactionListResponseDto(
        String walletName,
        BigDecimal balance,
        BigDecimal usableBalance,
        List<TransactionResponseDto> transactions
) {

}
