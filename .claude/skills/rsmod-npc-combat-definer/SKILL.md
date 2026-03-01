---
name: rsmod-npc-combat-definer
description: Define NPC combat stats and behaviors for RSMod v2. Use when porting monster data from Kronos, creating drop tables, setting up NPC combat parameters, or configuring monster AI and aggression.
---

# RSMod v2 NPC Combat Definer

## Task Coordination — Do This First

Multiple agents (Claude, OpenCode, Kimi, Codex) work this codebase simultaneously.
Before writing a single line of code, coordinate via the `agent-tasks` MCP server:

1. `list_tasks({ status: "pending" })` — find available work
2. `get_task("TASK-ID")` — read the full task description
3. `claim_task("TASK-ID", "your-agent-name")` — atomically claim it (fails if taken)
4. `check_conflicts(["path/file"])` — verify no one else is editing your files
5. `lock_file("path/file", "your-agent-name", "TASK-ID")` — lock every file before editing
6. Implement.
7. `complete_task("TASK-ID", "your-agent-name", "what I built")` — releases locks, marks done

If blocked: `block_task("TASK-ID", "your-agent-name", "exact reason")` so others know.
See `START_HERE.md` for the full project orientation.

Define NPC combat stats, drops, and behaviors. RSMod v2 stores combat data in cache params and drop tables in Kotlin plugins.

## Combat Stats Architecture

In RSMod v2, NPC combat stats are set via **NpcEditor** in Kotlin (not TOML params).
Define them in a `NpcEditor` subclass inside your module's configs file:

```kotlin
// In configs/MyModuleNpcs.kt (or the area configs file):
internal object MyNpcEditor : NpcEditor() {
    init {
        edit(my_npcs.goblin) {
            hitpoints   = 5       // max HP
            attack      = 1       // attack level
            strength    = 1       // strength level
            defence     = 1       // defence level
            ranged      = 1       // ranged level (1 if melee-only)
            magic       = 1       // magic level (1 if melee-only)
            attackRange = 1       // 1 = melee, >1 = ranged/magic
            respawnRate = 50      // ticks before NPC respawns
            giveChase   = true    // pursues fleeing player
        }
    }
}
```

**Available NpcEditor combat fields** (from `NpcPluginBuilder`):
`hitpoints`, `attack`, `strength`, `defence`, `ranged`, `magic`, `attackRange`, `respawnRate`, `giveChase`, `huntRange`, `huntMode`, `regenRate`

See `rsmod/content/areas/city/lumbridge/configs/LumbridgeNpcs.kt` for a full NpcEditor example with movement/shop fields.

## Porting from Kronos

Kronos stores NPC combat data in JSON files:

```json
// Kronos: data/npcs/combat/Goblin.json
{
  "name": "Goblin",
  "hitpoints": 8,
  "attack": 1,
  "strength": 1,
  "defence": 1,
  "attackSpeed": 6,
  "attackAnimation": 6184,
  "defenceAnimation": 6183,
  "deathAnimation": 6182,
  "attackSound": 3578,
  "slayer": {
    "level": 1,
    "experience": 8.0
  },
  "bonuses": {
    "attack": {"stab": 0, "slash": 0, "crush": 0, "magic": 0, "ranged": 0},
    "defence": {"stab": -21, "slash": -21, "crush": -21, "magic": -21, "ranged": -21}
  }
}
```

### Conversion Mapping

| Kronos JSON | RSMod v2 Param |
|-------------|----------------|
| `hitpoints` | `params.hitpoints` |
| `attack` | `params.attack` |
| `strength` | `params.strength` |
| `defence` | `params.defence` |
| `ranged` | `params.ranged` |
| `magic` | `params.magic` |
| `attackSpeed` | `params.attack_speed` |
| `attackAnimation` | `params.attack_anim` |
| `defenceAnimation` | `params.defend_anim` |
| `deathAnimation` | `params.death_anim` |
| `attackSound` | `params.attack_sound` |
| `bonuses.attack.*` | `params.*_bonus` |
| `bonuses.defence.*` | `params.*_defence` |

## Creating Drop Tables

Drop tables are registered in `content/other/npc-drops/NpcDropTablesScript.kt`.
Inject `NpcDropTableRegistry` and use the `dropTable { }` DSL:

