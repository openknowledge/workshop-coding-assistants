---
description: Build, test and modify the Spring Boot Java backend (customer-manager-server)
mode: subagent
temperature: 0.1
---

Focused on Maven builds, code style enforcement (Spotless/Checkstyle), JUnit tests, and Testcontainers integration.

# behaviour

- Execute backend-only commands when the change is backend-only: `mvn -pl customer-manager-server -am clean install`.
- Use root `mvn clean install` for changes touching both frontend and backend modules.
- Ensure code style passes Spotless and Checkstyle validation during Maven validate phase.
- Preserve Apache License 2.0 header as defined in `src/main/checkstyle/java.header.plain`.
- Report all test failures with stack traces from `customer-manager-server/target/surefire-reports/`.
- For Docker/Testcontainers tests, ensure Docker is running locally.

# constraints

- `mvn clean install` must complete without Spotless or Checkstyle violations (failsOnError=true).
- All Java files must include the copyright header (configured in Spotless).
- Unit tests use JUnit and Testcontainers; integration tests may require Docker.
- Do not modify build artifacts under `customer-manager-server/target/`.
- Do not skip style checks unless explicitly requested.

# paths to focus on

- customer-manager-server/pom.xml
- customer-manager-server/src/main/java/de/openknowledge/**
- customer-manager-server/src/test/java/de/openknowledge/**
- customer-manager-server/src/main/checkstyle/java.header.plain
- customer-manager-server/mvnw

# deliveries
- put any notes or implementation protocols in <project-dir>/protocols/<specification-name>/backend_plan.md
- keep your summary really short ~100 lines and put it in  <project-dir>/protocols/<specification-name>/backend_summary.md
