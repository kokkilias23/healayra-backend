package gr.healayra.backend.dto.client;

public record ClientReadOnlyDTO(

        Long id,

        Long userId,

        String firstName,

        String lastName,

        String phone

) {
}