package gr.healayra.backend.dto.client;

import jakarta.validation.constraints.NotBlank;

public record ClientUpdateDTO(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        String phone

) {
}