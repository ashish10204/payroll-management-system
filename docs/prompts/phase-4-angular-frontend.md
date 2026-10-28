# Phase 4 — Angular HR Portal

## Copilot Agent prompt

Implement **Phase 4 only**: build the PayFlow frontend using modern Angular and Angular Material and connect it to the real Spring Boot APIs. Start by inspecting the repository, the actual API routes/DTOs, security approach, and approved documentation. Do not assume endpoints exist if they are not present; report any backend gaps before building around them.

Read `docs/requirements.md`, `docs/architecture.md`, `docs/data-model.md`, and `docs/decisions.md`. Use the existing backend contract as the source of truth. Do not use hardcoded mock dashboard metrics as a substitute for real API integration.

## Frontend foundation

- Use the Angular CLI/version chosen and pinned in the repository, compatible with the installed Node.js version.
- Use TypeScript strict mode and Angular Material for tables, buttons, dialogs, navigation, forms, date controls, and feedback.
- Use a clear feature structure: `core` (authentication, HTTP, interceptors, shared configuration), `shared` (reusable controls/pipes), `features/dashboard`, `features/employees`, `features/compensation`, and `features/reports`.
- Use typed API services and typed request/response models. Keep HTTP calls out of components where practical.
- Use reactive forms and client-side validation for good UX, but treat server-side validation as authoritative.
- Configure environment-specific API base URLs. Do not hardcode production hostnames, credentials, or tokens.
- Respect the backend's actual session/cookie or authentication design, including CSRF protection, credentialed requests, and logout. Do not store tokens in local storage unless the reviewed security design explicitly requires and justifies it.
- Handle API errors consistently and never display internal stack traces or sensitive server details.

## Screens and workflows

### 1. Authentication

Provide a login screen and session state based on the existing backend auth flow. Show an actionable message for invalid login/session expiry. Protect routes from unauthenticated navigation, while remembering that backend authorization remains the real security boundary.

### 2. Dashboard

Show real API-backed employee headcount/status metrics and compensation summaries with their currency and pay frequency visible. Use charts only when they clarify a question. Do not combine unrelated currencies or frequencies into one chart value. Include loading, empty, and error states.

### 3. Employee directory

Provide:
- Search by employee number, name, or email
- Filters for country, department, job title, and employment status
- Server-side pagination and sorting
- Clear current-filter display and reset action
- Empty/error/loading states and accessible table controls
- A create-employee form and editable employee details

Do not download all 10,000 records to the browser. Keep page/filter/sort state synchronized with API calls and make sure filter changes reset pagination appropriately.

### 4. Employee detail and salary history

Show employee profile, current compensation as of the business date, and chronological salary records. Include effective dates, currency, pay frequency, and change reason. Make clear whether a record is current, ended, or scheduled in the future.

Provide a salary-change form with amount, ISO currency code, pay frequency, effective date, and reason. Display backend validation errors inline. Confirm before submission where useful. After a successful update, refresh the employee history and any visible dashboard/report data. Never suggest that a salary change succeeded if the API rejected it.

### 5. Compensation reports and CSV export

Provide report filters matching the actual API capabilities. Display currency and pay frequency for every monetary result. Use the backend's export endpoints and filters rather than reconstructing CSV data from the current page or creating a separate inconsistent calculation in the browser.

## Usability and accessibility

- Create a polished, coherent HR admin interface appropriate for a hiring assessment.
- Support desktop and tablet layouts and a usable mobile layout.
- Use semantic headings, labels, visible focus indicators, keyboard-accessible controls, accessible dialog behavior, and sufficient contrast.
- Format dates consistently without changing the meaning of date-only effective dates.
- Format decimal salary amounts without floating-point conversion or misleading currency symbols.
- Provide loading indicators, empty states, useful error messages, and success confirmations.
- Do not add decorative features that distract from the core workflows.

## Tests

Add meaningful tests using the project's supported Angular testing framework. Cover:
- API service request mapping and error handling
- Login/session-state handling appropriate to the backend auth method
- Employee form validation
- Directory search/filter/pagination interactions and request parameters
- Salary-change form validation and backend validation errors
- Salary history rendering, including future-dated records
- Correct currency/frequency labels on analytics
- Loading, empty, and error states
- CSV export initiation and preservation of active filters

Use test doubles only at frontend boundaries, not as evidence that the backend works. Keep tests deterministic and do not depend on network services.

## Verification and exit criteria

Run frontend install/build/lint/test commands available in the repository and report exact results. Verify cross-origin/session/CSRF behavior with the documented backend configuration where possible. Do not claim a feature works end-to-end unless it has been exercised against the actual API.

Update README setup instructions and include the exact commands to run the frontend and backend locally. Summarize routes/screens, API dependencies, test results, known gaps, and proposed commit `feat: build Angular HR portal`. **Stop for review before Phase 5. Do not commit automatically.**
