package gr.healayra.backend.dto.visit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record VisitCreateDTO(

        @NotNull
        Long doctorId,

        @NotNull
        Long clientId,

        @NotNull
        LocalDateTime visitTime,

        @NotBlank
        String service

) {
}