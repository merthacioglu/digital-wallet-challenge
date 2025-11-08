package org.mhejaju.digitalwalletchallenge.services;


import org.mhejaju.digitalwalletchallenge.dto.*;
import org.mhejaju.digitalwalletchallenge.entities.Customer;

public interface TransactionService {
    TransactionResponseDto makeDeposit(DepositDto depositDto, Customer customer);
    TransactionResponseDto makeDeposit(DepositDto depositDto, String customerTrIdentityNo);
    TransactionResponseDto withdraw(WithdrawDto withdrawDto, Customer customer);
    WalletTransactionListResponseDto getTransactions(Customer customer, String walletId);
    void changeTransactionStatus(Customer customer, ApproveOrDenyRequestDto changeRequest);
}
