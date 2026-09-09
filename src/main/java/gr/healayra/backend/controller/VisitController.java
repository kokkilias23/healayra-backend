package gr.healayra.backend.controller;

import gr.healayra.backend.dto.visit.VisitCreateDTO;
import gr.healayra.backend.dto.visit.VisitReadOnlyDTO;
import gr.healayra.backend.service.IVisitService;
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
public class VisitController {

    private final IVisitService visitService;

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