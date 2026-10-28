# Phase 2 — Employee, Compensation, Salary History, and Seeding

## Copilot Agent prompt

Implement **Phase 2 only** for PayFlow using the reviewed Phase 1 backend. Start by inspecting the actual code, tests, schema migrations, security setup, and design documents. Do not rewrite working foundations just to match personal preferences; identify necessary changes and explain them first.

Read `docs/requirements.md`, `docs/architecture.md`, `docs/data-model.md`, and `docs/decisions.md` before implementation. Backend stack: Java 21, Spring Boot, Spring Data JPA, Oracle Database, and Maven.

## Scope

Implement authenticated employee management and compensation-history workflows. Do not implement analytics endpoints or the Angular frontend in this phase.

### 1. Employee APIs

Provide REST endpoints under `/api/v1`, including:
- Create employee
- Get employee detail
- Update editable employee profile fields
- Change employment status
- Search/list employees with server-side pagination, allowlisted sorting, and filters for country, department, job title, and status
- Search by employee number, name, and email where appropriate

Use request/response DTOs, validation annotations, explicit mapping, and consistent Problem Details/error responses. Do not return persistence entities directly. Enforce unique employee number and normalized email. Prevent hard deletion of employees with compensation history; use employment status instead.

Define and document a consistent page response contract. Bound page sizes. Whitelist sortable fields rather than interpolating user-provided names into SQL. Ensure search/filter combinations behave predictably and return stable ordering.

### 2. Salary and history APIs

Provide endpoints to:
- Read the current salary as of a supplied date (default to the configured business date)
- Retrieve an employee's chronological salary history
- Record a salary change effective today or in the future
- Retrieve the audit trail for salary changes, subject to HR authorization

Each salary record must contain amount (`BigDecimal`), ISO currency code, pay frequency, inclusive effective start/end dates, reason, creator, and timestamp. Validate positive amounts, supported currency codes, supported pay frequencies, nonblank bounded reasons, and dates.

### 3. Effective-date correctness — critical

Follow the period semantics in `docs/data-model.md`: inclusive `EFFECTIVE_FROM` and `EFFECTIVE_TO`; null end means no known end. For any employee and date, at most one salary record may match.

When recording a salary change effective on date D:
- Reject retroactive user-entered changes where D is before the configured business date.
- Validate and adjust the timeline within a single transaction.
- Preserve all unaffected history and future-dated records.
- Close any prior record that overlaps the new period to the day before D where appropriate.
- If the change is inserted before an already scheduled future record, set the new record's end to the day before that next record.
- Ensure the timeline has no gaps or overlaps caused by the change, according to the documented policy.
- Record the salary change and audit event atomically; any failure must roll back both.
- Serialize concurrent changes for one employee, for example by locking the employee row inside the transaction. A unique employee/start-date key alone does not prevent all overlaps.

Do not implement these rules only in the controller. Keep the business logic in a testable service. Use an injected `Clock` or equivalent approach so date-sensitive tests are deterministic. Use one documented business timezone.

### 4. Security

Verify that all employee, salary, and audit endpoints require an authenticated HR Manager. Use the Phase 1 security approach, not a second competing scheme. Tests must prove unauthenticated access is denied and authorized access works. Do not rely on UI visibility as authorization.

### 5. Deterministic seed data for exactly 10,000 employees

Implement a dedicated, explicitly invoked seed mechanism suitable for local/demo environments only. It must:
- Generate exactly 10,000 fictional employees with stable employee numbers and stable, deterministic attributes.
- Use a fixed random seed or fully deterministic generation rules.
- Cover several countries, departments, job titles, employment statuses, and salary currencies.
- Create at least one valid current salary record per employee and historical salary records for a deterministic subset.
- Respect the effective-date rules and never create overlapping salary periods.
- Be safe to rerun without duplicating records. Use stable keys/upserts or a documented clean-and-reseed strategy guarded to demo/local profiles only.
- Use batch persistence/flush strategies and avoid one insert per network round trip where possible.
- Never run automatically against production or use real personal data.
- At completion, query and verify the employee and salary-record counts and fail clearly if the expected invariant is violated.

Document the command and required profile/environment safeguards. Keep seed orchestration out of the normal application startup path.

### 6. Tests

Add focused, deterministic tests for:
- Employee create/update validation and duplicate employee number/email
- Search, filters, sorting allowlist, pagination bounds, and stable ordering
- Current salary as of dates before, on, and after effective boundaries
- Salary change effective today
- Scheduling a future salary and inserting a change before a later scheduled change
- Invalid retroactive dates, amount, currency, frequency, and reason
- Prevention of overlaps and behavior under concurrent update attempts where practical
- Atomic rollback if audit persistence fails
- Authorization for sensitive endpoints
- Seeder determinism, exactly 10,000 employees, salary coverage, and idempotency

Use JUnit 5, Mockito, and MockMvc (or the project's established equivalents). Mock external boundaries rather than the service under test. If database semantics require integration tests, add an Oracle-backed path or clearly mark the tests that require Oracle. Do not use an in-memory database as proof of Oracle behavior.

### Verification and exit criteria

Run the relevant Maven build and tests and report exact commands/results. Validate the seeder against Oracle if available; otherwise clearly state what remains unverified. Do not fabricate results.

Before stopping, report endpoints with example request/response shapes, important business rules, files changed, test coverage summary, known limitations, and the proposed commit message `feat: implement employee and salary management APIs`. Update the README and design artifacts when needed. **Stop for my review before Phase 3. Do not commit automatically.**
