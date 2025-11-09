package org.mhejaju.digitalwalletchallenge.services.impl.TransactionService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mhejaju.digitalwalletchallenge.dto.WithdrawDto;
import org.mhejaju.digitalwalletchallenge.dto.TransactionResponseDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;
import org.mhejaju.digitalwalletchallenge.entities.Wallet;
import org.mhejaju.digitalwalletchallenge.entities.Transaction;
import org.mhejaju.digitalwalletchallenge.exceptions.CustomerNotFoundException;
import org.mhejaju.digitalwalletchallenge.exceptions.InsufficientFundsException;
import org.mhejaju.digitalwalletchallenge.exceptions.WalletNotAvailableException;
import org.mhejaju.digitalwalletchallenge.exceptions.WalletNotFoundException;
import org.mhejaju.digitalwalletchallenge.repositories.CustomerRepository;
import org.mhejaju.digitalwalletchallenge.repositories.TransactionRepository;
import org.mhejaju.digitalwalletchallenge.repositories.WalletRepository;
import org.mhejaju.digitalwalletchallenge.services.impl.TransactionServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class TransactionServiceWithdrawTests {

    // Constants
    private static final String TR_IDENTITY_NO = "12345678901";
    private static final String WALLET_ID = "test-wallet-123";
    private static final String NON_EXISTENT_WALLET_ID = "non-existent-wallet";
    private static final String TARGET_IBAN = "TR330006100519786457841326";
    private static final String TARGET_TYPE = "IBAN";
    private static final String WALLET_NAME = "Test Wallet";
    private static final String CUSTOMER_NAME = "John";
    private static final String CUSTOMER_SURNAME = "Doe";
    private static final String CUSTOMER_EMAIL = "john.doe@example.com";

    private static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(1000.00);
    private static final BigDecimal INITIAL_USABLE_BALANCE = BigDecimal.valueOf(500.00);
    private static final BigDecimal AMOUNT_UNDER_1000 = BigDecimal.valueOf(500.00);
    private static final BigDecimal AMOUNT_OVER_1000 = BigDecimal.valueOf(2500.00);
    private static final BigDecimal AMOUNT_EXCEEDING_USABLE = BigDecimal.valueOf(600.00);

    private static final long CUSTOMER_ID = 1L;
    private static final long WALLET_ID_LONG = 1L;
    private static final long DIFFERENT_CUSTOMER_ID = 2L;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Customer customer;
    private Wallet wallet;
    private WithdrawDto withdrawDto;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(CUSTOMER_ID);
        customer.setName(CUSTOMER_NAME);
        customer.setSurname(CUSTOMER_SURNAME);
        customer.setTrIdentityNo(TR_IDENTITY_NO);
        customer.setEmail(CUSTOMER_EMAIL);

        wallet = new Wallet();
        wallet.setId(WALLET_ID_LONG);
        wallet.setWalletId(WALLET_ID);
        wallet.setWalletName(WALLET_NAME);
        wallet.setBalance(INITIAL_BALANCE);
        wallet.setUsableBalance(INITIAL_USABLE_BALANCE);
        wallet.setActiveForWithdraw(true);
        wallet.setActiveForShopping(true);
        wallet.setCustomer(customer);
    }

    @Test
    @Order(1)
    @DisplayName("Withdrawal under 1000 should be automatically approved and deducted from balance")
    void testWithdraw_whenAmountUnder1000_shouldAutoApproveAndUpdateBalance() {
        // Arrange
        withdrawDto = new WithdrawDto(
                AMOUNT_UNDER_1000,
                WALLET_ID,
                TARGET_TYPE,
                TARGET_IBAN
        );

        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal expectedBalance = wallet.getBalance().subtract(AMOUNT_UNDER_1000);
        BigDecimal expectedUsableBalance = wallet.getUsableBalance().subtract(AMOUNT_UNDER_1000);

        // Act
        TransactionResponseDto response = transactionService.withdraw(withdrawDto, customer);

        // Assert
        assertNotNull(response);
        assertEquals(WALLET_ID, response.walletId());
        assertEquals(TARGET_IBAN, response.oppositeParty());
        assertEquals("WITHDRAW", response.type());
        assertEquals("APPROVED", response.status());
        assertEquals(AMOUNT_UNDER_1000, response.amount());

        assertEquals(expectedBalance, wallet.getBalance());
        assertEquals(expectedUsableBalance, wallet.getUsableBalance());

        verify(walletRepository, times(1)).findByWalletId(WALLET_ID);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }


    @Test
    @Order(2)
    @DisplayName("Withdrawal over 1000 should be pending and only deducted from usable balance")
    void testWithdraw_whenAmountOver1000_shouldBePendingAndOnlyUpdateUsableBalance() {
        // Arrange
        wallet.setUsableBalance(BigDecimal.valueOf(3000.00));

        withdrawDto = new WithdrawDto(
                AMOUNT_OVER_1000,
                WALLET_ID,
                TARGET_TYPE,
                TARGET_IBAN
        );

        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal initialBalance = wallet.getBalance();

        // Act
        TransactionResponseDto response = transactionService.withdraw(withdrawDto, customer);

        // Assert
        assertNotNull(response);
        assertEquals("PENDING", response.status());
        assertEquals(initialBalance, wallet.getBalance());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @Order(3)
    @DisplayName("Withdrawal should fail when wallet not found")
    void testWithdraw_whenWalletNotFound_shouldThrowException() {
        // Arrange
        withdrawDto = new WithdrawDto(
                AMOUNT_UNDER_1000,
                NON_EXISTENT_WALLET_ID,
                TARGET_TYPE,
                TARGET_IBAN
        );

        when(walletRepository.findByWalletId(NON_EXISTENT_WALLET_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(WalletNotFoundException.class, () -> {
            transactionService.withdraw(withdrawDto, customer);
        });

        verify(walletRepository, times(1)).findByWalletId(NON_EXISTENT_WALLET_ID);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @Order(4)
    @DisplayName("Withdrawal should fail when wallet belongs to different customer")
    void testWithdraw_whenWalletBelongsToDifferentCustomer_shouldThrowException() {
        // Arrange
        Customer differentCustomer = new Customer();
        differentCustomer.setId(DIFFERENT_CUSTOMER_ID);
        wallet.setCustomer(differentCustomer);

        withdrawDto = new WithdrawDto(
                AMOUNT_UNDER_1000,
                WALLET_ID,
                TARGET_TYPE,
                TARGET_IBAN
        );

        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));

        // Act & Assert
        assertThrows(WalletNotFoundException.class, () -> {
            transactionService.withdraw(withdrawDto, customer);
        });

        verify(walletRepository, times(1)).findByWalletId(WALLET_ID);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @Order(5)
    @DisplayName("Withdrawal should fail when wallet inactive for withdrawal")
    void testWithdraw_whenWalletInactiveForWithdrawal_shouldThrowException() {
        // Arrange
        wallet.setActiveForWithdraw(false);

        withdrawDto = new WithdrawDto(
                AMOUNT_UNDER_1000,
                WALLET_ID,
                TARGET_TYPE,
                TARGET_IBAN
        );

        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));

        // Act & Assert
        assertThrows(WalletNotAvailableException.class, () -> {
            transactionService.withdraw(withdrawDto, customer);
        });

        verify(walletRepository, times(1)).findByWalletId(WALLET_ID);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @Order(6)
    @DisplayName("Withdrawal should fail when wallet inactive for shopping")
    void testWithdraw_whenWalletInactiveForShopping_shouldThrowException() {
        // Arrange
        wallet.setActiveForShopping(false);

        withdrawDto = new WithdrawDto(
                AMOUNT_UNDER_1000,
                WALLET_ID,
                TARGET_TYPE,
                TARGET_IBAN
        );

        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));

        // Act & Assert
        assertThrows(WalletNotAvailableException.class, () -> {
            transactionService.withdraw(withdrawDto, customer);
        });

        verify(walletRepository, times(1)).findByWalletId(WALLET_ID);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @Order(7)
    @DisplayName("Withdrawal should fail when insufficient funds")
    void testWithdraw_whenInsufficientFunds_shouldThrowException() {
        // Arrange
        withdrawDto = new WithdrawDto(
                AMOUNT_EXCEEDING_USABLE,
                WALLET_ID,
                TARGET_TYPE,
                TARGET_IBAN
        );

        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () -> {
            transactionService.withdraw(withdrawDto, customer);
        });

        verify(walletRepository, times(1)).findByWalletId(WALLET_ID);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @Order(8)
    @DisplayName("Withdrawal with TR Identity should work when customer exists")
    void testWithdrawWithTrIdentity_whenCustomerExists_shouldProcessWithdrawal() {
        // Arrange
        withdrawDto = new WithdrawDto(
                AMOUNT_UNDER_1000,
                WALLET_ID,
                TARGET_TYPE,
                TARGET_IBAN
        );

        when(customerRepository.findByTrIdentityNo(TR_IDENTITY_NO)).thenReturn(Optional.of(customer));
        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransactionResponseDto response = transactionService.withdraw(withdrawDto, TR_IDENTITY_NO);

        // Assert
        assertNotNull(response);
        assertEquals("APPROVED", response.status());

        verify(customerRepository, times(1)).findByTrIdentityNo(TR_IDENTITY_NO);
        verify(walletRepository, times(1)).findByWalletId(WALLET_ID);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @Order(9)
    @DisplayName("Withdrawal with TR Identity should fail when customer not found")
    void testWithdrawWithTrIdentity_whenCustomerNotFound_shouldThrowException() {
        // Arrange
        withdrawDto = new WithdrawDto(
                AMOUNT_UNDER_1000,
                WALLET_ID,
                TARGET_TYPE,
                TARGET_IBAN
        );

        when(customerRepository.findByTrIdentityNo(TR_IDENTITY_NO)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> {
            transactionService.withdraw(withdrawDto, TR_IDENTITY_NO);
        });

        verify(customerRepository, times(1)).findByTrIdentityNo(TR_IDENTITY_NO);
        verifyNoInteractions(walletRepository);
        verifyNoInteractions(transactionRepository);
    }
}