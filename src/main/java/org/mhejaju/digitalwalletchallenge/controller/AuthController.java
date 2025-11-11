package org.mhejaju.digitalwalletchallenge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mhejaju.digitalwalletchallenge.dto.AuthenticationDto;
import org.mhejaju.digitalwalletchallenge.dto.ErrorDto;
import org.mhejaju.digitalwalletchallenge.dto.LoginDto;
import org.mhejaju.digitalwalletchallenge.dto.RegisterDto;
import org.mhejaju.digitalwalletchallenge.services.impl.AuthenticationService;
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
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Register a new customer",
            description = "Creates a new customer account and returns JWT tokens for authentication"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Customer registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid registration data - validation errors",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    type = "object",
                                    example = """
                                {
                                    "name": "Name must be between 2 and 50 characters",
                                    "surname": "Surname must be between 2 and 50 characters",
                                    "trIdentityNo": "TR Identity Number must contain exactly 11 digits",
                                    "email": "Email must be valid",
                                    "password": "Password is required"
                                }
                                """
                            )
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<AuthenticationDto> register(@RequestBody @Valid RegisterDto registerDto) {
        AuthenticationDto res = authenticationService.register(registerDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(res);
    }

    @Operation(
            summary = "Authenticate a customer",
            description = "Authenticates a customer with email and password, returning JWT tokens for subsequent API requests"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful - JWT tokens returned",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationDto.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                        "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid login data - validation errors",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    type = "object",
                                    example = """
                                    {
                                        "email": "Email must be valid",
                                        "password": "Password is required"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed - invalid credentials",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorDto.class,
                                    example = """
                                    {
                                        "timestamp": "2025-11-09T14:31:25.549614700",
                                        "status": 401,
                                        "error": "Unauthorized",
                                        "message": "Email or password is incorrect",
                                        "path": "/api/v1/login"
                                    }
                                    """
                            )
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationDto> login(@RequestBody @Valid LoginDto loginDto) {
        AuthenticationDto response = authenticationService.authenticate(loginDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
