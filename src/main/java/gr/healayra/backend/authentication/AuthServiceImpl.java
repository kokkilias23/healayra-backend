package gr.healayra.backend.authentication;

import gr.healayra.backend.core.exception.ConflictException;
import gr.healayra.backend.core.exception.ResourceNotFoundException;
import gr.healayra.backend.dto.auth.AuthResponseDTO;
import gr.healayra.backend.dto.auth.LoginRequestDTO;
import gr.healayra.backend.dto.auth.RegisterRequestDTO;
import gr.healayra.backend.model.Client;
import gr.healayra.backend.model.Role;
import gr.healayra.backend.model.User;
import gr.healayra.backend.repository.ClientRepository;
import gr.healayra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO dto) {

        if (userRepository.existsByEmail(dto.email())) {
            throw new ConflictException(
                    "Email already exists"
            );
        }

        User user = User.builder()
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(Role.CLIENT)
                .build();

        User savedUser =
                userRepository.save(user);

        Client client = Client.builder()
                .user(savedUser)
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .phone(dto.phone())
                .build();

        clientRepository.save(client);

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

        User user = userRepository
                .findByEmailAndDeletedFalse(dto.email())
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