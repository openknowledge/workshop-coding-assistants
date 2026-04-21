Core facts:
- This is a Maven multi-module project with two modules: customer-manager-client and customer-manager-server (see root pom.xml modules list).
- Preferred full-build (correct module order + checks): run from repo root: `mvn clean install`.

Constraints:
- Stay in your repository; do NOT use /tmp/ directory
- Specifications must be written to ./specifications/ directory
- Implementation plans must be written to ./specifications/ directory
- Do not access /tmp/ for any file operations

Frontend (customer-manager-client)
- Client is a Vite + React + TypeScript app with npm scripts in customer-manager-client/package.json.
- Run frontend for development:
  - `cd customer-manager-client && npm run dev > ./tmp/client.log` (it only need to run once and then supports hot reload)

Backend (customer-manager-server)
- Server is Spring Boot (Java 21). Prefer `mvn` from repo root; server also contains a Maven wrapper at customer-manager-server/mvnw if you need to build server only.
- Run backend for development:
  - run the database, migration and backend in dev mode: `docker compose up database flyway -d && cd customer-manager-server && ./mvnw spring-boot:run > ./tmp/server.log`
- Code style is enforced during Maven validate: Spotless and Checkstyle run and are configured to fail the build (failsOnError=true). Don't skip these unless explicitly asked.
- License/header requirement: Spotless uses src/main/checkstyle/java.header.plain — preserve header if editing Java files.

Build & test ordering gotchas
- Root `mvn clean install` is the reproducible path CI uses and respects module ordering (client -> server). Use it for changes that touch both modules or to reproduce CI failures.
- If you only change frontend code, you can run client npm commands, but the server's final artifact served by Docker will expect the client dist to be packaged (so root mvn needed for full end-to-end verification).

Files and directories an agent should not modify automatically
- Do not modify generated/build artifacts under `*/target/` or `customer-manager-client/dist/`.
- Avoid changing CI files (.gitlab-ci.yml) or publishing credentials/tokens unless explicitly instructed.

Quick troubleshooting checks an agent should run first
- If a Maven build fails: inspect `customer-manager-server/target/surefire-reports/` and `target/checkstyle-result.xml` for failing tests or style violations.
- If frontend tests fail: run `cd customer-manager-client && npm run test -- --reporter=verbose` and check `customer-manager-client/src/test/setup.ts` for test harness issues.

References (executable sources of truth)
- Root pom.xml (module order)
- customer-manager-client/package.json (npm scripts)
- customer-manager-server/pom.xml (spotless/checkstyle & testcontainers)
- .gitlab-ci.yml (how CI reproduces integration tests)

call the requirements engineer, before asking questions to the human by yourself!!! 

Agent workflow for feature implementation (USE THIS WORKFLOW AND AGENTS FOR EVERY FEATURE):
- **Lead-Dev (primary)**: Main orchestrator for feature work. It checks `./specifications/` first, calls **Requirements-Engineer** when a specification is missing, delegates implementation to backend/frontend, and verifies implementation against the specification.
- **Requirements-Engineer** (can be skipped if specification in `./specifications/` already exists): Gather, analyze, and document structured requirements answering who/what/how/test — translating business needs into clear, testable specifications.
- let the following agents run in parallel:
  - **Backend**: Reads specification and implementation plan; implements backend services/repositories/controllers and writes unit/integration tests.
  - **Frontend**: Reads specification and implementation plan; implements components, pages, API integration, and unit tests.

Lead-Dev operating constraint:
- Lead-Dev does not write feature code directly; it only delegates implementation to subagents and validates delivered work against the specification.

Repository meta-agent policy
- Whenever you have something releveant for the ai coding tools, tell the opencode-meta agent to update: AGENTS.md, opencode.json (root), or any file under `.opencode/`.
