package gr.healayra.backend.dto.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClientCreateDTO(

        @NotNull
        Long userId,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        String phone

) {
}