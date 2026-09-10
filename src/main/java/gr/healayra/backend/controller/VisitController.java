package gr.healayra.backend.controller;

import gr.healayra.backend.dto.visit.VisitCreateDTO;
import gr.healayra.backend.dto.visit.VisitReadOnlyDTO;
import gr.healayra.backend.service.IVisitService;
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
@RequestMapping("/api/visits")
@RequiredArgsConstructor
@Tag(
        name = "Visits",
        description = "Doctor-only visit and session history management endpoints"
)
public class VisitController {

    private final IVisitService visitService;

    @Operation(
            summary = "Create a visit",
            description = """
                    Creates a new visit/session record for a client.
                    The authenticated doctor can create visits only
                    within their own client workspace.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Visit created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid visit data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor is not allowed to create this visit"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor or client not found"
            )
    })
    @PostMapping
    public ResponseEntity<VisitReadOnlyDTO> createVisit(
            @Valid @RequestBody VisitCreateDTO dto,
            Principal principal
    ) {

        VisitReadOnlyDTO createdVisit =
                visitService.createVisit(
                        dto,
                        principal.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdVisit);
    }

    @Operation(
            summary = "Get visit by ID",
            description = """
                    Returns a specific visit/session record.
                    Access is restricted to the doctor who owns
                    the visit.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Visit returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor cannot access this visit"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Visit not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<VisitReadOnlyDTO> getVisitById(
            @PathVariable Long id,
            Principal principal
    ) {

        VisitReadOnlyDTO visit =
                visitService.getVisitById(
                        id,
                        principal.getName()
                );

        return ResponseEntity.ok(visit);
    }

    @Operation(
            summary = "Get visits for a client",
            description = """
                    Returns the visit history of a specific client
                    that is visible to the authenticated doctor.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Client visits returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor cannot access this client's visit history"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Client not found"
            )
    })
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<VisitReadOnlyDTO>> getVisitsByClient(
            @PathVariable Long clientId,
            Principal principal
    ) {

        List<VisitReadOnlyDTO> visits =
                visitService.getVisitsByClient(
                        clientId,
                        principal.getName()
                );

        return ResponseEntity.ok(visits);
    }

    @Operation(
            summary = "Get visits for a doctor and client",
            description = """
                    Returns visit/session records for a specific doctor-client pair.
                    The authenticated doctor must own the requested doctor profile
                    and the related visit records.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Doctor-client visits returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor cannot access another doctor's visit records"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Doctor or client not found"
            )
    })
    @GetMapping("/doctor/{doctorId}/client/{clientId}")
    public ResponseEntity<List<VisitReadOnlyDTO>> getVisitsByDoctorAndClient(
            @PathVariable Long doctorId,
            @PathVariable Long clientId,
            Principal principal
    ) {

        List<VisitReadOnlyDTO> visits =
                visitService.getVisitsByDoctorAndClient(
                        doctorId,
                        clientId,
                        principal.getName()
                );

        return ResponseEntity.ok(visits);
    }

    @Operation(
            summary = "Delete a visit",
            description = """
                    Soft-deletes an existing visit/session record.
                    A doctor can delete only visits that belong
                    to their own workspace.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Visit deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor cannot delete this visit"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Visit not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisit(
            @PathVariable Long id,
            Principal principal
    ) {

        visitService.deleteVisit(
                id,
                principal.getName()
        );

        return ResponseEntity.noContent().build();
    }
}