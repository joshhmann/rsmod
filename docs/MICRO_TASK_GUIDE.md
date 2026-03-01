# Micro-Task Guide — NPC Combat & Drop Tables

**Target audience:** Small/fast models (Haiku, smaller Sonnet) assigned a single NPC combat or drop table task.
**Scope:** Covers the two most common mechanical tasks: adding an NPC's combat handler and adding its drop table.

---

## The 5 Files You Need to Know

All NPC combat + drop work lives in one module:
```
rsmod/content/other/npc-drops/src/main/kotlin/org/rsmod/content/other/npcdrops/
├── F2PMonsterCombatScript.kt   ← Add onNpcHit handlers here
├── NpcDropTablesScript.kt      ← Add drop table registrations here
├── DropTableNpcs.kt            ← Add NPC type references (find("name")) here
└── DropTableObjs.kt            ← Add item references if not in BaseObjs
```

---

## Step-by-Step Workflow

### 1. Claim the task
```
mcp__agent-tasks__claim_task(taskId: "NPC-XXX", agent: "your-name")
```

### 2. Look up the NPC's internal cache name
Use the MCP OSRS cache tool:
```
search_npctypes(query: "hill giant")
```
This gives you the internal sym name (e.g., `hill_giant`) used in `find("hill_giant")`.

> **CRITICAL SYM RULE:** Internal names are NOT always what you expect. Check `rsmod/.data/symbols/npc.sym` if the search returns no match. Common traps:
> - `"man"` not `"human"`
> - `"dark_wizard"` does not exist — use `"bearded_dark_wizard"` and `"young_dark_wizard"`

### 3. Look up combat stats
**Primary source:** Kronos JSON
```
Kronos-184-Fixed/Kronos-master/kronos-server/data/npcs/combat/<MonsterName>.json
```
Key fields to extract:
- `hitpoints` → `hitpoints` in NpcEditor
- `attack` / `strength` / `defence` → combat stats
- `attack_ticks` → timer interval
- `attack_animation` → attack anim ID
- `defend_animation` → block anim ID
- `death_animation` → death anim ID
- `aggressive_level` → used to determine aggression radius

**Secondary source:** OSRS wiki page parse via `osrs-cache` MCP:
```
osrs_wiki_parse_page(page: "Hill Giant")
```

### 4. Look up drop table
**Primary source:** Kronos JSON
```
Kronos-184-Fixed/Kronos-master/kronos-server/data/npcs/drops/eco/<MonsterName>.json
```
**Always cross-reference with OSRS wiki:**
```
osrs_wiki_parse_page(page: "Hill Giant")
```
Kronos is rev 184 — drop quantities may differ from rev 233. Wiki takes priority.

### 5. Edit the files (see templates below)

### 6. Build and verify
```
./gradlew :content:other:npc-drops:build
```
Build must pass with zero errors. No need to boot the server for this task.

### 7. Complete the task
```
mcp__agent-tasks__complete_task(taskId: "NPC-XXX", agent: "your-name", notes: "Added Hill Giant combat + drops")
```

---

## Template: Adding NPC References (DropTableNpcs.kt)

Add to the existing `DropTableNpcs` object. Find the sym name first.

```kotlin
// In DropTableNpcs.kt — add to the existing object
val hill_giant = find("hill_giant")
val hill_giant_2 = find("hill_giant_2")  // add variants if they exist in cache
```

**How to find variants:** Run `search_npctypes(query: "hill giant")` — look for entries sharing the same base name with suffix `_2`, `_3`, etc.

---

## Template: Adding a Combat Handler (F2PMonsterCombatScript.kt)

Add a `registerXxx()` private function and call it from `startup()`.

**Single variant:**
```kotlin
// In startup():
registerHillGiant()

// New private function:
private fun ScriptContext.registerHillGiant() {
    onNpcHit(DropTableNpcs.hill_giant) { /* Retaliation handled by engine */ }
}
```

**Multiple variants:**
```kotlin
// In startup():
registerHillGiant()

// New private function:
private fun ScriptContext.registerHillGiant() {
    val npcs = listOf(
        DropTableNpcs.hill_giant,
        DropTableNpcs.hill_giant_2,
        // add all variants
    )
    npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
}
```

> **Note:** The handler body is intentionally empty. The combat engine handles retaliation automatically when the NPC has combat stats set. The `onNpcHit` hook is registered so the engine knows to track this NPC type.

---

## Template: Adding a Drop Table (NpcDropTablesScript.kt)

Add a `registerXxx()` private function and call it from `startup()`.

```kotlin
// In startup() — add the call:
registerHillGiant()

// New private function — fill in from Kronos JSON + wiki:
// -----------------------------------------------------------------------
// Hill Giant
// Drop table source: https://oldschool.runescape.wiki/w/Hill_Giant
// Always: Big bones
// Three tables: Armour/Weapons, Runes, Other
// -----------------------------------------------------------------------
private fun registerHillGiant() {
    val table = dropTable {
        // Guaranteed drops (always given, every kill)
        always(DropTableObjs.big_bones)

        // Weighted tables — engine picks ONE table per kill (weighted by 'weight' param)
        // Within the chosen table, ONE item is selected (weighted by item 'weight')
        table("Armour/Weapons", weight = 1) {
            item(objs.iron_sword, weight = 5)
            item(objs.iron_full_helm, weight = 3)
            item(objs.iron_kiteshield, weight = 2)
            nothing(weight = 10)  // "nothing" entry = no drop from this roll
        }

        table("Runes", weight = 1) {
            item(objs.mind_rune, quantity = 9..9, weight = 5)
            item(objs.chaos_rune, quantity = 3..3, weight = 3)
            item(objs.death_rune, quantity = 1..1, weight = 1)
        }

        table("Other", weight = 1) {
            item(objs.coins, quantity = 30..100, weight = 10)
            item(DropTableObjs.limpwurt_root, weight = 2)  // add to DropTableObjs if missing
        }
    }

    val npcs: List<NpcType> = listOf(
        DropTableNpcs.hill_giant,
        DropTableNpcs.hill_giant_2,
    )
    registry.register(npcs, table)
}
```

