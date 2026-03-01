# Master Implementation Roadmap

**Consolidated from:** Kronos-184, Alter, OpenRune, Tarnish, RS-SDK

**Date:** 2026-02-20

---

## Executive Summary

You have **5 major codebases** with thousands of implementations to reference:

| Repository | Revision | Language | Key Strengths |
|------------|----------|----------|---------------|
| **Kronos-184** | 184 | Java | Skills, combat formulas, drops, bosses |
| **Alter** | 223-228 (donor) | Kotlin | Thieving, combat DSL, loot tables |
| **OpenRune** | 236 | Kotlin | Quest system, skills, special attacks |
| **Tarnish** | 218 | Java | Complete minigames, 40+ bosses |
| **RS-SDK** | 2004-era | TypeScript | Protocol reference, bot framework |

**All of this code can be adapted for RSMod v2 Rev 233.**

---

## Phase 1: Foundation (Priority: CRITICAL)

### 1.1 Combat System Completion

#### NPC Combat Definitions
```kotlin
// TODO: Create DSL for NPC combat definitions (from Alter)
// Reference: Alter/game-api/dsl/NpcCombatDsl.kt

setCombatDef(npcs.goblin) {
    configs {
        attackSpeed = 4
        respawnDelay = 25
    }
    stats {
        hitpoints = 5
        attack = 1
        strength = 1
        defence = 1
    }
    anims {
        attack = anims.goblin_attack
        block = anims.goblin_block
        death = anims.goblin_death
    }
    drops {
        main(weight = 128) {
            add(objs.bronze_sq_shield, min = 1, max = 1, weight = 3)
            add(objs.coins, min = 5, max = 5, weight = 35)
        }
        always {
            add(objs.bones)
        }
    }
}
```

**TODOs:**
- [ ] Port Alter's `NpcCombatDsl.kt` to RSMod v2
- [ ] Port `LootTableDsl.kt` for drop tables
- [ ] Create combat stat loader from JSON (like Kronos)
- [ ] Hook into existing NPC system

#### Combat Formulas
```kotlin
// TODO: Port accurate OSRS combat formulas
// Reference: OpenRune/game-plugins/combat/formula/
// Reference: Kronos/model/combat/
// Reference: Tarnish/com.osroyale.game.world.entity.combat

class MeleeCombatFormula {
    // Accuracy roll
    fun calculateAccuracy(attackLevel: Int, attackBonus: Int, style: AttackStyle): Int
    
    // Defence roll  
    fun calculateDefence(defenceLevel: Int, defenceBonus: Int): Int
    
    // Max hit
    fun calculateMaxHit(strengthLevel: Int, strengthBonus: Int, style: AttackStyle): Int
    
    // Prayer multipliers
    fun applyPrayerBoost(prayer: Prayer, base: Int): Int
    
    // Equipment bonuses (Void, Slayer helm, etc.)
    fun applyEquipmentBonus(equipment: Equipment, base: Int): Int
}
```

**TODOs:**
- [ ] Port melee accuracy formula
- [ ] Port melee max hit formula
- [ ] Port ranged accuracy/max hit
- [ ] Port magic accuracy/max hit
- [ ] Add prayer multipliers
- [ ] Add equipment set bonuses

#### Special Attack System
```kotlin
// TODO: Port special attack framework
// Reference: Kronos/model/combat/special/
// Reference: OpenRune/game-plugins/combat/specialattack/

interface SpecialAttack {
    val weaponName: String
    val drainAmount: Int  // 25, 50, 55, 60, 100
    fun activate(player: Player, target: Entity): Boolean
}

class DragonClawsSpecial : SpecialAttack {
    override val weaponName = "dragon claws"
    override val drainAmount = 50
    
    override fun activate(player: Player, target: Entity): Boolean {
        // Complex 4-hit calculation from Kronos
        val hit1 = calculateFirstHit()
        val hit2 = calculateSecondHit(hit1)
        val hit3 = calculateThirdHit(hit2)
        val hit4 = calculateFourthHit()
        return true
    }
}
```

**TODOs:**
- [ ] Create special attack registry
- [ ] Port Dragon Claws special
- [ ] Port Armadyl Godsword special
- [ ] Port Bandos Godsword special
- [ ] Port Saradomin Godsword special
- [ ] Port Zamorak Godsword special
- [ ] Port all other specials from Kronos list (29 melee, 12 ranged)

