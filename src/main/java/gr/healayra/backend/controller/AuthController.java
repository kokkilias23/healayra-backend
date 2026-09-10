package gr.healayra.backend.controller;

import gr.healayra.backend.authentication.IAuthService;
import gr.healayra.backend.dto.auth.AuthResponseDTO;
import gr.healayra.backend.dto.auth.LoginRequestDTO;
import gr.healayra.backend.dto.auth.RegisterRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "User registration and login endpoints"
)
public class AuthController {

    private final IAuthService authService;

    @Operation(
            summary = "Register a new client account",
            description = """
                    Creates a new user account and the corresponding client profile.
                    Public registration is intended for CLIENT users.
                    On successful registration, the API returns authentication data
                    including a JWT token.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Client account created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid registration data"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "An account with the same email already exists"
            )
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO dto
    ) {

        AuthResponseDTO response = authService.register(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Authenticate user",
            description = """
                    Authenticates an existing user using their credentials.
                    On success, the API returns a JWT token together with
                    basic authenticated-user information.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid login request"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid email or password"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto
    ) {

        AuthResponseDTO response = authService.login(dto);

        return ResponseEntity.ok(response);
    }
}