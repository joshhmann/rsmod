# Legacy Implementation Playbook

> How to extract, translate, and port content from Kronos, Alter, and other local repos into RSMod v2.

---

## Source Priority Order

Always use the most accurate source first. Higher = more accurate.

| Priority | Source | Revision | Use for |
|----------|--------|----------|---------|
| 1 | **MCP `get_npc_rev233`** | 233 | NPC combat stats, HP, attack speed |
| 2 | **`wiki-data/` JSON files** | 233 | Skill XP tables, item data, pre-scraped wiki |
| 3 | **MCP `osrs_wiki_parse_page`** | Live | Full drop tables, quest walkthroughs |
| 4 | **Alter plugins** | 228 | Kotlin dialogue patterns, mechanic logic |
| 5 | **Kronos JSON data** | 184 | NPC combat data (fallback), drop table structure |
| 6 | **Kronos Java source** | 184 | Skill logic, agility courses, quest flow (translate carefully) |
| 7 | **Tarnish** | ~218 | Compiled only — reference for event names, not code |

**Never blindly copy Kronos data** — it's Rev 184. Always validate against Rev 233 using MCP tools.

---

## 1. Kronos (Rev 184 Java RSPS)

**Root:** `Kronos-184-Fixed/Kronos-master/`

### What Kronos has that we want

#### A. NPC Combat Stats (900+ JSON files)
```
kronos-server/data/npcs/combat/<NpcName>.json
```

Example — `Chicken.json`:
```json
{
  "ids": [1173, 1174, 2804],
  "hitpoints": 3,
  "aggressive_level": 2,
  "max_damage": 1,
  "attack_style": "STAB",
  "slayer_xp": 3.0,
  "slayer_tasks": ["Birds"],
  "attack": 1, "strength": 1, "defence": 1, "ranged": 1, "magic": 1,
  "attack_ticks": 4,
  "death_ticks": 5,
  "respawn_ticks": 50,
  "attack_animation": 5387,
  "defend_animation": 5388,
  "death_animation": 5389,
  "poison_immunity": false,
  "venom_immunity": false
}
```

**How to use in RSMod v2:**
```kotlin
// In NpcEditor init block:
edit(my_npcs.chicken) {
    hitpoints   = 3
    attack      = 1
    strength    = 1
    defence     = 1
    ranged      = 1
    magic       = 1
    attackRange = 1         // melee
    respawnRate = 50        // from respawn_ticks
    // attack_animation → verify sym via search_seqtypes first
}
```

> **Always cross-check** `hitpoints` and combat stats with `get_npc_rev233({ name: "X" })`.
> Animation IDs (5387, 5388, 5389) are Rev 184 raw IDs — use `search_seqtypes` to find the sym name.

#### B. NPC Drop Tables (706 JSON files)
```
kronos-server/data/npcs/drops/eco/<NpcName>.json
```

Example — `Chicken.json`:
```json
{
  "ids": [1173, 1174],
  "guaranteed": [
    { "id": 526, "min": 1, "max": 1 },   // Bones
    { "id": 2138, "min": 1, "max": 1 }   // Raw chicken
  ],
  "tables": [
    {
      "name": "Other",
      "weight": 1,
      "items": [
        { "id": 314, "min": 5, "max": 15, "weight": 1 }  // Feather
      ]
    }
  ]
}
```

**How to use in RSMod v2** (translate to `dropTable { }` DSL):
```kotlin
val chickenTable = dropTable {
    always(objs.bones)
    always(objs.raw_chicken)
    table("Other", weight = 1) {
        item(objs.feather, quantity = 5..15, weight = 1)
    }
}
registry.register(my_npcs.chicken, chickenTable)
```

> Item IDs in Kronos JSON are **raw cache IDs**, not sym names.
> Use `search_objtypes({ query: "feather" })` to get the sym name, then use `objs.feather`.

#### C. Skill Implementations (Java source — logic reference only)

