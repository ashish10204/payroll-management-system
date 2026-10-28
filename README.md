# PayFlow — Employee Salary Management

PayFlow is a proposed full-stack compensation-management application for ACME, designed for an HR Manager who needs to maintain employee profiles, preserve salary history, and answer compensation questions for an organization with 10,000 employees across multiple countries.

> **Current status:** Phase 0 documentation only. Application code, Oracle migrations, tests, seed data, deployment, and demo recording have not yet been implemented or verified.

## Planned technology

- Frontend: modern Angular + Angular Material
- Backend: Java 21 + Spring Boot REST API
- Database: Oracle Database
- Persistence and migrations: Spring Data JPA/Hibernate plus a versioned Oracle migration tool
- Testing: JUnit 5, Mockito, Spring web tests, and Angular tests

Pinned versions and exact local commands will be added after the project scaffold is generated and verified.

## Scope

The first version is planned to include secure HR access, employee directory management, effective-dated salary records and history, server-side search/filter/pagination, currency-aware compensation analytics, CSV export, and deterministic seed data for exactly 10,000 fictional employees.

Payroll execution, tax calculations, payslips, employee self-service, foreign-exchange conversion, external HR integrations, bulk spreadsheet import, and complex approval workflows are deliberately excluded from v1.

## Documentation

- [Requirements](docs/requirements.md)
- [Architecture](docs/architecture.md)
- [Data model](docs/data-model.md)
- [Design decisions and trade-offs](docs/decisions.md)
- [AI usage log](docs/ai-usage.md)
- [Proposed demo script](docs/demo-script.md)


## Copilot implementation prompts

Use the reviewed, phase-by-phase prompts in [`docs/prompts/`](docs/prompts/README.md). Run Phase 1 through Phase 5 in order, review each diff, execute the tests, and commit only after you approve each milestone. These prompts guide implementation; they do not indicate that the corresponding code or verification has already been completed.

## Planned delivery checklist

- [ ] Implement backend and Oracle migrations
- [ ] Implement employee and compensation APIs
- [ ] Add deterministic 10,000-employee seed tooling
- [ ] Implement analytics and CSV export
- [ ] Build Angular UI and connect the API
- [ ] Run automated tests and Oracle verification
- [ ] Add environment-specific setup/deployment instructions
- [ ] Deploy and verify the application
- [ ] Record and verify a demo video

## Security note

Use fictional data only. Do not commit credentials, signing keys, real employee information, or production configuration. No demo credentials or run commands are claimed until the corresponding implementation exists.
