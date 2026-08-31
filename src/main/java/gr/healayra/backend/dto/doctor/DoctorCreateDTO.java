package gr.healayra.backend.dto.doctor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DoctorCreateDTO(

        @NotNull
        Long userId,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        String specialty,

        String phone

) {
}