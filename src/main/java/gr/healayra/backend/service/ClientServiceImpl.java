package gr.healayra.backend.service;

import gr.healayra.backend.core.exception.ConflictException;
import gr.healayra.backend.core.exception.ResourceNotFoundException;
import gr.healayra.backend.dto.client.ClientCreateDTO;
import gr.healayra.backend.dto.client.ClientReadOnlyDTO;
import gr.healayra.backend.dto.client.ClientUpdateDTO;
import gr.healayra.backend.model.Client;
import gr.healayra.backend.model.User;
import gr.healayra.backend.repository.ClientRepository;
import gr.healayra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    @Override
    public ClientReadOnlyDTO createClient(ClientCreateDTO dto) {

        User user = userRepository
                .findByIdAndDeletedFalse(dto.userId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        boolean clientAlreadyExists =
                clientRepository
                        .findByUserId(dto.userId())
                        .isPresent();

        if (clientAlreadyExists) {
            throw new ConflictException(
                    "Client profile already exists for this user"
            );
        }

        Client client = Client.builder()
                .user(user)
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .phone(dto.phone())
                .build();

        Client savedClient =
                clientRepository.save(client);

        return mapToReadOnlyDTO(savedClient);
    }

    @Override
    public ClientReadOnlyDTO getClientById(Long id) {

        Client client = clientRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Client not found"
                        )
                );

        return mapToReadOnlyDTO(client);
    }

    @Override
    public ClientReadOnlyDTO getClientByUserId(Long userId) {

        Client client = clientRepository
                .findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Client not found"
                        )
                );

        return mapToReadOnlyDTO(client);
    }

    @Override
    public List<ClientReadOnlyDTO> getAllClients() {

        return clientRepository
                .findAllByDeletedFalse()
                .stream()
                .map(this::mapToReadOnlyDTO)
                .toList();
    }

    @Override
    public ClientReadOnlyDTO updateClient(
            Long clientId,
            ClientUpdateDTO dto
    ) {

        Client client = clientRepository
                .findByIdAndDeletedFalse(clientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Client not found"
                        )
                );

        client.setFirstName(dto.firstName());
        client.setLastName(dto.lastName());
        client.setPhone(dto.phone());

        Client updatedClient =
                clientRepository.save(client);

        return mapToReadOnlyDTO(updatedClient);
    }

    @Override
    @Transactional
    public void deleteClient(Long clientId) {

        Client client = clientRepository
                .findByIdAndDeletedFalse(clientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Client not found"
                        )
                );

        User user = client.getUser();

        client.softDelete();
        user.softDelete();

        clientRepository.save(client);
        userRepository.save(user);
    }

    private ClientReadOnlyDTO mapToReadOnlyDTO(
            Client client
    ) {

        return new ClientReadOnlyDTO(
                client.getId(),
                client.getUser().getId(),
                client.getFirstName(),
                client.getLastName(),
                client.getPhone()
        );
    }
}