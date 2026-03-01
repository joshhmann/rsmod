# CLAUDE.md — Claude Content Implementation Handbook

Read `README.md` first (definitive playbook), then use this file for Claude-specific scope and constraints.

---

## Scope

- Primary ownership: `rsmod/content/**`, `bots/**`, `mcp/**`, `agent-runner/**`
- Do not edit Gemini-owned modules: `rsmod/engine/**`, `rsmod/api/**`, `rsmod/server/app/**` (unless task explicitly allows)
- **Exception**: `rsmod/api/quest/QuestList.kt` and `rsmod/api/config/refs/Base*.kt` may be edited when a task requires adding new quest/varp/npc entries — coordinate via `lock_file` first

---

## Kotlin Tooling Workflow (Required)

1. Use `cclsp` first:
   - `get_diagnostics`
   - `find_workspace_symbols`
   - `find_definition`
   - `find_references`
   - `get_hover`
2. For call hierarchy needs, use the fallback script:
```powershell
& 'C:\Program Files\PowerShell\7\pwsh.exe' -File "scripts\kotlin-call-hierarchy.ps1" -Symbol <Name> -RepoRoot "Z:\Projects\OSRS-PS-DEV" -Scope rsmod -Mode both -MaxResults 200
```
3. Current Kotlin LSP setup does not implement `prepareCallHierarchy`; treat that as expected.
4. Do not leave manual `kotlin-lsp --stdio` sessions running during normal work; let `cclsp` manage it.
5. If `cclsp` returns `cancelled`, run README Kotlin operational check and retry before falling back to grep.

---

## Build/Test Gate

Run these before marking task completion:

1. `& 'C:\Program Files\PowerShell\7\pwsh.exe' -File "scripts\preflight-ref-hygiene.ps1" -FailOnIssues`
2. `& 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat spotlessApply"` (mandatory — format before every build)
3. Scoped module build from `rsmod/` using the standard prefix.
4. Grep boot log for `Skipping script startup` — **must return zero lines**. Any match means a script is silently disabled.

If symbol/global config changed, run full boot gate via `scripts\start-server.bat` and verify `[MainGameProcess] World is live` in log output.

## Agent Heartbeat (Mandatory)

Call `agent_heartbeat` every 1-2 minutes while working:
```
agent_heartbeat(agent: "claude", status: "working", current_task: "TASK-ID", current_file: "path/to/file", message: "what you're doing")
```
This lets other agents see your activity via `list_active_agents()`.

## Required Completion Report

Always return:
1. Claimed task
2. Locked files
3. Commands run (exact order)
4. Results (pass/fail + first error line)
5. Cleanup (`gradlew --stop` and kotlin-lsp cleanup)





