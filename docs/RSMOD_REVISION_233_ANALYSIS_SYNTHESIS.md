# RSMod Revision 233 Analysis: Synthesis & Actionable Insights

**Research Source**: Gemini's comprehensive architectural analysis  
**F2P Content Source**: Kimi's dependency mapping  
**Date**: 2026-02-26  
**Purpose**: Bridge engine-level analysis with content-level implementation priorities

---

## Executive Summary

Gemini's research confirms what we've observed: **RSMod has a world-class protocol engine but lacks content APIs**. This creates a critical gap where:

- ✅ **Engine works**: Network, crypto, spatial computing, combat formulas
- ❌ **Content missing**: NPC Combat API, Drop Tables, Teleports, Environmental hazards

**Key Insight**: Our F2P content tasks (F2P-CRIT-1 through F2P-BIS-2) are blocked by the very APIs Gemini identified as missing.

---

## 1. Engine Capabilities Confirmed (Working)

From Gemini's research + our testing:

| Component | Status | Evidence |
|-----------|--------|----------|
| Protocol Revision 233 | ✅ Working | Can connect clients |
| ISAAC Cipher sync | ✅ Working | No opcode corruption |
| XTEA decryption | ✅ Working | Cache loads |
| Kotlin coroutines | ✅ Working | 600ms tick maintained |
| Java 21 ZGC | ✅ Working | No GC pauses |
| MAP_ANIMs | ✅ Working | AoE telegraphs possible |
| Multi-style combat | ✅ Working | Accuracy calc dynamic |
| Link_below support | ✅ Working | Multi-level rendering |

**Implication**: The engine can support our F2P content IF we build the APIs.

---

## 2. Critical API Gaps Identified (Blocking F2P Content)

### Gap #1: NPC Combat API (Blocks F2P-CRIT-5, F2P-CRIT-6, F2P-BOSS-1, F2P-BOSS-2)

**Gemini's Finding**: "Absence of a Unified NPC Combat Architecture"

**What's Missing**:
```kotlin
// We DON'T have this API:
interface NpcCombatApi {
    fun setAggressionRadius(tiles: Int)
    fun setAttackSpeed(ticks: Int)
    fun setAttackStyles(styles: List<AttackStyle>)
    fun setRetreatBehavior(healthPercent: Int)
    fun switchPhase(healthPercent: Int, newBehavior: CombatBehavior)
}
```

**Impact on F2P**:
- ❌ Can't implement Hill Giants (F2P-CRIT-5)
- ❌ Can't implement Moss Giants (F2P-CRIT-6)
- ❌ Can't implement Obor multi-phase (F2P-BOSS-1)
- ❌ Can't implement Bryophyta poison/summons (F2P-BOSS-2)

**Workaround Required**: Build ad-hoc combat scripts per NPC (inefficient)

---

### Gap #2: Drop Table API (Blocks ALL NPC Tasks)

**Gemini's Finding**: "Loot Generation and the Missing Drop Table API"

**What's Missing**:
```kotlin
// We DON'T have this:
interface DropTableApi {
    fun addGuaranteedDrop(item: Item)
    fun addWeightedDrop(item: Item, weight: Int, rarity: DropRarity)
    fun setRareDropTable(entries: List<RDTEntry>)
    fun addTertiaryDrop(item: Item, rate: Int) // Pets, clues
    fun setQuantityVariance(item: Item, min: Int, max: Int)
}
```

**Impact on F2P**:
- ❌ Hill Giants need: Big bones (100%), Limpwurt (1/15), Key (1/128)
- ❌ Moss Giants need: Big bones (100%), Mossy key (1/150), Nature runes
- ❌ Obor needs: Hill giant club (1/100?), Rune drops
- ❌ Bryophyta needs: Bryophyta's staff, herb drops

**Current State**: Manual PRNG per NPC (copy-paste hell)

---

### Gap #3: Action Queue / Delayed State API (Blocks Skills)

**Gemini's Finding**: "Disallow clearPendingAction during delayed state" patch

