# Rollback Policy

## Purpose

This document defines the automated and manual rollback procedures for the Night Run deployment pipeline. The rollback policy ensures that defective or unstable deployments can be safely and swiftly reverted to a known-good state, minimizing downtime and cascading failures. It codifies when a rollback must be triggered automatically, how the revert process is executed and documented, and what follow-up actions are required to prevent recurrence.

---

## Rollback Triggers

A rollback **must** be initiated — automatically or immediately upon manual detection — when any of the following conditions are met:

| Trigger | Severity | Auto-Rollback |
|--------|----------|---------------|
| **Critical error rate spike** — HTTP 5xx responses exceed 5% of total traffic over a 2-minute sliding window | Critical | Yes |
| **Deployment health check failure** — Any primary health endpoint (`/health`, `/ready`, `/live`) returns non-200 for 3 consecutive probes within 60 seconds | Critical | Yes |
| **Data integrity violation** — Corruption, missing records, or schema drift detected in audit logs or database assertions | Critical | No (manual confirmation required) |
| **Performance degradation** — P95 latency exceeds the previous baseline by 2x or more for a sustained 5-minute period | High | Yes |
| **Critical security vulnerability** — The deployed code introduces a known CVE or credential leak | Critical | Yes |
| **Failed smoke-test step** — A post-deployment smoke test (canary, integration, or synthetic monitor) fails | Critical | Yes |
| **Manual trigger by on-call engineer** — Any engineer with deploy authority determines the deployment is unsafe | Variable | N/A (manual) |

---

## Rollback Process

Every rollback follows a strict five-step process. Automation handles steps 1–3 for auto-rollbacks; manual rollbacks require the engineer to execute all five steps explicitly.

### 1. Identify the Revert Commit

Determine the exact commit hash that introduced the faulty deployment.

- **Auto-rollback:** The pipeline reads the `deploy.log` entry for the current deployment and resolves `HEAD` (or the pinned tag) as the commit to revert.
- **Manual rollback:** The engineer identifies the last known-good commit via:
  - The deployment status matrix (see below).
  - The commit log between the current deployment and the last green deployment.
  - Git bisect if the faulty change is uncertain.

Record the revert target in the format: `target: <full-commit-hash>`.

### 2. Create the Revert Commit

Create a clean revert commit using `git revert` (not a force-push or reset).

```bash
# Auto-rollback executes this via the pipeline runner:
git revert --no-edit <faulty-commit-hash>

# Manual rollback (recommended with a reason message):
git revert --no-edit <faulty-commit-hash> -m "revert: <reason summary>"
```

**Rules:**
- Do **not** squash the revert commit.
- Do **not** amend history — the revert must be visible as a distinct commit in the log.
- If the faulty deployment spans multiple commits, revert them in reverse chronological order (youngest first).

### 3. Log the Reason

Append a structured rollback entry to `/var/log/night-run/rollback.log` (or equivalent pipeline log):

```
[YYYY-MM-DD HH:MM:SS UTC] ROLLBACK — env: <environment>
  trigger:   <trigger type>
  revert:    <faulty-commit-hash> → <revert-commit-hash>
  reason:    <detailed explanation>
  triggered-by: <auto | engineer-name>
  task-url:  <URL of follow-up fix task>
```

### 4. Update the Status Matrix

Update the deployment status matrix (file: `deployments/status-matrix.yml`) to mark the rolled-back deployment as `rolled-back` and the target commit as the `current` stable baseline.

```yaml
environments:
  production:
    current: a1b2c3d4e5f6...
    history:
      - commit: f6e5d4c3b2a1...
        status: rolled-back
        timestamp: 2026-06-21T03:15:00Z
        rollback-reason: "Error rate spike >5%"
```

### 5. Create a Fix Task

Create a high-priority issue/task in the project tracker (e.g., Linear, Jira, GitHub Issue) with the following template:

```markdown
**Title:** [ROLLBACK FOLLOW-UP] <short description of root cause>
**Priority:** Critical / High
**Labels:** `rollback`, `bug`, `night-run`

**Description:**
A rollback was triggered on <date> due to <trigger>.
- **Faulty commit:** `<hash>`
- **Revert commit:** `<hash>`
- **Rollback log entry:** `<link or path>`

**Action items:**
- [ ] Identify root cause
- [ ] Write regression test
- [ ] Submit fix PR
- [ ] Pass all smoke tests before merging
- [ ] Update runbook if needed
```

---

## Never Auto-Revert Rule

