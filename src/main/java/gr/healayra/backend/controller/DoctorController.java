package gr.healayra.backend.controller;

import gr.healayra.backend.dto.doctor.DoctorCreateDTO;
import gr.healayra.backend.dto.doctor.DoctorReadOnlyDTO;
import gr.healayra.backend.dto.doctor.DoctorUpdateDTO;
import gr.healayra.backend.service.IDoctorService;
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
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(
        name = "Doctors",
        description = "Doctor profile management and doctor lookup endpoints"
)
public class DoctorController {

    private final IDoctorService doctorService;

    @Operation(
            summary = "Create doctor profile",
            description = """
                    Creates a doctor profile for the authenticated doctor user.
                    The profile is associated with the currently authenticated
                    account.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Doctor profile created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid doctor profile data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only doctors can create doctor profiles"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Doctor profile already exists"
            )
    })
    @PostMapping
    public ResponseEntity<DoctorReadOnlyDTO> createDoctor(
            @Valid @RequestBody DoctorCreateDTO dto,
            Principal principal
    ) {

        DoctorReadOnlyDTO createdDoctor =
                doctorService.createDoctor(
                        dto,
                        principal.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdDoctor);
    }

    @Operation(
            summary = "Get doctor by ID",
            description = """
                    Returns an active doctor profile using the doctor's
                    unique identifier.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor returned successfully"
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
    @GetMapping("/{id}")
    public ResponseEntity<DoctorReadOnlyDTO> getDoctorById(
            @PathVariable Long id
    ) {

        DoctorReadOnlyDTO doctor =
                doctorService.getDoctorById(id);

        return ResponseEntity.ok(doctor);
    }

    @Operation(
            summary = "Get all doctors",
            description = """
                    Returns all active doctor profiles available
                    to authenticated users.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctors returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @GetMapping
    public ResponseEntity<List<DoctorReadOnlyDTO>> getAllDoctors() {

        List<DoctorReadOnlyDTO> doctors =
                doctorService.getAllDoctors();

        return ResponseEntity.ok(doctors);
    }

    @Operation(
            summary = "Get doctor by user ID",
            description = """
                    Returns the doctor profile associated with
                    a specific user account.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor profile not found for this user"
            )
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<DoctorReadOnlyDTO> getDoctorByUserId(
            @PathVariable Long userId
    ) {

        DoctorReadOnlyDTO doctor =
                doctorService.getDoctorByUserId(userId);

        return ResponseEntity.ok(doctor);
    }

    @Operation(
            summary = "Update doctor profile",
            description = """
                    Updates an existing doctor profile.
                    A doctor can modify only their own profile.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor profile updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid doctor profile data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor cannot update another doctor's profile"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<DoctorReadOnlyDTO> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorUpdateDTO dto,
            Principal principal
    ) {

        DoctorReadOnlyDTO updatedDoctor =
                doctorService.updateDoctor(
                        id,
                        dto,
                        principal.getName()
                );

        return ResponseEntity.ok(updatedDoctor);
    }

    @Operation(
            summary = "Delete doctor profile",
            description = """
                    Soft-deletes an existing doctor profile.
                    A doctor can delete only their own profile.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Doctor profile deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor cannot delete another doctor's profile"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(
            @PathVariable Long id,
            Principal principal
    ) {

        doctorService.deleteDoctor(
                id,
                principal.getName()
        );

        return ResponseEntity.noContent().build();
    }
}