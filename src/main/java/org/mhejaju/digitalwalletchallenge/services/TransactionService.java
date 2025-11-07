package org.mhejaju.digitalwalletchallenge.services;


import org.mhejaju.digitalwalletchallenge.dto.DepositDto;
import org.mhejaju.digitalwalletchallenge.dto.TransactionResponseDto;
import org.mhejaju.digitalwalletchallenge.dto.WalletTransactionListResponseDto;
import org.mhejaju.digitalwalletchallenge.dto.WithdrawDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;

public interface TransactionService {
    TransactionResponseDto makeDeposit(DepositDto depositDto, Customer customer);
    TransactionResponseDto withdraw(WithdrawDto withdrawDto, Customer customer);
    WalletTransactionListResponseDto getTransactions(Customer customer, String walletId);
}
