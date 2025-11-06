package org.mhejaju.digitalwalletchallenge.mapper;

import org.mhejaju.digitalwalletchallenge.dto.RegisterDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;
import org.mhejaju.digitalwalletchallenge.entities.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomerMapper {
    public static Customer mapToCustomer(PasswordEncoder encoder, RegisterDto registerDto) {
        Customer customer = new Customer();
        customer.setName(registerDto.name());
        customer.setSurname(registerDto.surname());
        customer.setEmail(registerDto.email());
        customer.setPassword(encoder.encode(registerDto.password()));
        customer.setTrIdentityNo(registerDto.trIdentityNo());
        customer.setRole(Role.BASIC);
        return customer;
    }
}
