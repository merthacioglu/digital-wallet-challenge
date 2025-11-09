package org.mhejaju.digitalwalletchallenge.services.impl.TransactionService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mhejaju.digitalwalletchallenge.dto.TransactionStatusChangeRequestDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;
import org.mhejaju.digitalwalletchallenge.entities.Transaction;
import org.mhejaju.digitalwalletchallenge.entities.Wallet;
import org.mhejaju.digitalwalletchallenge.entities.enums.TransactionStatus;
import org.mhejaju.digitalwalletchallenge.entities.enums.TransactionType;
import org.mhejaju.digitalwalletchallenge.exceptions.CustomerNotFoundException;
import org.mhejaju.digitalwalletchallenge.exceptions.TransactionNotFoundException;
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
class TransactionServiceChangeTransactionsStatusTests {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    // Test Constants
    private static final String TRANSACTION_ID = "test-transaction-123";
    private static final String WALLET_ID = "test-wallet-123";
    private static final String TR_IDENTITY_NO = "12345678901";
    private static final String CUSTOMER_TR_IDENTITY_NO_2 = "98765432109";
    private static final String CUSTOMER_EMAIL = "john.doe@example.com";
    private static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(1000.00);
    private static final BigDecimal INITIAL_USABLE_BALANCE = BigDecimal.valueOf(500.00);
    private static final BigDecimal TRANSACTION_AMOUNT = BigDecimal.valueOf(300.00);

    private Customer customer;
    private Customer differentCustomer;
    private Wallet wallet;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        // Setup Customer
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John");
        customer.setSurname("Doe");
        customer.setTrIdentityNo(TR_IDENTITY_NO);
        customer.setEmail(CUSTOMER_EMAIL);

        // Setup Different Customer
        differentCustomer = new Customer();
        differentCustomer.setId(2L);
        differentCustomer.setTrIdentityNo(CUSTOMER_TR_IDENTITY_NO_2);

        // Setup Wallet
        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setWalletId(WALLET_ID);
        wallet.setWalletName("Test Wallet");
        wallet.setBalance(INITIAL_BALANCE);
        wallet.setUsableBalance(INITIAL_USABLE_BALANCE);
        wallet.setCustomer(customer);

