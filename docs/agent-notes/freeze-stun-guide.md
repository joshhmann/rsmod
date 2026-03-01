# Freeze and Stun Mechanics Implementation Guide (MECH-2)

**Task ID:** MECH-2
**Status:** In Progress (codex)
**Priority:** Wave 2 — Core Gameplay Mechanics

---

## OSRS Freeze/Stun Behavior Specification

### Freeze (Binding Spells)
- **Effect:** Player/NPC cannot move for a duration
- **Sources:** Ice spells (Ancient Magicks), binding spells, special attacks
- **Duration:** Varies by spell (5-20 seconds typically)
- **Actions allowed:** Attack, eat, drink potions, teleport
- **Actions blocked:** Walk, run

### Stun
- **Effect:** Player/NPC cannot perform any actions
- **Sources:** Dragon spear special, Zealots' equipment effect, NPC attacks
- **Duration:** Typically shorter than freeze (2-6 seconds)
- **Actions allowed:** None (except auto-retaliate in some cases)
- **Actions blocked:** All actions including attack, eat, move

### Key Differences
| Property | Freeze | Stun |
|----------|--------|------|
| Can move | No | No |
| Can attack | Yes | No |
| Can eat/drink | Yes | No |
| Can teleport | Yes | No |
| Breaks on damage | No (most spells) | No |
| Stackable | No (refreshes duration) | No |

---

## Current RSMod v2 Implementation Status

### Existing Infrastructure

1. **Freeze resistance param** exists in engine:
   - NPCs can have a freeze immunity/resistance
   - Need to locate exact param name in cache

2. **Movement queue system** (`api/player/queue/`):
   - `PlayerMove` actions can be blocked
   - `canMove` checks exist

3. **Action queue system**:
   - Players have action queues that can be interrupted
   - Stun should clear/prevent action queue

### What's Missing

1. **Freeze duration tracking** — No player attribute for "frozen until tick X"
2. **Stun duration tracking** — No player attribute for "stunned until tick X"
3. **Movement blocking** — Freeze should prevent walk/run commands
4. **Action blocking** — Stun should prevent all actions
5. **Visual indicators** — Frozen/stunned overlay graphics

---

## Implementation Plan

### Phase 1: Player State Attributes

```kotlin
// In BaseVarbits.kt or custom varbits
val frozen_until = find("frozen_until")      // Tick when freeze expires (0 = not frozen)
val stunned_until = find("stunned_until")    // Tick when stun expires (0 = not stunned)
val freeze_immunity = find("freeze_immunity") // Ticks of immunity after freeze ends

// Or use player attributes if varbits not available
// player.attr[frozenUntil] = mapClock + durationTicks
```

### Phase 2: Freeze Implementation

```kotlin
// FreezeScript.kt

class FreezeScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // Intercept movement requests
        onPlayerMove { event ->
            if (isFrozen(player)) {
                mes("A magical force stops you from moving.")
                event.cancel()
                return@onPlayerMove
            }
        }
    }
    
    fun freeze(player: Player, durationTicks: Int) {
        if (player.hasAttribute(freeze_immunity)) {
            return  // Immune
        }
        
        val currentFreeze = player.attr[frozenUntil] ?: 0
        val newFreezeEnd = mapClock + durationTicks
        
        // Only refresh if new duration is longer
        if (newFreezeEnd > currentFreeze) {
            player.attr[frozenUntil] = newFreezeEnd
            
            // Apply graphics
            player.graphic(spotanims.ice_barrage_freeze)
            
            // Schedule unfreeze
            player.timer(timers.freeze_expire, durationTicks)
        }
    }
    
    fun isFrozen(player: Player): Boolean {
        val frozenUntil = player.attr[frozenUntil] ?: 0
        return mapClock < frozenUntil
    }
    
    fun unfreeze(player: Player) {
        player.attr[frozenUntil] = 0
        // Apply brief immunity
        player.attr[freeze_immunity] = mapClock + 5  // 3 second immunity
    }
}

// Timer handler
onPlayerTimer(timers.freeze_expire) { event ->
    unfreeze(player)
    mes("The ice begins to melt...")
}
```

### Phase 3: Stun Implementation

