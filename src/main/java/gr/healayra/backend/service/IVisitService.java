package gr.healayra.backend.service;

import gr.healayra.backend.dto.visit.VisitCreateDTO;
import gr.healayra.backend.dto.visit.VisitReadOnlyDTO;

import java.util.List;

public interface IVisitService {

    VisitReadOnlyDTO createVisit(VisitCreateDTO dto);

    VisitReadOnlyDTO getVisitById(Long id);

    List<VisitReadOnlyDTO> getVisitsByClient(Long clientId);

    List<VisitReadOnlyDTO> getVisitsByDoctorAndClient(
            Long doctorId,
            Long clientId
    );

    void deleteVisit(Long visitId);
}