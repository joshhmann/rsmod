# RSMod v2 Implementation Backlog — Cache Analysis

**Generated:** 2026-02-26  
**Cache Source:** OSRS Rev 233 (OpenRS2 runescape/2293)  
**Total NPCs:** 14,794 | **Total Items:** 31,175 | **Total Locations:** 57,691

---

## 📊 Quick Stats

| Category | Total in Cache | Implemented | Missing | Priority |
|----------|---------------|-------------|---------|----------|
| NPC Combat + Drops | ~500 F2P relevant | ~25 | ~475 | 🔴 High |
| Shops | ~100 | ~15 | ~85 | 🟡 Medium |
| Quests | 22 F2P | 13 | 9 | 🔴 High |
| Skills (processing) | 17 | 17 | 0 (interfaces missing) | 🟡 Medium |
| Furnace interactions | 159 locs | Partial | Most | 🔴 High |
| Make-X interfaces | All skills | 1 (smithing) | All | 🔴 High |

---

## 🔴 PRIORITY 1: Critical Path (Blocks Playability)

### 1. NPC Combat & Drop Tables

**Status:** Only basic F2P NPCs done (~5% complete)

#### F2P Training Monsters (High Priority)
| NPC | Cache ID | Status | Notes |
|-----|----------|--------|-------|
| Hill Giant | 13502-13504 variants | 🟡 Partial | Combat done, drops done |
| Skeleton | 70-84 variants | 🟡 Partial | Combat done, drops done |
| Zombie | 26-45 variants | ❌ Missing | Edgeville Dung, Varrock Sewer, Draynor Sewer |
| Moss Giant | 100-104 variants | ❌ Missing | Varrock Sewer, Wilderness, Crandor |
| Mugger | 436 | ❌ Missing | Lumbridge, Varrock, Wilderness |
| Barbarian | 324-329 variants | ❌ Missing | Barbarian Village |
| Dwarf | 321-323 variants | ❌ Missing | Dwarven Mine, Ice Mountain |
| Warrior Woman | 4095 | ❌ Missing | Varrock Palace |
| Bear | 128-131 variants | ❌ Missing | Varrock, Wilderness |
| Unicorn | 2837, 2849 | ✅ Done | Just completed |

**Cache Search Results:**
- **Goblin variants:** 259 total (F2P: ~20 variants) — ✅ Basic goblin done
- **Skeleton variants:** 114 total (F2P: ~15 unarmed/armed) — 🟡 Partial
- **Zombie variants:** 303 total (F2P: ~25 unarmed/sewer) — ❌ Missing
- **Guard variants:** 821 total (F2P: ~10) — ✅ Basic guard done

**Implementation Pattern:**
```bash
# Use batch processor for F2P NPCs
python tools/batch_npc_processor.py --tier 1 --dry-run
python tools/batch_npc_processor.py --tier 1
```

---

### 2. Magic Utility Spells

**Status:** Combat spells done, utility spells missing

| Spell | Status | Blocker |
|-------|--------|---------|
| Varrock Teleport | ❌ Missing | Rune check + animation + coord |
| Lumbridge Teleport | ❌ Missing | Rune check + animation + coord |
| Falador Teleport | ❌ Missing | Rune check + animation + coord |
| High Alchemy | ❌ Missing | Item value lookup + animation |
| Low Alchemy | ❌ Missing | Item value lookup + animation |
| Superheat Item | ❌ Missing | Requires furnace + bar logic |

**Cache Resources:**
- `search_objtypes "teleport"` — 47 results
- `search_objtypes "alch"` — alchemy spell items
- Spell rune requirements in `api/spells-runes/` (has compile errors)

---

### 3. Make-X Interfaces

**Status:** Only Smithing anvil has Make-X

| Skill | Interface | Status | Blocker |
|-------|-----------|--------|---------|
| Fletching | Bow/string/arrows | ❌ Missing | MAKEQ-FLETCH |
| Herblore | Potions | ❌ Missing | MAKEQ-HERB |
| Crafting | Gems/leather | ❌ Missing | MAKEQ-CRAFT |
| Cooking | Food | ❌ Missing | MAKEQ-COOK |
| Smithing | Anvil | 🟡 Partial | Works but no selection UI |

**Key Insight:** All 3 skills have the ITEM-ON-ITEM handlers (`onOpHeldU`) working. Only the Make-X *interface* for batch processing is missing.

---

### 4. Furnace Location Interactions (SMITH-2)

**Cache Data:** 159 furnace locations

| Furnace Type | Cache IDs | Locations | Status |
|--------------|-----------|-----------|--------|
| Standard furnace | 2030, 2099 | Lumbridge, Al Kharid, Falador, Edgeville | ❌ Missing |
| Doric's anvil | 2031 | Taverley (Doric's Quest) | ❌ Missing |
| Varrock furnace | 5098 | Varrock east | ❌ Missing |

**F2P Furnace Locations (need handlers):**
- Lumbridge (3228, 3256)
- Al Kharid (3277, 3185)
- Falador west (2973, 3369)
- Edgeville (3111, 3496)
- Varrock east (3239, 3426)

