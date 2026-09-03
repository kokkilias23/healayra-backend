package gr.healayra.backend.service;

import gr.healayra.backend.core.exception.BadRequestException;
import gr.healayra.backend.core.exception.ConflictException;
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

        Doctor doctor = doctorRepository
                .findByIdAndDeletedFalse(dto.doctorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found"
                        )
                );

        Client client = clientRepository
                .findByUserEmailAndDeletedFalse(clientEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Client profile not found"
                        )
                );

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

        if (!availability.isEnabled()) {
            throw new BadRequestException(
                    "Doctor is not available on this day"
            );
        }

        LocalTime appointmentStart =
                dto.appointmentTime().toLocalTime();

        LocalTime appointmentEnd =
                appointmentStart.plusMinutes(
                        availability.getSessionDuration()
                );

        if (appointmentStart.isBefore(
                availability.getStartTime()
        ) || appointmentEnd.isAfter(
                availability.getEndTime()
        )) {

            throw new BadRequestException(
                    "Appointment time is outside doctor's availability"
            );
        }

        long minutesFromAvailabilityStart =
                Duration.between(
                        availability.getStartTime(),
                        appointmentStart
                ).toMinutes();

        if (minutesFromAvailabilityStart
                % availability.getSessionDuration() != 0) {

            throw new BadRequestException(
                    "Appointment time does not match an available session slot"
            );
        }

        boolean alreadyBooked =
                appointmentRepository
                        .existsByDoctorIdAndAppointmentTimeAndDeletedFalse(
                                dto.doctorId(),
                                dto.appointmentTime()
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
                .status(AppointmentStatus.PENDING)
                .build();

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        return mapToReadOnlyDTO(savedAppointment);
    }

    @Override
    public AppointmentReadOnlyDTO getAppointmentById(
            Long id
    ) {

        Appointment appointment =
                appointmentRepository
                        .findByIdAndDeletedFalse(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Appointment not found"
                                )
                        );

        return mapToReadOnlyDTO(appointment);
    }

    @Override
    public List<AppointmentReadOnlyDTO> getAppointmentsByDoctor(
            Long doctorId
    ) {

        return appointmentRepository
                .findByDoctorIdAndDeletedFalse(doctorId)
                .stream()
                .map(this::mapToReadOnlyDTO)
                .toList();
    }

    @Override
    public List<AppointmentReadOnlyDTO> getAppointmentsByClient(
            Long clientId
    ) {

        return appointmentRepository
                .findByClientIdAndDeletedFalse(clientId)
                .stream()
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
            AppointmentUpdateStatusDTO dto
    ) {

        Appointment appointment =
                appointmentRepository
                        .findByIdAndDeletedFalse(appointmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Appointment not found"
                                )
                        );

        appointment.setStatus(dto.status());

        Appointment updatedAppointment =
                appointmentRepository.save(appointment);

        return mapToReadOnlyDTO(updatedAppointment);
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