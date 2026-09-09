package gr.healayra.backend.controller;

import gr.healayra.backend.dto.availability.AvailabilityCreateDTO;
import gr.healayra.backend.dto.availability.AvailabilityReadOnlyDTO;
import gr.healayra.backend.dto.availability.AvailabilityUpdateDTO;
import gr.healayra.backend.service.IAvailabilityService;
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
public class AvailabilityController {

    private final IAvailabilityService availabilityService;

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

    @GetMapping("/{id}")
    public ResponseEntity<AvailabilityReadOnlyDTO> getAvailabilityById(
            @PathVariable Long id
    ) {

        AvailabilityReadOnlyDTO availability =
                availabilityService.getAvailabilityById(id);

        return ResponseEntity.ok(availability);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AvailabilityReadOnlyDTO>> getAvailabilityByDoctor(
            @PathVariable Long doctorId
    ) {

        List<AvailabilityReadOnlyDTO> availability =
                availabilityService.getAvailabilityByDoctor(doctorId);

        return ResponseEntity.ok(availability);
    }

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