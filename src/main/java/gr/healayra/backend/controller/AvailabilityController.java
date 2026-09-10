package gr.healayra.backend.controller;

import gr.healayra.backend.dto.availability.AvailabilityCreateDTO;
import gr.healayra.backend.dto.availability.AvailabilityReadOnlyDTO;
import gr.healayra.backend.dto.availability.AvailabilityUpdateDTO;
import gr.healayra.backend.service.IAvailabilityService;
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
@RequestMapping("/api/availability")
@RequiredArgsConstructor
@Tag(
        name = "Availability",
        description = "Doctor weekly availability management endpoints"
)
public class AvailabilityController {

    private final IAvailabilityService availabilityService;

    @Operation(
            summary = "Create doctor availability",
            description = """
                    Creates a new weekly availability entry for the
                    authenticated doctor.

                    The availability defines the weekday, working hours,
                    session duration and whether the day is enabled.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Availability created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid availability data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only doctors can create availability"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Availability already exists for this doctor and weekday"
            )
    })
    @PostMapping
    public ResponseEntity<AvailabilityReadOnlyDTO> createAvailability(
            @Valid @RequestBody AvailabilityCreateDTO dto,
            Principal principal
    ) {

        AvailabilityReadOnlyDTO createdAvailability =
                availabilityService.createAvailability(
                        dto,
                        principal.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdAvailability);
    }

    @Operation(
            summary = "Get availability by ID",
            description = """
                    Returns a specific active availability entry
                    using its unique identifier.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Availability returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Availability not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AvailabilityReadOnlyDTO> getAvailabilityById(
            @PathVariable Long id
    ) {

        AvailabilityReadOnlyDTO availability =
                availabilityService.getAvailabilityById(id);

        return ResponseEntity.ok(availability);
    }

    @Operation(
            summary = "Get availability for a doctor",
            description = """
                    Returns all active weekly availability entries
                    configured for the selected doctor.

                    This endpoint is used by clients when generating
                    available booking dates and time slots.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor availability returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found"
            )
    })
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AvailabilityReadOnlyDTO>> getAvailabilityByDoctor(
            @PathVariable Long doctorId
    ) {

        List<AvailabilityReadOnlyDTO> availability =
                availabilityService.getAvailabilityByDoctor(doctorId);

        return ResponseEntity.ok(availability);
    }

    @Operation(
            summary = "Update doctor availability",
            description = """
                    Updates an existing weekly availability entry.
                    A doctor can modify only availability that belongs
                    to their own profile.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Availability updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid availability data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor cannot modify another doctor's availability"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Availability not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityReadOnlyDTO> updateAvailability(
            @PathVariable Long id,
            @Valid @RequestBody AvailabilityUpdateDTO dto,
            Principal principal
    ) {

        AvailabilityReadOnlyDTO updatedAvailability =
                availabilityService.updateAvailability(
                        id,
                        dto,
                        principal.getName()
                );

        return ResponseEntity.ok(updatedAvailability);
    }

    @Operation(
            summary = "Delete doctor availability",
            description = """
                    Soft-deletes an existing availability entry.
                    A doctor can delete only availability belonging
                    to their own profile.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Availability deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor cannot delete another doctor's availability"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Availability not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long id,
            Principal principal
    ) {

        availabilityService.deleteAvailability(
                id,
                principal.getName()
        );

        return ResponseEntity.noContent().build();
    }
}