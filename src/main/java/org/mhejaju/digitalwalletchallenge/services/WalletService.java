package org.mhejaju.digitalwalletchallenge.services;


import org.mhejaju.digitalwalletchallenge.dto.WalletDto;
import org.mhejaju.digitalwalletchallenge.dto.WalletResponseDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;

import java.util.List;

public interface WalletService {
    void addWallet(WalletDto walletDto, Customer customer);
    List<WalletResponseDto> listWallets(Customer customer);
}
