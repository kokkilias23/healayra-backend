package gr.healayra.backend.service;

import gr.healayra.backend.dto.doctor.DoctorCreateDTO;
import gr.healayra.backend.dto.doctor.DoctorReadOnlyDTO;
import gr.healayra.backend.dto.doctor.DoctorUpdateDTO;

import java.util.List;

public interface IDoctorService {

    DoctorReadOnlyDTO createDoctor(DoctorCreateDTO dto);

    DoctorReadOnlyDTO getDoctorById(Long id);

    DoctorReadOnlyDTO getDoctorByUserId(Long userId);

    List<DoctorReadOnlyDTO> getAllDoctors();

    DoctorReadOnlyDTO updateDoctor(
            Long doctorId,
            DoctorUpdateDTO dto
    );

    void deleteDoctor(Long doctorId);
}