All skill source files:
```
kronos-server/src/main/java/io/ruin/model/skills/
├── agility/courses/GnomeStrongholdCourse.java   ← obstacle-by-obstacle, XP per obstacle
├── agility/courses/rooftop/DraynorCourse.java   ← rooftop agility courses
├── cooking/                                      ← burn rates, cooking tables
├── crafting/                                     ← leather, gems, pottery recipes
├── farming/                                      ← patch states, growth timers
├── fishing/FishingArea.java                      ← spot locations + spot types
├── fishing/FishingCatch.java                     ← catch data with XP values
├── hunter/                                       ← trap mechanics
├── mining/                                       ← ore tables, respawn logic
├── runecrafting/                                 ← altar handling
├── slayer/                                       ← task assignment, Slayer master lists
├── smithing/                                     ← bar/item tables
├── woodcutting/                                  ← axe checks, tree respawn
└── thieving/                                     ← NPC pickpocket, stall, chest
```

**Agility course example — GnomeStrongholdCourse.java:**
```java
// Each obstacle: ObjectAction.register(objectId, "option", (p, obj) -> p.startEvent(e -> {
//   p.lock(...);
//   p.animate(animId);
//   p.getMovement().teleport(x, y, z);
//   e.delay(ticks);
//   p.getStats().addXp(StatType.Agility, xp, true);
//   p.unlock();
// }));
ObjectAction.register(23145, "walk-across", (p, obj) -> p.startEvent(e -> {
    p.lock(LockType.FULL_DELAY_DAMAGE);
    p.getAppearance().setCustomRenders(Renders.AGILITY_BALANCE);
    p.stepAbs(2474, 3429, StepType.FORCE_WALK);
    e.waitForMovement(p);
    p.getStats().addXp(StatType.Agility, 7.5, true);
    p.unlock();
}));
```

**RSMod v2 equivalent:**
```kotlin
onOpLoc1(locs.log_balance) {
    anim(seqs.human_log_balance)
    delay(2)
    statAdvance(stats.agility, 7.5)
    mes("You walk carefully across the log...")
}
```

#### D. Kronos Field Mapping to RSMod v2

| Kronos Java | RSMod v2 Kotlin |
|-------------|-----------------|
| `p.getStats().addXp(StatType.Agility, xp, true)` | `statAdvance(stats.agility, xp)` |
| `p.animate(animId)` | `anim(seqs.sym_name)` — **never use raw int IDs** |
| `e.delay(n)` | `delay(n)` |
| `p.sendFilteredMessage("text")` | `mes("text")` |
| `p.getInventory().add(itemId, qty)` | `invAdd(invs.inv, objs.sym, qty)` |
| `p.getInventory().remove(itemId, qty)` | `invDel(invs.inv, objs.sym, qty)` |
| `p.getSkills().getLevel(Skill.X)` | `woodcuttingLvl` (or `miningLvl`, etc.) |
| `p.getMovement().teleport(x, y, z)` | `teleport(CoordGrid(z, rx, rz, lx, lz))` |
| `ObjectAction.register(id, "option", handler)` | `onOpLoc1(locs.sym)` |
| `p.startEvent(e -> { ... })` | `suspend fun ProtectedAccess.handle() { ... }` |
| `p.lock(LockType.FULL_DELAY_DAMAGE)` | handled by RSMod v2 protect system automatically |
| `NpcAction.register(id, "talk-to", handler)` | `onOpNpc1(npcs.sym)` |

---

## 2. Alter (Rev 228 Kotlin RSPS)

**Root:** `Alter/`

### What Alter has

Alter is a Kotlin RSPS using the OpenRune framework — much closer to RSMod v2's style than Kronos.

```
Alter/game-plugins/src/main/kotlin/org/alter/plugins/content/
├── areas/lumbridge/npcs/          ← Lumbridge NPC dialogue (Hans, Cook, Bartender, etc.)
├── combat/formula/                 ← Melee/ranged/magic damage formulae
├── combat/specialattack/          ← Special attack handlers (dragon dagger, whip, etc.)
├── combat/strategy/               ← Combat AI strategies (melee, ranged, magic)
├── items/consumables/             ← Food/potion consume handlers
├── mechanics/aggro/               ← NPC aggression mechanics
├── mechanics/poison/              ← Poison/venom mechanics
├── mechanics/prayer/              ← Prayer drain + effects
├── skills/thieving/               ← Full thieving (pickpocket, stall, chest)
└── weapons/                       ← Weapon special attacks
```

