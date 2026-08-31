package gr.healayra.backend.dto.note;

import jakarta.validation.constraints.NotBlank;

public record NoteUpdateDTO(

        @NotBlank
        String content

) {
}