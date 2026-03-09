# Shift Scheduler REST API

A production-grade backend API for employee shift management, timekeeping, and labor cost analysis. Architected to mirror the labor management tools found in industry-leading platforms like Toast.

## Overview

The **Shift Scheduler API** provides a robust solution for businesses to manage their workforce efficiently. It handles employee records, shift scheduling, real-time clock-in/out tracking, and automated labor cost calculations.

### Key Features

- **Employee Management** — CRUD operations for staff records with role-specific hourly rates.
- **Dynamic Scheduling** — Create and manage shifts with overlap conflict prevention.
- **Timekeeping** — Real-time clock-in/out tracking with open-entry detection.
- **Labor Cost Engine** — Automated calculation of labor costs based on actual hours worked × hourly rate.
- **Labor Reports** — Date-range reports aggregated by employee and by role.
- **API Documentation** — Full Swagger/OpenAPI UI available at runtime.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin (JVM 17) |
| Framework | Spring Boot 3.2 |
| Database | PostgreSQL 15 |
| ORM | Spring Data JPA / Hibernate |
| Migrations | Flyway |
| Containerization | Docker & Docker Compose |
| Build Tool | Gradle (Kotlin DSL) |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Testing | JUnit 5, MockK, SpringMockK, AssertJ |

---

## Architecture

```
src/main/kotlin/com/shiftscheduler/
├── ShiftSchedulerApplication.kt     # Entry point
├── domain/                          # JPA entities & enums
│   ├── Employee.kt
│   ├── Shift.kt
│   ├── TimeEntry.kt
│   └── Role.kt                      # Role & ShiftStatus enums
├── repository/                      # Spring Data JPA repositories
│   ├── EmployeeRepository.kt
│   ├── ShiftRepository.kt
│   └── TimeEntryRepository.kt
├── dto/                             # Request/Response DTOs
│   ├── EmployeeDto.kt
│   ├── ShiftDto.kt
│   ├── TimeEntryDto.kt
│   └── LaborReportDto.kt
├── service/                         # Business logic
│   ├── EmployeeService.kt
│   ├── ShiftService.kt
│   ├── TimeEntryService.kt
│   ├── LaborCalculationService.kt
│   └── ReportService.kt
├── controller/                      # REST controllers
│   ├── EmployeeController.kt
│   ├── ShiftController.kt
│   ├── TimeEntryController.kt
│   └── ReportController.kt
└── exception/                       # Error handling
    └── GlobalExceptionHandler.kt
```

---

## Data Model

**Employee** — `id`, `name`, `email` (unique), `role`, `hourlyRate`

**Shift** — `id`, `employeeId`, `startTime`, `endTime`, `roleAtShift`, `status` (SCHEDULED | COMPLETED | CANCELLED)

**TimeEntry** — `id`, `employeeId`, `shiftId` (optional), `clockIn`, `clockOut`, `totalHours`

**Role enum** — `SERVER`, `COOK`, `MANAGER`, `BARTENDER`, `HOST`

---

## Getting Started

### Prerequisites

- Docker & Docker Compose
- JDK 17+

### 1. Start Infrastructure (PostgreSQL)

```bash
docker-compose up -d postgres
```

### 2. Run the Application

```bash
./gradlew bootRun
```

Or run everything with Docker Compose:

```bash
docker-compose up --build
```

### 3. Access API Documentation

Once running, open:

```
http://localhost:8080/swagger-ui.html
```

---

## API Endpoints

### Employees — `/api/employees`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/employees` | List all employees |
| GET | `/api/employees/{id}` | Get employee by ID |
| POST | `/api/employees` | Create employee |
| PUT | `/api/employees/{id}` | Update employee |
| DELETE | `/api/employees/{id}` | Delete employee |

### Shifts — `/api/shifts`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/shifts` | List all shifts (optional `?employeeId=`) |
| GET | `/api/shifts/{id}` | Get shift by ID |
| POST | `/api/shifts` | Create shift (validates no overlap) |
| PUT | `/api/shifts/{id}` | Update shift times/role |
| PATCH | `/api/shifts/{id}/status` | Update shift status |
| DELETE | `/api/shifts/{id}` | Delete shift |

### Timekeeping — `/api/timekeeping`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/timekeeping/clock-in` | Clock an employee in |
| POST | `/api/timekeeping/clock-out` | Clock an employee out |
| GET | `/api/timekeeping` | Get all time entries |
| GET | `/api/timekeeping/employee/{id}` | Get entries for an employee |

### Reports — `/api/reports`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/reports/labor-costs?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD` | Labor cost report by date range |

---

## Running Tests

```bash
./gradlew test
```

Tests use H2 in-memory database (no Docker needed for testing). Unit tests cover:

- `LaborCalculationService` — hours and cost calculation logic
- `ShiftService` — overlap detection and business rules
- `EmployeeController` — REST layer with MockMvc
- `ShiftController` — REST layer with MockMvc

---

## Building the Docker Image

```bash
docker-compose up --build
```

Or manually:

```bash
./gradlew bootJar
docker build -t shift-scheduler .
docker run -p 8080:8080 shift-scheduler
```

---

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/shift_scheduler` | PostgreSQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | `scheduler_user` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `scheduler_pass` | Database password |

---

## License

Open source under the [MIT License](LICENSE).
