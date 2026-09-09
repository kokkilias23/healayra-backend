package gr.healayra.backend.service;

import gr.healayra.backend.core.exception.ForbiddenException;
import gr.healayra.backend.core.exception.ResourceNotFoundException;
import gr.healayra.backend.dto.visit.VisitCreateDTO;
import gr.healayra.backend.dto.visit.VisitReadOnlyDTO;
import gr.healayra.backend.model.Client;
import gr.healayra.backend.model.Doctor;
import gr.healayra.backend.model.Visit;
import gr.healayra.backend.repository.ClientRepository;
import gr.healayra.backend.repository.DoctorRepository;
import gr.healayra.backend.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements IVisitService {

    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final ClientRepository clientRepository;

    @Override
    public VisitReadOnlyDTO createVisit(
            VisitCreateDTO dto,
            String doctorEmail
    ) {

        Doctor doctor = doctorRepository
                .findByIdAndDeletedFalse(dto.doctorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found"
                        )
                );

        // A doctor can create visits only under their own profile.
        validateDoctorOwnership(
                doctor,
                doctorEmail
        );

        Client client = clientRepository
                .findByIdAndDeletedFalse(dto.clientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Client not found"
                        )
                );

        Visit visit = Visit.builder()
                .doctor(doctor)
                .client(client)
                .visitTime(dto.visitTime())
                .service(dto.service())
                .build();

        Visit savedVisit =
                visitRepository.save(visit);

        return mapToReadOnlyDTO(savedVisit);
    }

    @Override
    public VisitReadOnlyDTO getVisitById(
            Long id,
            String doctorEmail
    ) {

        Visit visit = visitRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Visit not found"
                        )
                );

        // A doctor can view only their own visits.
        validateDoctorOwnership(
                visit.getDoctor(),
                doctorEmail
        );

        return mapToReadOnlyDTO(visit);
    }

    @Override
    public List<VisitReadOnlyDTO> getVisitsByClient(
            Long clientId,
            String doctorEmail
    ) {

        Doctor doctor =
                getDoctorByEmail(doctorEmail);

        return visitRepository
                .findByDoctorIdAndClientIdAndDeletedFalse(
                        doctor.getId(),
                        clientId
                )
                .stream()
                .map(this::mapToReadOnlyDTO)
                .toList();
    }

    @Override
    public List<VisitReadOnlyDTO> getVisitsByDoctorAndClient(
            Long doctorId,
            Long clientId,
            String doctorEmail
    ) {

        Doctor doctor = doctorRepository
                .findByIdAndDeletedFalse(doctorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found"
                        )
                );

        validateDoctorOwnership(
                doctor,
                doctorEmail
        );

        return visitRepository
                .findByDoctorIdAndClientIdAndDeletedFalse(
                        doctorId,
                        clientId
                )
                .stream()
                .map(this::mapToReadOnlyDTO)
                .toList();
    }

    @Override
    public void deleteVisit(
            Long visitId,
            String doctorEmail
    ) {

        Visit visit = visitRepository
                .findByIdAndDeletedFalse(visitId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Visit not found"
                        )
                );

        // A doctor can delete only their own visits.
        validateDoctorOwnership(
                visit.getDoctor(),
                doctorEmail
        );

        visit.softDelete();

        visitRepository.save(visit);
    }
    private Doctor getDoctorByEmail(
            String doctorEmail
    ) {

        return doctorRepository
                .findByUserEmailAndDeletedFalse(doctorEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor profile not found"
                        )
                );
    }

    private void validateDoctorOwnership(
            Doctor doctor,
            String doctorEmail
    ) {

        if (!doctor.getUser()
                .getEmail()
                .equals(doctorEmail)) {

            throw new ForbiddenException(
                    "You do not have permission to access this visit"
            );
        }
    }

    private VisitReadOnlyDTO mapToReadOnlyDTO(
            Visit visit
    ) {

        return new VisitReadOnlyDTO(
                visit.getId(),
                visit.getDoctor().getId(),
                visit.getClient().getId(),
                visit.getVisitTime(),
                visit.getService()
        );
    }
}