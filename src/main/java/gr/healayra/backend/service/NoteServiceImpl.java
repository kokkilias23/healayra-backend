package gr.healayra.backend.service;

import gr.healayra.backend.core.exception.ForbiddenException;
import gr.healayra.backend.core.exception.ResourceNotFoundException;
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
    public NoteReadOnlyDTO createNote(
            NoteCreateDTO dto,
            String doctorEmail
    ) {

        Visit visit = visitRepository
                .findByIdAndDeletedFalse(dto.visitId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Visit not found"
                        )
                );

        // A doctor can create notes only for their own visits.
        validateVisitOwnership(
                visit,
                doctorEmail
        );

        Note note = Note.builder()
                .visit(visit)
                .content(dto.content())
                .build();

        Note savedNote =
                noteRepository.save(note);

        return mapToReadOnlyDTO(savedNote);
    }

    @Override
    public NoteReadOnlyDTO getNoteById(
            Long id,
            String doctorEmail
    ) {

        Note note = noteRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Note not found"
                        )
                );

        validateVisitOwnership(
                note.getVisit(),
                doctorEmail
        );

        return mapToReadOnlyDTO(note);
    }

    @Override
    public List<NoteReadOnlyDTO> getNotesByVisit(
            Long visitId,
            String doctorEmail
    ) {

        Visit visit = visitRepository
                .findByIdAndDeletedFalse(visitId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Visit not found"
                        )
                );

        validateVisitOwnership(
                visit,
                doctorEmail
        );

        return noteRepository
                .findByVisitIdAndDeletedFalse(visitId)
                .stream()
                .map(this::mapToReadOnlyDTO)
                .toList();
    }

    @Override
    public NoteReadOnlyDTO updateNote(
            Long noteId,
            NoteUpdateDTO dto,
            String doctorEmail
    ) {

        Note note = noteRepository
                .findByIdAndDeletedFalse(noteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Note not found"
                        )
                );

        // A doctor can update only notes from their own visits.
        validateVisitOwnership(
                note.getVisit(),
                doctorEmail
        );

        note.setContent(dto.content());

        Note updatedNote =
                noteRepository.save(note);

        return mapToReadOnlyDTO(updatedNote);
    }

    @Override
    public void deleteNote(
            Long noteId,
            String doctorEmail
    ) {

        Note note = noteRepository
                .findByIdAndDeletedFalse(noteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Note not found"
                        )
                );

        // A doctor can delete only notes from their own visits.
        validateVisitOwnership(
                note.getVisit(),
                doctorEmail
        );

        note.softDelete();

        noteRepository.save(note);
    }

    private void validateVisitOwnership(
            Visit visit,
            String doctorEmail
    ) {

        if (!visit.getDoctor()
                .getUser()
                .getEmail()
                .equals(doctorEmail)) {

            throw new ForbiddenException(
                    "You do not have permission to access this note"
            );
        }
    }

    private NoteReadOnlyDTO mapToReadOnlyDTO(
            Note note
    ) {

        return new NoteReadOnlyDTO(
                note.getId(),
                note.getVisit().getId(),
                note.getContent(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}