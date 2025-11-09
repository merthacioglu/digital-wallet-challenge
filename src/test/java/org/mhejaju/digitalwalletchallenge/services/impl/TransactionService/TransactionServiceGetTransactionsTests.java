package org.mhejaju.digitalwalletchallenge.services.impl.TransactionService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mhejaju.digitalwalletchallenge.dto.WalletTransactionListResponseDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;
import org.mhejaju.digitalwalletchallenge.entities.Transaction;
import org.mhejaju.digitalwalletchallenge.entities.Wallet;
import org.mhejaju.digitalwalletchallenge.entities.enums.OppositePartyType;
import org.mhejaju.digitalwalletchallenge.entities.enums.TransactionStatus;
import org.mhejaju.digitalwalletchallenge.entities.enums.TransactionType;
import org.mhejaju.digitalwalletchallenge.exceptions.CustomerNotFoundException;
import org.mhejaju.digitalwalletchallenge.exceptions.WalletNotFoundException;
import org.mhejaju.digitalwalletchallenge.repositories.CustomerRepository;
import org.mhejaju.digitalwalletchallenge.repositories.TransactionRepository;
import org.mhejaju.digitalwalletchallenge.repositories.WalletRepository;
import org.mhejaju.digitalwalletchallenge.services.impl.TransactionServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class TransactionServiceGetTransactionsTests {

    private static final String WALLET_ID = "test-wallet-123";
    private static final String TR_IDENTITY_NO = "12345678901";
    private static final String DIFFERENT_TR_IDENTITY_NO = "98765432109";
    private static final String WALLET_NAME = "Test Wallet";
    private static final String IBAN = "TR330006100519786457841326";
    private static final BigDecimal WALLET_BALANCE = BigDecimal.valueOf(2000.00);
    private static final BigDecimal WALLET_USABLE_BALANCE = BigDecimal.valueOf(1500.00);
    private static final Long CUSTOMER_ID = 1L;
    private static final Long DIFFERENT_CUSTOMER_ID = 2L;
    private static final Long WALLET_DB_ID = 1L;

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

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(CUSTOMER_ID);
        customer.setTrIdentityNo(TR_IDENTITY_NO);

        wallet = new Wallet();
        wallet.setId(WALLET_DB_ID);
        wallet.setWalletId(WALLET_ID);
        wallet.setWalletName(WALLET_NAME);
        wallet.setBalance(WALLET_BALANCE);
        wallet.setUsableBalance(WALLET_USABLE_BALANCE);
        wallet.setCustomer(customer);
    }

    @Test
    @Order(1)
    @DisplayName("Should return empty transaction list when wallet has no transactions")
    void testGetTransactions_whenNoTransactionsExist_shouldReturnEmptyList() {
        // Arrange
        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWalletId(WALLET_DB_ID)).thenReturn(Collections.emptyList());

        // Act
        WalletTransactionListResponseDto response = transactionService.getTransactions(customer, WALLET_ID);

        // Assert
        assertNotNull(response);
        assertEquals(WALLET_NAME, response.walletName());
        assertEquals(WALLET_BALANCE, response.balance());
        assertEquals(WALLET_USABLE_BALANCE, response.usableBalance());
        assertTrue(response.transactions().isEmpty());

        verify(walletRepository, times(1)).findByWalletId(WALLET_ID);
        verify(transactionRepository, times(1)).findByWalletId(WALLET_DB_ID);
    }

    @Test
    @Order(2)
    @DisplayName("Should return single transaction when wallet has one transaction")
    void testGetTransactions_whenSingleTransactionExists_shouldReturnSingleTransaction() {
        // Arrange
        Transaction transaction = createTransaction(
                TransactionType.DEPOSIT,
                TransactionStatus.APPROVED,
                BigDecimal.valueOf(500.00),
                OppositePartyType.IBAN
        );
        List<Transaction> transactions = List.of(transaction);

        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWalletId(WALLET_DB_ID)).thenReturn(transactions);

        // Act
        WalletTransactionListResponseDto response = transactionService.getTransactions(customer, WALLET_ID);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.transactions().size());
        assertEquals(WALLET_ID, response.transactions().get(0).walletId());
        assertEquals("DEPOSIT", response.transactions().get(0).type());
        assertEquals("APPROVED", response.transactions().get(0).status());
        assertEquals(BigDecimal.valueOf(500.00), response.transactions().get(0).amount());
        assertEquals("IBAN", response.transactions().get(0).oppositePartyType());

        verify(transactionRepository, times(1)).findByWalletId(WALLET_DB_ID);
    }

    @Test
    @Order(3)
    @DisplayName("Should return multiple transactions when wallet has multiple transactions")
    void testGetTransactions_whenMultipleTransactionsExist_shouldReturnAllTransactions() {
        // Arrange
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(createTransaction(
                TransactionType.DEPOSIT,
                TransactionStatus.APPROVED,
                BigDecimal.valueOf(500.00),
                OppositePartyType.IBAN
        ));
        transactions.add(createTransaction(
                TransactionType.WITHDRAW,
                TransactionStatus.PENDING,
                BigDecimal.valueOf(1500.00),
                OppositePartyType.PAYMENT
        ));
        transactions.add(createTransaction(
                TransactionType.DEPOSIT,
                TransactionStatus.DENIED,
                BigDecimal.valueOf(2000.00),
                OppositePartyType.IBAN
        ));

        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWalletId(WALLET_DB_ID)).thenReturn(transactions);

        // Act
        WalletTransactionListResponseDto response = transactionService.getTransactions(customer, WALLET_ID);

        // Assert
        assertNotNull(response);
        assertEquals(3, response.transactions().size());
        assertEquals("DEPOSIT", response.transactions().get(0).type());
        assertEquals("WITHDRAW", response.transactions().get(1).type());
        assertEquals("PENDING", response.transactions().get(1).status());
        assertEquals("DENIED", response.transactions().get(2).status());

        verify(transactionRepository, times(1)).findByWalletId(WALLET_DB_ID);
    }

    @Test
    @Order(4)
    @DisplayName("Should throw WalletNotFoundException when wallet does not exist")
    void testGetTransactions_whenWalletNotFound_shouldThrowException() {
        // Arrange
        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(WalletNotFoundException.class,
                () -> transactionService.getTransactions(customer, WALLET_ID));

        verify(walletRepository, times(1)).findByWalletId(WALLET_ID);
        verify(transactionRepository, never()).findByWalletId(anyLong());
    }

    @Test
    @Order(5)
    @DisplayName("Should throw WalletNotFoundException when wallet belongs to different customer")
    void testGetTransactions_whenWalletBelongsToDifferentCustomer_shouldThrowException() {
        // Arrange
        Customer differentCustomer = new Customer();
        differentCustomer.setId(DIFFERENT_CUSTOMER_ID);
        differentCustomer.setTrIdentityNo(DIFFERENT_TR_IDENTITY_NO);
        wallet.setCustomer(differentCustomer);

        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));

        // Act & Assert
        assertThrows(WalletNotFoundException.class,
                () -> transactionService.getTransactions(customer, WALLET_ID));

        verify(walletRepository, times(1)).findByWalletId(WALLET_ID);
        verify(transactionRepository, never()).findByWalletId(anyLong());
    }

    @Test
    @Order(6)
    @DisplayName("Should return transactions with correct wallet balance information")
    void testGetTransactions_shouldReturnCorrectBalanceInformation() {
        // Arrange
        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWalletId(WALLET_DB_ID)).thenReturn(Collections.emptyList());

        // Act
        WalletTransactionListResponseDto response = transactionService.getTransactions(customer, WALLET_ID);

        // Assert
        assertEquals(WALLET_BALANCE, response.balance());
        assertEquals(WALLET_USABLE_BALANCE, response.usableBalance());
        assertEquals(WALLET_NAME, response.walletName());
    }

    @Test
    @Order(7)
    @DisplayName("Should correctly map transaction enums to string representations")
    void testGetTransactions_shouldMapEnumsToStrings() {
        // Arrange
        Transaction transaction = createTransaction(
                TransactionType.WITHDRAW,
                TransactionStatus.PENDING,
                BigDecimal.valueOf(750.00),
                OppositePartyType.PAYMENT
        );

        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWalletId(WALLET_DB_ID)).thenReturn(List.of(transaction));

        // Act
        WalletTransactionListResponseDto response = transactionService.getTransactions(customer, WALLET_ID);

        // Assert
        assertEquals("WITHDRAW", response.transactions().get(0).type());
        assertEquals("PENDING", response.transactions().get(0).status());
        assertEquals("PAYMENT", response.transactions().get(0).oppositePartyType());
    }

    @Test
    @Order(8)
    @DisplayName("Should return transactions when valid TR Identity Number is provided")
    void testGetTransactionsWithTrIdentityNo_whenCustomerFound_shouldReturnTransactions() {
        // Arrange
        Transaction transaction = createTransaction(
                TransactionType.DEPOSIT,
                TransactionStatus.APPROVED,
                BigDecimal.valueOf(500.00),
                OppositePartyType.IBAN
        );

        when(customerRepository.findByTrIdentityNo(TR_IDENTITY_NO)).thenReturn(Optional.of(customer));
        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWalletId(WALLET_DB_ID)).thenReturn(List.of(transaction));

        // Act
        WalletTransactionListResponseDto response = transactionService.getTransactions(TR_IDENTITY_NO, WALLET_ID);

        // Assert
        assertNotNull(response);
        assertEquals(WALLET_NAME, response.walletName());
        assertEquals(1, response.transactions().size());

        verify(customerRepository, times(1)).findByTrIdentityNo(TR_IDENTITY_NO);
        verify(walletRepository, times(1)).findByWalletId(WALLET_ID);
        verify(transactionRepository, times(1)).findByWalletId(WALLET_DB_ID);
    }

    @Test
    @Order(9)
    @DisplayName("Should throw CustomerNotFoundException when TR Identity Number is invalid")
    void testGetTransactionsWithTrIdentityNo_whenCustomerNotFound_shouldThrowException() {
        // Arrange
        String invalidTrIdentityNo = "99999999999";
        when(customerRepository.findByTrIdentityNo(invalidTrIdentityNo)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomerNotFoundException.class,
                () -> transactionService.getTransactions(invalidTrIdentityNo, WALLET_ID));

        verify(customerRepository, times(1)).findByTrIdentityNo(invalidTrIdentityNo);
        verify(walletRepository, never()).findByWalletId(anyString());
        verify(transactionRepository, never()).findByWalletId(anyLong());
    }

    private Transaction createTransaction(TransactionType type, TransactionStatus status,
                                           BigDecimal amount, OppositePartyType oppositePartyType) {
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setType(type);
        transaction.setStatus(status);
        transaction.setAmount(amount);
        transaction.setOppositeParty(IBAN);
        transaction.setOppositePartyType(oppositePartyType);
        return transaction;
    }
}