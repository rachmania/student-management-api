# student-management-api

A Spring Boot 4 REST API for managing students and courses.

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

## Getting started

### Prerequisites

- JDK 21 or newer
- Maven 3.9+

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

## API endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/api/students` | List all students |
| GET | `/api/students/{id}` | Get a single student by ID (e.g. `S001`) |

On startup, a few demo students and a course are seeded in-memory
(see `DomainServiceConfig`), so these endpoints return data immediately.
Data is not yet persisted — it resets on every restart until Spring Data
JPA is introduced later in the build.

## Project structure

| Package | Contents |
|---|---|
| `com.studentmanagement` | Application entry point and new web-layer code (controllers, DTOs, config) |
| `com.javamastery.module10` | The capstone domain — copied unchanged from java-mastery |

## Roadmap

Built out module by module. Current status:

- [x] Project scaffolding
- [x] Domain service wired in as a Spring bean
- [ ] REST API (DTOs, versioning, error handling)
- [ ] Persistence (Spring Data JPA)
- [ ] Security (OAuth2 / Google login)
- [ ] Testing (JUnit, MockMvc, Testcontainers)
- [ ] Containerization (Docker)
- [ ] Observability (Actuator, metrics, structured logging)

## License

TBD