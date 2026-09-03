package gr.healayra.backend.repository;

import gr.healayra.backend.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository
        extends JpaRepository<Availability, Long> {

    Optional<Availability> findByIdAndDeletedFalse(Long id);

    List<Availability> findByDoctorIdAndDeletedFalse(Long doctorId);

    Optional<Availability> findByDoctorIdAndDayOfWeekAndDeletedFalse(
            Long doctorId,
            DayOfWeek dayOfWeek
    );
}