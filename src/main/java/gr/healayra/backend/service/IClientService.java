package gr.healayra.backend.service;

import gr.healayra.backend.dto.client.ClientReadOnlyDTO;

import java.util.List;

public interface IClientService {

    ClientReadOnlyDTO getClientById(
            Long id,
            String doctorEmail
    );

    List<ClientReadOnlyDTO> getAllClients(
            String doctorEmail
    );

    List<ClientReadOnlyDTO> searchClients(
            String query,
            String doctorEmail
    );
}