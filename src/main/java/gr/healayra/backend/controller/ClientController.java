package gr.healayra.backend.controller;

import gr.healayra.backend.dto.client.ClientCreateDTO;
import gr.healayra.backend.dto.client.ClientReadOnlyDTO;
import gr.healayra.backend.dto.client.ClientUpdateDTO;
import gr.healayra.backend.service.IClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final IClientService clientService;

    @PostMapping
    public ResponseEntity<ClientReadOnlyDTO> createClient(
            @Valid @RequestBody ClientCreateDTO dto
    ) {

        ClientReadOnlyDTO createdClient =
                clientService.createClient(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdClient);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientReadOnlyDTO> getClientById(
            @PathVariable Long id
    ) {

        ClientReadOnlyDTO client =
                clientService.getClientById(id);

        return ResponseEntity.ok(client);
    }

    @GetMapping
    public ResponseEntity<List<ClientReadOnlyDTO>> getAllClients() {

        List<ClientReadOnlyDTO> clients =
                clientService.getAllClients();

        return ResponseEntity.ok(clients);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ClientReadOnlyDTO> getClientByUserId(
            @PathVariable Long userId
    ) {

        ClientReadOnlyDTO client =
                clientService.getClientByUserId(userId);

        return ResponseEntity.ok(client);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientReadOnlyDTO> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientUpdateDTO dto
    ) {

        ClientReadOnlyDTO updatedClient =
                clientService.updateClient(id, dto);

        return ResponseEntity.ok(updatedClient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(
            @PathVariable Long id
    ) {

        clientService.deleteClient(id);

        return ResponseEntity.noContent().build();
    }
}