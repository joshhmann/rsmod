# OSRS-PS-DEV (Rev 233)

Multi-agent RSMod v2 workspace targeting OSRS parity at **revision 233**.

If you are an agent, this file is your single bootstrap entrypoint.

## Start Here (Hard Gate)

Complete this checklist **before any edits**:

1. Read `docs/README.md`.
2. Open `docs/AGENTS.md`.
3. Claim task.
4. Lock files.
5. Confirm assignment template fields are all present.

### Do Not Proceed

If any required assignment field is missing — `task id`, `allowed/forbidden paths`, `insertion point`, or `validation command` — stop immediately.

The agent must return a clarification request and perform **zero edits** until all required fields are provided.

Cross-reference this startup contract from:
- `docs/AGENTS.md`
- `docs/NEXT_STEPS.md`

## Definitive Playbook Contract

Treat this README as the operational playbook for startup, tooling, validation, and cleanup.

If docs conflict, use this precedence:
1. `agent-tasks` registry (live execution truth)
2. `README.md` (definitive workflow playbook)
3. `AGENTS.md` (ownership tables, blockers, role scopes)
4. `docs/NEXT_STEPS.md` (active sequencing)
5. `docs/CONTENT_AUDIT.md` (feature status)
6. `docs/MASTER_ROADMAP.md` (long-horizon scope)

Important current policy:
- Use `osrs-cache` as the primary MCP for content ID/data lookups.
- Do not block tasks on `osrs-wiki-rev233` wrapper availability.

## Stability Gates (P0)

- `packCache` must pass: `\.\\gradlew.bat packCache --console=plain`
- strict boot must pass: `scripts/strict-boot-probe.ps1` exits `0`
- login must be stable through RSProx: `scripts/start-server.bat` then `scripts/start-rsprox.bat` (no injected-client crash)

## Agent Bootstrap (Do This First)

1. Read this `README.md` fully (this is sufficient for normal task execution).
2. Check task availability and ownership through `agent-tasks`.
3. Claim task and lock files before editing.
4. Before implementing anything new, search for an existing in-repo example to copy:
   - `rg -n "<feature keyword>" rsmod/content rsmod/api`
   - Prefer RSMod v2 examples over external RSPS repos (API + revision match).
5. Read `docs/quirks.md` for RSMod-specific gotchas (symbols `.local` override behavior, delay semantics, transaction notes).
6. Run Kotlin tooling warmup (below) before Kotlin-heavy work.
5. Implement, run validation gate, then complete task and release locks.
6. Open `AGENTS.md` only if you need ownership/blocker/DoD deep details.

## Task Flow (Mandatory)

1. `get_status()`
2. `list_tasks({ status: "pending" })`
3. `get_task("TASK-ID")`
4. `claim_task("TASK-ID", "<agent-name>")`
5. `check_conflicts(["path/to/file"])`
6. `lock_file("path/to/file", "<agent-name>", "TASK-ID")`
7. Implement and call `agent_heartbeat(...)` every 1-2 minutes.
8. Validate (see Build Gate).
9. `complete_task("TASK-ID", "<agent-name>", "summary")`

Never edit without locking. Never start without claiming.

## Kotlin Tooling Workflow (Mandatory)

Use `cclsp` first:
- `get_diagnostics`
- `find_workspace_symbols`
- `find_definition`
- `find_references`
- `get_hover`

Call hierarchy note:
- Current Kotlin LSP setup does not support `prepareCallHierarchy`.
- `cclsp` hierarchy tools failing with `no handler for request: textDocument/prepareCallHierarchy` is expected.
- Use fallback:

```powershell
& 'C:\Program Files\PowerShell\7\pwsh.exe' -File "scripts\kotlin-call-hierarchy.ps1" -Symbol <Name> -RepoRoot "Z:\Projects\OSRS-PS-DEV" -Scope rsmod -Mode both -MaxResults 200
```

Do not run manual long-lived `kotlin-lsp --stdio` sessions in normal work; let `cclsp` manage LSP startup.

## Kotlin Session Operational Check (Copy/Paste)

1. Ensure both configs have identical Kotlin server entry:
- `C:\Users\CRIMS\.claude\cclsp.json`
- `C:\Users\CRIMS\.config\cclsp\cclsp.json`

Expected entry:

```json
{
  "extensions": ["kt", "kts"],
  "command": ["C:\\Windows\\System32\\cmd.exe", "/c", "C:\\tools\\kotlin-lsp\\kotlin-lsp.cmd", "--stdio"],
  "rootDir": "Z:\\Projects\\OSRS-PS-DEV\\rsmod",
  "timeout": 600000
}
```

2. Clean stale LSP (safe filter):

```powershell
Get-CimInstance Win32_Process | ? { ($_.Name -in @('java.exe','cmd.exe')) -and $_.CommandLine -match 'kotlin-lsp' } | % { Stop-Process -Id $_.ProcessId -Force }
```

3. Warmup order:
- `find_workspace_symbols("GameServer")`
- Wait 10-15 seconds
- `get_diagnostics("rsmod/server/app/src/main/kotlin/org/rsmod/server/app/GameServer.kt")`
- `find_definition(GameServer)`

4. If still cancelled, mark `cclsp` degraded and continue with fallback script (do not block task).