### Alter plugin pattern and RSMod v2 equivalent

**Alter (v1) — KotlinPlugin:**
```kotlin
class HansPlugin(r: PluginRepository, world: World, server: Server) : KotlinPlugin(r, world, server) {
    val dialogOptions = listOf("Option A", "Option B", "Nothing.")

    init {
        spawnNpc("npc.hans", 3221, 3219, 0, 0, Direction.EAST)

        onNpcOption("npc.hans", option = "talk-to") {
            player.queue { dialog(player) }
        }
    }

    suspend fun QueueTask.dialog(player: Player) {
        chatNpc(player, "Hello. What are you doing here?")
        when (options(player, *dialogOptions.toTypedArray())) {
            1 -> chatPlayer(player, "Option A.")
            2 -> chatPlayer(player, "Option B.")
            3 -> chatPlayer(player, "Nothing.")
        }
    }
}
```

**RSMod v2 — PluginScript:**
```kotlin
class HansScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(npcs.hans) { talkToHans(it.npc) }
    }

    private suspend fun ProtectedAccess.talkToHans(npc: Npc) =
        startDialogue(npc) {
            chatNpc(neutral, "Hello. What are you doing here?")
            val choice = choice3(
                "Option A.", 1,
                "Option B.", 2,
                "Nothing.",  3,
            )
            when (choice) {
                1 -> chatPlayer(neutral, "Option A.")
                2 -> chatPlayer(neutral, "Option B.")
                3 -> chatPlayer(neutral, "Nothing.")
            }
        }
}
```

### Alter → RSMod v2 API Mapping

| Alter v1 | RSMod v2 |
|----------|----------|
| `class X(r, world, server) : KotlinPlugin(...)` | `class X @Inject constructor() : PluginScript()` |
| `init { onNpcOption("npc.x", option = "talk-to") { player.queue { ... } } }` | `onOpNpc1(npcs.x) { ... }` |
| `onNpcOption("npc.x", option = "attack")` | `onOpNpc2(npcs.x)` |
| `onObjOption("object.x", option = "search")` | `onOpLoc2(locs.x)` |
| `onItemOption("item.x", option = "eat")` | `onOpObj1(objs.x)` |
| `chatNpc(player, "text")` | `chatNpc(neutral, "text")` inside `startDialogue` block |
| `chatPlayer(player, "text")` | `chatPlayer(neutral, "text")` inside `startDialogue` block |
| `options(player, "A", "B", "C")` returns 1/2/3 | `choice3("A", 1, "B", 2, "C", 3)` returns value |
| `player.addXp(Skills.WC, xp)` | `statAdvance(stats.woodcutting, xp)` |
| `player.animate(Animation.X)` | `anim(seqs.sym_name)` |
| `player.message("text")` | `mes("text")` |
| `player.queue { task.wait(3) }` | `delay(3)` directly in suspend handler |
| `player.inventory.add(item, qty)` | `invAdd(invs.inv, objs.item, qty)` |
| `player.inventory.remove(item, qty)` | `invDel(invs.inv, objs.item, qty)` |
| `player.getSkillLevel(Skills.WC)` | `woodcuttingLvl` |
| `spawnNpc("npc.x", x, y, z, direction)` | `npcs.toml` spawn entry |
| `getRSCM("npc.hans")` hash lookup | `find("hans", hash)` in NpcReferences |

### Alter content worth porting

