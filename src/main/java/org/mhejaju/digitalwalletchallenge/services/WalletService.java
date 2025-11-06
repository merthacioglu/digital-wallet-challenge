package org.mhejaju.digitalwalletchallenge.services;


import org.mhejaju.digitalwalletchallenge.dto.WalletDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;

public interface WalletService {
    void addWallet(WalletDto walletDto, Customer customer);
}
