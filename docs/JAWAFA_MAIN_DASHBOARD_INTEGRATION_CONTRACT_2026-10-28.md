# JAWAFA — Integration Contract for the Existing System Dashboard
Version: 2026-10-28 | Scope: technical integration specification only — NOT deployed API

## Target (DO NOT deploy a separate dashboard)

Use the already-existing unified financial and operations panel:
- PR #66: `dashboard/jawafa-operations.html` (CFO / CPO / GM, cheques, bank, accounting, purchases, receiving, staff, suppliers, inventory, evidence, decisions).
- Existing gate page: `dashboard/jawafa-gate15.html`.
- PR #65: `dashboard/index.html#jawafa-monitor` (device-first security/TrustSoft activity monitor).
- Preserve the current host application's login, navigation, access roles, layout and deployments. Integrate components into the main dashboard instead of embedding a separate public financial app.

## Authoritative evidence and source distinctions

- Production SQL Server: `JAWAFFASVR`, `Jawafa_DB`; FORENSIC-READ-ONLY. Never mutate original source numbers, amounts, quantities or inventory.
- Verified forensic snapshot: `Jawafa_Audit15_20261028_0320`; extraction evidence in `Audit15_20261028_0315` with `ALL_RESULTS.csv`, `result_*.csv`, `cloud_verified_*.json` and `backup_restore_1..7.json`.
- Drive final evidence folder ID: `1iiu8JpSGKeeCcyCV2JU3z_RyXARxw8DT`; owner-only access. Never embed Google Drive OAuth, SQL login, service keys or raw evidence in frontend or public git.
- Historical operational status application exists at AppDeploy `guava-forensic-command-center-dhv9z9`. It carries a 2026-10-28 reference baseline of **97 tasks**. Its app-isolated DB is NOT a verified live SQL source and there is no authenticated export API verified yet. Treat this as a secondary reference, not a direct browser connection.
- GATE-15: export/round-trip/sha of **15/15** tables verified; causality of some differences is still unresolved; therefore `NOT ACCEPTED`, and F01–F14 remain `BLOCKED_BY_G15`.
- Seven *historical* backups passed isolated restore/CHECKDB/read tests; do not infer the daily backup is validated.
- During any active backup/export/import, serve **last prior completed and verified** version. Reject partial files.

## Verified status data contract (private API; a proposed design, not existing endpoints)

Return a versioned envelope from authenticated `GET /api/jawafa/v1/dashboard`:

```json
{
  "schema_version": "jawafa.command.v1",
  "snapshot_id": "audit15-20261028-0320",
  "source_system": "Jawafa_Audit15",
  "source_cutoff_at": "2026-10-28T03:20:00+03:00",
  "extracted_at": null,
  "verified_at": null,
  "report_generated_at": null,
  "live_sql": false,
  "gate": { "id": "GATE-15", "status": "NOT ACCEPTED", "technical_export_tables": 15, "technical_verified_tables": 15, "historical_causality_accepted": false },
  "backup_restore": { "tested": 7, "accepted_tests": 7, "daily_latest_accepted": false },
  "dashboard": { "status": "PARTIAL", "private_https_verified": false, "e2e_verified": false },
  "tasks": [],
  "evidence_refs": [],
  "checks": []
}
```

Null dates mean missing verification, not a verified zero timestamp. Do not use an app-side boolean to override evidence gate status. A gate transitions to ACCEPTED only after independent signed 15/15 audit acceptance, a human-authorized change and full receipts. Every entry in `tasks` should include `task_id`, `status` (COMPLETED / IN PROGRESS / BLOCKED / NOT STARTED), `evidence_ref`, `acceptance_test`, `checked_at`, `source_version`, and any blocking dependency.

Provide filtered read endpoints after auth:
- `GET /api/jawafa/v1/gates/15`
- `GET /api/jawafa/v1/backups`
- `GET /api/jawafa/v1/tasks`
- `GET /api/jawafa/v1/evidence`
- `GET /api/jawafa/v1/commands/:request_id`
- Existing PR #65 monitor can consume a separately authenticated, private `GET /snapshot` with `mode: FORENSIC-READ-ONLY` (keep its existing response contract).