```kotlin
class NpcDropTablesScript @Inject constructor(
    private val registry: NpcDropTableRegistry
) : PluginScript() {
    override fun ScriptContext.startup() {
        registerGoblin()
        registerCow()
    }

    private fun registerGoblin() {
        val goblinTable = dropTable {
            always(objs.bones)                          // 100% drop

            table("Armour/Weapons", weight = 1) {       // one of N equal tables
                item(objs.bronze_sq_shield, weight = 9)
                item(objs.bronze_scimitar, weight = 3)
                nothing(weight = 10)                    // nothing drop slot
            }

            table("Coins", weight = 1) {
                item(objs.coins, quantity = 1..4, weight = 5)
                nothing(weight = 5)
            }
        }
        // Register for all goblin variants
        registry.register(listOf(DropTableNpcs.goblin, DropTableNpcs.goblin_2), goblinTable)
    }

    private fun registerCow() {
        val cowTable = dropTable {
            always(objs.bones)
            always(objs.raw_beef)
            table("Hide", weight = 1) {
                item(objs.cowhide, weight = 1)
            }
        }
        registry.register(listOf(DropTableNpcs.cow, DropTableNpcs.cow2), cowTable)
    }
}
```

### Drop Table DSL — Correct Functions

| Function | Description |
|----------|-------------|
| `always(obj, qty = 1)` | 100% guaranteed drop |
| `table("Name", weight = N) { ... }` | Weighted sub-table (one rolled per kill) |
| `item(obj, quantity = 1..1, weight = N)` | Weighted item in a table |
| `nothing(weight = N)` | Weighted empty slot (no drop) |

**NPC reference object:** Add NPCs to a local `NpcReferences` object (e.g. `DropTableNpcs`) — do NOT use `npcs.*` from BaseNpcs for this module unless they're already there. See `DropTableNpcs.kt` in the same module.

See `content/other/npc-drops/NpcDropTablesScript.kt` for the full working reference (Man/Woman, Goblin, Cow, Chicken, etc.).

## NPC Combat Behavior

### Basic Retaliation

Add to NPC plugin or generic NPC handler:

```kotlin
// In startup()
onNpcHit(npcs.goblin) {
    npc.queueCombatRetaliate(attacker)
}
```

### Aggression Radius

⚠️ **Engine feature** - Check if `BaseHuntModes` supports this:

```kotlin
// If engine supports hunt modes:
onNpcSpawn(npcs.goblin) {
    npc.huntMode = huntModes.aggressive_4_tile
}
```

If not supported, flag for engine fix - do not hack around.

## NPC Type Registration

Add NPC types to `BaseNpcs.kt`:

```kotlin
// For specific NPC variants
val goblin_unarmed = find("goblin_unarmed", <hash>)
val goblin_armed = find("goblin_armed", <hash>)

// For content group matching (multiple NPCs)
val goblin = find("goblin_content_group")
```

Get hashes from `npc.sym` in cache symbols.

## Full NPC Definition Workflow

**NEW: Use the NPC Data Tools First**
```bash
# 0. Auto-generate complete NPC data (RECOMMENDED)
python tools/npc_lookup.py "Hill Giant"
# Shows: rev 233 symbol, combat stats from wiki, drops from wiki-data, 
#        animations from Kronos, all auto-mapped

# Generate Kotlin drop table skeleton
python tools/npc_lookup.py "Hill Giant" --output kotlin

# Batch process multiple NPCs
python tools/batch_npc_processor.py --tier 1
```

**Data source priority (highest accuracy first):**
1. **`tools/npc_lookup.py`** — Unified tool combining all sources (USE THIS FIRST)
2. `get_npc_rev233` MCP tool — Rev 233 wiki scrape (our exact target revision)
3. `osrs_wiki_parse_page` — Full wiki page for drop table detail
4. Kronos JSON — Fallback only (rev 184, may differ from rev 233)

1. **Get unified NPC data (PRIMARY — Python tool):**
   ```bash
   python tools/npc_lookup.py "Goblin"
   # Returns: rev 233 symbol, combat stats, drops with mapped item symbols,
   #          animations, variants — all sources combined
   ```
   
   Or use MCP directly:
   ```javascript
   get_npc_rev233({ name: "Goblin" })
   // Returns: hitpoints, attack, strength, defence, magic, ranged,
   //          attack_speed, combat_level, slayer_xp, drops, attack_styles
   ```

2. **Fallback: Read Kronos JSON (rev 184 — verify against wiki):**
   ```json
   {
     "name": "Goblin",
     "hitpoints": 8,
     "attack": 1,
     "strength": 1,
     "defence": 1,
     "attackSpeed": 6,
     "attackAnimation": 6184,
     "defenceAnimation": 6183,
     "deathAnimation": 6182,
     "slayer": {"level": 1, "experience": 8.0}
   }
   ```

3. **Find animation seq IDs:**
   ```javascript
   search_seqtypes({ query: "goblin attack", pageSize: 10 })
   // → id: 6184, name: "goblin_attack"
   search_seqtypes({ query: "goblin death", pageSize: 10 })
   // → id: 6182, name: "goblin_death"
   ```
   Cross-check in `.data/symbols/seq.sym` for the exact sym name to use with `seqs.*`.

4. **Add to BaseNpcs.kt:**
   ```kotlin
   val goblin = find("goblin", <hash>)
   ```

