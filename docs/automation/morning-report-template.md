# Morning Report — {{RUN_DATE}}

**Generated:** {{GENERATED_AT}}
**Run Coordinator:** {{COORDINATOR}}

---

## Run Summary

| Field | Value |
|-------|-------|
| **Mode** | {{RUN_MODE}} |
| **Start Time** | {{START_TIME}} |
| **End Time** | {{END_TIME}} |
| **Duration** | {{DURATION}} |
| **Overall Status** | {{OVERALL_STATUS}} |
| **Exit Condition** | {{EXIT_CONDITION}} |

---

## Tasks Attempted

| # | Task ID | Description | Status |
|---|---------|-------------|--------|
| 1 | {{TASK_1_ID}} | {{TASK_1_DESC}} | {{TASK_1_STATUS}} |
| 2 | {{TASK_2_ID}} | {{TASK_2_DESC}} | {{TASK_2_STATUS}} |
| 3 | {{TASK_3_ID}} | {{TASK_3_DESC}} | {{TASK_3_STATUS}} |
| 4 | {{TASK_4_ID}} | {{TASK_4_DESC}} | {{TASK_4_STATUS}} |
| 5 | {{TASK_5_ID}} | {{TASK_5_DESC}} | {{TASK_5_STATUS}} |

**Total Attempted:** {{TASKS_ATTEMPTED_COUNT}}

---

## Tasks Completed

- [x] **{{COMPLETED_TASK_1}}** — {{COMPLETED_TASK_1_DETAIL}} ({{COMPLETED_TASK_1_DURATION}})
- [x] **{{COMPLETED_TASK_2}}** — {{COMPLETED_TASK_2_DETAIL}} ({{COMPLETED_TASK_2_DURATION}})
- [x] **{{COMPLETED_TASK_3}}** — {{COMPLETED_TASK_3_DETAIL}} ({{COMPLETED_TASK_3_DURATION}})

**Completion Rate:** {{COMPLETION_RATE}}% ({{TASKS_COMPLETED}} / {{TASKS_ATTEMPTED_COUNT}})

---

## Tasks Blocked

| Task ID | Block Reason | Owner | Resolution Path |
|---------|-------------|-------|-----------------|
| {{BLOCKED_TASK_1_ID}} | {{BLOCKED_TASK_1_REASON}} | {{BLOCKED_TASK_1_OWNER}} | {{BLOCKED_TASK_1_RESOLUTION}} |
| {{BLOCKED_TASK_2_ID}} | {{BLOCKED_TASK_2_REASON}} | {{BLOCKED_TASK_2_OWNER}} | {{BLOCKED_TASK_2_RESOLUTION}} |
| {{BLOCKED_TASK_3_ID}} | {{BLOCKED_TASK_3_REASON}} | {{BLOCKED_TASK_3_OWNER}} | {{BLOCKED_TASK_3_RESOLUTION}} |

### Common Block Categories
- 🔴 **Dependency unavailable** — {{BLOCK_DEP_UNAVAIL_COUNT}} occurrences
- 🔴 **API/Service failure** — {{BLOCK_API_FAIL_COUNT}} occurrences
- 🔴 **Missing credentials** — {{BLOCK_CRED_COUNT}} occurrences
- 🔴 **Resource exhaustion** — {{BLOCK_RESOURCE_EXHAUST_COUNT}} occurrences
- 🔴 **Time constraint** — {{BLOCK_TIME_COUNT}} occurrences
- 🟡 **Awaiting human decision** — {{BLOCK_HUMAN_DECISION_COUNT}} occurrences

---

## Commits Made

