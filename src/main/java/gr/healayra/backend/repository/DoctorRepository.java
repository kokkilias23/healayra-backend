package gr.healayra.backend.repository;

import gr.healayra.backend.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUserId(Long userId);

    Optional<Doctor> findByIdAndDeletedFalse(Long id);

    Optional<Doctor> findByUserIdAndDeletedFalse(Long userId);

    List<Doctor> findAllByDeletedFalse();
}