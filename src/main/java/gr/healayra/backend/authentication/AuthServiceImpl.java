package gr.healayra.backend.authentication;

import gr.healayra.backend.core.exception.ConflictException;
import gr.healayra.backend.core.exception.ResourceNotFoundException;
import gr.healayra.backend.dto.auth.AuthResponseDTO;
import gr.healayra.backend.dto.auth.LoginRequestDTO;
import gr.healayra.backend.dto.auth.RegisterRequestDTO;
import gr.healayra.backend.model.User;
import gr.healayra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    @Override
    public AuthResponseDTO register(RegisterRequestDTO dto) {

        if (userRepository.existsByEmail(dto.email())) {
            throw new ConflictException(
                    "Email already exists"
            );
        }

        User user = User.builder()
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(dto.role())
                .build();

        User savedUser = userRepository.save(user);

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(
                        savedUser.getEmail()
                );

        String token =
                jwtService.generateToken(userDetails);

        return new AuthResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole(),
                token
        );
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.email(),
                        dto.password()
                )
        );

        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(
                        user.getEmail()
                );

        String token =
                jwtService.generateToken(userDetails);

        return new AuthResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                token
        );
    }
}