CREATE TABLE appointments (
                              id BIGSERIAL PRIMARY KEY,

                              doctor_id BIGINT NOT NULL,
                              client_id BIGINT NOT NULL,

                              appointment_time TIMESTAMP NOT NULL,

                              status VARCHAR(50) NOT NULL,

                              notes VARCHAR(500),

                              CONSTRAINT fk_appointment_doctor
                                  FOREIGN KEY (doctor_id)
                                      REFERENCES doctors(id)
                                      ON DELETE CASCADE,

                              CONSTRAINT fk_appointment_client
                                  FOREIGN KEY (client_id)
                                      REFERENCES clients(id)
                                      ON DELETE CASCADE
);