**What's Working Now**:
- ✅ Action queue prevents movement during delays

**Still Missing**:
```kotlin
// Face angle synchronization
interface EntityOrientationApi {
    fun setFaceAngle(target: Entity)
    fun setFaceAngle(angle: Int) // 0-2048
    fun getFaceAngle(): Int
}
```

**Impact on F2P**:
- 🟡 Giants might attack facing wrong direction
- 🟡 Boss telegraphing broken
- 🟡 Player combat animations desync

**Priority**: Medium (visual bug, not game-breaking)

---

### Gap #4: Teleport API (Blocks Magic, Quests)

**Gemini's Finding**: "Magic teleport spells remain an open issue"

**What's Missing**:
```kotlin
// We DON'T have this:
interface TeleportApi {
    fun teleport(
        player: Player,
        destination: CoordGrid,
        animation: AnimationId,
        gfx: GraphicsId,
        delayTicks: Int = 3,
        requireRuneCheck: Boolean = true
    )
    
    fun verifyRuneRequirements(
        player: Player,
        runes: Map<RuneType, Int>
    ): Boolean
}
```

**Impact on F2P**:
- ❌ Can't implement Varrock teleport (25 Magic)
- ❌ Can't implement Lumbridge teleport (31 Magic)
- ❌ Can't implement Falador teleport (37 Magic)
- ❌ Dragon Slayer I (F2P-QUEST-1) needs boat travel (teleport-like)

---

### Gap #5: Environmental Hazard API (Blocks Cave Content)

**Gemini's Finding**: "Lumbridge Swamp Cave plugin" missing

**What's Missing**:
```kotlin
interface EnvironmentalHazardApi {
    fun checkLightSource(player: Player): Boolean
    fun applyGasExplosion(player: Player)
    fun applyDarknessDamage(player: Player, ticksInDark: Int)
    fun registerWallBeast(burrowTile: Tile, triggerTile: Tile)
}
```

**Impact on F2P**:
- 🟡 Edgeville Dungeon (Hill Giants) needs hazard support
- 🟡 Varrock Sewers (Moss Giants) needs environment handling
- 🟡 Future: Dwarven Mine darkness

**Priority**: Medium (can use simplified implementation)

---

## 3. F2P Content Blocker Matrix