5. **Add to BaseSeqs.kt (if missing):**
   ```kotlin
   val goblin_attack = find("goblin_attack", <hash>)
   val goblin_hit = find("goblin_hit", <hash>)
   val goblin_death = find("goblin_death", <hash>)
   ```

6. **Add combat stats via NpcEditor** (in your module's configs file):
   ```kotlin
   internal object MyNpcEditor : NpcEditor() {
       init {
           edit(my_npcs.goblin) {
               hitpoints   = 8
               attack      = 1
               strength    = 1
               defence     = 1
               ranged      = 1
               magic       = 1
               attackRange = 1
               respawnRate = 100
           }
       }
   }
   ```

7. **Create drop table** in `NpcDropTablesScript.kt`:
   ```kotlin
   private fun registerGoblin() {
       val table = dropTable {
           always(objs.bones)
           table("Loot", weight = 1) {
               nothing(weight = 10)
               item(objs.bronze_sword, weight = 2)
               item(objs.coins, quantity = 1..4, weight = 5)
           }
       }
       registry.register(listOf(DropTableNpcs.goblin, DropTableNpcs.goblin_2), table)
   }
   ```
   
   **⚠️ CRITICAL: If using separate table files (e.g., `tables/GoblinDropTables.kt`):**
   ```kotlin
   // In tables/GoblinDropTables.kt
   object GoblinDropTables {
       fun registerAll(registry: NpcDropTableRegistry) {
           val table = dropTable { /* ... */ }
           registry.register(listOf(DropTableNpcs.goblin), table)
       }
   }
   
   // In NpcDropTablesScript.kt - YOU MUST CALL registerAll()!
   override fun ScriptContext.startup() {
       GoblinDropTables.registerAll(registry)  // <-- DON'T FORGET THIS!
   }
   ```

8. **Add on-hit handler** (only needed for custom behavior — basic retaliation is automatic):
   ```kotlin
   // In F2PMonsterCombatScript or similar:
   onNpcHit(DropTableNpcs.goblin) { /* retaliation handled by engine */ }
   ```

## Drop Rate Reference

Standard OSRS drop rates (out of 128):

| Rate | Probability | Rarity |
|------|-------------|--------|
| 128 | 100% | Guaranteed |
| 64 | 50% | Common |
| 32 | 25% | Uncommon |
| 16 | 12.5% | Uncommon |
| 8 | 6.25% | Rare |
| 4 | 3.125% | Rare |
| 2 | 1.56% | Very rare |
| 1 | 0.78% | Very rare |

For rates < 1/128, use:
```kotlin
// 1/256 drop
drop(objs.dragon_med_helm, 1, rate = 1, denominator = 256)

// 1/5000 drop
drop(objs.dragon_legs, 1, rate = 1, denominator = 5000)
```

## Common Drop Tables

### F2P Low-Level (Goblins, Cows, Chickens)
```kotlin
guaranteed(objs.bones)
drop(objs.coins, qtyRange(1, 5), rate = 20)
drop(objs.iron_dagger, 1, rate = 1)
```

### F2P Mid-Level (Guards, Giants)
```kotlin
guaranteed(objs.bones)
drop(objs.coins, qtyRange(5, 20), rate = 25)
drop(objs.iron_ore, 1, rate = 3)
drop(objs.water_rune, qtyRange(5, 10), rate = 5)
```

### Members (Slayer Monsters)
```kotlin
guaranteed(objs.bones)
// Unique drops
drop(objs.abyssal_whip, 1, rate = 1, denominator = 512)
// Herbs
drop(objs.grimy_ranarr_weed, 1, rate = 8)
// Seeds
drop(objs.snapdragon_seed, 1, rate = 3)
```

## Loot Ownership

RSMod v2 has hero points system for loot ownership:

```kotlin
// Drop with loot ownership (for multi-combat)
onNpcDeath(npcs.boss) { event ->
    val killer = event.getTopDamager()
    npc.dropTable.roll(killer)
}
```

## Testing Drops

Use the drop tester agent:

```bash
# Run drop simulation
python agent-runner/run.py --test drops --npc goblin --samples 1000
```

Or verify in-game:
1. Spawn NPC: `::npc goblin`
2. Kill 100 times
3. Check drop distribution matches rates

## NPC Spawns

NPC spawns are data-driven TOML files, not in plugins:

```toml
# In resources/.../npcs.toml
[[spawn]]
npc = 'goblin'
coords = '0_51_50_12_20'   # level_regionX_regionZ_localX_localZ

[[spawn]]
npc = 'goblin_2'
coords = '0_51_50_18_25'
```

The TOML file is loaded via a `MapNpcSpawnBuilder` class in your module's `map/` directory. See `LumbridgeNpcSpawns.kt` for the pattern.

## See Also

- `content/other/npc-drops/NpcDropTablesScript.kt` for examples
- `docs/WORK_PLAN.md` Phase 6 for monster priorities
- Kronos JSON files for reference stats
