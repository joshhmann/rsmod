Implement NPC combat and drop table for: $ARGUMENTS

Follow this exact workflow. Do not skip steps. Do not ask clarifying questions — use the data sources to find answers yourself.

---

## Step 1 — Find the task pair

Look up both tasks in the registry:
```
mcp__agent-tasks__get_task(taskId: "NPC-<NAME>-COMB")   // or similar
mcp__agent-tasks__get_task(taskId: "NPC-DROP-<NAME>")
```

If unsure of the exact task ID, list pending tasks and search for the NPC name:
```
mcp__agent-tasks__list_tasks(status: "pending")
```

Claim both tasks before doing any work:
```
mcp__agent-tasks__claim_task(taskId: "NPC-XXX-COMB", agent: "claude")
mcp__agent-tasks__claim_task(taskId: "NPC-DROP-XXX", agent: "claude")
```

---

## Step 2 — Find the internal cache name

Run `search_npctypes(query: "$ARGUMENTS")` via the osrs-cache MCP server.

This returns the internal sym name (e.g. `hill_giant`, NOT `Hill Giant`).
Also look for variant suffixes: `_2`, `_3`, `aggressive_`, etc.

**Common traps:**
- `dark_wizard` does not exist — use `bearded_dark_wizard` and `young_dark_wizard`
- `man` not `human`, `woman` not `female`
- Check `rsmod/.data/symbols/npc.sym` if search returns nothing

---

## Step 3 — Get combat stats

Use **both** sources and prefer the wiki:

```
get_npc_rev233(name: "$ARGUMENTS")           // primary — rev 233 accurate
```

Also read the Kronos JSON for animations:
```
Kronos-184-Fixed/Kronos-master/kronos-server/data/npcs/combat/<Name>.json
```

Fields you need:
- `hitpoints`, `attack`, `strength`, `defence`
- `attack_ticks` (= timer / combat speed)
- `attack_animation`, `defend_animation`, `death_animation` (seq IDs)
- `aggressive_level` (for future aggression wiring — note it but don't implement now)

---

## Step 4 — Get drop table

```
osrs_wiki_parse_page(page: "$ARGUMENTS")     // wiki is ground truth for quantities
```

Also check Kronos drops for structure:
```
Kronos-184-Fixed/Kronos-master/kronos-server/data/npcs/drops/eco/<Name>.json
```

Extract:
- Guaranteed drops (always given)
- Weighted table structure (which tables exist, what items, what weights)
- Note any items that need to be added to `DropTableObjs.kt`

---

## Step 5 — Edit the four files

All files are in:
```
rsmod/content/other/npc-drops/src/main/kotlin/org/rsmod/content/other/npcdrops/
```

### 5a — DropTableNpcs.kt
Add NPC type references. Read the file first to avoid duplicates.

```kotlin
// Add to the existing DropTableNpcs object:
val hill_giant = find("hill_giant")
val hill_giant_2 = find("hill_giant_2")  // add all variants found in Step 2
```

### 5b — DropTableObjs.kt
Add any item refs not already in `BaseObjs` or `DropTableObjs`. Read both files first.

To check if an item exists: `search_objtypes(query: "item name")`

```kotlin
// Add to the existing DropTableObjs object:
val limpwurt_root = find("limpwurt_root")
val big_bones = find("big_bones")        // likely already there — check first
```

### 5c — F2PMonsterCombatScript.kt
Add an `onNpcHit` handler. Read the file first. Add the call in `startup()`.

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

private fun ScriptContext.registerHillGiant() {
    val npcs = listOf(
        DropTableNpcs.hill_giant,
        DropTableNpcs.hill_giant_2,
    )
    npcs.forEach { npcType -> onNpcHit(npcType) { /* Retaliation handled by engine */ } }
}
```

### 5d — NpcDropTablesScript.kt
Add a drop table registration. Read the file first. Add the call in `startup()`.

```kotlin
// In startup():
registerHillGiant()

// -----------------------------------------------------------------------
// Hill Giant
// Drop table source: https://oldschool.runescape.wiki/w/Hill_Giant
// Always: Big bones
// -----------------------------------------------------------------------
private fun registerHillGiant() {
    val table = dropTable {
        always(DropTableObjs.big_bones)

        table("Armour/Weapons", weight = 1) {
            item(objs.iron_sword, weight = 5)
            item(objs.iron_full_helm, weight = 3)
            nothing(weight = 10)
        }

        table("Runes", weight = 1) {
            item(objs.chaos_rune, quantity = 3..3, weight = 5)
            item(objs.nature_rune, weight = 2)
        }

        table("Other", weight = 1) {
            item(objs.coins, quantity = 30..100, weight = 10)
            item(DropTableObjs.limpwurt_root, weight = 2)
        }
    }

    val npcs: List<NpcType> = listOf(
        DropTableNpcs.hill_giant,
        DropTableNpcs.hill_giant_2,
    )
    registry.register(npcs, table)
}
```

**Drop table DSL reference:**
| Syntax | Meaning |
|--------|---------|
| `always(objs.item)` | Drop every kill |
| `table("Name", weight = 1) { ... }` | One loot table — engine picks one per kill |
| `item(objs.item, weight = 5)` | Item within a table |
| `item(objs.item, quantity = 3..10, weight = 5)` | Range quantity |
| `nothing(weight = 10)` | No drop from this roll |

**Common items already available:**

| Item | Reference |
|------|-----------|
| Bones | `objs.bones` |
| Big bones | `DropTableObjs.big_bones` |
| Coins | `objs.coins` |
| Air/Mind/Water/Earth/Fire rune | `objs.air_rune` etc. |
| Body rune | `DropTableObjs.body_rune` |
| Chaos/Nature/Death rune | `objs.chaos_rune` etc. |
| Iron sword | `objs.iron_sword` |
| Iron dagger | `DropTableObjs.iron_dagger` |
| Feather | `DropTableObjs.feather` |
| Unid. guam | `DropTableObjs.unidentified_guam` |
| Unid. ranarr | `DropTableObjs.unidentified_ranarr` |
| Air talisman | `DropTableObjs.air_talisman` |

---

## Step 6 — Build

```javascript
build_server({ module: "npc-drops" })
```

If build fails:
- Check import errors — look at top of existing file for required imports
- Check sym name errors — re-run `search_npctypes` or `search_objtypes` for the exact name
- If an item sym is wrong: use `find("correct_name")` — no fallback IDs
- Fix the error and rebuild until it passes

---

## Step 7 — Complete the tasks

```
mcp__agent-tasks__complete_task(
  taskId: "NPC-XXX-COMB",
  agent: "claude",
  notes: "Added Hill Giant combat handler (3 variants). Retaliation engine-handled."
)
mcp__agent-tasks__complete_task(
  taskId: "NPC-DROP-XXX",
  agent: "claude",
  notes: "Added Hill Giant drop table: big bones always, 3 weighted tables. Wiki-accurate."
)
```

---

## Reference

Full guide with more detail: `docs/MICRO_TASK_GUIDE.md`
Sym naming traps: `docs/SYM_NAMING_GUIDE.md`