| Hash | Description | Author | Branch | Timestamp |
|------|------------|--------|--------|-----------|
| `{{COMMIT_HASH_1}}` | {{COMMIT_DESC_1}} | {{COMMIT_AUTHOR_1}} | `{{COMMIT_BRANCH_1}}` | {{COMMIT_TIME_1}} |
| `{{COMMIT_HASH_2}}` | {{COMMIT_DESC_2}} | {{COMMIT_AUTHOR_2}} | `{{COMMIT_BRANCH_2}}` | {{COMMIT_TIME_2}} |
| `{{COMMIT_HASH_3}}` | {{COMMIT_DESC_3}} | {{COMMIT_AUTHOR_3}} | `{{COMMIT_BRANCH_3}}` | {{COMMIT_TIME_3}} |

**Total Commits:** {{TOTAL_COMMITS}}

---

## Files Changed

**Total Files Changed:** {{FILES_CHANGED_COUNT}}

### Key Files Modified

| File | Change Type | Summary |
|------|------------|---------|
| `{{FILE_1_PATH}}` | {{FILE_1_CHANGE_TYPE}} | {{FILE_1_SUMMARY}} |
| `{{FILE_2_PATH}}` | {{FILE_2_CHANGE_TYPE}} | {{FILE_2_SUMMARY}} |
| `{{FILE_3_PATH}}` | {{FILE_3_CHANGE_TYPE}} | {{FILE_3_SUMMARY}} |
| `{{FILE_4_PATH}}` | {{FILE_4_CHANGE_TYPE}} | {{FILE_4_SUMMARY}} |
| `{{FILE_5_PATH}}` | {{FILE_5_CHANGE_TYPE}} | {{FILE_5_SUMMARY}} |

### Change Type Legend
- `ADD` — New file created
- `MOD` — Existing file modified
- `DEL` — File deleted
- `REN` — File renamed/moved

---

## Validations Run

### Compile / Build

| Component | Status | Duration | Output / Errors |
|-----------|--------|----------|-----------------|
| {{BUILD_COMPONENT_1}} | {{BUILD_STATUS_1}} | {{BUILD_DURATION_1}} | {{BUILD_OUTPUT_1}} |
| {{BUILD_COMPONENT_2}} | {{BUILD_STATUS_2}} | {{BUILD_DURATION_2}} | {{BUILD_OUTPUT_2}} |
| {{BUILD_COMPONENT_3}} | {{BUILD_STATUS_3}} | {{BUILD_DURATION_3}} | {{BUILD_OUTPUT_3}} |

### Raw-ID / Asset Validation

| Check | Status | Details |
|-------|--------|---------|
| {{RAW_ID_CHECK_1}} | {{RAW_ID_STATUS_1}} | {{RAW_ID_DETAIL_1}} |
| {{RAW_ID_CHECK_2}} | {{RAW_ID_STATUS_2}} | {{RAW_ID_DETAIL_2}} |
| {{RAW_ID_CHECK_3}} | {{RAW_ID_STATUS_3}} | {{RAW_ID_DETAIL_3}} |

### Lint / Static Analysis

| Tool | Status | Issues Found |
|------|--------|-------------|
| {{LINT_TOOL_1}} | {{LINT_STATUS_1}} | {{LINT_ISSUES_1}} |
| {{LINT_TOOL_2}} | {{LINT_STATUS_2}} | {{LINT_ISSUES_2}} |

---

## Playerbot / Agent QA Results

### Bot Performance

| Bot Name | Tasks Assigned | Tasks Completed | Success Rate | Avg Response Time | Notes |
|----------|---------------|-----------------|-------------|-------------------|-------|
| {{BOT_1_NAME}} | {{BOT_1_ASSIGNED}} | {{BOT_1_COMPLETED}} | {{BOT_1_SUCCESS_RATE}}% | {{BOT_1_AVG_RESP_TIME}}ms | {{BOT_1_NOTES}} |
| {{BOT_2_NAME}} | {{BOT_2_ASSIGNED}} | {{BOT_2_COMPLETED}} | {{BOT_2_SUCCESS_RATE}}% | {{BOT_2_AVG_RESP_TIME}}ms | {{BOT_2_NOTES}} |
| {{BOT_3_NAME}} | {{BOT_3_ASSIGNED}} | {{BOT_3_COMPLETED}} | {{BOT_3_SUCCESS_RATE}}% | {{BOT_3_AVG_RESP_TIME}}ms | {{BOT_3_NOTES}} |

