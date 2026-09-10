package gr.healayra.backend.controller;

import gr.healayra.backend.dto.client.ClientReadOnlyDTO;
import gr.healayra.backend.service.IClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(
        name = "Clients",
        description = "Doctor-only client management and search endpoints"
)
public class ClientController {

    private final IClientService clientService;

    @Operation(
            summary = "Get client by ID",
            description = """
                    Returns a specific client profile.
                    Access is restricted to doctors and the backend verifies
                    that the requested client belongs to the authenticated
                    doctor's workspace.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Client returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Doctor is not allowed to access this client"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Client not found"
            )
    })
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

    @Operation(
            summary = "Get all clients",
            description = """
                    Returns the active clients available to the currently
                    authenticated doctor.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Clients returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Endpoint is available only to doctors"
            )
    })
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

    @Operation(
            summary = "Search clients",
            description = """
                    Searches clients visible to the authenticated doctor
                    using the provided query.

                    The search is used by the doctor client-management
                    interface.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search query"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Endpoint is available only to doctors"
            )
    })
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