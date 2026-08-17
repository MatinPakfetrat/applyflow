# ApplyFlow

A full-stack job application tracker built with Spring Boot and React, featuring JWT authentication with refresh token rotation, rate limiting, per-user data ownership, and a fully tested REST API.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-brightgreen)
![React](https://img.shields.io/badge/React-Frontend-61DAFB)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## Overview

ApplyFlow is a job application tracker — company, role, salary, and status (Applied, Interview, Rejected, Offer) — scoped to individual authenticated users, with a React dashboard on top of a Spring Boot REST API. It was built as a full-stack portfolio project, with a deliberate focus on production-realistic practices: layered backend architecture, DTO-based API boundaries, structured error handling, revocable token-based auth, rate limiting, a real test suite, and a deployed, usable frontend rather than an API-only demo.

## Live Demo

**App:** `https://applyflow-frontend-p4qm.onrender.com`
**API:** `https://applyflow-qhqw.onrender.com/api`

> Both run on free-tier hosting. The API spins down after inactivity — the first action after idle time (login, loading applications) may take 30–60 seconds while it wakes up. The app itself loads instantly regardless.

## Features

**Authentication & Security**
- Short-lived JWT access tokens (15 min) paired with long-lived, revocable refresh tokens (7 days), stored server-side and rotated on every use
- Real logout — refresh tokens are revoked in the database, not just discarded client-side
- Rate limiting (5 requests/minute per IP) on all auth endpoints via Bucket4j, defending against brute-force login attempts
- Passwords hashed with BCrypt — never stored or logged in plaintext
- Every application record is scoped to its owning user; ownership violations return `404` rather than `403`, so an attacker can't tell whether a resource exists
- CORS explicitly scoped to the deployed frontend origin

**Application Management**
- Full CRUD for job applications
- Pagination, sorting, and filtering (by status and/or company) on the list endpoint
- Enum-validated status field (`APPLIED`, `INTERVIEW`, `REJECTED`, `OFFER`), with descriptive errors on invalid values in both query parameters and JSON bodies
- Automatic `createdAt` / `updatedAt` timestamps

**Frontend**
- React dashboard with live filtering, sorting, and pagination against the API
- Full CRUD via modal forms, with toast feedback on save/delete
- Each application's pipeline stage is reflected directly in the UI's color system
- Automatic, race-safe access token refresh via Axios interceptors — a session survives page reloads without re-entering credentials

**API Design**
- DTOs decouple the API contract from the JPA entity layer
- Consistent, structured error responses across validation failures, not-found errors, authentication failures, and unexpected exceptions

**Testing**
- Unit tests for JWT logic and service-layer business rules (JUnit 5, Mockito, AssertJ)
- Integration tests exercising the full HTTP → security filter → controller → database flow (MockMvc, H2 in-memory database), including refresh token rotation

**Logging**
- Structured, leveled logging via SLF4J/Logback
- Rolling daily log files with 7-day retention
- Sensitive data (passwords, tokens) intentionally never logged

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4 (Spring Framework 7) |
| Security | Spring Security, JWT (jjwt 0.12.6) |
| Rate Limiting | Bucket4j |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Local Config | spring-dotenv |
| Build Tool | Maven |
| Testing | JUnit 5, Mockito, AssertJ, MockMvc, H2 |
| Logging | SLF4J + Logback |
| Containerization | Docker, Docker Compose |
| Frontend | React, Vite, Tailwind CSS, React Router, Axios |
| Deployment | Render (backend + static frontend), Neon (PostgreSQL) |
| API Testing | Postman |

## API Documentation

Base URL (local): `http://localhost:8080/api`

### Authentication

Protected endpoints require a JWT access token, obtained via register, login, or refresh, sent as:

```
Authorization: Bearer <accessToken>
```

Auth endpoints are rate limited to 5 requests per minute per IP address.

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| POST | `/auth/register` | No | Create a new user account |
| POST | `/auth/login` | No | Authenticate and receive a token pair |
| POST | `/auth/refresh` | No | Exchange a valid refresh token for a new token pair |
| POST | `/auth/logout` | No | Revoke a refresh token |

**Register / Login**

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "matin",
  "email": "matin@email.com",
  "password": "secret123"
}
```

Response `201 Created`:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
}
```