### Agent Decision Quality

| Agent | Sound Decisions | Poor Decisions | Neutral / Unclear | Soundness % | Notable Interactions |
|-------|----------------|---------------|-------------------|-------------|---------------------|
| {{AGENT_1_NAME}} | {{AGENT_1_SOUND}} | {{AGENT_1_POOR}} | {{AGENT_1_NEUTRAL}} | {{AGENT_1_SOUNDNESS}}% | {{AGENT_1_NOTABLE}} |
| {{AGENT_2_NAME}} | {{AGENT_2_SOUND}} | {{AGENT_2_POOR}} | {{AGENT_2_NEUTRAL}} | {{AGENT_2_SOUNDNESS}}% | {{AGENT_2_NOTABLE}} |

### Observations
{{QA_OBSERVATIONS}}

---

## Failures and Stuck States

| Timestamp | Component | Failure / Stuck Description | Root Cause | Auto-Recovery | Human Intervention Required |
|-----------|-----------|----------------------------|------------|---------------|----------------------------|
| {{FAIL_TIME_1}} | {{FAIL_COMPONENT_1}} | {{FAIL_DESC_1}} | {{FAIL_CAUSE_1}} | {{FAIL_RECOVERY_1}} | {{FAIL_HUMAN_1}} |
| {{FAIL_TIME_2}} | {{FAIL_COMPONENT_2}} | {{FAIL_DESC_2}} | {{FAIL_CAUSE_2}} | {{FAIL_RECOVERY_2}} | {{FAIL_HUMAN_2}} |
| {{FAIL_TIME_3}} | {{FAIL_COMPONENT_3}} | {{FAIL_DESC_3}} | {{FAIL_CAUSE_3}} | {{FAIL_RECOVERY_3}} | {{FAIL_HUMAN_3}} |

### Stuck State Summary
- **Total Stuck Events:** {{STUCK_EVENT_COUNT}}
- **Total Downtime:** {{STUCK_TOTAL_DOWNTIME}}
- **Longest Stuck Duration:** {{STUCK_LONGEST_DURATION}} ({{STUCK_LONGEST_COMPONENT}})

---

## Worklog Links

- **Run Log:** {{RUN_LOG_LINK}}
- **Build Artifacts:** {{BUILD_ARTIFACTS_LINK}}
- **Test Results:** {{TEST_RESULTS_LINK}}
- **QA Dashboard:** {{QA_DASHBOARD_LINK}}
- **GitHub / GitLab:** {{REPO_LINK}}
- **CI/CD Pipeline:** {{CI_PIPELINE_LINK}}
- **Incident Tracker:** {{INCIDENT_TRACKER_LINK}}

---

## Roadmap / Status Changes

### Milestones Affected

| Milestone | Previous ETA | New ETA | Impact | Notes |
|-----------|-------------|---------|--------|-------|
| {{MILESTONE_1_NAME}} | {{MILESTONE_1_PREV_ETA}} | {{MILESTONE_1_NEW_ETA}} | {{MILESTONE_1_IMPACT}} | {{MILESTONE_1_NOTES}} |
| {{MILESTONE_2_NAME}} | {{MILESTONE_2_PREV_ETA}} | {{MILESTONE_2_NEW_ETA}} | {{MILESTONE_2_IMPACT}} | {{MILESTONE_2_NOTES}} |

### Status Flags
- 🟢 **On track** — {{ON_TRACK_ITEMS}}
- 🟡 **At risk** — {{AT_RISK_ITEMS}}
- 🔴 **Behind** — {{BEHIND_ITEMS}}
- ⚪ **Completed this run** — {{COMPLETED_THIS_RUN}}

---

## Recommended Next Human Decisions