| Alter plugin | Port to RSMod v2 | Notes |
|---|---|---|
| `combat/formula/MeleeCombatFormula.kt` | RSMod combat engine | Already more complete in rsmod |
| `combat/specialattack/weapons/DragonDaggerPlugin.kt` | `content/mechanics/` | Good reference for spec logic |
| `mechanics/aggro/` | `content/mechanics/aggression/` (MECH-1) | Directly useful — aggro radius + timer |
| `mechanics/poison/` | Already ported | Done in `content/mechanics/poison/` |
| `items/consumables/` | Already ported | Food/potions working |
| `skills/thieving/pickpocket/` | Already ported | Thieving done |
| `areas/lumbridge/npcs/HansPlugin.kt` | Partially done | Hans dialogue exists, needs age feature |

---

## 3. wiki-data/ (Pre-scraped Rev 233)

**Root:** `wiki-data/`

Pre-scraped Rev 233 skill data. Use this before scraping fresh — it's already done.

```
wiki-data/
├── skills/
│   ├── woodcutting-complete.json    ← All trees, axes, XP rates, animation IDs
│   ├── fishing-complete.json        ← All spots, baits, catches, XP
│   ├── mining-complete.json         ← All ores, pickaxes, XP
│   ├── cooking-complete.json        ← All food, burn rates, XP
│   ├── smithing-complete.json       ← All bars, items, XP
│   ├── crafting-complete.json       ← All recipes, XP
│   ├── firemaking-complete.json     ← All logs, XP, burn times
│   ├── fletching-complete.json      ← All bow/arrow recipes, XP
│   ├── herblore-complete.json       ← All potion recipes, XP
│   ├── prayer-complete.json         ← All bones, altar XP
│   ├── runecrafting-complete.json   ← All altars, rune multipliers
│   ├── thieving-complete.json       ← All pickpocket/stall targets
│   └── agility.json                 ← Agility course data
└── monsters/
    ├── chicken.json, cow.json, goblin.json, ...  ← Monster data at Rev 233
```

**JSON structure (woodcutting-complete.json):**
```json
{
  "skill": "woodcutting",
  "source": "OSRS Rev 233 Cache Extraction",
  "axes": {
    "bronze_axe": { "item_id": 1351, "animation_id": 879, "level_req": 1 }
  },
  "trees": {
    "normal_tree": { "xp": 25.0, "level_req": 1, "log_id": 1511 }
  }
}
```

---

## 4. OSRSWikiScraper (Live Rev 233 Data)

**Root:** `OSRSWikiScraper/`

Use this to scrape fresh data for anything not already in `wiki-data/`.

```bash
# Scrape an NPC's drop table
python main.py -n "Lesser Demon"

# Scrape an item's stats
python main.py -e "Rune scimitar"

# Scrape all weapons
python main.py -aw

# Rev 233 validator
python rev233_validator.py
```

Output goes to `OSRSWikiScraper/rev233/` — JSON files per entity.

---

## 5. Tarnish (Rev ~218 Kotlin)

**Root:** `tarnish/`

Tarnish is a Kotlin RSPS at an intermediate revision. **Primarily useful as a compiled binary reference** — the source has event-based architecture (`FirstNpcOptionEvent`, `SecondNpcOptionEvent`) that doesn't map cleanly to RSMod v2.

**When to use:** Only if Alter is missing something and Kronos Java is too hard to read. Look in `tarnish/game-server/src/main/kotlin/` for event handler patterns.

**Do not copy Tarnish data** — revision is uncertain (218–228 range).

---

## Practical Workflows

### "I need to implement an agility course obstacle"

1. Find the obstacle in Kronos: `kronos-server/src/main/java/io/ruin/model/skills/agility/courses/<CourseName>.java`
2. Read the `ObjectAction.register(objectId, "option", ...)` block
3. Get the sym name for the loc: `search_loctypes({ query: "log balance" })`
4. Get the animation sym: `search_seqtypes({ query: "balance" })`
5. Get canonical XP: `osrs_wiki_parse_page({ page: "Gnome Stronghold Agility Course" })`
6. Write RSMod v2: `onOpLoc1(locs.sym) { anim(seqs.sym); delay(n); statAdvance(stats.agility, xp) }`

### "I need to implement an NPC with dialogue"

