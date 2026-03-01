# True OSRS Emulation Guide

**Source:** [Rune-Server Thread](https://rune-server.org/threads/so-you-think-your-server-is-an-emulation.706150/)  
**Project:** 2004scape (Lost City)  
**Topic:** What separates true emulation from custom implementations

---

## Overview

This guide explains the fundamental interaction system in OSRS that most private servers get wrong. Understanding OP vs AP interactions is crucial for authentic gameplay mechanics.

---

## OP vs AP System

OSRS uses two distinct interaction modes when dealing with entities:

### OP - Operate/Operable

**Use Cases:**
- Melee combat
- Talking to NPCs
- Opening doors
- Picking up ground items

**Requirements:**
- Must have direct **Line of Walk** to the target
- Must be within operable distance

**Visual Representation:**
```
Green tiles = valid OP positions
Player must be able to walk directly to target

    [G][G][G]
    [G][NPC][G]
    [G][G][G]
```

**Code Reference (2004scape TypeScript):**
```typescript
inOperableDistance(target: Entity): boolean {
    if (target.level !== this.level) {
        return false;
    }
    if (target instanceof PathingEntity) {
        // Targetting an Npc or Player
        return reached(this.level, this.x, this.z, target.x, target.z, 
            target.width, target.length, this.width, target.orientation, -2);
    } else if (target instanceof Loc) {
        // Targetting a Loc (object)
        const forceapproach = LocType.get(target.type).forceapproach;
        return reached(this.level, this.x, this.z, target.x, target.z,
            target.width, target.length, this.width, target.angle, target.shape, forceapproach);
    }
    // Targetting an Obj (ground item)
    const shape = isFlagged(target.x, target.z, target.level, CollisionFlag.WALK_BLOCKED) ? -2 : -1;
    return reached(this.level, this.x, this.z, target.x, target.z,
        target.width, target.length, this.width, 0, shape);
}
```

**RuneScript Example (NPC Shop):**
```runescript
[opnpc1,obli]
~chatnpc(default, "Welcome to Obli's General Store Bwana!|Would you like to see my items?");
def_int $option = ~p_choice2("Yes please!", 1, "No, but thanks for the offer.", 2);

switch_int ($option) {
    case 1:
    ~chatplayer(happy, "Yes please!");
    ~openshop_activenpc;
    case 2:
    ~chatplayer(default, "No, but thanks for the offer.");
}
```

---

### AP - Approach/Approachable

**Use Cases:**
- Ranged combat
- Magic combat
- Talking to NPCs behind obstacles (bankers)
- Any interaction requiring line of sight but not walk access

**Requirements:**
- Must have direct **Line of Sight** to the target
- Must be within specified AP range
- Can interact through obstacles (fences, counters, etc.)

**Visual Representation:**
```
Green tiles = valid AP positions (range=10 default)
Can interact through obstacles

    [G][G][G][G][G]
    [G][G][G][G][G]
    [G][G][FENCE][G][G]
    [G][G][G][G][G]
    [G][G][NPC][G][G]
```

**Code Reference (2004scape TypeScript):**
```typescript
inApproachDistance(range: number, target: Entity): boolean {
    if (target.level !== this.level) {
        return false;
    }
    if (target instanceof PathingEntity && Position.intersects(
        this.x, this.z, this.width, this.length, 
        target.x, target.z, target.width, target.length)) {
        // Pathing entity has -2 shape for AP
        // Cannot be underneath target for AP interactions
        return false;
    }
    return Position.distanceTo(this, target) <= range && 
        hasLineOfSight(this.level, this.x, this.z, target.x, target.z,
            this.width, target.width, target.length, CollisionFlag.PLAYER);
}
```

**RuneScript Example (Bank Teller):**
```runescript
[apnpc1,_bank_teller]
if (npc_range(coord) > 2) {
    p_aprange(2);
    return;
}
@talk_to_banker;
```

**Note:** All AP interactions start with `aprange = 10`, then content scripts can modify the range and re-trigger.

---

## AP to OP Switching

One of the most nuanced aspects of OSRS combat - AP and OP triggers can coexist for the same interaction.

### Example: Attack Option (op2) on Cows

**Scenario Flow:**

1. **AP Trigger Runs First**
   - AP trigger checks if NPC is already in combat
   - Sends "Someone else is fighting that" message
   - This is necessary for Ranged/Magic combat

2. **Combat Style Check**
   - If wearing melee weapon → `aprange = 0`
   - Interaction converts to OP

3. **OP Trigger Runs**
   - Upon arriving within melee distance
   - Sends combat message again
   - Melee combat proceeds

**Visual Flow:**
```
Player clicks Attack on Cow (10 tiles away)
         |
         v
AP Trigger (range=10) executes
- Checks if cow in combat
- Sends message if blocked
         |
         v
Melee weapon detected? → Yes
- Set aprange = 0
- Convert to OP
         |
         v
Player walks to cow
         |
         v
OP Trigger executes
- Combat begins
```

---

## RSMod Implementation Notes

### Current State in RSMod

RSMod handles interactions through:
- `OpNpc` / `OpLoc` / `OpObj` - Standard interactions
- `ApNpc` / `ApLoc` / `ApObj` - Approach interactions

### Key Differences to Implement

1. **Collision Detection**
   ```kotlin
   // OP requires Line of Walk
   fun hasLineOfWalk(level: Int, x: Int, z: Int, tx: Int, tz: Int): Boolean
   
   // AP requires Line of Sight
   fun hasLineOfSight(level: Int, x: Int, z: Int, tx: Int, tz: Int): Boolean
   ```

2. **Dynamic AP Range**
   ```kotlin
   // Content script should be able to set AP range
   player.setApRange(range: Int)
   ```

3. **Trigger Priority**
   ```kotlin
   // AP triggers should execute before OP
   // If AP returns early (range check), OP pathing should begin
   ```

---

## Common Emulation Mistakes

### 1. Skipping AP Triggers
```kotlin
// WRONG: Going directly to OP
onOpNpc2(npcId) {
    player.attack(npc)  // This bypasses AP checks!
}

// CORRECT: AP then OP flow
onApNpc2(npcId) {
    if (npc.inCombat()) {
        player.message("Someone else is fighting that.")
        return
    }
    if (player.combatStyle == MELEE) {
        player.setApRange(0)  // Force OP
        return  // Will re-trigger as OP
    }
    player.attackRanged(npc)
}

onOpNpc2(npcId) {
    player.attackMelee(npc)
}
```

### 2. Incorrect Collision Checks
```kotlin
// WRONG: Using distance only
if (player.tile.distanceTo(target) <= 1) {
    interact()
}

// CORRECT: Check line of walk
if (player.inOperableDistance(target)) {
    interact()
}
```

### 3. Missing AP Range Modification
```kotlin
// WRONG: Hardcoded ranges
val maxRange = 10

// CORRECT: Dynamic from script
var apRange = 10  // Default
// Content script can modify with p_aprange
```

---

## Testing Your Emulation

### Test Cases

1. **Melee Through Wall**
   - Try to attack NPC through wall
   - Should fail - no line of walk
   - Should path around wall first

2. **Ranged Through Fence**
   - Try ranged attack through fence
   - Should succeed - has line of sight
   - AP trigger should fire

3. **Banker Through Counter**
   - Talk to banker behind counter
   - Should work through counter
   - AP distance check should apply

4. **AP to OP Switch**
   - Attack monster 10 tiles away with melee
   - Should get "Someone else fighting" message at range
   - Should get same message when arriving (OP trigger)

---

## References

- **2004scape Source:** https://github.com/2004scape/Server
- **Original Thread:** https://rune-server.org/threads/so-you-think-your-server-is-an-emulation.706150/
- **RuneScript Documentation:** See 2004scape GitHub

---

## Summary

True OSRS emulation requires:
1. ✅ Separate OP and AP interaction systems
2. ✅ Line of Walk checks for OP
3. ✅ Line of Sight checks for AP
4. ✅ Dynamic AP range modification
5. ✅ AP trigger priority over OP
6. ✅ AP to OP switching for melee combat

Most private servers implement only distance checks, missing these nuanced mechanics that make OSRS feel authentic.

