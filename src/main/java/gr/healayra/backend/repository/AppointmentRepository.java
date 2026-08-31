package gr.healayra.backend.repository;

import gr.healayra.backend.model.Appointment;
import gr.healayra.backend.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByClientId(Long clientId);

    List<Appointment> findByDoctorIdAndStatus(
            Long doctorId,
            AppointmentStatus status
    );

    boolean existsByDoctorIdAndAppointmentTime(
            Long doctorId,
            LocalDateTime appointmentTime
    );
}