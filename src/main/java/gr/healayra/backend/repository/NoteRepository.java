package gr.healayra.backend.repository;

import gr.healayra.backend.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Optional<Note> findByIdAndDeletedFalse(Long id);

    List<Note> findByVisitIdAndDeletedFalse(Long visitId);
}