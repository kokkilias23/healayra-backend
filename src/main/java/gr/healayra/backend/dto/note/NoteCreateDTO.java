package gr.healayra.backend.dto.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NoteCreateDTO(

        @NotNull
        Long visitId,

        @NotBlank
        String content

) {
}