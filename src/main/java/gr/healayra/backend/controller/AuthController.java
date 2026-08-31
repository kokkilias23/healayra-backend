package gr.healayra.backend.controller;

import gr.healayra.backend.dto.auth.AuthResponseDTO;
import gr.healayra.backend.dto.auth.LoginRequestDTO;
import gr.healayra.backend.dto.auth.RegisterRequestDTO;
import gr.healayra.backend.authentication.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO dto
    ) {

        AuthResponseDTO response = authService.register(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto
    ) {

        AuthResponseDTO response = authService.login(dto);

        return ResponseEntity.ok(response);
    }
}