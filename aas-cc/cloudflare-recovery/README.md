# AAS-CC Cloudflare Workers AI recovery candidate (2026-10-13)

This branch is a review-only staging candidate. Do not merge or deploy to production until independent Security, QA, POS/CHANGE/BLOCK live tests, one-owner/one-lock checks, and receipt verification have passed.

Governance:
- Active Source Manifest 1.3-R1; Canonical Rules 3.4.
- Task: CPDEV-CLOUDFLARE-DISPATCHER-RECOVERY-20261013
- Assignment: 1700c37d-a811-4f91-aba7-a057eee3e50b
- Lock: 33e85a34-1a84-4af5-bbde-1e91d430f743
- Candidate artifact SHA256: ab8632edb2c88b474dc7cc9f55af8eaaee6c2c7b32e7d0b70013915081590a89
- Baseline: dispatcher edge function version 97, HTTP 503, no live provider.
- Independent QA and Security pending. Local mock tests 20/20, not runtime tests.

Do not publish credential values. Supabase Vault row existence does not create a Deno.env Edge secret binding.

## v1.2.1 hardening (security review follow-up)

Changes against the 1.2.0 candidate above (the artifact SHA256 refers to 1.2.0, not this revision):
- Request bodies are read as a stream capped at 12000 bytes, so a missing or non-numeric Content-Length can no longer bypass the size limit (non-numeric → 400, over limit → 413).
- Required Rate Limiting binding `AAS_RATE_LIMITER` (key `aas-bridge`), checked after auth and before any body read or model call. Missing or failing binding → 503, limit exceeded → 429.
- Key rotation: `AAS_BRIDGE_SECRET_NEXT` is accepted alongside `AAS_BRIDGE_SECRET`. Rotate by setting NEXT on the Worker, switching the Supabase Edge secret to it, then promoting it to `AAS_BRIDGE_SECRET` and removing NEXT.
- Unauthenticated `/health` no longer reports the model or whether bindings/secrets are configured.
- The Supabase adapter stops reading a Worker response once it exceeds 18000 bytes instead of buffering it first.

Example `wrangler.toml` binding (limits to be set by the owner against the free-tier budget):

```toml
[[ratelimits]]
name = "AAS_RATE_LIMITER"
namespace_id = "1001"
simple = { limit = 30, period = 60 }
```

Still open: a daily budget cap in the dispatcher itself, independent Security PASS, live inference, live canaries, and independent QA.
