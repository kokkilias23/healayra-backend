CREATE TABLE doctors (
                         id BIGSERIAL PRIMARY KEY,

                         user_id BIGINT NOT NULL UNIQUE,

                         first_name VARCHAR(100) NOT NULL,
                         last_name VARCHAR(100) NOT NULL,
                         specialty VARCHAR(150),
                         phone VARCHAR(30),

                         CONSTRAINT fk_doctor_user
                             FOREIGN KEY (user_id)
                                 REFERENCES users(id)
                                 ON DELETE CASCADE
);

CREATE TABLE clients (
                         id BIGSERIAL PRIMARY KEY,

                         user_id BIGINT NOT NULL UNIQUE,

                         first_name VARCHAR(100) NOT NULL,
                         last_name VARCHAR(100) NOT NULL,
                         phone VARCHAR(30),

                         CONSTRAINT fk_client_user
                             FOREIGN KEY (user_id)
                                 REFERENCES users(id)
                                 ON DELETE CASCADE
);