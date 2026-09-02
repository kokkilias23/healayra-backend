package gr.healayra.backend.service;

import gr.healayra.backend.core.exception.ConflictException;
import gr.healayra.backend.core.exception.ResourceNotFoundException;
import gr.healayra.backend.dto.appointment.AppointmentCreateDTO;
import gr.healayra.backend.dto.appointment.AppointmentReadOnlyDTO;
import gr.healayra.backend.dto.appointment.AppointmentUpdateStatusDTO;
import gr.healayra.backend.model.Appointment;
import gr.healayra.backend.model.AppointmentStatus;
import gr.healayra.backend.model.Client;
import gr.healayra.backend.model.Doctor;
import gr.healayra.backend.repository.AppointmentRepository;
import gr.healayra.backend.repository.ClientRepository;
import gr.healayra.backend.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements IAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final ClientRepository clientRepository;

    @Override
    public AppointmentReadOnlyDTO createAppointment(
            AppointmentCreateDTO dto,
            String clientEmail
    ) {

        Doctor doctor = doctorRepository.findById(dto.doctorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found"
                        )
                );

        Client client = clientRepository.findByUserEmail(clientEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Client profile not found"
                        )
                );

        boolean alreadyBooked =
                appointmentRepository
                        .existsByDoctorIdAndAppointmentTime(
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
                appointmentRepository.findById(id)
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

        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::mapToReadOnlyDTO)
                .toList();
    }

    @Override
    public List<AppointmentReadOnlyDTO> getAppointmentsByClient(
            Long clientId
    ) {

        return appointmentRepository.findByClientId(clientId)
                .stream()
                .map(this::mapToReadOnlyDTO)
                .toList();
    }

    @Override
    public List<AppointmentReadOnlyDTO> getMyAppointments(
            String clientEmail
    ) {

        Client client = clientRepository.findByUserEmail(clientEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Client profile not found"
                        )
                );

        return appointmentRepository
                .findByClientId(client.getId())
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
                appointmentRepository.findById(appointmentId)
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