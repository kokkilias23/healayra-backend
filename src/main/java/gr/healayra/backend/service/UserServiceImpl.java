package gr.healayra.backend.service;

import gr.healayra.backend.core.exception.ResourceNotFoundException;
import gr.healayra.backend.dto.user.UserReadOnlyDTO;
import gr.healayra.backend.model.User;
import gr.healayra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    @Override
    public UserReadOnlyDTO getUserById(Long id) {

        User user = userRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        return mapToReadOnlyDTO(user);
    }

    @Override
    public UserReadOnlyDTO getUserByEmail(String email) {

        User user = userRepository
                .findByEmailAndDeletedFalse(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        return mapToReadOnlyDTO(user);
    }

    private UserReadOnlyDTO mapToReadOnlyDTO(
            User user
    ) {

        return new UserReadOnlyDTO(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}