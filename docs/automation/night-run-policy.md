# Night Run Policy

## Purpose

This document defines the operating policies, safety constraints, mode transitions, and task governance for automated and semi-automated night-run operations. It ensures that all runtime activities remain within a predefined safety envelope while allowing varying degrees of autonomy depending on the active operating mode.

The policy applies to all orchestrator-driven tasks executed between sundown and sunrise, or during any period designated as a "night run" by the system operator. Adherence to this policy is mandatory for all automation subsystems, including the Orchestrator, the Safety Monitor, and all connected agents.

---

## Operating Modes

The system supports five distinct operating modes, each defining a different level of autonomy and human involvement.

### MANUAL_MODE

**Description:** All actions require explicit human initiation and confirmation. The system may suggest tasks but will not execute any automation without direct operator consent.

**Characteristics:**
- No autonomous task execution
- All orchestration commands are queued for operator review
- Safety Monitor is passive (warnings only, no intervention)
- Default mode on initial startup

**Use case:** System commissioning, testing, debugging, or when full human control is required.

---

### ASSISTED_MODE

**Description:** The system may propose and queue tasks but waits for human approval before executing each one. Commonly referred to as "confirm-by-default."

**Characteristics:**
- Tasks are suggested and pre-validated but require explicit approval
- Safety Monitor is in advisory mode (flags risks, does not block)
- Operator can approve, reject, or defer individual tasks
- Recommended for normal operations with human oversight

**Use case:** Routine nightly maintenance where human judgment is desired but the system does the heavy lifting.

---

### SAFE_AUTONOMOUS_MODE

**Description:** The system may autonomously execute tasks that fall within a predefined **low-risk task list** and meet all **safety envelope** criteria. Any task outside the approved list or envelope requires human escalation.

**Characteristics:**
- Autonomous execution for pre-approved low-risk tasks only
- Safety Monitor actively enforces the safety envelope (can pause/abort)
- Operator is notified of all executed tasks (async log)
- Escalation required for any non-approved action

**Use case:** Trusted, repeatable maintenance tasks in controlled environments.

---

### NIGHT_RUN_MODE

**Description:** The highest autonomy level. The system may execute any task that is explicitly listed in the **Allowed NIGHT_RUN_MODE Tasks** section below. The Safety Monitor remains active but the escalation threshold is raised — only violations of the safety envelope trigger intervention.

**Characteristics:**
- Autonomous execution of all allowed tasks
- Safety Monitor enforces the safety envelope (hard boundary)
- Operator receives summary reports at configurable intervals
- Orchestrator may chain dependent tasks without human interruption
- The system may self-correct within allowed boundaries

**Use case:** Fully unattended overnight operations, batch processing, scheduled maintenance windows.

---

### LOCKDOWN_MODE

**Description:** All automation is suspended. No tasks may be queued, executed, or proposed. The system enters a read-only state for operational review.

**Characteristics:**
- No new tasks accepted
- Active tasks are paused (if safe) or aborted (if required by policy)
- Safety Monitor escalates to emergency contacts
- Only a human operator with appropriate credentials may exit LOCKDOWN_MODE
- Audit logging is elevated to maximum verbosity

**Use case:** Security incidents, critical system failures, policy violations detected by the Safety Monitor, or operator-initiated emergency stop.

---

## Allowed NIGHT_RUN_MODE Tasks

The following task categories are approved for autonomous execution in NIGHT_RUN_MODE. Each task must also pass the **Safety Envelope** (see below) before execution.

### System Maintenance
- Log rotation and archival
- Temporary file cleanup (cache, staging, temp directories)
- Database vacuum and reindex operations
- Certificate renewal checks (no deployment without verification)
- Filesystem disk usage checks and low-space alerts

### Monitoring & Telemetry
- System health checks (CPU, memory, disk, network)
- Service uptime verification and restart of failed services within safety envelope
- Metric collection and forwarding to monitoring infrastructure
- Log tailing for error pattern detection

### Scheduled Jobs
- Report generation (pre-defined templates only)
- Data synchronization between approved endpoints
- Backup initiation and integrity verification
- Cron job review and reconciliation

### Orchestration
- Task queue draining and retry of failed tasks (max 3 retries per task)
- Workflow resumption from safe checkpoints
- Dependency graph validation
- Resource scaling within configured min/max bounds

---

## Forbidden Tasks (Needs Human Approval)

The following tasks are **never** permitted without explicit human approval, regardless of operating mode. Attempting to queue or execute these tasks autonomously will trigger an immediate escalation and, if in NIGHT_RUN_MODE, a transition to LOCKDOWN_MODE.

