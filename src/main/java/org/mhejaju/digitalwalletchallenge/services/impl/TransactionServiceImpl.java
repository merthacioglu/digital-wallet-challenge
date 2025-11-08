package org.mhejaju.digitalwalletchallenge.services.impl;

import lombok.RequiredArgsConstructor;
import org.mhejaju.digitalwalletchallenge.dto.*;
import org.mhejaju.digitalwalletchallenge.entities.Customer;
import org.mhejaju.digitalwalletchallenge.entities.Transaction;
import org.mhejaju.digitalwalletchallenge.entities.Wallet;
import org.mhejaju.digitalwalletchallenge.entities.enums.TransactionStatus;
import org.mhejaju.digitalwalletchallenge.entities.enums.TransactionType;
import org.mhejaju.digitalwalletchallenge.exceptions.*;
import org.mhejaju.digitalwalletchallenge.mapper.TransactionMapper;
import org.mhejaju.digitalwalletchallenge.repositories.CustomerRepository;
import org.mhejaju.digitalwalletchallenge.repositories.TransactionRepository;
import org.mhejaju.digitalwalletchallenge.repositories.WalletRepository;
import org.mhejaju.digitalwalletchallenge.services.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public TransactionResponseDto makeDeposit(DepositDto depositDto, Customer customer) {
        Optional<Wallet> optionalWallet = walletRepository.findByWalletId(depositDto.walletId());

        if (optionalWallet.isEmpty() || optionalWallet.get().getCustomer().getId() != customer.getId()) {
            throw new WalletNotFoundException(customer.getTrIdentityNo(), depositDto.walletId());
        }
        Wallet targetWallet = optionalWallet.get();

        targetWallet.setBalance(targetWallet.getBalance().add(depositDto.amount()));
        Transaction transaction = TransactionMapper.mapToTransaction(depositDto);
        transaction.setWallet(targetWallet);
        if (depositDto.amount().compareTo(BigDecimal.valueOf(1000.0)) < 0) {
            targetWallet.setUsableBalance(targetWallet.getUsableBalance().add(depositDto.amount()));
            transaction.setStatus(TransactionStatus.APPROVED);
        } else {
            transaction.setStatus(TransactionStatus.PENDING);
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

    @Transactional
    @Override
    public TransactionResponseDto makeDeposit(DepositDto depositDto, String customerTrIdentityNo) {
        Optional<Customer> optionalCustomer = customerRepository.findByTrIdentityNo(customerTrIdentityNo);
        Customer customer = optionalCustomer.orElseThrow(() ->
                new CustomerNotFoundException(customerTrIdentityNo));

        return makeDeposit(depositDto, customer);
    }

    @Override
    @Transactional
    public TransactionResponseDto withdraw(WithdrawDto withdrawDto, Customer customer) {

        Optional<Wallet> optionalWallet = walletRepository.findByWalletId(withdrawDto.walletId());
        if (optionalWallet.isEmpty() || optionalWallet.get().getCustomer().getId() != customer.getId()) {
            throw new WalletNotFoundException(customer.getTrIdentityNo(), withdrawDto.walletId());
        }

        Wallet targetWallet = optionalWallet.get();

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
            transaction.setStatus(TransactionStatus.APPROVED);
        } else {
            transaction.setStatus(TransactionStatus.PENDING);
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
    @Transactional
    public TransactionResponseDto withdraw(WithdrawDto withdrawDto, String customerTrIdentityNo) {
        Optional<Customer> optionalCustomer = customerRepository.findByTrIdentityNo(customerTrIdentityNo);
        Customer customer = optionalCustomer.orElseThrow(() ->
                new CustomerNotFoundException(customerTrIdentityNo));

        return withdraw(withdrawDto, customer);
    }

    @Override
    public WalletTransactionListResponseDto getTransactions(String customerTrIdentityNo, String walletId) {
        Optional<Customer> optionalCustomer = customerRepository.findByTrIdentityNo(customerTrIdentityNo);
        Customer customer = optionalCustomer.orElseThrow(() ->
                new CustomerNotFoundException(customerTrIdentityNo));

        return getTransactions(customer, walletId);
    }

    @Override
    public WalletTransactionListResponseDto getTransactions(Customer customer, String walletId) {
        Optional<Wallet> optionalWallet = walletRepository.findByWalletId(walletId);
        if (optionalWallet.isEmpty() || optionalWallet.get().getCustomer().getId() != customer.getId()) {
            throw new WalletNotFoundException(customer.getTrIdentityNo(), walletId);
        }

        Wallet targetWallet = optionalWallet.get();

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

    @Transactional
    @Override
    public void changeTransactionStatus(String customerTrIdentityNo, TransactionStatusChangeRequestDto changeRequest) {
        Optional<Customer> optionalCustomer = customerRepository.findByTrIdentityNo(customerTrIdentityNo);
        Customer customer = optionalCustomer.orElseThrow(() ->
                new CustomerNotFoundException(customerTrIdentityNo));

        changeTransactionStatus(customer, changeRequest);
    }

    @Transactional
    @Override
    public void changeTransactionStatus(Customer customer, TransactionStatusChangeRequestDto changeRequest) {

        Optional<Transaction> optionalTransaction = transactionRepository.findByTransactionId(changeRequest.transactionId());
        Transaction transaction = optionalTransaction.orElseThrow(() ->
                        new TransactionNotFoundException(customer.getTrIdentityNo(), changeRequest.transactionId()));

        Wallet targetWallet = transaction.getWallet();

        if (targetWallet.getCustomer().getId() != customer.getId()) {
            throw new TransactionNotFoundException(customer.getTrIdentityNo(), changeRequest.transactionId());
        }

        if (transaction.getStatus().equals(TransactionStatus.APPROVED)) {
            throw new RuntimeException("Transaction is already approved");
        }

        if (transaction.getStatus().equals(TransactionStatus.DENIED)) {
            throw new RuntimeException("Transaction is already denied");
        }

        if (changeRequest.status().equals(TransactionStatus.APPROVED.name())) { // if an approve request comes
            if (transaction.getType().equals(TransactionType.DEPOSIT)) {
                // if a deposit is approved then the amount of transaction must be added to the usable balance of the wallet
                targetWallet.setUsableBalance(targetWallet.getUsableBalance().add(transaction.getAmount()));
            } else {
                // if a withdraw is approved then the amount of transaction must be deducted from the balance of the wallet
                targetWallet.setBalance(targetWallet.getBalance().subtract(transaction.getAmount()));
            }
        } else { // if a deny request comes
            if (transaction.getType().equals(TransactionType.DEPOSIT)) {
                // if a deposit is denied then the amount of transaction must be deducted from the balance of the wallet
                targetWallet.setBalance(targetWallet.getBalance().subtract(transaction.getAmount()));
            } else {
                // if a withdraw is denied then the amount of transaction must be added to the usable balance of the wallet
                targetWallet.setUsableBalance(targetWallet.getUsableBalance().add(transaction.getAmount()));
            }
        }

        transaction.setStatus(TransactionStatus.valueOf(changeRequest.status()));
        transactionRepository.save(transaction);
        walletRepository.save(targetWallet);
    }


}
