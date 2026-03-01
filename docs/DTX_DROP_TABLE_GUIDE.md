# DTX Drop Table Framework Guide for Rev 233

**What is DTX?** A generic Kotlin drop table framework with powerful rolling mechanics.

**Can we use it?** YES! Just use Rev 233 item IDs instead of example items.

---

## Overview

### What DTX Provides

DTX is a generic drop table framework that gives you:

- **Weighted tables** - OSRS-style 1/128 drops
- **Guaranteed tables** - Always drop (bones)
- **Tertiary tables** - Independent rolls (pets, clues)
- **Chained tables** - Multiple sequential rolls
- **Matrix tables** - Complex conditional logic

### What You Need to Provide

- Item/NPC IDs from your Rev 233 cache
- Drop rates from OSRS Wiki
- Any custom logic

---

## Core Concepts

### 1. Rollable

Everything that can be rolled extends Rollable:

```kotlin
interface Rollable<T, R> {
    fun selectResult(target: T, otherArgs: ArgMap): RollResult<R>
}
```

- T = Target (usually Player)
- R = Result (usually Item)

### 2. Table Types

```kotlin
// Weighted - OSRS standard
typealias WeightedTable<T, R> = 

// Guaranteed - Always drops
typealias GuaranteedTable<T, R> = 

// Pre-roll - Roll before main
typealias PreRollTable<T, R> = 

// Tertiary - Independent rolls
typealias TertiaryTable<T, R> = 
```

### 3. Roll Results

```kotlin
sealed class RollResult<R> {
    data class Nothing<R> : RollResult<R>()
    data class Single<R>(val result: R) : RollResult<R>()
    data class ListOf<R>(val results: List<R>) : RollResult<R>()
}
```

---

## Table Types

### 1. Weighted Table (Main Drops)

OSRS Standard: 128 total weight

```kotlin
val goblinMainDrops = rsWeightedTable<Player, Item> {
    name("Goblin main drops")
    
    // Weapons/Armor (low weight = rare)
    3 weight Item("bronze_sq_shield")   // 3/128
    3 weight Item("bronze_spear")       // 3/128
    
    // Runes (medium weight)
    5 weight Item("body_rune", 7)       // 5/128
    6 weight Item("water_rune", 6)      // 6/128
    
    // Herbs (common)
    8 weight Item("grimy_guam_leaf")    // 8/128
    6 weight Item("grimy_marrentill")   // 6/128
    
    // Coins (most common)
    35 weight Item("coins", 5)          // 35/128
    
    // Nothing (common filler)
    16 weight nothing()                  // 16/128
}
```

### 2. Guaranteed Table (Always Drop)

```kotlin
val goblinGuaranteed = rsGuaranteedTable<Player, Item> {
    name("Goblin guaranteed")
    
    add(Item("bones"))                    // Always
}
```

### 3. Tertiary Table (Independent Rolls)

```kotlin
val goblinTertiary = rsTertiaryTable<Player, Item> {
    name("Goblin tertiaries")
    
    // Each rolls independently
    1 outOf 90 chance Item("clue_scroll_beginner")
    1 outOf 128 chance Item("clue_scroll_easy")
    1 outOf 5_000 chance Item("goblin_champion_scroll")
}
```

### 4. Complete Drop Table

```kotlin
val goblinDrops = RSDropTable(
    tableIdentifier = "Goblin",
    
    // 1. Always dropped
    guaranteed = goblinGuaranteed,
    
    // 2. Pre-rolled (clues, etc.)
    preRoll = goblinPreRoll,
    
    // 3. Main weighted table
    mainTable = goblinMainDrops,
    
    // 4. Independent tertiaries
    tertiaries = goblinTertiary
)
```

---

## Rev 233 Examples

### Example 1: Goblin

```kotlin
package org.rsmod.content.drops.tables

import org.rsmod.api.config.refs.objs
import org.rsmod.content.drops.*

// Define guaranteed drops
val goblinGuaranteed = rsGuaranteedTable<Player, Item> {
    add(objs.bones)
}

// Define main drops (from wiki: 128 weight system)
val goblinMain = rsWeightedTable<Player, Item> {
    // Weapons/Armor (3-4/128 each)
    3 weight objs.bronze_sq_shield
    4 weight objs.bronze_spear
    
    // Runes
    5 weight objs.body_rune(7)
    6 weight objs.water_rune(6)
    3 weight objs.earth_rune(4)
    2 weight objs.fire_rune(3)
    1 weight objs.mind_rune(2)
    1 weight objs.chaos_rune(1)
    
    // Herbs
    8 weight objs.grimy_guam_leaf
    6 weight objs.grimy_marrentill
    4 weight objs.grimy_tarromin
    3 weight objs.grimy_harralander
    2 weight objs.grimy_ranarr_weed
    1 weight objs.grimy_irit_leaf
    1 weight objs.grimy_avantoe
    
    // Coins (most common)
    35 weight objs.coins(5)
    10 weight objs.coins(10)
    6 weight objs.coins(15)
    4 weight objs.coins(25)
    1 weight objs.coins(35)
    
    // Other
    3 weight objs.bronze_bolts(8)
    1 weight objs.fishing_bait
    1 weight objs.earth_talisman
    
    // Nothing
    16 weight nothing()
}

// Define tertiaries
val goblinTertiary = rsTertiaryTable<Player, Item> {
    1 outOf 90 chance objs.clue_scroll_beginner
    1 outOf 128 chance objs.clue_scroll_easy
    1 outOf 5_000 chance objs.goblin_champion_scroll
}

// Complete table
val goblinDrops = RSDropTable(
    tableIdentifier = "Goblin",
    guaranteed = goblinGuaranteed,
    mainTable = goblinMain,
    tertiaries = goblinTertiary
)
```

