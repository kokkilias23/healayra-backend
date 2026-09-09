package gr.healayra.backend.service;

import gr.healayra.backend.core.exception.BadRequestException;
import gr.healayra.backend.core.exception.ConflictException;
import gr.healayra.backend.core.exception.ForbiddenException;
import gr.healayra.backend.core.exception.ResourceNotFoundException;
import gr.healayra.backend.dto.appointment.AppointmentCreateDTO;
import gr.healayra.backend.dto.appointment.AppointmentReadOnlyDTO;
import gr.healayra.backend.dto.appointment.AppointmentUpdateStatusDTO;
import gr.healayra.backend.model.Appointment;
import gr.healayra.backend.model.AppointmentStatus;
import gr.healayra.backend.model.Availability;
import gr.healayra.backend.model.Client;
import gr.healayra.backend.model.Doctor;
import gr.healayra.backend.repository.AppointmentRepository;
import gr.healayra.backend.repository.AvailabilityRepository;
import gr.healayra.backend.repository.ClientRepository;
import gr.healayra.backend.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements IAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final ClientRepository clientRepository;
    private final AvailabilityRepository availabilityRepository;

    @Override
    public AppointmentReadOnlyDTO createAppointment(
            AppointmentCreateDTO dto,
            String clientEmail
    ) {

        // Resolve the doctor selected for the appointment.
        Doctor doctor = doctorRepository
                .findByIdAndDeletedFalse(dto.doctorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found"
                        )
                );

        // Resolve the authenticated client's profile from the JWT email.
        Client client = clientRepository
                .findByUserEmailAndDeletedFalse(clientEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Client profile not found"
                        )
                );

        // Find the doctor's configured availability for the selected weekday.
        Availability availability =
                availabilityRepository
                        .findByDoctorIdAndDayOfWeekAndDeletedFalse(
                                dto.doctorId(),
                                dto.appointmentTime().getDayOfWeek()
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Doctor is not available on this day"
                                )
                        );

        // Disabled availability must never accept new appointments.
        if (!availability.isEnabled()) {
            throw new BadRequestException(
                    "Doctor is not available on this day"
            );
        }

        LocalTime appointmentStart =
                dto.appointmentTime().toLocalTime();

        // Calculate the end of the session using the doctor's configured duration.
        LocalTime appointmentEnd =
                appointmentStart.plusMinutes(
                        availability.getSessionDuration()
                );

        // Reject appointments that fall outside the doctor's working hours.
        if (appointmentStart.isBefore(
                availability.getStartTime()
        ) || appointmentEnd.isAfter(
                availability.getEndTime()
        )) {

            throw new BadRequestException(
                    "Appointment time is outside doctor's availability"
            );
        }

        // Calculate the offset from the beginning of the working period.
        long minutesFromAvailabilityStart =
                Duration.between(
                        availability.getStartTime(),
                        appointmentStart
                ).toMinutes();

        // Ensure the requested time starts exactly on a valid session boundary.
        if (minutesFromAvailabilityStart
                % availability.getSessionDuration() != 0) {

            throw new BadRequestException(
                    "Appointment time does not match an available session slot"
            );
        }

        // Prevent the same doctor from being double-booked for the same time slot.
        boolean alreadyBooked =
                appointmentRepository
                        .existsByDoctorIdAndAppointmentTimeAndStatusInAndDeletedFalse(
                                dto.doctorId(),
                                dto.appointmentTime(),
                                List.of(
                                        AppointmentStatus.PENDING,
                                        AppointmentStatus.CONFIRMED
                                )
                        );

        if (alreadyBooked) {
            throw new ConflictException(
                    "Appointment slot already booked"
            );
        }

        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .client(client)
                .appointmentTime(dto.appointmentTime())

                // New appointments start as pending until the doctor confirms them.
                .status(AppointmentStatus.PENDING)
                .build();

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        return mapToReadOnlyDTO(savedAppointment);
    }

    @Override
    public AppointmentReadOnlyDTO getAppointmentById(
            Long id,
            String doctorEmail
    ) {

        Appointment appointment =
                appointmentRepository
                        .findByIdAndDeletedFalse(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Appointment not found"
                                )
                        );

        // A doctor can view only their own appointments.
        validateDoctorOwnership(
                appointment.getDoctor(),
                doctorEmail
        );

        return mapToReadOnlyDTO(appointment);
    }

    @Override
    public List<AppointmentReadOnlyDTO> getAppointmentsByDoctor(
            Long doctorId,
            String doctorEmail
    ) {

        Doctor doctor = doctorRepository
                .findByIdAndDeletedFalse(doctorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found"
                        )
                );

        // Prevent a doctor from requesting another doctor's appointments.
        validateDoctorOwnership(
                doctor,
                doctorEmail
        );

        return appointmentRepository
                .findByDoctorIdAndDeletedFalse(doctorId)
                .stream()
                .map(this::mapToReadOnlyDTO)
                .toList();
    }

    @Override
    public List<AppointmentReadOnlyDTO> getAppointmentsByClient(
            Long clientId,
            String doctorEmail
    ) {

        Doctor doctor = doctorRepository
                .findByUserEmailAndDeletedFalse(doctorEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor profile not found"
                        )
                );

        // Return only this client's appointments that belong to the logged-in doctor.
        return appointmentRepository
                .findByClientIdAndDeletedFalse(clientId)
                .stream()
                .filter(appointment ->
                        appointment.getDoctor()
                                .getId()
                                .equals(doctor.getId())
                )
                .map(this::mapToReadOnlyDTO)
                .toList();
    }

    @Override
    public List<AppointmentReadOnlyDTO> getMyAppointments(
            String clientEmail
    ) {

        Client client = clientRepository
                .findByUserEmailAndDeletedFalse(clientEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Client profile not found"
                        )
                );

        return appointmentRepository
                .findByClientIdAndDeletedFalse(client.getId())
                .stream()
                .map(this::mapToReadOnlyDTO)
                .toList();
    }

    @Override
    public AppointmentReadOnlyDTO updateStatus(
            Long appointmentId,
            AppointmentUpdateStatusDTO dto,
            String doctorEmail
    ) {

        Appointment appointment =
                appointmentRepository
                        .findByIdAndDeletedFalse(appointmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Appointment not found"
                                )
                        );

        // A doctor can change the status only of their own appointments.
        validateDoctorOwnership(
                appointment.getDoctor(),
                doctorEmail
        );

        // Allow only valid business transitions between appointment statuses.
        validateStatusTransition(
                appointment.getStatus(),
                dto.status()
        );

        appointment.setStatus(dto.status());

        Appointment updatedAppointment =
                appointmentRepository.save(appointment);

        return mapToReadOnlyDTO(updatedAppointment);
    }

    private void validateDoctorOwnership(
            Doctor doctor,
            String doctorEmail
    ) {

        if (!doctor.getUser()
                .getEmail()
                .equals(doctorEmail)) {

            throw new ForbiddenException(
                    "You do not have permission to access this appointment"
            );
        }
    }

    private void validateStatusTransition(
            AppointmentStatus currentStatus,
            AppointmentStatus newStatus
    ) {

        boolean validTransition =
                switch (currentStatus) {

                    case PENDING ->
                            newStatus == AppointmentStatus.CONFIRMED
                                    || newStatus == AppointmentStatus.CANCELLED;

                    case CONFIRMED ->
                            newStatus == AppointmentStatus.COMPLETED
                                    || newStatus == AppointmentStatus.CANCELLED;

                    case COMPLETED, CANCELLED ->
                            false;
                };

        if (!validTransition) {
            throw new BadRequestException(
                    "Invalid appointment status transition from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }
    }

    private AppointmentReadOnlyDTO mapToReadOnlyDTO(
            Appointment appointment
    ) {

        return new AppointmentReadOnlyDTO(
                appointment.getId(),
                appointment.getDoctor().getId(),
                appointment.getClient().getId(),
                appointment.getAppointmentTime(),
                appointment.getStatus(),
                appointment.getNotes()
        );
    }
}