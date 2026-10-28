# Phase 1 — Backend Foundation and Oracle Schema

## Copilot Agent prompt

Act as a senior Java/Spring Boot engineer. Implement **Phase 1 only** for PayFlow, the ACME employee compensation management application. Inspect the existing repository and the approved design documents before editing anything.

### Source of truth

Read:
- `docs/requirements.md`
- `docs/architecture.md`
- `docs/data-model.md`
- `docs/decisions.md`
- `README.md`

The required stack is Java 21, Spring Boot, Spring Data JPA/Hibernate, Oracle Database, Maven, and versioned Oracle-compatible migrations. Use modern Angular later; do not implement the frontend in this phase.

### Before coding

1. Summarize the current repository state and the Phase 1 implementation plan.
2. Choose compatible stable, non-deprecated dependency versions and pin them. Explain the selected Spring Boot and migration-tool versions.
3. Decide and document the authentication approach before implementing it. Prefer the simplest secure approach for the app's actual deployment topology; account for cookie security, CSRF, CORS, and local development. Do not create a fake authentication mechanism.
4. Confirm the Oracle version/driver assumptions and the required environment variables. Do not assume Oracle is available; identify which verification needs a real Oracle instance.

### Implement in this phase

1. Create a clean Maven Spring Boot project and a maintainable package structure organized by feature/module, such as `employee`, `compensation`, `reporting`, `security`, `audit`, and shared API concerns.
2. Add runtime configuration for Oracle JDBC through environment variables. Do not hardcode credentials or signing secrets. Set production-safe defaults and make local configuration straightforward.
3. Add one versioned migration tool (Flyway or Liquibase; choose one) and Oracle-compatible schema migrations for the approved logical model:
   - `COUNTRY`
   - `DEPARTMENT`
   - `EMPLOYEE`
   - `SALARY_RECORD`
   - `SALARY_AUDIT`
4. Include primary keys, foreign keys, unique/check constraints, and indexes justified by expected search, filter, salary timeline, and reporting access paths. Use Oracle-compatible syntax and types. Keep schema migrations separate from Hibernate schema generation; do not rely on `ddl-auto=create` or `update` for deployed environments.
5. Create the basic JPA entity mappings and repository interfaces needed to establish schema compatibility. Avoid exposing entities directly from the API. Do not implement the full employee or salary business endpoints yet.
6. Add application validation and a consistent exception/error-response foundation, preferably following the Spring version's support for RFC 9457 Problem Details.
7. Add the chosen authentication/security foundation, protect application APIs by default, and ensure secrets and salary values are not logged. Provide a clearly isolated development/demo configuration where needed. Do not commit real credentials or permanent default passwords.
8. Add a non-sensitive health endpoint and readiness/configuration notes. Health responses must not expose credentials, internal stack traces, or sensitive configuration.
9. Add unit tests for any logic implemented in this phase and a backend test/build setup that later phases can reuse.
10. Update the README with prerequisites, environment variable names (without secret values), how to run migrations, how to run tests, and how to start the backend.

### Quality constraints

- Keep controllers thin and business logic out of controllers.
- Use constructor injection.
- Use explicit, validated configuration properties where appropriate.
- Use Java `BigDecimal` for compensation values and an Oracle `NUMBER(p,s)`-appropriate mapping.
- Use date-only types for effective date values; do not accidentally convert them through UTC timestamps.
- Do not log passwords, tokens, salary amounts, or sensitive request bodies.
- Do not create demo employee data during application startup.
- Do not introduce microservices, Kubernetes, or unrelated infrastructure.

### Verify before finishing

Run every relevant available command and report the exact results, including:
- Maven compile/build
- Backend tests
- Migration validation or migration execution against Oracle, if an Oracle instance is available
- Any checks that could not be run and why

Do not claim Oracle verification if only compilation or H2-based tests were run. If no Oracle instance is available, provide the exact environment setup needed for the remaining smoke test and be clear about the limitation.

### Phase 1 exit criteria

- The backend builds from a clean checkout using documented steps.
- Dependencies and package structure are clear and maintainable.
- Oracle connection settings are environment-driven.
- Versioned Oracle migrations exist and are reviewed for compatibility.
- Core schema entities and repositories compile.
- Security and error handling foundations are in place.
- Tests are deterministic and passing for what was implemented.
- README setup steps reflect reality.

At the end, summarize files changed, architecture choices, commands and actual outcomes, unresolved limitations, and proposed commit message `feat: initialize backend and Oracle schema`. **Stop and wait for my review before starting Phase 2. Do not commit automatically.**
