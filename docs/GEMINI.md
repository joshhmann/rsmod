# GEMINI.md — Gemini Engine & Infrastructure Handbook

**Mandatory context for the Gemini-CLI agent. Read before every task.**
Read `README.md` first (definitive playbook), then apply this file's Gemini-specific constraints.
Update the "Active Blocker Tracking" section when you hit a wall or clear a path.

---

## 🏛 Gemini Primary Ownership
Gemini is the authoritative agent for the core server architecture.

| Module               | Scope                                                                 |
| -------------------- | --------------------------------------------------------------------- |
| `rsmod/engine/`      | Core mechanics (Movement, Collision, Net, Maps, Entity Processing)    |
| `rsmod/api/`         | Framework interfaces (Quests, Combat, Skills, Stats, Transactions)    |
| `rsmod/server/app/`  | Entry point, lifecycle, and global service configuration              |
| **Shared**           | Build system (`build.gradle.kts`), Symbols (`.sym`), and Infra tools  |

---

## 🛡 Mandatory Protocols (Non-Negotiable)

### 1. The Build Gate
After **every** edit and before `complete_task()`, you MUST run four checks:

**Step 1 — Pre-Flight Hygiene:**
```powershell
& 'C:\Program Files\PowerShell\7\pwsh.exe' -File "scripts\preflight-ref-hygiene.ps1" -FailOnIssues
```
Must exit with 0 issues. Fix any private References, bindScript calls, or missing base symbols.

**Step 2 — Format + Compile:**
```powershell
& 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat spotlessApply"
```
Then compile any touched API or Content modules individually (e.g., `./gradlew :api:spells:compileKotlin`).

**Step 3 — Full Boot Gate:**
```powershell
& 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat :server:app:run --console=plain --args='--skip-type-verification --allow-type-verification-failures'"
```
**Definition of Done:** Task is only complete if this reaches `[MainGameProcess] World is live`. Compilation alone is insufficient.

**Step 4 — Startup WARNs (Zero Tolerance):**
Grep the boot log from Step 3 for `Skipping script startup`. Must return **zero lines**. Any match means a script is silently disabled — treat it as a build failure and fix it.

### 2. Task Coordination
Never work "in the dark."
1. `get_status()` & `list_tasks({ status: "pending" })`
2. `claim_task("TASK-ID", "gemini")`
3. `lock_file("path/to/file", "gemini", "TASK-ID")`
4. Implement & **Build Gate**
5. `complete_task("TASK-ID", "gemini", "notes")`

### 3. Process Cleanup
RSMod server and Gradle daemons must be stopped before handoff.
- `\.\\gradlew.bat --stop`
- `Get-Process java -ErrorAction SilentlyContinue | Stop-Process` (if server was running)

### 4. Kotlin Navigation Protocol (Required)
Use Kotlin tooling in this order:
1. `cclsp` for `get_diagnostics`, `find_workspace_symbols`, `find_definition`, `find_references`, `get_hover`.
2. For caller/callee discovery, run:
```powershell
& 'C:\Program Files\PowerShell\7\pwsh.exe' -File "scripts\kotlin-call-hierarchy.ps1" -Symbol <Name> -RepoRoot "Z:\Projects\OSRS-PS-DEV" -Scope rsmod -Mode both -MaxResults 200
```
3. Do not rely on LSP call hierarchy endpoints (`prepareCallHierarchy`) in current setup; they are not implemented by the active Kotlin LSP backend.
4. If `cclsp` returns `cancelled`, run the README Kotlin operational check (stale process cleanup + warmup) before continuing.

---

## 🚫 The "Top Mistake" Guardrails
These patterns have broken the build previously. DO NOT use them:

