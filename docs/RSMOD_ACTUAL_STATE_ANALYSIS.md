# RSMod Actual State Analysis

**Date**: 2026-02-26  
**Purpose**: Document what RSMod team has actually implemented vs what we assumed was missing

---

## 🎯 Key Discovery

**Many F2P features we thought were missing are ALREADY IMPLEMENTED by the RSMod team!**

We were planning to build APIs that **already exist**. This document corrects our understanding.

---

## ✅ What's ALREADY Implemented (RSMod Team)

### 1. Drop Table API (`api/drop-table/`)

**Status**: ✅ **COMPLETE and FUNCTIONAL**

**Files**:
```
api/drop-table/
├── NpcDropTable.kt
└── NpcDropTableRegistry.kt
```

**Pattern Used**:
```kotlin
// From HillGiantDropTables.kt (ALREADY EXISTS)
val hillGiantTable = dropTable {
    always(objs.big_bones)
    
    table("Pre-roll", weight = 1) {
        nothing(weight = 127)
        item(HillGiantObjs.giant_key, weight = 1) // 1/128
    }
    
    table("Weapons", weight = 16) {
        item(objs.iron_dagger, weight = 4)
        // ...
    }
}
registry.register(HillGiantNpcs.giant, hillGiantTable)
```

**What's Already Done**:
- ✅ Hill Giant drop table (complete with giant key, limpwurt roots, runes, etc.)
- ✅ Moss Giant drop table (complete with mossy key, herbs, seeds, etc.)
- ✅ Man/Woman drop tables
- ✅ Barbarian drop tables
- ✅ Bear drop tables
- ✅ Black Knight drop tables
- ✅ Zombie drop tables
- ✅ And many more...

---

### 2. NPC Combat System (`content/other/npc-combat/`)

**Status**: ✅ **FUNCTIONAL via Engine + Minimal Scripts**

