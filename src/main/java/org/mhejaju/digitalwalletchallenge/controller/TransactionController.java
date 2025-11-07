package org.mhejaju.digitalwalletchallenge.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.mhejaju.digitalwalletchallenge.dto.DepositDto;
import org.mhejaju.digitalwalletchallenge.dto.TransactionResponseDto;
import org.mhejaju.digitalwalletchallenge.dto.WalletTransactionListResponseDto;
import org.mhejaju.digitalwalletchallenge.dto.WithdrawDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;
import org.mhejaju.digitalwalletchallenge.services.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDto> makeDeposit(@RequestBody @Valid DepositDto depositDto,
                                                              @AuthenticationPrincipal Customer customer) {
        TransactionResponseDto res = transactionService.makeDeposit(depositDto, customer);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDto> withdraw(
            @RequestBody @Valid WithdrawDto withdrawDto,
            @AuthenticationPrincipal Customer customer
            ) {

        TransactionResponseDto res = transactionService.withdraw(withdrawDto, customer);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

    @GetMapping("/transactions")
    public ResponseEntity<WalletTransactionListResponseDto> getTransactions(
            @AuthenticationPrincipal Customer customer,

            @RequestParam
            @NotEmpty(message = "Wallet ID must be provided")
            @NotNull(message = "Wallet ID must be provided") String walletId

    ) {
        WalletTransactionListResponseDto res = transactionService.getTransactions(customer, walletId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }


}
