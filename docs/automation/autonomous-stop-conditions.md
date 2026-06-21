# Autonomous Stop Conditions

> **Part of:** Night-Run Automation System
> **Document:** 3 of 3
> **Status:** Definitive Reference

---

## Purpose

Define the complete set of conditions under which an autonomous night-run session halts — either immediately (hard stop) or at a natural breakpoint (soft stop) — and prescribe the response protocol for each. Every stop condition maps to a deterministic handler so that the system never stalls silently or enters an undefined state.

---

## Immediate Stop Conditions

An **immediate stop** (hard stop) terminates all running tasks, kills any active playerbot processes, records a failure summary, and returns control to the orchestrator. The session is not resumed.

| # | Condition | Identifier | Trigger | Rationale |
|---|-----------|------------|---------|-----------|
| 1 | **Compile failure** | `STOP_COMPILE_FAIL` | `bazel build //...` or `mvn compile` exits non-zero | No point running tests or bots against broken artifacts |
| 2 | **Raw-ID failure** | `STOP_RAWID_FAIL` | `raw-id` subcommand (or equivalent mapping step) produces no output or errors | Upstream dependency resolution is broken; subsequent stages depend on correct IDs |
| 3 | **CT 123 unavailable** | `STOP_CT123_DOWN` | `curl` or gRPC health check to `ct123:12345` fails | Core test infrastructure is unreachable |
| 4 | **Git dirty** | `STOP_GIT_DIRTY` | `git status --porcelain` returns non-empty before stage execution | Uncommitted changes could pollute results; session requires a clean baseline |
| 5 | **Merge conflict** | `STOP_MERGE_CONFLICT` | `git merge --no-commit` exits 1 or `grep -c '<<<<<<<'` > 0 | Merge state is unrecoverable without human intervention |
| 6 | **Playerbot crash loop** | `STOP_BOT_CRASH_LOOP` | Same bot crashes ≥3 times within a 5-minute sliding window | Indicates fundamental bot or environment instability |
| 7 | **Repeated stuck state** | `STOP_STUCK_LOOP` | Any bot reports `STATE_STUCK` for ≥3 consecutive poll cycles without progress | Bot is alive but incapable of forward movement |
| 8 | **More than N blocked tasks** | `STOP_BLOCKED_OVERFLOW` | Blocked-task counter > `MAX_BLOCKED` (default: 5, configurable per run) | Dependency deadlock or unresolvable obstacle chain |
| 9 | **Human review request** | `STOP_HUMAN_REVIEW` | Any subsystem calls `request_review()` or the orchestrator receives a SIGUSR1 | Explicit handoff to a human operator |
| 10 | **Confidence below threshold** | `STOP_LOW_CONFIDENCE` | Confidence score (0.0–1.0) of the primary decision model drops below `CONFIDENCE_FLOOR` (default: 0.4) | Model is operating outside its reliability envelope |
| 11 | **Batch limit exceeded** | `STOP_BATCH_LIMIT` | Number of completed batches > `MAX_BATCHES` (default: 20) | Hard ceiling to prevent runaway runs |
| 12 | **Unknown module path** | `STOP_UNKNOWN_MODULE` | Stage references a module path not present in the module registry or `MODULES.yaml` | Configuration error — cannot proceed safely |
| 13 | **Skill/workflow contradiction** | `STOP_CONTRADICTION` | Two loaded skills or workflow steps assert mutually exclusive preconditions or postconditions | Logical inconsistency detected by the validation engine |

---

## Soft Stop Conditions

A **soft stop** completes the current stage, then refuses to schedule further work. The session is marked as *partial success* and the orchestrator may optionally attempt a reduced-scope run.

| # | Condition | Identifier | Behavior |
|---|-----------|------------|----------|
| 1 | **Batch limit** | `SOFT_BATCH_LIMIT` | Current batch finishes; no new batch is started. Equivalent to `MAX_BATCHES` but hit gracefully rather than as a hard ceiling. |
| 2 | **Time limit** | `SOFT_TIME_LIMIT` | Wall-clock timer expires (default: 6 hours, configurable via `--time-limit`). Active tasks get a SIGTERM + 30 s grace, then SIGKILL. |
| 3 | **Max consecutive failures** | `SOFT_MAX_FAILURES` | Consecutive failure counter hits `MAX_CONSECUTIVE_FAILURES` (default: 3). The run stops rather than burning resources on likely-failing work. |
| 4 | **No more safe tasks** | `SOFT_NO_SAFE_TASKS` | Dependency resolution finds zero tasks whose preconditions are satisfied and whose risk score is below the safety threshold (`SAFE_TASK_THRESHOLD`, default: 0.3). |

