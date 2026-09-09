package gr.healayra.backend.controller;

import gr.healayra.backend.dto.note.NoteCreateDTO;
import gr.healayra.backend.dto.note.NoteReadOnlyDTO;
import gr.healayra.backend.dto.note.NoteUpdateDTO;
import gr.healayra.backend.service.INoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final INoteService noteService;

    @PostMapping
    public ResponseEntity<NoteReadOnlyDTO> createNote(
            @Valid @RequestBody NoteCreateDTO dto,
            Principal principal
    ) {

        NoteReadOnlyDTO createdNote =
                noteService.createNote(
                        dto,
                        principal.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdNote);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteReadOnlyDTO> getNoteById(
            @PathVariable Long id,
            Principal principal
    ) {

        return ResponseEntity.ok(
                noteService.getNoteById(
                        id,
                        principal.getName()
                )
        );
    }

    @GetMapping("/visit/{visitId}")
    public ResponseEntity<List<NoteReadOnlyDTO>> getNotesByVisit(
            @PathVariable Long visitId,
            Principal principal
    ) {

        return ResponseEntity.ok(
                noteService.getNotesByVisit(
                        visitId,
                        principal.getName()
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteReadOnlyDTO> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteUpdateDTO dto,
            Principal principal
    ) {

        return ResponseEntity.ok(
                noteService.updateNote(
                        id,
                        dto,
                        principal.getName()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long id,
            Principal principal
    ) {

        noteService.deleteNote(
                id,
                principal.getName()
        );

        return ResponseEntity.noContent().build();
    }
}