---

### 1.2 Skill Implementations

#### Woodcutting (Enhancement)
```kotlin
// TODO: Enhance existing Woodcutting.kt with features from other repos
// Reference: Kronos/model/skills/woodcutting/
// Reference: OpenRune/content/skills/woodcutting/

// Add from Kronos:
// - Bird nest drops (1/200 chance)
// - Infernal axe auto-burn (1/3 chance)
// - Woodcutting guild invisible boost (+7)
// - Dragon axe special bonus

// Add success formula from Kronos:
// chance = ((level - levelReq) + 1 + hatchetPoints) / difficulty
```

**TODOs:**
- [ ] Add bird nest drop system
- [ ] Add infernal axe support
- [ ] Add woodcutting guild boost
- [ ] Verify success formula against Kronos

#### Mining (Enhancement)
```kotlin
// TODO: Enhance existing Mining.kt
// Reference: Kronos/model/skills/mining/
// Reference: OpenRune/content/skills/mining/

// Add from Kronos:
// - Mining gloves (33% depletion reduction)
// - Superior mining gloves
// - Geode drops (1/250)
// - Mining guild minerals (1/30 to 1/150)
// - Prospector outfit (2.5% XP bonus)
// - Infernal pickaxe auto-smelt

// Success formula:
// chance = ((level - levelReq) + 1 + pickaxePoints) / difficulty
```

**TODOs:**
- [ ] Add mining gloves effects
- [ ] Add geode drop system
- [ ] Add prospector outfit bonus
- [ ] Add infernal pickaxe support

#### Thieving (New Implementation)
```kotlin
// TODO: Implement Thieving skill from scratch
// Reference: Alter/game-plugins/skills/thieving/
// Reference: Kronos/model/skills/thieving/

// Three main components:
// 1. Pickpocketing
class PickpocketService {
    fun loadPickpocketsFromJson()
    fun attemptPickpocket(player: Player, npc: Npc)
}

// 2. Stall thieving
class StallThievingService {
    fun handleStallClick(player: Player, stall: Loc)
    fun startRespawnTimer(stall: Loc, respawnTicks: Int)
}

// 3. Chest thieving
class ChestThievingService {
    fun handleChestClick(player: Player, chest: Loc)
    fun checkForTrap(player: Player, chest: Loc)
    fun dismantleTrap(player: Player, chest: Loc)
}
```

**TODOs:**
- [ ] Create PickpocketPlugin.kt
- [ ] Create StallThievingPlugin.kt
- [ ] Create ChestThievingPlugin.kt
- [ ] Port JSON data from Alter
- [ ] Add all pickpocket NPCs
- [ ] Add all stalls
- [ ] Add all chests

#### Agility (New Implementation)
```kotlin
// TODO: Implement Agility skill
// Reference: Kronos/model/skills/agility/
// Reference: OpenRune/content/skills/agility/

// Course-based system:
class GnomeStrongholdCourse {
    val obstacles = listOf(
        Obstacle(loc = locs.log_balance, xp = 7.5, delay = 3),
        Obstacle(loc = locs.obstacle_net, xp = 7.5, delay = 3),
        // ... more obstacles
    )
    val lapBonus = 39.0
}

// Add all courses:
// - Gnome Stronghold
// - Al Kharid
// - Barbarian Outpost
// - Canifis
// - Falador
// - Seers' Village
// - Wilderness
// - Ardougne
// - Rellekka
// - Pollnivneach
```

**TODOs:**
- [ ] Create AgilityCourse base class
- [ ] Implement Gnome Stronghold course
- [ ] Implement Al Kharid course
- [ ] Implement Barbarian course
- [ ] Implement rooftop courses
- [ ] Add graceful outfit support

#### Herblore (Enhancement)
```kotlin
// TODO: Enhance existing Herblore.kt
// Reference: Kronos/model/skills/herblore/
// Reference: OpenRune/content/skills/herblore/

// Add from Kronos:
// - 50+ potion definitions
// - Decanting system (to 1/2/3/4 dose)
// - Upgrade potions (stamina, extended antifire)
// - Noted decanting support

// Potion enum pattern from Kronos:
enum class Potion(
    val level: Int,
    val xp: Double,
    val unfinished: Int,
    val ingredient: Int,
    val finished: Int
) {
    ATTACK_POTION(3, 25.0, UNF_GUAM, EYE_OF_NEWT, ATTACK_POTION_4),
    // ... 50+ more potions
}
```

