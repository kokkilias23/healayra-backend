package gr.healayra.backend.dto.user;

import gr.healayra.backend.model.Role;

import java.time.LocalDateTime;

public record UserReadOnlyDTO(

        Long id,

        String email,

        Role role,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}