### Infrastructure Changes
- Provisioning or decommissioning of infrastructure resources
- Changes to networking rules (firewall, routing, DNS)
- Modifications to IAM roles, service accounts, or access control policies
- Certificate deployment or key rotation
- Database schema migrations or destructive queries (DROP, TRUNCATE, etc.)

### Configuration Changes
- Modifications to system-level configuration files
- Changes to monitoring alert thresholds or notification routes
- Alteration of any mode transition rule (this document)
- Deployment of new software versions or rollback of existing ones
- Changes to backup or recovery procedures

### Security-Sensitive Operations
- User account creation, modification, or deletion
- Password or secret rotation
- Access log tampering or deletion
- Installation of unsigned or unverified packages
- Changes to encryption settings or TLS configurations

### Destructive Actions
- Bulk delete operations on production data
- Filesystem operations outside designated working directories
- Process termination of critical system services (unless explicitly listed in allowed tasks)
- System reboot or shutdown (except under verified emergency protocol)

---

## Mode Transition Rules

Transitions between operating modes follow strict rules. Invalid transitions are rejected by the Orchestrator.

### Allowed Transitions

| From | To | Trigger | Authorization Required |
|------|----|---------|----------------------|
| MANUAL_MODE | ASSISTED_MODE | Operator command | Operator |
| MANUAL_MODE | SAFE_AUTONOMOUS_MODE | Operator command | Operator |
| MANUAL_MODE | LOCKDOWN_MODE | Operator command or Safety Monitor | Operator or automatic |
| ASSISTED_MODE | MANUAL_MODE | Operator command | Operator |
| ASSISTED_MODE | SAFE_AUTONOMOUS_MODE | Operator command | Operator |
| ASSISTED_MODE | LOCKDOWN_MODE | Operator command or Safety Monitor | Operator or automatic |
| SAFE_AUTONOMOUS_MODE | ASSISTED_MODE | Operator command or Safety Monitor | Operator or automatic |
| SAFE_AUTONOMOUS_MODE | LOCKDOWN_MODE | Operator command or Safety Monitor | Operator or automatic |
| SAFE_AUTONOMOUS_MODE | NIGHT_RUN_MODE | Operator command | Operator (elevated) |
| NIGHT_RUN_MODE | SAFE_AUTONOMOUS_MODE | Operator command or Safety Monitor | Operator or automatic |
| NIGHT_RUN_MODE | LOCKDOWN_MODE | Operator command or Safety Monitor | Operator or automatic |
| LOCKDOWN_MODE | MANUAL_MODE | Operator command | Operator (elevated credentials required) |

### Automatic Transition Rules

The Safety Monitor may trigger an automatic transition under these conditions:

1. **SAFE_AUTONOMOUS_MODE → LOCKDOWN_MODE:** Safety envelope breach detected.
2. **NIGHT_RUN_MODE → LOCKDOWN_MODE:** Safety envelope breach detected, or forbidden task attempted.
3. **NIGHT_RUN_MODE → SAFE_AUTONOMOUS_MODE:** Unusual but non-critical anomaly detected (de-escalation for caution).
4. **ASSISTED_MODE → LOCKDOWN_MODE:** Repeated safety warnings ignored by operator.

### Transition Constraints

- **No direct transition** from MANUAL_MODE to NIGHT_RUN_MODE — must pass through SAFE_AUTONOMOUS_MODE.
- **No direct transition** from LOCKDOWN_MODE to any mode other than MANUAL_MODE.
- Transitions are logged with full context (trigger, timestamp, actor, previous mode, new mode).
- Mode transition requests that violate these rules are rejected with a descriptive error.

---

## Safety Envelope

The safety envelope defines the boundary within which autonomous operations are permitted. Any operation that would cause the system to exit this envelope is blocked or causes an automatic mode transition.

### Resource Thresholds

| Resource | Warning Threshold | Critical Threshold | Action |
|----------|-------------------|--------------------|--------|
| CPU usage | > 80% for 5 min | > 95% for 1 min | Pause non-essential tasks; abort at critical |
| Memory usage | > 85% | > 95% | Halt new task creation; abort critical |
| Disk usage (system) | > 80% | > 95% | Block write-heavy tasks; escalate |
| Disk usage (data) | > 85% | > 97% | Block write-heavy tasks; escalate |
| Network bandwidth | > 70% sustained | > 90% sustained | Throttle tasks; abort |
| Error rate (any service) | > 5% in 5 min | > 15% in 5 min | Pause dependent tasks; escalate |

### Operational Boundaries

