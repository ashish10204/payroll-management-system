# PayFlow — Design Decisions and Trade-offs

This document records the initial decisions for the take-home assessment. Revisit any decision when implementation evidence or recruiter clarification changes the constraints.

## ADR-001: Modular monolith instead of microservices

**Decision:** Keep one Spring Boot deployment organized into employee, compensation, reporting, security, and audit modules.

**Reasoning:** The known scale is 10,000 employees, which is modest for a properly indexed relational database. A monolith reduces deployment and operational overhead, keeps salary changes transactionally straightforward, and makes the end-to-end workflow easier to test.

**Trade-off:** Modules are deployed and scaled together. Reconsider separation only if independently scaling workloads, ownership boundaries, or reliability requirements emerge.

## ADR-002: Angular + Spring Boot + Oracle

**Decision:** Use modern Angular with Angular Material, Java 21/Spring Boot, and Oracle Database.

**Reasoning:** This stack fits the requested Java backend and Oracle SQL preferences and demonstrates a conventional typed SPA + REST API + relational database design.

**Trade-off / follow-up:** The assignment's frontend wording says “ReactJS or NextJS” and “AngularJS for Java,” which is ambiguous and may refer to modern Angular imprecisely. Confirm the intended frontend constraint with the recruiter if possible. Do not use legacy AngularJS 1.x for a new application.

## ADR-003: Oracle migrations are the source of truth

**Decision:** Use one versioned migration tool (Flyway or Liquibase, selected before implementation) with Oracle-compatible SQL. Keep Hibernate schema generation disabled outside disposable experiments.

**Reasoning:** Repeatable schema changes and realistic Oracle validation are important for submission reliability.

**Trade-off:** Oracle setup adds local-development friction. Document the required Oracle image/version, licence/usage prerequisites where applicable, environment variables, and an explicit migration/seed procedure.

## ADR-004: Effective-dated salary history

**Decision:** Store every salary period as a separate record. A change does not overwrite the previous amount.

**Reasoning:** HR needs to answer current and historical compensation questions and understand who changed a salary and why.

**Trade-off:** Correct handling of future-effective changes is more complex than updating a single salary column. Use non-overlapping inclusive date ranges, a transaction, same-employee locking, audit records, and deterministic edge-case tests. Historical corrections are excluded from v1 to constrain the workflow.

## ADR-005: Backend-side pagination and aggregation

**Decision:** Search/list endpoints are paginated on the server; dashboard/report statistics are calculated with Oracle queries.

**Reasoning:** This limits response size, avoids unnecessary browser work, and keeps reporting behavior consistent as the dataset grows.

**Trade-off:** Filtering and sorting must be explicitly supported by the API and tested. Only whitelisted sort fields are accepted; query plans should be checked against seeded data.

## ADR-006: Preserve currency and pay-frequency boundaries

**Decision:** Every compensation aggregate is grouped by ISO currency and pay frequency. Do not implement exchange conversion or annualization in v1.

**Reasoning:** INR, USD, hourly pay, monthly pay, and annual pay cannot be summed into an honest single total without explicit conversion and normalization policies.

**Trade-off:** The UI shows separate totals/series, which may be less visually simple than one headline number but is materially more accurate.

## ADR-007: Salary access is protected at the API

**Decision:** Require authentication and backend authorization for employee and compensation operations. Use environment-managed secrets, password hashing, careful token/session handling, and non-sensitive logs.

**Reasoning:** Salary information is confidential. A hidden frontend button is not an authorization boundary.

**Trade-off:** Authentication and local-demo setup require additional configuration. Any demo account must be fictional, configured for a local/demo profile, and documented as non-production only.

## ADR-008: Oracle validation cannot be replaced by H2 alone

**Decision:** Unit tests may use mocks where useful, but schema, locking, migrations, constraints, seed behavior, and critical query tests need an Oracle verification path.

**Reasoning:** SQL dialect and concurrency behavior can differ between in-memory databases and Oracle.

**Trade-off:** Oracle integration tests require a running Oracle instance and are slower than unit tests. Keep the fast unit suite separate from the explicitly documented Oracle smoke/integration suite.

## ADR-009: Deliberate v1 scope

**Decision:** Exclude payment execution, taxes/deductions, payslips, FX conversion, external HR integrations, employee self-service, bulk spreadsheet import, and multi-level approvals.

**Reasoning:** These features expand business-policy, compliance, and integration scope. They are not required to demonstrate dependable salary-data management and insights.

**Trade-off:** The application is a compensation-management tool, not a complete payroll engine. State this clearly in the UI/README and demo.

## ADR-010: Incremental AI-assisted development

**Decision:** Use Copilot Agent mode in reviewed milestones; ask it to inspect files, make a focused change, run tests, report limitations, and stop for review. Commit only after review.

**Reasoning:** Smaller diffs are easier to reason about, test, and explain in an interview. AI-generated output must be treated as a proposal, not evidence of correctness.

**Trade-off:** The process may require more review cycles than a single broad generation prompt, but it creates a stronger and more verifiable engineering record.
