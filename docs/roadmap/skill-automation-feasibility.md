# Skill Automation Feasibility

## Purpose

Evaluate which RSMod skills can benefit from automation (wiki→data→config files) vs what must be hand-coded. Determine the best first POC skill for building reusable action foundations.

## Skill Status Summary (June 2026)

| Skill | Files | Existing System | Missing | POC Viable? |
|-------|:-----:|-----------------|---------|:-----------:|
| **Cooking** | 1 | Full 21 fish + nonfish with burn rates, range/fire, gauntlets, Cook-o-Matic | None for 1–99 | ✅ **Best POC** |
| **Woodcutting** | 11 | Full tree type enums, per-axe rates, tree respawn, easter egg, integration tests | Bird nests, axe effects | ✅ Immediate |
| **Mining** | 5 | Full ore rocks with depletion, pickaxe selection, gem rocks, level gating | Prospector outfit, infernal pickaxe, geode drops | ✅ Immediate |
| **Fishing** | 1 | Full fishing with spot NPCs, net/bait/lure/cage/harpoon | Fishing spots need BaseNpcs entries (see ENGINE_GAPS.md notes) | ✅ Immediate |
| **Smithing** | 5 | Furnace smelting + anvil smithing, bars, items | Interface-based product selection needs work | 🟡 After resource skills |
| **Firemaking** | 2 | Log lighting exists | Missing logs cache entries? Quick audit needed | 🟡 Needs woodcutting first |
| **Fletching** | 3 | Fletching exists | Interface-based bow creation | 🟡 Needs woodcutting first |
| **Crafting** | 7 | Gem cutting, jewelry, leather, pottery, spinning | Multiple sub-skills | 🟡 After core resource skills |
| **Thieving** | 1 | Thieving exists | NPC stalls? | 🟡 Lower priority |
| **Prayer** | 3 | Bone-burying, altar offering, full XP table | None for basic training | ✅ Already working |
| **Runecrafting** | 3 | Runecrafting exists | Altars, tiaras, pouches | 🟡 After core skills |
| **Herblore** | 3 | Herblore exists | Secondary ingredient data | 🟡 After farming |
| **Farming** | 3 | Farming exists | Full patch cycle | 🟡 Complex |
| **Agility** | 6 | 4 rooftop courses + base system | Missing courses | 🟡 Medium effort |
| **Hunter** | 1 | Hunter exists | Many missing trap types | 🟡 Large scope |
| **Slayer** | 6 | Slayer master tasks, unlocks | Many monsters | 🟡 Needs more NPCs |
| **Construction** | 2 | Base configs exist | Full POH system | ❌ Major system |

## Skill Action Data (Wiki → Corpus Availability)

The OSRS wiki corpus at `/var/lib/pixelrag/osrs/` provides structured data for:

| Dataset | Fields | Skills Covered |
|---------|--------|----------------|
| `skill_guides.json` | Level, item, XP, method | All 23 skills |
| `item_by_name.json` | Item ID, name, members, stackable, equipable | All items |
| `monster_by_name.json` | Monster stats, drops | Combat only |

**Key finding:** The corpus has skill training guides that list methods (e.g., "Burn oak logs" → 15 XP), but does NOT have structured behavior data (e.g., "click tree → check axe → start delay → check depletion → produce log → respawn timer"). **Skills are behavior-driven, not data-driven.**

## What Can Be Automated for Skills

### High Confidence (80-100%)
- **Level requirements** — wiki corpus has level per method
- **XP rates** — wiki corpus has XP per action
- **Product items** — wiki corpus links item names to outputs
- **Tool requirements** — wiki corpus has equipment data
- **Item symbols** — resolver handles standard item names

### Medium Confidence (40-80%)
- **Animation IDs** — wiki may list them in infoboxes, not reliably extracted
- **Resource locations** — wiki mentions regions, not `.sym` loc names
- **Depletion/respawn rates** — wiki has base values but OSRS changes them

### Low Confidence (<40%)
- **Behavioral mechanics** — tick delays, success chance formulas, animation timing
- **Interface widgets** — CS2 scripts, widget IDs not in structured corpus
- **Edge cases** — unique per-skill quirks (burn multipliers, invisible level boosts)

## Best POC: Cooking (Levels 1–30)

Cooking is the cleanest POC because:

1. **Already fully implemented** in RSMod (Cooking.kt with all food types)
2. **No new code needed** for 1–30 — raw shrimps, raw beef, raw chicken, trout, salmon all work
3. **Teaches the interaction pattern**: `onOpLocU(range/fire, rawFood) → check level → check burn → produce cooked/burnt`
4. **Has system gaps to test**: Does range vs fire differential work? Are cooking gauntlets functional? Does Cook-o-Matic quest unlock work?
5. **No resource node logic needed** — food is in inventory, not gathered from locs

