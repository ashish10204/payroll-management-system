# Phase 5 — End-to-End Integration, Hardening, Deployment, and Demo

## Copilot Agent prompt

Implement **Phase 5 only**: integrate and harden the existing PayFlow application, verify the assessment's acceptance criteria, and prepare deployment/demo artifacts. Inspect the actual code and Git status first. Do not overwrite, reset, or discard my existing work. Do not claim a test or deployment succeeded unless it has actually been executed and verified.

Read the design documents, prior phase reports if present, and current README. Preserve the approved Angular + Java 21/Spring Boot + Oracle architecture unless a concrete issue requires a documented deviation.

## 1. End-to-end user journey

Verify and fix the complete primary workflow:
1. Start the application with the documented configuration.
2. Log in as a local/demo HR Manager using explicitly documented, non-production-only setup.
3. Open the dashboard and confirm metrics come from the backend.
4. Search, filter, sort, and paginate through the seeded employee directory.
5. Open an employee and inspect current salary and salary history.
6. Record a salary change effective today or in the future.
7. Verify the previous record is preserved and the timeline has no overlap.
8. Verify relevant analytics reflect the change and remain currency/frequency-correct.
9. Export a filtered report and verify its contents.
10. Log out and verify protected data cannot be accessed unauthenticated.

Add automated API/integration or end-to-end smoke tests for the critical parts where practical. Avoid brittle tests that depend on arbitrary sleeps, public internet, or uncontrolled external services.

## 2. Security review

Review and test:
- All employee, salary, audit, report, and export endpoints are protected as intended.
- Authentication secrets and database credentials are environment-driven and absent from source control.
- Passwords are stored only using an adaptive hash; demo credentials are isolated to a local/demo profile and are not production defaults.
- Session/JWT handling follows the chosen design, including cookie flags, CSRF, CORS, expiry, and logout as applicable.
- Request validation and error responses do not expose stack traces, SQL details, credentials, or sensitive values.
- Logs do not contain passwords, tokens, salary amounts, or sensitive request bodies.
- CSV formula injection mitigation is tested.
- Sort fields, page sizes, and export sizes are constrained.
- CORS allows only configured trusted origins, not arbitrary origins with credentials.

Fix issues introduced by the app's implementation and document any remaining risk honestly. Do not claim a professional security audit has taken place.

## 3. Data integrity and performance checks

- Verify the seed command creates exactly 10,000 fictional employees and the expected salary coverage.
- Verify seed reruns do not create duplicates and cannot accidentally run in production.
- Verify salary history boundaries, concurrent update protections, and rollback behavior.
- Verify reports separate currencies and pay frequencies.
- Check database-side pagination and aggregation; do not retrieve all employees into the UI for filtering or dashboard calculations.
- Review important Oracle indexes and queries. Run Oracle-backed migrations and smoke tests if Oracle is available. Record the database/version and exact commands used. H2 results alone are not Oracle validation.
- Avoid arbitrary performance claims. If measurements are made, document the environment, dataset, command, and observed result.

## 4. Build, tests, and developer experience

- Run all available backend unit tests, integration tests, frontend tests, lint checks, and production builds.
- Fix regressions and flaky or nondeterministic tests introduced by the implementation.
- Ensure a clean checkout can follow the README without undocumented manual changes.
- Provide Dockerfiles/Compose only if they improve reproducibility and are compatible with the actual Oracle setup. Do not imply that Oracle itself is bundled/licensed for every deployment configuration.
- Keep secrets out of `.env` examples; provide `.env.example` with placeholder values only and ensure actual local secret files are ignored by Git.
- Add or verify health/readiness checks and safe production configuration.

## 5. Deployment readiness

Prepare deployment configuration and instructions for the selected available platform. Document separately:
- Frontend build/deployment
- Backend build/deployment
- Oracle provision and connection configuration
- Required environment variables
- Migration execution
- Health-check and rollback considerations
- Seed command and the restriction to local/demo only

If credentials, a deployment platform, or Oracle access are unavailable, complete the reusable configuration and clearly identify the manual actions still required. Do not invent a public URL or claim the app is deployed.

## 6. Submission artifacts

Update the README so it includes:
- Project overview and architecture
- Feature list and explicit exclusions
- Prerequisites and verified tool versions
- Local setup and exact run commands
- Environment variable descriptions
- Oracle setup and migration instructions
- Exact seed command and expected counts
- Backend/frontend test and build commands
- API examples or API documentation location
- Deployment instructions and actual status
- Limitations and known issues
- Link to a demo recording only after an actual video exists

Update `docs/ai-usage.md` with the prompts used, what Copilot generated, human review performed, corrections made, and tests run. Record actual activity only; do not fabricate prompts/results. Update architectural/decision notes for any accepted deviations.

Review `docs/demo-script.md` and adjust it to match actual implemented behavior. Do not create or claim to have recorded a video unless a recording was actually produced.

## Exit criteria

Provide a checklist against every requirement in `docs/requirements.md`, labeling each as verified, partially verified, or not verified. Include links/path names for evidence where possible. Run tests/builds and report exact commands and results. Explicitly separate locally verified behavior from Oracle-only or deployment-only checks that could not be run.

Summarize all code/docs changes, remaining blockers, exact demo steps, and proposed commit message `test: verify end-to-end readiness and deployment`. **Stop for my review. Do not create a Git commit or deploy anything automatically.**
