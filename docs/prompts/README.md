# Copilot Implementation Prompts

This folder contains the prompts to implement PayFlow incrementally after completing and reviewing the Phase 0 design documents.

## How to use

1. Open the repository in VS Code and start GitHub Copilot Chat in **Agent mode**.
2. Read the prompt file for the next phase and paste its contents into Copilot Chat.
3. Let Copilot inspect the current repository before making changes.
4. Review every diff, run the suggested commands yourself, and check the result against the acceptance checklist.
5. Do not move to the next phase until the current phase is working and its commit is created.
6. Commit only reviewed changes. Never claim a test, Oracle validation, deployment, or demo succeeded unless you ran and verified it.

## Order

| Order | Prompt | Goal | Suggested commit |
|---|---|---|---|
| 1 | [Phase 1 — Backend foundation](phase-1-backend-foundation.md) | Establish Spring Boot, Oracle migrations, configuration, security foundation, and backend test setup | `feat: initialize backend and Oracle schema` |
| 2 | [Phase 2 — Employee, compensation, and seeding](phase-2-employee-salary-apis-seeding.md) | Implement employee APIs, effective-dated salary history, audit events, and 10,000 deterministic seed records | `feat: implement employee and salary management APIs` |
| 3 | [Phase 3 — Analytics and CSV export](phase-3-analytics-and-csv-export.md) | Implement aggregated reporting, filters, CSV exports, and reporting tests | `feat: add salary analytics and CSV exports` |
| 4 | [Phase 4 — Angular frontend](phase-4-angular-frontend.md) | Build the HR portal and connect it to real backend APIs | `feat: build Angular HR portal` |
| 5 | [Phase 5 — Integration, hardening, and delivery](phase-5-integration-hardening-delivery.md) | Validate end-to-end workflows, security, performance, deployment, README, and demo readiness | `test: verify end-to-end readiness and deployment` |

## Ground rules

- Use `docs/requirements.md`, `docs/architecture.md`, `docs/data-model.md`, and `docs/decisions.md` as the design baseline.
- If implementation reveals a necessary change, explain it and update the relevant artifact in the same phase.
- Do not generate the entire project in one pass.
- Do not commit credentials, real personal data, or production configuration.
- Use modern Angular, not legacy AngularJS 1.x.
- Oracle compatibility must be tested against Oracle; H2-only tests do not prove Oracle compatibility.
- Use a modular monolith. Do not add microservices or infrastructure without a demonstrated requirement.
