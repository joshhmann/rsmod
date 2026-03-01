# RSMod vs AlterRSPS Pattern Analysis

**Date**: 2026-02-26  
**Context**: Understanding which codebase patterns to follow for F2P implementation

---

## Key Finding

**The RSMod codebase includes work from BOTH:**
1. **Original RSMod team** - Core engine, protocol, infrastructure
2. **Our team** (Gemini, Claude, Codex, Kimi) - Content plugins, drop tables, APIs

**AlterRSPS** is a fork/reference based on rev 228 with different architecture patterns.

---

## Architecture Comparison

### RSMod v2 (Current Codebase)

**Pattern: Engine-driven with minimal scripts**

```
Engine (handles combat automatically)
    ↓
npcs.toml (spawn definitions)
    ↓
Drop Tables (content/other/npc-drops/)
    ↓
Minimal Scripts (only for special behavior)
```

**NPC Combat Pattern**:
```kotlin
// RSMod: Minimal script, engine handles most
class ChaosDruidCombatScript : PluginScript() {
    override fun ScriptContext.startup() {
        onNpcHit(NpcCombatTypes.chaos_druid) {
            // Engine handles aggression/retaliation
            // Only add special behavior here
        }
    }
}
```

**Key Files**:
- `api/drop-table/` - Drop table API (our work)
- `content/other/npc-drops/tables/` - Drop implementations (our work)
- `content/areas/*/npcs.toml` - Spawns (our work)
- `content/other/npc-combat/` - Minimal combat scripts

---

### AlterRSPS (Reference - Rev 228)

**Pattern: Scripted combat with full control**

```
Plugin scripts handle everything
    ↓
Combat loop in coroutine/queue
    ↓
Manual attack selection
    ↓
Manual hit calculation
    ↓
Manual effects
```

**NPC Combat Pattern**:
```kotlin
// Alter: Full combat script
class KbdCombatPlugin : KotlinPlugin(...) {
    init {
        onNpcCombat("npc.king_black_dragon") {
            npc.queue {
                npc.combat(this)  // Full combat loop
            }
        }
    }
    
    private suspend fun Npc.combat(it: QueueTask) {
        while (canEngageCombat(target)) {
            // Manual attack selection
            when (world.random(3)) {
                0 -> fireAttack(this, target)
                1 -> poisonAttack(target)
                2 -> freezeAttack(target)
                3 -> shockAttack(target)
            }
            
            // Manual hit calculation
            dealHit(target, formula = DragonfireFormula(maxHit = 65))
            
            it.wait(1)  // Tick wait
        }
    }
}
```

**Key Differences**:
- Full combat loop in plugin
- Manual attack selection
- Manual formula calculations
- Manual effect application (poison, freeze)
- Projectile spawning

---

## What We Actually Have (RSMod v2)

### ✅ Already Implemented by Our Team

| Feature | Location | Author |
|---------|----------|--------|
| Drop Table API | `api/drop-table/` | Our team |
| Hill Giant drops | `HillGiantDropTables.kt` | Our team |
| Moss Giant drops | `MossGiantDropTables.kt` | Our team |
| Edgeville Dungeon spawns | `edgeville-dungeon/npcs.toml` | Our team |
| Varrock Sewer spawns | `varrock-sewer/npcs.toml` | Our team |
| NPC Combat Types | `NpcCombatTypes.kt` | Our team |

### ✅ Implemented by RSMod Team

| Feature | Location |
|---------|----------|
| Core engine | `engine/` |
| Protocol handling | `api/net/` |
| Combat formulas | `api/combat/` |
| Cache system | `api/cache/` |
| Inventory system | `api/invtx/` |

---

## Boss Implementation Gap

### Current State

**King Black Dragon** (in RSMod):
```kotlin
// Very minimal - engine handles most
class KingBlackDragon : PluginScript() {
    override fun ScriptContext.startup() {
        onNpcHit(KingBlackDragonNpcs.king_black_dragon) {
            // Dragonfire handled by engine
            // Custom behavior can be added here
        }
    }
}
```

**KBD in AlterRSPS**:
```kotlin
// Full implementation with 4 attack types
class KbdCombatPlugin : KotlinPlugin(...) {
    // Fire, Poison, Freeze, Shock attacks
    // Each with projectiles, formulas, effects
}
```

### What We Need for F2P Bosses

**Obor and Bryophyta are NOT implemented in either codebase.**

We need to choose a pattern:

**Option A: RSMod Pattern (Engine-driven)**
- Define stats in npcs.toml
- Create minimal combat script for special attacks
- Let engine handle basics

**Option B: Alter Pattern (Script-driven)**
- Full combat loop in plugin
- Manual attack selection
- Manual effects
- More control, more code

---

## Recommended Approach

### For F2P Bosses: Hybrid Pattern

Use RSMod's engine for basics, add scripts for specials:

```kotlin
// content/other/bosses/obor/OborPlugin.kt
class OborPlugin : PluginScript() {
    override fun ScriptContext.startup() {
        // Instance creation when player uses giant key
        onItemOnObj(Objs.giant_key, Objs.obor_gate) {
            createInstance(player, "obor")
        }
        
        // Combat - engine handles basics
        onNpcHit(Npcs.obor) {
            // Special: Knockback attack
            if (npc.healthPercent < 50 && chance(1, 4)) {
                player.knockback(3)
            }
        }
        
        // Death - custom drops
        onNpcDeath(Npcs.obor) {
            dropLoot(killer, OborDrops.table)
        }
    }
}
```

---

## Summary Table

| Feature | RSMod v2 | AlterRSPS | What We Should Use |
|---------|----------|-----------|-------------------|
| Combat System | Engine-driven | Script-driven | RSMod (follow existing) |
| Drop Tables | API-based | ? | RSMod (already done) |
| Boss Scripts | Minimal | Full control | Hybrid (special attacks only) |
| NPC Spawns | TOML files | ? | RSMod (already done) |
| Instance System | ? | Manual | Need to check RSMod |

---

## Action Items

### 1. Verify Existing Work
Check which content is already done by our team:
```bash
# Check git history for Hill Giant files
git log --oneline rsmod/content/other/npc-drops/tables/HillGiantDropTables.kt
git log --oneline rsmod/content/areas/dungeons/edgeville-dungeon/npcs.toml
```

### 2. Identify True Gaps
- ❌ Obor boss implementation
- ❌ Bryophyta boss implementation
- ❌ Boss instance system
- ❌ Key → boss access

### 3. Follow Existing Patterns
Since our team already built the drop tables and NPC system:
- **Continue using RSMod patterns**
- Don't rewrite working systems
- Build bosses following existing conventions

### 4. Reference Alter for Complex Mechanics
When implementing boss special attacks:
- Look at Alter's KBD for projectile patterns
- Look at Alter's combat formulas
- Adapt to RSMod's API

---

## Questions for Team

1. **Who built the drop table system?** Was it Gemini/Codex or RSMod team?
2. **Do we have instance support?** For Obor/Bryophyta fights
3. **Should we use Alter patterns for bosses?** Or extend RSMod's engine-driven approach?
4. **Are Hill/Moss Giants already tested?** Do they work in-game?

---

**Status**: Analysis complete  
**Next Step**: Verify what's working vs what's truly missing

