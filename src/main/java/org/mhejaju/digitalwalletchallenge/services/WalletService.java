package org.mhejaju.digitalwalletchallenge.services;


import org.mhejaju.digitalwalletchallenge.dto.WalletDto;
import org.mhejaju.digitalwalletchallenge.dto.WalletResponseDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;

import java.util.List;

public interface WalletService {
    void addWallet(WalletDto walletDto, Customer customer);
    void addWallet(String customerTrIdentityNo, WalletDto walletDto);
    List<WalletResponseDto> listWallets(Customer customer);
    List<WalletResponseDto> listWallets(String customerTrIdentityNo);
}
