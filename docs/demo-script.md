# PayFlow — Demo Script

**Status:** This is a structured walkthrough for a local or demo environment. No video recording or public deployment has been created in this environment.

## Before the demo

- Confirm the backend is available and the frontend is served locally.
- Use a fictional local/demo credential set rather than any production identity.
- Verify the app is running against the correct configuration and not against a shared environment.
- Ensure the CSV export and HR dashboard still reflect the latest data state.
- Keep secrets and environment values out of the recording and out of source control.

## Walkthrough

### 0:00–0:20 — HR workflow overview

Show the HR portal login and explain that this application tracks employee compensation and salary history in a controlled and auditable way.

### 0:20–0:45 — Dashboard review

Open the dashboard and highlight total headcount, country distribution, and compensation grouping by currency and pay frequency. Explain that the groupings are backend-driven and intentionally keep unlike currencies or pay frequencies separate.

### 0:45–1:15 — Employee directory search

Open the employee list, demonstrate filtering and navigation, and show the directory is built from the backend rather than static mock data.

### 1:15–1:50 — Salary history and update

Open a sample employee, review the current salary and history timeline, and enter a valid future-dated salary change with a reason. Confirm the record appears in the history without overwriting the prior entry.

### 1:50–2:20 — Reporting and CSV export

Open the compensation report view and show a filtered summary. Export the filtered employee directory or report to CSV and verify the file is spreadsheet-safe and includes the expected columns.

### 2:20–2:40 — Security and logout

Show that login is required and that protected routes are unavailable without valid credentials. Log out and confirm the session is closed.

## Completion checklist

- [x] Backend validation executed locally with Maven tests.
- [x] Frontend compilation verified with the Angular build.
- [x] Final requirement-by-requirement signoff completed for the Phase 5 handoff.
- [ ] Live Oracle database validation pending.
- [ ] Completed end-to-end demo recording pending.
- [ ] Production deployment not performed.