1. Check Alter first: `find "Z:/Projects/OSRS-PS-DEV/Alter" -name "*NpcName*"`
2. If found, read the Alter plugin and translate using the mapping table above
3. If not in Alter, look in `wiki-data/` or use `osrs_wiki_parse_page({ page: "NPC Name" })`
4. Get NPC sym: `search_npctypes({ query: "npc_name" })`
5. Write RSMod v2 PluginScript with `startDialogue` block

### "I need NPC combat stats"

1. **First:** `get_npc_rev233({ name: "Goblin" })` — most accurate
2. **Fallback:** `Kronos-184-Fixed/.../data/npcs/combat/Goblin.json`
3. Map to NpcEditor fields: `hitpoints`, `attack`, `strength`, `defence`, `ranged`, `magic`, `attackRange`, `respawnRate`
4. Get animation syms: `search_seqtypes({ query: "goblin attack" })`

### "I need an NPC's drop table"

1. **First:** `osrs_wiki_parse_page({ page: "Goblin" })` — scrapes live Rev 233 wiki
2. **Cross-check:** `Kronos-184-Fixed/.../data/npcs/drops/eco/Goblin.json`
3. Translate item IDs → sym names: `search_objtypes({ query: "item name" })`
4. Write `dropTable { }` DSL in `NpcDropTablesScript.kt`

### "I need skill XP table data"

1. **First:** `wiki-data/skills/<skill>-complete.json` — already scraped
2. **Or:** `osrs_wiki_parse_page({ page: "Woodcutting" })` for full tables
3. **Fallback:** `Kronos-184-Fixed/.../src/.../skills/<skill>/` for the Java enum with XP values

---

## 6. DTX (Advanced Drop Tables — Deferred)

**Root:** `DTX/`

DTX is a standalone Kotlin drop table library with more advanced rolling mechanics than RSMod's built-in system. It is **not integrated into RSMod v2** and is **explicitly deferred** until after F2P content is complete.

**Do NOT use DTX for current work.** Use RSMod's native `dropTable { }` DSL instead (documented in `rsmod-npc-combat-definer` skill and `NpcDropTablesScript.kt`).

### What DTX offers (for future reference)

| Table Type | DSL Syntax | Use case |
|------------|-----------|---------|
| `WeightedTable` | `N weight item` | Standard loot table (same as current RSMod system) |
| `ChainedTable` | `N rolls rollable` | Cascade rolls (pre-roll then main table) |
| `ExhaustiveTable` | Items exhaust after rolling | Guaranteed-once drops over multiple kills |
| `MatrixTable` | Row × column probability matrix | Complex 2D loot grids |
| `SequentialTable` | First unfulfilled slot wins | Ordered drop priority |
| `MultiChanceTable` | Multiple independent rolls per kill | Herb + seed + coin simultaneously |

### When to revisit

Per `docs/DTX_INTEGRATION.md`: revisit after all F2P NPC drop tables are implemented and a need for multi-roll or exhaustive tables emerges (e.g. PVM bosses with unique slot systems).

---

## Red Flags — When Kronos Data Is Wrong

Cross-check Kronos data against Rev 233 when you see any of these:

| Red flag | Why |
|----------|-----|
| `hitpoints > expected` | Kronos custom server may have buffed NPCs |
| `respawn_ticks` very low | Kronos had custom respawn rates |
| Item IDs that don't match `search_objtypes` | Rev 184 IDs may differ from Rev 233 |
| Drop table has rare items not on OSRS wiki | Kronos custom drops |
| NPC has attack type "DRAGON_FIRE" or custom types | Kronos-specific strings |
| `slayer_tasks` list contains non-OSRS task names | Kronos custom slayer |

**Rule of thumb:** If Kronos says it and the wiki doesn't mention it, skip it.

---

## File Count Reference

| Source | NPC combat files | Drop table files | Skill source files |
|--------|-----------------|------------------|-------------------|
| Kronos | 900+ JSON | 706 JSON | 18 skill dirs (Java) |
| Alter | None | None | 1 (thieving only) |
| wiki-data | — | 10+ monsters | 13 skill JSONs |
| MCP osrs-wiki-rev233 | All F2P NPCs | Via wiki | Via wiki |

