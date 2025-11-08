package org.mhejaju.digitalwalletchallenge.services.impl;

import lombok.RequiredArgsConstructor;
import org.mhejaju.digitalwalletchallenge.dto.WalletDto;
import org.mhejaju.digitalwalletchallenge.dto.WalletResponseDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;
import org.mhejaju.digitalwalletchallenge.entities.Wallet;
import org.mhejaju.digitalwalletchallenge.exceptions.CustomerNotFoundException;
import org.mhejaju.digitalwalletchallenge.repositories.CustomerRepository;
import org.mhejaju.digitalwalletchallenge.repositories.WalletRepository;
import org.mhejaju.digitalwalletchallenge.services.WalletService;
import org.mhejaju.digitalwalletchallenge.mapper.WalletMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void addWallet(WalletDto walletDto, Customer customer) {
        Wallet wallet = WalletMapper.mapToWallet(walletDto);
        wallet.setCustomer(customer);
        walletRepository.save(wallet);

    }

    @Override
    public void addWallet(String customerTrIdentityNo, WalletDto walletDto) {
        Optional<Customer> optionalCustomer = customerRepository.findByTrIdentityNo(customerTrIdentityNo);
        Customer customer = optionalCustomer.orElseThrow(() ->
                new CustomerNotFoundException(customerTrIdentityNo));

        addWallet(walletDto, customer);
    }


    @Override
    @Transactional(readOnly = true)
    public List<WalletResponseDto> listWallets(Customer customer) {
        List<Wallet> wallets = walletRepository.findByCustomerId(customer.getId());
        return wallets.stream()
                .map(WalletMapper::mapToWalletResponseDto)
                .toList();
    }
}
