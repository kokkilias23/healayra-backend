package gr.healayra.backend.service;

import gr.healayra.backend.dto.availability.AvailabilityCreateDTO;
import gr.healayra.backend.dto.availability.AvailabilityReadOnlyDTO;
import gr.healayra.backend.dto.availability.AvailabilityUpdateDTO;

import java.util.List;

public interface IAvailabilityService {

    AvailabilityReadOnlyDTO createAvailability(
            AvailabilityCreateDTO dto,
            String doctorEmail
    );

    AvailabilityReadOnlyDTO getAvailabilityById(Long id);

    List<AvailabilityReadOnlyDTO> getAvailabilityByDoctor(Long doctorId);

    AvailabilityReadOnlyDTO updateAvailability(
            Long availabilityId,
            AvailabilityUpdateDTO dto,
            String doctorEmail
    );

    void deleteAvailability(
            Long availabilityId,
            String doctorEmail
    );
}