Under no circumstances shall a rollback be automatically re-deployed or auto-reverted in the opposite direction. The **Never Auto-Revert Rule** prohibits:

- Automatically re-applying the original faulty commit after a rollback.
- Automatically rolling forward to a new commit without explicit human sign-off.
- Any CI/CD pipeline step that re-deploys silently after a rollback event.

**Rationale:** Auto-reverting creates infinite loops, masks root causes, and can escalate partial failures into full outages. Every rollback must be followed by a deliberate human-driven investigation and fix.

**Exception:** If a rollback itself fails (e.g., `git revert` conflicts), the pipeline must halt immediately and alert the on-call engineer. No further automated recovery is attempted.

---

## Small Commits Requirement

All deployments MUST consist of small, atomic commits. This rule directly supports the rollback process.

| Requirement | Rationale |
|-------------|-----------|
| Each commit addresses a single logical change | Reduces the blast radius of a revert; avoids reverting unrelated changes |
| No commit shall exceed 400 lines of changed code (excluding generated files and lockfiles) | Keeps the diff reviewable and the revert low-risk |
| Database migrations MUST be in a separate commit from application code | Allows the rollback process to revert code without automatically reverting a schema change that may be non-backwards-compatible |
| Squashing is permitted only for feature branches before merge; the merge commit must still produce a clean `git revert` | Preserves a reversible history on the main branch |

**Enforcement:** The CI pipeline will reject PRs with commits exceeding the size threshold. A pre-merge check ensures migration commits are isolated.

---

## Rollback Documentation Format

Every rollback entry — whether auto-generated or manually written — must conform to the following schema:

```yaml
rollback_entry:
  timestamp: 2026-06-21T03:15:00Z
  environment: production | staging | canary
  trigger: error_rate | health_check | data_integrity | performance | security | smoke_test | manual
  faulty_commit:
    hash: a1b2c3d4...
    author: engineer@example.com
    title: "Subject line of the faulty commit"
  revert_commit:
    hash: f6e5d4c3...
    author: night-run-bot | engineer@example.com
  reason: "Free-text explanation of what went wrong and why the revert was necessary"
  triggered_by: auto | <engineer-name>
  fix_task_url: https://...
```

This structured entry is written to both the rollback log file and the deployment status matrix. It also feeds into the post-mortem process.

---

## Recovery After Rollback

After a successful rollback, the following recovery steps must be completed before any new deployment is attempted:

1. **Verify restored health** — Confirm that all health endpoints return 200, the error rate is below the threshold, and synthetic monitors are green. This must be done within 5 minutes of the rollback completing.

2. **Re-run smoke tests** — Execute the full smoke-test suite against the rolled-back deployment to prove the previous good state is intact.

3. **Notify stakeholders** — Post a rollback summary to the #deployments and #incidents Slack/Teams channels, tagging the on-call engineer and the author of the faulty commit.

4. **Investigate root cause** — Begin RCA immediately. No new feature deployments are permitted until the root cause is identified and a fix is ready.

5. **Freeze deploys (if applicable)** — If the rollback was triggered by a severity "Critical" event, impose a 2-hour deployment freeze on the affected environment. The freeze may be lifted earlier if the fix is trivial and has been reviewed.

6. **Deploy the fix** — Once the fix PR has passed review and all smoke tests pass on a canary/staging environment, the fix may be deployed following the standard Night Run pipeline. The deployment freeze timer resets if a new rollback occurs.

---

## Prevention

Rollbacks are a safety net, not a strategy. Preventative measures to reduce rollback frequency include:

- **Mandatory code review** — Every PR requires at least one approval from a senior engineer. Deployment-affecting changes require two approvals.
- **Comprehensive smoke tests** — The smoke-test suite must cover health checks, critical user journeys, data integrity assertions, and performance baselines. Tests run automatically in the canary stage before full production rollout.
- **Canary deployment** — New releases are first deployed to a canary environment (5–10% of traffic) for a 10-minute observation window. A rollback is triggered automatically if the canary fails; the full rollout never proceeds if the canary is unhealthy.
- **Feature flags** — High-risk or experimental features must be gated behind feature flags. This allows disabling a feature without a full deployment rollback.
- **Runbook automation** — Each deployable service must maintain a runbook (in `runbooks/`) that includes: common failure modes, manual rollback commands, and expected recovery times.
- **Post-mortem culture** — Every rollback (auto or manual) produces a blameless post-mortem within 48 hours. Action items from the post-mortem are tracked in the project backlog.

---

*Document version: 1.0 | Last updated: 2026-06-21 | Owner: Night Run Engineering*
