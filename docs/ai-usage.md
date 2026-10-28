# PayFlow — AI Usage Log

## Purpose

This file records the actual AI-assisted work performed on this repository, including the prompt scope, files reviewed, human follow-up, and verification commands that were executed.

## Actual activity log

### Entry 1

- **Date:** 2026-10-29
- **Tool/model and mode:** GitHub Copilot Agent mode, MAI-Code-1.1-Flash
- **Milestone:** requirements validation and Phase 1 backend foundation
- **Prompt summary:** Review the repository design docs and implementation prompts before coding, then begin only with the backend foundation and Oracle-compatible schema work.
- **Files changed:** `pom.xml`, `src/main/resources/application.properties`, `src/main/resources/db/migration/V1__initial_schema.sql`, project backend package structure
- **AI contribution:** Proposed the backend package layout, dependency version alignment, and initial schema design consistent with the approved documents.
- **Human review:** Confirmed the design documents were the ground truth; corrected the Oracle JDBC version, schema needs, and project structure before approving the backend foundation.
- **Verification:** `mvn test` executed successfully after the backend foundation; reported `BUILD SUCCESS` with the test suite green.
- **Known limitations:** Oracle runtime verification was not possible without a live Oracle instance.
- **Commit:** `3f3875a feat: initialize backend and Oracle schema`

### Entry 2

- **Date:** 2026-10-29
- **Tool/model and mode:** GitHub Copilot Agent mode
- **Milestone:** Phase 2 employee and salary APIs + seed validation
- **Prompt summary:** Implement the employee/salary API layer and seed support without broadening scope beyond the approved backend work.
- **Files changed:** employee and salary service/controller classes, repositories, seed service, tests
- **AI contribution:** Generated DTOs, service logic, validation flow, and seed/demo support aligned to the backend requirements.
- **Human review:** Verified salary date rules, test fixtures, and repository usage; corrected the business-date logic to use a fixed Clock in tests.
- **Verification:** `mvn test` ran successfully for the Phase 2 work; later reruns remained green with the Phase 3 additions.
- **Known limitations:** Local verification used H2-backed tests rather than an Oracle database.
- **Commit:** `4202dc0 feat: add employee salary seed backend validation`

### Entry 3

- **Date:** 2026-10-29
- **Tool/model and mode:** GitHub Copilot Agent mode
- **Milestone:** Phase 3 analytics and CSV export
- **Prompt summary:** Implement headcount/compensation reporting and protected CSV export behavior, then validate formula injection and business-date rules.
- **Files changed:** reporting package, export logic, `Phase3ReportingTest.java` and later renamed test file
- **AI contribution:** Generated reporting service/controller code and export neutralization logic.
- **Human review:** Fixed the business clock mismatch in tests and adjusted CSV sanitization so formula marks are neutralized rather than preserved.
- **Verification:** `mvn test` reported `BUILD SUCCESS` with `Tests run: 7, Failures: 0, Errors: 0, Skipped: 0`.
- **Known limitations:** Production Oracle behavior remains unverified without a live Oracle database.
- **Commit:** `610446f feat: add analytics and CSV export backend`

### Entry 4

- **Date:** 2026-10-29
- **Tool/model and mode:** GitHub Copilot Agent mode
- **Milestone:** Phase 4 Angular frontend foundation
- **Prompt summary:** Scaffold Angular, connect to the real backend contract, build login/dashboard/employee views, and verify the app compiles.
- **Files changed:** `frontend/*` application files, Angular Material setup, API service layer, auth guard, login/dashboard/employee components
- **AI contribution:** Generated the Angular app shell, routes, auth flow, typed API service, and initial UI screens.
- **Human review:** Confirmed the app compiles with the real dependencies, adjusted the starter template, and kept the frontend tied to backend endpoints rather than mock-only data.
- **Verification:** `cd frontend; npm install; npm run build` completed successfully and produced the Angular bundle.
- **Known limitations:** End-to-end browser interaction and deployment were not exercised in this environment.
- **Commit:** `62a6cab feat: build Angular HR portal`

### Entry 5

- **Date:** 2026-10-29
- **Tool/model and mode:** GitHub Copilot Agent mode
- **Milestone:** Phase 5 documentation and readiness review begin
- **Prompt summary:** Update the project documentation to match the verified implementation status, list actual commands run, and record honest deployment/Oracle limitations.
- **Files changed:** `README.md`, `docs/ai-usage.md`, `docs/demo-script.md`
- **AI contribution:** Drafted the README refresh and actual activity log entries based on the verified commands and implemented milestones.
- **Human review:** Confirmed the status wording reflects the real local verification and does not over-claim Oracle or production deployment.
- **Verification:** Documentation update itself is source-controlled; no automated test was required for text-only changes.
- **Known limitations:** Final end-to-end deployment and Oracle smoke testing remain pending live-system access.
- **Commit:** Not created. This is a review/preparation step only.