## Build Gate (Mandatory Before Completion)

Run from current `rsmod/` working directory:

```powershell
& 'C:\Program Files\PowerShell\7\pwsh.exe' -File "scripts\preflight-ref-hygiene.ps1" -FailOnIssues
& 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat spotlessApply"
& 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat <scoped-module-build> --console=plain"
```

The preflight script includes a **symbol-name drift gate** (`tools/symbol_ref_canon.py`) that blocks NEW unknown symbol refs
against `docs/symbol-ref-baseline.txt`.

If task touches symbols/global startup/config, full boot gate is required:

```powershell
scripts\start-server.bat
```

Success condition: log reaches `[MainGameProcess] World is live`. Then grep for `Skipping script startup` — must return **zero lines** (any match = silently broken script).

For strict boot validation without hanging your terminal, use:

```powershell
& 'C:\Program Files\PowerShell\7\pwsh.exe' -File "scripts\strict-boot-probe.ps1" -TimeoutSeconds 120
```

This runs strict `:server:app:run`, stops once it detects a stable boot marker, and performs Gradle/Java cleanup. Exit code `0` means strict boot reached the marker; non-zero means failure/timeout.

## Symbol Table Rules (Rev 233) (Mandatory)

- `rsmod/.data/symbols/*.sym` entries are canonical cache internal names for rev 233. Do not rename IDs.
- `.local/*.sym` is only for adding missing canonical names when they do not exist in the base `.sym`.
- Never map an existing ID to a different name in `.local/*.sym` (it causes event collisions and hard-to-debug startup failures). Prefer updating Kotlin code to use the canonical name from the base `.sym`.

## Stable Login Flow (RSProx)

Before content work, confirm local login stability with this exact order:

1. Start server:
```bat
scripts\start-server.bat
```
This now runs `packCache` before launching RSMod.

2. Start RSProx:
```bat
scripts\start-rsprox.bat
```
This now resets `%USERPROFILE%\.rsprox\caches` and `%USERPROFILE%\.rsprox\binary` by default.
It also pins a RuneLite bootstrap commit for rev 233 compatibility in the generated `proxy-targets.yaml`.

3. Login with client through `RSMod Local` target in RSProx.

If you need to skip RSProx cache reset for debugging:
```bat
set RSPROX_SKIP_RESET=1
scripts\start-rsprox.bat
```

Crash logs to inspect:
- RuneLite: `%USERPROFILE%\.runelite\logs\client.log`
- RSProx binaries/transcripts: `%USERPROFILE%\.rsprox\binary\RSMod Local\`

If you see `Mismatch in overlaid cache archive hash` or `injected-client` crashes, do a one-time full reset
(clears RuneLite caches under `%USERPROFILE%\.runelite\` as well):
```bat
set RSPROX_FULL_RESET=1
scripts\start-rsprox.bat
```

For stability, `scripts\start-rsprox.bat` launches RuneLite in safe mode by default.
Disable if needed:
```bat
set RSPROX_SAFE_MODE=0
scripts\start-rsprox.bat
```

Optional triage overrides (only use if debugging RSProx login issues):
```bat
set RSPROX_BOOTSTRAP_HASH=<commit-hash>
set RSPROX_JAV_CONFIG_URL=<url>
scripts\start-rsprox.bat
```

## End-of-Session Cleanup (Mandatory)

```powershell
\.\\gradlew.bat --stop
Get-CimInstance Win32_Process | ? { ($_.Name -in @('java.exe','cmd.exe')) -and $_.CommandLine -match 'kotlin-lsp' } | % { Stop-Process -Id $_.ProcessId -Force }
```

## Source-of-Truth Order

1. `agent-tasks` registry (execution/ownership truth)
2. `README.md` (definitive workflow playbook)
3. `AGENTS.md` (ownership and blockers)
4. `docs/NEXT_STEPS.md` (active sequencing)
5. `docs/CONTENT_AUDIT.md` (feature status)
6. `docs/MASTER_ROADMAP.md` (long-horizon scope)

## Role Routing

- `CLAUDE.md`: content implementation flow (`rsmod/content/**`, `bots/**`, `mcp/**`)
- `GEMINI.md`: engine/infrastructure flow (`rsmod/engine/**`, `rsmod/api/**`, `rsmod/server/app/**`)
- `START_HERE.md`: expanded implementation patterns and API examples
- `AGENTS.md`: ownership, blockers, DoD, and coordination policy
- `docs/quirks.md`: RSMod internal design quirks/gotchas (read before touching symbols/transactions/delays)
- `docs/DOC_AUTHORITY.md`: which docs are authoritative vs historical/archived — check before acting on old docs

## One-Line Delegation Prompt

Use this when assigning agents:

`Read README.md only, follow the definitive playbook exactly, claim+lock via agent-tasks, run the Kotlin operational check, implement, run build gate, clean up processes, then report commands and results.`

## Required Completion Report (Copy/Paste)

Every agent completion message must include:

1. `claimed task`: `<TASK-ID>`
2. `locked files`: explicit list
3. `commands run`: exact commands in order
4. `results`: pass/fail + first error line when failing
5. `cleanup`: `gradlew --stop` + kotlin-lsp process cleanup result