### 1–30 POC Steps
1. Login asslord at Lumbridge
2. Acquire raw shrimps, raw beef, raw chicken
3. Test cooking on fire (Lumbridge cow field) — check XP, burn rates
4. Test cooking on range (Lumbridge kitchen) — check reduced burn chance
5. Acquire trout/salmon (via fishing or spawning)
6. Test higher-level fish — verify level gate works
7. Document everything that works and doesn't

This proves the interaction foundation works BEFORE building resource node systems.

## Best Resource Node POC: Woodcutting (Levels 1–30)

Woodcutting is the best resource node POC because:

1. **Fully implemented** — trees, axes, rates, depletion, respawn
2. **Teaches the resource node pattern**: `onOpLoc1(tree) → check axe → check level → play anim → delay → produce log → deplete tree → respawn timer`
3. **Has integration tests** — WoodcuttingScriptTest.kt exists
4. **Multiple tree types** — normal → oak → willow teaches tier progression
5. **Builds toward Wintertodt** — logs → firemaking → Wintertodt fuel

### 1–30 POC Steps
1. Chop normal trees in Lumbridge — verify XP, log production
2. Test each axe type (bronze, iron, steel) — verify speed differences
3. Chop oaks at Draynor Manor — verify level gate (15)
4. Test depletion/respawn — chop until tree depletes, time respawn
5. Document success rates per axe per tree type

## Skill Data Automation Tables

### Cooking Methods (1–30, from wiki corpus)

| Method | Level | XP | Input | Output | Burnt |
|--------|:-----:|:---:|-------|--------|:-----:|
| Raw shrimps | 1 | 30 | `raw_shrimps` | `shrimps` | `burnt_shrimp` |
| Raw beef | 1 | 30 | `raw_beef` | `cooked_meat` | `burnt_meat` |
| Raw chicken | 1 | 30 | `raw_chicken` | `cooked_chicken` | `burnt_chicken` |
| Raw trout | 15 | 70 | `raw_trout` | `trout` | `burnt_trout` |
| Raw salmon | 25 | 90 | `raw_salmon` | `salmon` | `burnt_salmon` |

### Woodcutting Methods (1–30, from wiki corpus)

| Tree | Level | XP | Log | Respawn (s) |
|:----|:-----:|:---:|:----|:-----------:|
| Normal | 1 | 25 | `logs` | ~8 |
| Oak | 15 | 37.5 | `oak_logs` | ~10 |
| Willow | 30 | 67.5 | `willow_logs` | ~13 |

## Automation Confidence Per Skill

| Skill | Data Coverage | Code Needed | Automation Potential | Best POC |
|-------|:-------------:|:-----------:|:--------------------:|:--------:|
| Cooking | ✅ Full | None | N/A — already implemented | Test existing |
| Woodcutting | ✅ Full | None | N/A — already implemented | Test existing |
| Mining | ✅ Full | None | N/A — already implemented | Test existing |
| Fishing | ✅ Full | BaseNpcs entries | Fill missing cache refs | Test existing |
| Smithing | ✅ Ore/bar data | Full system exists | Add bars 1–30 | After mining |
| Firemaking | 🟡 Log data | Full system exists | Test log->ash chain | After woodcutting |
| Fletching | 🟡 Item data | Full system exists | Test arrow shafts+bow | After woodcutting |
| Crafting | 🟡 Gem/leather | Full system exists | Test gem cutting 1–30 | Standalone |
| Thieving | 🟡 NPC data | Full system exists | Test pickpocket 1–30 | Standalone |
| Prayer | ✅ Bone data | Already working | No action needed | Already done |
| Runecrafting | 🟡 Altar data | Full system exists | Test rune essence->air | Standalone |
| Herblore | 🟡 Herb data | Full system exists | Requires farming/combat | Defer |
| Farming | 🟡 Seed data | Full system exists | Very complex | Defer to M6 |
| Agility | ✅ Course data | 4 courses done | Add more courses | Per-course |
| Hunter | 🟡 Trap data | Basic system exists | Very wide scope | Defer |
| Slayer | ✅ Monster data | Full task system | Requires more monsters | Defer to more NPCs |
| Construction | ❌ Limited | Stub only | Full POH engine needed | Defer to major system |

## Conclusion

**Best first POC:** Test existing Cooking 1–30 (no code changes needed, validates interaction foundation)
**Best resource system POC:** Test existing Woodcutting 1–30 (validates resource node + depletion + respawn)
**Next build target:** Shared SkillAction data class for new skill entries (reduce boilerplate)
**Deferred:** Construction (needs full POH engine), Sailing (no cache assets), Hunter (wide scope)