        // Setup Transaction
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransactionId(TRANSACTION_ID);
        transaction.setAmount(TRANSACTION_AMOUNT);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setWallet(wallet);
    }

    @Test
    @Order(1)
    @DisplayName("Approving pending deposit should add amount to usable balance")
    void testChangeTransactionStatus_whenApprovingPendingDeposit_shouldAddToUsableBalance() {
        // Arrange
        transaction.setType(TransactionType.DEPOSIT);
        TransactionStatusChangeRequestDto request = new TransactionStatusChangeRequestDto(
                TRANSACTION_ID,
                TransactionStatus.APPROVED.name()
        );

        when(transactionRepository.findByTransactionId(TRANSACTION_ID)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal expectedUsableBalance = INITIAL_USABLE_BALANCE.add(TRANSACTION_AMOUNT);

        // Act
        transactionService.changeTransactionStatus(customer, request);

        // Assert
        assertEquals(TransactionStatus.APPROVED, transaction.getStatus());
        assertEquals(expectedUsableBalance, wallet.getUsableBalance());
        assertEquals(INITIAL_BALANCE, wallet.getBalance());

        verify(transactionRepository, times(1)).findByTransactionId(TRANSACTION_ID);
        verify(transactionRepository, times(1)).save(transaction);
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    @Order(2)
    @DisplayName("Approving pending withdrawal should deduct amount from balance")
    void testChangeTransactionStatus_whenApprovingPendingWithdrawal_shouldDeductFromBalance() {
        // Arrange
        transaction.setType(TransactionType.WITHDRAW);
        TransactionStatusChangeRequestDto request = new TransactionStatusChangeRequestDto(
                TRANSACTION_ID,
                TransactionStatus.APPROVED.name()
        );

        when(transactionRepository.findByTransactionId(TRANSACTION_ID)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal expectedBalance = INITIAL_BALANCE.subtract(TRANSACTION_AMOUNT);

        // Act
        transactionService.changeTransactionStatus(customer, request);

        // Assert
        assertEquals(TransactionStatus.APPROVED, transaction.getStatus());
        assertEquals(expectedBalance, wallet.getBalance());
        assertEquals(INITIAL_USABLE_BALANCE, wallet.getUsableBalance());

        verify(transactionRepository, times(1)).save(transaction);
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    @Order(3)
    @DisplayName("Denying pending deposit should deduct amount from balance")
    void testChangeTransactionStatus_whenDenyingPendingDeposit_shouldDeductFromBalance() {
        // Arrange
        transaction.setType(TransactionType.DEPOSIT);
        TransactionStatusChangeRequestDto request = new TransactionStatusChangeRequestDto(
                TRANSACTION_ID,
                TransactionStatus.DENIED.name()
        );

        when(transactionRepository.findByTransactionId(TRANSACTION_ID)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal expectedBalance = INITIAL_BALANCE.subtract(TRANSACTION_AMOUNT);

        // Act
        transactionService.changeTransactionStatus(customer, request);

        // Assert
        assertEquals(TransactionStatus.DENIED, transaction.getStatus());
        assertEquals(expectedBalance, wallet.getBalance());
        assertEquals(INITIAL_USABLE_BALANCE, wallet.getUsableBalance());

        verify(transactionRepository, times(1)).save(transaction);
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    @Order(4)
    @DisplayName("Denying pending withdrawal should add amount to usable balance")
    void testChangeTransactionStatus_whenDenyingPendingWithdrawal_shouldAddToUsableBalance() {
        // Arrange
        transaction.setType(TransactionType.WITHDRAW);
        TransactionStatusChangeRequestDto request = new TransactionStatusChangeRequestDto(
                TRANSACTION_ID,
                TransactionStatus.DENIED.name()
        );

        when(transactionRepository.findByTransactionId(TRANSACTION_ID)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal expectedUsableBalance = INITIAL_USABLE_BALANCE.add(TRANSACTION_AMOUNT);

        // Act
        transactionService.changeTransactionStatus(customer, request);

        // Assert
        assertEquals(TransactionStatus.DENIED, transaction.getStatus());
        assertEquals(INITIAL_BALANCE, wallet.getBalance());
        assertEquals(expectedUsableBalance, wallet.getUsableBalance());

        verify(transactionRepository, times(1)).save(transaction);
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    @Order(5)
    @DisplayName("Should throw exception when transaction not found")
    void testChangeTransactionStatus_whenTransactionNotFound_shouldThrowException() {
        // Arrange
        TransactionStatusChangeRequestDto request = new TransactionStatusChangeRequestDto(
                "non-existent-transaction",
                TransactionStatus.APPROVED.name()
        );

        when(transactionRepository.findByTransactionId("non-existent-transaction")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.changeTransactionStatus(customer, request);
        });

        verify(transactionRepository, times(1)).findByTransactionId("non-existent-transaction");
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    @Order(6)
    @DisplayName("Should throw exception when transaction belongs to different customer")
    void testChangeTransactionStatus_whenTransactionBelongsToDifferentCustomer_shouldThrowException() {
        // Arrange
        wallet.setCustomer(differentCustomer);
        TransactionStatusChangeRequestDto request = new TransactionStatusChangeRequestDto(
                TRANSACTION_ID,
                TransactionStatus.APPROVED.name()
        );

        when(transactionRepository.findByTransactionId(TRANSACTION_ID)).thenReturn(Optional.of(transaction));

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.changeTransactionStatus(customer, request);
        });

        verify(transactionRepository, times(1)).findByTransactionId(TRANSACTION_ID);
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    @Order(7)
    @DisplayName("Should throw exception when transaction is already approved")
    void testChangeTransactionStatus_whenTransactionAlreadyApproved_shouldThrowException() {
        // Arrange
        transaction.setStatus(TransactionStatus.APPROVED);
        TransactionStatusChangeRequestDto request = new TransactionStatusChangeRequestDto(
                TRANSACTION_ID,
                TransactionStatus.DENIED.name()
        );

        when(transactionRepository.findByTransactionId(TRANSACTION_ID)).thenReturn(Optional.of(transaction));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.changeTransactionStatus(customer, request);
        });

        assertEquals("Transaction is already approved", exception.getMessage());

        verify(transactionRepository, times(1)).findByTransactionId(TRANSACTION_ID);
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    @Order(8)
    @DisplayName("Should throw exception when transaction is already denied")
    void testChangeTransactionStatus_whenTransactionAlreadyDenied_shouldThrowException() {
        // Arrange
        transaction.setStatus(TransactionStatus.DENIED);
        TransactionStatusChangeRequestDto request = new TransactionStatusChangeRequestDto(
                TRANSACTION_ID,
                TransactionStatus.APPROVED.name()
        );

        when(transactionRepository.findByTransactionId(TRANSACTION_ID)).thenReturn(Optional.of(transaction));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.changeTransactionStatus(customer, request);
        });

        assertEquals("Transaction is already denied", exception.getMessage());

        verify(transactionRepository, times(1)).findByTransactionId(TRANSACTION_ID);
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    @Order(9)
    @DisplayName("Should work with TR Identity Number when customer exists")
    void testChangeTransactionStatusWithTrIdentity_whenCustomerExists_shouldProcessRequest() {
        // Arrange
        transaction.setType(TransactionType.DEPOSIT);
        TransactionStatusChangeRequestDto request = new TransactionStatusChangeRequestDto(
                TRANSACTION_ID,
                TransactionStatus.APPROVED.name()
        );

        when(customerRepository.findByTrIdentityNo(TR_IDENTITY_NO)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByTransactionId(TRANSACTION_ID)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal expectedUsableBalance = INITIAL_USABLE_BALANCE.add(TRANSACTION_AMOUNT);

        // Act
        transactionService.changeTransactionStatus(TR_IDENTITY_NO, request);

        // Assert
        assertEquals(TransactionStatus.APPROVED, transaction.getStatus());
        assertEquals(expectedUsableBalance, wallet.getUsableBalance());

        verify(customerRepository, times(1)).findByTrIdentityNo(TR_IDENTITY_NO);
        verify(transactionRepository, times(1)).findByTransactionId(TRANSACTION_ID);
        verify(transactionRepository, times(1)).save(transaction);
        verify(walletRepository, times(1)).save(wallet);
    }

    @Test
    @Order(10)
    @DisplayName("Should throw exception with TR Identity Number when customer not found")
    void testChangeTransactionStatusWithTrIdentity_whenCustomerNotFound_shouldThrowException() {
        // Arrange
        TransactionStatusChangeRequestDto request = new TransactionStatusChangeRequestDto(
                TRANSACTION_ID,
                TransactionStatus.APPROVED.name()
        );

        when(customerRepository.findByTrIdentityNo(TR_IDENTITY_NO)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> {
            transactionService.changeTransactionStatus(TR_IDENTITY_NO, request);
        });

        verify(customerRepository, times(1)).findByTrIdentityNo(TR_IDENTITY_NO);
        verifyNoInteractions(transactionRepository);
        verifyNoInteractions(walletRepository);
    }
}