package org.mhejaju.digitalwalletchallenge.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransactionResponseDto(
        String walletId,
        String oppositeParty,
        String oppositePartyType,
        String type,
        String status,
        BigDecimal amount

) {
}
