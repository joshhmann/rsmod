# F2P Task Contracts - For opencode

## Status: 28 tasks completed, 256 pending

---

## WAVE 1: QUICK WINS (High Priority - Can Complete Today)

### NPC Dialogues (10-15 min each)

| Task ID | NPC | Location | Pattern File | Files to Create |
|---------|-----|----------|--------------|-----------------|
| NPC-KARIM | Karim | Al Kharid | `lumbridge/npcs/GeneralStore.kt` | `alkharid/npcs/Karim.kt` |
| NPC-RANAEL | Ranael | Al Kharid | `lumbridge/npcs/GeneralStore.kt` | `alkharid/npcs/Ranael.kt` |
| NPC-DOMMIK | Dommik | Al Kharid | `lumbridge/npcs/GeneralStore.kt` | `alkharid/npcs/Dommik.kt` |
| NPC-LOU-E-G | Louie Legs | Al Kharid | `lumbridge/npcs/GeneralStore.kt` | `alkharid/npcs/LouieLegs.kt` |
| NPC-ZEKE | Zeke | Al Kharid | `lumbridge/npcs/GeneralStore.kt` | `alkharid/npcs/Zeke.kt` |
| NPC-GEM-TRADER | Gem Trader | Al Kharid | `lumbridge/npcs/GeneralStore.kt` | `alkharid/npcs/GemTrader.kt` |
| NPC-SILK-TRADER | Silk Trader | Al Kharid | `lumbridge/npcs/Bob.kt` | `alkharid/npcs/SilkTrader.kt` |
| NPC-ELLIS | Ellis | Al Kharid | `lumbridge/npcs/Bob.kt` | `alkharid/npcs/Ellis.kt` |

**Contract Template for NPC Dialogues:**
```
1) Objective: Implement [NPC_NAME] dialogue with talk-to interaction
2) Allowed files: rsmod/content/areas/city/alkharid/npcs/[NpcName].kt
3) Forbidden: engine/**, api/**, other cities
4) Pattern: Follow lumbridge/npcs/GeneralStore.kt or Bob.kt
5) Validation: ./gradlew :content:areas:city:alkharid:build
6) Output: NPC file with onOpNpc1 handler, choice dialogue, chatNpc/chatPlayer
7) Uncertainty: Leave TODO if dialogue content uncertain
```

### General Stores (Already Done)
- ✅ Draynor General Store
- ✅ Falador General Store
- ⏳ Al Kharid General Store (directory doesn't exist - needs full module)

---

## WAVE 2: SHOPS (Medium Priority)

| Task ID | Shop | Location | Notes |
|---------|------|----------|-------|
| F2P-SHOP-1 | Al Kharid General Store | Al Kharid | Need full module structure |
| F2P-SHOP-3 | Port Sarim General Store | Port Sarim | Add to existing module |
| SHOP-BETTY | Betty's Magic Emporium | Port Sarim | Magic supplies |

---

## WAVE 3: WORLD OBJECTS (Medium Priority)

| Task ID | Object | Locations | Pattern |
|---------|--------|-----------|---------|
| WORLD-FURNACE | Furnace | Lumbridge, Falador, Al Kharid, Edgeville | Already exists in SmithingFurnace.kt |
| WORLD-ANVIL | Anvil | Lumbridge, Varrock, Falador | Already exists in SmithingAnvil.kt |
| WORLD-ALTAR | Prayer Altar | Lumbridge, Varrock, Edgeville Monastery | Follow prayer tab pattern |
| WORLD-MILK | Dairy Cow | Lumbridge, Falador | Needs new interaction |
| WORLD-EGG | Chicken Eggs | Lumbridge | Needs new interaction |
| WORLD-FLOUR | Flour Mill | Lumbridge | Grain -> flour process |

---

## WAVE 4: AREAS (Lower Priority - Complex)

| Task ID | Area | Complexity | Blockers |
|---------|------|------------|----------|
| AREA-LUM-SWAMP | Lumbridge Swamp | Medium | None |
| AREA-DARK-WIZARD | Dark Wizards Tower | Low | None |
| AREA-WIZARD-TOWER | Wizards Tower | Medium | None |
| AREA-POTATO-ONION | Fields | Low | None |
| AREA-WHEAT-FIELD | Wheat Fields | Low | None |
| AREA-COW-PEN | Cow/Chicken Pens | Low | None |

---

## WAVE 5: NPC COMBAT & DROPS (Lower Priority - Needs Research)

| Task ID | NPC Type | Drop Table Source |
|---------|----------|-------------------|
| NPC-DROP-HILL-GIANT | Hill Giants | Kronos data |
| NPC-DROP-BARBARIAN | Barbarians | Kronos data |
| NPC-DROP-MUGGER | Muggers | Kronos data |
| NPC-DROP-MAN-WOMAN | Men/Women | Kronos data |
| NPC-DROP-SKELETON | Skeletons | Kronos data |

---

## COMPLETED BY OPCODE (28 Tasks)

### Areas (Completed)
- ✅ AREA-2: Draynor Village
- ✅ AREA-5: Falador
- ✅ AREA-6: Port Sarim/Rimmington
- ✅ AREA-7: Edgeville/Barbarian Village

### Quests (Completed)
- ✅ QUEST-4: Romeo & Juliet
- ✅ QUEST-5: Imp Catcher
- ✅ QUEST-12: Prince Ali Rescue
- ✅ QUEST-9: Vampyre Slayer
- ✅ QUEST-10: Dragon Slayer I

### Skills (Completed)
- ✅ CRAFT-1: Crafting Skill
- ✅ AGIL-1: Agility Gnome Course
- ✅ SKILL-23: Hunter Baseline

### NPCs (Completed)
- ✅ NPC-EXAMINE-1: Examine interactions
- ✅ F2P-NPC-1: Hans

### Systems (Completed)
- ✅ SYSTEM-UI-1: Friends/Ignore
- ✅ SYSTEM-UI-3: Music Player

---

## NEXT ACTIONS FOR OPCODE

1. **Claim Wave 1 NPC tasks** (Karim, Ranael, Dommik, etc.)
2. **Check Al Kharid module status** - create if missing
3. **Focus on simple dialogues first** (10-15 min each)
4. **Leave complex areas for later** (Lumbridge Swamp, Wizards Tower)

## REFERENCE PATTERNS

**Simple NPC Dialogue:**
- File: `lumbridge/npcs/Hengel.kt`
- Pattern: onOpNpc1, startDialogue, chatNpc, chatPlayer

**Shop NPC:**
- File: `lumbridge/npcs/GeneralStore.kt` or `port-sarim/npcs/Wydin.kt`
- Pattern: @Inject constructor, onOpNpc1 (dialogue), onOpNpc3 (shop)

**Area Module Structure:**
- `configs/[Area]Npcs.kt` - NPC references
- `configs/[Area]Invs.kt` - Shop inventories (if any)
- `npcs/*.kt` - Individual NPC files
- `[Area]Script.kt` - Module entry point
- `resources/npcs.toml` - Spawn locations

---

*Last updated: 2026-02-24*
*Total F2P Tasks: 284 (28 completed, 256 pending)*

