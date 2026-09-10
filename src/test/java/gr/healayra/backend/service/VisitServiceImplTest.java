package gr.healayra.backend.service;

import gr.healayra.backend.core.exception.ForbiddenException;
import gr.healayra.backend.dto.visit.VisitCreateDTO;
import gr.healayra.backend.dto.visit.VisitReadOnlyDTO;
import gr.healayra.backend.model.Client;
import gr.healayra.backend.model.Doctor;
import gr.healayra.backend.model.Role;
import gr.healayra.backend.model.User;
import gr.healayra.backend.model.Visit;
import gr.healayra.backend.repository.AppointmentRepository;
import gr.healayra.backend.repository.ClientRepository;
import gr.healayra.backend.repository.DoctorRepository;
import gr.healayra.backend.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitServiceImplTest {

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private VisitServiceImpl visitService;

    private Doctor doctor;
    private Client client;

    @BeforeEach
    void setUp() {

        User doctorUser = User.builder()
                .id(1L)
                .email("doctor@test.com")
                .password("encoded-password")
                .role(Role.DOCTOR)
                .build();

        doctor = Doctor.builder()
                .id(10L)
                .user(doctorUser)
                .firstName("Doctor")
                .lastName("Test")
                .specialty("Psychologist")
                .build();

        User clientUser = User.builder()
                .id(2L)
                .email("client@test.com")
                .password("encoded-password")
                .role(Role.CLIENT)
                .build();

        client = Client.builder()
                .id(20L)
                .user(clientUser)
                .firstName("Client")
                .lastName("Test")
                .build();
    }

    @Test
    void createVisit_shouldThrowForbidden_whenClientDoesNotBelongToDoctor() {

        VisitCreateDTO dto =
                new VisitCreateDTO(
                        doctor.getId(),
                        client.getId(),
                        LocalDateTime.of(
                                2026,
                                10,
                                5,
                                10,
                                0
                        ),
                        "Individual Session"
                );

        when(
                doctorRepository
                        .findByIdAndDeletedFalse(
                                doctor.getId()
                        )
        ).thenReturn(
                Optional.of(doctor)
        );

        when(
                clientRepository
                        .findByIdAndDeletedFalse(
                                client.getId()
                        )
        ).thenReturn(
                Optional.of(client)
        );

        when(
                appointmentRepository
                        .existsByDoctorIdAndClientIdAndDeletedFalse(
                                doctor.getId(),
                                client.getId()
                        )
        ).thenReturn(false);

        ForbiddenException exception =
                assertThrows(
                        ForbiddenException.class,
                        () ->
                                visitService.createVisit(
                                        dto,
                                        "doctor@test.com"
                                )
                );

        assertEquals(
                "You do not have permission to create a visit for this client",
                exception.getMessage()
        );

        verify(
                visitRepository,
                never()
        ).save(any(Visit.class));
    }

    @Test
    void createVisit_shouldCreateVisit_whenClientBelongsToDoctor() {

        VisitCreateDTO dto =
                new VisitCreateDTO(
                        doctor.getId(),
                        client.getId(),
                        LocalDateTime.of(
                                2026,
                                10,
                                5,
                                10,
                                0
                        ),
                        "Individual Session"
                );

        when(
                doctorRepository
                        .findByIdAndDeletedFalse(
                                doctor.getId()
                        )
        ).thenReturn(
                Optional.of(doctor)
        );

        when(
                clientRepository
                        .findByIdAndDeletedFalse(
                                client.getId()
                        )
        ).thenReturn(
                Optional.of(client)
        );

        when(
                appointmentRepository
                        .existsByDoctorIdAndClientIdAndDeletedFalse(
                                doctor.getId(),
                                client.getId()
                        )
        ).thenReturn(true);

        Visit savedVisit = Visit.builder()
                .id(30L)
                .doctor(doctor)
                .client(client)
                .visitTime(dto.visitTime())
                .service(dto.service())
                .build();

        when(
                visitRepository.save(
                        any(Visit.class)
                )
        ).thenReturn(savedVisit);

        VisitReadOnlyDTO result =
                visitService.createVisit(
                        dto,
                        "doctor@test.com"
                );

        assertEquals(
                30L,
                result.id()
        );

        assertEquals(
                doctor.getId(),
                result.doctorId()
        );

        assertEquals(
                client.getId(),
                result.clientId()
        );

        verify(
                visitRepository
        ).save(any(Visit.class));
    }
}