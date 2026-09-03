package gr.healayra.backend.repository;

import gr.healayra.backend.model.Appointment;
import gr.healayra.backend.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByIdAndDeletedFalse(Long id);

    List<Appointment> findByDoctorIdAndDeletedFalse(Long doctorId);

    List<Appointment> findByClientIdAndDeletedFalse(Long clientId);

    List<Appointment> findByDoctorIdAndStatusAndDeletedFalse(
            Long doctorId,
            AppointmentStatus status
    );

    boolean existsByDoctorIdAndAppointmentTimeAndDeletedFalse(
            Long doctorId,
            LocalDateTime appointmentTime
    );
}