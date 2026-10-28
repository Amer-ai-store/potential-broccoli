# AAS-CC free runtime / verified operations (2026-10-27)

## Live owner dashboard

[AAS-CC Operations Console](https://aas-cc-live-command-center.floot.app/) — sign in with the dashboard's authorized Google Owner account. Use **التشغيل وGitHub والبيانات**.

## What was actually deployed

- Supabase Edge `aas-signed-local-worker-bridge` v2: RSA-3072 signed requests, 90-second timestamp checks, single-use nonce, isolated `SMOKE_TEST` / `READ_ONLY_ANALYSIS` jobs, receipts.
- Windows Server `JAWAFFASVR` worker under **NT AUTHORITY\\LOCAL SERVICE**, scheduled every 2 minutes. The task uses only outbound HTTPS, and executes the Qwen3-0.6B GGUF model via llama.cpp in an isolated folder.
- Worker signs with a **private RSA key kept on the server**; the Edge function stores only the public key. No Supabase service-role key or GitHub credential on the Windows worker.
- Supabase owner operations status via restricted `aas_cc_operation_truth()`, and existing owner-verified `aas-cc-floot-bridge` endpoints.
- GitHub Actions workflow in this branch provides credential-free bridge health, unsigned request denial, Cloudflare candidate regression, and public repository health. GitHub default CodeQL setup remains authoritative (no duplicate advanced CodeQL workflow).

## QA evidence

Two isolated jobs returned answer `4`, with verified exact-match QA:

- Manual: `eac6deb8-b70b-4207-9efc-53c9829a731d`, `QA_PASSED`, 2879 ms.
- Scheduler unattended: `651446d5-ac2b-44db-950f-0de8cd0eec42`, `QA_PASSED`, 3534 ms.

Caveat: These prove limited inference and transport ONLY. They **do not** prove canonical AAS-CC/AAS-AW tasks run end-to-end. 19 ChatGPT UI bindings remain unverified and direct ChatGPT Plus chat delivery is unsupported. The canonical dispatcher remains 503 as of this record.

## Important operational safeguards

- **NO EVIDENCE = NO DONE**, and machine ACK != completion.
- No accounting database connection or writes permitted. No external inbound port opened on JAWAFFASVR.
- Windows system clock was found ~63 minutes ahead of Supabase; the worker uses Edge server-time synchronization to sign requests, **without changing Windows time**. The host time discrepancy is an independent infrastructure finding to address via authorized time-service maintenance.
- Preserve active governance policy pins, existing coordination locks and historical receipts.
- This pilot is NOT a rollout of arbitrary model-generated commands. It does not access file shares, execute generated code or mutate financial records.
- GitHub dashboard status is **public repository Actions read-only**. Writing to GitHub from the web dashboard requires separate securely provisioned GitHub App/OAuth authorization. Do not claim GitHub write access without it.
- Fortify on Demand failures are due to absent `FOD_CLIENT_ID` / `FOD_USER` credentials (evidence: GitHub run 40390929397). CodeQL is an additive free scan candidate; Fortify has not been disabled.

## Remaining acceptance gates

1. Verify the new GitHub Actions checks on this PR.
2. Security review for the isolated worker and service account ACLs.
3. Independent QA on nontrivial read-only tasks, adverse prompt and timeout cases.
4. Governed integration with canonical dispatcher, owner/lock, policy, QA, GM review, then verifiable receipt.
5. Live validation of GitHub write scopes before adding any mutation buttons in the dashboard.

Do not report the overall program `DONE_VERIFIED` before all mandatory gates are satisfied.


## Source-controlled worker & latest verification

- [Worker code](./worker/aas-worker-once.js) — RSA-signed outgoing transport; load the PRIVATE key from the server's protected filesystem; **no key stored in GitHub**.
- [Windows task launcher](./worker/worker-launch.ps1) — periodically invoked under `NT AUTHORITY\\LOCAL SERVICE` with `IgnoreNew` and startup recovery.
- [Security negative tests](./worker/worker-security-test.js) — protected key remains local; never run the test without the authorized machine key.

Verified security checks on 2026-10-27:

1. Unsigned POST rejected with HTTP 401.
2. Properly signed heartbeat accepted with HTTP 200.
3. Identical signed replay rejected with HTTP 409.
4. Tampered signed request rejected with HTTP 401.

Automated Task Scheduler execution recorded `LastTaskResult=0` and a successful unattended smoke-test receipt. Windows task has periodic and startup triggers, `StartWhenAvailable=true`, and nonoverlapping execution. Last tested worker state was **not** a verified production dispatcher replacement.

Independent nontrivial read-only analysis canary: job `c4f692fa-64b0-40c6-939f-4411fc2c7024`, result `SUBMITTED_FOR_REVIEW`, 6347 ms, NOT `DONE_VERIFIED`. The output itself is not independently approved.

GitHub main CI: `AAS Free Runtime & Security CI` passed following merge of PR #55; repo's CodeQL default setup remains authoritative. Fortify paid FoD authorization remains a separate visible failure.