**TODOs:**
- [ ] Add all potion definitions
- [ ] Add decanting system
- [ ] Add upgrade potion support
- [ ] Add cleaning herbs

#### Smithing (New Implementation)
```kotlin
// TODO: Implement Smithing skill
// Reference: Kronos/model/skills/smithing/
// Reference: OpenRune/content/skills/smithing/
// Reference: Tarnish/smithing/

// Smelting (furnace)
class SmeltingAction {
    fun smeltBar(player: Player, barType: BarType)
}

// Forging (anvil)
class SmithingAction {
    fun openSmithingInterface(player: Player)
    fun smithItem(player: Player, item: SmithableItem)
}

enum class BarType(val level: Int, val xp: Double, val ores: Map<Int, Int>) {
    BRONZE(1, 6.2, mapOf(objs.copper_ore to 1, objs.tin_ore to 1)),
    IRON(15, 12.5, mapOf(objs.iron_ore to 1)),
    STEEL(30, 17.5, mapOf(objs.iron_ore to 1, objs.coal to 2)),
    // ... more bars
}
```

**TODOs:**
- [ ] Create SmeltingPlugin.kt
- [ ] Create SmithingPlugin.kt
- [ ] Add all bar types
- [ ] Add all smithable items
- [ ] Add blast furnace support (optional)

#### Crafting (New Implementation)
```kotlin
// TODO: Implement Crafting skill
// Reference: Kronos/model/skills/crafting/
// Reference: Tarnish/crafting/

// Multiple components:
// - Gem cutting
// - Jewelry making (mould interface)
// - Leather working
// - Battlestaves
// - Glass blowing
// - Pottery
// - Silver items
```

**TODOs:**
- [ ] Create gem cutting
- [ ] Create jewelry interface
- [ ] Create leather working
- [ ] Create glass blowing
- [ ] Create pottery wheel

#### Slayer (New Implementation)
```kotlin
// TODO: Implement Slayer skill
// Reference: Kronos/model/skills/slayer/
// Reference: OpenRune/Slayer.kt

// Core components:
class SlayerSystem {
    fun getTask(player: Player, master: SlayerMaster): SlayerTask
    fun checkTaskCompletion(player: Player, npc: Npc)
    fun extendTask(player: Player)
    fun cancelTask(player: Player)
}

// Task data from Kronos data/slayer_tasks.json
// Masters: Turael, Mazchna, Vannaka, Chaeldar, Nieve, Duradel, Konar

// Features:
// - Point system (scales with streak)
// - Task extensions
// - Task blocking
// - Superior slayer creatures
// - Slayer helm/black mask bonuses
```

**TODOs:**
- [ ] Create SlayerTask data class
- [ ] Create task assignment system
- [ ] Create point system
- [ ] Add all slayer masters
- [ ] Add all slayer tasks
- [ ] Add superior spawn system

---

### 1.3 Drop Table System

```kotlin
// TODO: Complete drop table implementation
// Reference: Alter/LootTableDsl.kt
// Reference: Kronos/LootTable.java
// Reference: Tarnish/LootTable.java

// You already have GeneratedDropTables.kt - now hook it up:

class NpcDropTablesScript : PluginScript() {
    override fun ScriptContext.startup() {
        // Register all generated drop tables
        registerGeneratedDropTables()
        
        // Hook into NPC death
        onNpcDeath { npc, killer ->
            val drops = rollDrops(npc, killer)
            drops.forEach { drop ->
                spawnGroundItem(drop, npc.coords)
            }
        }
    }
}
```

**TODOs:**
- [ ] Hook GeneratedDropTables.kt into combat
- [ ] Add always drops (bones, etc.)
- [ ] Add main drop rolling
- [ ] Add tertiary drops (pets, clues)
- [ ] Add pre-roll drops (clue scrolls)
- [ ] Add player condition checks

---

## Phase 2: Content (Priority: HIGH)

### 2.1 Quests

