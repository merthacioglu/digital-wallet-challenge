package org.mhejaju.digitalwalletchallenge.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mhejaju.digitalwalletchallenge.dto.DepositDto;
import org.mhejaju.digitalwalletchallenge.dto.TransactionResponseDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;
import org.mhejaju.digitalwalletchallenge.services.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
