package gr.healayra.backend.dto.auth;

import gr.healayra.backend.model.Role;

public record AuthResponseDTO(

        Long userId,

        String email,

        Role role,

        String token

) {
}