```kotlin
// TODO: Implement quest system
// Reference: OpenRune/quest/

// Quest framework:
abstract class Quest(
    val name: String,
    val questPoints: Int,
    val requiredQuests: List<Quest> = emptyList()
) {
    abstract fun startQuest(player: Player)
    abstract fun completeQuest(player: Player)
    abstract fun getJournal(player: Player): QuestJournal
}

// Example implementation:
class CooksAssistant : Quest("Cook's Assistant", 1) {
    override fun startQuest(player: Player) {
        player.setQuestStage(this, 1)
    }
    
    override fun getJournal(player: Player) = questJournal {
        description("The Lumbridge Castle cook is in a mess.")
        
        objective("Bring items to the Cook") {
            hasItem(objs.pot_of_flour, "Pot of flour")
            hasItem(objs.egg, "Egg")
            hasItem(objs.bucket_of_milk, "Bucket of milk")
        }
        
        if (player.getQuestStage(this) == 100) {
            completed()
        }
    }
}
```

**TODOs:**
- [ ] Create Quest base class
- [ ] Create QuestJournal DSL
- [ ] Implement Cook's Assistant
- [ ] Implement Sheep Shearer
- [ ] Implement Romeo & Juliet
- [ ] Implement Demon Slayer
- [ ] Implement The Restless Ghost
- [ ] Implement Prince Ali Rescue
- [ ] Implement Dragon Slayer

### 2.2 Minigames

From **Tarnish** (complete implementations):

```kotlin
// TODO: Port minigames from Tarnish
// Reference: Tarnish/plugins/minigames/

// Priority order:
// 1. Barrows
// 2. Pest Control
// 3. Fight Caves
// 4. Warrior Guild
// 5. God Wars Dungeon
// 6. Duel Arena
// 7. Recipe for Disaster
// 8. Last Man Standing
// 9. Wintertodt
```

**TODOs:**
- [ ] Port Barrows (crypt system, brothers, rewards)
- [ ] Port Pest Control (void knight, portals, commendations)
- [ ] Port Fight Caves (waves, TzTok-Jad)
- [ ] Port Warrior Guild (cyclopes, defenders)

### 2.3 Bosses

From **Tarnish** (40+ boss strategies):

```kotlin
// TODO: Port boss implementations from Tarnish
// Reference: Tarnish/combat/strategy/npc/boss/

// Priority F2P bosses:
// - King Black Dragon
// - Kalphite Queen
// - Giant Mole

// Priority members bosses:
// - God Wars: Kree'Arra, Zilyana, Graardor, K'ril
// - Dagannoth Kings
// - Wilderness: Callisto, Venenatis, Vet'ion
// - Slayer: Cerberus, Abyssal Sire, Hydra
// - Others: Vorkath, Zulrah, Corporeal Beast

// Raids:
// - Chambers of Xeric
// - Theatre of Blood
// - Tombs of Amascut (233 era!)
```

**TODOs:**
- [ ] Port King Black Dragon
- [ ] Port God Wars bosses
- [ ] Port Dagannoth Kings
- [ ] Port Wilderness bosses
- [ ] Port Slayer bosses
- [ ] Port Zulrah
- [ ] Port Vorkath

---

## Phase 3: Polish (Priority: MEDIUM)

### 3.1 Prayer System Enhancement

```kotlin
// TODO: Complete prayer system
// Reference: Alter/mechanics/prayer/
// Reference: OpenRune/prayer/

// Features to add:
// - Prayer drain calculation (tick-based)
// - Rapid Heal, Rapid Restore
// - Protection prayers (full damage block)
// - Overhead prayer icons
// - Prayer flicking
// - Quick prayers
```

**TODOs:**
- [ ] Add prayer drain formula
- [ ] Add all protection prayers
- [ ] Add overhead icons
- [ ] Add quick prayer support

### 3.2 Bank System Enhancement

```kotlin
// TODO: Enhance bank system
// Reference: Kronos/bank/
// Reference: Tarnish/bank/

// Features:
// - Bank tabs
// - Bank search
// - Deposit box
// - Pin system
// - Note/unnote
// - Equip from bank
// - X/All/All-but-1
```

**TODOs:**
- [ ] Add bank tabs
- [ ] Add bank search
- [ ] Add deposit boxes
- [ ] Add bank PIN

### 3.3 Shop System

```kotlin
// TODO: Implement shop system
// Reference: Kronos/shop/
// Reference: OpenRune/shops/

class Shop {
    val items: Map<Int, ShopItem>
    val currency: Int
    val restockRate: Int
    
    fun buy(player: Player, item: Int, amount: Int)
    fun sell(player: Player, item: Int, amount: Int)
    fun restock()
}
```

