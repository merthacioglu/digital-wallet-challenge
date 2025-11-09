package org.mhejaju.digitalwalletchallenge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.mhejaju.digitalwalletchallenge.constants.Regex;
import org.mhejaju.digitalwalletchallenge.constants.ResponseMessages;
import org.mhejaju.digitalwalletchallenge.constants.ValidationMessages;
import org.mhejaju.digitalwalletchallenge.dto.ErrorDto;
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
@Tag(name = "Wallet", description = "Wallet management APIs")
public class WalletController {

    private final WalletService walletService;

    @Operation(
            summary = "Create a new wallet",
            description = "Creates a new wallet for the authenticated customer with the specified configuration"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Wallet created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid wallet data provided - validation errors",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    type = "object",
                                    example = """
                                            {
                                                "walletName": "Wallet name must be between 2 and 50 characters",
                                                "currency": "Currency must be provided"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                            Unauthorized - Authentication failed. Possible reasons:
                            - No JWT token provided
                            - JWT token is invalid or expired
                            - JWT signature verification failed
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Missing JWT Token",
                                            value = """
                                                                                    {
                                                                                        "timestamp": "2025-11-09T14:31:25.549614700",
                                                                                        "status": 401,
                                                                                        "error": "Unauthorized",
                                                                                        "message": "Full authentication is required to access this resource",
                                                                                        "path": "/api/v1/addWallet"
                                                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Invalid JWT Token",
                                            value = """
                                                                                    {
                                                                                        "error": "Invalid JWT Token"
                                                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/addWallet")
    public ResponseEntity<ResponseDto> createWallet(@RequestBody @Valid WalletDto walletDto,
                                                    @AuthenticationPrincipal Customer customer) {
        walletService.addWallet(walletDto, customer);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(HttpStatus.CREATED.value(), ResponseMessages.WALLET_CREATION_SUCCESS_MESSAGE));
    }



    @Operation(
            summary = "Create a new wallet (Admin)",
            description = "Creates a new wallet for a customer specified by Turkish Identity Number. Admin access required."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Wallet created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid wallet data or customer identity number",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    type = "object",
                                    example = """
                    {
                        "walletName": "Wallet name must be between 2 and 50 characters",
                        "currency": "Currency must be provided"
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
                            schema = @Schema(
                                    type = "object",
                                    example = """
                    {
                        "timestamp": "2025-11-09T14:43:42.786558300",
                        "status": 401,
                        "error": "Unauthorized",
                        "message": "Full authentication is required to access this resource",
                        "path": "/api/v1/admin/addWallet"
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
                            schema = @Schema(implementation = ErrorDto.class, example = """
                                    
                                    {
                                        "timestamp": "2025-11-09T15:14:45.237910",
                                        "status": 403,
                                        "error": "Forbidden",
                                        "message": "Access Denied",
                                        "path": "/api/v1/admin/addWallet"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found with provided TR Identity Number",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class, example = """
                                    
                                    {
                                        "apiPath": "uri=/api/v1/admin/addWallet",
                                        "statusCode": 404,
                                        "message": "No customer found with the TR Identity No: 84627827270",
                                        "timestamp": "2025-11-09T15:12:00"
                                    }
                                    """)
                    )
            )
    })
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

    @Operation(
            summary = "List all wallets",
            description = "Retrieves all wallets belonging to the authenticated customer"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Wallets retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = WalletResponseDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token is missing or invalid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    type = "object",
                                    example = """
                    {
                        "timestamp": "2025-11-09T14:43:42.786558300",
                        "status": 401,
                        "error": "Unauthorized",
                        "message": "Full authentication is required to access this resource",
                        "path": "/api/v1/listWallets"
                    }
                    """
                            )
                    )
            )
    })
    @GetMapping("/listWallets")
    public ResponseEntity<List<WalletResponseDto>> listWallets(@AuthenticationPrincipal Customer customer) {
        List<WalletResponseDto> res = walletService.listWallets(customer);
        return ResponseEntity.status(HttpStatus.OK)
                .body(res);
    }



    @Operation(
            summary = "List all wallets for a customer (Admin)",
            description = "Retrieves all wallets belonging to a specific customer identified by their Turkish Identity Number. This endpoint is restricted to administrators."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Wallets retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = WalletResponseDto.class)),
                            examples = @ExampleObject(
                                    name = "Wallet List Example",
                                    value = """
                                        [
                                            {
                                                "walletId": "ebed7406-0593-4e01-bd7b-7f5abee2315f",
                                                "walletName": "Test Wallet 2",
                                                "customer": "John Doe",
                                                "currency": "TRY",
                                                "activeForWithdraw": true,
                                                "activeForShopping": true,
                                                "balance": 1900.00,
                                                "usableBalance": 400.00
                                            }
                                        ]
                                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid TR Identity Number format",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class,
                            example = """
                                    
                                    {
                                        "apiPath": "uri=/api/v1/admin/listWallets",
                                        "statusCode": 400,
                                        "message": "listWallets.customerTrIdentityNo: TR Identity Number must contain exactly 11 digits",
                                        "timestamp": "2025-11-09T15:39:22"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class, example = """
                                    {
                                        "apiPath": "uri=/api/v1/admin/listWallets",
                                        "statusCode": 404,
                                        "message": "No customer found with the TR Identity No: 84627827270",
                                        "timestamp": "2025-11-09T15:33:30"
                                    }
                                    """)
                    )
            )
    })
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