| Priority | Decision Required | Context | Suggested Action | Deadline |
|----------|------------------|---------|------------------|----------|
| 🔴 High | {{DECISION_1_QUESTION}} | {{DECISION_1_CONTEXT}} | {{DECISION_1_SUGGESTION}} | {{DECISION_1_DEADLINE}} |
| 🟡 Medium | {{DECISION_2_QUESTION}} | {{DECISION_2_CONTEXT}} | {{DECISION_2_SUGGESTION}} | {{DECISION_2_DEADLINE}} |
| 🟢 Low | {{DECISION_3_QUESTION}} | {{DECISION_3_CONTEXT}} | {{DECISION_3_SUGGESTION}} | {{DECISION_3_DEADLINE}} |

### Quick-Response Items (Estimated < 5 min)
- {{QUICK_DECISION_1}}
- {{QUICK_DECISION_2}}

---

## Notes / Commentary

{{RUN_NOTES}}

---

## Appendix: Example Completed Report

Below is a worked example of a completed morning report for reference.

---

# Morning Report — 2026-06-20

**Generated:** 2026-06-20T08:30:00Z
**Run Coordinator:** night-run-bot-v2

---

## Run Summary

| Field | Value |
|-------|-------|
| **Mode** | Full Automation (heads-down) |
| **Start Time** | 2026-06-20T02:00:00Z |
| **End Time** | 2026-06-20T08:00:00Z |
| **Duration** | 6h 0m 12s |
| **Overall Status** | ⚠️ Partial Success |
| **Exit Condition** | Run timeout (max 6h reached) |

---

## Tasks Attempted

| # | Task ID | Description | Status |
|---|---------|-------------|--------|
| 1 | T-241 | Bootstrap authentication service | ✅ Complete |
| 2 | T-242 | Integrate playerbot matchmaking API | ✅ Complete |
| 3 | T-243 | Implement raw-ID asset validation pipeline | ⚠️ Partial |
| 4 | T-244 | Refactor inventory module | ❌ Blocked |
| 5 | T-245 | Write integration tests for quest system | ✅ Complete |

**Total Attempted:** 5

---

## Tasks Completed

- [x] **T-241 — Bootstrap authentication service** — Set up OAuth2 provider, JWT signing, and user session middleware. Unit tests passing. (1h 45m)
- [x] **T-242 — Integrate playerbot matchmaking API** — Connected to the matchmaking endpoint, added retry logic with exponential backoff. E2E test verified. (2h 10m)
- [x] **T-245 — Write integration tests for quest system** — 34 test cases covering accept, progress, completion, and reward distribution. All green. (1h 20m)

**Completion Rate:** 60% (3 / 5)

---

## Tasks Blocked

| Task ID | Block Reason | Owner | Resolution Path |
|---------|-------------|-------|-----------------|
| T-244 | Refactor inventory module — depends on T-243 sub-task (asset schema migration) which is incomplete | @dev-alice | Complete T-243 first or split into independent PR |
| T-243 | Raw-ID schema v3 not yet deployed to staging; validation pipeline cannot be fully tested | @ops-bob | Deploy schema migration to staging, expected EOD 2026-06-21 |

### Common Block Categories
- 🔴 **Dependency unavailable** — 2 occurrences
- 🔴 **API/Service failure** — 0 occurrences
- 🔴 **Missing credentials** — 0 occurrences
- 🔴 **Resource exhaustion** — 0 occurrences
- 🔴 **Time constraint** — 1 occurrence (run timeout)
- 🟡 **Awaiting human decision** — 1 occurrence (schema migration approval)

---

## Commits Made

