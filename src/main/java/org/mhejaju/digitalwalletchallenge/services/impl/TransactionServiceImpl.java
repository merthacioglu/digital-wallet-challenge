package org.mhejaju.digitalwalletchallenge.services.impl;

import lombok.RequiredArgsConstructor;
import org.mhejaju.digitalwalletchallenge.dto.DepositDto;
import org.mhejaju.digitalwalletchallenge.dto.TransactionResponseDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;
import org.mhejaju.digitalwalletchallenge.entities.Transaction;
import org.mhejaju.digitalwalletchallenge.entities.Wallet;
import org.mhejaju.digitalwalletchallenge.entities.enums.Status;
import org.mhejaju.digitalwalletchallenge.exceptions.WalletNotFoundException;
import org.mhejaju.digitalwalletchallenge.mapper.TransactionMapper;
import org.mhejaju.digitalwalletchallenge.repositories.TransactionRepository;
import org.mhejaju.digitalwalletchallenge.repositories.WalletRepository;
import org.mhejaju.digitalwalletchallenge.services.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public TransactionResponseDto makeDeposit(DepositDto depositDto, Customer customer) {
        List<Wallet> wallets = walletRepository.findByCustomerId(customer.getId());
        Wallet targetWallet = wallets.stream().filter(w -> w.getWalletId().equals(depositDto.walletId()))
                .findAny().orElseThrow(() -> new WalletNotFoundException(customer.getTrIdentityNo(), depositDto.walletId()));

        targetWallet.setBalance(targetWallet.getBalance().add(depositDto.amount()));
        Transaction transaction = TransactionMapper.mapToTransaction(depositDto);
        transaction.setWallet(targetWallet);
        if (depositDto.amount().compareTo(BigDecimal.valueOf(1000.0)) < 0) {
            targetWallet.setUsableBalance(targetWallet.getUsableBalance().add(depositDto.amount()));
            transaction.setStatus(Status.APPROVED);
        } else {
            transaction.setStatus(Status.PENDING);
        }

        transactionRepository.save(transaction);
        return TransactionResponseDto.builder()
                .walletId(targetWallet.getWalletId())
                .oppositeParty(transaction.getOppositeParty())
                .oppositePartyType(transaction.getOppositePartyType().name())
                .type(transaction.getType().name())
                .status(transaction.getStatus().name())
                .amount(transaction.getAmount())
                .build();
    }
}