### Example 2: Hill Giant

```kotlin
package org.rsmod.content.drops.tables

// Based on Obor/F2P bosses
val hillGiantDrops = RSDropTable(
    tableIdentifier = "Hill Giant",
    
    guaranteed = rsGuaranteedTable {
        add(objs.big_bones)
        add(objs.ensouled_giant_head)  // If on task
    },
    
    mainTable = rsWeightedTable {
        // Weapons/Armor
        6 weight objs.iron_full_helm
        5 weight objs.iron_kiteshield
        3 weight objs.steel_axe
        2 weight objs.steel_arrow(10)
        1 weight objs.mithril_dagger
        
        // Runes
        10 weight objs.air_rune(15)
        8 weight objs.earth_rune(5)
        5 weight objs.mind_rune(3)
        2 weight objs.law_rune(2)
        
        // Herbs
        12 weight objs.grimy_guam_leaf
        10 weight objs.grimy_marrentill
        8 weight objs.grimy_tarromin
        5 weight objs.grimy_harralander
        3 weight objs.grimy_ranarr_weed
        
        // Other
        15 weight objs.limpwurt_root
        8 weight objs.beer
        5 weight objs.body_talisman
        
        // Coins
        25 weight objs.coins(15)
        15 weight objs.coins(25)
        10 weight objs.coins(40)
        5 weight objs.coins(75)
        
        // Nothing
        12 weight nothing()
    },
    
    tertiaries = rsTertiaryTable {
        1 outOf 60 chance objs.clue_scroll_beginner
        1 outOf 128 chance objs.clue_scroll_easy
    }
)
```

---

## RSMod Integration

### Step 1: Register Tables

```kotlin
// RSDropTables.kt
package org.rsmod.content.drops

import org.rsmod.content.drops.tables.*
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class RSDropTables : PluginScript() {
    override fun ScriptContext.startup() {
        // Register all drop tables
        registerDropTable(npcs.goblin, goblinDrops)
        registerDropTable(npcs.hill_giant, hillGiantDrops)
        // ... etc
    }
}
```

### Step 2: Hook Into Combat

```kotlin
// Extension function to hook into combat
fun <T, R> registerDropTable(npcType: NpcType, table: RSDropTable<T, R>) {
    onNpcDeath(npcType) { npc, killer ->
        val drops = table.roll(killer)
        drops.forEach { drop ->
            spawnGroundItem(drop, npc.coords)
        }
    }
}
```

---

## Best Practices

### 1. Use Wiki Data

Always get drop rates from OSRS Wiki:
- Weight values (e.g., 3/128)
- Quantity ranges
- Tertiary rates

### 2. Verify Item IDs

Before using an item, verify it exists in Rev 233:

```kotlin
// Use MCP server to verify
search_objtypes({ query: "item_name" })
// If found then safe to use
// If not found then item added after Rev 233
```

### 3. Organize by Category

```
tables/
├── f2p/
│   ├── GoblinDrops.kt
│   ├── HillGiantDrops.kt
│   └── ...
├── members/
│   ├── SlayerDrops.kt
│   ├── BossDrops.kt
│   └── ...
└── shared/
    ├── HerbDrops.kt
    ├── SeedDrops.kt
    └── RDT.kt  // Rare drop table
```

### 4. Test Drop Rates

```kotlin
// Unit test for drop rates
@Test
fun goblinDropRatesMatchWiki() {
    val results = mutableMapOf<String, Int>()
    
    repeat(100_000) {
        val drop = goblinDrops.roll(testPlayer)
        drop.forEach { item ->
            results.merge(item.name, 1, Int::plus)
        }
    }
    
    // Verify rates
    assertApproximate(results["bones"], 100_000, 1.0)  // 100%
    assertApproximate(results["bronze_sq_shield"], 2_344, 0.05)
}
```

---

## Migration from Wiki Scraper

### Current: Basic Drop Tables

```kotlin
// From wiki scraper
register(npcs.goblin) {
    drop(objs.bones, 1, rate = 128)           // Always
    drop(objs.bronze_sq_shield, 1, rate = 3)  // 3/128
    drop(objs.coins, 5, rate = 35)            // 35/128
}
```

### New: DTX Drop Tables

```kotlin
// Using DTX
val goblinDrops = RSDropTable(
    guaranteed = rsGuaranteedTable {
        add(objs.bones)
    },
    mainTable = rsWeightedTable {
        3 weight objs.bronze_sq_shield
        35 weight objs.coins(5)
    },
    tertiaries = rsTertiaryTable {
        1 outOf 5_000 chance objs.goblin_champion_scroll
    }
)

// Register
registerDropTable(npcs.goblin, goblinDrops)
```

### Benefits

| Feature | Wiki Scraper | DTX |
|---------|-------------|-----|
| Weighted drops | Yes | Yes |
| Guaranteed drops | Yes | Yes |
| Tertiary rolls | No | Yes |
| Chained rolls | No | Yes |
| Transform hooks | No | Yes |
| Type safety | Partial | Yes |
| Testing | No | Yes |

---

## Summary

DTX plus Rev 233 equals Powerful Drop System

1. DTX is framework-agnostic
2. Use your Rev 233 cache IDs
3. Get rates from OSRS Wiki
4. Organize tables by NPC/boss
5. Test drop rates
6. Hook into RSMod combat

Next Steps:
1. Copy DTX to your project
2. Create first drop table (Goblin)
3. Test in-game
4. Expand to all NPCs

Ready to build the ultimate drop table system?

