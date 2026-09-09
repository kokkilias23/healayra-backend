package gr.healayra.backend.service;

import gr.healayra.backend.core.exception.ForbiddenException;
import gr.healayra.backend.core.exception.ResourceNotFoundException;
import gr.healayra.backend.dto.client.ClientReadOnlyDTO;
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
public class ClientServiceImpl implements IClientService {

    private final ClientRepository clientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public ClientReadOnlyDTO getClientById(
            Long id,
            String doctorEmail
    ) {

        Doctor doctor =
                getDoctorByEmail(doctorEmail);

        Client client = clientRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Client not found"
                        )
                );

        // A doctor can access only clients that have an appointment with them.
        boolean belongsToDoctor =
                appointmentRepository
                        .findByDoctorIdAndDeletedFalse(
                                doctor.getId()
                        )
                        .stream()
                        .anyMatch(appointment ->
                                appointment.getClient()
                                        .getId()
                                        .equals(client.getId())
                        );

        if (!belongsToDoctor) {
            throw new ForbiddenException(
                    "You do not have permission to access this client"
            );
        }

        return mapToReadOnlyDTO(client);
    }

    @Override
    public List<ClientReadOnlyDTO> getAllClients(
            String doctorEmail
    ) {

        Doctor doctor =
                getDoctorByEmail(doctorEmail);

        // Return only clients that have appointments with the logged-in doctor.
        return appointmentRepository
                .findByDoctorIdAndDeletedFalse(
                        doctor.getId()
                )
                .stream()
                .map(appointment ->
                        appointment.getClient()
                )
                .filter(client ->
                        !client.isDeleted()
                )
                .distinct()
                .map(this::mapToReadOnlyDTO)
                .toList();
    }

    @Override
    public List<ClientReadOnlyDTO> searchClients(
            String query,
            String doctorEmail
    ) {

        String normalizedQuery =
                query.toLowerCase();

        // Search only inside the logged-in doctor's own clients.
        return getAllClients(doctorEmail)
                .stream()
                .filter(client ->
                        client.firstName()
                                .toLowerCase()
                                .contains(normalizedQuery)
                                ||
                                client.lastName()
                                        .toLowerCase()
                                        .contains(normalizedQuery)
                )
                .toList();
    }

    private Doctor getDoctorByEmail(
            String doctorEmail
    ) {

        return doctorRepository
                .findByUserEmailAndDeletedFalse(
                        doctorEmail
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor profile not found"
                        )
                );
    }

    private ClientReadOnlyDTO mapToReadOnlyDTO(
            Client client
    ) {

        return new ClientReadOnlyDTO(
                client.getId(),
                client.getUser().getId(),
                client.getFirstName(),
                client.getLastName(),
                client.getPhone()
        );
    }
}