# Jawafa Financial & Operations Console — Candidate (2026-10-28)

## Integration
- Existing dashboard -> **تحقيق جوافة** -> **لوحة GATE-15** -> **فتح مركز العمليات**.
- Entry: dashboard/jawafa-operations.html; keep the 15-table gate as the governing authority.
- User screens: leadership, evidence import, cheques, banking, accounting, purchases, receiving, inventory, suppliers, employees, liabilities, agents, decisions, tasks, reports, evidence register, integrations.

## Honest operational scope
- **Frontend staging ONLY**. This is not a connected accounting system. Server JAWAFFASVR was not reachable through the Remote Desktop Commander connection checked on 2026-10-28.
- No SQL writes, no cheque signing, no payables dispatch, no AI calls from the browser.
- Real financial reconciliation and live agent execution are BLOCKED pending the separate GATE-15 acceptance and secure backend.
- Files are parsed locally in ephemeral memory (max 10MB and 50,000 rows per import). SHA-256 computed by WebCrypto. No user-side storage or third-party analytics.
- Multiple imports to the same dataset replace the current in-memory preview, while all source metadata stays in the session register; never treat these separate snapshots as combined financial evidence.
- The manager-agent roles are specified, but **not yet connected to actual API runtimes**.
- Human approval is necessary for payment issuance, posting, employee decisions, accounting changes, deleting files, and production settings.

## Data interface (planned backend, not deployed)
- GET /api/jawafa/v1/status: authenticated operator, gate status, last verified snapshot, source cut-off.
- GET /api/jawafa/v1/checks: read-only filterable cheque ledger with physical leaf ID/number, bank clearing status, beneficiary, amount, currency, draw/due/clear date, cancel/reversal references, source pointers.
- GET /api/jawafa/v1/receiving and /purchases /staff /journal /bank: read-only approved snapshots, provenance and row counts.
- POST /api/jawafa/v1/agents/review: **server-side** validated request, CFO/CPO independent outputs, GM policy evaluation, QA + evidence receipts.
- POST /api/jawafa/v1/approvals: explicit owner approval, no financial mutation without separate production authorization and independent signed audit trail.
- Authentication: private gateway, short-lived role-scoped identities, CSRF protection, rate limiting, API keys only on server, no raw production direct browser access.
- Evidence contract: snapshot_id, cutoff_at, extracted_at, source_system, source_object, row_pk, original_reference, SHA256, verification status, verifier and timestamp.
- Gate cannot be marked passed on browser input alone: acceptance must derive from signed and independently verified 15-table audit manifest.

## Agent execution constraints
1. **CFO**: bank/cheque reconciliations (only after gate), cash forecast, anomalies, associated evidence and confidence; never assert fraud from differences alone.
2. **CPO**: purchase and receiving deviations, supplier negotiations, unit-cost and shortage analysis, alternatives.
3. **GM**: independent CFO/CPO synthesis, evidence qualification, risk ranking, task assignments, approval-gated recommendation; actual payment and personnel actions require authorized humans.
4. Complete workflow: INGEST -> VERIFY -> CFO + CPO REVIEW -> QA -> GM REVIEW -> HUMAN APPROVAL (when needed) -> EXECUTION RECEIPT -> RECHECK.
5. Logging: source device ID -> Windows user -> TrustSoft user -> event time -> document number -> auxiliary evidence. Shared accounts alone are not identity proof.

## QA
Run: node --test dashboard/tests/jawafa-operations.test.cjs
Required beyond syntax/parser unit tests: private authentication, permissions tests, complete original-data verification, live read-only API contract tests, independent security review, correct bank comparison, and receipt-backed agent job completion.

## Release
Do not market this candidate as a deployed live automated financial command centre. Link it within the existing Github dashboard only and await verified review before production API activation.