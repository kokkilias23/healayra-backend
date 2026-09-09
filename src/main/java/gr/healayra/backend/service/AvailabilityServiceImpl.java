package gr.healayra.backend.service;

import gr.healayra.backend.core.exception.BadRequestException;
import gr.healayra.backend.core.exception.ConflictException;
import gr.healayra.backend.core.exception.ForbiddenException;
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
            AvailabilityCreateDTO dto,
            String doctorEmail
    ) {

        Doctor doctor = doctorRepository
                .findByIdAndDeletedFalse(dto.doctorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found"
                        )
                );

        // A doctor can create availability only for their own profile.
        validateOwnership(
                doctor,
                doctorEmail
        );

        // A working period is valid only when the start time is before the end time.
        if (!dto.startTime().isBefore(dto.endTime())) {
            throw new BadRequestException(
                    "Start time must be before end time"
            );
        }

        // Keep only one active availability record per doctor and weekday.
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
            AvailabilityUpdateDTO dto,
            String doctorEmail
    ) {

        Availability availability =
                availabilityRepository
                        .findByIdAndDeletedFalse(availabilityId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Availability not found"
                                )
                        );

        // A doctor can update only their own availability.
        validateOwnership(
                availability.getDoctor(),
                doctorEmail
        );

        // Revalidate the working time range before updating the schedule.
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
            Long availabilityId,
            String doctorEmail
    ) {

        Availability availability =
                availabilityRepository
                        .findByIdAndDeletedFalse(availabilityId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Availability not found"
                                )
                        );

        // A doctor can delete only their own availability.
        validateOwnership(
                availability.getDoctor(),
                doctorEmail
        );

        // Preserve availability history by marking the record as deleted.
        availability.softDelete();

        availabilityRepository.save(availability);
    }

    private void validateOwnership(
            Doctor doctor,
            String doctorEmail
    ) {

        if (!doctor.getUser()
                .getEmail()
                .equals(doctorEmail)) {

            throw new ForbiddenException(
                    "You do not have permission to modify this availability"
            );
        }
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