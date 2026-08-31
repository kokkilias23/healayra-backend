package gr.healayra.backend.service;

import gr.healayra.backend.dto.note.NoteCreateDTO;
import gr.healayra.backend.dto.note.NoteReadOnlyDTO;
import gr.healayra.backend.dto.note.NoteUpdateDTO;

import java.util.List;

public interface INoteService {

    NoteReadOnlyDTO createNote(NoteCreateDTO dto);

    NoteReadOnlyDTO getNoteById(Long id);

    List<NoteReadOnlyDTO> getNotesByVisit(Long visitId);

    NoteReadOnlyDTO updateNote(
            Long noteId,
            NoteUpdateDTO dto
    );

    void deleteNote(Long noteId);
}