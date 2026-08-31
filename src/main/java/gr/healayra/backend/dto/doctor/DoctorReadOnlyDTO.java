package gr.healayra.backend.dto.doctor;

public record DoctorReadOnlyDTO(

        Long id,

        Long userId,

        String firstName,

        String lastName,

        String specialty,

        String phone

) {
}