| Hash | Description | Author | Branch | Timestamp |
|------|------------|--------|--------|-----------|
| `a1b2c3d` | feat(auth): bootstrap OAuth2 provider with JWT signing | night-run-bot | `feat/auth-service` | 2026-06-20T03:45:00Z |
| `e4f5g6h` | feat(matchmaking): integrate playerbot matchmaking API | night-run-bot | `feat/matchmaking` | 2026-06-20T05:30:00Z |
| `i7j8k9l` | test(quests): add integration tests for quest lifecycle | night-run-bot | `feat/quest-tests` | 2026-06-20T07:15:00Z |
| `m0n1o2p` | chore(deps): bump lodash from 4.17.21 to 4.17.22 | dependabot | `dependabot/npm` | 2026-06-20T04:22:00Z |

**Total Commits:** 4

---

## Files Changed

**Total Files Changed:** 24

### Key Files Modified

| File | Change Type | Summary |
|------|------------|---------|
| `src/auth/provider.go` | ADD | New OAuth2 provider implementation (320 lines) |
| `src/auth/middleware.go` | ADD | JWT validation middleware (180 lines) |
| `src/matchmaking/client.go` | ADD | Playerbot matchmaking API client (240 lines) |
| `src/matchmaking/retry.go` | ADD | Retry logic with exponential backoff (95 lines) |
| `tests/quests/integration_test.go` | ADD | Quest lifecycle integration tests (410 lines) |
| `src/inventory/manager.go` | MOD | Partial inventory refactor (blocked, stashed) |
| `go.mod` | MOD | Dependency updates (lodash bump, new deps) |

---

## Validations Run

### Compile / Build

| Component | Status | Duration | Output / Errors |
|-----------|--------|----------|-----------------|
| auth-service | ✅ Pass | 12s | Clean build, 0 warnings |
| matchmaking-service | ✅ Pass | 10s | Clean build, 0 warnings |
| quest-service | ✅ Pass | 14s | Clean build, 0 warnings |
| inventory-service | ❌ Fail | 8s | Compilation error: undefined symbol `AssetSchemaV3` in `manager.go:142` |

### Raw-ID / Asset Validation

| Check | Status | Details |
|-------|--------|---------|
| Asset schema v2 → v3 migration | ⚠️ Skipped | Schema v3 not deployed to staging environment |
| Raw-ID fingerprint consistency | ✅ Pass | All 1,423 assets checked, 0 mismatches |
| Duplicate ID scan | ✅ Pass | No duplicate raw-IDs found across asset database |

### Lint / Static Analysis

| Tool | Status | Issues Found |
|------|--------|-------------|
| golangci-lint | ⚠️ 3 Warnings | 2 unused params (minor), 1 missing error check (inventory/manager.go:88) |
| eslint (frontend) | ✅ Pass | 0 issues |

---

## Playerbot / Agent QA Results

### Bot Performance

| Bot Name | Tasks Assigned | Tasks Completed | Success Rate | Avg Response Time | Notes |
|----------|---------------|-----------------|-------------|-------------------|-------|
| builder-bot | 3 | 3 | 100% | 320ms | All builds completed cleanly |
| tester-bot | 2 | 2 | 100% | 450ms | Quest integration tests passed, unit tests green |
| matchmaker-bot | 1 | 1 | 100% | 280ms | Matchmaking API integration verified |
| inventory-bot | 1 | 0 | 0% | — | Blocked on schema dependency, no task started |

### Agent Decision Quality

| Agent | Sound Decisions | Poor Decisions | Neutral / Unclear | Soundness % | Notable Interactions |
|-------|----------------|---------------|-------------------|-------------|---------------------|
| coordinator-agent | 12 | 1 | 2 | 92% | One poor decision: continued retrying inventory build 4 times before marking blocked |
| qa-agent | 8 | 0 | 0 | 100% | Flagged schema mismatch early, preventing wasted work |

### Observations
- coordinator-agent correctly escalated the T-244 block after 4 retries (could have escalated after 2 — minor efficiency loss).
- qa-agent proactively identified the asset schema dependency chain and paused validation to avoid false negatives.
- All bots successfully cleaned up temporary resources after task completion.

---

## Failures and Stuck States