## Command channel and evidence receipts (proposed; no direct ChatGPT cross-chat bus)

The browser must NOT connect to `Jawafa_DB` or to ChatGPT conversations directly. A ChatGPT share link is a static copy, not a queue and not an authenticated bridge.

Implement a protected server-side command queue:
- `POST /api/jawafa/v1/commands`: accepts `request_id` (idempotency key), `task_id`, `operation`, `params`, `requested_by`, `source_version`, `approval_id` (when required), `created_at`.
- On receipt initially use `QUEUED` / `NOT_SENT`, NOT `EXECUTED`.
- A real authorized agent/Work/automation process must explicitly poll/claim via a trusted service bridge and return signed `ACK`, `RUNNING`, `VERIFIED`, `FAILED`, or `BLOCKED` events with execution timestamps, tests and `evidence_ref`.
- Persist append-only `command_events` and `evidence_receipts` with `request_id`, `agent_id`, device ID, Windows user, TrustSoft user (when relevant), execution result, SHA-256 and source version. Prevent duplicate execution via idempotency.
- Disallow SQL writes, cheque issuance, posting, changing accounting data, payroll decisions, production setting modifications and public evidence access. Any high-impact action needs an explicit approval workflow separate from automated command receipt.

## Security

- Owner-only private backend with least privilege, authenticated HTTPS, scoped authorization, CSRF controls where applicable, rate limits, credential rotation and server-side secrets only.
- SQL gateway on private localhost/protected network; no public SQL access, no shared `sa` connection for production browser calls. Use read-only user.
- No raw bank statements, staff records, CCTV, personally identifying event logs, sensitive documents, tokens or direct Drive file URLs in public git or public APIs.
- All uploaded snapshots require completeness indicator, row counts, SHA-256 and source/cutoff. Reject incomplete imports; preserve earlier accepted snapshot until new version passes tests.
- Accept only signed verified records, enforce source monotonicity, sequence IDs, optimistic concurrency, and don't confuse clock skew with actual sequence.
- Logs must record device → Windows → TrustSoft → event time → voucher/document → supporting evidence. No person attribution based on shared username.

## UI deliverables inside PR #66

1. A **GATE-15 command** tab with per-table extraction, reference mismatch, historical cause, last evidence and acceptance status.
2. Historical backup RESTORE 7/7 status, current daily backup validation and selected accepted copy.
3. Dashboard connectivity section with authenticated HTTPS monitor and verified refresh timestamp, no fake online state.
4. Single project task registry (97 reference tasks; reconcile against authoritative plan), filter/search and gate dependencies.
5. Existing CFO/CPO/GM agent panes with user-visible queued/requested/executed distinction and receipts.
6. Buttons for **read-only** check/status, reverify source, recheck backup, compare historical snapshots, export evidence receipts, and review queued commands. Every button returns a real `request_id` or a clear `NOT_CONNECTED` / `BLOCKED`; it must not claim actions merely because it rendered.
7. Respect the current financial reconciliation gating (all F01–F14 BLOCKED until GATE-15 ACCEPTED).
8. Report exports only through the private Google Drive folder, with access check and file hash verification.

## QA acceptance

- Test unauthenticated and non-owner access (401/403), command creation and idempotency, polling/ACK/VERIFY receipts, stale snapshot/failed hash and incomplete CSV rejection, blocked F01 before GATE-15, private gateway security, backup read from last verified version, E2E on desktop and mobile.
- Do not merge PR #66 or #65 into production or claim live integration before the private gateway, independent security review and end-to-end tests pass.
- Deliver GitHub PR, updated QA test logs, result of authenticated read/write to *operational* dashboard database only, Drive evidence and exact status: COMPLETED / IN PROGRESS / BLOCKED.
