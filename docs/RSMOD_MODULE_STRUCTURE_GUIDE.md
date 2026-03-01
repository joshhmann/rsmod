# RSMod Module Structure Guide for F2P Implementation

**Architecture Pattern**: RSMod uses strict modular separation
- `engine/` - Core network/protocol (don't touch)
- `api/` - Standardized APIs bridging engine and content
- `content/` - Game logic plugins using APIs

**Date**: 2026-02-26

---

## Module Hierarchy

```
rsmod/
├── engine/          # Core protocol, crypto, tick loop (Gemini only)
│   ├── net/         # Network packet handling
│   ├── game/        # Game loop, entity management
│   └── ...          # Never modify for content
│
├── api/             # Standardized APIs (Formal modules)
│   ├── config/      # Cache configs, constants
│   ├── player/      # Player APIs
│   ├── npc/         # NPC base APIs
│   ├── combat/      # Combat formulas
│   ├── invtx/       # Inventory transactions
│   ├── drop-table/  # 🟡 MISSING - needs creation
│   ├── npc-combat/  # 🟡 MISSING - needs creation
│   └── teleport/    # 🟡 MISSING - needs creation
│
└── content/         # Game content plugins (Drop-in modules)
    ├── skills/      # All skills (WC, Mining, Smithing, etc.)
    ├── areas/       # Cities, dungeons
    ├── quests/      # Quest implementations
    ├── mechanics/   # Combat, poison, etc.
    └── other/       # NPCs, bosses, drops
        ├── npc-combat/      # NPC combat behaviors
        ├── npc-drops/       # Drop table registrations
        └── bosses/          # Boss implementations
```

---

## API Module Creation Pattern

When building missing APIs, follow this structure:

### Step 1: Create API Module

```
rsmod/api/npc-combat/
├── build.gradle.kts
└── src/main/kotlin/org/rsmod/api/npc/combat/
    ├── NpcCombat.kt           # Main API interface
    ├── NpcCombatConfig.kt     # Configuration data class
    ├── NpcCombatProcessor.kt  # Core logic
    └── NpcCombatModule.kt     # Dependency injection module
```

### Step 2: Content Uses API

```
rsmod/content/other/npc-combat/
├── build.gradle.kts          # Depends on api:npc-combat
└── src/main/kotlin/org/rsmod/content/other/npccombat/
    ├── HillGiantConfig.kt    # Uses NpcCombatConfig
    ├── MossGiantConfig.kt    # Uses NpcCombatConfig
    └── NpcCombatPlugin.kt    # Registers NPCs with API
```

---

## Required API Modules for F2P

### API Module 1: api/npc-combat (NEW)

**Purpose**: Standardized NPC combat behavior

**Files to Create**:

```kotlin
// api/npc-combat/src/main/kotlin/org/rsmod/api/npc/combat/NpcCombatApi.kt
package org.rsmod.api.npc.combat

interface NpcCombatApi {
    fun configure(npc: Npc, config: NpcCombatConfig)
    fun processCombatTick(npc: Npc)
    fun findTarget(npc: Npc): Player?
    fun attack(npc: Npc, target: Player)
    fun onDeath(npc: Npc, killer: Player?)
}

data class NpcCombatConfig(
    val aggressionRadius: Int = 4,
    val attackSpeed: Int = 4, // ticks
    val attackStyles: List<AttackStyle> = listOf(AttackStyle.MELEE_CRUSH),
    val stats: NpcStats,
    val maxHit: Int,
    val retreatHealthPercent: Int? = null,
    val isPoisonous: Boolean = false,
    val phases: List<CombatPhase> = emptyList()
)
```

**Build Config**:
```kotlin
// api/npc-combat/build.gradle.kts
plugins { id("base-conventions") }

dependencies {
    implementation(projects.api.pluginCommons)
    implementation(projects.api.npc)
    implementation(projects.api.combat.combatFormulas)
}
```

### API Module 2: api/drop-table (NEW)

**Purpose**: Standardized loot generation

**Files to Create**:

```kotlin
// api/drop-table/src/main/kotlin/org/rsmod/api/droptable/DropTableApi.kt
package org.rsmod.api.droptable

interface DropTableApi {
    fun createTable(): DropTableBuilder
    fun roll(table: DropTable, player: Player, npc: Npc): List<Item>
}

class DropTableBuilder {
    fun addGuaranteed(item: Item, quantity: Int = 1)
    fun addWeighted(item: Item, weight: Int, quantityRange: IntRange = 1..1)
    fun setRdt(rdt: RareDropTable)
    fun addTertiary(item: Item, denominator: Int) // 1/denominator chance
}
```

**Build Config**:
```kotlin
// api/drop-table/build.gradle.kts
plugins { id("base-conventions") }

dependencies {
    implementation(projects.api.pluginCommons)
    implementation(projects.api.random) // For PRNG
}
```

### API Module 3: api/teleport (NEW or extend existing)

**Purpose**: Standardized teleportation

**Files to Create/Extend**:

```kotlin
// api/teleport/src/main/kotlin/org/rsmod/api/teleport/TeleportApi.kt
package org.rsmod.api.teleport

interface TeleportApi {
    fun standardTeleport(
        player: Player,
        destination: CoordGrid,
        animation: SeqType? = TeleportAnims.STANDARD,
        graphic: SpotanimType? = TeleportGfx.STANDARD,
        delayTicks: Int = 4
    )
    
    fun verifyRunes(player: Player, runes: Map<RuneType, Int>): Boolean
    fun consumeRunes(player: Player, runes: Map<RuneType, Int>): Boolean
}
```

---

## Content Module Implementation

### Content Module: content/other/npc-combat

**Purpose**: Specific NPC combat configurations using the API

**Files**:

```kotlin
// content/other/npc-combat/src/main/kotlin/org/rsmod/content/other/npccombat/F2pGiants.kt
package org.rsmod.content.other.npccombat

import org.rsmod.api.npc.combat.NpcCombatApi
import org.rsmod.api.npc.combat.NpcCombatConfig
import org.rsmod.api.config.refs.Npcs

class F2pGiants @Inject constructor(
    private val combatApi: NpcCombatApi
) : PluginScript() {
    
    override fun ScriptContext.startup() {
        // Hill Giant configuration
        combatApi.configure(
            npc = Npcs.hill_giant,
            config = NpcCombatConfig(
                aggressionRadius = 4,
                attackSpeed = 4,
                attackStyles = listOf(AttackStyle.MELEE_CRUSH),
                stats = NpcStats(
                    hitpoints = 35,
                    attack = 18,
                    strength = 16,
                    defence = 18
                ),
                maxHit = 4
            )
        )
        
        // Moss Giant configuration
        combatApi.configure(
            npc = Npcs.moss_giant,
            config = NpcCombatConfig(
                aggressionRadius = 4,
                attackSpeed = 4,
                attackStyles = listOf(AttackStyle.MELEE_CRUSH),
                stats = NpcStats(
                    hitpoints = 60,
                    attack = 30,
                    strength = 30,
                    defence = 30
                ),
                maxHit = 6
            )
        )
    }
}
```

**Build Config**:
```kotlin
// content/other/npc-combat/build.gradle.kts
plugins { id("base-conventions") }

dependencies {
    implementation(projects.api.pluginCommons)
    implementation(projects.api.npcCombat) // NEW API
    implementation(projects.api.config)
}
```

### Content Module: content/other/npc-drops

**Purpose**: Drop table registrations using the API

```kotlin
// content/other/npc-drops/src/main/kotlin/org/rsmod/content/other/npcdrops/F2pGiantDrops.kt
package org.rsmod.content.other.npcdrops

import org.rsmod.api.droptable.DropTableApi
import org.rsmod.api.config.refs.Npcs
import org.rsmod.api.config.refs.Objs

class F2pGiantDrops @Inject constructor(
    private val dropApi: DropTableApi
) : PluginScript() {
    
    override fun ScriptContext.startup() {
        // Hill Giant drops
        val hillGiantTable = dropApi.createTable {
            addGuaranteed(Objs.bones, quantity = 1)
            addWeighted(Objs.limpwurt_root, weight = 20, quantity = 1)
            addWeighted(Objs.iron_arrow, weight = 10, quantity = 5..15)
            addWeighted(Objs.coins, weight = 50, quantity = 5..100)
            addTertiary(Objs.giant_key, denominator = 128) // Obor key
        }
        
        // Moss Giant drops
        val mossGiantTable = dropApi.createTable {
            addGuaranteed(Objs.bones, quantity = 1)
            addWeighted(Objs.nature_rune, weight = 30, quantity = 5..15)
            addWeighted(Objs.law_rune, weight = 15, quantity = 1..5)
            addTertiary(Objs.mossy_key, denominator = 150) // Bryophyta key
        }
        
        // Register tables
        registerDropTable(Npcs.hill_giant, hillGiantTable)
        registerDropTable(Npcs.moss_giant, mossGiantTable)
    }
}
```

**Build Config**:
```kotlin
// content/other/npc-drops/build.gradle.kts
dependencies {
    implementation(projects.api.pluginCommons)
    implementation(projects.api.dropTable) // NEW API
    implementation(projects.api.config)
}
```

---

## Implementation Order

### Phase 1: Create APIs (Gemini)

**Week 1 - API Development**:
```
api/npc-combat/
├── Create module
├── Implement core interfaces
├── Build combat tick processor
└── Test with dummy NPC

api/drop-table/
├── Create module
├── Implement PRNG rolls
├── Build weighted selection
└── Test drop rates
```

### Phase 2: Create Content (Kimi/Codex)

**Week 1-2 - Content Using APIs**:
```
content/other/npc-combat/
├── Add dependency on api:npc-combat
├── Create F2pGiants.kt
├── Configure Hill Giant
├── Configure Moss Giant
└── Test combat

content/other/npc-drops/
├── Add dependency on api:drop-table
├── Create F2pGiantDrops.kt
├── Configure drop tables
└── Test drops
```

### Phase 3: Boss Content (Wave 2)

```
content/other/bosses/
├── Obor implementation
│   ├── Uses api:npc-combat (phases)
│   ├── Uses api:drop-table (loot)
│   └── Key access logic
├── Bryophyta implementation
│   ├── Uses api:npc-combat (phases + poison)
│   ├── Uses api:drop-table (loot)
│   └── Key access logic
```

---

## Dependency Graph

```
api/npc-combat (NEW)
├── engine/ (uses)
├── api/npc/ (uses)
└── api/combat/ (uses)

api/drop-table (NEW)
├── engine/ (uses)
└── api/random/ (uses)

content/other/npc-combat (NEW)
├── api/npc-combat/ (depends on)
├── api/config/ (depends on)
└── api/npc/ (depends on)

content/other/npc-drops (NEW)
├── api/drop-table/ (depends on)
├── api/config/ (depends on)
└── api/npc/ (depends on)

content/other/bosses (exists?)
├── api/npc-combat/ (depends on)
├── api/drop-table/ (depends on)
└── api/config/ (depends on)
```

---

## Build Integration

After creating modules, update:

```kotlin
// rsmod/settings.gradle.kts
// No changes needed - auto-discovers new modules

// rsmod/api/build.gradle.kts (if aggregator exists)
dependencies {
    // Add new API modules
    implementation(project(":api:npc-combat"))
    implementation(project(":api:drop-table"))
}
```

---

## Testing Strategy

### API-Level Tests (in api/ modules)
```kotlin
// api/npc-combat/src/test/kotlin/...
class NpcCombatApiTest {
    @Test
    fun `aggression radius respected`() {
        // Test NPC only aggros within 4 tiles
    }
    
    @Test
    fun `attack speed is 4 ticks`() {
        // Test 600ms * 4 = 2.4s between attacks
    }
}

// api/drop-table/src/test/kotlin/...
class DropTableApiTest {
    @Test
    fun `guaranteed drop always drops`() {
        // Test 100% drop rate
    }
    
    @Test
    fun `1 in 128 drop rate accurate`() {
        // Statistical test over 10000 rolls
    }
}
```

### Content-Level Tests (in content/ modules)
```kotlin
// content/other/npc-combat/src/test/kotlin/...
class HillGiantCombatTest {
    @Test
    fun `hill giant has 35 hp`() {
        // Verify config applied
    }
    
    @Test
    fun `hill giant max hit is 4`() {
        // Verify combat formula
    }
}
```

---

## Migration Path

### Current State (Ad-Hoc)
```kotlin
// Current: Each NPC has hardcoded combat
class HillGiantScript : PluginScript() {
    override fun ScriptContext.startup() {
        onNpcSpawn(Npcs.hill_giant) { npc ->
            // Hardcoded combat logic
            // No reuse
        }
    }
}
```

### Target State (API-Based)
```kotlin
// Target: Use standardized API
class HillGiantConfig @Inject constructor(
    api: NpcCombatApi
) {
    fun configure() {
        api.configure(Npcs.hill_giant, config)
    }
}
```

### Migration Steps:
1. Build API modules
2. Migrate existing NPCs to use API
3. Create new F2P NPCs using API
4. Deprecate ad-hoc scripts

---

## Summary

**API Modules to Create** (in `api/`):
- `api/npc-combat` - Standardized combat behavior
- `api/drop-table` - Standardized loot generation
- `api/teleport` - Standardized teleportation

**Content Modules to Create** (in `content/`):
- `content/other/npc-combat` - F2P giant configurations
- `content/other/npc-drops` - F2P giant drop tables
- `content/other/bosses` - Obor, Bryophyta

**Pattern**: Build API → Test API → Build Content using API → Test Content

---

**Next Action**: Gemini creates `api/npc-combat` and `api/drop-table` modules, then Kimi/Codex create content modules that depend on them.

