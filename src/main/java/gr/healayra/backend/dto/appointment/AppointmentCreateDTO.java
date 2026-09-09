package gr.healayra.backend.dto.appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentCreateDTO(

        @NotNull
        Long doctorId,

        @NotNull
        @Future
        LocalDateTime appointmentTime,

        @NotBlank
        @Size(max = 150)
        String service

) {
}