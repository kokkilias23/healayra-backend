package gr.healayra.backend.dto.availability;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record AvailabilityReadOnlyDTO(

        Long id,

        Long doctorId,

        DayOfWeek dayOfWeek,

        LocalTime startTime,

        LocalTime endTime,

        Integer sessionDuration,

        boolean enabled

) {
}