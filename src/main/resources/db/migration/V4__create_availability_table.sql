CREATE TABLE availability (
                              id BIGSERIAL PRIMARY KEY,

                              doctor_id BIGINT NOT NULL,

                              day_of_week VARCHAR(20) NOT NULL,

                              start_time TIME NOT NULL,
                              end_time TIME NOT NULL,

                              session_duration INTEGER NOT NULL,

                              enabled BOOLEAN NOT NULL,

                              CONSTRAINT fk_availability_doctor
                                  FOREIGN KEY (doctor_id)
                                      REFERENCES doctors(id)
                                      ON DELETE CASCADE
);