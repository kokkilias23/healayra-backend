package gr.healayra.backend.service;

import gr.healayra.backend.core.exception.BadRequestException;
import gr.healayra.backend.core.exception.ConflictException;
import gr.healayra.backend.core.exception.ForbiddenException;
import gr.healayra.backend.dto.appointment.AppointmentCreateDTO;
import gr.healayra.backend.dto.appointment.AppointmentReadOnlyDTO;
import gr.healayra.backend.dto.appointment.AppointmentUpdateStatusDTO;
import gr.healayra.backend.model.Appointment;
import gr.healayra.backend.model.AppointmentStatus;
import gr.healayra.backend.model.Availability;
import gr.healayra.backend.model.Client;
import gr.healayra.backend.model.Doctor;
import gr.healayra.backend.model.Role;
import gr.healayra.backend.model.User;
import gr.healayra.backend.repository.AppointmentRepository;
import gr.healayra.backend.repository.AvailabilityRepository;
import gr.healayra.backend.repository.ClientRepository;
import gr.healayra.backend.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    private static final String SERVICE =
            "Ατομική Συνεδρία";

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Doctor doctor;
    private Client client;
    private Availability availability;

    private final String doctorEmail =
            "doctor@healayra.gr";

    private final String clientEmail =
            "client@healayra.gr";

    @BeforeEach
    void setUp() {

        User doctorUser = User.builder()
                .id(1L)
                .email(doctorEmail)
                .password("hashed-password")
                .role(Role.DOCTOR)
                .build();

        User clientUser = User.builder()
                .id(2L)
                .email(clientEmail)
                .password("hashed-password")
                .role(Role.CLIENT)
                .build();

        doctor = Doctor.builder()
                .id(1L)
                .user(doctorUser)
                .firstName("Danai")
                .lastName("Doctor")
                .specialty("Psychologist")
                .phone("6900000000")
                .build();

        client = Client.builder()
                .id(10L)
                .user(clientUser)
                .firstName("Maria")
                .lastName("Client")
                .phone("6911111111")
                .build();

        availability = Availability.builder()
                .id(100L)
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .sessionDuration(60)
                .enabled(true)
                .build();
    }

    @Test
    void createAppointment_shouldCreatePendingAppointment_whenSlotIsValid() {

        LocalDateTime appointmentTime =
                validMondayAppointmentTime();

        AppointmentCreateDTO dto =
                new AppointmentCreateDTO(
                        doctor.getId(),
                        appointmentTime,
                        SERVICE
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
                        .findByUserEmailAndDeletedFalse(
                                clientEmail
                        )
        ).thenReturn(
                Optional.of(client)
        );

        when(
                availabilityRepository
                        .findByDoctorIdAndDayOfWeekAndDeletedFalse(
                                doctor.getId(),
                                DayOfWeek.MONDAY
                        )
        ).thenReturn(
                Optional.of(availability)
        );

        when(
                appointmentRepository
                        .existsByDoctorIdAndAppointmentTimeAndStatusInAndDeletedFalse(
                                eq(doctor.getId()),
                                eq(appointmentTime),
                                any()
                        )
        ).thenReturn(false);

        when(
                appointmentRepository
                        .save(
                                any(Appointment.class)
                        )
        ).thenAnswer(invocation -> {

            Appointment appointment =
                    invocation.getArgument(0);

            appointment.setId(500L);

            return appointment;
        });

        AppointmentReadOnlyDTO result =
                appointmentService.createAppointment(
                        dto,
                        clientEmail
                );

        assertNotNull(result);

        assertEquals(
                500L,
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

        assertEquals(
                appointmentTime,
                result.appointmentTime()
        );

        assertEquals(
                SERVICE,
                result.service()
        );

        assertEquals(
                AppointmentStatus.PENDING,
                result.status()
        );

        verify(
                appointmentRepository
        ).save(
                any(Appointment.class)
        );
    }

    @Test
    void createAppointment_shouldThrowConflict_whenSlotAlreadyBooked() {

        LocalDateTime appointmentTime =
                validMondayAppointmentTime();

        AppointmentCreateDTO dto =
                new AppointmentCreateDTO(
                        doctor.getId(),
                        appointmentTime,
                        SERVICE
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
                        .findByUserEmailAndDeletedFalse(
                                clientEmail
                        )
        ).thenReturn(
                Optional.of(client)
        );

        when(
                availabilityRepository
                        .findByDoctorIdAndDayOfWeekAndDeletedFalse(
                                doctor.getId(),
                                DayOfWeek.MONDAY
                        )
        ).thenReturn(
                Optional.of(availability)
        );

        when(
                appointmentRepository
                        .existsByDoctorIdAndAppointmentTimeAndStatusInAndDeletedFalse(
                                eq(doctor.getId()),
                                eq(appointmentTime),
                                any()
                        )
        ).thenReturn(true);

        ConflictException exception =
                assertThrows(
                        ConflictException.class,
                        () ->
                                appointmentService.createAppointment(
                                        dto,
                                        clientEmail
                                )
                );

        assertEquals(
                "Appointment slot already booked",
                exception.getMessage()
        );

        verify(
                appointmentRepository,
                never()
        ).save(
                any(Appointment.class)
        );
    }

    @Test
    void createAppointment_shouldThrowBadRequest_whenSlotIsOutsideAvailability() {

        LocalDateTime appointmentTime =
                validMondayAppointmentTime()
                        .withHour(17);

        AppointmentCreateDTO dto =
                new AppointmentCreateDTO(
                        doctor.getId(),
                        appointmentTime,
                        SERVICE
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
                        .findByUserEmailAndDeletedFalse(
                                clientEmail
                        )
        ).thenReturn(
                Optional.of(client)
        );

        when(
                availabilityRepository
                        .findByDoctorIdAndDayOfWeekAndDeletedFalse(
                                doctor.getId(),
                                DayOfWeek.MONDAY
                        )
        ).thenReturn(
                Optional.of(availability)
        );

        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () ->
                                appointmentService.createAppointment(
                                        dto,
                                        clientEmail
                                )
                );

        assertEquals(
                "Appointment time is outside doctor's availability",
                exception.getMessage()
        );

        verify(
                appointmentRepository,
                never()
        ).save(
                any(Appointment.class)
        );
    }

    @Test
    void getAppointmentById_shouldReturnAppointment_whenDoctorOwnsIt() {

        Appointment appointment =
                createAppointment(
                        AppointmentStatus.PENDING
                );

        when(
                appointmentRepository
                        .findByIdAndDeletedFalse(
                                appointment.getId()
                        )
        ).thenReturn(
                Optional.of(appointment)
        );

        AppointmentReadOnlyDTO result =
                appointmentService.getAppointmentById(
                        appointment.getId(),
                        doctorEmail
                );

        assertEquals(
                appointment.getId(),
                result.id()
        );

        assertEquals(
                doctor.getId(),
                result.doctorId()
        );

        assertEquals(
                SERVICE,
                result.service()
        );
    }

    @Test
    void getAppointmentById_shouldThrowForbidden_whenDoctorDoesNotOwnIt() {

        Appointment appointment =
                createAppointment(
                        AppointmentStatus.PENDING
                );

        when(
                appointmentRepository
                        .findByIdAndDeletedFalse(
                                appointment.getId()
                        )
        ).thenReturn(
                Optional.of(appointment)
        );

        ForbiddenException exception =
                assertThrows(
                        ForbiddenException.class,
                        () ->
                                appointmentService.getAppointmentById(
                                        appointment.getId(),
                                        "other-doctor@healayra.gr"
                                )
                );

        assertEquals(
                "You do not have permission to access this appointment",
                exception.getMessage()
        );
    }

    @Test
    void updateStatus_shouldChangePendingToConfirmed() {

        Appointment appointment =
                createAppointment(
                        AppointmentStatus.PENDING
                );

        AppointmentUpdateStatusDTO dto =
                new AppointmentUpdateStatusDTO(
                        AppointmentStatus.CONFIRMED
                );

        when(
                appointmentRepository
                        .findByIdAndDeletedFalse(
                                appointment.getId()
                        )
        ).thenReturn(
                Optional.of(appointment)
        );

        when(
                appointmentRepository
                        .save(
                                any(Appointment.class)
                        )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        AppointmentReadOnlyDTO result =
                appointmentService.updateStatus(
                        appointment.getId(),
                        dto,
                        doctorEmail
                );

        assertEquals(
                AppointmentStatus.CONFIRMED,
                result.status()
        );

        assertEquals(
                AppointmentStatus.CONFIRMED,
                appointment.getStatus()
        );
    }

    @Test
    void updateStatus_shouldChangeConfirmedToCompleted() {

        Appointment appointment =
                createAppointment(
                        AppointmentStatus.CONFIRMED
                );

        AppointmentUpdateStatusDTO dto =
                new AppointmentUpdateStatusDTO(
                        AppointmentStatus.COMPLETED
                );

        when(
                appointmentRepository
                        .findByIdAndDeletedFalse(
                                appointment.getId()
                        )
        ).thenReturn(
                Optional.of(appointment)
        );

        when(
                appointmentRepository
                        .save(
                                any(Appointment.class)
                        )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        AppointmentReadOnlyDTO result =
                appointmentService.updateStatus(
                        appointment.getId(),
                        dto,
                        doctorEmail
                );

        assertEquals(
                AppointmentStatus.COMPLETED,
                result.status()
        );
    }

    @Test
    void updateStatus_shouldAllowPendingToCancelled() {

        Appointment appointment =
                createAppointment(
                        AppointmentStatus.PENDING
                );

        AppointmentUpdateStatusDTO dto =
                new AppointmentUpdateStatusDTO(
                        AppointmentStatus.CANCELLED
                );

        when(
                appointmentRepository
                        .findByIdAndDeletedFalse(
                                appointment.getId()
                        )
        ).thenReturn(
                Optional.of(appointment)
        );

        when(
                appointmentRepository
                        .save(
                                any(Appointment.class)
                        )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        AppointmentReadOnlyDTO result =
                appointmentService.updateStatus(
                        appointment.getId(),
                        dto,
                        doctorEmail
                );

        assertEquals(
                AppointmentStatus.CANCELLED,
                result.status()
        );
    }

    @Test
    void updateStatus_shouldThrowBadRequest_whenPendingChangesDirectlyToCompleted() {

        Appointment appointment =
                createAppointment(
                        AppointmentStatus.PENDING
                );

        AppointmentUpdateStatusDTO dto =
                new AppointmentUpdateStatusDTO(
                        AppointmentStatus.COMPLETED
                );

        when(
                appointmentRepository
                        .findByIdAndDeletedFalse(
                                appointment.getId()
                        )
        ).thenReturn(
                Optional.of(appointment)
        );

        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () ->
                                appointmentService.updateStatus(
                                        appointment.getId(),
                                        dto,
                                        doctorEmail
                                )
                );

        assertEquals(
                "Invalid appointment status transition from PENDING to COMPLETED",
                exception.getMessage()
        );

        verify(
                appointmentRepository,
                never()
        ).save(
                any(Appointment.class)
        );
    }

    @Test
    void updateStatus_shouldThrowBadRequest_whenCompletedAppointmentChangesStatus() {

        Appointment appointment =
                createAppointment(
                        AppointmentStatus.COMPLETED
                );

        AppointmentUpdateStatusDTO dto =
                new AppointmentUpdateStatusDTO(
                        AppointmentStatus.CONFIRMED
                );

        when(
                appointmentRepository
                        .findByIdAndDeletedFalse(
                                appointment.getId()
                        )
        ).thenReturn(
                Optional.of(appointment)
        );

        assertThrows(
                BadRequestException.class,
                () ->
                        appointmentService.updateStatus(
                                appointment.getId(),
                                dto,
                                doctorEmail
                        )
        );

        verify(
                appointmentRepository,
                never()
        ).save(
                any(Appointment.class)
        );
    }

    @Test
    void updateStatus_shouldThrowForbidden_whenAnotherDoctorTriesToUpdateAppointment() {

        Appointment appointment =
                createAppointment(
                        AppointmentStatus.PENDING
                );

        AppointmentUpdateStatusDTO dto =
                new AppointmentUpdateStatusDTO(
                        AppointmentStatus.CONFIRMED
                );

        when(
                appointmentRepository
                        .findByIdAndDeletedFalse(
                                appointment.getId()
                        )
        ).thenReturn(
                Optional.of(appointment)
        );

        assertThrows(
                ForbiddenException.class,
                () ->
                        appointmentService.updateStatus(
                                appointment.getId(),
                                dto,
                                "other-doctor@healayra.gr"
                        )
        );

        verify(
                appointmentRepository,
                never()
        ).save(
                any(Appointment.class)
        );
    }

    private Appointment createAppointment(
            AppointmentStatus status
    ) {

        return Appointment.builder()
                .id(500L)
                .doctor(doctor)
                .client(client)
                .appointmentTime(
                        validMondayAppointmentTime()
                )
                .service(SERVICE)
                .status(status)
                .notes(null)
                .build();
    }

    private LocalDateTime validMondayAppointmentTime() {

        LocalDate nextMonday =
                LocalDate.now()
                        .with(
                                TemporalAdjusters.next(
                                        DayOfWeek.MONDAY
                                )
                        );

        return nextMonday.atTime(
                10,
                0
        );
    }
}