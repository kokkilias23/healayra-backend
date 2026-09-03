package gr.healayra.backend.repository;

import gr.healayra.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndDeletedFalse(Long id);

    Optional<User> findByEmailAndDeletedFalse(String email);

    boolean existsByEmail(String email);
}