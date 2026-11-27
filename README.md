# student-management-api

A Spring Boot 4 REST API for managing students, courses, and enrollments.

This project is the web-layer evolution of the Student Management System
capstone from [java-mastery](https://github.com/rachmania/java-mastery)
(Module 10). The core domain — `Student`, `Course`, `StudentManagementService`,
and its exception types — originates there as plain, fully-tested Java, and is
wrapped in a REST and web layer here. The two are sibling projects sharing the
`com.javamastery` group.

## Tech stack

- **Java 21** (Amazon Corretto)
- **Spring Boot 4** (Spring Framework 7, Jakarta EE 11)
- **Maven** — build and dependency management
- **Lombok** — boilerplate reduction
- **Bean Validation** (jakarta.validation)
- **Thymeleaf** — server-side HTML templating
- **springdoc-openapi** — OpenAPI 3 / Swagger UI documentation
- **Spring Data JPA** (Hibernate) — persistence
- **H2** — file-based database (development)
- **PostgreSQL** — database for containerized and production runs
- **Spring Security** — authentication and role-based authorization
- **Docker & Docker Compose** — containerization and multi-container orchestration
- **RabbitMQ / Spring AMQP** — asynchronous messaging (event publish/consume)
- **Spring Boot HTTP Service Clients** — declarative, interface-based REST clients
- **Spring Boot Actuator / Micrometer** — health, metrics, structured logging

## Getting started

### Prerequisites

- JDK 21 or newer
- Maven 3.9+
- Docker Desktop — for the containerized runs below and the Testcontainers tests

### Run locally

```bash
mvn spring-boot:run
```

The application starts an embedded server on port 8080. Once running, try
`http://localhost:8080/api/students` to see the seeded data.

### Run the tests

```bash
mvn test
```

## Running with Docker

The service is packaged as an OCI image two ways — a hand-written multi-stage
Dockerfile and Cloud Native Buildpacks (`mvn spring-boot:build-image`, no
Dockerfile required). It runs as a multi-container Docker Compose stack
(Spring Boot + PostgreSQL), with application data persisted in a named volume
across restarts. For local development, Spring Boot's Docker Compose support
auto-provisions and wires a PostgreSQL container on `mvn spring-boot:run`.

### Required environment variables

Set these in your shell, or in a `.env` file in the project root (the full
stack reads `.env` automatically):

- `STUDENT_API_FIELD_KEY` — AES key for field encryption (16/24/32 chars)
- `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET` — OAuth2 client credentials

### Build the image

```bash
# Option A — multi-stage Dockerfile
docker build -t student-management-api:0.0.1 .

# Option B — Cloud Native Buildpacks, no Dockerfile
mvn spring-boot:build-image
```

### Run standalone (in-memory H2)

```bash
docker run -p 8080:8080 \
  -e STUDENT_API_FIELD_KEY -e GOOGLE_CLIENT_ID -e GOOGLE_CLIENT_SECRET \
  student-management-api:0.0.1
```

Data is ephemeral — with no Spring profile active, the app falls back to an
in-memory H2 database, which is lost when the container stops.

### Run the full stack (app + PostgreSQL, persistent)

The `docker-compose.prod.yml` stack references the application image by name,
so build it first (see **Build the image** above). PostgreSQL is supplied by
the stack itself — it runs as a container alongside the app, so no separate
database install is needed (the standalone and local-Maven runs use H2 instead).

```bash
docker compose -f docker-compose.prod.yml up
```

- App and PostgreSQL run as networked containers.
- Data persists in the `pgdata` named volume across restarts.
- Stop with `docker compose -f docker-compose.prod.yml down` (add `-v` to also
  delete the volume and its data).

### Local development

With the `spring-boot-docker-compose` dependency and `compose.yml` present,
`mvn spring-boot:run` automatically starts a PostgreSQL container and wires the
datasource for you — no manual connection configuration needed.

## API endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/api/students` | List all students |
| GET | `/api/students/{id}` | Get a single student by ID (e.g. `S001`) |
| GET | `/api/students/page/{page}` | Get a page of students (page size set via `student-api.default-page-size`) |
| POST | `/api/students` | Create a student (validated; returns 201 with a `Location` header) |
| POST | `/api/students/{studentId}/enroll/{courseCode}` | Enroll a student in a course |
| GET/POST | `/students` | Server-rendered HTML page: list students and add one (Thymeleaf) |

Write operations (`POST`) require authentication with the `ADMIN` role;
read operations (`GET`) are public. See **Security** below.

On startup, a few demo students and a course are seeded if not already
present (see `DomainServiceConfig`). By default, students are persisted to an
H2 file-based database and survive restarts (or to PostgreSQL when run via the
Docker stack — see **Running with Docker**); courses and enrollments are
currently held in-memory and reset on restart.

### API versioning

Student-detail endpoints are versioned via the `X-API-Version` header
(`1` or `2`; defaults to `1` if omitted). Version `2` adds derived fields
(`enrolledCourseCount`, `honorRoll`) not present in version `1`.

## Error handling

Errors are returned as [RFC 9457 Problem Detail](https://www.rfc-editor.org/rfc/rfc9457)
responses (`application/problem+json`), consistent across the API:

| Status | When |
|---|---|
| 400 Bad Request | Request body fails validation (includes a per-field `errors` map) |
| 404 Not Found | Student or course does not exist |
| 409 Conflict | Duplicate ID, or an enrollment conflict (already enrolled / course full) |

Stack traces are never included in error responses.

## Security

The application uses two authentication mechanisms for its two audiences:

- **JSON API** (`/api/students/**`) — HTTP Basic authentication. `GET` is
  public; `POST` requires the `ADMIN` role.
- **Web pages** (`/students`) — OAuth2 login via Google. Viewing the student
  list is public; adding a student requires signing in.

Authentication failures return **401 Unauthorized**; authenticated requests
lacking the required role return **403 Forbidden**.

> **Note:** API users are defined in-memory for development (see `SecurityConfig`);
> Google OAuth credentials are supplied via environment variables and never
> committed. Credentials are not documented here.

### Data protection

- **At rest:** sensitive fields (e.g. a student's national ID) are encrypted
  in the database via a JPA `AttributeConverter` (AES). The encryption key is
  supplied through the `STUDENT_API_FIELD_KEY` environment variable and is
  never committed. Stored values are ciphertext; the application works with
  plaintext transparently.
- **In transit:** HTTPS/TLS is available via the `https` profile
  (`mvn spring-boot:run -Dspring-boot.run.profiles=dev,https`), serving on
  port 8443 with a self-signed certificate for local development. Production
  deployments would use a CA-issued certificate.

## API documentation

Interactive API docs (Swagger UI) are available in development at
`http://localhost:8080/swagger-ui.html`, with the raw OpenAPI spec at
`/v3/api-docs`. Documentation endpoints are disabled in the `prod` profile.

## Messaging

Enrolling a student publishes a `StudentEnrolledEvent` to RabbitMQ, which a
listener consumes asynchronously — demonstrating event-driven communication
alongside the synchronous REST API. The enrollment HTTP call returns
immediately; the notification is handled on a separate thread, off the broker,
so the two are decoupled.

- **Broker:** RabbitMQ runs as a container (defined in `compose.yml`),
  auto-started and auto-wired via Spring Boot's Docker Compose support on
  `mvn spring-boot:run` — no connection configuration needed in development.
- **Queue:** `student.enrollment` (durable — messages survive a broker restart
  and wait until a consumer is available).
- **Serialization:** events travel as JSON (`JacksonJsonMessageConverter`), so
  payloads are readable and not tied to Java-only consumers.
- **Management UI:** browse `http://localhost:15672` (guest/guest) to inspect
  queues, message counts, and payloads.

### Synchronous service calls

Enrollment also makes a synchronous, declarative HTTP call to a (stubbed)
notification service, using Spring Boot 4's HTTP Service Clients: the
`NotificationServiceClient` interface is annotated with `@GetExchange` /
`@PostExchange`, registered via `@ImportHttpServices`, and Spring generates the
implementation — no hand-written client code. The base URL is externalized under
`spring.http.serviceclient.notifications.base-url`. This is the request-response
counterpart to the asynchronous RabbitMQ event: the HTTP call blocks for a
response, while the event is fire-and-forget.

## Observability

Operational state is exposed through observability's three pillars:

- **Actuator** — `/actuator/health` (public; includes liveness/readiness groups),
  with `/actuator/info` and `/actuator/metrics` behind authentication. Exposure is
  a deliberate, minimal allowlist (`management.endpoints.web.exposure.include`).
- **Metrics** — a custom Micrometer `students.created` counter, incremented in
  `InstrumentedStudentService` so the domain service stays untouched; read it at
  `/actuator/metrics/students.created`.
- **Structured logging** — the console stays human-readable while
  `logs/application.json` is written as ECS-format JSON
  (`logging.structured.format.file: ecs`) for log aggregators.

Distributed tracing (OpenTelemetry, under `management.opentelemetry.*`) is the
natural next step once the system spans multiple services.

## Testing

The project has a three-layer test suite (16 tests):

| Layer | Scope | Speed | Needs Docker? |
|---|---|---|---|
| **Unit** | Service logic with a mocked repository | Fast | No |
| **Web** (`MockMvc`) | Controllers, status codes, JSON, security — service mocked | Fast | No |
| **Integration** (Testcontainers) | Repository against a real PostgreSQL container | Slower | **Yes** |

### Running the tests

```bash
mvn test
```

**Prerequisites:**

- **Docker must be running** for the Testcontainers integration tests — they
  start a disposable PostgreSQL container. Verify with `docker ps` (an empty
  table means the daemon is reachable). The first run pulls the
  `postgres:16-alpine` image, so it will be slower; later runs reuse the cached image.
- **Environment variables** must be set for any test that boots the full
  application context (`STUDENT_API_FIELD_KEY`, and the OAuth/keystore values if
  those paths are exercised). Set them in the same terminal before running.

## Configuration

Application settings live in `src/main/resources/application.yml` under the
`student-api` prefix and are validated at startup — an out-of-range value
stops the app from booting.

| Property | Default | Constraint | Description |
|---|---|---|---|
| `student-api.default-page-size` | `20` | 1–100 | Students returned per page |
| `student-api.default-sort-field` | `name` | — | Reserved for future dynamic sorting (currently sorts by name) |
| `student-api.support-email` | — | valid email | Support contact address |
| `student-api.feature.audit-log.enabled` | `true` | true/false | Toggles audit logging of student queries |

### Profiles

Environment-specific settings live in profile files loaded alongside the base `application.yml`:

- `application-dev.yml` — verbose (`DEBUG`) logging; file-based H2 database and console
- `application-prod.yml` — quieter (`INFO`) logging; PostgreSQL settings that read credentials from environment variables, never committed

Activate a profile at launch:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Project structure

| Package | Contents |
|---|---|
| `com.studentmanagement` | Application entry point and new web-layer code (controllers, DTOs, config) |
| `com.javamastery.module10` | The capstone domain — copied unchanged from java-mastery |
| `com.javamastery.module10.persistence` | JPA entities and Spring Data repositories (added in the persistence refactor) |

## Roadmap

Built out module by module. Current status:

- [x] Project scaffolding
- [x] Domain service wired in as a Spring bean
- [x] REST API (DTOs, versioning, error handling)
- [x] Persistence (Spring Data JPA)
- [x] Security (OAuth2 / Google login)
- [x] Testing (JUnit, MockMvc, Testcontainers)
- [x] Containerization (Docker)
- [x] Messaging (RabbitMQ, async events)
- [x] Observability (Actuator, metrics, structured logging)

## License

TBD