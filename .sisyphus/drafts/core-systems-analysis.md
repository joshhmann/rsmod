# Core Systems Analysis Draft

**Date**: 2026-02-25  
**Analyst**: Prometheus (Plan Builder)  
**Scope**: RSMod v2 Build Readiness Assessment

---

## Executive Summary

The RSMod v2 codebase is remarkably mature - **23/23 skills have implementations**, combat system is 95% complete, and the engine is solid. However, there are **critical build blockers** preventing server startup, and several **high-value features** that exist in disabled state but could be quickly enabled.

### Key Discovery: Hidden Assets
**MAJOR FINDING**: Teleport spells, High/Low Alchemy, and Superheat Item are **ALREADY IMPLEMENTED** in `_disabled_wip/strict-startup-pass2/content/skills/magic/teleports/` - they just need to be moved to active modules and wired up!

---

## Critical Blockers (Preventing Server Start)

### 1. PoisonScript.kt Compile Error ⛔
- **Location**: `rsmod/content/mechanics/poison/scripts/PoisonScript.kt:492`
- **Error**: `Unresolved reference` on `objTypes[obj.id]!!`
- **Impact**: BLOCKS server startup
- **Assigned**: Gemini (per CORE_SYSTEMS_AUDIT.md)
- **Root Cause**: `objTypes` parameter not properly available in function scope

### 2. BUILD-CRIT-15: Null InternalId Issues
- **Affected**: PrayerAltar.kt, ShopKeeper NPCs, PiratesTreasure quest
- **Symptom**: Scripts disabled at startup due to missing symbol references
- **Fix**: Need to verify/correct symbol names in `.sym` files

---

## High-Value Quick Wins (Low Effort / High Impact)

### Tier 1: Enable Existing WIP Content 🚀

| Feature | Status | Location | Effort |
|---------|--------|----------|--------|
| Varrock Teleport | ✅ Implemented | `_disabled_wip/.../teleports/` | 30 min |
| Lumbridge Teleport | ✅ Implemented | `_disabled_wip/.../teleports/` | 30 min |
| Falador Teleport | ✅ Implemented | `_disabled_wip/.../teleports/` | 30 min |
| High Alchemy | ✅ Implemented | `_disabled_wip/.../teleports/` | 30 min |
| Low Alchemy | ✅ Implemented | `_disabled_wip/.../teleports/` | 30 min |
| Superheat Item | ✅ Implemented | `_disabled_wip/.../teleports/` | 30 min |

**Impact**: These are ESSENTIAL F2P features. Enabling them would immediately improve gameplay.

### Tier 2: Critical Missing Features

| Feature | Priority | Pattern Exists | Effort |
|---------|----------|----------------|--------|
| Furnace location interaction | HIGH | Yes (canoe, banks) | 1-2 hrs |
| Skeleton NPC combat/drops | HIGH | Yes (Hill Giant) | 2-3 hrs |
| Zombie NPC combat/drops | HIGH | Yes (Hill Giant) | 2-3 hrs |
| Moss Giant NPC combat/drops | HIGH | Yes (Hill Giant) | 2-3 hrs |
| Prayer altar restoration | MEDIUM | Needs fix first | 1-2 hrs |

### Tier 3: NPC Drop Tables to Complete

**Already Active** (in `rsmod/content/other/npc-drops/`):
- ✅ Hill Giant (complete, well-documented)
- ✅ Zombie (exists)
- ✅ Moss Giant (exists)
- ✅ Man/Woman, Black Knight, Barbarian, etc.

**In `_disabled_wip/npc-drops/`** (need enabling):
- 🟡 Bear
- 🟡 Chaos Druid
- 🟡 Dwarf
- 🟡 Mugger
- 🟡 Outlaw
- 🟡 Spider
- 🟡 Thief/Rogue
- 🟡 Unicorn

---

## Implementation Patterns Identified

### NPC Drop Tables Pattern
```kotlin
// 1. Create table file: tables/XxxDropTables.kt
internal object XxxDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.bones)
            table("Loot", weight = 1) {
                item(objs.coins, quantity = 5..25, weight = 10)
            }
        }
        registry.register(XxxNpcs.npcType, table)
    }
}

// 2. Wire in NpcDropTablesScript.kt:
XxxDropTables.registerAll(registry)

// 3. Create refs: XxxNpcs (extends NpcReferences), XxxObjs (extends ObjReferences)
```

