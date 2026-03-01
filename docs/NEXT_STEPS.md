# Next Steps — OSRS-PS-DEV F2P Readiness Roadmap

Last updated: 2026-03-01.

This document serves as the master checklist for achieving 1:1 F2P parity with OSRS Revision 233.

## 🎯 Target: 100% F2P Parity
Every quest, skill, area, and mechanic must match vanilla behavior.

---

## 🧹 Tier 0: Codebase Cleanup (P0)

Cleanup tasks from the 2026-02-26 audit. See `docs/CODEBASE_AUDIT_2026-02-26.md` for full details.

- [ ] **CLEANUP-1**: Fix WoodsmanTutor.kt runtime crash (`TODO()` throws NotImplementedError at runtime)
- [ ] **CLEANUP-2**: Delete ghost module `content/quests/romeo-and-juliet/` (empty, real quest at `romeo-juliet/`)
- [ ] **CLEANUP-3**: Delete 5 orphaned dialogue files in `generic-npcs/npcs/` (outside src/, never compiled)
- [ ] **CLEANUP-4**: Remove dead code from NpcDropTablesScript.kt and F2pDropTables.kt
- [ ] **CLEANUP-8**: Update AGENTS.md blocker table — verify and remove resolved blockers

## 🧱 Tier 0.5: Stability Gates (P0)

Do these first. If these are not green, new content work tends to cause rework and regressions.

- [ ] **STAB-P0-LOGIN**: RSProx login reliability gate.
  - Goal: successful login through RSProx `RSMod Local` with no overlay hash mismatch warnings.
  - Use: `scripts\start-server.bat` then `scripts\start-rsprox.bat` (see `README.md`).
- [ ] **STAB-P0-STRICT-BOOT**: strict boot + type/hash integrity gate.
  - Goal: strict `:server:app:run` converges without “cache type edits need to be packed” loops.
  - Use: `scripts\strict-boot-probe.ps1 -TimeoutSeconds 120`

Fixed allocation policy (mandatory until green):
- Reserve a **minimum 40% of total sprint effort** for Tier 0 + Tier 0.5 blockers until all Tier 0 and Tier 0.5 items are green.
- If either strict boot gate (`STAB-P0-STRICT-BOOT`) or login gate (`STAB-P0-LOGIN`) is red, cap parallel new-feature work to **max 1 active Tier 1+ feature task per agent** (remaining capacity stays on Tier 0/0.5 blockers).
- Do not increase Tier 1+ in-flight task count while any Tier 0/Tier 0.5 blocker remains unowned.

Rule:
- Prefer claiming stability/cleanup tasks over new content tasks until Tier 0 + 0.5 are complete.
- Sprint allocation while Tier 0/Tier 0.5 are open:
  - minimum 40% capacity on cleanup/stability tasks.
  - maximum 60% on new content/API expansion tasks.

---

## 🚀 Tier 1: The Critical Path (High Priority)
These tasks are required for a fundamentally playable F2P experience.

### 1. Magic Utility
- [ ] **MAGIC-F2P-UTILITY**: Implement Varrock/Lumbridge/Falador teleports, High/Low Alch, and Superheat.
- [x] **NPC-TRAINING-F2P**: ~~Combat stats and drop tables for Hill Giants, Moss Giants, Skeletons, and Zombies.~~ **RECLASSIFIED as P2P** — Combat Training Camp is members-only content. See NPC-TRAINING-P2P for corrected scope.

### 2. Core Mechanics
- [ ] **SYSTEM-LEVELUP**: (Verify status) Ensure level-up messages and skill flash interfaces work for all skills.
- [ ] **SYSTEM-COMBAT-LEVEL**: (Verify status) Accurate combat level calculation and display.
- [ ] **SMITH-2**: Complete furnace location interactions (In Progress).

---

## 🛠️ Tier 2: Gameplay Depth (Medium Priority)
Filling the noticeable gaps in content.

### 1. Interfaces & Flow
- [ ] **INTERFACE-MAKE-X**: Implement Make-X menus for Fletching, Herblore, and Crafting.
- [ ] **INTERFACE-SMITHING**: Implement the anvil selection interface.
- [ ] **SYSTEM-MUSIC-UNLOCK**: Logic for unlocking music tracks on region entry.

### 2. World Content
- [ ] **WORLD-F2P-AGILITY**: Implement Draynor, Al Kharid, and Varrock Rooftop courses.
- [ ] **AREA-KARAMJA-F2P-SURFACE**: NPCs and scenery for the F2P part of Karamja.
- [ ] **AREA-EDGEVILLE-DUNG**: (Verify status) Edgeville Dungeon population.