---

## Stop Response Protocol

Every stop condition triggers a deterministic handler. The table below specifies what happens for each.

### Hard stop response

| Condition | Kill Bots | Write Report | Upload Artifacts | Notify | Exit Code |
|-----------|-----------|-------------|-----------------|--------|-----------|
| `STOP_COMPILE_FAIL` | Yes | `failure-compile.md` | Build logs only | Slack + Discord | 10 |
| `STOP_RAWID_FAIL` | Yes | `failure-rawid.md` | Config dump | Slack | 11 |
| `STOP_CT123_DOWN` | Yes | `failure-ct123-down.md` | Network diagnostics | PagerDuty | 12 |
| `STOP_GIT_DIRTY` | Yes | `failure-git-dirty.md` | `git diff` output | Slack | 20 |
| `STOP_MERGE_CONFLICT` | Yes | `failure-merge-conflict.md` | `git diff` + conflicted files | Slack + Discord | 21 |
| `STOP_BOT_CRASH_LOOP` | Yes | `failure-bot-crash-loop.md` | Bot core dumps, logs | Slack | 30 |
| `STOP_STUCK_LOOP` | Yes | `failure-stuck-loop.md` | Bot state dumps, poll history | Slack | 31 |
| `STOP_BLOCKED_OVERFLOW` | Yes | `failure-blocked-overflow.md` | Dependency graph | Slack + Discord | 40 |
| `STOP_HUMAN_REVIEW` | No | `handoff-human-review.md` | Full session state | PagerDuty + Slack + Discord | 50 |
| `STOP_LOW_CONFIDENCE` | Yes | `failure-low-confidence.md` | Model scores, input features | Slack | 60 |
| `STOP_BATCH_LIMIT` | Yes | `summary-batch-limit.md` | Full results | Slack | 70 |
| `STOP_UNKNOWN_MODULE` | Yes | `failure-unknown-module.md` | Module registry dump | Slack | 80 |
| `STOP_CONTRADICTION` | Yes | `failure-contradiction.md` | Skill/workflow validation trace | Slack + Discord | 90 |

### Soft stop response

| Condition | Kill Bots | Write Report | Upload Artifacts | Notify | Exit Code |
|-----------|-----------|-------------|-----------------|--------|-----------|
| `SOFT_BATCH_LIMIT` | Graceful drain | `summary-soft-batch-limit.md` | Partial results | Slack | 0 (partial) |
| `SOFT_TIME_LIMIT` | SIGTERM + 30s | `summary-soft-time-limit.md` | Results up to cutoff | Slack | 0 (partial) |
| `SOFT_MAX_FAILURES` | Graceful drain | `summary-soft-max-failures.md` | Partial results | Slack | 0 (partial) |
| `SOFT_NO_SAFE_TASKS` | Graceful drain | `summary-soft-no-safe-tasks.md` | Partial results | Slack | 0 (partial) |

**General notes:**
- "Kill Bots" = `pkill -f playerbot` + wait + `pkill -9 -f playerbot` if any remain after 5 seconds.
- "Write Report" writes to `<run-dir>/reports/<filename>`. The report is structured YAML + human-readable summary.
- "Upload Artifacts" tars the `<run-dir>/artifacts/` directory and uploads to S3/GCS under `night-runs/<run-id>/`.
- "Notify" posts to the configured channels; PagerDuty triggers an incident with severity=CRITICAL.

---

## Emergency Stop Commands

Operators can trigger an emergency stop at any time using the following methods.

### Shell / CLI

```bash
# Graceful stop (completes current stage, then halts)
touch /tmp/night-run-stop

# Immediate hard stop (kills everything now)
touch /tmp/night-run-stop-hard

# Human-review handoff (completes current action, then pauses)
touch /tmp/night-run-human-review
```

### Signal-based

