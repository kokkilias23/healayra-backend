# Healayra Backend

**Trust the Process.**

Healayra is a healthcare and therapy appointment management platform developed as the final project for the Coding Factory program.

This repository contains the **Spring Boot REST API backend** of the application.

The frontend is maintained in a separate repository:

`healayra-frontend`

---

## Overview

Healayra provides two main user roles:

### Doctor

Doctors can:

- Log in securely
- Manage their availability
- View appointment requests
- Confirm appointments
- View their clients
- Search clients
- View client details
- Record visits / sessions
- Add notes to visits

### Client

Clients can:

- Register a new account
- Log in securely
- View doctor availability
- Book appointments
- View their appointments
- See appointment confirmation status

---

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Hibernate
- Spring Security
- JWT Authentication
- PostgreSQL
- Flyway
- Jakarta Validation
- Lombok
- Gradle
- Docker Compose
- Swagger / OpenAPI

---

## Architecture

The backend follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

The application uses DTOs to separate the REST API contract from the persistence entities.

Main application layers:

```text
controller/
dto/
service/
repository/
model/
authentication/
security/
core/
```

---

## Main Domain Model

The current MVP contains the following main entities:

```text
User
 ├── Doctor
 └── Client

Doctor
 ├── Availability
 ├── Appointment
 └── Visit

Client
 ├── Appointment
 └── Visit

Visit
 └── Note
```

---

## Authentication & Authorization

Authentication is based on JWT.

Available roles:

```text
DOCTOR
CLIENT
```

Public registration always creates a user with the:

```text
CLIENT
```

role.

Doctor accounts are provisioned separately.

Passwords are stored using BCrypt hashing.

Protected API endpoints require:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

## Appointment Workflow

A client creates an appointment with the initial status:

```text
PENDING
```

This means that the appointment is waiting for confirmation from the doctor.

The doctor can confirm the appointment:

```text
PENDING
   ↓
CONFIRMED
```

Other supported appointment statuses are:

```text
COMPLETED
CANCELLED
```

The backend validates:

- Doctor availability
- Available day of the week
- Start and end time
- Session duration
- Appointment slot alignment
- Double booking

---

## Doctor Availability

Doctors can configure their weekly availability.

Each availability entry contains:

- Day of week
- Start time
- End time
- Session duration
- Enabled / disabled status

Example:

```text
Monday
09:00 - 14:00
Session duration: 50 minutes
```

The frontend retrieves the doctor's availability through the REST API and generates the available appointment time slots.

---

## Client Management

Doctors can:

- View active clients
- Search clients by first name or last name
- Open individual client profiles
- View previous visits
- Create new visits / sessions
- Add notes to visits

The application uses the term **Client** in the codebase and **Θεραπευόμενος** in the Greek user interface.

---

## Visits and Notes

Doctors can create visits for their clients.

A visit stores information such as:

- Doctor
- Client
- Visit date and time
- Service

Notes can then be attached to individual visits.

This allows the doctor to maintain a basic history of previous sessions.

---

## Soft Delete

Several entities use soft deletion.

Instead of permanently removing records from the database, deleted records can be marked with fields such as:

```text
deleted
deletedAt
```

This helps preserve historical information.

---

## Database

The project uses PostgreSQL.

Local development configuration:

```text
Database: healayra
Username: healayra_user
Port: 5432
```

The database can be started with Docker Compose.

Database schema changes are managed using Flyway migrations.

Hibernate is configured with:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

This means that Hibernate validates the schema while Flyway is responsible for database migrations.

---

## Running PostgreSQL

Docker must be installed and running.

From the backend project directory:

```bash
docker compose up -d
```

Check that PostgreSQL is running:

```bash
docker ps
```

---

## Environment Variables

The backend requires a JWT secret.

The secret is not stored directly in the repository.

The application expects:

```text
JWT_SECRET
```

### Git Bash

```bash
export JWT_SECRET="$(openssl rand -base64 32)"
```

### Windows PowerShell

```powershell
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
```

Do not commit production secrets to Git.

---

## Run the Backend

After PostgreSQL is running and `JWT_SECRET` has been configured:

### Git Bash

```bash
./gradlew bootRun
```

