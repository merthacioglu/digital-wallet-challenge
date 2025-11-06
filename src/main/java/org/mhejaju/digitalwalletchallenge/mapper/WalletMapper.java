package org.mhejaju.digitalwalletchallenge.mapper;

import org.mhejaju.digitalwalletchallenge.dto.WalletDto;
import org.mhejaju.digitalwalletchallenge.entities.Wallet;

import java.math.BigDecimal;

public class WalletMapper {
    public static Wallet mapToWallet(WalletDto walletDto) {

        Wallet wallet = new Wallet();

        wallet.setWalletName(walletDto.walletName());
        wallet.setCurrency(walletDto.currency());
        wallet.setActiveForShopping(wallet.isActiveForShopping());
        wallet.setActiveForWithdraw(wallet.isActiveForWithdraw());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUsableBalance(BigDecimal.ZERO);

        return wallet;
    }
}
