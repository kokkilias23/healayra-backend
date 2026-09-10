package gr.healayra.backend.controller;

import gr.healayra.backend.dto.appointment.AppointmentCreateDTO;
import gr.healayra.backend.dto.appointment.AppointmentReadOnlyDTO;
import gr.healayra.backend.dto.appointment.AppointmentUpdateStatusDTO;
import gr.healayra.backend.service.IAppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Appointments",
        description = "Appointment booking and management endpoints"
)
public class AppointmentController {

    private final IAppointmentService appointmentService;

    @Operation(
            summary = "Create an appointment",
            description = """
                    Creates a new appointment for the authenticated client.
                    The backend validates doctor availability, working hours,
                    session duration, slot alignment and double booking.
                    New appointments are created with PENDING status.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Appointment created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid appointment data or unavailable time slot"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only clients can create appointments"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor or client profile not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Appointment slot is already booked"
            )
    })
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

    @Operation(
            summary = "Get authenticated client's appointments",
            description = """
                    Returns all appointments belonging to the currently
                    authenticated client.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointments returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Endpoint is available only to clients"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Client profile not found"
            )
    })
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

    @Operation(
            summary = "Get appointment by ID",
            description = """
                    Returns a specific appointment.
                    A doctor can access only appointments that belong
                    to their own practice.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor does not own this appointment"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentReadOnlyDTO> getAppointmentById(
            @PathVariable Long id,
            Principal principal
    ) {

        return ResponseEntity.ok(
                appointmentService.getAppointmentById(
                        id,
                        principal.getName()
                )
        );
    }

    @Operation(
            summary = "Get appointments for a doctor",
            description = """
                    Returns all appointments belonging to the selected doctor.
                    The authenticated doctor can request only their own
                    appointments.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor appointments returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor cannot access another doctor's appointments"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found"
            )
    })
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentReadOnlyDTO>> getAppointmentsByDoctor(
            @PathVariable Long doctorId,
            Principal principal
    ) {

        return ResponseEntity.ok(
                appointmentService.getAppointmentsByDoctor(
                        doctorId,
                        principal.getName()
                )
        );
    }

    @Operation(
            summary = "Get appointments for a client",
            description = """
                    Returns appointments for a specific client that belong
                    to the currently authenticated doctor.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Client appointments returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Endpoint is available only to doctors"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor profile not found"
            )
    })
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<AppointmentReadOnlyDTO>> getAppointmentsByClient(
            @PathVariable Long clientId,
            Principal principal
    ) {

        return ResponseEntity.ok(
                appointmentService.getAppointmentsByClient(
                        clientId,
                        principal.getName()
                )
        );
    }

    @Operation(
            summary = "Update appointment status",
            description = """
                    Updates the status of an appointment owned by the
                    authenticated doctor.

                    Valid status transitions are:

                    PENDING -> CONFIRMED or CANCELLED
                    CONFIRMED -> COMPLETED or CANCELLED

                    COMPLETED and CANCELLED are terminal states.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid appointment status transition"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor does not own this appointment"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found"
            )
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentReadOnlyDTO> updateAppointmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentUpdateStatusDTO dto,
            Principal principal
    ) {

        return ResponseEntity.ok(
                appointmentService.updateStatus(
                        id,
                        dto,
                        principal.getName()
                )
        );
    }
}