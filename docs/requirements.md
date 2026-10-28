# PayFlow — Requirements

**Project:** ACME employee compensation management · **Primary user:** Authorized HR Manager · **Status:** Phase 0 — design

## Problem and goal

ACME manages compensation for approximately 10,000 employees across multiple countries using spreadsheets. This makes records difficult to search consistently, salary changes hard to audit, and organization-wide compensation questions time-consuming. PayFlow will provide a secure web application to manage employee and salary records and explore compensation data.

## In scope

- **Access control:** authenticated HR access; employee and salary APIs protected by backend authorization.
- **Employee directory:** create and update profiles; view details; search by employee number, name, or email; filter by country, department, job title, and employment status; server-side pagination and sorting.
- **Salary history:** view compensation as of a date; record amount, ISO currency code, pay frequency, effective date, and reason; preserve historical records and record who made changes.
- **Insights and exports:** headcount and status metrics; compensation statistics by country, department, and job title; counts, minimums, averages, maximums, and totals; filtering and CSV export.
- **Seed data:** deterministic, repeatable tooling for exactly 10,000 fictional employees, each with a current salary record and a subset with history.
- **Quality:** responsive, accessible UI; validation and useful loading, empty, and error states; automated tests; setup and deployment documentation.

## Acceptance criteria

1. Unauthenticated users cannot read employee or salary data; an authorized HR user can complete the main workflows.
2. A reviewer can search, filter, sort, and page through 10,000 seeded employees without loading the full directory into the browser.
3. Salary changes preserve previous records, validate effective dates, and commit atomically with audit information.
4. Reports never combine different currencies or pay frequencies in one aggregate; units are clearly labelled.
5. Seeding is deterministic, safe to rerun in a demo environment, and verifies the expected employee count.
6. Core business rules have automated tests; actual test and deployment verification status is documented honestly.

## Deliberately out of scope

Version one excludes payment execution, tax/statutory calculations, payslips, employee self-service, external HR or banking integrations, foreign-exchange conversion, bulk spreadsheet import, and multi-level approvals. These add policy, compliance, or integration complexity beyond the main goal of reliable compensation management and insight. User-entered retroactive salary corrections are also excluded; changes may take effect today or in the future.

## Constraints and assumptions

- **Frontend:** modern Angular with Angular Material (not legacy AngularJS 1.x).
- **Backend/database:** Java 21, compatible pinned Spring Boot release, REST/JSON, and Oracle Database with versioned migrations.
- **Architecture:** modular monolith; 10,000 records alone do not justify microservices.
- **Compensation semantics:** effective-dated salary history; aggregate only within matching currency and pay frequency unless a documented normalization policy is later introduced.
- **Sensitive data:** fictional seed records, authenticated APIs, least-privilege access, environment-managed secrets, and no salary/password/token values in application logs.