```kotlin
// StunScript.kt

class StunScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // Intercept ALL player actions
        onPlayerAction { event ->
            if (isStunned(player)) {
                mes("You're stunned!")
                event.cancel()
                return@onPlayerAction
            }
        }
        
        // Intercept movement
        onPlayerMove { event ->
            if (isStunned(player)) {
                event.cancel()
            }
        }
        
        // Intercept NPC interactions
        onOpNpc1 { event ->
            if (isStunned(player)) {
                mes("You're stunned!")
                return@onOpNpc1
            }
        }
    }
    
    fun stun(player: Player, durationTicks: Int) {
        val currentStun = player.attr[stunnedUntil] ?: 0
        val newStunEnd = mapClock + durationTicks
        
        if (newStunEnd > currentStun) {
            player.attr[stunnedUntil] = newStunEnd
            
            // Apply graphics
            player.graphic(spotanims.stun_stars)
            
            // Clear current action queue
            player.clearActions()
            
            // Schedule stun end
            player.timer(timers.stun_expire, durationTicks)
        }
    }
    
    fun isStunned(player: Player): Boolean {
        val stunnedUntil = player.attr[stunnedUntil] ?: 0
        return mapClock < stunnedUntil
    }
}
```

### Phase 4: Freeze Spell Integration

```kotlin
// In spell attack scripts (Ice Rush, Ice Burst, Ice Blitz, Ice Barrage)

private data class IceSpell(
    val name: String,
    val freezeDuration: Int,  // In ticks
    val damage: Int,
    val maxHit: Int,
)

private val ICE_SPELLS = listOf(
    IceSpell("Ice Rush", freezeDuration = 8, damage = 16, maxHit = 22),   // 5 sec
    IceSpell("Ice Burst", freezeDuration = 16, damage = 22, maxHit = 28), // 10 sec
    IceSpell("Ice Blitz", freezeDuration = 25, damage = 26, maxHit = 34), // 15 sec
    IceSpell("Ice Barrage", freezeDuration = 33, damage = 30, maxHit = 42), // 20 sec
)

// In spell cast handler
fun castIceSpell(target: Player, spell: IceSpell) {
    val damage = calculateMagicDamage(player, spell)
    target.dealDamage(damage)
    
    if (damage > 0) {
        freeze(target, spell.freezeDuration)
    }
}
```

### Phase 5: NPC Stun Attack Integration

```kotlin
// Example: Dragon spear special attack stuns player

class DragonSpearSpecial @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onSpecialAttack(objs.dragon_spear) { target ->
            stun(target, durationTicks = 6)  // 3.6 seconds
            target.dealDamage(calculateDamage())
        }
    }
}
```

---

## Testing Checklist

### Unit Tests
- [ ] Frozen player cannot move
- [ ] Frozen player can still attack
- [ ] Frozen player can eat food
- [ ] Stunned player cannot move
- [ ] Stunned player cannot attack
- [ ] Stunned player cannot eat
- [ ] Freeze duration refreshes if longer
- [ ] Freeze immunity after unfreeze works
- [ ] Stun duration does not stack

### Integration Tests (Bot Scripts)
```typescript
// bots/freeze-stun-test.ts

// Test freeze
await actions.castSpellOnPlayer("Ice Barrage", targetPlayer);
await bot.delay(10);
const canMove = await sdk.canMove();
console.log(`Frozen player can move: ${canMove}`); // Should be false

// Test stun
await actions.useSpecialAttack("Dragon Spear", targetPlayer);
await bot.delay(5);
const canAttack = await sdk.canAttack();
console.log(`Stunned player can attack: ${canAttack}`); // Should be false
```

---

## Files to Create/Modify

### New Files
1. `rsmod/content/mechanics/freeze/scripts/FreezeScript.kt`
2. `rsmod/content/mechanics/stun/scripts/StunScript.kt`
3. `rsmod/content/mechanics/freeze/configs/FreezeSpotanims.kt`
4. `rsmod/content/mechanics/stun/configs/StunSpotanims.kt`

### Modified Files
1. `rsmod/api/config/src/.../refs/BaseVarbits.kt` — Add freeze/stun varbits
2. `rsmod/api/config/src/.../refs/BaseTimers.kt` — Add freeze/stun timers
3. `rsmod/content/skills/magic/spell-attacks/` — Integrate freeze into ice spells

---

## Spotanim IDs (Verify in spotanims.sym)

| Effect | Symbol Name | ID (approximate) |
|--------|-------------|------------------|
| Ice freeze | ice_barrage_freeze | 369 |
| Ice burst | ice_burst_freeze | 363 |
| Stun stars | stun_stars | 245 |

---

## Notes

- Freeze duration displayed to player via icon
- Antifire potions reduce freeze duration from dragonfire
- Some NPCs are immune to freeze (check NpcType params)
- Stun immunity exists for some NPCs
- Consider "Diminishing Returns" for PvP (not applicable for F2P baseline)

