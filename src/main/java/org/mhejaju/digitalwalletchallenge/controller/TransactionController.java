package org.mhejaju.digitalwalletchallenge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.mhejaju.digitalwalletchallenge.constants.Regex;
import org.mhejaju.digitalwalletchallenge.constants.ValidationMessages;
import org.mhejaju.digitalwalletchallenge.dto.*;
import org.mhejaju.digitalwalletchallenge.entities.Customer;
import org.mhejaju.digitalwalletchallenge.services.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
@Tag(name = "Transaction", description = "Transaction management APIs")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(
            summary = "Make a deposit",
            description = "Deposits funds into the authenticated customer's wallet. Deposits under 1000 are automatically approved, while larger amounts require manual approval."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Deposit transaction created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                    name = "APPROVED Deposit",
                                    value = """
                                {
                                    "walletId": "ebed7406-0593-4e01-bd7b-7f5abee2315f",
                                    "oppositeParty": "TR330006100519786457841326",
                                    "oppositePartyType": "IBAN",
                                    "type": "DEPOSIT",
                                    "status": "APPROVED",
                                    "amount": 500.00
                                }
                                """
                            ),
                                    @ExampleObject(
                                            name = "PENDING Deposit",
                                            value = """
                                {
                                    "walletId": "ebed7406-0593-4e01-bd7b-7f5abee2315f",
                                    "oppositeParty": "TR330006100519786457841326",
                                    "oppositePartyType": "IBAN",
                                    "type": "DEPOSIT",
                                    "status": "PENDING",
                                    "amount": 2500.00
                                }
                                """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid deposit data - validation errors",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    type = "object",
                                    example = """
                                {
                                    "amount": "Amount must be between 0.01 and 10000.00",
                                    "walletId": "Wallet ID is required",
                                    "sourceType": "Source type must be either IBAN or PAYMENT"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token is missing or invalid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "timestamp": "2025-11-09T14:31:25.549614700",
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Full authentication is required to access this resource",
                                    "path": "/api/v1/deposit"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Wallet not found or does not belong to authenticated customer",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "apiPath": "uri=/api/v1/deposit",
                                    "statusCode": 404,
                                    "message": "No wallet with id: 381d7a69-e6d7-401b-8cd5-6c0a394b2d6 found belonging to the user with TR Identity Number: 10195827811",
                                    "timestamp": "2025-11-09T15:45:30"
                                }
                                """
                            )
                    )
            )
    })
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDto> makeDeposit(@RequestBody @Valid DepositDto depositDto,
                                                              @AuthenticationPrincipal Customer customer) {
        TransactionResponseDto res = transactionService.makeDeposit(depositDto, customer);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }


    @Operation(
            summary = "Make a deposit (Admin)",
            description = "Deposits funds into a customer's wallet specified by Turkish Identity Number. Deposits under 1000 are automatically approved, while larger amounts require manual approval. Admin access required."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Deposit transaction created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "APPROVED Deposit",
                                            value = """
                                {
                                    "walletId": "ebed7406-0593-4e01-bd7b-7f5abee2315f",
                                    "oppositeParty": "TR330006100519786457841326",
                                    "oppositePartyType": "IBAN",
                                    "type": "DEPOSIT",
                                    "status": "APPROVED",
                                    "amount": 500.00
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "PENDING Deposit",
                                            value = """
                                {
                                    "walletId": "ebed7406-0593-4e01-bd7b-7f5abee2315f",
                                    "oppositeParty": "TR330006100519786457841326",
                                    "oppositePartyType": "IBAN",
                                    "type": "DEPOSIT",
                                    "status": "PENDING",
                                    "amount": 2500.00
                                }
                                """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid deposit data or customer identity number - validation errors",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    type = "object",
                                    example = """
                                {
                                    "amount": "Amount must be between 0.01 and 10000.00",
                                    "walletId": "Wallet ID is required",
                                    "sourceType": "Source type must be either IBAN or PAYMENT",
                                    "customerTrIdentityNo": "TR Identity Number must contain exactly 11 digits"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token is missing or invalid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "timestamp": "2025-11-09T14:31:25.549614700",
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Full authentication is required to access this resource",
                                    "path": "/api/v1/admin/deposit"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "timestamp": "2025-11-09T15:14:45.237910",
                                    "status": 403,
                                    "error": "Forbidden",
                                    "message": "Access Denied",
                                    "path": "/api/v1/admin/deposit"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer or wallet not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Customer Not Found",
                                            value = """
                                        {
                                            "apiPath": "uri=/api/v1/admin/deposit",
                                            "statusCode": 404,
                                            "message": "No customer found with the TR Identity No: 84627827270",
                                            "timestamp": "2025-11-09T15:12:00"
                                        }
                                        """
                                    ),
                                    @ExampleObject(
                                            name = "Wallet Not Found",
                                            value = """
                                        {
                                            "apiPath": "uri=/api/v1/admin/deposit",
                                            "statusCode": 404,
                                            "message": "No wallet with id: 381d7a69-e6d7-401b-8cd5-6c0a394b2d6 found belonging to the user with TR Identity Number: 10195827811",
                                            "timestamp": "2025-11-09T15:45:30"
                                        }
                                        """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/admin/deposit")
    public ResponseEntity<TransactionResponseDto> makeDeposit(@RequestBody @Valid DepositDto depositDto,
                                                              @RequestParam
                                                              @Pattern(regexp = Regex.TR_IDENTITY_NO_REGEX, message = ValidationMessages.TR_IDENTITY_NO_INVALID)
                                                              String customerTrIdentityNo) {

        TransactionResponseDto res = transactionService.makeDeposit(depositDto, customerTrIdentityNo);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }



    @Operation(
            summary = "Make a withdrawal",
            description = "Withdraws funds from the authenticated customer's wallet. Withdrawals under 1000 are automatically approved and immediately deducted, while larger amounts require manual approval."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Withdrawal transaction created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "APPROVED Withdrawal",
                                            value = """
                                {
                                    "walletId": "ebed7406-0593-4e01-bd7b-7f5abee2315f",
                                    "oppositeParty": "TR330006100519786457841326",
                                    "oppositePartyType": "TR330006100519786457841326",
                                    "type": "WITHDRAW",
                                    "status": "APPROVED",
                                    "amount": 500.00
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "PENDING Withdrawal",
                                            value = """
                                {
                                    "walletId": "ebed7406-0593-4e01-bd7b-7f5abee2315f",
                                    "oppositeParty": "TR330006100519786457841326",
                                    "oppositePartyType": "TR330006100519786457841326",
                                    "type": "WITHDRAW",
                                    "status": "PENDING",
                                    "amount": 2500.00
                                }
                                """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid withdrawal data - validation errors or insufficient funds",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Validation Error",
                                            value = """
                                {
                                    "amount": "Amount must be between 0.01 and 10000.00",
                                    "walletId": "Wallet ID is required",
                                    "targetType": "Target type must be either IBAN or PAYMENT"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Insufficient Funds",
                                            value = """
                                {
                                    "apiPath": "uri=/api/v1/withdraw",
                                    "statusCode": 400,
                                    "message": "Not enough funds available in the wallet",
                                    "timestamp": "2025-11-09T16:20:15"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Wallet Inactive for Withdrawal",
                                            value = """
                                {
                                    "apiPath": "uri=/api/v1/withdraw",
                                    "statusCode": 400,
                                    "message": "Wallet ebed7406-0593-4e01-bd7b-7f5abee2315f is not active for Withdraw",
                                    "timestamp": "2025-11-09T16:22:30"
                                }
                                """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token is missing or invalid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "timestamp": "2025-11-09T14:31:25.549614700",
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Full authentication is required to access this resource",
                                    "path": "/api/v1/withdraw"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Wallet not found or does not belong to authenticated customer",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "apiPath": "uri=/api/v1/withdraw",
                                    "statusCode": 404,
                                    "message": "No wallet with id: 381d7a69-e6d7-401b-8cd5-6c0a394b2d6 found belonging to the user with TR Identity Number: 10195827811",
                                    "timestamp": "2025-11-09T15:45:30"
                                }
                                """
                            )
                    )
            )
    })
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDto> withdraw(
            @RequestBody @Valid WithdrawDto withdrawDto,
            @AuthenticationPrincipal Customer customer
    ) {

        TransactionResponseDto res = transactionService.withdraw(withdrawDto, customer);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }


    @Operation(
            summary = "Make a withdrawal (Admin)",
            description = "Withdraws funds from a customer's wallet specified by Turkish Identity Number. Withdrawals under 1000 are automatically approved and immediately deducted, while larger amounts require manual approval. Admin access required."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Withdrawal transaction created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "APPROVED Withdrawal",
                                            value = """
                                {
                                    "walletId": "ebed7406-0593-4e01-bd7b-7f5abee2315f",
                                    "oppositeParty": "TR330006100519786457841326",
                                    "oppositePartyType": "TR330006100519786457841326",
                                    "type": "WITHDRAW",
                                    "status": "APPROVED",
                                    "amount": 500.00
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "PENDING Withdrawal",
                                            value = """
                                {
                                    "walletId": "ebed7406-0593-4e01-bd7b-7f5abee2315f",
                                    "oppositeParty": "TR330006100519786457841326",
                                    "oppositePartyType": "TR330006100519786457841326",
                                    "type": "WITHDRAW",
                                    "status": "PENDING",
                                    "amount": 2500.00
                                }
                                """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid withdrawal data, customer identity number, or insufficient funds",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Validation Error",
                                            value = """
                                {
                                    "amount": "Amount must be between 0.01 and 10000.00",
                                    "walletId": "Wallet ID is required",
                                    "targetType": "Target type must be either IBAN or PAYMENT",
                                    "customerTrIdentityNo": "TR Identity Number must contain exactly 11 digits"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Insufficient Funds",
                                            value = """
                                {
                                    "apiPath": "uri=/api/v1/admin/withdraw",
                                    "statusCode": 400,
                                    "message": "Not enough funds available in the wallet",
                                    "timestamp": "2025-11-09T16:20:15"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Wallet Inactive for Withdrawal",
                                            value = """
                                {
                                    "apiPath": "uri=/api/v1/admin/withdraw",
                                    "statusCode": 400,
                                    "message": "Wallet ebed7406-0593-4e01-bd7b-7f5abee2315f is not active for Withdraw",
                                    "timestamp": "2025-11-09T16:22:30"
                                }
                                """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token is missing or invalid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "timestamp": "2025-11-09T14:31:25.549614700",
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Full authentication is required to access this resource",
                                    "path": "/api/v1/admin/withdraw"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "timestamp": "2025-11-09T15:14:45.237910",
                                    "status": 403,
                                    "error": "Forbidden",
                                    "message": "Access Denied",
                                    "path": "/api/v1/admin/withdraw"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer or wallet not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Customer Not Found",
                                            value = """
                                {
                                    "apiPath": "uri=/api/v1/admin/withdraw",
                                    "statusCode": 404,
                                    "message": "No customer found with the TR Identity No: 84627827270",
                                    "timestamp": "2025-11-09T15:12:00"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Wallet Not Found",
                                            value = """
                                {
                                    "apiPath": "uri=/api/v1/admin/withdraw",
                                    "statusCode": 404,
                                    "message": "No wallet with id: 381d7a69-e6d7-401b-8cd5-6c0a394b2d6 found belonging to the user with TR Identity Number: 10195827811",
                                    "timestamp": "2025-11-09T15:45:30"
                                }
                                """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/admin/withdraw")
    public ResponseEntity<TransactionResponseDto> withdraw(
            @RequestBody @Valid WithdrawDto withdrawDto,
            @Pattern(regexp = Regex.TR_IDENTITY_NO_REGEX, message = ValidationMessages.TR_IDENTITY_NO_INVALID)
            String customerTrIdentityNo

    ) {

        TransactionResponseDto res = transactionService.withdraw(withdrawDto, customerTrIdentityNo);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }


    @Operation(
            summary = "Get wallet transactions",
            description = "Retrieves all transactions for a specific wallet belonging to the authenticated customer, including balance information."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WalletTransactionListResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Transaction List",
                                    value = """
                                {
                                    "walletName": "Test Wallet 2",
                                    "balance": 1900.00,
                                    "usableBalance": 400.00,
                                    "transactions": [
                                        {
                                            "walletId": "ebed7406-0593-4e01-bd7b-7f5abee2315f",
                                            "oppositeParty": "TR330006100519786457841326",
                                            "oppositePartyType": "IBAN",
                                            "type": "DEPOSIT",
                                            "status": "APPROVED",
                                            "amount": 500.00
                                        },
                                        {
                                            "walletId": "ebed7406-0593-4e01-bd7b-7f5abee2315f",
                                            "oppositeParty": "TR330006100519786457841326",
                                            "oppositePartyType": "IBAN",
                                            "type": "WITHDRAW",
                                            "status": "PENDING",
                                            "amount": 1500.00
                                        }
                                    ]
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid wallet ID - validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "apiPath": "uri=/api/v1/transactions",
                                    "statusCode": 400,
                                    "message": "getTransactions.walletId: Wallet ID is required",
                                    "timestamp": "2025-11-09T17:10:20"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token is missing or invalid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "timestamp": "2025-11-09T14:31:25.549614700",
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Full authentication is required to access this resource",
                                    "path": "/api/v1/transactions"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Wallet not found or does not belong to authenticated customer",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "apiPath": "uri=/api/v1/transactions",
                                    "statusCode": 404,
                                    "message": "No wallet with id: 381d7a69-e6d7-401b-8cd5-6c0a394b2d6 found belonging to the user with TR Identity Number: 10195827811",
                                    "timestamp": "2025-11-09T17:15:45"
                                }
                                """
                            )
                    )
            )
    })
    @GetMapping("/transactions")
    public ResponseEntity<WalletTransactionListResponseDto> getTransactions(
            @AuthenticationPrincipal Customer customer,

            @RequestParam
            @NotEmpty(message = ValidationMessages.WALLET_ID_REQUIRED)
            String walletId

    ) {
        WalletTransactionListResponseDto res = transactionService.getTransactions(customer, walletId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

    @Operation(
            summary = "Get wallet transactions (Admin)",
            description = "Retrieves all transactions for a specific wallet belonging to a customer identified by Turkish Identity Number, including balance information. Admin access required."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WalletTransactionListResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Transaction List",
                                    value = """
                                {
                                    "walletName": "Test Wallet 2",
                                    "balance": 1900.00,
                                    "usableBalance": 400.00,
                                    "transactions": [
                                        {
                                            "walletId": "ebed7406-0593-4e01-bd7b-7f5abee2315f",
                                            "oppositeParty": "TR330006100519786457841326",
                                            "oppositePartyType": "IBAN",
                                            "type": "DEPOSIT",
                                            "status": "APPROVED",
                                            "amount": 500.00
                                        }
                                    ]
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid customer TR Identity Number or wallet ID - validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "apiPath": "uri=/api/v1/admin/transactions",
                                    "statusCode": 400,
                                    "message": "getTransactions.customerTrIdentityNo: TR Identity Number must contain exactly 11 digits",
                                    "timestamp": "2025-11-09T17:10:20"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token is missing or invalid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "timestamp": "2025-11-09T14:31:25.549614700",
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Full authentication is required to access this resource",
                                    "path": "/api/v1/admin/transactions"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "timestamp": "2025-11-09T15:14:45.237910",
                                    "status": 403,
                                    "error": "Forbidden",
                                    "message": "Access Denied",
                                    "path": "/api/v1/admin/transactions"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer or wallet not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Customer Not Found",
                                            value = """
                                {
                                    "apiPath": "uri=/api/v1/admin/transactions",
                                    "statusCode": 404,
                                    "message": "No customer found with the TR Identity No: 84627827270",
                                    "timestamp": "2025-11-09T17:12:00"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Wallet Not Found",
                                            value = """
                                {
                                    "apiPath": "uri=/api/v1/admin/transactions",
                                    "statusCode": 404,
                                    "message": "No wallet with id: 381d7a69-e6d7-401b-8cd5-6c0a394b2d6 found belonging to the user with TR Identity Number: 10195827811",
                                    "timestamp": "2025-11-09T17:15:45"
                                }
                                """
                                    )
                            }
                    )
            )
    })
    @GetMapping("/admin/transactions")
    public ResponseEntity<WalletTransactionListResponseDto> getTransactions(
            @RequestParam
            @Pattern(regexp = Regex.TR_IDENTITY_NO_REGEX, message = ValidationMessages.TR_IDENTITY_NO_INVALID)
            String customerTrIdentityNo,

            @RequestParam
            @NotEmpty(message = ValidationMessages.WALLET_ID_REQUIRED)
            String walletId
    ) {
        WalletTransactionListResponseDto res = transactionService.getTransactions(customerTrIdentityNo, walletId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

    @Operation(
            summary = "Change transaction status",
            description = "Updates the status of a pending transaction (approve or deny). Only affects transactions belonging to the authenticated customer. Automatically adjusts wallet balances based on the decision."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction status updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "statusCode": 200,
                                    "message": "Transaction has successfully been updated"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request - validation errors or transaction already processed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Validation Error",
                                            value = """
                                {
                                    "transactionId": "Transaction ID is required",
                                    "status": "Status must be either APPROVED or DENIED"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Already Approved",
                                            value = """
                                {
                                    "apiPath": "uri=/api/v1/changeTransactionStatus",
                                    "statusCode": 400,
                                    "message": "Transaction is already approved",
                                    "timestamp": "2025-11-09T18:10:20"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Already Denied",
                                            value = """
                                {
                                    "apiPath": "uri=/api/v1/changeTransactionStatus",
                                    "statusCode": 400,
                                    "message": "Transaction is already denied",
                                    "timestamp": "2025-11-09T18:15:30"
                                }
                                """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token is missing or invalid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "timestamp": "2025-11-09T14:31:25.549614700",
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Full authentication is required to access this resource",
                                    "path": "/api/v1/changeTransactionStatus"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found or does not belong to authenticated customer",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "apiPath": "uri=/api/v1/changeTransactionStatus",
                                    "statusCode": 404,
                                    "message": "No transaction with id: 381d7a69-e6d7-401b-8cd5-6c0a394b2d6 found belonging to the user with TR Identity Number: 10195827811",
                                    "timestamp": "2025-11-09T18:20:45"
                                }
                                """
                            )
                    )
            )
    })
    @PostMapping("/changeTransactionStatus")
    public ResponseEntity<ResponseDto> changeTransactionStatus(
            @AuthenticationPrincipal
            Customer customer,

            @RequestBody
            @Valid
            TransactionStatusChangeRequestDto requestDto) {

        transactionService.changeTransactionStatus(customer, requestDto);
        ResponseDto res = new ResponseDto(
                HttpStatus.OK.value(),
                "Transaction has successfully been updated"

        );
        return ResponseEntity.status(HttpStatus.OK)
                .body(res);
    }

    @Operation(
            summary = "Change transaction status (Admin)",
            description = "Updates the status of a pending transaction (approve or deny) for a customer specified by Turkish Identity Number. Automatically adjusts wallet balances based on the decision. Admin access required."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction status updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "statusCode": 200,
                                    "message": "Transaction has successfully been updated"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request - validation errors or transaction already processed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Validation Error",
                                            value = """
                                {
                                    "transactionId": "Transaction ID is required",
                                    "status": "Status must be either APPROVED or DENIED",
                                    "customerTrIdentityNo": "TR Identity Number must contain exactly 11 digits"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Already Approved",
                                            value = """
                                {
                                    "apiPath": "uri=/api/v1/admin/changeTransactionStatus",
                                    "statusCode": 400,
                                    "message": "Transaction is already approved",
                                    "timestamp": "2025-11-09T18:10:20"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Already Denied",
                                            value = """
                                {
                                    "apiPath": "uri=/api/v1/admin/changeTransactionStatus",
                                    "statusCode": 400,
                                    "message": "Transaction is already denied",
                                    "timestamp": "2025-11-09T18:15:30"
                                }
                                """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token is missing or invalid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "timestamp": "2025-11-09T14:31:25.549614700",
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Full authentication is required to access this resource",
                                    "path": "/api/v1/admin/changeTransactionStatus"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "timestamp": "2025-11-09T15:14:45.237910",
                                    "status": 403,
                                    "error": "Forbidden",
                                    "message": "Access Denied",
                                    "path": "/api/v1/admin/changeTransactionStatus"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer or transaction not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Customer Not Found",
                                            value = """
                                {
                                    "apiPath": "uri=/api/v1/admin/changeTransactionStatus",
                                    "statusCode": 404,
                                    "message": "No customer found with the TR Identity No: 84627827270",
                                    "timestamp": "2025-11-09T18:25:00"
                                }
                                """
                                    ),
                                    @ExampleObject(
                                            name = "Transaction Not Found",
                                            value = """
                                {
                                    "apiPath": "uri=/api/v1/admin/changeTransactionStatus",
                                    "statusCode": 404,
                                    "message": "No transaction with id: 381d7a69-e6d7-401b-8cd5-6c0a394b2d6 found belonging to the user with TR Identity Number: 10195827811",
                                    "timestamp": "2025-11-09T18:30:45"
                                }
                                """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/admin/changeTransactionStatus")
    public ResponseEntity<ResponseDto> changeTransactionStatus(
            @RequestParam
            @Pattern(regexp = Regex.TR_IDENTITY_NO_REGEX, message = ValidationMessages.TR_IDENTITY_NO_INVALID)
            String customerTrIdentityNo,

            @RequestBody
            @Valid
            TransactionStatusChangeRequestDto requestDto

    ) {
        transactionService.changeTransactionStatus(customerTrIdentityNo, requestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }


}
