package gr.healayra.backend.dto.user;

import gr.healayra.backend.model.Role;

import java.time.Instant;

public record UserReadOnlyDTO(

        Long id,

        String email,

        Role role,

        Instant createdAt,

        Instant updatedAt

) {
}