### Teleport Spell Pattern (from disabled_wip)
```kotlin
class TeleportSpells @Inject constructor(...) : PluginScript() {
    override fun ScriptContext.startup() {
        onIfModalButton(components.spellbook_varrock_teleport) { castVarrockTeleport(it) }
    }
    
    private suspend fun ProtectedAccess.castVarrockTeleport(button: IfModalButton) {
        // Check level, runes, consume, teleport, grant XP
    }
}
```

### Location Interaction Pattern
```kotlin
// Pattern from SmithingFurnace.kt and travel/canoe/
onOpLoc1(locIds.furnace) { handleFurnaceInteraction(it) }
```

---

## Current Task Registry Status

- **Pending**: 66 tasks
- **In Progress**: 3 tasks
- **Completed**: 367 tasks
- **Blocked**: 11 tasks
- **File Locks**: 1 active

**Blocked Tasks**:
- NPC-DROP-UNICORN
- SLAYER-8 (gear protection)
- WORLD-TREE-F2P
- Various bot tests and diary tasks

---

## Recommended Execution Strategy

### Phase 1: Unblock Build (Immediate)
1. Fix PoisonScript.kt compile error
2. Address BUILD-CRIT-15 symbol issues

### Phase 2: Enable WIP Content (Quick Wins)
1. Move teleport spells from `_disabled_wip` to active module
2. Move alchemy/superheat spells
3. Enable existing NPC drop tables in `_disabled_wip`

### Phase 3: Fill Critical Gaps
1. Implement furnace location interaction
2. Add Skeleton/Zombie/Moss Giant combat definitions
3. Fix prayer altar

### Phase 4: Remaining F2P Content
1. Remaining NPC drop tables
2. Make-X interfaces
3. Wilderness PvP rules

---

## Risk Assessment

| Risk | Level | Mitigation |
|------|-------|------------|
| PoisonScript fix causes cascade errors | Medium | Test build after each fix |
| Symbol names mismatch | Medium | Verify against `.sym` files before committing |
| WIP content dependencies unclear | Low | Check imports before moving |
| Overlapping work with other agents | Low | Check task registry before claiming |

---

## Success Criteria for Build Readiness

- [ ] Server starts without compile errors
- [ ] Player can log in and play
- [ ] All 23 skills functional
- [ ] Combat system works (melee, ranged, magic)
- [ ] Economy works (trading, shops, banking)
- [ ] Essential F2P features: teleports, alchemy, furnace
- [ ] Core NPCs have combat + drops

---

## Next Steps

Awaiting user input to:
1. Confirm priority order
2. Decide on parallel vs sequential execution
3. Confirm resource allocation (which agents work on what)
4. Set timeline expectations


---

## Agent Exploration Findings (2026-02-26)

