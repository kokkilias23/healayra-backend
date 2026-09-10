package gr.healayra.backend.controller;

import gr.healayra.backend.dto.note.NoteCreateDTO;
import gr.healayra.backend.dto.note.NoteReadOnlyDTO;
import gr.healayra.backend.dto.note.NoteUpdateDTO;
import gr.healayra.backend.service.INoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Notes",
        description = "Doctor-only clinical note management endpoints"
)
public class NoteController {

    private final INoteService noteService;

    @Operation(
            summary = "Create a note",
            description = """
                    Creates a new note associated with an existing visit.
                    The authenticated doctor can create notes only for
                    visit records that belong to their own workspace.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Note created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid note data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor is not allowed to create a note for this visit"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Visit not found"
            )
    })
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

    @Operation(
            summary = "Get note by ID",
            description = """
                    Returns a specific note.
                    Access is restricted to the doctor who owns
                    the related visit.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Note returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor cannot access this note"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Note not found"
            )
    })
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

    @Operation(
            summary = "Get notes for a visit",
            description = """
                    Returns all active notes attached to a specific visit.
                    The authenticated doctor must have access to the
                    related visit.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Visit notes returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor cannot access notes for this visit"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Visit not found"
            )
    })
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

    @Operation(
            summary = "Update a note",
            description = """
                    Updates an existing note.
                    A doctor can modify only notes that belong
                    to visits inside their own workspace.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Note updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid note data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor cannot update this note"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Note not found"
            )
    })
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

    @Operation(
            summary = "Delete a note",
            description = """
                    Soft-deletes an existing note.
                    A doctor can delete only notes associated
                    with visits inside their own workspace.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Note deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor cannot delete this note"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Note not found"
            )
    })
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