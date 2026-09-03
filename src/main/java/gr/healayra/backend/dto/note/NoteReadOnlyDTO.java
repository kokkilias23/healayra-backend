package gr.healayra.backend.dto.note;

import java.time.Instant;

public record NoteReadOnlyDTO(

        Long id,

        Long visitId,

        String content,

        Instant createdAt,

        Instant updatedAt

) {
}