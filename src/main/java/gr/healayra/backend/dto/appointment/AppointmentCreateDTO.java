package gr.healayra.backend.dto.appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentCreateDTO(

        @NotNull
        Long doctorId,

        @NotNull
        @Future
        LocalDateTime appointmentTime

) {
}