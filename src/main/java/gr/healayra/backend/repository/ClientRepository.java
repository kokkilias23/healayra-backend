package gr.healayra.backend.repository;

import gr.healayra.backend.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByUserId(Long userId);

    Optional<Client> findByUserEmail(String email);

    Optional<Client> findByIdAndDeletedFalse(Long id);

    Optional<Client> findByUserIdAndDeletedFalse(Long userId);

    Optional<Client> findByUserEmailAndDeletedFalse(String email);

    List<Client> findAllByDeletedFalse();
}