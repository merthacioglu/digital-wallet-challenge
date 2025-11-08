package org.mhejaju.digitalwalletchallenge.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.mhejaju.digitalwalletchallenge.constants.Regex;
import org.mhejaju.digitalwalletchallenge.constants.ResponseMessages;
import org.mhejaju.digitalwalletchallenge.constants.ValidationMessages;
import org.mhejaju.digitalwalletchallenge.dto.ResponseDto;
import org.mhejaju.digitalwalletchallenge.dto.WalletDto;
import org.mhejaju.digitalwalletchallenge.dto.WalletResponseDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;
import org.mhejaju.digitalwalletchallenge.services.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
@Validated
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/addWallet")
    public ResponseEntity<ResponseDto> createWallet(@RequestBody @Valid WalletDto walletDto,
                                                    @AuthenticationPrincipal Customer customer) {
        walletService.addWallet(walletDto, customer);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(HttpStatus.CREATED.value(), ResponseMessages.WALLET_CREATION_SUCCESS_MESSAGE));
    }

    @GetMapping("/listWallets")
    public ResponseEntity<List<WalletResponseDto>> listWallets(@AuthenticationPrincipal Customer customer) {
        List<WalletResponseDto> res = walletService.listWallets(customer);
        return ResponseEntity.status(HttpStatus.OK)
                .body(res);
    }

    @PostMapping("/admin/addWallet")
    public ResponseEntity<ResponseDto> createWallet(
            @RequestBody @Valid WalletDto walletDto,
            @RequestParam
            @Pattern(regexp = Regex.TR_IDENTITY_NO_REGEX, message = ValidationMessages.TR_IDENTITY_NO_FORMAT_ERROR)
            String customerTrIdentityNo
    ) {

        walletService.addWallet(customerTrIdentityNo, walletDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto(HttpStatus.CREATED.value(), ResponseMessages.WALLET_CREATION_SUCCESS_MESSAGE));
    }

    @GetMapping("/admin/listWallets")
    public ResponseEntity<List<WalletResponseDto>> listWallets(
            @RequestParam
            @Pattern(regexp = Regex.TR_IDENTITY_NO_REGEX, message = ValidationMessages.TR_IDENTITY_NO_FORMAT_ERROR)
            String customerTrIdentityNo
    ) {
        List<WalletResponseDto> res = walletService.listWallets(customerTrIdentityNo);
        return ResponseEntity.status(HttpStatus.OK)
                .body(res);
    }



}
