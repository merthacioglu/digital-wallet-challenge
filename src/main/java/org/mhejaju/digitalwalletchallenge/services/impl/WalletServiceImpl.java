package org.mhejaju.digitalwalletchallenge.services.impl;

import lombok.RequiredArgsConstructor;
import org.mhejaju.digitalwalletchallenge.dto.WalletDto;
import org.mhejaju.digitalwalletchallenge.entities.Wallet;
import org.mhejaju.digitalwalletchallenge.repositories.WalletRepository;
import org.mhejaju.digitalwalletchallenge.services.WalletService;
import org.mhejaju.digitalwalletchallenge.mapper.WalletMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public void addWallet(WalletDto walletDto) {
        Wallet wallet = WalletMapper.mapToWallet(walletDto);

        walletRepository.save(wallet);

    }
}
