package gr.healayra.backend.service;

import gr.healayra.backend.dto.client.ClientCreateDTO;
import gr.healayra.backend.dto.client.ClientReadOnlyDTO;
import gr.healayra.backend.dto.client.ClientUpdateDTO;

import java.util.List;

public interface IClientService {

    ClientReadOnlyDTO createClient(ClientCreateDTO dto);

    ClientReadOnlyDTO getClientById(Long id);

    ClientReadOnlyDTO getClientByUserId(Long userId);

    List<ClientReadOnlyDTO> getAllClients();

    List<ClientReadOnlyDTO> searchClients(String query);

    ClientReadOnlyDTO updateClient(
            Long clientId,
            ClientUpdateDTO dto
    );

    void deleteClient(Long clientId);
}