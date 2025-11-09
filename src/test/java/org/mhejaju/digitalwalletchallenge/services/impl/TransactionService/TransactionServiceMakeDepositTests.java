package org.mhejaju.digitalwalletchallenge.services.impl.TransactionService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mhejaju.digitalwalletchallenge.dto.DepositDto;
import org.mhejaju.digitalwalletchallenge.dto.TransactionResponseDto;
import org.mhejaju.digitalwalletchallenge.entities.Customer;
import org.mhejaju.digitalwalletchallenge.entities.Transaction;
import org.mhejaju.digitalwalletchallenge.entities.Wallet;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class TransactionServiceMakeDepositTests {

    private static final String WALLET_ID = "test-wallet-123";
    private static final String TR_IDENTITY_NO = "12345678901";
    private static final String IBAN = "TR330006100519786457841326";
    private static final String SOURCE_TYPE_IBAN = "IBAN";
    private static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(1000.00);
    private static final BigDecimal INITIAL_USABLE_BALANCE = BigDecimal.valueOf(500.00);
    private static final BigDecimal THRESHOLD_AMOUNT = BigDecimal.valueOf(1000.00);

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
        customer.setId(1L);
        customer.setTrIdentityNo(TR_IDENTITY_NO);

        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setWalletId(WALLET_ID);
        wallet.setBalance(INITIAL_BALANCE);
        wallet.setUsableBalance(INITIAL_USABLE_BALANCE);
        wallet.setCustomer(customer);
    }

    @Test
    @Order(1)
    @DisplayName("Should auto-approve deposit when amount is less than 1000")
    void testMakeDeposit_whenAmountBelowThreshold_shouldAutoApprove() {
        // Arrange
        BigDecimal depositAmount = BigDecimal.valueOf(500.00);
        DepositDto depositDto = createDepositDto(depositAmount);

        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal expectedBalance = INITIAL_BALANCE.add(depositAmount);
        BigDecimal expectedUsableBalance = INITIAL_USABLE_BALANCE.add(depositAmount);

        // Act
        TransactionResponseDto response = transactionService.makeDeposit(depositDto, customer);

        // Assert
        assertNotNull(response);
        assertEquals(WALLET_ID, response.walletId());
        assertEquals(IBAN, response.oppositeParty());
        assertEquals(SOURCE_TYPE_IBAN, response.oppositePartyType());
        assertEquals("DEPOSIT", response.type());
        assertEquals("APPROVED", response.status());
        assertEquals(depositAmount, response.amount());
        assertEquals(expectedBalance, wallet.getBalance());
        assertEquals(expectedUsableBalance, wallet.getUsableBalance());

        verify(walletRepository, times(1)).findByWalletId(WALLET_ID);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @Order(2)
    @DisplayName("Should set status to PENDING when amount equals 1000")
    void testMakeDeposit_whenAmountEqualsThreshold_shouldBePending() {
        // Arrange
        DepositDto depositDto = createDepositDto(THRESHOLD_AMOUNT);

        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal expectedBalance = INITIAL_BALANCE.add(THRESHOLD_AMOUNT);

        // Act
        TransactionResponseDto response = transactionService.makeDeposit(depositDto, customer);

        // Assert
        assertEquals("PENDING", response.status());
        assertEquals(expectedBalance, wallet.getBalance());
        assertEquals(INITIAL_USABLE_BALANCE, wallet.getUsableBalance());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @Order(3)
    @DisplayName("Should set status to PENDING when amount exceeds 1000")
    void testMakeDeposit_whenAmountAboveThreshold_shouldBePending() {
        // Arrange
        BigDecimal depositAmount = BigDecimal.valueOf(2500.00);
        DepositDto depositDto = createDepositDto(depositAmount);

        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransactionResponseDto response = transactionService.makeDeposit(depositDto, customer);

        // Assert
        assertEquals("PENDING", response.status());
        assertEquals(INITIAL_USABLE_BALANCE, wallet.getUsableBalance());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @Order(4)
    @DisplayName("Should throw WalletNotFoundException when wallet does not exist")
    void testMakeDeposit_whenWalletNotFound_shouldThrowException() {
        // Arrange
        DepositDto depositDto = createDepositDto(BigDecimal.valueOf(500.00));

        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.empty());

        // Act & Assert
        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class,
                () -> transactionService.makeDeposit(depositDto, customer));

        verify(walletRepository, times(1)).findByWalletId(WALLET_ID);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @Order(5)
    @DisplayName("Should throw WalletNotFoundException when wallet belongs to different customer")
    void testMakeDeposit_whenWalletBelongsToDifferentCustomer_shouldThrowException() {
        // Arrange
        Customer differentCustomer = new Customer();
        differentCustomer.setId(2L);
        differentCustomer.setTrIdentityNo("98765432109");
        wallet.setCustomer(differentCustomer);

        DepositDto depositDto = createDepositDto(BigDecimal.valueOf(500.00));

        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));

        // Act & Assert
        assertThrows(WalletNotFoundException.class,
                () -> transactionService.makeDeposit(depositDto, customer));

        verify(walletRepository, times(1)).findByWalletId(WALLET_ID);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @Order(6)
    @DisplayName("Should correctly update balance for minimum deposit amount")
    void testMakeDeposit_withMinimumAmount_shouldUpdateBalances() {
        // Arrange
        BigDecimal minAmount = BigDecimal.valueOf(0.01);
        DepositDto depositDto = createDepositDto(minAmount);

        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal expectedBalance = INITIAL_BALANCE.add(minAmount);
        BigDecimal expectedUsableBalance = INITIAL_USABLE_BALANCE.add(minAmount);

        // Act
        TransactionResponseDto response = transactionService.makeDeposit(depositDto, customer);

        // Assert
        assertEquals("APPROVED", response.status());
        assertEquals(expectedBalance, wallet.getBalance());
        assertEquals(expectedUsableBalance, wallet.getUsableBalance());
    }

    @Test
    @Order(7)
    @DisplayName("Should correctly handle deposit just below threshold (999.99)")
    void testMakeDeposit_justBelowThreshold_shouldAutoApprove() {
        // Arrange
        BigDecimal amountJustBelow = BigDecimal.valueOf(999.99);
        DepositDto depositDto = createDepositDto(amountJustBelow);

        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransactionResponseDto response = transactionService.makeDeposit(depositDto, customer);

        // Assert
        assertEquals("APPROVED", response.status());
        assertEquals(INITIAL_USABLE_BALANCE.add(amountJustBelow), wallet.getUsableBalance());
    }

    @Test
    @Order(8)
    @DisplayName("Should auto-approve deposit when valid TR Identity Number is provided")
    void testMakeDepositWithTrIdentityNo_whenCustomerFound_shouldAutoApprove() {
        // Arrange
        BigDecimal depositAmount = BigDecimal.valueOf(500.00);
        DepositDto depositDto = createDepositDto(depositAmount);

        when(customerRepository.findByTrIdentityNo(TR_IDENTITY_NO)).thenReturn(Optional.of(customer));
        when(walletRepository.findByWalletId(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BigDecimal expectedBalance = INITIAL_BALANCE.add(depositAmount);
        BigDecimal expectedUsableBalance = INITIAL_USABLE_BALANCE.add(depositAmount);

        // Act
        TransactionResponseDto response = transactionService.makeDeposit(depositDto, TR_IDENTITY_NO);

        // Assert
        assertNotNull(response);
        assertEquals(WALLET_ID, response.walletId());
        assertEquals("APPROVED", response.status());
        assertEquals(depositAmount, response.amount());
        assertEquals(expectedBalance, wallet.getBalance());
        assertEquals(expectedUsableBalance, wallet.getUsableBalance());

        verify(customerRepository, times(1)).findByTrIdentityNo(TR_IDENTITY_NO);
        verify(walletRepository, times(1)).findByWalletId(WALLET_ID);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @Order(9)
    @DisplayName("Should throw CustomerNotFoundException when TR Identity Number is invalid")
    void testMakeDepositWithTrIdentityNo_whenCustomerNotFound_shouldThrowException() {
        // Arrange
        String invalidTrIdentityNo = "99999999999";
        DepositDto depositDto = createDepositDto(BigDecimal.valueOf(500.00));

        when(customerRepository.findByTrIdentityNo(invalidTrIdentityNo)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomerNotFoundException.class,
                () -> transactionService.makeDeposit(depositDto, invalidTrIdentityNo));

        verify(customerRepository, times(1)).findByTrIdentityNo(invalidTrIdentityNo);
        verify(walletRepository, never()).findByWalletId(anyString());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    private DepositDto createDepositDto(BigDecimal amount) {
        return new DepositDto(amount, WALLET_ID, SOURCE_TYPE_IBAN, IBAN);
    }
}