- **NO `bindScript<T>()`**: In `PluginModule.kt`, delete any manual bindings. Scripts are auto-discovered.
- **NO `private object ... : NpcReferences()`**: All `TypeReferences` objects must be `internal` or `public`. `private` objects crash the loader.
- **NO `mes(player, "text")`**: Inside `ProtectedAccess` / `PluginScript`, use the extension `mes("text")`.
- **NO `find("name", 123)`**: If the symbol exists in `.sym` files, use `find("name")` only. Guessing IDs causes hash mismatches.
- **NO Interface IDs > 924**: Rev 233 limit for `.local/interface.sym` is 924.
- **NO `Thread.sleep()`**: This is a coroutine environment. Freezing a thread freezes the whole server. Use `delay(ticks)`.
- **NO Global Mutable State**: Do not write `var` inside a singleton `object`. Store state safely on the `Player` or `Loc`.
- **NO raw `println()`**: Use RSMod's `InlineLogger()`.
- **NO Silent `invAdd`**: You must check `.success` or use `invAddOrDrop` to prevent deletion when inventories are full.
- **NO Duplicate Functional Stubs**: NEVER map multiple functional symbols (e.g. two different bows) to `blankobject`. This crashes `EnumBuilders` and `ObjEditors` at boot due to duplicate map keys. Every added symbol MUST be grepped from `rsmod/.data/symbols/*.sym` first.

---

## 🛠 Command Reference (Standardized)

| Action                | Command (run from `rsmod/` directory)                                           |
| --------------------- | ------------------------------------------------------------------------------- |
| **Full Build**        | `& 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat build"`    |
| **Generate RSA**      | `& 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat generateRsa"` |
| **Start Server**      | `& 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat :server:app:run"` |
| **Check Scripts**     | `grep "Skipping script startup" run_server.log` (Target: Zero matches)           |
| **Format Code**       | `& 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat spotlessApply"` |
| **Hierarchy Fallback**| `& 'C:\Program Files\PowerShell\7\pwsh.exe' -File "scripts\kotlin-call-hierarchy.ps1" -Symbol <Name> -RepoRoot "Z:\Projects\OSRS-PS-DEV" -Scope rsmod -Mode both` |

---

## Required Completion Report

Every completion update must include:
1. Claimed task
2. Locked files
3. Commands run (exact order)
4. Results (pass/fail + first failing line)
5. Cleanup done (`gradlew --stop`, kotlin-lsp process cleanup if used)

---

## 🧩 Technical Mandates (Rev 233)

- **Canonical revision source**: OpenRS2 cache `runescape/2293` (major `233`, built `2025-09-10T16:47:47Z`).
- **Varp/Varbit Truth**: Search `rsmod/.data/symbols/varp.sym` and `varbit.sym` before creating new ones.
- **CoordGrid**: Use `.level` for the Z-axis, never `.plane`.
- **Transactions**: Always check `.success` when using `invAdd`, `invDel`, or `statAdvance`.
- **Duplicate Handlers**: Do not register the same `onOp*` for the same entity in different files. It silently disables the second registration.
- **NpcEditor**: One module "owns" an NPC type's stats and aggression. Avoid duplicate `edit(npc)` calls in different modules to prevent infinite loops.

---

## 🚦 Active Blocker Tracking (Gemini-Specific)

| Blocker Task       | Status     | Description                                                                 |
| ------------------ | ---------- | --------------------------------------------------------------------------- |
| `ENGINE-STARTUP`   | ⚠️ Verify   | `game.key` + `api:invtx` — may be resolved. Verify: `gradlew.bat generateRsa` then `gradlew.bat build`. |
| `REF-HASH-DRIFT`   | ⚠️ Warning  | Some symbol hashes in `BaseObjs` may be stale relative to Rev 233 cache.    |
| `SYMBOL-NAMING`    | ℹ️ Info     | Use `docs/SYM_NAMING_GUIDE.md` to map Wiki names → Cache symbols.           |

---

## 📝 Truth Sources for Gemini
1. `AGENTS.md` — Global coordination rules.
2. `docs/CORE_SYSTEMS_GUIDE.md` — Detailed engine/API documentation.
3. `docs/REV_LOCK_POLICY.md` — Revision 233 guardrails.
4. `rsmod/.data/symbols/` — The only valid source for IDs.



