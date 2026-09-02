package gr.healayra.backend.controller;

import gr.healayra.backend.dto.visit.VisitCreateDTO;
import gr.healayra.backend.dto.visit.VisitReadOnlyDTO;
import gr.healayra.backend.service.IVisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final IVisitService visitService;

    @PostMapping
    public ResponseEntity<VisitReadOnlyDTO> createVisit(
            @Valid @RequestBody VisitCreateDTO dto
    ) {

        VisitReadOnlyDTO createdVisit =
                visitService.createVisit(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdVisit);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitReadOnlyDTO> getVisitById(
            @PathVariable Long id
    ) {

        VisitReadOnlyDTO visit =
                visitService.getVisitById(id);

        return ResponseEntity.ok(visit);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<VisitReadOnlyDTO>> getVisitsByClient(
            @PathVariable Long clientId
    ) {

        List<VisitReadOnlyDTO> visits =
                visitService.getVisitsByClient(clientId);

        return ResponseEntity.ok(visits);
    }

    @GetMapping("/doctor/{doctorId}/client/{clientId}")
    public ResponseEntity<List<VisitReadOnlyDTO>> getVisitsByDoctorAndClient(
            @PathVariable Long doctorId,
            @PathVariable Long clientId
    ) {

        List<VisitReadOnlyDTO> visits =
                visitService.getVisitsByDoctorAndClient(
                        doctorId,
                        clientId
                );

        return ResponseEntity.ok(visits);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisit(
            @PathVariable Long id
    ) {

        visitService.deleteVisit(id);

        return ResponseEntity.noContent().build();
    }
}