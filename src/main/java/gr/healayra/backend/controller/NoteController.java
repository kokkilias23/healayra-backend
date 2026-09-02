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

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final INoteService noteService;

    @PostMapping
    public ResponseEntity<NoteReadOnlyDTO> createNote(
            @Valid @RequestBody NoteCreateDTO dto
    ) {

        NoteReadOnlyDTO createdNote =
                noteService.createNote(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdNote);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteReadOnlyDTO> getNoteById(
            @PathVariable Long id
    ) {

        NoteReadOnlyDTO note =
                noteService.getNoteById(id);

        return ResponseEntity.ok(note);
    }

    @GetMapping("/visit/{visitId}")
    public ResponseEntity<List<NoteReadOnlyDTO>> getNotesByVisit(
            @PathVariable Long visitId
    ) {

        List<NoteReadOnlyDTO> notes =
                noteService.getNotesByVisit(visitId);

        return ResponseEntity.ok(notes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteReadOnlyDTO> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteUpdateDTO dto
    ) {

        NoteReadOnlyDTO updatedNote =
                noteService.updateNote(id, dto);

        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long id
    ) {

        noteService.deleteNote(id);

        return ResponseEntity.noContent().build();
    }
}