### Module Structure Confirmed
- **api/**: 50+ submodules (combat, drops, inventory, NPCs, quests, shops)
- **engine/**: 12 submodules (core game engine)
- **content/**: 100+ submodules (skills, quests, areas, mechanics)
- **server/**: 4 submodules (runtime)

### Key Pattern Discoveries

#### 1. Magic Spells Are Data-Driven
- Spells loaded from cache enums (`spell_enums.spellbooks`)
- Combat spells use `SpellAttackMap` interface
- **Utility spells (teleport/alchemy) need different handlers** - likely via `onOpObj1` or interface buttons

#### 2. Furnace Code EXISTS
- `SmithingFurnace.kt` has full implementation
- May just need wiring verification in module

#### 3. NPC Drop Tables - Robust Pattern
- 10+ NPCs already registered
- Pattern: `tables/<Npc>DropTables.kt` → register in `NpcDropTablesScript.kt`
- Hill Giant is best reference implementation

### Quick Win Rankings (Updated)

| Rank | Task | Effort | Impact | Status |
|------|------|--------|--------|--------|
| 1 | Verify furnace wiring | 15 min | HIGH | Code exists |
| 2 | Fix PrayerAltar loc ref | 30 min | MEDIUM | Needs symbol fix |
| 3 | Enable WIP teleport spells | 1-2 hrs | HIGH | In _disabled_wip |
| 4 | Skeleton/Zombie drops | 2-3 hrs | HIGH | Pattern ready |
| 5 | Alchemy spells | 2-3 hrs | MEDIUM | Needs handlers |

### Critical Paths Identified
1. **Build blocker**: PoisonScript.kt:492 - needs compile fix
2. **Startup blocker**: BUILD-CRIT-15 symbol issues (PrayerAltar, shops)
3. **Gameplay blocker**: Teleport spells (essential F2P feature)
4. **Training blocker**: Missing NPC drops (Skeleton, Zombie for Edgeville Dungeon)

---


### Build Blocker Deep Dive (from Agent Exploration)

#### PoisonScript.kt Compile Error - ROOT CAUSE IDENTIFIED
**Location**: `rsmod/content/mechanics/poison/scripts/PoisonScript.kt:124-145`
**Issue**: References varps that don't exist in `BaseVarps.kt`

**Missing varps** (documented in file comments):
- `varps.poison_damage`
- `varps.venom_damage`
- `varps.poison_sub_tick`
- `varps.poison_immunity_ticks`
- `varps.venom_immunity_ticks`
- `varps.hp_orb_toxin`

**Fix**: Add these varp references to `rsmod/api/config/src/main/kotlin/org/rsmod/api/config/refs/BaseVarps.kt`

#### 101 Missing Symbol Entries
**Source**: `run_default.log` type verification failures
**Examples**: bronze_bolts, cowhide, raw_shrimps, bowstring, vial, marrentill, etc.
**Impact**: Type verification warnings at runtime (non-blocking but should be fixed)

#### api:invtx Status
- Build logs show module **PASSING**
- Transaction classes exist in `engine/objtx/`
- Dependency properly wired
- **Status**: Likely already resolved

---

## Final Priority Recommendations

### Phase 1: Unblock Build (Today)
1. **Add missing varps to BaseVarps.kt** (30 min) - Unblocks PoisonScript
2. **Verify SmithingFurnace wiring** (15 min) - Quick win
3. **Fix PrayerAltar loc ref** (30 min) - Re-enables prayer restoration

### Phase 2: Enable WIP Content (This Week)
4. **Move teleport spells from _disabled_wip** (2 hrs) - Major gameplay impact
5. **Move alchemy/superheat spells** (2 hrs) - Essential F2P magic
6. **Enable existing NPC drop tables in _disabled_wip** (1 hr) - Quick content

### Phase 3: Fill Critical Gaps (This Week)
7. **Skeleton combat + drops** (2 hrs) - Edgeville Dungeon needs this
8. **Zombie combat + drops** (2 hrs) - Essential training mob
9. **Furnace polish** (1 hr) - Complete smelting loop

### Phase 4: Remaining F2P (Next Week)
10. Moss Giant, other NPCs
11. Make-X interfaces
12. Wilderness PvP basics

**Total effort to playable F2P**: ~2-3 days focused work

---


---

## Task Registry Integration (2026-02-26)

### Existing Registry Tasks Incorporated

| Registry Task ID | Plan Task | Wave | Priority | Status |
|------------------|-----------|------|----------|--------|
| **MAGIC-F2P-UTILITY** | Tasks 4-7 | 2 | W1 (High) | Pending |
| **NPC-TRAINING-F2P** | Task 3 | 1 | W1 (High) | Pending |
| NPC-DROP-UNICORN | - | - | W2 | Blocked (kimi-npc) |
| SLAYER-8 | - | - | W2 | Blocked (codex) |
| WORLD-TREE-F2P | - | - | W2 | Blocked (kimi-world) |

### Blocked Tasks (Explicitly NOT in Plan)
The following blocked registry tasks are NOT included in this plan:
- NPC-DROP-UNICORN → Assigned to kimi-npc
- SLAYER-8 → Assigned to codex
- WORLD-TREE-F2P → Assigned to kimi-world
- BOTS-FLETCH → Assigned to codex
- NPC-RELDO → Assigned to kimi-npc
- NPC-TRAINER → Assigned to kimi
- SPECIAL-F2P-2 → Assigned to kimi
- SYSTEM-RANDOM-F2P → Assigned to kimi
- SYSTEM-DIARY-F2P → Assigned to kimi
- SYSTEM-DIARY-LUMBRIDGE → Assigned to kimi
- WORLD-FAL-DUNGEON → Assigned to kimi

### Claim Strategy
1. **Wave 0 (Task 0)**: Any agent can start immediately
2. **Wave 1 (Tasks 1-3)**: 
   - Task 3: Claim `NPC-TRAINING-F2P` first
   - Tasks 1-2: Can be done by same or different agent
3. **Wave 2 (Tasks 4-7)**:
   - Claim `MAGIC-F2P-UTILITY` once
   - Work on Tasks 4-7 as sub-components
   - Coordinate internally if multiple agents involved
4. **Wave 3 (Tasks 8-10)**: Any agent can start after Wave 2 completes

### Plan Status: COMPLETE ✅

The work plan has been:
- ✅ Synthesized from CORE_SYSTEMS_AUDIT.md
- ✅ Validated against codebase exploration
- ✅ Reviewed by Metis for gaps
- ✅ Integrated with existing task registry
- ✅ Saved to: `.sisyphus/plans/build-readiness.md`

Ready for execution via `/start-work`
