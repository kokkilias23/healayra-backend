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

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final IAppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentReadOnlyDTO> createAppointment(
            @Valid @RequestBody AppointmentCreateDTO dto,
            Principal principal
    ) {

        AppointmentReadOnlyDTO createdAppointment =
                appointmentService.createAppointment(
                        dto,
                        principal.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdAppointment);
    }

    @GetMapping("/me")
    public ResponseEntity<List<AppointmentReadOnlyDTO>> getMyAppointments(
            Principal principal
    ) {

        List<AppointmentReadOnlyDTO> appointments =
                appointmentService.getMyAppointments(
                        principal.getName()
                );

        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentReadOnlyDTO> getAppointmentById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                appointmentService.getAppointmentById(id)
        );
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentReadOnlyDTO>> getAppointmentsByDoctor(
            @PathVariable Long doctorId
    ) {

        return ResponseEntity.ok(
                appointmentService.getAppointmentsByDoctor(doctorId)
        );
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<AppointmentReadOnlyDTO>> getAppointmentsByClient(
            @PathVariable Long clientId
    ) {

        return ResponseEntity.ok(
                appointmentService.getAppointmentsByClient(clientId)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentReadOnlyDTO> updateAppointmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentUpdateStatusDTO dto
    ) {

        return ResponseEntity.ok(
                appointmentService.updateStatus(id, dto)
        );
    }
}