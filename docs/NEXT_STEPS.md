# Next Steps — OSRS-PS-DEV F2P Readiness Roadmap

Last updated: 2026-02-26.

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

Rule:
- Prefer claiming stability/cleanup tasks over new content tasks until Tier 0 + 0.5 are complete.

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

## 📝 Coordination Rules
1. **Always** claim a task in the registry before starting.
2. **Always** run the Mandatory Build Gate defined in `README.md` (definitive playbook):
   - preflight hygiene
   - `spotlessApply`
   - scoped module build
   - full boot gate when symbols/global config are touched
3. **Never** use hardcoded IDs if a `.sym` exists.
4. **Never** use global mutable state in `object` blocks.

