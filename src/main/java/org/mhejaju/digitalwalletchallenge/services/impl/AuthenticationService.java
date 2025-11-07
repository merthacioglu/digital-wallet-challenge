package org.mhejaju.digitalwalletchallenge.services.impl;

import lombok.RequiredArgsConstructor;
import org.mhejaju.digitalwalletchallenge.dto.AuthenticationDto;
import org.mhejaju.digitalwalletchallenge.dto.LoginDto;
import org.mhejaju.digitalwalletchallenge.dto.RegisterDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;
import org.mhejaju.digitalwalletchallenge.exceptions.ResourceNotFoundException;
import org.mhejaju.digitalwalletchallenge.mapper.CustomerMapper;
import org.mhejaju.digitalwalletchallenge.repositories.CustomerRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationDto register(RegisterDto registerRequest) {

        Customer customer = CustomerMapper.mapToCustomer(passwordEncoder, registerRequest);
        customerRepository.save(customer);
        var jwtToken = jwtService.generateToken(customer);
        var refreshToken = jwtService.generateRefresh(new HashMap<>(), customer);
        return new AuthenticationDto(jwtToken, refreshToken);
    }

    public AuthenticationDto authenticate(LoginDto loginDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password())
        );
        var user = customerRepository.findByEmail(loginDto.email()).orElseThrow(() ->
                new BadCredentialsException("Email or password is incorrect"));

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefresh(new HashMap<>(), user);
        return new AuthenticationDto(jwtToken, refreshToken);
    }

    public AuthenticationDto refreshToken(String refreshToken) {

        var user = customerRepository.findByEmail(jwtService.getEmailFromToken(refreshToken)).orElseThrow(() ->
                new ResourceNotFoundException("JWT Token", "Refresh Token", refreshToken));

        var jwtToken = jwtService.generateToken(user);
        var newRefreshToken = jwtService.generateRefresh(new HashMap<>(), user);
        return new AuthenticationDto(jwtToken, newRefreshToken);
    }

    public Boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }




}
