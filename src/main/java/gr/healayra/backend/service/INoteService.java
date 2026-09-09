package gr.healayra.backend.service;

import gr.healayra.backend.dto.note.NoteCreateDTO;
import gr.healayra.backend.dto.note.NoteReadOnlyDTO;
import gr.healayra.backend.dto.note.NoteUpdateDTO;

import java.util.List;

public interface INoteService {

    NoteReadOnlyDTO createNote(
            NoteCreateDTO dto,
            String doctorEmail
    );

    NoteReadOnlyDTO getNoteById(
            Long id,
            String doctorEmail
    );

    List<NoteReadOnlyDTO> getNotesByVisit(
            Long visitId,
            String doctorEmail
    );

    NoteReadOnlyDTO updateNote(
            Long noteId,
            NoteUpdateDTO dto,
            String doctorEmail
    );

    void deleteNote(
            Long noteId,
            String doctorEmail
    );
}