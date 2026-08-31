package gr.healayra.backend.dto.doctor;

import jakarta.validation.constraints.NotBlank;

public record DoctorUpdateDTO(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        String specialty,

        String phone

) {
}