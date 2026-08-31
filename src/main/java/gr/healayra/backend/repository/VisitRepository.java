package gr.healayra.backend.repository;

import gr.healayra.backend.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findByClientId(Long clientId);

    List<Visit> findByDoctorIdAndClientId(
            Long doctorId,
            Long clientId
    );
}