**Implementation:**
```kotlin
// In content/skills/smithing/scripts/SmithingFurnace.kt
onOpLoc1(furnace_locs) { smeltInterface(player) }
```

---

## 🟡 PRIORITY 2: Noticeable Gaps

### 5. Areas / Dungeons

| Area | Cache Locs | NPCs | Status |
|------|------------|------|--------|
| Edgeville Dungeon | ~50 | Hill Giants, Skeletons, Zombies | ❌ Missing |
| Varrock Sewer | ~30 | Moss Giants, Zombies, Skeletons | ❌ Missing |
| Draynor Sewer | ~20 | Zombies, Skeletons | ❌ Missing |
| Karamja (F2P) | ~100 | None (surface only) | ❌ Missing |
| Dwarven Mine (full) | ~200 | Dwarves, scorpions | 🟡 Partial |
| Asgarnian Ice Dungeon | ✅ Done | Ice warriors, Ice giants | ✅ Done |

**Cache Search:**
- `search_loctypes "dungeon"` — 1,761 results
- `search_loctypes "sewer"` — 47 results
- `search_loctypes "cave"` — 2,890 results

---

### 6. Shop NPCs

**Cache Data:** 100+ shopkeeper NPCs

| Shop Type | Count | Implemented | Priority |
|-----------|-------|-------------|----------|
| General store | ~15 | 5 | Medium |
| Weapon shops | ~20 | 3 (Bob, Lowe, Zaff) | Medium |
| Armor shops | ~15 | 2 (Horvik, Wayne) | Medium |
| Food shops | ~10 | 2 | Low |
| Crafting shops | ~8 | 1 (Ellis tanner) | Low |
| Magic shops | ~10 | 1 (Aubury runes) | Low |

**Missing F2P Shops:**
- Gerrant's Fish Shop (Port Sarim)
- Wydin's Food Store (Port Sarim)
- Grum's Gold Exchange (Port Sarim)
- Rommik's Crafty Supplies (Rimmington)
- Tynan's Fishing Supplies (Port Sarim)
- Brian's Battleaxe Bazaar (Port Sarim)

---

### 7. Quests

**Status:** 13/22 F2P quests complete

| Quest | Status | Missing Parts |
|-------|--------|---------------|
| Cook's Assistant | 🟡 Broken | Compile errors (QUEST-1) |
| Pirate's Treasure | 🔴 Disabled | null internalId (BUILD-CRIT-15) |
| Shield of Arrav | ❌ Missing | Full implementation |
| Knight's Sword | ❌ Missing | Full implementation + blurite mine |
| Misthalin Mystery | ❌ Missing | Full implementation |
| Below Ice Mountain | ❌ Missing | Camdozaal area + mechanics |
| X Marks the Spot | ❌ Missing | Full implementation |
| Corsair Curse | ❌ Missing | Corsair Cove + boss |
| Prince Ali Rescue | 🟡 Partial | Quest logic exists, need verification |

**Quest NPCs Needed (from cache):**
- Shield of Arrav: ~15 NPCs (Katrine, Baraek, Straven, Charlie, etc.)
- Knight's Sword: ~8 NPCs (Squire, Sir Vyvin, Thurgo, Reldo)
- Corsair Curse: ~10 NPCs (Corsairs, boss)

---

### 8. Combat System Gaps

| Feature | Status | Notes |
|---------|--------|-------|
| Combat level calculation | ❌ Missing | `SYSTEM-COMBAT-LEVEL` |
| Special attacks | ✅ Done | Framework exists |
| NPC attack styles | 🟡 Partial | Some NPCs missing style definitions |
| Ranged ammo recovery | 🟡 Partial | Needs validation |
| Prayer drain | ✅ Done | Works |
| Prayer bonus effects | ✅ Done | Works |

---

## 🟢 PRIORITY 3: Polish & Content

### 9. Wilderness Content

| Feature | Status | Cache Resources |
|---------|--------|-----------------|
| Skulling | ❌ Missing | WILD-1 |
| Item protection | ❌ Missing | WILD-2 |
| Combat level ranges | ❌ Missing | WILD-3 |
| Wilderness bosses | N/A | Not F2P |
| Revenants | N/A | Not F2P |

**Cache Wilderness NPCs:**
- Hill Giant variants: 13502-13504
- Skeleton variants: Multiple
- Zombie variants: Multiple
- Black Knight variants: Multiple

---

### 10. Interfaces

| Interface | Status | Blocker |
|-----------|--------|---------|
| Level-up messages | ❌ Missing | SYSTEM-LEVELUP |
| Friends list backend | ❌ Missing | SYSTEM-FRIENDS |
| Ignore list backend | ❌ Missing | SYSTEM-FRIENDS |
| GE trading | 🟡 Partial | SYSTEM-GE-IMPL |
| Music unlock | 🟡 Partial | SYSTEM-MUSIC-UNLOCK |

---

### 11. NPCs Missing Dialogue

**F2P NPCs without dialogue (from cache search):**

