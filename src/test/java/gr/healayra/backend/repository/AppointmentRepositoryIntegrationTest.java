package gr.healayra.backend.repository;

import gr.healayra.backend.model.Appointment;
import gr.healayra.backend.model.AppointmentStatus;
import gr.healayra.backend.model.Client;
import gr.healayra.backend.model.Doctor;
import gr.healayra.backend.model.Role;
import gr.healayra.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class AppointmentRepositoryIntegrationTest {

    private static final String SERVICE =
            "Ατομική Συνεδρία";

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ClientRepository clientRepository;

    private Doctor doctor;
    private Client firstClient;
    private Client secondClient;

    private LocalDateTime appointmentTime;

    @BeforeEach
    void setUp() {

        String uniqueId =
                UUID.randomUUID().toString();

        User doctorUser = User.builder()
                .email(
                        "doctor-"
                                + uniqueId
                                + "@test.com"
                )
                .password("test-password")
                .role(Role.DOCTOR)
                .build();

        doctorUser =
                userRepository.saveAndFlush(
                        doctorUser
                );

        doctor = Doctor.builder()
                .user(doctorUser)
                .firstName("Test")
                .lastName("Doctor")
                .specialty("Psychologist")
                .phone("6900000000")
                .build();

        doctor =
                doctorRepository.saveAndFlush(
                        doctor
                );

        User firstClientUser =
                User.builder()
                        .email(
                                "client-one-"
                                        + uniqueId
                                        + "@test.com"
                        )
                        .password("test-password")
                        .role(Role.CLIENT)
                        .build();

        firstClientUser =
                userRepository.saveAndFlush(
                        firstClientUser
                );

        firstClient = Client.builder()
                .user(firstClientUser)
                .firstName("First")
                .lastName("Client")
                .phone("6911111111")
                .build();

        firstClient =
                clientRepository.saveAndFlush(
                        firstClient
                );

        User secondClientUser =
                User.builder()
                        .email(
                                "client-two-"
                                        + uniqueId
                                        + "@test.com"
                        )
                        .password("test-password")
                        .role(Role.CLIENT)
                        .build();

        secondClientUser =
                userRepository.saveAndFlush(
                        secondClientUser
                );

        secondClient = Client.builder()
                .user(secondClientUser)
                .firstName("Second")
                .lastName("Client")
                .phone("6922222222")
                .build();

        secondClient =
                clientRepository.saveAndFlush(
                        secondClient
                );

        LocalDate nextMonday =
                LocalDate.now()
                        .with(
                                TemporalAdjusters.next(
                                        DayOfWeek.MONDAY
                                )
                        );

        appointmentTime =
                nextMonday.atTime(
                        10,
                        0
                );
    }

    @Test
    void shouldPreventDoubleBookingForSameDoctorAndTime() {

        Appointment firstAppointment =
                Appointment.builder()
                        .doctor(doctor)
                        .client(firstClient)
                        .appointmentTime(
                                appointmentTime
                        )
                        .service(SERVICE)
                        .status(
                                AppointmentStatus.PENDING
                        )
                        .build();

        appointmentRepository.saveAndFlush(
                firstAppointment
        );

        Appointment secondAppointment =
                Appointment.builder()
                        .doctor(doctor)
                        .client(secondClient)
                        .appointmentTime(
                                appointmentTime
                        )
                        .service(SERVICE)
                        .status(
                                AppointmentStatus.PENDING
                        )
                        .build();

        assertThrows(
                DataIntegrityViolationException.class,
                () ->
                        appointmentRepository
                                .saveAndFlush(
                                        secondAppointment
                                )
        );
    }

    @Test
    void shouldPreventBookingWhenExistingAppointmentIsConfirmed() {

        Appointment firstAppointment =
                Appointment.builder()
                        .doctor(doctor)
                        .client(firstClient)
                        .appointmentTime(
                                appointmentTime
                        )
                        .service(SERVICE)
                        .status(
                                AppointmentStatus.CONFIRMED
                        )
                        .build();

        appointmentRepository.saveAndFlush(
                firstAppointment
        );

        Appointment secondAppointment =
                Appointment.builder()
                        .doctor(doctor)
                        .client(secondClient)
                        .appointmentTime(
                                appointmentTime
                        )
                        .service(SERVICE)
                        .status(
                                AppointmentStatus.PENDING
                        )
                        .build();

        assertThrows(
                DataIntegrityViolationException.class,
                () ->
                        appointmentRepository
                                .saveAndFlush(
                                        secondAppointment
                                )
        );
    }

    @Test
    void shouldAllowBookingWhenPreviousAppointmentIsCancelled() {

        Appointment cancelledAppointment =
                Appointment.builder()
                        .doctor(doctor)
                        .client(firstClient)
                        .appointmentTime(
                                appointmentTime
                        )
                        .service(SERVICE)
                        .status(
                                AppointmentStatus.CANCELLED
                        )
                        .build();

        appointmentRepository.saveAndFlush(
                cancelledAppointment
        );

        Appointment newAppointment =
                Appointment.builder()
                        .doctor(doctor)
                        .client(secondClient)
                        .appointmentTime(
                                appointmentTime
                        )
                        .service(SERVICE)
                        .status(
                                AppointmentStatus.PENDING
                        )
                        .build();

        assertDoesNotThrow(
                () ->
                        appointmentRepository
                                .saveAndFlush(
                                        newAppointment
                                )
        );
    }
}