package gr.healayra.backend.dto.note;

import java.time.LocalDateTime;

public record NoteReadOnlyDTO(

        Long id,

        Long visitId,

        String content,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}