1. **Task duration:** No single autonomous task may run longer than 60 minutes without a checkpoint/pause.
2. **Concurrent tasks:** Maximum 3 concurrent autonomous tasks in NIGHT_RUN_MODE, 2 in SAFE_AUTONOMOUS_MODE.
3. **Retry limit:** Maximum 3 retries per task; no retry of tasks that failed with a security-relevant error.
4. **Escalation timeout:** Operator must acknowledge an escalation within 15 minutes; failure triggers automatic LOCKDOWN_MODE transition.
5. **Logging:** All autonomous actions must be logged with at minimum: timestamp, task ID, action, result, resource delta.

### Exclusions

The Safety Monitor may temporarily exclude certain operations from safety envelope checks if:
- The exclusion is explicitly approved by an operator in MANUAL_MODE or ASSISTED_MODE.
- The exclusion is limited to a specific task instance (not a category).
- The exclusion is logged and expires after the task completes.

---

## Orchestrator Commands

The Orchestrator exposes the following commands for mode management, task control, and system interrogation.

### Mode Management

| Command | Syntax | Description |
|---------|--------|-------------|
| `SET_MODE` | `SET_MODE <mode_name>` | Request a mode transition. Valid modes: MANUAL_MODE, ASSISTED_MODE, SAFE_AUTONOMOUS_MODE, NIGHT_RUN_MODE, LOCKDOWN_MODE. |
| `GET_MODE` | `GET_MODE` | Return the current operating mode. |
| `MODE_HISTORY` | `MODE_HISTORY [limit]` | Return recent mode transition history. |

### Task Control

| Command | Syntax | Description |
|---------|--------|-------------|
| `QUEUE` | `QUEUE <task_id> [params]` | Queue a task for execution. Task must be valid for current mode. |
| `EXECUTE` | `EXECUTE <task_id> [params]` | Immediately execute a task (operator override, requires approval in ASSISTED_MODE+). |
| `CANCEL` | `CANCEL <task_id>` | Cancel a queued or running task. |
| `PAUSE` | `PAUSE [task_id]` | Pause all tasks or a specific task. |
| `RESUME` | `RESUME [task_id]` | Resume paused tasks. |
| `LIST_TASKS` | `LIST_TASKS [--status <status>]` | List all tasks with optional status filter. |
| `TASK_STATUS` | `TASK_STATUS <task_id>` | Get detailed status of a specific task. |

### Safety & Monitoring

| Command | Syntax | Description |
|---------|--------|-------------|
| `SAFETY_STATUS` | `SAFETY_STATUS` | Return current safety envelope metrics and status. |
| `SAFETY_OVERRIDE` | `SAFETY_OVERRIDE <task_id> <reason>` | Temporarily bypass safety envelope for a specific task (requires operator-level auth). |
| `ESCALATE` | `ESCALATE <level> <message>` | Manually trigger an escalation. |
| `ACKNOWLEDGE` | `ACKNOWLEDGE <escalation_id>` | Acknowledge a pending escalation. |

### System Information

| Command | Syntax | Description |
|---------|--------|-------------|
| `STATUS` | `STATUS` | Full system status: mode, active tasks, resource usage, safety envelope. |
| `LOG` | `LOG [--level <level>] [--since <time>]` | Query the system log. |
| `HELP` | `HELP [command]` | Display help for available commands. |

### Command Authorization

- **Operator-level commands:** SET_MODE, QUEUE, EXECUTE, CANCEL, PAUSE, RESUME, SAFETY_OVERRIDE, ESCALATE, ACKNOWLEDGE
- **Read-only commands (all roles):** GET_MODE, MODE_HISTORY, LIST_TASKS, TASK_STATUS, SAFETY_STATUS, STATUS, LOG, HELP
- **Elevated commands:** SET_MODE to/from LOCKDOWN_MODE requires elevated credentials (multi-factor authentication).

All commands are logged with actor identity, timestamp, parameters, and result status.

---

## Compliance & Enforcement

Violation of any policy in this document is logged and reported. The Safety Monitor enforces all policy rules in real time:

1. **Warnings** are issued for minor infractions (e.g., resource threshold warnings).
2. **Task aborts** occur for operations that violate the safety envelope.
3. **Automatic mode transitions** occur for repeated or severe violations.
4. **Operator escalation** is triggered for security-sensitive violations or forbidden task attempts.

This policy document is version-controlled. Any modification to this policy requires operator approval in MANUAL_MODE and is itself a Forbidden Task when attempted autonomously.

---

*Document version: 1.0.0*
*Last reviewed: June 21, 2026*
