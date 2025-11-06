package org.mhejaju.digitalwalletchallenge.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mhejaju.digitalwalletchallenge.constants.ResponseMessages;
import org.mhejaju.digitalwalletchallenge.dto.ResponseDto;
import org.mhejaju.digitalwalletchallenge.dto.WalletDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;
import org.mhejaju.digitalwalletchallenge.services.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@RestController
@Validated
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<ResponseDto> createWallet(@RequestBody @Valid WalletDto walletDto,
                                                    @AuthenticationPrincipal Customer customer) {
        walletService.addWallet(walletDto, customer);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(HttpStatus.CREATED.value(), ResponseMessages.WALLET_CREATION_SUCCESS_MESSAGE));
    }


}
