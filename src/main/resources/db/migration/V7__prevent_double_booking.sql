-- Prevent two active appointments from occupying
-- the same time slot for the same doctor.
--
-- CANCELLED appointments do not block the slot,
-- allowing another client to book it again.

CREATE UNIQUE INDEX uq_active_doctor_appointment_slot
    ON appointments (doctor_id, appointment_time)
    WHERE deleted = FALSE
      AND status IN ('PENDING', 'CONFIRMED');