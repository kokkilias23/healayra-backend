package gr.healayra.backend.repository;

import gr.healayra.backend.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByVisitId(Long visitId);
}