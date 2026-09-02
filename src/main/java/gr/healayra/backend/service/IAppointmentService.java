package gr.healayra.backend.service;

import gr.healayra.backend.dto.appointment.AppointmentCreateDTO;
import gr.healayra.backend.dto.appointment.AppointmentReadOnlyDTO;
import gr.healayra.backend.dto.appointment.AppointmentUpdateStatusDTO;

import java.util.List;

public interface IAppointmentService {

    AppointmentReadOnlyDTO createAppointment(
            AppointmentCreateDTO dto,
            String clientEmail
    );

    AppointmentReadOnlyDTO getAppointmentById(Long id);

    List<AppointmentReadOnlyDTO> getAppointmentsByDoctor(Long doctorId);

    List<AppointmentReadOnlyDTO> getAppointmentsByClient(Long clientId);

    List<AppointmentReadOnlyDTO> getMyAppointments(String clientEmail);

    AppointmentReadOnlyDTO updateStatus(
            Long appointmentId,
            AppointmentUpdateStatusDTO dto
    );
}