```bash
# Soft stop (same as graceful)
kill -USR1 <orchestrator-pid>

# Hard stop
kill -USR2 <orchestrator-pid>

# Human review request
kill -SIGQUIT <orchestrator-pid>
```

### Orchestrator API (gRPC)

```protobuf
rpc EmergencyStop(EmergencyStopRequest) returns (EmergencyStopResponse) {
  option (google.api.http) = { post: "/v1/night-run/emergency-stop" };
}

message EmergencyStopRequest {
  enum Mode {
    MODE_UNSPECIFIED = 0;
    SOFT = 1;      // graceful drain
    HARD = 2;      // immediate kill
    HUMAN = 3;     // human-review handoff
  }
  Mode mode = 1;
  string reason = 2;       // optional operator note
  string operator = 3;     // who requested it
}
```

### Webhook

```
POST /hooks/emergency-stop
Content-Type: application/json

{
  "mode": "hard",
  "reason": "Production incident requires all resources",
  "operator": "devops-bot"
}
```

---

## Stop Decision Tree (ASCII Flowchart)

```
                         ┌──────────────────────┐
                         │   Night Run Starts    │
                         └──────────┬───────────┘
                                    │
                         ┌──────────▼───────────┐
                         │  Pre-flight checks   │
                         │  (compile, raw-id,   │
                         │   CT 123, git, mods) │
                         └──────────┬───────────┘
                                    │
                         ┌──────────▼───────────┐
                         │  Any pre-flight      │
                         │  check fail?         │
                         └────┬──────────────┬──┘
                        ┌─────┘              └─────┐
                   ┌────▼────┐               ┌────▼────┐
                   │  HARD   │               │  PASS   │
                   │  STOP   │               └────┬────┘
                   └─────────┘                    │
                                          ┌───────▼────────┐
                                          │  Task Queue    │
                                          │  (dependency   │
                                          │   resolution)  │
                                          └───────┬────────┘
                                                  │
                                  ┌───────────────┼───────────────┐
                                  │               │               │
                            ┌─────▼────┐   ┌──────▼──────┐  ┌────▼─────┐
                            │ No safe  │   │ Contradiction│  │ Unknown  │
                            │ tasks?   │   │ detected?    │  │ module?  │
                            └─────┬────┘   └──────┬───────┘  └────┬─────┘
                          ┌───────┘               │              │
                     ┌────▼────┐          ┌───────▼──────┐  ┌───▼─────┐
                     │  SOFT   │          │    HARD      │  │  HARD   │
                     │  STOP   │          │    STOP      │  │  STOP   │
                     └─────────┘          └──────────────┘  └─────────┘
                                                  │
                                          ┌───────▼────────┐
                                          │  Execute Task  │
                                          └───────┬────────┘
                                                  │
                                  ┌───────────────┼───────────────────┐
                                  │               │                   │
                            ┌─────▼────┐   ┌──────▼──────┐    ┌──────▼──────┐
                            │ Bot crash│   │ Stuck loop  │    │ Blocked     │
                            │ loop?    │   │ detected?   │    │ overflow?   │
                            └─────┬────┘   └──────┬───────┘    └──────┬─────┘
                          ┌───────┘               │                   │
                     ┌────▼────┐          ┌───────▼──────┐    ┌──────▼─────┐
                     │  HARD   │          │    HARD      │    │   HARD     │
                     │  STOP   │          │    STOP      │    │   STOP     │
                     └─────────┘          └──────────────┘    └────────────┘
                                                  │
                                          ┌───────▼────────┐
                                          │  Evaluate      │
                                          │  confidence    │
                                          └───────┬────────┘
                                                  │
                                        ┌─────────┴──────────┐
                                        │                    │
                                   ┌────▼────┐         ┌────▼────┐
                                   │ Below   │         │  OK     │
                                   │ thresh? │         └────┬────┘
                                   └────┬────┘              │
                                        │          ┌────────▼────────┐
                                   ┌────▼────┐     │  Check batch & │
                                   │  HARD   │     │  time limits   │
                                   │  STOP   │     └────────┬────────┘
                                   └─────────┘              │
                                                 ┌──────────┴──────────┐
                                                 │                     │
                                            ┌────▼────┐          ┌────▼────┐
                                            │ Limit   │          │ More    │
                                            │ hit?    │          │ tasks?  │
                                            └────┬────┘          └────┬────┘
                                                 │                    │
                                            ┌────▼────┐         ┌────▼────┐
                                            │  SOFT   │         │  ──▶    │
                                            │  STOP   │         │ Task Q  │
                                            └─────────┘         └─────────┘
```

