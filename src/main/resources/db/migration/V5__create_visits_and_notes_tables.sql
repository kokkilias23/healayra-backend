CREATE TABLE visits (
                        id BIGSERIAL PRIMARY KEY,

                        doctor_id BIGINT NOT NULL,
                        client_id BIGINT NOT NULL,

                        visit_time TIMESTAMP NOT NULL,

                        service VARCHAR(150),

                        CONSTRAINT fk_visit_doctor
                            FOREIGN KEY (doctor_id)
                                REFERENCES doctors(id)
                                ON DELETE CASCADE,

                        CONSTRAINT fk_visit_client
                            FOREIGN KEY (client_id)
                                REFERENCES clients(id)
                                ON DELETE CASCADE
);

CREATE TABLE notes (
                       id BIGSERIAL PRIMARY KEY,

                       visit_id BIGINT NOT NULL,

                       content TEXT NOT NULL,

                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT fk_note_visit
                           FOREIGN KEY (visit_id)
                               REFERENCES visits(id)
                               ON DELETE CASCADE
);