package gr.healayra.backend.dto.availability;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record AvailabilityCreateDTO(

        @NotNull
        Long doctorId,

        @NotNull
        DayOfWeek dayOfWeek,

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