### Windows PowerShell

```powershell
.\gradlew bootRun
```

The REST API will be available at:

```text
http://localhost:8080
```

---

## Build

Run:

### Git Bash

```bash
./gradlew build
```

### Windows PowerShell

```powershell
.\gradlew build
```

A successful build should finish with:

```text
BUILD SUCCESSFUL
```

The generated JAR file is located inside:

```text
build/libs/
```

---

## Swagger / OpenAPI

Swagger UI is available while the backend is running:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI documentation is available at:

```text
http://localhost:8080/v3/api-docs
```

For protected endpoints:

1. Login using `/api/auth/login`
2. Copy the returned JWT
3. Open Swagger UI
4. Click **Authorize**
5. Enter the JWT token
6. Test the protected endpoints

---

## Main API Areas

### Authentication

```text
POST /api/auth/register
POST /api/auth/login
```

### Doctors

```text
GET    /api/doctors
GET    /api/doctors/{id}
GET    /api/doctors/user/{userId}
POST   /api/doctors
PUT    /api/doctors/{id}
DELETE /api/doctors/{id}
```

### Clients

```text
GET /api/clients
GET /api/clients/{id}
GET /api/clients/search?query=
```

### Availability

```text
POST   /api/availability
GET    /api/availability/{id}
GET    /api/availability/doctor/{doctorId}
PUT    /api/availability/{id}
DELETE /api/availability/{id}
```

### Appointments

```text
POST  /api/appointments
GET   /api/appointments/me
GET   /api/appointments/{id}
GET   /api/appointments/doctor/{doctorId}
GET   /api/appointments/client/{clientId}
PATCH /api/appointments/{id}/status
```

### Visits

```text
POST   /api/visits
GET    /api/visits/{id}
GET    /api/visits/client/{clientId}
GET    /api/visits/doctor/{doctorId}/client/{clientId}
DELETE /api/visits/{id}
```

### Notes

```text
POST   /api/notes
GET    /api/notes/{id}
GET    /api/notes/visit/{visitId}
PUT    /api/notes/{id}
DELETE /api/notes/{id}
```

---

## Frontend

The frontend is implemented using:

- React
- TypeScript
- Vite
- React Router

During local development, the frontend communicates with:

```text
http://localhost:8080
```

The frontend development server normally runs at:

```text
http://localhost:5173
```

---

## Current MVP Flow

The following end-to-end workflow has been implemented:

```text
Client Registration
        ↓
JWT Authentication
        ↓
Doctor Availability
        ↓
Appointment Booking
        ↓
PENDING Appointment
        ↓
Doctor Confirmation
        ↓
CONFIRMED Appointment
        ↓
Client sees confirmed appointment
```

The doctor can additionally manage:

```text
Clients
   ↓
Visits
   ↓
Notes
```

---

## Security

The MVP includes:

- Spring Security
- JWT authentication
- Role-based authorization
- BCrypt password hashing
- Protected REST endpoints
- CORS configuration
- Request validation

For a production healthcare environment, additional security and privacy hardening would be required.

---

## Testing

The project can be verified with:

```bash
./gradlew build
```

The REST API can also be tested using:

- Swagger UI
- Postman

The final frontend and backend builds were successfully tested locally.

---

## Future Improvements

Possible future improvements include:

- Full multi-tenant support
- Multiple doctors
- Doctor search
- Custom doctor domains
- Client appointment cancellation
- Email notifications
- Appointment reminders
- HttpOnly Secure cookie authentication
- Advanced appointment status transitions
- Database-level booking concurrency protection
- Cloud deployment
- Logging and monitoring
- Extended automated tests
- Production GDPR and privacy hardening

---

## Project Status

The current version represents the MVP developed as the final project for Coding Factory.

Implemented core features include:

- Authentication
- Authorization
- Registration
- Doctor availability management
- Appointment booking
- Appointment confirmation
- Client management
- Client search
- Visit history
- Visit notes
- PostgreSQL persistence
- Flyway database migrations
- Swagger / OpenAPI documentation
- Docker-based PostgreSQL development environment

---

## Author

Developed by **kokkilias23** as a Coding Factory final project.

## Healayra

**Trust the Process.**