### Legend

```
Symbol           Meaning
────▶            Flow direction
┌──┐ └──┘ ──┬──  Decision / Process box
│ HARD STOP │  Immediate stop (exit code 10-99)
│ SOFT STOP │  Graceful drain (exit code 0 partial)
│  ──▶      │  Continue to next step
```

---

## Configuration Reference

All thresholds and limits are configurable via `night-run.yaml`:

```yaml
stop_conditions:
  max_blocked_tasks: 5
  confidence_floor: 0.4
  max_batches: 20
  max_consecutive_failures: 3
  safe_task_threshold: 0.3
  bot_crash_window_minutes: 5
  bot_crash_max_count: 3
  stuck_poll_cycles: 3
  time_limit_minutes: 360
  hard_stop_on_condition:
    - compile_fail
    - rawid_fail
    - ct123_down
    - git_dirty
    - merge_conflict
    - bot_crash_loop
    - stuck_loop
    - blocked_overflow
    - human_review
    - low_confidence
    - batch_limit
    - unknown_module
    - contradiction
```

---

## Failure Report Template

Every hard-stop report follows this structure:

```yaml
---
run_id: "nr-20260621-001"
timestamp: "2026-06-21T03:14:15Z"
condition: "STOP_COMPILE_FAIL"
stage: "build"
exit_code: 10
summary: "bazel build //... failed with exit code 1; see artifacts/build.log"
bots_running: 3
bots_killed: 3
artifacts:
  - "build.log"
  - "bazel-stderr.log"
notified:
  - slack
operator_required: false
```

---
*End of Document 3 — Autonomous Stop Conditions*

---

## 7. Iteration Budget Stop Condition

Added in v3.4. Guards against workers exhausting resources on oversized or already-existing tasks.

### Hard Stop: STOP_BUDGET_EXCEEDED

| Property | Value |
|:---------|:------|
| **Identifier** | `STOP_BUDGET_EXCEEDED` |
| **Trigger** | Worker exceeds the iteration budget for its task size class |
| **Budget Table** | See `docs/automation/task-sizing-policy.md` §4 |
| **Exit Code** | 100 |
| **Response** | Kill worker, write `failure-budget-exceeded.md`, block card |

### Hard Stop: STOP_NO_PREFLIGHT

| Property | Value |
|:---------|:------|
| **Identifier** | `STOP_NO_PREFLIGHT` |
| **Trigger** | 10 iterations without a pre-flight report posted to card comments |
| **Exit Code** | 101 |
| **Response** | Block card with reason `MISSING_PREFLIGHT`, tag for human review |

### Hard Stop: STOP_ALREADY_EXISTS

| Property | Value |
|:---------|:------|
| **Identifier** | `STOP_ALREADY_EXISTS` |
| **Trigger** | Worker produced code for a system that already exists on CT 123 |
| **Exit Code** | 102 |
| **Response** | Discard code, close card as `CLOSE_ALREADY_EXISTS`, optionally spawn validation card |

### Soft Stop: SOFT_BUDGET_WARNING

| Property | Value |
|:---------|:------|
| **Identifier** | `SOFT_BUDGET_WARNING` |
| **Trigger** | Worker at 75% of iteration budget with no validation result |
| **Behavior** | Post warning to card comment, allow continuation but flag for post-completion review |

### Integration with Hard Stop Table (Extended)

Add to the Immediate Stop Conditions table:

| # | Condition | Identifier | Trigger | Rationale |
|---|-----------|------------|---------|-----------|
| 14 | **Budget exceeded** | `STOP_BUDGET_EXCEEDED` | Worker iteration count > task size class hard cap | Worker caught in loop or oversized task — must break |
| 15 | **No pre-flight report** | `STOP_NO_PREFLIGHT` | 10 iterations without pre-flight post | Worker skipped reality check — likely burning budget on wrong task |
| 16 | **Already exists** | `STOP_ALREADY_EXISTS` | Worker wrote code for system found on CT 123 | Worker skipped pre-flight — code is redundant |
