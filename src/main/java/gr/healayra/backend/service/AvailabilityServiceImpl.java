package gr.healayra.backend.service;

import gr.healayra.backend.core.exception.BadRequestException;
import gr.healayra.backend.core.exception.ConflictException;
import gr.healayra.backend.core.exception.ResourceNotFoundException;
import gr.healayra.backend.dto.availability.AvailabilityCreateDTO;
import gr.healayra.backend.dto.availability.AvailabilityReadOnlyDTO;
import gr.healayra.backend.dto.availability.AvailabilityUpdateDTO;
import gr.healayra.backend.model.Availability;
import gr.healayra.backend.model.Doctor;
import gr.healayra.backend.repository.AvailabilityRepository;
import gr.healayra.backend.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements IAvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public AvailabilityReadOnlyDTO createAvailability(
            AvailabilityCreateDTO dto
    ) {

        Doctor doctor = doctorRepository
                .findByIdAndDeletedFalse(dto.doctorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found"
                        )
                );

        if (!dto.startTime().isBefore(dto.endTime())) {
            throw new BadRequestException(
                    "Start time must be before end time"
            );
        }

        boolean alreadyExists =
                availabilityRepository
                        .findByDoctorIdAndDayOfWeekAndDeletedFalse(
                                dto.doctorId(),
                                dto.dayOfWeek()
                        )
                        .isPresent();

        if (alreadyExists) {
            throw new ConflictException(
                    "Availability already exists for this day"
            );
        }

        Availability availability = Availability.builder()
                .doctor(doctor)
                .dayOfWeek(dto.dayOfWeek())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .sessionDuration(dto.sessionDuration())
                .enabled(dto.enabled())
                .build();

        Availability savedAvailability =
                availabilityRepository.save(availability);

        return mapToReadOnlyDTO(savedAvailability);
    }

    @Override
    public AvailabilityReadOnlyDTO getAvailabilityById(
            Long id
    ) {

        Availability availability =
                availabilityRepository
                        .findByIdAndDeletedFalse(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Availability not found"
                                )
                        );

        return mapToReadOnlyDTO(availability);
    }

    @Override
    public List<AvailabilityReadOnlyDTO> getAvailabilityByDoctor(
            Long doctorId
    ) {

        return availabilityRepository
                .findByDoctorIdAndDeletedFalse(doctorId)
                .stream()
                .map(this::mapToReadOnlyDTO)
                .toList();
    }

    @Override
    public AvailabilityReadOnlyDTO updateAvailability(
            Long availabilityId,
            AvailabilityUpdateDTO dto
    ) {

        Availability availability =
                availabilityRepository
                        .findByIdAndDeletedFalse(availabilityId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Availability not found"
                                )
                        );

        if (!dto.startTime().isBefore(dto.endTime())) {
            throw new BadRequestException(
                    "Start time must be before end time"
            );
        }

        availability.setStartTime(dto.startTime());
        availability.setEndTime(dto.endTime());
        availability.setSessionDuration(dto.sessionDuration());
        availability.setEnabled(dto.enabled());

        Availability updatedAvailability =
                availabilityRepository.save(availability);

        return mapToReadOnlyDTO(updatedAvailability);
    }

    @Override
    public void deleteAvailability(
            Long availabilityId
    ) {

        Availability availability =
                availabilityRepository
                        .findByIdAndDeletedFalse(availabilityId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Availability not found"
                                )
                        );

        availability.softDelete();

        availabilityRepository.save(availability);
    }

    private AvailabilityReadOnlyDTO mapToReadOnlyDTO(
            Availability availability
    ) {

        return new AvailabilityReadOnlyDTO(
                availability.getId(),
                availability.getDoctor().getId(),
                availability.getDayOfWeek(),
                availability.getStartTime(),
                availability.getEndTime(),
                availability.getSessionDuration(),
                availability.isEnabled()
        );
    }
}