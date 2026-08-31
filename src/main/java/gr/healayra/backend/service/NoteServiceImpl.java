package gr.healayra.backend.service;

import gr.healayra.backend.dto.note.NoteCreateDTO;
import gr.healayra.backend.dto.note.NoteReadOnlyDTO;
import gr.healayra.backend.dto.note.NoteUpdateDTO;
import gr.healayra.backend.model.Note;
import gr.healayra.backend.model.Visit;
import gr.healayra.backend.repository.NoteRepository;
import gr.healayra.backend.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements INoteService {

    private final NoteRepository noteRepository;
    private final VisitRepository visitRepository;

    @Override
    public NoteReadOnlyDTO createNote(NoteCreateDTO dto) {

        Visit visit = visitRepository.findById(dto.visitId())
                .orElseThrow(() -> new RuntimeException("Visit not found"));

        Note note = Note.builder()
                .visit(visit)
                .content(dto.content())
                .build();

        Note savedNote = noteRepository.save(note);

        return mapToReadOnlyDTO(savedNote);
    }

    @Override
    public NoteReadOnlyDTO getNoteById(Long id) {

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        return mapToReadOnlyDTO(note);
    }

    @Override
    public List<NoteReadOnlyDTO> getNotesByVisit(Long visitId) {

        return noteRepository.findByVisitId(visitId)
                .stream()
                .map(this::mapToReadOnlyDTO)
                .toList();
    }

    @Override
    public NoteReadOnlyDTO updateNote(
            Long noteId,
            NoteUpdateDTO dto
    ) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        note.setContent(dto.content());

        Note updatedNote = noteRepository.save(note);

        return mapToReadOnlyDTO(updatedNote);
    }

    @Override
    public void deleteNote(Long noteId) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        noteRepository.delete(note);
    }

    private NoteReadOnlyDTO mapToReadOnlyDTO(Note note) {

        return new NoteReadOnlyDTO(
                note.getId(),
                note.getVisit().getId(),
                note.getContent(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}