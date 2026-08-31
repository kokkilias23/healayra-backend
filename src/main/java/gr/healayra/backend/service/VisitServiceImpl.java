package gr.healayra.backend.service;

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
    public VisitReadOnlyDTO createVisit(VisitCreateDTO dto) {

        Doctor doctor = doctorRepository.findById(dto.doctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Client client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Visit visit = Visit.builder()
                .doctor(doctor)
                .client(client)
                .visitTime(dto.visitTime())
                .service(dto.service())
                .build();

        Visit savedVisit = visitRepository.save(visit);

        return mapToReadOnlyDTO(savedVisit);
    }

    @Override
    public VisitReadOnlyDTO getVisitById(Long id) {

        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visit not found"));

        return mapToReadOnlyDTO(visit);
    }

    @Override
    public List<VisitReadOnlyDTO> getVisitsByClient(Long clientId) {

        return visitRepository.findByClientId(clientId)
                .stream()
                .map(this::mapToReadOnlyDTO)
                .toList();
    }

    @Override
    public List<VisitReadOnlyDTO> getVisitsByDoctorAndClient(
            Long doctorId,
            Long clientId
    ) {

        return visitRepository.findByDoctorIdAndClientId(
                        doctorId,
                        clientId
                )
                .stream()
                .map(this::mapToReadOnlyDTO)
                .toList();
    }

    @Override
    public void deleteVisit(Long visitId) {

        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found"));

        visitRepository.delete(visit);
    }

    private VisitReadOnlyDTO mapToReadOnlyDTO(Visit visit) {

        return new VisitReadOnlyDTO(
                visit.getId(),
                visit.getDoctor().getId(),
                visit.getClient().getId(),
                visit.getVisitTime(),
                visit.getService()
        );
    }
}