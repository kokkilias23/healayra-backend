package gr.healayra.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
class AppointmentSecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        mockMvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void getMyAppointments_shouldReturn401_whenUserIsNotAuthenticated()
            throws Exception {

        mockMvc.perform(
                        get("/api/appointments/me")
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    @WithMockUser(
            username = "doctor@test.com",
            roles = "DOCTOR"
    )
    void getMyAppointments_shouldReturn403_whenUserIsDoctor()
            throws Exception {

        mockMvc.perform(
                        get("/api/appointments/me")
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    @WithMockUser(
            username = "client@test.com",
            roles = "CLIENT"
    )
    void getDoctorAppointmentEndpoint_shouldReturn403_whenUserIsClient()
            throws Exception {

        mockMvc.perform(
                        get("/api/appointments/999999")
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    @WithMockUser(
            username = "doctor@test.com",
            roles = "DOCTOR"
    )
    void getAppointmentById_shouldPassSecurity_whenUserIsDoctor()
            throws Exception {

        mockMvc.perform(
                        get("/api/appointments/999999")
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    @WithMockUser(
            username = "doctor@test.com",
            roles = "DOCTOR"
    )
    void createAppointment_shouldReturn403_whenUserIsDoctor()
            throws Exception {

        String requestBody = """
                {
                    "doctorId": 1,
                    "appointmentTime": "2026-10-05T10:00:00"
                }
                """;

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType("application/json")
                                .content(requestBody)
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    @WithMockUser(
            username = "client@test.com",
            roles = "CLIENT"
    )
    void createAppointment_shouldPassSecurity_whenUserIsClient()
            throws Exception {

        String invalidRequestBody = """
                {
                    "doctorId": null,
                    "appointmentTime": null
                }
                """;

        mockMvc.perform(
                        post("/api/appointments")
                                .contentType("application/json")
                                .content(invalidRequestBody)
                )
                .andExpect(
                        status().isBadRequest()
                );
    }
}