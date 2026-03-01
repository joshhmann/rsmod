# F2P Implementation Plan for RSMod

**Architecture**: RSMod modular pattern (api/ + content/)
**Goal**: Complete F2P content using proper module separation
**Timeline**: 3-4 weeks

---

## Team Assignments

| Role | Responsibility | Modules |
|------|----------------|---------|
| **Gemini** | API Development | `api/npc-combat/`, `api/drop-table/`, `api/teleport/` |
| **Kimi** | Content Implementation | `content/skills/*`, `content/other/npc-combat/`, `content/other/npc-drops/` |
| **Codex** | Boss Implementation | `content/other/bosses/`, `content/quests/` |

---

## Phase 1: API Foundation (Week 1)

### Task: API-NPC-COMBAT-1
**Owner**: Gemini  
**Module**: `api/npc-combat/` (NEW)  
**Estimated**: 16 hours

**Deliverables**:
```
api/npc-combat/
├── build.gradle.kts
└── src/main/kotlin/org/rsmod/api/npc/combat/
    ├── NpcCombatApi.kt              # Interface
    ├── NpcCombatConfig.kt           # Data classes
    ├── NpcCombatProcessor.kt        # Tick processing
    ├── NpcCombatModule.kt           # DI module
    └── internal/
        ├── AggressionHandler.kt     # Target finding
        ├── AttackHandler.kt         # Attack execution
        └── DeathHandler.kt          # Death/drops
```

**Key Features**:
- Aggression radius check (line-of-sight)
- Attack speed/tick delay (4 ticks default)
- Attack style switching
- Basic melee combat
- Death callback

**Tests Required**:
- [ ] Aggression works within radius
- [ ] Attack speed is 4 ticks (2.4s)
- [ ] Death callback triggers

---

### Task: API-DROP-TABLE-1
**Owner**: Gemini  
**Module**: `api/drop-table/` (NEW)  
**Estimated**: 12 hours

**Deliverables**:
```
api/drop-table/
├── build.gradle.kts
└── src/main/kotlin/org/rsmod/api/droptable/
    ├── DropTableApi.kt
    ├── DropTableBuilder.kt
    ├── DropTableProcessor.kt
    ├── PrngEngine.kt              # Random number gen
    └── domain/
        ├── DropEntry.kt
        ├── WeightedDrop.kt
        └── GuaranteedDrop.kt
```

**Key Features**:
- Guaranteed drops (100%)
- Weighted main table
- Tertiary drops (independent)
- Quantity ranges
- PRNG accuracy

**Tests Required**:
- [ ] 1/128 drop rate accurate over 10k rolls
- [ ] Guaranteed drops always drop
- [ ] Quantity ranges work

---

### Task: API-TELEPORT-1
**Owner**: Gemini  
**Module**: `api/teleport/` (NEW or extend)  
**Estimated**: 8 hours

**Deliverables**:
- Standard teleport with animation/gfx
- Rune verification
- Rune consumption
- Delay enforcement

**Key Features**:
```kotlin
interface TeleportApi {
    fun standardTeleport(player, dest, anim, gfx, delayTicks)
    fun ancientTeleport(player, dest, anim, gfx, delayTicks)
    fun lunarTeleport(player, dest, anim, gfx, delayTicks)
    fun consumeRunes(player, runes): Boolean
}
```

---

## Phase 2: F2P Content (Parallel with APIs)

### Task: F2P-SKILL-WC-1
**Owner**: Kimi  
**Module**: `content/skills/woodcutting/` (EXTEND)  
**Blocked By**: None  
**Estimated**: 4 hours

**Work**:
- Add maple tree spawns to F2P areas
- Add yew tree spawns to F2P areas
- Verify tree levels (Maple 45, Yew 60)

**Files**:
```
content/skills/woodcutting/src/.../configs/TreeLocs.kt
- Add mapleLocations
- Add yewLocations
```

---

### Task: F2P-SKILL-SMITH-1
**Owner**: Kimi  
**Module**: `content/skills/smithing/` (EXTEND)  
**Blocked By**: None  
**Estimated**: 6 hours

**Work**:
- Implement rune bar smelting (85 Smithing)
- Implement rune equipment smithing
- Verify furnace locations work
- Verify anvil locations work

**Files**:
```
content/skills/smithing/src/.scripts/Smithing.kt
- Add rune bar recipe
- Add rune weapon recipes
- Add rune armor recipes
```

---

### Task: F2P-SKILL-FLETCH-1
**Owner**: Kimi  
**Module**: `content/skills/fletching/` (EXTEND)  
**Blocked By**: F2P-SKILL-WC-1 (needs maple/yew logs)  
**Estimated**: 4 hours

**Work**:
- Maple shortbow (50 Fletching)
- Maple longbow (55 Fletching)
- Yew shortbow (65 Fletching)
- Yew longbow (70 Fletching)

---

### Task: F2P-CONTENT-NPCS-1
**Owner**: Kimi  
**Module**: `content/other/npc-combat/` (NEW)  
**Blocked By**: API-NPC-COMBAT-1  
**Estimated**: 8 hours

**Work**:
- Hill Giant configuration
- Moss Giant configuration

**Files**:
```
content/other/npc-combat/
├── build.gradle.kts
└── src/main/kotlin/org/rsmod/content/other/npccombat/
    ├── F2pGiants.kt
    └── F2pGiantConfig.kt
```

