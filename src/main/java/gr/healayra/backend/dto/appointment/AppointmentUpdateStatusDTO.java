package gr.healayra.backend.dto.appointment;

import gr.healayra.backend.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record AppointmentUpdateStatusDTO(

        @NotNull
        AppointmentStatus status

) {
}