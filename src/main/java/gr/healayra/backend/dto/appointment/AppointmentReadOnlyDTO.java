package gr.healayra.backend.dto.appointment;

import gr.healayra.backend.model.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentReadOnlyDTO(

        Long id,

        Long doctorId,

        Long clientId,

        LocalDateTime appointmentTime,

        AppointmentStatus status,

        String notes

) {
}