| NPC | Cache ID | Location | Priority |
|-----|----------|----------|----------|
| Hans | 3077 | Lumbridge Castle | Low |
| Cook | 3078 | Lumbridge Castle | ✅ Done |
| Duke Horacio | 3080 | Lumbridge Castle | Low |
| Father Aereck | 3079 | Lumbridge Church | 🟡 Partial |
| Seth Groats | 3081 | East Lumbridge | Low |
| Gillie Groats | 3082 | Lumbridge Cow pen | Low |
| Veos | 3083 | Port Sarim | Low |
| Redbeard Frank | 3084 | Port Sarim | 🟡 Partial |
| Wilfred | 3085 | North of Falador | Low |
| Wyson | 3086 | Falador Park | Low |
| Sir Tiffy Cashien | 3087 | Falador Park | Low |

---

## 📋 Implementation Toolkit

### NPC Data Extraction
```bash
# Single NPC lookup
python tools/npc_lookup.py "Hill Giant"
python tools/npc_lookup.py "Hill Giant" --output kotlin

# Batch processing
python tools/batch_npc_processor.py --tier 1 --dry-run
python tools/batch_npc_processor.py --tier 1

# Wiki data extraction
python OSRSWikiScraper/main.py -n "Green dragon"
python OSRSWikiScraper/main.py -e "Dragon Scimitar"
```

### Cache Symbol Verification
```bash
# Before implementing, verify symbol exists:
search_npctypes "zombie_unarmed" --pageSize 5
search_objtypes "unicorn_horn" --pageSize 5
search_loctypes "furnace" --pageSize 5
```

### Build Commands
```bash
# Single module build
./gradlew :content:other:npc-drops:build --console=plain
./gradlew :content:skills:magic:build --console=plain
./gradlew :content:areas:city:varrock:build --console=plain

# Full build (slow)
./gradlew build -x test --console=plain

# Preflight checks
scripts/preflight-ref-hygiene.ps1 -FailOnIssues
./gradlew spotlessApply
```

---

## 🎯 Recommended Sprint Structure

### Week 1: F2P Combat Core
1. **NPC-DROP-ZOMBIE-F2P** — Zombie combat + drops
2. **NPC-DROP-MOSS-GIANT-F2P** — Moss Giant combat + drops
3. **NPC-DROP-MUGGER-F2P** — Mugger combat + drops
4. **NPC-DROP-BARBARIAN-F2P** — Barbarian combat + drops
5. **AREA-EDGEVILLE-DUNG** — Edgeville Dungeon population

### Week 2: Magic & Smithing
1. **MAGIC-TELE** — Teleport spells (3 cities)
2. **MAGIC-ALCH** — High/Low alchemy
3. **SMITH-2** — Furnace loc interactions
4. **SYSTEM-LEVELUP** — Level-up messages

### Week 3: Make-X & Quests
1. **MAKEQ-FLETCH** — Fletching Make-X
2. **MAKEQ-HERB** — Herblore Make-X
3. **MAKEQ-CRAFT** — Crafting Make-X
4. **QUEST-SHIELD-OF-ARRAV** — Quest implementation

---

## 🔍 Key Cache Discoveries

### NPC Symbol Patterns
- **Standard NPCs:** `man`, `man2`, `man3`, `woman`, etc.
- **Monsters:** `goblin`, `goblin_2`, `goblin_3`, `skeleton_unarmed`, `zombie_unarmed`
- **Wilderness variants:** `wilderness_hill_giant`, `wilderness_rogue`
- **Quest NPCs:** Often prefixed with quest name (e.g., `romeo`, `juliet`)

### Location Symbol Patterns
- **Trees:** `tree`, `tree2`, `oaktree`, `willowtree`, `yewtree`
- **Rocks:** `rock`, `copper_rock`, `tin_rock`, `iron_rock`
- **Fishing:** `fishing_spot`, `lure_fishing_spot`, `bait_fishing_spot`
- **Furnaces:** `furnace`, `furnace2`, `fai_varrock_furnace_chimney`

### Item Symbol Patterns
- **Runes:** `airrune`, `waterrune`, `firerune`, `earthrune`, `mindrune`, `bodyrune`
- **Logs:** `logs`, `oak_logs`, `willow_logs`, `maple_logs`, `yew_logs`, `magic_logs`
- **Ores:** `copper_ore`, `tin_ore`, `iron_ore`, `coal`, `mithril_ore`, `adamantite_ore`, `runite_ore`
- **Bars:** `bronze_bar`, `iron_bar`, `steel_bar`, etc.

---

## 📈 Success Metrics

| Milestone | Target | Current |
|-----------|--------|---------|
| F2P NPC Combat | 50 NPCs | ~25 |
| F2P NPC Drop Tables | 50 tables | ~15 |
| F2P Quests | 22 complete | 13 |
| F2P Shops | 30 shops | ~15 |
| F2P Dungeons | 5 complete | 2 |
| Utility Spells | 6 spells | 0 |

---

*Document generated from cache analysis. For latest status, check `docs/CONTENT_AUDIT.md`.*

