package gr.healayra.backend.controller;

import gr.healayra.backend.dto.appointment.AppointmentCreateDTO;
import gr.healayra.backend.dto.appointment.AppointmentReadOnlyDTO;
import gr.healayra.backend.dto.appointment.AppointmentUpdateStatusDTO;
import gr.healayra.backend.service.IAppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final IAppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentReadOnlyDTO> createAppointment(
            @Valid @RequestBody AppointmentCreateDTO dto
    ) {

        AppointmentReadOnlyDTO createdAppointment =
                appointmentService.createAppointment(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdAppointment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentReadOnlyDTO> getAppointmentById(
            @PathVariable Long id
    ) {

        AppointmentReadOnlyDTO appointment =
                appointmentService.getAppointmentById(id);

        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentReadOnlyDTO>> getAppointmentsByDoctor(
            @PathVariable Long doctorId
    ) {

        List<AppointmentReadOnlyDTO> appointments =
                appointmentService.getAppointmentsByDoctor(doctorId);

        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<AppointmentReadOnlyDTO>> getAppointmentsByClient(
            @PathVariable Long clientId
    ) {

        List<AppointmentReadOnlyDTO> appointments =
                appointmentService.getAppointmentsByClient(clientId);

        return ResponseEntity.ok(appointments);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentReadOnlyDTO> updateAppointmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentUpdateStatusDTO dto
    ) {

        AppointmentReadOnlyDTO updatedAppointment =
                appointmentService.updateStatus(id, dto);

        return ResponseEntity.ok(updatedAppointment);
    }
}