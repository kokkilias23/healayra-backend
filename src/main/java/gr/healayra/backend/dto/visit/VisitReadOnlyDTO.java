package gr.healayra.backend.dto.visit;

import java.time.LocalDateTime;

public record VisitReadOnlyDTO(

        Long id,

        Long doctorId,

        Long clientId,

        LocalDateTime visitTime,

        String service

) {
}