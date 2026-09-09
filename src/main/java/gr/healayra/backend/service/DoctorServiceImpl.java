package gr.healayra.backend.service;

import gr.healayra.backend.core.exception.ConflictException;
import gr.healayra.backend.core.exception.ForbiddenException;
import gr.healayra.backend.core.exception.ResourceNotFoundException;
import gr.healayra.backend.dto.doctor.DoctorCreateDTO;
import gr.healayra.backend.dto.doctor.DoctorReadOnlyDTO;
import gr.healayra.backend.dto.doctor.DoctorUpdateDTO;
import gr.healayra.backend.model.Doctor;
import gr.healayra.backend.model.User;
import gr.healayra.backend.repository.DoctorRepository;
import gr.healayra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements IDoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Override
    public DoctorReadOnlyDTO createDoctor(
            DoctorCreateDTO dto,
            String doctorEmail
    ) {

        User user = userRepository
                .findByIdAndDeletedFalse(dto.userId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        // A doctor can create only their own doctor profile.
        if (!user.getEmail().equals(doctorEmail)) {
            throw new ForbiddenException(
                    "You cannot create a doctor profile for another user"
            );
        }

        // Prevent multiple doctor profiles from being linked to the same user account.
        boolean doctorAlreadyExists =
                doctorRepository
                        .findByUserId(dto.userId())
                        .isPresent();

        if (doctorAlreadyExists) {
            throw new ConflictException(
                    "Doctor profile already exists for this user"
            );
        }

        Doctor doctor = Doctor.builder()
                .user(user)
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .specialty(dto.specialty())
                .phone(dto.phone())
                .build();

        Doctor savedDoctor =
                doctorRepository.save(doctor);

        return mapToReadOnlyDTO(savedDoctor);
    }

    @Override
    public DoctorReadOnlyDTO getDoctorById(Long id) {

        Doctor doctor = doctorRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found"
                        )
                );

        return mapToReadOnlyDTO(doctor);
    }

    @Override
    public DoctorReadOnlyDTO getDoctorByUserId(
            Long userId
    ) {

        Doctor doctor = doctorRepository
                .findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found"
                        )
                );

        return mapToReadOnlyDTO(doctor);
    }

    @Override
    public List<DoctorReadOnlyDTO> getAllDoctors() {

        return doctorRepository
                .findAllByDeletedFalse()
                .stream()
                .map(this::mapToReadOnlyDTO)
                .toList();
    }

    @Override
    public DoctorReadOnlyDTO updateDoctor(
            Long doctorId,
            DoctorUpdateDTO dto,
            String doctorEmail
    ) {

        Doctor doctor = doctorRepository
                .findByIdAndDeletedFalse(doctorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found"
                        )
                );

        // A doctor can update only their own profile.
        validateOwnership(
                doctor,
                doctorEmail
        );

        doctor.setFirstName(dto.firstName());
        doctor.setLastName(dto.lastName());
        doctor.setSpecialty(dto.specialty());
        doctor.setPhone(dto.phone());

        Doctor updatedDoctor =
                doctorRepository.save(doctor);

        return mapToReadOnlyDTO(updatedDoctor);
    }

    @Override
    @Transactional
    public void deleteDoctor(
            Long doctorId,
            String doctorEmail
    ) {

        Doctor doctor = doctorRepository
                .findByIdAndDeletedFalse(doctorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found"
                        )
                );

        // A doctor can delete only their own profile.
        validateOwnership(
                doctor,
                doctorEmail
        );

        User user = doctor.getUser();

        // Disable both the doctor profile and its linked authentication account.
        doctor.softDelete();
        user.softDelete();

        doctorRepository.save(doctor);
        userRepository.save(user);
    }

    private void validateOwnership(
            Doctor doctor,
            String doctorEmail
    ) {

        if (!doctor.getUser()
                .getEmail()
                .equals(doctorEmail)) {

            throw new ForbiddenException(
                    "You do not have permission to modify this doctor profile"
            );
        }
    }

    private DoctorReadOnlyDTO mapToReadOnlyDTO(
            Doctor doctor
    ) {

        return new DoctorReadOnlyDTO(
                doctor.getId(),
                doctor.getUser().getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getSpecialty(),
                doctor.getPhone()
        );
    }
}

// TODO: add search by first name / last name V.2.