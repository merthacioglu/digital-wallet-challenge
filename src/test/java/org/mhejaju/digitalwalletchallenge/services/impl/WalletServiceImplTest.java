package org.mhejaju.digitalwalletchallenge.services.impl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mhejaju.digitalwalletchallenge.dto.WalletDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;
import org.mhejaju.digitalwalletchallenge.entities.Wallet;
import org.mhejaju.digitalwalletchallenge.exceptions.CustomerNotFoundException;
import org.mhejaju.digitalwalletchallenge.mapper.WalletMapper;
import org.mhejaju.digitalwalletchallenge.repositories.CustomerRepository;
import org.mhejaju.digitalwalletchallenge.repositories.WalletRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    private Customer customer;
    private WalletDto walletDto;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John");
        customer.setSurname("Doe");
        customer.setTrIdentityNo("12345678901");

        walletDto = new WalletDto(
                "My Wallet",
                "TRY",
                true,
                true
        );
    }

    @DisplayName("Wallet is added to customer's wallet list")
    @Test
    @Order(1)
    void testAddWallet_whenWalletAndCustomerProvided_shouldSaveWalletWithCustomer() {
        // arrange
        Wallet wallet = WalletMapper.mapToWallet(walletDto);
        wallet.setCustomer(customer);
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        // act

        walletService.addWallet(walletDto, customer);

        // assert
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    @DisplayName("Wallet not added because no customer found for the TR identity number")
    @Order(2)
    void testAddWalletWithIdentityNo_whenNoCustomerFoundForTheIdentityNo_shouldThrowException() {
        // assert
        String identityNo = "12345678901";
        when(customerRepository.findByTrIdentityNo(identityNo)).thenThrow(CustomerNotFoundException.class);

        // act & assert

        assertThrows(CustomerNotFoundException.class, () -> {
            walletService.addWallet(identityNo, walletDto);
        }, "CustomerNotFound exception should have been thrown");

        verifyNoInteractions(walletRepository);
    }

    @Test
    @DisplayName("Wallet list not returned because no customer found for the TR identity number")
    @Order(3)
    void testListWalletstWithIdentityNo_whenNoCustomerFoundForTheIdentityNo_shouldThrowException() {
        // assert
        String identityNo = "12345678901";
        when(customerRepository.findByTrIdentityNo(identityNo)).thenThrow(CustomerNotFoundException.class);

        // act & assert

        assertThrows(CustomerNotFoundException.class, () -> {
            walletService.listWallets(identityNo);
        }, "CustomerNotFound exception should have been thrown");

        verifyNoInteractions(walletRepository);
    }


}