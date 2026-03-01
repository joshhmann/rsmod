# RuneServer Best Practices

_Populated by Kimi from RuneServer research. Last updated: February 21, 2026_

This document captures OSRS private server conventions sourced from RuneServer
community knowledge. All agents should read this before implementing game mechanics.

---

## Table of Contents

1. [Tick Timing Reference](#tick-timing-reference)
2. [Combat Formulas](#combat-formulas)
3. [NPC Spawn Table Conventions](#npc-spawn-table-conventions)
4. [Drop Table Weight Calculations](#drop-table-weight-calculations)
5. [Animation Timing Standards](#animation-timing-standards)
6. [Experience Calculation Accuracy](#experience-calculation-accuracy)
7. [References](#references)

---

## Tick Timing Reference

### Core Tick Duration

**OSRS runs on a 600ms tick cycle** (0.6 seconds per tick). This is confirmed by Andrew Gower (Jagex founder) who stated:

> "The game 'ticks' at 600 millisecond intervals... It's not possible for an error to cause it to be something other than a multiple of 600 milliseconds slower."

All server-side actions are synchronized to this tick. Client-side actions (right-click menus, interface tab switching) are processed separately at 20ms.

### Skill Action Timings (in ticks)

| Action | Ticks | Seconds | Notes |
|--------|-------|---------|-------|
| **Woodcutting** | 4 | 2.4s | Chance to cut per 4 ticks |
| **Mining** (rune pick) | 3 | 1.8s | Varies with pickaxe type |
| **Fishing** | 5 | 3.0s | Chance to catch per 5 ticks |
| **Cooking** | 4 | 2.4s | Using cook-all option |
| **Firemaking** | 4 | 2.4s | Per log burned |
| **Smithing** (smelt) | 4 | 2.4s | Per ore at furnace |
| **Smithing** (forge) | 4 | 2.4s | Per item at anvil |
| **Crafting** (gems) | 1 | 0.6s | Per gem cut |
| **Crafting** (jewelry) | 3 | 1.8s | Per item |
| **Fletching** (cut bow) | 3 | 1.8s | Per unstrung bow |
| **Fletching** (string) | 2 | 1.2s | Per bow strung |
| **Herblore** (unfinished) | 1 | 0.6s | Per herb-vial |
| **Herblore** (complete) | 2 | 1.2s | Per finished potion |
| **Thieving** | 1 | 0.6s | Per pickpocket |
| **Prayer** (bury bone) | 1 | 0.6s | Per bone |
| **Prayer** (altar offer) | 3 | 1.8s | Per bone on POH altar |

### Combat Attack Speeds (in ticks)

| Weapon | Accurate/Long Range | Rapid | Notes |
|--------|---------------------|-------|-------|
| **Abyssal whip** | 4 | - | Cannot use rapid |
| **Scimitar/Dagger/Rapier** | 4 | - | - |
| **Longsword** | 5 | - | - |
| **Godsword/Battleaxe** | 6 | - | - |
| **2H Sword/Dharok's** | 8 | - | - |
| **Dart/Throwing knife** | 3 | 2 | - |
| **Shortbow** | 4 | 3 | - |
| **Longbow/God bow** | 6 | 5 | - |
| **Crossbow** | 6 | 5 | - |
| **Dark bow** | 9 | 8 | - |

### Implementation Notes

```kotlin
// RSMod v2 uses ticks as the time unit
delay(4)  // Delays 4 ticks (2.4 seconds)

// For accurate timing, always use tick-based delays
suspend fun ProtectedAccess.chopTree() {
    anim(seqs.woodcutting_axe)
    delay(4)  // Wait 4 ticks before next action
}
```

### Tick Drift Considerations

From Rune-Server thread "How to fix tick drift in your private server":
- Ticks should be processed at exactly 600ms intervals
- Server lag can cause drift - use a scheduled executor with fixed delays
- Never use `Thread.sleep()` for tick timing - use a proper game loop

---

## Combat Formulas

### Two-Roll Combat System

OSRS uses a **two-stage roll system** for combat:

1. **Accuracy Roll**: Determines if attack hits (Stage 1)
2. **Damage Roll**: Determines damage dealt 0 to max hit (Stage 2)

> "For every attack, there are two stages. Stage 1 (accuracy roll): Roll the attacker's offensive bonuses against the target's defensive bonuses. If true, proceed to stage 2. Stage 2 (damage roll): Roll a hit ranging from 0 to the attacker's maximum hit."

### Max Hit Formula (Melee)

**Step 1: Calculate Effective Strength Level**
```
effective_strength = floor((visible_strength_level * prayer_bonus) + style_bonus + 8)

Where:
- visible_strength_level = current level (including potions)
- prayer_bonus = multiplier from prayers (e.g., Piety = 1.23)
- style_bonus = +3 for accurate, +1 for controlled
```

**Step 2: Calculate Max Hit**
```
max_hit = floor(0.5 + (effective_strength * (equipment_strength_bonus + 64) / 640))
```

**Step 3: Apply Special Attack Modifiers**
```
// Examples from Rune-Server:
Dragon dagger: multiply by 1.15
Dragon longsword: multiply by 1.25
Dragon mace: multiply by 1.50
Abyssal whip: multiply by 0.85 (spec)
```

### Accuracy Formula (Melee)

**Step 1: Calculate Effective Attack Level**
```
effective_attack = floor((visible_attack_level * prayer_bonus) + style_bonus + 8)
```

**Step 2: Calculate Attack and Defence Rolls**
```
attack_roll = floor(effective_attack * (equipment_attack_bonus + 64) / 64)

defence_roll = floor(effective_defence * (equipment_defence_bonus + 64) / 64)
```

**Step 3: Calculate Hit Chance**
```kotlin
// If attack roll > defence roll
hit_chance = 1 - ((defence_roll + 1) / (2 * attack_roll))

// If attack roll <= defence roll  
hit_chance = attack_roll / (2 * (defence_roll + 1))
```

### Magic Defence Formula

Magic defence uses a unique calculation:
```
effective_magic = floor(magic_level * 0.7 + defence_level * 0.3)
defence_roll = floor(effective_magic * (magic_defence_bonus + 64) / 64)
```

### Special Attack Accuracy Modifiers

| Item | Modifier | Notes |
|------|----------|-------|
| Abyssal whip | 1.10 | Special attack only |
| Dragon dagger | 1.10 | Special attack |
| Magic longbow | 100% | Always hits |
| Salve amulet (e) | 1.20 | Vs undead only |
| Black mask (i) | 7/6 | Slayer task only |
| Void melee | 1.10 | Full set required |
| Void range | 1.10 | Full set required |
| Void mage | 1.30 | Full set required |

### Attack Style Bonuses

| Style | Attack | Strength | Defence |
|-------|--------|----------|---------|
| Accurate | +3 | +0 | +0 |
| Aggressive | +0 | +3 | +0 |
| Defensive | +0 | +0 | +3 |
| Controlled | +1 | +1 | +1 |

### Prayer Bonuses

| Prayer | Attack | Strength | Defence | Ranged | Magic |
|--------|--------|----------|---------|--------|-------|
| Burst of Strength | - | 1.05 | - | - | - |
| Superhuman Strength | - | 1.10 | - | - | - |
| Ultimate Strength | - | 1.15 | - | - | - |
| Clarity of Thought | 1.05 | - | - | - | - |
| Improved Reflexes | 1.10 | - | - | - | - |
| Incredible Reflexes | 1.15 | - | - | - | - |
| Piety | 1.20 | 1.23 | 1.25 | - | - |
| Rigour | - | - | - | 1.20 | - |
| Augury | - | - | 1.25 | - | 1.25 |

---

## NPC Spawn Table Conventions

### Spawn Data Format

From Rune-Server conventions, NPC spawn data typically includes:

```json
{
  "npc_id": 118,
  "x": 3228,
  "z": 3254,
  "plane": 0,
  "respawn_delay": 25,
  "walk_radius": 5,
  "facing": "SOUTH"
}
```

### Respawn Timer Standards

| NPC Type | Respawn (ticks) | Respawn (seconds) | Notes |
|----------|-----------------|-------------------|-------|
| **Cows/Chickens** | 25 | 15s | Low-level NPCs |
| **Goblins** | 25-50 | 15-30s | Standard NPCs |
| **Guards** | 50 | 30s | Medium NPCs |
| **Hill Giants** | 60 | 36s | Higher-level NPCs |
| **Boss NPCs** | 100-300 | 60-180s | Special NPCs |

### Spawn Table Conventions

**From Rune-Server thread "NPC respawn timer":**

1. **Respawn delays are measured in ticks** (not seconds directly)
2. Respawn timer starts when NPC dies
3. Timer must account for death animation duration
4. Multi-combat zones allow multiple players to attack same NPC

### Implementation Pattern

```kotlin
// RSMod v2 spawn configuration example
// File: content/generic/generic-npcs/spawns/

spawn(npc = npcs.cow_1234, x = 3228, z = 3254, plane = 0) {
    respawnDelay = 25  // ticks
    wanderRadius = 5
}
```

### Aggression Mechanics

From Rune-Server:
- NPCs have an "aggression range" (typically 3-8 tiles)
- Aggression timer: NPCs remain aggressive for 10 minutes after spawning
- NPCs de-aggro if player is 2x combat level higher
- Multi-combat areas allow multiple NPCs to attack simultaneously

---

## Drop Table Weight Calculations

### Weighted Drop System

OSRS uses a **weighted probability system** for drops, not simple percentages.

**From Rune-Server thread "Weighted Drop Tables" by Greyfield:**

> "They use a weight based system, but people don't know what those variables correspond to versus an items weight; they just guess."

### Drop Table Structure

```json
{
  "npcIdentifiers": [8133],
  "rollRange": 256,
  "rareDropTableAccess": true,
  "availableTables": [
    {
      "lootTableType": "UNIQUE",
      "accessRoll": 1,
      "tableRollRange": 8,
      "broadcastable": true,
      "loot": [
        {"itemIdentifier": 13750, "weight": 1},
        {"itemIdentifier": 13748, "weight": 1},
        {"itemIdentifier": 13746, "weight": 3}
      ]
    },
    {
      "lootTableType": "DYNAMIC",
      "loot": [
        {"itemIdentifier": 13754, "weight": 2},
        {"itemIdentifier": 13734, "weight": 6}
      ]
    },
    {
      "lootTableType": "STATIC",
      "loot": [
        {"itemIdentifier": 592, "weight": 1}  // Bones
      ]
    }
  ]
}
```

### Drop Table Types

| Type | Behavior | Example |
|------|----------|---------|
| **STATIC** | Always drops | Bones, ashes |
| **DYNAMIC** | One item always drops from table | Regular loot |
| **UNIQUE** | Chance to access, then roll for specific | Boss uniques |
| **PET** | Separate roll for pet | Boss pets |

### Weight Calculation Algorithm

```kotlin
// From Rune-Server implementation by Greyfield

fun generateDrop(npc: NPC, player: Player): List<Item> {
    val drops = DROP_REPOSITORY[npc.id]
    val roll = random(drops.rollRange)
    val items = mutableListOf<Item>()
    
    for (table in drops.availableTables) {
        when (table.lootTableType) {
            STATIC -> {
                // Always add all items
                table.loot.forEach { items.add(Item(it.itemId, 1)) }
            }
            DYNAMIC -> {
                if (items.isNotEmpty()) break
                // Roll once for this table
                val drop = rollWeightedDrop(table.loot, roll)
                items.add(drop)
            }
            UNIQUE -> {
                // Check if we hit the unique table
                if (roll < table.accessRoll) {
                    val secondaryRoll = random(table.tableRollRange)
                    val drop = rollWeightedDrop(table.loot, secondaryRoll)
                    items.add(drop)
                }
            }
        }
    }
    
    return items
}

fun rollWeightedDrop(loot: List<Loot>, roll: Int): Item {
    var sum = 0
    for (item in loot) {
        sum += item.weight
        if (roll < sum) {
            return Item(item.itemId, random(item.minAmount, item.maxAmount))
        }
    }
    return Item(loot.last().itemId, 1) // Fallback
}
```

### Weight to Drop Rate Conversion

**From Greyfield's explanation:**

> "If you look at Corporeal Beast and every drop. The OSRS total bucket weight is 585, but if you were to go by what people have estimated labels to weight to be which is 1:8(common), 1:32(uncommon), 1:64(rare)..."

| Rarity Label | Approximate Weight | Drop Chance |
|--------------|-------------------|-------------|
| Always | Guaranteed | 100% |
| Common | 1/8 | ~12.5% |
| Uncommon | 1/32 | ~3.1% |
| Rare | 1/64 | ~1.6% |
| Very Rare | 1/128 | ~0.8% |
| Extremely Rare | 1/512+ | ~0.2% |

### Rare Drop Table (RDT) Access

```kotlin
// From Rune-Server conventions
if (drops.hasRareDropTableAccess) {
    when {
        npc.combatLevel > 100 -> {
            if (random(32) == 0) {
                RareDropTable.createGemDrop(npc)
            }
        }
        npc.combatLevel > 40 -> {
            if (random(64) == 0) {
                RareDropTable.createDrop(npc)
            }
        }
    }
}
```

---

## Animation Timing Standards

### Animation Duration Guidelines

Animations should align with tick timing:

| Animation Type | Duration (ticks) | Notes |
|---------------|------------------|-------|
| Weapon attack | 4-8 | Matches weapon speed |
| Skill action | 1-5 | See skill timing table |
| Death | 3-4 | NPC death animation |
| Teleport | 4-5 | Including delay |
| Eat food | 1 | Instant with delay |
| Drink potion | 1 | Instant with delay |

### Animation Sequence Standards

**From Rune-Server:**

1. **Attack animations must sync with hit timing**
   - Melee: Damage applied tick 0 (same tick as animation starts)
   - Ranged: Damage applied after projectile travel
   - Magic: Damage applied after spell cast delay

2. **Skill animations**
   - Start animation → Wait ticks → Grant XP → Allow next action
   - Animation must finish before next action can begin

3. **Hit delays for ranged/magic**
```kotlin
// Ranged hit delay based on distance
hitDelay = max(1, distance / 2)

// Magic hit delay is typically fixed
hitDelay = 2  // for most combat spells
```

### RSMod v2 Implementation

```kotlin
// Proper animation sequencing
onOpLoc1(content.tree) {
    // Start animation
    anim(seqs.woodcutting_axe)
    
    // Wait for ticks (animation plays during this time)
    delay(4)
    
    // Grant XP and item
    statAdvance(stats.woodcutting, xp = 25.0)
    invAdd(invs.inv, objs.logs_1511)
}
```

---

## Experience Calculation Accuracy

### XP Formula

OSRS uses a specific formula for level-to-XP conversion:

```kotlin
fun levelToXp(level: Int): Int {
    var xp = 0
    for (i in 1 until level) {
        xp += floor(i + 300 * 2.0.pow(i / 7.0)).toInt()
    }
    return xp / 4
}

fun xpToLevel(xp: Int): Int {
    var level = 1
    while (level < 99 && levelToXp(level + 1) <= xp) {
        level++
    }
    return level
}
```

### XP Rate Guidelines

**From Rune-Server research:**

1. XP is always granted **at the moment of success**
2. For gathering skills: XP when resource obtained
3. For processing skills: XP when item completed
4. XP rates should match OSRS wiki for authenticity

### Experience Modifiers

| Source | Modifier | Stackable |
|--------|----------|-----------|
| Brawling gloves | 1.5x | No |
| Bonus XP weekend | 2.0x | Yes |
| RAF bonus | 1.10x | Yes |
| Wisdom aura | 2.5% | Yes |
| Clan avatar | 3-6% | Yes |

---

## References

### Primary Sources

| Source | URL | Content |
|--------|-----|---------|
| Rune-Server | https://rune-server.org/threads/runescape-clock-ticks.315560/ | Tick timing guide |
| Rune-Server | https://rune-server.org/threads/weighted-drop-tables.650219/ | Drop table system |
| Rune-Server | https://rune-server.org/threads/97-correct-accuracy-formula.348852/ | Accuracy formula |
| Rune-Server | https://rune-server.org/threads/runescape-combat-formulas-max-hit-accuracy-extra.658441/ | Combat formulas |
| OSRS Wiki | https://oldschool.runescape.wiki/w/Game_tick | Official tick info |
| OSRS Wiki | https://oldschool.runescape.wiki/w/Successful_hit | Combat mechanics |

### Credit

- **Tick timing research**: Xaves (Rune-Server), Andrew Gower (Jagex)
- **Drop table system**: Greyfield (Rune-Server)
- **Accuracy formula**: mgi125, Chaos Pooky, Mandulf, Obliv (Rune-Server)
- **Max hit formula**: Obliv (Rune-Server), updated by Tyluur

### Notes for RSMod v2 Implementation

1. **Always use 600ms ticks** - Do not deviate from this timing
2. **Use weighted drop tables** - Not simple percentages
3. **Combat uses two-roll system** - Accuracy then damage
4. **XP timing matters** - Grant at moment of success
5. **Animation sync** - Must align with tick processing

---

_Last verified against Rune-Server threads: February 21, 2026_

