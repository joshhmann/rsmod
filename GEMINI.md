# RS Mod Development Guide

Welcome to the RS Mod project! This document provides essential context for navigating and developing within this RuneScape game-server emulator.

## Project Overview

RS Mod is a highly modular, mechanically accurate Old School RuneScape (OSRS) game-server emulator written in **Kotlin** and targeting **Java 21+**. It prioritizes mechanical fidelity to the original game while maintaining a clean, modern codebase.

### Core Technologies
- **Language:** Kotlin (Java 21+)
- **Build System:** Gradle (Kotlin DSL)
- **Logging:** Logback
- **Client Recommendation:** [RSProx](https://github.com/blurite/rsprox) is the preferred client for development and debugging.

## Architecture

The project is organized into several key directories, each serving a specific purpose in the server's lifecycle:

- **`api/`**: High-level interfaces, definitions, and extensions. Provides the "language" for content developers.
- **`engine/`**: The core game engine. Handles low-level mechanics like movement, map management, entity processing, networking, and collision.
- **`content/`**: Implementation of game-specific features.
    - `skills/`: Individual skill logic (e.g., Woodcutting, Mining).
    - `quests/`: Quest logic and state management.
    - `mechanics/`: Core gameplay mechanics (e.g., combat, interactions).
    - `interfaces/`: UI behavior and bank logic.
- **`server/`**: The application's entry point, installation scripts, logging configuration, and global services.
- **`.data/`**: Local storage for the game cache, player saves, and symbols.

## Building and Running

### Initial Setup
Before running the server for the first time, you must perform the installation steps to download the required cache and generate security keys:

```bash
./gradlew install
```

### Sprint Command Policy (Windows)
Use this exact invocation shape for Gradle during the sprint to keep command approvals stable:

```powershell
& 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat <args>"
```

Run commands from the `rsmod/` directory.

### Running the Server
To start the game server:

```bash
./gradlew run
```

### Running Tests
The project includes an extensive test suite. You can run all tests using:

```bash
./gradlew test
```

### Additional Tasks
- `cleanInstall`: Removes partial or corrupted installation artifacts.
- `downloadCache`: Manually triggers the cache download and extraction.
- `packCache`: Packs the cache files.
- `generateRsa`: Generates new RSA keys for network security.

### Startup Triage Order (Use This Exactly)
When server startup fails, follow this sequence before changing unrelated code:

1. Generate missing key if needed:
   - `.\gradlew.bat generateRsa --console=plain`
2. Run strict startup once to classify failure:
   - `.\gradlew.bat :server:app:run --console=plain`
3. If it fails at cache pack/update (`TypeUpdaterConfigs` / `NpcTypeEncoder`), resolve unresolved `id=-1` type updates first.
4. If it then fails on identity hash mismatches, rerun with:
   - `.\gradlew.bat :server:app:run --console=plain --args='--skip-type-verification'`
5. If it fails on unresolved symbol-name drift, use:
   - `.\gradlew.bat :server:app:run --console=plain --args='--skip-type-verification --allow-type-verification-failures'`
6. Record exact blocker output in task notes and create follow-up tasks for hash/symbol cleanup instead of mixing with runtime fixes.

### Current Known Startup Reality (rev233 workspace)
- Strict startup may fail due to widespread reference hash/name drift not tied to a single module edit.
- A runnable server path is expected via:
  - `--skip-type-verification --allow-type-verification-failures`
- Use strict mode for diagnostics and drift measurement; use flagged mode to unblock integration testing.

## Development Conventions

### Mechanical Accuracy
The primary goal is mechanical accuracy. When implementing features, ensure they align with OSRS behavior.

### Type-Safety & Magic Numbers
Avoid using magic numbers (literal IDs) for game objects, NPCs, or items. Use the project's type-safe configuration system (e.g., `ObjType`, `NpcType`) and symbol-based references.

### Game Logic Patterns
- **Randomness:** Use `GameRandom` functions for all game-related random generation to ensure consistency and testability.
- **Delays:** The `delay(n)` function (often referred to as `p_delay` in scripts) uses a direct tick-to-delay mapping. `delay(1)` results in a 1-tick delay.
- **Inventory Transactions:** Use `InvTransactions` for modifications to player inventories or banks. Note that these are not thread-safe in tests; mark such tests with `@Execution(ExecutionMode.SAME_THREAD)`.
- **Collision:** Teleportation and movement mechanics must interact correctly with the `CollisionFlagMap`.

### Documentation & Resources
- **`docs/quirks.md`**: Contains a list of known technical quirks, antipatterns, and specific design decisions. Read this before making architectural changes.
- **`LICENSE.md`**: The project is licensed under the ISC license.

## Multi-Agent Guardrails (Sprint-Critical)

These rules are mandatory when collaborating in the shared sprint workspace.

### Ownership Boundaries
- Gemini owns: `server/app/`, `api/`, and `engine/`.
- Do not implement content plugins in `content/` unless explicitly assigned by task coordination.

### Canonical Path Discipline
- Never create a second module directory that represents the same area/system with a different folder name.
- Reuse the existing canonical module path from `AGENTS.md` (for example, do not create both `al-kharid/` and `alkharid/` trees).
- Do not create duplicate Kotlin package roots across different physical module folders.

### Task Focus and Concurrency
- Keep at most one active `in_progress` task unless a coordinator explicitly requests parallel Gemini work.
- Before coding: claim task, lock files, then start implementation.
- If a task prompt does not include an explicit target entity/system and task id, request clarification before editing.

### Task Prompt Contract (Required)
- Every assignment must include:
  - exact task id,
  - exact objective (`Implement <named target>`),
  - allowed paths,
  - forbidden paths,
  - exact insertion point (inside function/class and placement rule),
  - validation command.
- Reject vague prompts like `work on skill implementations` with a clarification request.

### Kotlin Edit Pre-Flight (Required)
- Read target file before editing.
- Verify refs/handlers/declarations do not already exist.
- Add only missing entries (no duplicates).
- Verify file structure after edit:
  - no orphaned code outside class/object/function blocks,
  - no duplicate function declarations.
- Keep path/package canonical:
  - no duplicate area/module trees,
  - no duplicate package roots.

### Lock and Blocker Hygiene
- If blocked or paused for more than 10 minutes, do one of:
  - post a blocker note in task registry with concrete details, or
  - release file locks.
- Blocker notes must include:
  - exact failing command,
  - first relevant error line,
  - impacted file path(s).

### Validation and Cleanup
- Validation order is mandatory:
  - `edit -> spotlessApply -> scoped build`.
- Always run module-scoped Gradle validation for changed scope.
- After build/test activity, stop daemons:
  - `./gradlew --stop`
- If you launched server runtime during testing, stop the Java server process before handoff.

### Reference Hygiene Preflight
- Run before scoped build when touching refs or content wiring:
  - `pwsh -File ..\scripts\preflight-ref-hygiene.ps1 -RepoRoot .. -FailOnIssues`
- Purpose:
  - catch unresolved `BaseObjs/BaseNpcs/BaseLocs` usages early,
  - catch known bad symbol-name patterns (`grimy_guam_leaf`, `objs.tuna`, `find(..., -1)`).