### Drop Table DSL Reference

| Syntax | Meaning |
|--------|---------|
| `always(objs.item)` | Drop this every kill, no roll |
| `always(objs.item, quantity = 5..15)` | Always drop 5–15 of item |
| `table("Name", weight = 1) { ... }` | One table entry; higher weight = more likely to be chosen |
| `item(objs.item, weight = 5)` | Item within a table; higher weight = more likely within this table |
| `item(objs.item, quantity = 3..10, weight = 5)` | Range quantity within a table |
| `nothing(weight = 10)` | "No drop" entry — used to reduce effective drop rate |

### Using Items Not in BaseObjs

Check `BaseObjs` first: `rsmod/api/config/src/main/kotlin/org/rsmod/api/config/refs/BaseObjs.kt`

If the item isn't there, add it to `DropTableObjs.kt`:
```kotlin
// In DropTableObjs.kt:
val limpwurt_root = find("limpwurt_root")  // check npc.sym for exact name
val big_bones = find("big_bones")
```

> **Item sym naming:** Use `search_objtypes(query: "limpwurt")` to find the internal name. See `docs/SYM_NAMING_GUIDE.md` for known tricky names.

---

## Common Items for F2P Drop Tables

These are available in `DropTableObjs` or `objs` (BaseObjs):

| Item | Reference |
|------|-----------|
| Bones | `objs.bones` |
| Big bones | `DropTableObjs.big_bones` |
| Coins | `objs.coins` |
| Air rune | `objs.air_rune` |
| Mind rune | `objs.mind_rune` |
| Water rune | `objs.water_rune` |
| Earth rune | `objs.earth_rune` |
| Fire rune | `objs.fire_rune` |
| Body rune | `DropTableObjs.body_rune` |
| Chaos rune | `objs.chaos_rune` |
| Nature rune | `objs.nature_rune` |
| Death rune | `objs.death_rune` |
| Bronze sword | `objs.bronze_sword` |
| Iron dagger | `DropTableObjs.iron_dagger` |
| Iron sword | `objs.iron_sword` |
| Feather | `DropTableObjs.feather` |
| Unid. guam | `DropTableObjs.unidentified_guam` |
| Unid. ranarr | `DropTableObjs.unidentified_ranarr` |
| Cowhide | `DropTableObjs.cowhide` |
| Raw chicken | `DropTableObjs.raw_chicken` |
| Air talisman | `DropTableObjs.air_talisman` |
| Water talisman | `DropTableObjs.water_talisman` |
| Clue scroll (easy) | `DropTableObjs.trail_clue_easy_simple001` |

---

## Kronos Combat JSON → RSMod Field Mapping

| Kronos field | RSMod NpcEditor field | Notes |
|-------------|----------------------|-------|
| `hitpoints` | `hitpoints` | Direct |
| `attack` | `attack` | Direct |
| `strength` | `strength` | Direct |
| `defence` | `defence` | Direct |
| `attack_ticks` | `timer` | Combat speed in ticks |
| `attack_animation` | `attackAnim` or via params | Seq ID |
| `defend_animation` | `defenceAnim` or via params | Seq ID |
| `death_animation` | `deathAnim` or via params | Seq ID |
| `aggressive_level` | Used for aggression radius | Not direct — see NpcAggression |
| `respawn_ticks` | `respawnRate` | |

> **NpcEditor combat stats** are set in the NPC's config file, e.g.:
> `rsmod/content/generic/generic-npcs/src/main/kotlin/org/rsmod/content/generic/npcs/<npc>/`
>
> If no config file exists for this NPC yet, create one following the pattern in:
> `rsmod/content/generic/generic-npcs/src/main/kotlin/org/rsmod/content/generic/npcs/cow/CowConfig.kt`

---

## Paired Task IDs

Most NPC work comes in pairs — combat handler + drop table. Both live in the same module.
If your task ID ends in `-COMB`, add the `onNpcHit` to `F2PMonsterCombatScript.kt`.
If your task ID ends in `-DROP-*`, add the drop table to `NpcDropTablesScript.kt`.
You can do both in one PR — they're in the same file.

---

## Data Sources (in priority order)

1. **osrs-cache wiki parse + rev233 symbols** — `osrs_wiki_parse_page(page: "X")` + verify names/IDs in `rsmod/.data/symbols/*.sym`
2. **Kronos-184 combat JSON** — `Kronos-184-Fixed/Kronos-master/kronos-server/data/npcs/combat/X.json` — good baseline for combat stats
3. **Kronos-184 drops JSON** — `Kronos-184-Fixed/Kronos-master/kronos-server/data/npcs/drops/eco/X.json` — starting point, cross-reference with wiki for quantities

---

## Checklist Before Completing Task

- [ ] NPC `find("name")` added to `DropTableNpcs.kt`
- [ ] All variants found via `search_npctypes()` — don't miss `_2`, `_3` suffixes
- [ ] `onNpcHit` handler registered in `F2PMonsterCombatScript.kt` (for `-COMB` tasks)
- [ ] Drop table registered in `NpcDropTablesScript.kt` (for `-DROP-*` tasks)
- [ ] New items added to `DropTableObjs.kt` (if not in BaseObjs)
- [ ] `./gradlew :content:other:npc-drops:build` passes with zero errors
- [ ] Task marked complete in registry