| Timestamp | Component | Failure / Stuck Description | Root Cause | Auto-Recovery | Human Intervention Required |
|-----------|-----------|----------------------------|------------|---------------|----------------------------|
| 2026-06-20T06:15:00Z | inventory-service | Failed to compile after asset schema update (symbol not found) | AssetSchemaV3 not yet deployed to staging | Retried 4× with 30s backoff; marked blocked | Yes — @ops-bob to deploy schema v3 to staging |
| 2026-06-20T05:00:00Z | matchmaker-bot | Transient 503 from matchmaking API | Downstream service restart — self-healed in 45s | Retry with backoff succeeded on 3rd attempt | No |

### Stuck State Summary
- **Total Stuck Events:** 2
- **Total Downtime:** 12m 30s
- **Longest Stuck Duration:** 8m 15s (inventory-service)

---

## Worklog Links

- **Run Log:** https://ci.example.com/runs/2026-06-20/log
- **Build Artifacts:** https://artifacts.example.com/runs/2026-06-20/
- **Test Results:** https://ci.example.com/runs/2026-06-20/tests
- **QA Dashboard:** https://qa.example.com/dashboard?date=2026-06-20
- **GitHub / GitLab:** https://github.com/org/project
- **CI/CD Pipeline:** https://ci.example.com/pipelines/12345
- **Incident Tracker:** https://incidents.example.com/?q=date:2026-06-20

---

## Roadmap / Status Changes

### Milestones Affected

| Milestone | Previous ETA | New ETA | Impact | Notes |
|-----------|-------------|---------|--------|-------|
| Auth Service MVP | 2026-06-22 | 2026-06-22 (unchanged) | None | On track |
| Matchmaking Integration | 2026-06-25 | 2026-06-25 (unchanged) | None | On track |
| Asset Validation Pipeline | 2026-06-28 | 2026-07-01 | ⚠️ +3 days delay | Blocked on schema deployment |
| Inventory Refactor | 2026-07-02 | 2026-07-05 | ⚠️ +3 days delay | Dependent on asset validation pipeline |

### Status Flags
- 🟢 **On track** — Auth service, matchmaking integration, quest tests
- 🟡 **At risk** — Asset validation pipeline (waiting on ops)
- 🔴 **Behind** — Inventory refactor (blocked by asset validation)
- ⚪ **Completed this run** — Auth service, matchmaking integration, quest integration tests

---

## Recommended Next Human Decisions

| Priority | Decision Required | Context | Suggested Action | Deadline |
|----------|------------------|---------|------------------|----------|
| 🔴 High | Approve asset schema v3 staging deployment? | Inventory refactor and asset validation pipeline are blocked awaiting schema v3 on staging | Approve deployment to staging; ETA 30m | 2026-06-20 12:00 UTC |
| 🟡 Medium | Should T-244 be split into independent sub-tasks? | Inventory refactor is large; could extract non-schema-dependent changes into a separate PR | Approve splitting T-244 into T-244a (schema-independent) and T-244b (schema-dependent) | 2026-06-21 12:00 UTC |
| 🟢 Low | Add more aggressive escalation thresholds for coordinator-agent? | coordinator-agent retried blocked task 4 times before escalating; 2 retries may be sufficient | Reduce retry limit from 4 to 2 for compile failures | 2026-06-22 |

### Quick-Response Items (Estimated < 5 min)
- Approve schema v3 staging deployment (P0 blocker)
- Confirm T-244 split approach

---

## Notes / Commentary

Overall a productive night run despite the inventory block. Three of five tasks completed cleanly. The asset schema dependency chain was the primary bottleneck — this is a known risk that was flagged in the previous run's recommendations. The coordinator-agent handled the transient API failure well with exponential backoff. Minor optimization opportunity on retry limits (see low-priority decision item).

Next run should prioritize clearing the schema deployment blocker first, then completing the asset validation pipeline and inventory refactor.

---

*End of Morning Report — {{RUN_DATE}}*
