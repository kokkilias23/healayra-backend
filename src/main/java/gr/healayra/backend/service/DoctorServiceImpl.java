package gr.healayra.backend.service;

import gr.healayra.backend.dto.doctor.DoctorCreateDTO;
import gr.healayra.backend.dto.doctor.DoctorReadOnlyDTO;
import gr.healayra.backend.dto.doctor.DoctorUpdateDTO;
import gr.healayra.backend.model.Doctor;
import gr.healayra.backend.model.User;
import gr.healayra.backend.repository.DoctorRepository;
import gr.healayra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements IDoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Override
    public DoctorReadOnlyDTO createDoctor(DoctorCreateDTO dto) {

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean doctorAlreadyExists =
                doctorRepository.findByUserId(dto.userId()).isPresent();

        if (doctorAlreadyExists) {
            throw new RuntimeException(
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

        Doctor savedDoctor = doctorRepository.save(doctor);

        return mapToReadOnlyDTO(savedDoctor);
    }

    @Override
    public DoctorReadOnlyDTO getDoctorById(Long id) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return mapToReadOnlyDTO(doctor);
    }

    @Override
    public DoctorReadOnlyDTO getDoctorByUserId(Long userId) {

        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return mapToReadOnlyDTO(doctor);
    }

    @Override
    public List<DoctorReadOnlyDTO> getAllDoctors() {

        return doctorRepository.findAll()
                .stream()
                .map(this::mapToReadOnlyDTO)
                .toList();
    }

    @Override
    public DoctorReadOnlyDTO updateDoctor(
            Long doctorId,
            DoctorUpdateDTO dto
    ) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctor.setFirstName(dto.firstName());
        doctor.setLastName(dto.lastName());
        doctor.setSpecialty(dto.specialty());
        doctor.setPhone(dto.phone());

        Doctor updatedDoctor = doctorRepository.save(doctor);

        return mapToReadOnlyDTO(updatedDoctor);
    }

    @Override
    public void deleteDoctor(Long doctorId) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctorRepository.delete(doctor);
    }

    private DoctorReadOnlyDTO mapToReadOnlyDTO(Doctor doctor) {

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