# Task Risk Classifier

## Purpose

The **Task Risk Classifier** is the central decision-making component of the Night Run automation framework. It assigns a risk level to every incoming task based on the **content type** of the operation requested. This risk level determines:

- Whether the task can proceed **autonomously** or requires **human approval**.
- Which **execution mode** (e.g., `chat`, `auto`, `plan`, `agent`, `research`) is allowed or enforced.
- What **safety guardrails** (e.g., read-only sandbox, dry-run, rate limiting) are applied during execution.
- How the **Orchestrator** schedules, queues, and escalates the task.

By classifying tasks upfront, the framework prevents accidental or malicious destructive operations while still enabling full automation for low-risk workflows.

---

## Risk Levels

Five progressive risk levels, ordered from safest to most dangerous:

| Level | Identifier | Label | Description |
|-------|-----------|-------|-------------|
| 1 | `DOCUMENTATION` | **Documentation** | Read-only information retrieval, doc lookups, summarization. No side effects. |
| 2 | `LOW` | **Low Risk** | Non-destructive operations on isolated or sandboxed resources (e.g., listing files, reading logs). |
| 3 | `MODERATE` | **Moderate Risk** | Operations that modify non-critical state or create new resources in safe namespaces (e.g., creating ephemeral test files, triggering non-destructive pipelines). |
| 4 | `HIGH` | **High Risk** | Operations that alter production-adjacent state, modify persistent data, or affect external systems (e.g., writing to shared databases, deploying to staging, deleting files). |
| 5 | `DANGEROUS` | **Dangerous** | Operations that can cause irreversible damage, data loss, security breaches, or significant downtime (e.g., `DROP TABLE`, `rm -rf`, production deploys, credential rotation, firewall changes). |

Each level carries progressively stricter permission gates.

---

## Risk Classification Matrix

Content types are mapped to risk levels. The matrix below defines the **autonomous permission** for each content type — whether the Orchestrator may execute the task without human intervention.

| Content Type | Risk Level | Autonomous Permission | Notes |
|-------------|-----------|----------------------|-------|
| Documentation lookup | 1 — DOCUMENTATION | ✅ **Allow** | Read-only, fully automated |
| Code review / static analysis | 1 — DOCUMENTATION | ✅ **Allow** | Read-only analysis |
| Log reading / tailing | 2 — LOW | ✅ **Allow** | Read-only, sandboxed |
| File listing (non-sensitive dirs) | 2 — LOW | ✅ **Allow** | No modification |
| Ephemeral file creation (e.g., `/tmp`) | 3 — MODERATE | 🔶 **Allow with approval fallback** | Auto-approve inside sandbox; prompt for non-sandbox paths |
| Pipeline trigger (non-production) | 3 — MODERATE | 🔶 **Allow with approval fallback** | CI/CD on staging/branch |
| Database read query (read replica) | 2 — LOW | ✅ **Allow** | Read-only replica only |
| Database write (staging / non-prod) | 4 — HIGH | 🛑 **Requires human approval** | Must be explicitly confirmed |
| Database write (production) | 5 — DANGEROUS | 🛑 **Requires human approval** | Blocked unless overridden with explicit confirmation |
| File modification (non-critical) | 3 — MODERATE | 🔶 **Allow with approval fallback** | E.g., editing config in user home |
| File deletion (non-critical) | 4 — HIGH | 🛑 **Requires human approval** | Risk of accidental loss |
| File deletion (critical paths) | 5 — DANGEROUS | 🛑 **Requires human approval** | e.g., `/etc`, `/var/lib`, database files |
| Package install (system level) | 4 — HIGH | 🛑 **Requires human approval** | Affects system stability |
| Package install (user/local) | 3 — MODERATE | 🔶 **Allow with approval fallback** | Lower blast radius |
| Shell command execution (read-only) | 2 — LOW | ✅ **Allow** | e.g., `cat`, `ls`, `grep` |
| Shell command execution (write) | 4 — HIGH | 🛑 **Requires human approval** | e.g., `mv`, `rm`, `dd` |
| Shell command execution (destructive) | 5 — DANGEROUS | 🛑 **Requires human approval** | e.g., `rm -rf /`, `dd if=/dev/zero` |
| Network request (outbound, safe API) | 2 — LOW | ✅ **Allow** | Whitelisted endpoints |
| Network request (untrusted destination) | 4 — HIGH | 🛑 **Requires human approval** | Potential data exfiltration |
| Credential / secret access | 5 — DANGEROUS | 🛑 **Requires human approval** | Must audit-log and confirm |
| Infrastructure change (staging) | 4 — HIGH | 🛑 **Requires human approval** | Terraform / k8s staging changes |
| Infrastructure change (production) | 5 — DANGEROUS | 🛑 **Requires human approval** | Production infra mutations |
| Read-only AI inference | 1 — DOCUMENTATION | ✅ **Allow** | Model queries with no side effects |
| AI inference with tool execution | 3 — MODERATE | 🔶 **Allow with approval fallback** | Agent using tools |

### Autonomous Permission Levels

| Icon | Permission | Behavior |
|------|-----------|----------|
| ✅ Allow | Task executes immediately without human interaction. |
| 🔶 Allow with approval fallback | Task executes automatically in sandboxed/low-risk contexts; prompts for human approval when outside safe boundaries. |
| 🛑 Requires human approval | Task is queued and waits for explicit human confirmation before execution. |

---

## How the Orchestrator Uses Risk Levels