**TODOs:**
- [ ] Create Shop class
- [ ] Add all F2P shops
- [ ] Add restocking
- [ ] Add general stores (sell anything)

---

## Phase 4: Advanced (Priority: LOW)

### 4.1 Achievement Diaries

```kotlin
// TODO: Implement achievement diaries
// Reference: OpenRune/achievementdiaries/

// Each area has easy/medium/hard/elite tasks
// Rewards: Items, experience lamps, shortcuts
```

### 4.2 Treasure Trails

```kotlin
// TODO: Implement clue scrolls
// Reference: Tarnish/cluescrolls/

// Types: Easy, Medium, Hard, Elite, Master
// Components:
// - Anagram clues
// - Cipher clues
// - Cryptic clues
// - Emote clues
// - Map clues
// - Coordinate clues
// - Puzzle boxes
```

### 4.3 Ironman Mode

```kotlin
// TODO: Implement ironman modes
// Reference: Tarnish/ironman/

// Modes:
// - Ironman
// - Hardcore Ironman
// - Ultimate Ironman
// - Group Ironman
```

---

## Implementation Priority Matrix

| Feature | Difficulty | Impact | Priority |
|---------|-----------|--------|----------|
| NPC Combat Definitions | Medium | Critical | P1 |
| Combat Formulas | Medium | Critical | P1 |
| Drop Tables | Low | Critical | P1 |
| Woodcutting (enhance) | Low | High | P1 |
| Mining (enhance) | Low | High | P1 |
| Thieving | Medium | High | P2 |
| Agility | Medium | High | P2 |
| Smithing | Medium | High | P2 |
| Crafting | Medium | High | P2 |
| Slayer | High | High | P2 |
| Special Attacks | Medium | Medium | P2 |
| Quests | Low | Medium | P2 |
| Barrows | High | Medium | P3 |
| GWD Bosses | High | Medium | P3 |
| Prayer System | Low | Medium | P3 |
| Achievement Diaries | Medium | Low | P4 |
| Treasure Trails | High | Low | P4 |

---

## Reference Mapping

### Where to Find What

| Need | Look In | Specific Files |
|------|---------|----------------|
| Combat formulas | OpenRune, Kronos | `formula/MeleeCombatFormula.kt`, `Combat.java` |
| Special attacks | Kronos, OpenRune | `combat/special/` |
| Skill success rates | Kronos | `skills/*/SuccessRate.java` |
| Thieving | Alter | `skills/thieving/*` |
| Loot tables | Alter, Kronos | `LootTableDsl.kt`, `LootTable.java` |
| Quest DSL | OpenRune | `quest/QuestScript.kt` |
| Boss AI | Tarnish | `combat/strategy/npc/boss/*` |
| Minigames | Tarnish | `plugins/minigames/*` |
| Packet handlers | RS-SDK, Tarnish | `network/packet/` |
| Protocol reference | RS-SDK | `ClientGameProt.ts`, `ServerGameProt.ts` |

---

## Next Steps

### This Week (Immediate Action)

1. **Hook up drop tables** (2 hours)
   ```kotlin
   // Connect GeneratedDropTables.kt to combat system
   ```

2. **Create first NPC combat def** (1 hour)
   ```kotlin
   // Goblin combat definition
   ```

3. **Port combat formulas** (4 hours)
   ```kotlin
   // Melee accuracy, max hit
   ```

### Next Two Weeks

4. **Implement Thieving** (8 hours)
   - Pickpocketing
   - Stall thieving

5. **Enhance existing skills** (4 hours)
   - Add bird nests to woodcutting
   - Add mining gloves

6. **Create 2-3 quests** (6 hours)
   - Cook's Assistant
   - Sheep Shearer

---

## Summary

You have **massive code resources** available:

- **Kronos-184**: Complete skills, 29 melee specials, 12 ranged specials, drops
- **Alter**: Thieving system, combat DSL, loot tables
- **OpenRune**: Quest system, skills, special attacks, prayers
- **Tarnish**: 40+ bosses, 10+ minigames, complete activities
- **RS-SDK**: Protocol reference, packet handlers

**Estimated time to F2P complete:** 3-6 months  
**Estimated time to Members complete:** 8-12 months

**Start with:** Hook up drop tables → Combat formulas → First skills

---

*This roadmap is a living document. Update as you progress.*

