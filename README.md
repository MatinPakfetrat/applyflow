# ApplyFlow

A backend-focused job application tracker built with Spring Boot, featuring JWT authentication, per-user data ownership, and a fully tested REST API.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## Overview

ApplyFlow is a REST API for tracking job applications — company, role, salary, and status (Applied, Interview, Rejected, Offer) — scoped to individual authenticated users. It was built as a backend-first portfolio project, with a deliberate focus on production-realistic practices: layered architecture, DTO-based API boundaries, structured error handling, ownership-based authorization, and a real test suite rather than manual-only verification.

## Live Demo

The API is deployed at: `https://applyflow-qhqw.onrender.com`

> **Note:** This runs on Render's free tier, which spins down after periods of inactivity. The first request may take 30–60 seconds to respond while the service wakes up — this is expected, not a bug.

## Features

**Authentication & Security**
- Stateless JWT authentication (register / login)
- Passwords hashed with BCrypt — never stored or logged in plaintext
- Every application record is scoped to its owning user; one user can never read or modify another user's data
- Ownership violations return `404` rather than `403`, so an attacker can't tell whether a resource exists

**Application Management**
- Full CRUD for job applications
- Pagination, sorting, and filtering (by status and/or company) on the list endpoint
- Enum-validated status field (`APPLIED`, `INTERVIEW`, `REJECTED`, `OFFER`), with descriptive errors on invalid values in both query parameters and JSON bodies
- Automatic `createdAt` / `updatedAt` timestamps

**API Design**
- DTOs (`ApplicationRequest` / `ApplicationResponse`) decouple the API contract from the JPA entity layer
- Consistent, structured error responses across validation failures, not-found errors, authentication failures, and unexpected exceptions

**Testing**
- Unit tests for JWT logic and service-layer business rules (JUnit 5, Mockito, AssertJ)
- Integration tests exercising the full HTTP → security filter → controller → database flow (MockMvc, H2 in-memory database)

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
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Build Tool | Maven |
| Testing | JUnit 5, Mockito, AssertJ, MockMvc, H2 |
| Logging | SLF4J + Logback |
| API Testing | Postman |

## API Documentation

Base URL: `http://localhost:8080/api`

### Authentication

All endpoints under `/api/applications` require a JWT, obtained via register or login, sent as:

```
Authorization: Bearer <token>
```

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| POST | `/auth/register` | No | Create a new user account |
| POST | `/auth/login` | No | Authenticate and receive a JWT |

**Register**

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
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

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
Authorization: Bearer <token>
```

**Create**

```http
POST /api/applications
Authorization: Bearer <token>
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

Every error — validation failures, not-found, auth failures, unexpected exceptions — returns the same shape:

```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": ["companyName: Company name is required"],
  "timeStamp": "2026-07-31T10:15:30.123"
}
```

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL (local instance)
- Postman (optional, for manual API testing)

### Setup

```bash
git clone https://github.com/<your-username>/applyflow.git
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

## Running Tests

```bash
mvn test
```

Integration tests run against an in-memory H2 database and don't touch your local PostgreSQL instance.

## Project Structure

```
src/main/java/com/matin/applyflow/
├── config/         # Security, JWT filter/util
├── controller/      # REST controllers
├── dto/             # Request/response DTOs, mappers, error response
├── exception/       # Custom exceptions, global exception handler
├── model/           # JPA entities, enums
├── repository/       # Spring Data JPA repositories
└── service/          # Business logic
```

## Roadmap

- [x] DTO layer
- [x] Structured error responses
- [x] JWT authentication & per-user ownership
- [x] Unit & integration testing
- [x] Logging
- [x] README
- [x] Deployment
- [ ] Docker

## License

MIT