---

## 🛡️ Tier 3: Hardening & Parity (Security & Polish)
Mechanical perfection and PvP safety.

### 1. Wilderness & PvP
- [ ] **WILD-RULES-F2P**: Implement skulling, item protection rules, and combat level ranges.
- [ ] **DEATH-SKULL-1**: Specific skull/wilderness death logic.

### 2. Quests (Implementation)
- [ ] **QUEST-F2P-17..22**: Implement the logic for the 6 modern F2P quests scaffolded today.
    - Knight's Sword, Shield of Arrav, Misthalin Mystery, Below Ice Mountain, X Marks the Spot, Corsair Curse.

---

## 🧪 Tier 4: Quality Assurance
- [ ] **QUEST-BOT-TESTS-F2P**: Create bot test scripts for all 22 F2P quests to prove completion.
- [ ] **CONTENT-AUDIT-FINAL**: Final verification pass against every item in `CONTENT_AUDIT.md`.

---

## 🚦 Delivery Phase Gates (Mandatory)

All task execution must follow the same hard-stop phase model. **You may not enter the next phase until the current phase exit criteria are met.**

### Phase 0 — Discovery (existing patterns + refs)
- Exit criteria:
  - Existing implementation patterns identified.
  - Required refs/symbols verified.
  - Intended module/file scope declared.
- Hard stop rule: no spec or code changes until discovery artifacts are captured.

### Phase 1 — Spec (behavior/state transitions)
- Exit criteria:
  - Behavior contract documented.
  - State transitions (including failure paths) documented.
  - Validation intent listed.
- Hard stop rule: no implementation until spec is explicit and reviewable.

### Phase 2 — Implementation (single module scope)
- Exit criteria:
  - Implementation stays within declared single-module scope.
  - Required behavior implemented (no placeholder TODO path for core logic).
  - Code ready for validation chain.
- Hard stop rule: do not start validation until implementation is complete in scope.

### Phase 3 — Validation (`preflight -> spotlessApply -> scoped build -> bot test`)
- Exit criteria:
  - Preflight hygiene executed.
  - `spotlessApply` executed.
  - Scoped build passes.
  - Bot test executed (or blocker+owner documented).
- Hard stop rule: no handoff/closure without recorded validation results.

### Phase 4 — Handoff (notes, blockers, audit updates)
- Exit criteria:
  - Session notes updated.
  - Blockers table/status updated.
  - Audit/progress docs updated with final state.
- Hard stop rule: task remains open until handoff updates are complete.
## Startup Contract (Hard Gate)

Before any edits, complete the startup checklist in `docs/README.md#start-here-hard-gate`:
1. Read `docs/README.md`.
2. Open `docs/AGENTS.md`.
3. Claim task.
4. Lock files.
5. Confirm assignment template fields are all present.

**Do Not Proceed:** If any required field is missing (`task id`, `allowed/forbidden paths`, `insertion point`, `validation command`), return a clarification request and perform zero edits.

---

## 📝 Coordination Rules
1. **Always** claim a task in the registry before starting.
2. **Always** run the Mandatory Build Gate defined in `README.md` (definitive playbook):
   - preflight hygiene
   - `spotlessApply`
   - scoped module build
   - full boot gate when symbols/global config are touched
3. **Never** use hardcoded IDs if a `.sym` exists.
4. **Never** use global mutable state in `object` blocks.

## 📉 Weekly Blocker Burndown Check (Required)

Run once per week in planning/review and post results in the active sprint tracker.

### Required owner assignment
- Each open Tier 0/Tier 0.5 blocker must have exactly one **Directly Responsible Individual (DRI)**.
- Unowned blockers must be assigned before new Tier 1+ work is pulled.

### Required status fields (per blocker)
- **Blocker ID** (e.g., `STAB-P0-STRICT-BOOT`)
- **Tier** (`0` or `0.5`)
- **Status** (`red`, `amber`, or `green`)
- **DRI (owner)**
- **Age (days open)**
- **Current impact** (what it blocks)
- **Next concrete action** (next executable step)
- **ETA to green**
- **Dependencies / escalation needed**

### Weekly exit checks
- Verify the sprint still meets the minimum 40% Tier 0/0.5 allocation while any blocker is non-green.
- Verify strict boot and login gate status; if either is red, enforce the 1-task parallel feature cap.
- Reassign or escalate any blocker with no forward movement for 7+ days.
5. **Always** use the coordinator SOP and assignment template for delegated work:
   - `docs/COORDINATOR_SOP.md`
   - `docs/AGENTS.md` -> *Coordinator Task Assignment Template (Copy/Paste)*
