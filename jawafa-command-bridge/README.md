# Jawafa Unified Command Bridge — STAGING ONLY
28 October 2026. Single host dashboard: `dashboard/index.html` → native Jawafa views (not an iframe, not a new app).

## Important truth boundaries
- The bridge is implemented and its HTTP routes are exercised in isolated tests. **It is not deployed as the live private HTTPS service**. Do not infer SQL connectivity from successful API tests.
- Original `Jawafa_DB` is never connected to or mutated by this code. Isolated `node:sqlite` operational database only.
- `GATE-15` is **NOT ACCEPTED**, despite 15/15 extraction and technical checks. F01–F14 are **BLOCKED_BY_G15**.
- 7/7 historical RESTORE validations do not validate the newest daily backup.
- AppDeploy's 97 **task IDs and historical statuses** are seeded into the isolated operational ledger as explicitly marked secondary reference records. This is not live task evidence, does not prove current completion, and still requires reconciliation to the authoritative daily plan. Individual case descriptions are not published to public GitHub.
- The source snapshot in the GET response is explicitly `CONTRACT_REFERENCE_NOT_LIVE_SQL`. It contains no live financial evidence.

## Required configuration for a private deployment (not executed)
- Node.js 24.
- `JAWAFA_LEDGER_DB`: absolute path on separate restricted filesystem, *never inside Jawafa_DB or its backup folder*.
- `JAWAFA_OWNER_UID`: verified Firebase Auth UID (not email) of authorized owner, stored server-side.
- `JAWAFA_AGENT_SHARED_SECRET`: high-entropy secret at least 32 bytes, stored server-side.
- `JAWAFA_QA_SHARED_SECRET`: separate high-entropy QA attestation secret, NOT shared with executing agents.
- `JAWAFA_ALLOWED_AGENT_IDS`: comma-separated allowlisted agent identities. Empty means fail-closed.
- `JAWAFA_BIND_HOST`: defaults to loopback 127.0.0.1; service refuses external bind.
- `JAWAFA_PORT`: defaults to 8787.
- Put an authenticated, owner-restricted HTTPS reverse proxy in front of the service under **the same origin as the dashboard** at `/api/jawafa/v1`. GitHub Pages alone cannot proxy this backend. Do not expose the port publicly.
- Firebase ID token is verified cryptographically using the Secure Token JWK endpoint with audience/issuer/UID checks. Browser obtains short-lived token via existing Firebase Auth. No service role or API key in client source.
- Each agent worker call uses signed timestamp/nonce/body hash and separate executor/QA secrets; nonces are single-use and timestamp bounded.
- Cross-origin access is not enabled. Reverse proxy must add rate limits, IP/role restrictions, HSTS, TLS, access logging and operational backup/restore of the ledger before release.

## Implemented routes
- GET `/api/jawafa/v1/dashboard`
- GET `/api/jawafa/v1/gates/15`
- GET `/api/jawafa/v1/backups`
- GET `/api/jawafa/v1/tasks`
- GET `/api/jawafa/v1/evidence`
- POST `/api/jawafa/v1/commands`
- GET `/api/jawafa/v1/commands/:request_id`
- Signed agent GET `/api/jawafa/v1/agent/queue`
- Signed agent POST `/api/jawafa/v1/commands/:request_id/claim`
- Signed agent POST `/api/jawafa/v1/commands/:request_id/events`

## State machine
`QUEUED → ACKNOWLEDGED → RUNNING → VERIFIED|FAILED|BLOCKED`.
- Enqueue is not ACK; ACK is not VERIFIED.
- `VERIFIED` requires a signed independent QA attestation plus `PASS`, a reference, and SHA256.
- Receipt entries are tagged `RECEIPT_NOT_INDEPENDENTLY_QA` in the ledger until actual verification pipeline connects source evidence and receipt signature to independently accessible artifacts; do **not** upgrade financial findings solely on a worker's statement.
- Replayed signed requests and conflicting idempotency IDs are rejected.
- Gated financial reconciliation requests become `BLOCKED` (not queued).
- No browser action can bypass GATE-15 or cause accounting SQL writes.

## Acceptance still open
1. Configure private HTTPS gateway/owner authentication; test 401/403 against real user tokens.
2. Register a permitted worker on JAWAFFASVR with outbound polling of signed queue, and separate independent QA.
3. Verify a real read-only command + source timestamp + signed receipt + evidence SHA256, not a synthetic test payload.
4. Validate complete verified SQL export source and GATE-15 closing separately.
5. Verify newest daily backup and restore independently.
6. Run mobile/desktop E2E under the authorized account; audit permissions and network.
7. Save evidence and tests in private Google Drive final-report folder and verify ownership/hash.
8. Obtain independent security approval before merging or publishing.

## QA
Run locally: `node --test jawafa-command-bridge/test.cjs dashboard/tests/jawafa-native-integration.test.cjs`.
The GitHub Actions workflow also checks real headless Chromium navigation, core CI, and source security.