| F2P Task | Blocked By | Gemini's Analysis | Workaround Available? |
|----------|-----------|-------------------|----------------------|
| F2P-CRIT-1 (Maple trees) | None | Engine supports | ✅ Can implement |
| F2P-CRIT-2 (Yew trees) | None | Engine supports | ✅ Can implement |
| F2P-CRIT-3 (Rune bars) | None | Engine supports | ✅ Can implement |
| F2P-CRIT-4 (Rune gear) | None | Engine supports | ✅ Can implement |
| **F2P-CRIT-5** (Hill Giants) | **NPC Combat API** | Missing per Gemini | ❌ Needs API |
| **F2P-CRIT-6** (Moss Giants) | **NPC Combat API** | Missing per Gemini | ❌ Needs API |
| **F2P-BOSS-1** (Obor) | **NPC Combat API** | Missing per Gemini | ❌ Needs API |
| **F2P-BOSS-2** (Bryophyta) | **NPC Combat API** | Missing per Gemini | ❌ Needs API |
| F2P-QUEST-1 (Dragon Slayer) | Teleport API | Missing per Gemini | 🟡 Manual teleport |
| F2P-BIS-1 (D'hide) | None | Crafting works | ✅ Can implement |
| F2P-BIS-2 (Bows) | None | Fletching works | ✅ Can implement |

**Critical Finding**: 4 of 11 F2P tasks are **hard blocked** by missing NPC Combat API.

---

## 4. Synthesis: Two Parallel Tracks Needed

Based on Gemini's research + our F2P needs:

### Track A: Engine API Development (Gemini's Domain)

**Priority 1: NPC Combat API** (Week 1-2)
```kotlin
// Proposed API structure based on Gemini's findings
package org.rsmod.api.npc.combat

class NpcCombatConfig {
    var aggressionRadius: Int = 4
    var attackSpeed: Int = 4 // ticks
    var attackStyles: List<AttackStyle> = listOf(AttackStyle.MELEE_CRUSH)
    var maxHit: Int = 1
    var stats: NpcStats = NpcStats()
    var retreatHealthPercent: Int? = null
    var isPoisonous: Boolean = false
    
    // Phase system for bosses
    var phases: List<CombatPhase> = emptyList()
}

class CombatPhase(
    val healthPercent: Int, // Trigger at HP%
    val newConfig: NpcCombatConfig,
    val onEnter: (Npc) -> Unit // Callback
)
```

**Priority 2: Drop Table API** (Week 1-2)
```kotlin
package org.rsmod.api.npc.drops

class DropTable {
    val guaranteed = mutableListOf<Item>()
    val mainTable = WeightedDropTable()
    val rareDropTable: RDT? = null
    val tertiary = mutableListOf<TertiaryDrop>()
    
    fun roll(): List<Item> {
        val drops = mutableListOf<Item>()
        drops.addAll(guaranteed)
        drops.addAll(mainTable.roll())
        drops.addAll(rollTertiary())
        return drops
    }
}
```

**Priority 3: Teleport API** (Week 2)
```kotlin
package org.rsmod.api.teleport

object TeleportService {
    fun standardTeleport(
        player: Player,
        dest: CoordGrid,
        anim: SeqType = TeleportAnims.standard,
        gfx: SpotanimType = TeleportGfx.standard,
        delay: Int = 4
    )
}
```

### Track B: F2P Content Implementation (Kimi/Codex Domain)

**Parallel Work (Can Start Now)**:
- F2P-CRIT-1: Maple trees (no API needed)
- F2P-CRIT-2: Yew trees (no API needed)
- F2P-CRIT-3: Rune bar smelting (no API needed)
- F2P-CRIT-4: Rune equipment smithing (no API needed)
- F2P-BIS-1: Green d'hide crafting (no API needed)
- F2P-BIS-2: Maple/Yew bows (no API needed)

**Blocked Until APIs Ready**:
- F2P-CRIT-5: Hill Giants (needs Combat API + Drop API)
- F2P-CRIT-6: Moss Giants (needs Combat API + Drop API)
- F2P-BOSS-1: Obor (needs Combat API + Phase system)
- F2P-BOSS-2: Bryophyta (needs Combat API + Phase system + Poison)

---

## 5. Recommended Immediate Actions

### For Gemini (Engine/API Work)

Based on Gemini's research expertise, prioritize:

**Week 1**:
```
API-NPC-COMBAT-1: Basic NPC Combat API
├── Aggression radius
├── Attack speed/tick delay
├── Basic melee attack
└── Death handling

API-NPC-DROPS-1: Basic Drop Table API
├── Guaranteed drops
├── Weighted main table
└── Single roll execution
```

**Week 2**:
```
API-NPC-COMBAT-2: Advanced Combat
├── Attack style switching
├── Retreat behavior
├── Combat phases

API-TELEPORT-1: Teleport API
├── Standard teleport
├── Rune verification
└── Animation/GFX handling
```

### For Kimi/Codex (Content Work)

**While Gemini builds APIs, implement non-blocked content**:

```
PARALLEL WEEK 1:
├── F2P-CRIT-1: Add maple trees
├── F2P-CRIT-2: Add yew trees
├── F2P-CRIT-3: Rune bar smelting
├── F2P-CRIT-4: Rune equipment smithing
└── F2P-BIS-2: Maple/Yew bows

PARALLEL WEEK 2:
├── F2P-BIS-1: Green d'hide crafting
├── Verify all F2P furnaces work
├── Verify all F2P anvils work
└── Test WC/Fletching integration
```

---

## 6. Implementation Strategy: Adapters

**Problem**: Can't wait for perfect APIs
**Solution**: Build adapter layer that can migrate to official APIs later

### Example: Temporary NPC Combat Adapter

```kotlin
// Temporary implementation until official API exists
// File: rsmod/content/f2p/combat/adapter/SimpleNpcCombat.kt

package org.rsmod.content.f2p.combat.adapter

class SimpleNpcCombat(private val npc: Npc) {
    private var target: Player? = null
    private var attackCooldown = 0
    
    fun process() {
        if (attackCooldown > 0) {
            attackCooldown--
            return
        }
        
        // Simple aggression check
        if (target == null) {
            findTarget()
        }
        
        // Attack if in range
        target?.let { attack(it) }
    }
    
    private fun findTarget() {
        // Check 4-tile radius for players
        // Set target if found
    }
    
    private fun attack(player: Player) {
        // Deal damage
        // Reset cooldown (4 ticks for most NPCs)
        attackCooldown = 4
    }
}

// Usage in Hill Giant script until official API:
val combat = SimpleNpcCombat(this)
onGameTick { combat.process() }
```

**Benefits**:
- ✅ Can implement F2P-CRIT-5,6 NOW
- ✅ Easy to migrate when official API exists
- ✅ Documents requirements for official API

---

## 7. Testing Strategy

Based on Gemini's research, test these critical areas:

### Protocol-Level Tests (Gemini)
```kotlin
@Test
fun `face angle sync after attack`() {
    // Verify NPC faces target when attacking
    // From Gemini's "faceangle" finding
}

@Test
fun `action queue delay respected`() {
    // Verify can't move during attack animation
    // From Gemini's "clearPendingAction" finding
}

@Test
fun `drop table PRNG accuracy`() {
    // Verify 1/128 drop rate over 10000 kills
    // From Gemini's PRNG requirements
}
```

### Content-Level Tests (Kimi/Codex)
```kotlin
@Test
fun `hill giant drops big bones`() {
    // Guaranteed drop works
}

@Test
fun `obor key 1 in 128 rate`() {
    // Statistical verification
}

@Test
fun `maple shortbow requires 50 fletching`() {
    // Level requirement enforced
}
```

---

## 8. Risk Mitigation

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| NPC Combat API delayed | High | F2P-CRIT-5,6 blocked | Build adapter layer |
| Drop Table API delayed | High | All NPC drops broken | Hardcode temporarily |
| Teleport API delayed | Medium | Quests broken | Manual coord updates |
| Protocol churn (234+) | Medium | Engine breaks | Lock to 233 for now |
| ZGC issues in production | Low | Server lag | Fallback to G1GC |

---

## 9. Success Metrics

### Engine-Level (Gemini Tracks)
- [ ] NPC Combat API merged
- [ ] Drop Table API merged
- [ ] Teleport API merged
- [ ] 0 GC pauses >10ms
- [ ] 600ms tick maintained under load

### Content-Level (Kimi/Codex Track)
- [ ] All 11 F2P tasks complete
- [ ] Hill Giants killable
- [ ] Obor killable
- [ ] Dragon Slayer I completable
- [ ] F2P BIS gear obtainable

### Integration (Joint Testing)
- [ ] 100 Hill Giant kills test
- [ ] 10 Obor kills test
- [ ] F2P money making: 50k+/hr
- [ ] F2P combat training: viable to 99

---

## 10. Conclusion

**Gemini's research is correct**: RSMod has excellent engine fundamentals but lacks content APIs.

**Our F2P work is valid**: The tasks we identified (F2P-CRIT-1 through F2P-BIS-2) are the right priorities.

**The blocker is real**: 4 of 11 F2P tasks need NPC Combat API.

**The path forward**:
1. **Gemini**: Build core APIs (Combat, Drops, Teleport)
2. **Kimi/Codex**: Build non-blocked content + adapters
3. **Joint**: Test integration, iterate

**Timeline**: 3-4 weeks to F2P completeness if APIs are prioritized.

---

**Document Status**: Synthesis complete  
**Next Step**: Gemini to prioritize API development, Kimi/Codex to claim non-blocked F2P tasks

