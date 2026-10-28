# PayFlow — AI Usage Log

## Purpose

Record how AI tools accelerate implementation while keeping engineering decisions, verification, and final responsibility with the developer. This file is a template and must be completed truthfully as work happens. Do not claim a prompt was run, a test passed, or a change was reviewed unless that actually occurred.

## Working principles

- Give the agent one bounded milestone at a time and point it to the approved requirements and design documents.
- Ask it to inspect the repository before changing files and to explain its plan before implementation.
- Review the diff for security, correctness, maintainability, and consistency with the architecture.
- Run tests and builds locally. Verify sensitive business rules manually or with explicit tests.
- Never paste real credentials, production data, personal employee data, or secrets into AI prompts.
- Do not accept generated dependencies, APIs, or configuration without verifying that they exist and are compatible with the pinned versions.
- Record failures and corrections as honestly as successes.

## Prompt and verification log

Add one entry for each meaningful AI-assisted milestone, using this template.

### Entry template

- **Date:** `YYYY-MM-DD`
- **Tool/model and mode:** e.g. GitHub Copilot Agent mode; record the actual tool/model shown.
- **Milestone:** e.g. requirements, Oracle schema, salary history service, Angular employee list.
- **Prompt summary:** What was requested? Link to a saved prompt file or include the exact prompt where useful.
- **Files changed:** List the paths reviewed.
- **AI contribution:** What code, tests, documentation, or alternatives were suggested/generated?
- **Human review:** What did I inspect, change, reject, or clarify?
- **Verification:** Exact commands run and actual pass/fail results.
- **Known limitations:** What remains unverified, including Oracle-specific or deployment checks?
- **Commit:** Actual commit hash/message, once committed.

---

## Actual activity log

_No implementation activity is recorded here yet. Add entries after each real Copilot-assisted session; do not backfill invented results._
