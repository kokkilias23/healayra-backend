package gr.healayra.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.healayra.backend.core.exception.BadRequestException;
import gr.healayra.backend.core.exception.ConflictException;
import gr.healayra.backend.core.exception.ForbiddenException;
import gr.healayra.backend.core.exception.GlobalExceptionHandler;
import gr.healayra.backend.dto.appointment.AppointmentCreateDTO;
import gr.healayra.backend.dto.appointment.AppointmentReadOnlyDTO;
import gr.healayra.backend.dto.appointment.AppointmentUpdateStatusDTO;
import gr.healayra.backend.model.AppointmentStatus;
import gr.healayra.backend.service.IAppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private IAppointmentService appointmentService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private final Principal clientPrincipal =
            () -> "client@healayra.gr";

    private final Principal doctorPrincipal =
            () -> "doctor@healayra.gr";

    @BeforeEach
    void setUp() {

        AppointmentController appointmentController =
                new AppointmentController(appointmentService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(appointmentController)
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void createAppointment_shouldReturn201_whenAppointmentIsCreated()
            throws Exception {

        LocalDateTime appointmentTime =
                LocalDateTime.of(
                        2026,
                        9,
                        14,
                        10,
                        0
                );

        AppointmentCreateDTO request =
                new AppointmentCreateDTO(
                        1L,
                        appointmentTime
                );

        AppointmentReadOnlyDTO response =
                new AppointmentReadOnlyDTO(
                        100L,
                        1L,
                        10L,
                        appointmentTime,
                        AppointmentStatus.PENDING,
                        null
                );

        when(
                appointmentService.createAppointment(
                        any(AppointmentCreateDTO.class),
                        eq("client@healayra.gr")
                )
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/appointments")
                                .principal(clientPrincipal)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request
                                                )
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(100)
                )
                .andExpect(
                        jsonPath("$.doctorId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.clientId")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("PENDING")
                );
    }

    @Test
    void createAppointment_shouldReturn409_whenSlotAlreadyBooked()
            throws Exception {

        LocalDateTime appointmentTime =
                LocalDateTime.of(
                        2026,
                        9,
                        14,
                        10,
                        0
                );

        AppointmentCreateDTO request =
                new AppointmentCreateDTO(
                        1L,
                        appointmentTime
                );

        when(
                appointmentService.createAppointment(
                        any(AppointmentCreateDTO.class),
                        eq("client@healayra.gr")
                )
        ).thenThrow(
                new ConflictException(
                        "Appointment slot already booked"
                )
        );

        mockMvc.perform(
                        post("/api/appointments")
                                .principal(clientPrincipal)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request
                                                )
                                )
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(409)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Appointment slot already booked"
                                )
                );
    }

    @Test
    void updateStatus_shouldReturn200_whenTransitionIsValid()
            throws Exception {

        AppointmentUpdateStatusDTO request =
                new AppointmentUpdateStatusDTO(
                        AppointmentStatus.CONFIRMED
                );

        AppointmentReadOnlyDTO response =
                new AppointmentReadOnlyDTO(
                        100L,
                        1L,
                        10L,
                        LocalDateTime.of(
                                2026,
                                9,
                                14,
                                10,
                                0
                        ),
                        AppointmentStatus.CONFIRMED,
                        null
                );

        when(
                appointmentService.updateStatus(
                        eq(100L),
                        any(AppointmentUpdateStatusDTO.class),
                        eq("doctor@healayra.gr")
                )
        ).thenReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/appointments/{id}/status",
                                100L
                        )
                                .principal(doctorPrincipal)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request
                                                )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("CONFIRMED")
                );
    }

    @Test
    void updateStatus_shouldReturn400_whenTransitionIsInvalid()
            throws Exception {

        AppointmentUpdateStatusDTO request =
                new AppointmentUpdateStatusDTO(
                        AppointmentStatus.COMPLETED
                );

        when(
                appointmentService.updateStatus(
                        eq(100L),
                        any(AppointmentUpdateStatusDTO.class),
                        eq("doctor@healayra.gr")
                )
        ).thenThrow(
                new BadRequestException(
                        "Invalid appointment status transition from PENDING to COMPLETED"
                )
        );

        mockMvc.perform(
                        patch(
                                "/api/appointments/{id}/status",
                                100L
                        )
                                .principal(doctorPrincipal)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request
                                                )
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Invalid appointment status transition from PENDING to COMPLETED"
                                )
                );
    }

    @Test
    void updateStatus_shouldReturn403_whenDoctorDoesNotOwnAppointment()
            throws Exception {

        AppointmentUpdateStatusDTO request =
                new AppointmentUpdateStatusDTO(
                        AppointmentStatus.CONFIRMED
                );

        when(
                appointmentService.updateStatus(
                        eq(100L),
                        any(AppointmentUpdateStatusDTO.class),
                        eq("doctor@healayra.gr")
                )
        ).thenThrow(
                new ForbiddenException(
                        "You do not have permission to access this appointment"
                )
        );

        mockMvc.perform(
                        patch(
                                "/api/appointments/{id}/status",
                                100L
                        )
                                .principal(doctorPrincipal)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request
                                                )
                                )
                )
                .andExpect(
                        status().isForbidden()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(403)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "You do not have permission to access this appointment"
                                )
                );
    }

    @Test
    void createAppointment_shouldReturn400_whenRequestIsInvalid()
            throws Exception {

        String invalidRequest = """
                {
                    "doctorId": null,
                    "appointmentTime": null
                }
                """;

        mockMvc.perform(
                        post("/api/appointments")
                                .principal(clientPrincipal)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(invalidRequest)
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("Validation failed")
                );
    }
}