package gr.healayra.backend.dto.availability;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;

public record AvailabilityUpdateDTO(

        @NotNull
        LocalTime startTime,

        @NotNull
        LocalTime endTime,

        @NotNull
        @Positive
        Integer sessionDuration,

        boolean enabled

) {
}