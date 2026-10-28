# PayFlow — Proposed Demo Script (2–3 minutes)

**Status:** Planned walkthrough only. This document is not evidence that a demo has been recorded or deployed.

## Before recording

- Start the backend and Angular UI against a designated demo Oracle database.
- Run the documented seed command and verify exactly 10,000 employee records.
- Use only fictional data and a local/demo account configured outside source control.
- Confirm login, search, salary update, analytics, and export work with the final build.
- Hide secrets and unrelated personal information from the recording.

## Walkthrough

### 0:00–0:20 — Introduce the problem

“PayFlow replaces spreadsheet-based employee compensation tracking with a searchable, auditable HR application. The current demo uses 10,000 fictional employees and preserves salary history.”

Show the sign-in screen and log in using the documented local/demo account.

### 0:20–0:45 — Dashboard and data quality

Show headcount and employment-status metrics. Point out that compensation metrics are grouped by currency and pay frequency, so unlike currencies or different pay periods are not combined into a misleading total.

### 0:45–1:15 — Find an employee

Open the directory, search for an employee number or name, and demonstrate one or two filters. Change pages or sort the results to show server-side pagination rather than loading all records at once.

### 1:15–1:50 — Inspect and change compensation

Open an employee detail page and review the current salary and prior salary history. Schedule a valid current/future salary change with a reason. Save it, then show the new timeline and the preserved prior record. Explain that the backend validates effective dates and saves the change and audit information transactionally.

### 1:50–2:20 — Answer an HR question

Open compensation reports and group statistics by department or country. Show the currency/pay-frequency labels and apply a filter. Export the filtered report to CSV and, if time permits, open the file.

### 2:20–2:45 — Engineering evidence

Briefly show the repository structure, the requirements/architecture notes, the seed command and test commands in the README, and the incremental commit history. Report the real test and Oracle verification results; do not claim checks that were not run.

## Completion checklist

- [ ] Demo run completed against the final build.
- [ ] Seeded employee count verified as 10,000.
- [ ] Login and protected APIs verified.
- [ ] Search/filter/pagination verified.
- [ ] Salary history and effective-date behavior verified.
- [ ] Currency/pay-frequency grouping verified.
- [ ] CSV export opened and checked.
- [ ] Recording saved and its shareable link checked.