**How It Works** (from RSMod team's code):
```kotlin
// From ChaosDruidCombatScript.kt
class ChaosDruidCombatScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onNpcHit(NpcCombatTypes.chaos_druid) {
            // Retaliation and aggression handled by combat engine via npcs.toml stats
            // Custom behavior (e.g., bind spell effect) can be added here if needed
        }
    }
}
```

**Key Insight**: Combat stats are defined in **`npcs.toml`** files, not in Kotlin code!

**Example from Edgeville Dungeon**:
```toml
# npcs.toml
[[spawn]]
npc = 'giant'
coords = '0_48_51_25_40'
```

The combat engine reads NPC stats from cache/config and handles:
- ✅ Aggression
- ✅ Retaliation
- ✅ Attack speed
- ✅ Combat calculations

---

### 3. Hill Giants (`content/other/npc-drops/` + `areas/dungeons/edgeville-dungeon/`)

**Status**: ✅ **COMPLETE**

**What's Implemented**:
- ✅ Drop table (`HillGiantDropTables.kt`)
  - Big bones (100%)
  - Giant key (1/128) for Obor
  - Limpwurt roots, runes, coins, etc.
- ✅ NPC spawns in Edgeville Dungeon (`npcs.toml`)
  - 5 Hill Giant spawns
- ✅ Combat behavior (engine handles this)

**Files**:
```
content/other/npc-drops/tables/HillGiantDropTables.kt  # Drops
content/areas/dungeons/edgeville-dungeon/npcs.toml     # Spawns
```

---

### 4. Moss Giants (`content/other/npc-drops/` + `areas/dungeons/varrock-sewer/`)

**Status**: ✅ **COMPLETE**

**What's Implemented**:
- ✅ Drop table (`MossGiantDropTables.kt`)
  - Big bones (100%)
  - Mossy key (1/64) for Bryophyta
  - Herbs, seeds, nature runes, etc.
- ✅ NPC spawns in Varrock Sewers (`npcs.toml`)
  - 5 Moss Giant spawns
- ✅ Combat behavior (engine handles this)

**Files**:
```
content/other/npc-drops/tables/MossGiantDropTables.kt  # Drops
content/areas/dungeons/varrock-sewer/npcs.toml         # Spawns
```

---

### 5. Boss Framework (`content/other/bosses/`)

**Status**: ✅ **PATTERN EXISTS, but F2P bosses missing**

**Implemented Bosses**:
- ✅ King Black Dragon (`KingBlackDragon.kt`)
- ✅ Kalphite Queen (`KalphiteQueen.kt`)

**Pattern for Bosses**:
```kotlin
class KingBlackDragon @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onNpcHit(KingBlackDragonNpcs.king_black_dragon) {
            // Boss-specific logic here
        }
    }
}
```

**Missing**:
- ❌ Obor (Hill Giant boss)
- ❌ Bryophyta (Moss Giant boss)

---

## ❌ What's Actually Missing

### Critical Missing Pieces for F2P:

| Feature | Status | Impact |
|---------|--------|--------|
| **Obor boss** | ❌ Not implemented | BIS F2P weapon (Hill giant club) unobtainable |
| **Bryophyta boss** | ❌ Not implemented | BIS F2P staff unobtainable |
| **Obor key mechanic** | 🟡 Unclear | Need to verify if giant key opens Obor instance |
| **Bryophyta key mechanic** | 🟡 Unclear | Need to verify if mossy key opens Bryophyta instance |

### Other Potential Gaps:

| Feature | Status | Notes |
|---------|--------|-------|
| Giant key functionality | 🟡 Test needed | Drops exist, but does it open Obor? |
| Mossy key functionality | 🟡 Test needed | Drops exist, but does it open Bryophyta? |
| Obor instance | ❌ Missing | Boss lair not implemented |
| Bryophyta instance | ❌ Missing | Boss lair not implemented |

---

## 🔍 RSMod Architecture Pattern (CORRECTED)

### How NPCs Actually Work in RSMod:

```
┌─────────────────────────────────────────────────────────────┐
│                     NPC IMPLEMENTATION                       │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  1. CACHE / CONFIG                                           │
│     ├── NPC stats (combat level, HP, attack, etc.)          │
│     └── In: .cache/ or npcs.toml                            │
│                                                              │
│  2. SPAWN LOCATION                                           │
│     └── In: content/areas/.../npcs.toml                     │
│         [[spawn]]                                           │
│         npc = 'giant'                                       │
│         coords = '0_48_51_25_40'                            │
│                                                              │
│  3. DROP TABLE (if needed)                                   │
│     └── In: content/other/npc-drops/tables/                 │
│         HillGiantDropTables.kt                              │
│                                                              │
│  4. COMBAT SCRIPT (only for special behavior)               │
│     └── In: content/other/npc-combat/                       │
│         Only if custom behavior needed                      │
│         (most NPCs don't need this!)                        │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

**Key Insight**: Most NPCs DON'T need a combat script! The engine handles aggression/retaliation automatically based on NPC stats in the cache/config.

---

## 📊 Corrected F2P Task Status

### Tasks We Thought Were Needed (But Are Done):

| Task | Original Status | Actual Status | Notes |
|------|----------------|---------------|-------|
| F2P-CRIT-5 (Hill Giants) | 🔴 Blocked by API | ✅ **DONE** | Drops + spawns exist |
| F2P-CRIT-6 (Moss Giants) | 🔴 Blocked by API | ✅ **DONE** | Drops + spawns exist |
| API-NPC-COMBAT-1 | 🔴 Needed | ✅ **EXISTS** | Pattern in npc-combat/ |
| API-DROP-TABLE-1 | 🔴 Needed | ✅ **EXISTS** | api/drop-table/ works |

### Tasks Actually Needed:

| Task | Status | Work Required |
|------|--------|---------------|
| F2P-BOSS-1 (Obor) | ❌ Missing | Implement boss fight |
| F2P-BOSS-2 (Bryophyta) | ❌ Missing | Implement boss fight |
| Obor instance | ❌ Missing | Create boss lair |
| Bryophyta instance | ❌ Missing | Create boss lair |
| Key → Boss access | 🟡 Unclear | Wire up key mechanics |

---

## 🎯 What We Should Actually Do

### Priority 1: Test Existing Implementation

```bash
# Test if Hill Giants work
1. Log into game
2. Go to Edgeville Dungeon
3. Check if Hill Giants spawn
4. Kill one, verify drops (big bones, limpwurt, key?)

# Test if Moss Giants work
1. Go to Varrock Sewers
2. Check if Moss Giants spawn
3. Kill one, verify drops (big bones, mossy key?)
```

### Priority 2: Implement Missing Bosses

**Obor Implementation**:
```kotlin
// content/other/bosses/obor/Obor.kt
class Obor @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // 1. Check for giant key when player tries to enter
        // 2. Create instance
        // 3. Spawn Obor NPC
        // 4. Handle multi-phase combat
        // 5. Handle drops on death
    }
}
```

**Files to Create**:
```
content/other/bosses/obor/
├── Obor.kt                    # Boss logic
├── OborInstance.kt            # Instance management
├── OborDrops.kt               # Drop table (separate from Hill Giants)
└── OborConfig.kt              # Config references
```

### Priority 3: Non-Blocked F2P Content (Can Still Do)

These are NOT blocked and still need work:
- ✅ F2P-CRIT-1: Maple trees (WC)
- ✅ F2P-CRIT-2: Yew trees (WC)
- ✅ F2P-CRIT-3: Rune bar smelting (Smithing)
- ✅ F2P-CRIT-4: Rune equipment smithing (Smithing)
- ✅ F2P-BIS-2: Maple/Yew bows (Fletching)

---

## 📋 Revised Implementation Plan

### Week 1: Verify & Test
- [ ] Test Hill Giant spawns and drops
- [ ] Test Moss Giant spawns and drops
- [ ] Verify giant key drops
- [ ] Verify mossy key drops
- [ ] Test key mechanics (do they work?)

### Week 2: Maple/Yew + Smithing (Kimi)
- [ ] Add Maple trees to F2P areas
- [ ] Add Yew trees to F2P areas
- [ ] Implement Rune bar smelting
- [ ] Implement Rune equipment smithing

### Week 3: Bosses (Codex)
- [ ] Implement Obor boss
- [ ] Implement Bryophyta boss
- [ ] Wire up key → boss access

### Week 4: Integration & Quests
- [ ] Dragon Slayer I quest
- [ ] Full F2P playthrough test

---

## 🔧 How to Check If Something Exists

```bash
# Check for drop tables
grep -r "HillGiant\|MossGiant" rsmod/content/other/npc-drops/

# Check for spawns
grep -r "giant\|mossgiant" rsmod/content/areas/*/npcs.toml

# Check for boss implementation
grep -r "obor\|bryophyta" rsmod/content/other/bosses/

# Check for API
ls rsmod/api/drop-table/
ls rsmod/content/other/npc-combat/
```

---

## 📝 Lessons Learned

1. **Always check existing code first** - We assumed APIs were missing when they existed
2. **RSMod uses TOML for spawns** - Not Kotlin scripts
3. **Combat is engine-handled** - Most NPCs don't need combat scripts
4. **Drop tables are content** - In `content/other/npc-drops/`, not `api/`
5. **Bosses are the real gap** - Obor and Bryophyta are truly missing

---

**Status**: Analysis complete  
**Next Step**: Test existing implementation before building new APIs