**Code Example**:
```kotlin
@Singleton
class F2pGiants @Inject constructor(
    private val combatApi: NpcCombatApi
) : PluginScript() {
    
    override fun ScriptContext.startup() {
        combatApi.configure(
            npc = BaseNpcs.hill_giant,
            config = NpcCombatConfig(
                aggressionRadius = 4,
                attackSpeed = 4,
                stats = NpcStats(hitpoints = 35, attack = 18, strength = 16),
                maxHit = 4
            )
        )
    }
}
```

---

### Task: F2P-CONTENT-DROPS-1
**Owner**: Kimi  
**Module**: `content/other/npc-drops/` (NEW)  
**Blocked By**: API-DROP-TABLE-1  
**Estimated**: 6 hours

**Work**:
- Hill Giant drop table
- Moss Giant drop table
- Key drops for bosses

**Files**:
```
content/other/npc-drops/
├── build.gradle.kts
└── src/main/kotlin/org/rsmod/content/other/npcdrops/
    ├── F2pGiantDrops.kt
    └── GiantDropTables.kt
```

---

## Phase 3: Bosses & Quests (Week 2-3)

### Task: F2P-BOSS-OBOR-1
**Owner**: Codex  
**Module**: `content/other/bosses/` (NEW or EXTEND)  
**Blocked By**: API-NPC-COMBAT-1, API-DROP-TABLE-1  
**Estimated**: 16 hours

**Work**:
- Multi-phase combat (3 phases)
- Knockback mechanic
- Drop table (Hill giant club, rune items)
- Key access (Giant key from Hill Giants)

**Files**:
```
content/other/bosses/
└── src/main/kotlin/org/rsmod/content/other/bosses/obor/
    ├── OborConfig.kt
    ├── OborPhaseHandler.kt
    └── OborDrops.kt
```

---

### Task: F2P-BOSS-BRYOPHYTA-1
**Owner**: Codex  
**Module**: `content/other/bosses/`  
**Blocked By**: API-NPC-COMBAT-1, API-DROP-TABLE-1  
**Estimated**: 16 hours

**Work**:
- Multi-phase combat
- Poison mechanic
- Growthling summons
- Drop table (Bryophyta's staff)
- Key access (Mossy key from Moss Giants)

---

### Task: F2P-QUEST-DRAGONSLAYER-1
**Owner**: Codex  
**Module**: `content/quests/dragon-slayer/` (NEW)  
**Blocked By**: API-TELEPORT-1 (boat travel)  
**Estimated**: 20 hours

**Work**:
- Quest steps (collect 3 map pieces)
- Elvarg boss fight
- Quest reward (Rune platebody equip)

---

## Phase 4: Integration & Testing (Week 3-4)

### Integration Tests

```kotlin
// Test: Complete F2P melee setup
@Test
fun `f2p player can obtain rune scimitar`() {
    // 1. Mine runite ore (85 Mining)
    // 2. Smelt rune bar (85 Smithing)
    // 3. Smith rune scimitar (90 Smithing)
    // 4. Equip (40 Attack)
}

// Test: Kill Hill Giant
@Test
fun `f2p player can kill hill giant`() {
    // 1. Find Hill Giant
    // 2. Combat works (API)
    // 3. Drops big bones (Drop API)
    // 4. Key drop works (1/128)
}

// Test: Obor fight
@Test
fun `f2p player can fight obor`() {
    // 1. Get Giant key
    // 2. Enter Obor instance
    // 3. Combat phases work
    // 4. Drops work
}
```

---

## Build Commands

### Build API Module
```bash
cd rsmod
./gradlew :api:npc-combat:build
./gradlew :api:drop-table:build
```

### Build Content Module
```bash
cd rsmod
./gradlew :content:other:npc-combat:build
./gradlew :content:other:npc-drops:build
```

### Full Build
```bash
cd rsmod
./gradlew build --console=plain
```

---

## Success Criteria

### APIs Complete (Gemini)
- [ ] api:npc-combat builds without errors
- [ ] api:drop-table builds without errors
- [ ] api:teleport builds without errors
- [ ] Unit tests pass

### Content Complete (Kimi/Codex)
- [ ] content:other:npc-combat builds
- [ ] content:other:npc-drops builds
- [ ] content:other:bosses builds
- [ ] content:quests:dragon-slayer builds

### Integration Complete
- [ ] Hill Giants killable
- [ ] Moss Giants killable
- [ ] Obor killable
- [ ] Bryophyta killable
- [ ] Dragon Slayer I completable
- [ ] Rune platebody equippable

---

## Risk Mitigation

| Risk | Mitigation |
|------|-----------|
| API takes longer than expected | Build adapter layer in content module temporarily |
| Drop rate accuracy issues | Extensive statistical testing (10k+ rolls) |
| Combat sync issues | Test face angles, tick delays rigorously |
| Quest complexity | Break Dragon Slayer into sub-tasks |

---

## Immediate Next Steps

### Today
1. **Gemini**: Create `api/npc-combat/` module structure
2. **Gemini**: Create `api/drop-table/` module structure
3. **Kimi**: Create `content/other/npc-combat/` module (prepare for API)

### This Week
1. Gemini implements API-NPC-COMBAT-1
2. Gemini implements API-DROP-TABLE-1
3. Kimi implements F2P-SKILL-WC-1 (parallel)
4. Kimi implements F2P-SKILL-SMITH-1 (parallel)

### Next Week
1. Kimi implements F2P-CONTENT-NPCS-1 (using APIs)
2. Kimi implements F2P-CONTENT-DROPS-1 (using APIs)
3. Codex begins F2P-BOSS-OBOR-1

---

**Status**: Ready to begin  
**Priority**: API modules first (blocks content)  
**Parallel work**: Non-blocked skills (WC, Smithing, Fletching)

