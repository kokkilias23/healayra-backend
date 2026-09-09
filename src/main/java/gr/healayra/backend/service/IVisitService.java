package gr.healayra.backend.service;

import gr.healayra.backend.dto.visit.VisitCreateDTO;
import gr.healayra.backend.dto.visit.VisitReadOnlyDTO;

import java.util.List;

public interface IVisitService {

    VisitReadOnlyDTO createVisit(
            VisitCreateDTO dto,
            String doctorEmail
    );

    VisitReadOnlyDTO getVisitById(
            Long id,
            String doctorEmail
    );

    List<VisitReadOnlyDTO> getVisitsByClient(
            Long clientId,
            String doctorEmail
    );

    List<VisitReadOnlyDTO> getVisitsByDoctorAndClient(
            Long doctorId,
            Long clientId,
            String doctorEmail
    );

    void deleteVisit(
            Long visitId,
            String doctorEmail
    );
}