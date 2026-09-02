package gr.healayra.backend.controller;

import gr.healayra.backend.dto.doctor.DoctorCreateDTO;
import gr.healayra.backend.dto.doctor.DoctorReadOnlyDTO;
import gr.healayra.backend.dto.doctor.DoctorUpdateDTO;
import gr.healayra.backend.service.IDoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final IDoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorReadOnlyDTO> createDoctor(
            @Valid @RequestBody DoctorCreateDTO dto
    ) {

        DoctorReadOnlyDTO createdDoctor =
                doctorService.createDoctor(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdDoctor);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorReadOnlyDTO> getDoctorById(
            @PathVariable Long id
    ) {

        DoctorReadOnlyDTO doctor =
                doctorService.getDoctorById(id);

        return ResponseEntity.ok(doctor);
    }

    @GetMapping
    public ResponseEntity<List<DoctorReadOnlyDTO>> getAllDoctors() {

        List<DoctorReadOnlyDTO> doctors =
                doctorService.getAllDoctors();

        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<DoctorReadOnlyDTO> getDoctorByUserId(
            @PathVariable Long userId
    ) {

        DoctorReadOnlyDTO doctor =
                doctorService.getDoctorByUserId(userId);

        return ResponseEntity.ok(doctor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorReadOnlyDTO> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorUpdateDTO dto
    ) {

        DoctorReadOnlyDTO updatedDoctor =
                doctorService.updateDoctor(id, dto);

        return ResponseEntity.ok(updatedDoctor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(
            @PathVariable Long id
    ) {

        doctorService.deleteDoctor(id);

        return ResponseEntity.noContent().build();
    }
}