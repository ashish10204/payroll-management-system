# Phase 3 — Compensation Analytics, Reporting, and CSV Export

## Copilot Agent prompt

Implement **Phase 3 only** in the existing PayFlow Spring Boot backend. Inspect the current implementation, endpoint conventions, DTOs, security configuration, migrations, tests, and documentation before changing files. Do not rebuild features already working in earlier phases.

Read `docs/requirements.md`, `docs/architecture.md`, `docs/data-model.md`, and `docs/decisions.md`. This phase focuses on reporting and exporting data. Do not implement the Angular frontend yet.

## Reporting API requirements

Create authenticated reporting endpoints under `/api/v1/reports`, using the existing API response and error conventions. Support appropriate filters for country, department, job title, employment status, currency, pay frequency, and effective/as-of date as supported by the report's meaning.

Provide the useful metrics needed by the HR manager:
- Total employee headcount and breakdown by country, department, and employment status
- Compensation employee count, minimum, average, maximum, and total amount
- Compensation summaries grouped by country, department, job title, **currency, and pay frequency** where appropriate
- Optional salary distribution buckets if they can be defined clearly without misleading mixed-currency comparisons

For each metric, document the population and semantics: for example, whether inactive employees count toward headcount, and which salary record is current as of the report date. Use the same documented business timezone and salary effective-date rules as the compensation service.

## Correctness rules

- Do not sum or average different currencies together.
- Do not sum or average different pay frequencies together unless a clearly documented, tested normalization rule exists. Version one has no annualization or currency conversion.
- Always label amount, currency, frequency, date, and filters in the response so the UI cannot misrepresent the result.
- Use Oracle-side aggregate queries or projections; do not load every employee or salary row into Java memory for reports.
- Use bound query parameters and allowlisted filter/sort options. Do not build SQL from untrusted raw strings.
- Avoid N+1 loading and return only the fields needed by the report.
- Keep reporting query logic in a dedicated module/repository/service, not in controllers.

## CSV export

Implement CSV export for the employee directory and relevant compensation reports. Export must use the same authorization, filters, effective date, and currency/frequency grouping rules as the corresponding API view.

- Use a documented, stable column order and include currency/frequency columns wherever monetary values appear.
- Correctly escape commas, quotes, line breaks, and Unicode text.
- Protect against spreadsheet-formula injection for untrusted textual fields whose values could be interpreted as formulas by spreadsheet tools. Use a documented, carefully tested neutralization policy.
- Set suitable content type and content-disposition headers; produce a useful filename without accepting arbitrary path values.
- Bound export size or stream/page results so an export cannot require unbounded heap memory.
- Do not include passwords, tokens, or unnecessary sensitive fields.

## Tests

Add fast, deterministic tests for:
- Correct aggregate values with seeded-like fixture data
- Separation of currency and pay frequency groups
- Effective-date selection for historical/future salary changes
- Filter combinations and status inclusion rules
- Empty datasets and zero-valued metrics
- CSV escaping, Unicode, line breaks, formula injection mitigation, content headers, and filtering parity
- Authentication/authorization for reports and exports
- Query parameters and page/export bounds

Prefer unit tests for grouping/business behavior plus repository/integration tests for important Oracle SQL semantics. Keep integration tests reproducible. Do not assert Oracle compatibility based only on H2.

## Performance verification

Use a representative 10,000-employee data set when available. Check that report queries are executed in the database, observe response times without inventing a target result, and inspect/describe any important query plan or index changes. Add indexes only with a reason grounded in query usage.

## Exit criteria

- Reporting endpoints are documented and protected.
- Results are grouped by currency and pay frequency correctly.
- CSV exports follow the same filters and authorization as the UI.
- Tests verify key calculations and CSV edge cases.
- README/API documentation includes sample requests and response shapes.
- Build/test results and any Oracle-only verification gaps are reported accurately.

Finish with a list of endpoints, query/performance notes, files changed, tests actually run, known limitations, and proposed commit `feat: add salary analytics and CSV exports`. **Stop for review before Phase 4. Do not commit automatically.**
