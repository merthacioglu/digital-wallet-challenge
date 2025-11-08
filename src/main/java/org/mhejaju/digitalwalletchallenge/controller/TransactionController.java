package org.mhejaju.digitalwalletchallenge.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.mhejaju.digitalwalletchallenge.constants.Regex;
import org.mhejaju.digitalwalletchallenge.constants.ValidationMessages;
import org.mhejaju.digitalwalletchallenge.dto.*;
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

    @PostMapping("/admin/deposit")
    public ResponseEntity<TransactionResponseDto> makeDeposit(@RequestBody @Valid DepositDto depositDto,
                                                              @RequestParam
                                                              @Pattern(regexp = Regex.TR_IDENTITY_NO_REGEX, message = ValidationMessages.TR_IDENTITY_NO_FORMAT_ERROR)
                                                              String customerTrIdentityNo) {

        TransactionResponseDto res = transactionService.makeDeposit(depositDto, customerTrIdentityNo);
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

    @PostMapping("/admin/withdraw")
    public ResponseEntity<TransactionResponseDto> withdraw(
            @RequestBody @Valid WithdrawDto withdrawDto,
            @Pattern(regexp = Regex.TR_IDENTITY_NO_REGEX, message = ValidationMessages.TR_IDENTITY_NO_FORMAT_ERROR)
            String customerTrIdentityNo

    ) {

        TransactionResponseDto res = transactionService.withdraw(withdrawDto, customerTrIdentityNo);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

    @GetMapping("/transactions")
    public ResponseEntity<WalletTransactionListResponseDto> getTransactions(
            @AuthenticationPrincipal Customer customer,

            @RequestParam
            @NotEmpty(message = ValidationMessages.WALLET_ID_REQUIRED)
            String walletId

    ) {
        WalletTransactionListResponseDto res = transactionService.getTransactions(customer, walletId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

    @GetMapping("/admin/transactions")
    public ResponseEntity<WalletTransactionListResponseDto> getTransactions(
            @RequestParam
            @Pattern(regexp = Regex.TR_IDENTITY_NO_REGEX, message = ValidationMessages.TR_IDENTITY_NO_FORMAT_ERROR)
            String customerTrIdentityNo,

            @RequestParam
            @NotEmpty(message = ValidationMessages.WALLET_ID_REQUIRED)
            String walletId
    ) {
        WalletTransactionListResponseDto res = transactionService.getTransactions(customerTrIdentityNo, walletId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

    @PostMapping("/changeTransactionStatus")
    public ResponseEntity<ResponseDto> changeTransactionStatus(
            @AuthenticationPrincipal
            Customer customer,

            @RequestBody
            @Valid
            ApproveOrDenyRequestDto requestDto) {

        transactionService.changeTransactionStatus(customer, requestDto);
        ResponseDto res = new ResponseDto(
                HttpStatus.OK.value(),
                "Transaction has successfully been updated"

        );
        return ResponseEntity.status(HttpStatus.OK)
                .body(res);
    }


}
