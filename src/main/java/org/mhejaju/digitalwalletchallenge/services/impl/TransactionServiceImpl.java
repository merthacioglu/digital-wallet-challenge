package org.mhejaju.digitalwalletchallenge.services.impl;

import lombok.RequiredArgsConstructor;
import org.mhejaju.digitalwalletchallenge.dto.DepositDto;
import org.mhejaju.digitalwalletchallenge.dto.TransactionResponseDto;
import org.mhejaju.digitalwalletchallenge.dto.WalletTransactionListResponseDto;
import org.mhejaju.digitalwalletchallenge.dto.WithdrawDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;
import org.mhejaju.digitalwalletchallenge.entities.Transaction;
import org.mhejaju.digitalwalletchallenge.entities.Wallet;
import org.mhejaju.digitalwalletchallenge.entities.enums.Status;
import org.mhejaju.digitalwalletchallenge.exceptions.InsufficientFundsException;
import org.mhejaju.digitalwalletchallenge.exceptions.WalletNotAvailableException;
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

    @Override
    @Transactional
    public TransactionResponseDto withdraw(WithdrawDto withdrawDto, Customer customer) {
        List<Wallet> wallets = walletRepository.findByCustomerId(customer.getId());
        Wallet targetWallet = wallets.stream().filter(w -> w.getWalletId().equals(withdrawDto.walletId()))
                .findAny().orElseThrow(() -> new WalletNotFoundException(customer.getTrIdentityNo(), withdrawDto.walletId()));

        if (!targetWallet.isActiveForWithdraw()) {
            throw new WalletNotAvailableException(targetWallet.getWalletId(), "Withdraw");
        }

        if (!targetWallet.isActiveForShopping()) {
            throw new WalletNotAvailableException(targetWallet.getWalletId(), "Shopping");
        }

        if (targetWallet.getUsableBalance().compareTo(withdrawDto.amount()) < 0) {
            throw new InsufficientFundsException("Not enough funds available in the wallet");
        }

        targetWallet.setUsableBalance(targetWallet.getUsableBalance().subtract(withdrawDto.amount()));
        Transaction transaction = TransactionMapper.mapToTransaction(withdrawDto);
        transaction.setWallet(targetWallet);

        if (withdrawDto.amount().compareTo(BigDecimal.valueOf(1000.0)) < 0) {
            targetWallet.setBalance(targetWallet.getBalance().subtract(withdrawDto.amount()));
            transaction.setStatus(Status.APPROVED);
        } else {
            transaction.setStatus(Status.PENDING);
        }

        transactionRepository.save(transaction);
        return TransactionResponseDto.builder()
                .walletId(targetWallet.getWalletId())
                .oppositeParty(transaction.getOppositeParty())
                .oppositePartyType(transaction.getOppositeParty())
                .type(transaction.getType().name())
                .status(transaction.getStatus().name())
                .amount(transaction.getAmount())
                .build();

    }

    @Override
    public WalletTransactionListResponseDto getTransactions(Customer customer, String walletId) {
        List<Wallet> wallets = walletRepository.findByCustomerId(customer.getId());
        Wallet targetWallet = wallets.stream().filter(w -> w.getWalletId().equals(walletId))
                .findAny().orElseThrow(() -> new WalletNotFoundException(customer.getTrIdentityNo(), walletId));

        List<TransactionResponseDto> transactions = transactionRepository.findByWalletId(targetWallet.getId())
                .stream().map(transaction -> TransactionResponseDto.builder()
                        .walletId(transaction.getWallet().getWalletId())
                        .oppositeParty(transaction.getOppositeParty())
                        .oppositePartyType(transaction.getOppositePartyType().name())
                        .type(transaction.getType().name())
                        .status(transaction.getStatus().name())
                        .amount(transaction.getAmount())
                        .build()).toList();

        return WalletTransactionListResponseDto.builder()
                .balance(targetWallet.getBalance())
                .usableBalance(targetWallet.getUsableBalance())
                .walletName(targetWallet.getWalletName())
                .transactions(transactions)
                .build();

    }



}
