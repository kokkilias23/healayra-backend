package gr.healayra.backend.repository;

import gr.healayra.backend.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    Optional<Visit> findByIdAndDeletedFalse(Long id);

    List<Visit> findByClientIdAndDeletedFalse(Long clientId);

    List<Visit> findByDoctorIdAndClientIdAndDeletedFalse(
            Long doctorId,
            Long clientId
    );
}