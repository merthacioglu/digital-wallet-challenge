package org.mhejaju.digitalwalletchallenge.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mhejaju.digitalwalletchallenge.dto.AuthenticationDto;
import org.mhejaju.digitalwalletchallenge.dto.RegisterDto;
import org.mhejaju.digitalwalletchallenge.services.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationDto> register(@RequestBody @Valid RegisterDto registerDto) {
        AuthenticationDto res = authenticationService.register(registerDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }
}
