# Lumbridge Population Guide (WORLD-1)

**Task ID:** WORLD-1
**Status:** In Progress (claude-main)
**Priority:** Wave 2 — Core Gameplay Mechanics

---

## Goal

Populate Lumbridge with NPCs, interactions, and world content to create a living, functional starting area.

---

## Current Lumbridge State

### Existing NPCs
| NPC | Location | Function | Status |
|-----|----------|----------|--------|
| Duke Horacio | Castle | Quest NPC (Rune Mysteries) | ✅ |
| Cook | Castle kitchen | Quest NPC (Cook's Assistant) | ✅ |
| Hans | Castle grounds | Tutorial/Info | ✅ |
| Bob | Axe shop | Shop + Repair | ✅ |
| Father Aereck | Church | Quest NPC (Restless Ghost) | ✅ |
| Fred the Farmer | Farm | Quest NPC (Sheep Shearer) | ✅ |
| Gillie Groats | Cow field | Dairy cow helper | ✅ |
| General Store Keeper | General store | Shop | ✅ |
| Barfy Bill | River | Canoe tutor | ✅ |
| Millie the Miller | Mill | Windmill info | ✅ |
| Smithing Apprentice | Furnace area | Smithing tutor | ✅ |
| Prayer Tutor | Church | Prayer tutor | ✅ |
| Hatius Cosaintus | Castle courtyard | NPC (info) | ✅ |
| Donie and Gee | Various | Info NPCs | ✅ |

### Existing Shops
| Shop | NPC | Items | Status |
|------|-----|-------|--------|
| Bob's Brilliant Axes | Bob | Bronze/Iron/Steel axes | ✅ |
| Lumbridge General Store | Shop Keeper | Basic supplies | ✅ |

### Missing Content

1. **Guards** — Lumbridge Castle guards (combat level 20ish)
2. **Men/Women** — Generic Lumbridge citizens
3. **Goblins** — Goblin area north of Lumbridge
4. **Giant Rats** — Around the river
5. **Cows** — Already exist but need combat stats
6. **Chickens** — Already exist but need combat stats
7. **Imps** — For Imp Catcher quest
8. **Tramp** — Behind castle
9. **Duke's advisors** — Castle npcs
10. **Bankers** — Bank area NPCs

---

## NPCs to Add/Complete

### Combat NPCs (with aggression)

```kotlin
// Goblins (north of Lumbridge)
edit(npcs.goblin) {
    hitpoints = 5
    attack = 1
    strength = 1
    defence = 1
    attackRange = 1
    respawnRate = 50
    huntRange = 4       // Aggression radius
    giveChase = true
}

// Giant Rats
edit(npcs.giant_rat) {
    hitpoints = 10
    attack = 1
    strength = 1
    defence = 1
    attackRange = 1
    respawnRate = 30
    huntRange = 3
}

// Imps (for Imp Catcher)
edit(npcs.imp) {
    hitpoints = 8
    attack = 1
    strength = 1
    defence = 1
    attackRange = 1
    respawnRate = 60
    // Not aggressive
}
```

### Passive NPCs

```kotlin
// Men and Women (Lumbridge citizens)
edit(npcs.man) {
    hitpoints = 7
    attack = 1
    strength = 1
    defence = 1
    // Not aggressive
}

edit(npcs.woman) {
    hitpoints = 7
    attack = 1
    strength = 1
    defence = 1
    // Not aggressive
}

// Guards
edit(npcs.guard_lumbridge) {
    hitpoints = 20
    attack = 5
    strength = 5
    defence = 5
    attackRange = 1
    respawnRate = 100
    // Not aggressive unless attacked
}
```

---

## NPC Spawns to Add

### Goblin Area (North of Lumbridge)
```
Coordinates: Around x=3225-3245, z=3220-3240, level=0
NPCs: Goblin (10-15 spawns)
```

### Giant Rats (Near River)
```
Coordinates: x=3220-3240, z=3200-3220, level=0
NPCs: Giant Rat (5-8 spawns)
```

### Imps (Scattered around Lumbridge)
```
Locations:
- x=3225, z=3220 (near goblins)
- x=3230, z=3215 (near general store)
- x=3215, z=3225 (near castle)
NPCs: Imp (3-5 spawns)
```

### Citizens (Throughout Lumbridge)
```
Locations:
- Castle courtyard: 2-3 men/women
- Market area: 2-3 men/women
- Near church: 1-2 men/women
```

### Guards
```
Locations:
- Castle entrance: 2 guards
- Castle courtyard: 1-2 guards
- Bank: 1 guard
```

---

## Interactions to Add

### Dialogue Trees

**Men/Women:**
```kotlin
onOpNpc1(npcs.man) { event ->
    startDialogue(event.npc) {
        chatNpc(calm, "Hello there! Nice day for a walk, isn't it?")
        choice2(
            "Yes, it's lovely.", 1,
            "I'm busy.", 2
        )
        // ... more dialogue
    }
}
```

**Guards:**
```kotlin
onOpNpc1(npcs.guard_lumbridge) { event ->
    startDialogue(event.npc) {
        chatNpc(calm, "Halt! What business do you have in the castle?")
        // ... guard dialogue
    }
}
```

### Pickpocketing (Thieving)

```kotlin
// Men/Women are pickpocketable
edit(npcs.man) {
    // ... combat stats
    pickpocket = PickpocketDef(
        levelReq = 1,
        xp = 8.0,
        damageOnFail = 1,
        stunTicks = 8,
        loot = listOf(
            { Item(objs.coins, random(1, 3)) },
        )
    )
}
```

---

## World Objects to Add/Verify

### Furnace
- **Location:** x=3228, z=3256 (already exists?)
- **Function:** Smithing smelting
- **Verify:** Interaction works with Smithing skill

### Anvil
- **Location:** Near furnace
- **Function:** Smithing smithing
- **Verify:** Interaction works

### Bank Booths
- **Location:** x=3208, z=3219 (bank area)
- **Function:** Open bank interface
- **Status:** ✅ Already working

### Cooking Range
- **Location:** x=3218, z=3215 (castle kitchen)
- **Function:** Cooking food
- **Status:** ✅ Already working

### Well
- **Location:** x=3224, z=3212
- **Function:** Fill buckets with water
- **Status:** Need to verify

---

## Drop Tables

### Goblin Drops
```json
{
  "npc": "Goblin",
  "drops": [
    { "id": 526, "name": "Bones", "weight": 1, "guaranteed": true },
    { "id": 995, "name": "Coins", "qty_min": 1, "qty_max": 5, "weight": 4 },
    { "id": 882, "name": "Bronze arrow", "qty_min": 1, "qty_max": 7, "weight": 2 },
    { "id": 1075, "name": "Bronze sq shield", "weight": 1 }
  ]
}
```

### Giant Rat Drops
```json
{
  "npc": "Giant Rat",
  "drops": [
    { "id": 526, "name": "Bones", "weight": 1, "guaranteed": true },
    { "id": 2132, "name": "Raw rat meat", "weight": 1 }
  ]
}
```

---

## Testing Checklist

### NPC Spawns
- [ ] Goblins spawn in correct locations
- [ ] Giant rats spawn near river
- [ ] Imps spawn in scattered locations
- [ ] Citizens spawn in populated areas
- [ ] Guards spawn at castle/bank

### NPC Interactions
- [ ] Citizens have dialogue
- [ ] Guards have dialogue
- [ ] Pickpocketing works on men/women
- [ ] Combat NPCs attack player (when MECH-1 complete)

### World Objects
- [ ] Furnace works for smelting
- [ ] Anvil works for smithing
- [ ] Bank booths open bank
- [ ] Cooking range works
- [ ] Well fills buckets

---

## Files to Create/Modify

### New Files
1. `rsmod/content/areas/city/lumbridge/npcs/LumbridgeCitizens.kt` — Men/Women dialogue
2. `rsmod/content/areas/city/lumbridge/npcs/LumbridgeGuards.kt` — Guard dialogue
3. `rsmod/content/areas/city/lumbridge/configs/LumbridgeNpcSpawns.kt` — Spawn definitions

### Modified Files
1. `rsmod/content/areas/city/lumbridge/LumbridgeScript.kt` — Add new NPC interactions
2. `rsmod/content/generic/generic-npcs/` — Configure combat stats for goblins, rats
3. `rsmod/content/other/npc-drops/` — Add drop tables for new NPCs

---

## Coordination with Other Tasks

- **MECH-1:** NPC aggression needed for goblins/rats to attack player
- **FOOD-1/FOOD-2:** Consumables needed for combat training
- **AGENTBRIDGE-6:** Combat events for testing NPC kills

---

## Reference: Complete NPC List

| NPC Name | Type | Aggressive | Notes |
|----------|------|------------|-------|
| Duke Horacio | Quest | No | Rune Mysteries |
| Cook | Quest/Shop | No | Cook's Assistant |
| Hans | Info | No | Tutorial |
| Bob | Shop | No | Axes + Repair |
| Father Aereck | Quest | No | Restless Ghost |
| Fred the Farmer | Quest | No | Sheep Shearer |
| Gillie Groats | Info | No | Dairy help |
| General Store Keeper | Shop | No | General store |
| Barfy Bill | Info | No | Canoe tutor |
| Millie the Miller | Info | No | Windmill info |
| Smithing Apprentice | Info | No | Smithing tutor |
| Prayer Tutor | Info | No | Prayer tutor |
| Hatius Cosaintus | Info | No | NPC |
| Donie | Info | No | NPC |
| Gee | Info | No | NPC |
| Man | Citizen | No | Pickpocketable |
| Woman | Citizen | No | Pickpocketable |
| Guard | Combat | No | Castle defense |
| Goblin | Combat | Yes (MECH-1) | Low-level mob |
| Giant Rat | Combat | Yes (MECH-1) | Low-level mob |
| Imp | Combat | No | Imp Catcher |
| Cow | Combat | No | Combat drops |
| Chicken | Combat | No | Combat drops |
| Tramp | Info | No | Behind castle |

