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
- PostgreSQL 17
- Flyway
- Jakarta Validation
- Lombok
- Gradle
- Docker
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
- Doctor-client ownership rules

Double booking protection exists both in the service layer and at database level.

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

Doctors can access only clients that are related to them through appointments.

The application uses the term **Client** in the codebase and **Θεραπευόμενος** in the Greek user interface.

---

## Visits and Notes

Doctors can create visits for their clients.

A visit stores information such as:

- Doctor
- Client
- Visit date and time
- Service

A doctor can create a visit only for a client that already has an appointment relationship with that doctor.

Notes can then be attached to individual visits.

This allows the doctor to maintain a basic history of previous sessions.

---

## Soft Delete and Auditing

Several entities use soft deletion.

Instead of permanently removing records from the database, deleted records can be marked with fields such as:

```text
deleted
deletedAt
```

The project also uses Spring Data JPA auditing for fields such as:

```text
createdAt
updatedAt
```

This helps preserve historical information and track entity changes.

---

## Database

The project uses PostgreSQL 17.

Local development configuration:

```text
Database: healayra
Username: healayra_user
Port: 5432
```

Database schema changes are managed using Flyway migrations.

Hibernate is configured with:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

This means that Hibernate validates the schema while Flyway is responsible for database migrations.

Current Flyway migrations include:

```text
V1 - Users
V2 - Doctors and clients
V3 - Appointments
V4 - Availability
V5 - Visits and notes
V6 - Base entity auditing
V7 - Double-booking protection
V8 - Appointment service field
```

---

## Docker

The complete backend stack can run with Docker Compose.

Docker Compose starts:

```text
healayra-backend
healayra-postgres
```

The backend container communicates with PostgreSQL through Docker's internal network:

```text
healayra-backend
        ↓
postgres:5432
        ↓
healayra-postgres
```

The PostgreSQL service also includes a health check so the backend waits until the database is ready before starting.

---

## Environment Variables

The backend requires a JWT secret.

The secret is **not stored directly in the repository**.

The application expects:

```text
JWT_SECRET
```

### Git Bash

Generate a random Base64 secret:

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

Other supported environment variables include:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_EXPIRATION
CORS_ALLOWED_ORIGINS
```

Docker Compose configures the database connection automatically for the backend container.

---

## Run with Docker Compose

Make sure Docker Desktop is installed and running.

First configure `JWT_SECRET`.

### Git Bash

```bash
export JWT_SECRET="$(openssl rand -base64 32)"
```

Then start the complete backend stack:

```bash
docker compose up --build
```

This builds the Spring Boot Docker image and starts both:

```text
healayra-backend
healayra-postgres
```

The REST API will be available at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

---

## Stop Docker Compose

To stop and remove the running containers:

```bash
docker compose down
```

The PostgreSQL data is stored in a named Docker volume and is preserved when running:

```bash
docker compose down
```

To also remove the database volume and its stored data:

```bash
docker compose down -v
```

Use `-v` only when you intentionally want to delete the Docker database data.

---

## Run without Dockerized Backend

The application can also be run directly with Gradle while PostgreSQL is available locally.

Configure `JWT_SECRET` first.

### Git Bash

```bash
export JWT_SECRET="$(openssl rand -base64 32)"
./gradlew bootRun
```

### Windows PowerShell

```powershell
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)

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
./gradlew clean build
```

### Windows PowerShell

```powershell
.\gradlew clean build
```

A successful build should finish with:

```text
BUILD SUCCESSFUL
```

The generated executable JAR file is located inside:

```text
build/libs/
```

---

## Swagger / OpenAPI

The REST API is documented using Swagger / OpenAPI.

Swagger UI is available while the backend is running:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI documentation is available at:

```text
http://localhost:8080/v3/api-docs
```

The API controllers include endpoint descriptions, response codes and role-specific documentation.

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

### Users

```text
GET /api/users/me
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

The backend CORS configuration allows the frontend development origin.

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
- Doctor ownership validation
- Client relationship validation
- Custom 401 and 403 responses
- Global exception handling
- CORS configuration
- Jakarta request validation
- Database-level double-booking protection

For a production healthcare environment, additional security, compliance and privacy hardening would be required.

---

## Error Handling

The backend uses a global REST exception handler.

API errors are returned using a consistent JSON response structure.

Handled error categories include:

```text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
405 Method Not Allowed
409 Conflict
500 Internal Server Error
```

Validation errors also return field-specific information.

---

## Testing

The project contains automated tests for important appointment and security behavior.

Current test areas include:

- Spring application context
- Appointment service logic
- Appointment controller behavior
- Appointment repository integration
- Appointment security integration
- Visit ownership validation

Run the complete test suite with:

```bash
./gradlew clean build
```

The REST API can also be tested manually using:

- Swagger UI
- Postman

The backend build and Docker deployment have been successfully tested locally.

---

## Dockerfile

The backend uses a multi-stage Docker build.

The build stage:

```text
Java 21 JDK
      ↓
Gradle bootJar
      ↓
Spring Boot executable JAR
```

The runtime stage uses a Java 21 JRE and contains only the executable application JAR required to run the backend.

This keeps the runtime image separate from the full build environment.

---

## Future Improvements

Possible future improvements include:

- Full multi-tenant support
- Multiple doctor organizations
- Doctor search
- Custom doctor domains
- Client appointment cancellation
- Email notifications
- Appointment reminders
- HttpOnly Secure cookie authentication
- Refresh token support
- Advanced appointment status workflows
- Pagination for larger datasets
- Cloud deployment
- CI/CD
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
- Visit ownership protection
- Visit notes
- PostgreSQL persistence
- Flyway database migrations
- Database-level double-booking protection
- JPA auditing
- Soft deletion
- Global REST exception handling
- Swagger / OpenAPI documentation
- Automated tests
- Dockerized Spring Boot backend
- Dockerized PostgreSQL database
- Docker Compose full backend environment

---

## Author

Developed by **kokkilias23** as a Coding Factory final project.

## Healayra

**Trust the Process.**