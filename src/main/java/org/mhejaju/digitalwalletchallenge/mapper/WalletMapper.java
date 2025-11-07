package org.mhejaju.digitalwalletchallenge.mapper;

import org.mhejaju.digitalwalletchallenge.dto.WalletDto;
import org.mhejaju.digitalwalletchallenge.dto.WalletResponseDto;
import org.mhejaju.digitalwalletchallenge.entities.Wallet;
import org.mhejaju.digitalwalletchallenge.entities.enums.Currency;

import java.math.BigDecimal;

public class WalletMapper {
    public static Wallet mapToWallet(WalletDto walletDto) {

        Wallet wallet = new Wallet();

        wallet.setWalletName(walletDto.walletName());
        wallet.setCurrency(Currency.valueOf(walletDto.currency()));
        wallet.setActiveForShopping(wallet.isActiveForShopping());
        wallet.setActiveForWithdraw(wallet.isActiveForWithdraw());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUsableBalance(BigDecimal.ZERO);

        return wallet;
    }

    public static WalletResponseDto mapToWalletResponseDto(Wallet wallet) {
        return WalletResponseDto.builder()
                .walletId(wallet.getWalletId())
                .walletName(wallet.getWalletName())
                .customer(wallet.getCustomer().getName() + " " + wallet.getCustomer().getSurname())
                .currency(wallet.getCurrency().name())
                .activeForShopping(wallet.isActiveForShopping())
                .activeForWithdraw(wallet.isActiveForWithdraw())
                .balance(wallet.getBalance())
                .usableBalance(wallet.getUsableBalance())
                .build();

    }
}