**Refresh**

```http
POST /api/auth/refresh
Content-Type: application/json

{ "refreshToken": "3fa85f64-5717-4562-b3fc-2c963f66afa6" }
```

Returns a brand new token pair. The refresh token used is immediately invalidated — reusing it returns `401`.

**Logout**

```http
POST /api/auth/logout
Content-Type: application/json

{ "refreshToken": "3fa85f64-5717-4562-b3fc-2c963f66afa6" }
```

`204 No Content`. The refresh token can no longer be used to obtain new access tokens.

### Applications

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| GET | `/applications` | Yes | List your applications (paginated, filterable) |
| POST | `/applications` | Yes | Create a new application |
| PUT | `/applications/{id}` | Yes | Update an application you own |
| DELETE | `/applications/{id}` | Yes | Delete an application you own |

**List with filters**

```http
GET /api/applications?status=APPLIED&company=Google&page=0&size=5&sort=salary,desc
Authorization: Bearer <accessToken>
```

**Create**

```http
POST /api/applications
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "companyName": "Google",
  "jobTitle": "Backend Engineer",
  "salary": 130000,
  "status": "APPLIED"
}
```

Response `201 Created`:
```json
{
  "id": 1,
  "companyName": "Google",
  "jobTitle": "Backend Engineer",
  "salary": 130000,
  "status": "APPLIED",
  "createdAt": "2026-07-31T10:15:30",
  "updatedAt": "2026-07-31T10:15:30"
}
```

### Error Format

Every error — validation failures, not-found, auth failures, rate limiting, unexpected exceptions — returns the same shape:

```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": ["companyName: Company name is required"],
  "timeStamp": "2026-07-31T10:15:30.123"
}
```

## Running with Docker (Recommended)

If you have Docker installed, this is the fastest way to run the full backend stack — no local Java, Maven, or PostgreSQL installation required.

### Prerequisites
- Docker Desktop

### Setup

```bash
git clone https://github.com/MatinPakfetrat/applyflow.git
cd applyflow
cp .env.example .env
```

Edit `.env` with your own values, then:

```bash
docker compose up --build
```

The API is available at `http://localhost:8080/api`.

## Getting Started (Manual)

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL (local instance)
- Postman (optional, for manual API testing)

### Setup

```bash
git clone https://github.com/MatinPakfetrat/applyflow.git
cd applyflow
```

Create the database (via pgAdmin or `psql`):
```sql
CREATE DATABASE applyflow_db;
```

Copy the config template and fill in your own values:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```
Edit `application.properties` with your PostgreSQL credentials, and generate your own JWT secret:
```bash
openssl rand -hex 32
```

Run the application:
```bash
mvn spring-boot:run
```

The API is now available at `http://localhost:8080/api`.

## Frontend

A React dashboard lives in `frontend/` — filter, sort, and paginate applications, and manage them through modal-based forms.

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

Requires the backend running separately (see above). Set `VITE_API_BASE_URL` in `frontend/.env` to point at your backend.

## Running Tests

```bash
mvn test
```

Integration tests run against an in-memory H2 database and don't touch your local PostgreSQL instance.

## Project Structure

```
src/main/java/com/matin/applyflow/
├── config/          # Security, JWT filter/util, CORS, rate limiting
├── controller/      # REST controllers
├── dto/             # Request/response DTOs, mappers, error response
├── exception/       # Custom exceptions, global exception handler
├── model/           # JPA entities, enums
├── repository/      # Spring Data JPA repositories
└── service/         # Business logic

frontend/
└── src/
    ├── api/         # Axios client with auth interceptors
    ├── components/  # Reusable UI components
    ├── context/     # Auth context
    ├── hooks/       # Custom hooks
    └── pages/       # Route-level pages
```

## Roadmap

**Core**
- [x] DTO layer
- [x] Structured error responses
- [x] JWT authentication & per-user ownership
- [x] Unit & integration testing
- [x] Logging
- [x] README
- [x] Deployment
- [x] Docker

**Extended**
- [x] Refresh tokens with rotation & revocation
- [x] Rate limiting on auth endpoints
- [x] React frontend
- [x] Frontend deployment

## License

MIT
