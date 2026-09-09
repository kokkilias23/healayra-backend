package gr.healayra.backend.controller;

import gr.healayra.backend.dto.client.ClientReadOnlyDTO;
import gr.healayra.backend.service.IClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final IClientService clientService;

    @GetMapping("/{id}")
    public ResponseEntity<ClientReadOnlyDTO> getClientById(
            @PathVariable Long id,
            Principal principal
    ) {

        ClientReadOnlyDTO client =
                clientService.getClientById(
                        id,
                        principal.getName()
                );

        return ResponseEntity.ok(client);
    }

    @GetMapping
    public ResponseEntity<List<ClientReadOnlyDTO>> getAllClients(
            Principal principal
    ) {

        List<ClientReadOnlyDTO> clients =
                clientService.getAllClients(
                        principal.getName()
                );

        return ResponseEntity.ok(clients);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ClientReadOnlyDTO>> searchClients(
            @RequestParam String query,
            Principal principal
    ) {

        List<ClientReadOnlyDTO> clients =
                clientService.searchClients(
                        query,
                        principal.getName()
                );

        return ResponseEntity.ok(clients);
    }
}