The **Night Run Orchestrator** integrates risk levels at every stage of the task lifecycle:

### 1. Intake & Classification

When a new task arrives (via Discord, API, cron, or file watch), the Orchestrator first inspects the **content type** of the requested action (e.g., "read file", "delete resource", "execute SQL"). It then looks up the content type in the Risk Classification Matrix to determine the risk level and autonomous permission.

```
Task arrives
  │
  ▼
Content Type Extraction
  │
  ▼
Risk Matrix Lookup ──► Risk Level (1-5)
  │
  ▼
Autonomous Permission Check
```

### 2. Mode Assignment

Based on the risk level, the Orchestrator selects an appropriate **execution mode** (see Mode-to-Risk-Level Mapping below). Higher-risk tasks are routed to modes that require more human oversight (e.g., `chat` or `plan`), while low-risk tasks can run in fully autonomous modes (`auto`, `agent`).

### 3. Execution Guardrails

The risk level controls runtime safeguards:

- **Level 1-2**: No special restrictions; full speed.
- **Level 3**: Dry-run / preview enabled where possible; execution is logged with full context; approval fallback activated for operations outside sandbox.
- **Level 4**: Human approval required; task is queued with a pending confirmation message; timeout for approval can be configured (default: 5 minutes); full audit trail captured.
- **Level 5**: Human approval required + additional confirmation ("Are you absolutely sure?"); rate-limiting applied; mandatory notification sent to designated channel; execution blocked if any guardrail is unresponsive.

### 4. Escalation & Override

If a task is blocked by its risk level, the Orchestrator provides a mechanism for **human override**:

- An authorized user can explicitly approve the task (via reaction, command, or API).
- The approval is logged alongside the task ID for audit purposes.
- Override permissions themselves are configurable per role (e.g., admin can override DANGEROUS, operator can override HIGH).

### 5. Post-Execution Audit

After a task completes, the Orchestrator records:

- Risk level assigned
- Whether autonomous execution or human approval was used
- Who approved (if applicable)
- What guardrails were active
- Execution outcome (success / failure / partial)

This audit trail feeds into continuous improvement of the risk matrix.

---

## Mode-to-Risk-Level Mapping

Each execution mode in the Night Run framework supports a specific range of risk levels. Tasks requesting a mode outside their allowed range are either **rejected** or **re-routed** to a compatible mode.

| Mode | Supported Risk Levels | Behavior | Typical Use Case |
|------|----------------------|----------|-----------------|
| **chat** | 1–5 (all) | Conversational mode with human in the loop at every turn. All autonomously risky operations prompt for confirmation. | General assistance, debugging, ad-hoc operations |
| **auto** | 1–2 only | Fully autonomous, no human confirmation needed. Fails closed if a task exceeds level 2. | Documentation lookups, log analysis, read-only automation |
| **plan** | 1–5 (all) | Always generates a plan and presents it for human review before executing. Can handle any risk level because execution only proceeds after approval. | Complex multi-step operations, infrastructure changes, production tasks |
| **agent** | 1–3 only | Autonomous execution with tool-use, but limited to moderate risk. Prompts for approval on level 4+ tasks. | Automated file operations, pipeline triggers, data processing |
| **research** | 1–2 only | Deep research mode — read-only, non-destructive by design. Web searches, document analysis, data synthesis. | Long-running analysis, competitive research, codebase understanding |
| **guard** | 1–5 (monitor only) | Passive monitoring mode. Does not execute tasks but observes and reports risk levels. | Safety monitoring, auditing, alerting |
| **emergency** | 5 only (DANGEROUS) | Special override mode requiring explicit multi-factor approval. Only available to admins. | Incident response, emergency rollback, security containment |

### Mode Routing Logic

```
Task Risk Level 1-2  ──► auto, agent, research, chat, plan
Task Risk Level 3    ──► agent, chat, plan  (auto/research rejected)
Task Risk Level 4    ──► chat, plan          (auto/agent/research rejected)
Task Risk Level 5    ──► chat, plan, emergency (all others rejected)
```

If a user sends a task in a mode incompatible with its risk level, the Orchestrator:

1. Warns the user that the task exceeds the mode's risk ceiling.
2. Suggests an alternative mode that can handle the task (e.g., "This task requires level 4 approval. Switching to `plan` mode.").
3. If no automatic re-routing is possible, returns an actionable error with instructions.

---

## Configuration

The risk classification matrix and mode mappings are configurable via YAML at `config/risk-classifier.yaml`. Administrators can:

- Add new content types and assign risk levels.
- Adjust autonomous permission thresholds.
- Whitelist or blacklist specific content types for certain modes.
- Configure approval timeouts and escalation channels.

Example snippet:

```yaml
risk_levels:
  documentation: 1
  low: 2
  moderate: 3
  high: 4
  dangerous: 5

content_types:
  file_read:
    risk_level: low
    autonomous: true
  file_delete:
    risk_level: high
    autonomous: false
  db_write_prod:
    risk_level: dangerous
    autonomous: false
```

---

## Summary

The Task Risk Classifier provides a structured, auditable, and configurable way to answer the question:

> **"Can this task run safely without human supervision?"**

By mapping content types to risk levels and risk levels to execution modes, the Night Run automation framework balances **efficiency** (fully autonomous for safe tasks) with **safety** (mandatory human approval for dangerous operations). This layered approach is essential for operating reliably in production environments where mistakes are costly.
