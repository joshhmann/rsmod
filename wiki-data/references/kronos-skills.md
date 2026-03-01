# Kronos Skill Implementation Reference

This document contains extracted skill implementations from the Kronos-184 Java codebase for porting to RSMod v2 (Kotlin).

## Table of Contents

1. [Mining](#mining)
2. [Woodcutting](#woodcutting)
3. [Fishing](#fishing)
4. [Cooking](#cooking)
5. [Firemaking](#firemaking)
6. [Common Patterns](#common-patterns)
7. [Utility Classes](#utility-classes)

---

## Mining

### File Structure
- `Mining.java` - Main skill handler
- `Rock.java` - Rock type definitions (enum)
- `Pickaxe.java` - Tool definitions and selection logic

### Rock Data (Rock.java)

```java
public enum Rock {
    CLAY(434, "clay", 1, 15, 5.0, 3, 74160, 1.0/5),
    COPPER(436, "copper", 1, 50, 17.5, 5, 74160, 1.0/8),
    TIN(438, "tin", 1, 50, 17.5, 5, 74160, 1.0/8),
    IRON(440, "iron", 15, 150, 25.0, 10, 74160, 1.0/4),
    SILVER(442, "silver", 20, 150, 30.0, 15, 74160, 1.0/4),
    COAL(453, "coal", 30, 150, 35.0, 20, 29064, 1.0/9),
    GOLD(444, "gold", 40, 200, 65.0, 20, 29664, 1.0/5),
    MITHRIL(447, "mithril", 55, 225, 75.0, 20, 14832, 1.0/5),
    ADAMANT(449, "adamant", 70, 250, 85.0, 25, 9328, 1.0/4),
    RUNE(451, "rune", 85, 300, 100.0, 30, 4237, 2.0/5),
    AMETHYST(21347, "amethyst", 92, 1000, 246.0, 120, 2500, 1);

    public final int ore, levelReq, difficulty, respawnTime, petOdds;
    public final String rockName;
    public final double experience;
    public final double depleteChance;
}
```

**Rock Parameters:**
- `ore` - Item ID of the mined ore
- `levelReq` - Mining level required
- `difficulty` - Base difficulty for success calculation
- `experience` - XP gained per successful mine
- `respawnTime` - Ticks until rock respawns
- `depleteChance` - Probability of rock depleting (0-1)

### Pickaxe Data (Pickaxe.java)

```java
public enum Pickaxe {
    BRONZE(1, 1265, 5, 625, 6753),
    IRON(1, 1267, 9, 626, 6754),
    STEEL(6, 1269, 14, 627, 6755),
    BLACK(11, 12297, 21, 6108, 3866),
    MITHRIL(21, 1273, 26, 629, 6757),
    ADAMANT(31, 1271, 30, 628, 6756),
    RUNE(41, 1275, 36, 624, 6752),
    DRAGON(61, 11920, 42, 7139, 6758);

    public final int levelReq, id, points, regularAnimationID, crystalAnimationID;
}
```

**Tool Selection Logic:**
```java
public static Pickaxe find(Player player) {
    Pickaxe bestPickaxe = null;
    for(Item item : player.getInventory().getItems())
        bestPickaxe = compare(player, item, bestPickaxe);
    Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
    return compare(player, weapon, bestPickaxe);
}

private static Pickaxe compare(Player player, Item item, Pickaxe best) {
    if(item == null) return best;
    Pickaxe pickaxe = item.getDef().pickaxe;
    if(pickaxe == null) return best;
    if(player.getStats().get(StatType.Mining).fixedLevel < pickaxe.levelReq)
        return best;
    if(best == null) return pickaxe;
    if(pickaxe.levelReq < best.levelReq) return best;
    return pickaxe;
}
```

### Success Formula (Mining.java)

```java
public static double chance(int level, Rock type, Pickaxe pickaxe) {
    double points = ((level - type.levelReq) + 1 + (double) pickaxe.points);
    double denominator = (double) type.difficulty;
    return (Math.min(0.95, points / denominator) * 100);
}
```

**Translation to RSMod v2:**
```kotlin
fun successRate(level: Int, rock: RockType, pickaxe: PickaxeType): Int {
    val points = (level - rock.levelReq) + 1 + pickaxe.points
    val chance = (points.toDouble() / rock.difficulty).coerceAtMost(0.95)
    return (chance * 100).toInt()
}

// Usage with statRandom
val success = statRandom(stats.mining, low, high, invisibleLvls)
```

### Mining Loop Structure

```java
private static void mine(Rock rockData, Player player, GameObject rock, int emptyId) {
    // 1. Check tool
    Pickaxe pickaxe = Pickaxe.find(player);
    if (pickaxe == null) { /* error */ return; }
    
    // 2. Check level requirement
    if (stat.currentLevel < rockData.levelReq) { /* error */ return; }
    
    // 3. Check inventory space
    if (player.getInventory().isFull()) { /* error */ return; }
    
    player.startEvent(event -> {
        int attempts = 0;
        while (true) {
            // Check if rock depleted
            if (rock.id == emptyId) { player.resetAnimation(); return; }
            
            // Check inventory full
            if(player.getInventory().isFull()) { /* exit */ return; }
            
            // First attempt - send message and animate
            if(attempts == 0) {
                player.sendFilteredMessage("You swing your pick at the rock.");
                player.animate(pickaxe.regularAnimationID);
                attempts++;
            } 
            // Success check every 2 ticks (attempts % 2 == 0)
            else if (attempts % 2 == 0 && Random.get(100) <= chance(...)) {
                // Grant ore
                player.getInventory().add(rockData.ore, 1);
                
                // Grant XP
                player.getStats().addXp(StatType.Mining, rockData.experience, true);
                
                // Check depletion
                if (Random.get() < rockData.depleteChance) {
                    player.resetAnimation();
                    World.startEvent(worldEvent -> {
                        rock.setId(emptyId);
                        worldEvent.delay(rockData.respawnTime);
                        rock.setId(rock.originalId);
                    });
                    return;
                }
            }
            
            // Refresh animation every 4 ticks
            if(attempts++ % 4 == 0)
                player.animate(pickaxe.regularAnimationID);
            
            event.delay(1); // Wait 1 tick
        }
    });
}
```

**RSMod v2 Translation:**
```kotlin
private fun ProtectedAccess.mine(loc: BoundLocInfo, type: UnpackedLocType) {
    val pickaxe = findTool(player, objTypes) ?: run {
        mes("You need a pickaxe to mine this rock.")
        return
    }
    
    // First click setup
    if (actionDelay < mapClock) {
        actionDelay = mapClock + 3
        skillAnimDelay = mapClock + 3
        spam("You swing your pick at the rock.")
        opLoc1(loc)
        return
    }
    
    // Refresh animation every 4 ticks
    if (skillAnimDelay <= mapClock) {
        skillAnimDelay = mapClock + 4
        anim(objTypes[pickaxe].toolAnim)
    }
    
    // Success on action tick
    if (actionDelay == mapClock) {
        val (low, high) = successRate(type, pickaxe)
        val gotResource = statRandom(stats.mining, low, high, invisibleLvls)
        
        if (gotResource) {
            // Grant ore and XP
            statAdvance(stats.mining, type.resourceXp)
            invAdd(inv, type.product)
            
            // Check depletion
            if (random.of(1, 255) <= type.depleteChance) {
                locRepo.change(loc, type.depletedLoc, type.respawnTime)
                resetAnim()
                return
            }
        }
        actionDelay = mapClock + 3
    }
    
    opLoc1(loc)
}
```

---

## Woodcutting

### File Structure
- `Woodcutting.java` - Main skill handler
- `Tree.java` - Tree type definitions (enum)
- `Hatchet.java` - Tool definitions and selection logic

### Tree Data (Tree.java)

```java
public enum Tree {
    REGULAR(1511, "logs", 1, 82.5, 25.0, 75, true, 31764, PlayerCounter.CHOPPED_REGULAR),
    OAK(1521, "oak logs", 15, 95, 37.5, 15, false, 36114, PlayerCounter.CHOPPED_OAK),
    WILLOW(1519, "willow logs", 30, 140, 67.5, 10, false, 28928, PlayerCounter.CHOPPED_WILLOW),
    TEAK(6333, "teak logs", 35, 140, 85.0, 10, false, 28928, PlayerCounter.CHOPPED_TEAK),
    MAPLE(1517, "maple logs", 45, 180, 100.0, 60, false, 22191, PlayerCounter.CHOPPED_MAPLE),
    YEW(1515, "yew logs", 60, 225, 175.0, 100, false, 14501, PlayerCounter.CHOPPED_YEW),
    MAGIC(1513, "magic logs", 75, 340, 250.0, 100, false, 7232, PlayerCounter.CHOPPED_MAGIC),
    REDWOOD(19669, "redwood logs", 90, 460, 380.0, 200, false, 6200, PlayerCounter.CHOPPED_REDWOOD);

    public final int log, levelReq, respawnTime, petOdds;
    public final double experience, difficulty;
    public final String treeName;
    public final boolean single; // Single-log trees (regular/achey)
}
```

### Hatchet Data (Hatchet.java)

```java
public enum Hatchet {
    BRONZE(1, 879, 3291, 9),
    IRON(1, 877, 3290, 11),
    STEEL(6, 875, 3289, 14),
    BLACK(6, 873, 3288, 18),
    MITHRIL(21, 871, 3287, 22),
    ADAMANT(31, 869, 3286, 26),
    RUNE(41, 867, 3285, 31),
    DRAGON(61, 2846, 3292, 42),
    INFERNAL(61, 2117, 3292, 45);

    public final int levelReq, animationId, canoeAnimationId, points;
}
```

### Success Formula

Identical pattern to Mining:
```java
private static double chance(int level, Tree type, Hatchet hatchet) {
    double points = ((level - type.levelReq) + 1 + (double) hatchet.points);
    double denominator = type.difficulty;
    return (Math.min(0.95, points / denominator) * 100);
}
```

### Woodcutting Loop

```java
public static void chop(Tree treeData, Player player, Supplier<Boolean> treeDeadCheck, EventConsumer treeDeadAction) {
    Hatchet hatchet = Hatchet.find(player);
    // Level/inventory checks...
    
    player.startEvent(event -> {
        int attempts = 0;
        while (true) {
            if (player.getInventory().isFull()) { /* exit */ }
            if (treeDeadCheck.get()) { /* exit */ }
            
            if (attempts == 0) {
                player.animate(hatchet.animationId);
                player.sendFilteredMessage("You swing your axe at the tree.");
                event.delay(1);
            }
            
            // Check success every 2 ticks
            if (attempts % 2 == 0 && successfullyCutTree(effectiveLevel, treeData, hatchet)) {
                player.getInventory().add(treeData.log, 1);
                player.getStats().addXp(StatType.Woodcutting, treeData.experience, true);
                
                // Tree depletion (random or single-log)
                if (treeData.single || Random.get(10) == 3) {
                    player.resetAnimation();
                    World.startEvent(treeDeadAction);
                    return;
                }
            }
            
            if (attempts++ % 4 == 0)
                player.animate(hatchet.animationId);
            
            event.delay(1);
        }
    });
}
```

**Key difference from Mining:** Trees use random depletion (1/10 chance) instead of fixed depletion chance.

---

## Fishing

### File Structure
- `FishingSpot.java` - Main fishing handler
- `FishingTool.java` - Tool definitions (enum)
- `FishingCatch.java` - Catchable fish definitions (enum)

### Fishing Tool Data

```java
public enum FishingTool {
    SMALL_FISHING_NET(303, 621),
    BIG_FISHING_NET(305, 620),
    FISHING_ROD(307, 313, 622, 623),
    FLY_FISHING_ROD(309, 314, 622, 623),
    LOBSTER_POT(301, 619),
    HARPOON(311, 618);

    public final int id;
    public final int startAnimationId, loopAnimationId;
    public final int secondaryId; // Bait/feather requirement
}
```

### Fishing Catch Data

```java
public enum FishingCatch {
    SHRIMPS(317, 1, 10.0, 0.6, 50000),
    SARDINE(327, 5, 20.0, 0.6, 49000),
    TROUT(335, 20, 50.0, 0.7, 46000),
    LOBSTER(377, 40, 90.0, 0.6, 28000),
    SWORDFISH(371, 50, 100.0, 0.6, 24000),
    MONKFISH(7944, 62, 120.0, 0.6, 24000),
    SHARK(383, 76, 110.0, 0.3, 20000);

    public final int id, levelReq;
    public final double xp, baseChance;
    public final int petOdds;
}
```

### Success Formula (Fishing)

```java
private FishingCatch randomCatch(int level, boolean barehand, FishingTool tool) {
    FishingCatch[] catches = barehand ? barehandCatches : regularCatches;
    double roll = Random.get();
    
    for(int i = catches.length - 1; i >= 0; i--) {
        FishingCatch c = catches[i];
        int levelDifference = level - c.levelReq;
        
        if(levelDifference < 0) continue; // Not high enough level
        
        double chance = c.baseChance;
        if(tool == FishingTool.DRAGON_HARPOON)
            chance += 1.20;
        chance += (double) levelDifference * 0.01;
        
        if(roll > Math.min(chance, 0.90))
            continue; // Failed to catch
            
        return c;
    }
    return null;
}
```

### Fishing Loop

```java
private void fish(Player player, NPC npc) {
    // Tool/level/inventory checks...
    
    player.animate(tool.startAnimationId);
    player.startEvent(event -> {
        int animTicks = 2;
        while(true) {
            // Check if NPC moved
            if(diffX + diffY > 1) { player.resetAnimation(); return; }
            
            if(animTicks > 0) {
                event.delay(1);
                animTicks--;
                continue;
            }
            
            // Attempt catch
            FishingCatch c = randomCatch(fishing.currentLevel, barehand, tool);
            if(c != null) {
                // Remove bait if needed
                if(secondary != null) secondary.incrementAmount(-1);
                
                // Add fish
                player.getInventory().add(c.id, 1);
                player.getStats().addXp(StatType.Fishing, c.xp, true);
            }
            
            // Continue animation
            player.animate(tool.loopAnimationId);
            animTicks = 3;
        }
    });
}
```

---

## Cooking

### File Structure
- `Cooking.java` - Main cooking handler
- `Food.java` - Cookable food definitions (enum)

### Food Data (Food.java)

```java
public enum Food {
    RAW_SHRIMPS(1, 30.0, 317, 315, 7954, "a shrimp", "shrimps", 3, 31, 31, 31),
    RAW_SARDINE(1, 40.0, 327, 325, 369, "a sardine", "sardines", 3, 35, 35, 35),
    RAW_TUNA(30, 100.0, 359, 361, 367, "a tuna", "tunas", 3, 64, 64, 63),
    RAW_LOBSTER(40, 120.0, 377, 379, 381, "a lobster", "lobsters", 3, 74, 74, 64),
    RAW_SHARK(80, 210.0, 383, 385, 387, "a shark", "sharks", 3, 99, 99, 94);

    // Constructor params:
    // levelRequirement, experience, rawID, cookedID, burntID,
    // descriptiveName, itemNamePlural, itemOffset,
    // burnLevelFire, burnLevelRange, burnLevelCookingGauntlets
}
```

### Burn Formula

```java
private static boolean cookedFood(Player player, Food food, Boolean fire) {
    if(food.burntID == -1) return true; // Unburnable
    if (CapePerks.wearsCookingCape(player)) return true;
    
    double burnBonus = 0.0;
    int levelReq = food.levelRequirement;
    int burnStop = getBurnStop(player, food, fire);
    if (!fire) burnBonus = 3.0; // Range bonus
    
    double burnChance = (55.0 - burnBonus);
    double cookingLevel = player.getStats().get(StatType.Cooking).currentLevel;
    double randNum = Random.get() * 100.0;
    
    burnChance -= ((cookingLevel - levelReq) * (burnChance / (burnStop - levelReq)));
    return burnChance <= randNum;
}

private static int getBurnStop(Player player, Food food, Boolean cookingOnRange) {
    Item gloves = player.getEquipment().get(Equipment.SLOT_HANDS);
    if (gloves != null && gloves.getId() == COOKING_GAUNLETS)
        return food.burnLevelCookingGauntlets;
    return cookingOnRange ? food.burnLevelRange : food.burnLevelFire;
}
```

### Cooking Loop

```java
private static void startCooking(Player player, Food food, GameObject obj, int amountToCook, int anim, boolean fire) {
    player.startEvent(e -> {
        int amount = amountToCook;
        while (amount-- > 0) {
            Item rawFood = player.getInventory().findItem(food.rawID);
            if (rawFood == null) break;
            
            player.animate(anim);
            if (cookedFood(player, food, fire)) {
                rawFood.setId(food.cookedID);
                player.getStats().addXp(StatType.Cooking, food.experience, true);
            } else {
                rawFood.setId(food.burntID);
            }
            
            e.delay(4); // 4 ticks per cook
        }
    });
}
```

**RSMod v2 Translation:**
```kotlin
private fun ProtectedAccess.cook(slot: Int, isRange: Boolean) {
    val item = inv[slot] ?: return
    val data = ProcessableItem.fromRaw(item) ?: return
    
    anim(if (isRange) seqs.range_anim else seqs.fire_anim)
    delay(4)
    
    if (inv[slot]?.id != item.id) return
    
    if (didFail(data, isRange)) {
        invReplace(inv, data.rawObj, 1, data.burntObj)
    } else {
        invReplace(inv, data.rawObj, 1, data.cookedObj)
        statAdvance(stats.cooking, data.xp)
    }
}
```

---

## Firemaking

### File Structure
- `Burning.java` - Main firemaking handler (enum-based)

### Log Data

```java
public enum Burning {
    NORMAL(1511, 40.0, 1, 21, 200, 26185, PlayerCounter.NORMAL_LOGS_BURNT),
    OAK(1521, 60.0, 15, 35, 233, 26185, PlayerCounter.OAK_LOGS_BURNT),
    WILLOW(1519, 90.0, 30, 50, 284, 26185, PlayerCounter.WILLOW_LOGS_BURNT),
    MAPLE(1517, 135.0, 45, 65, 350, 26185, PlayerCounter.MAPLE_LOGS_BURNT),
    YEW(1515, 202.5, 60, 80, 500, 26185, PlayerCounter.YEW_LOGS_BURNT),
    MAGIC(1513, 303.8, 75, 95, 550, 26185, PlayerCounter.MAGIC_LOGS_BURNT);

    public final int itemId, levelReq, barbLevelReq, lifeSpan, fireId;
    public final double exp;
}
```

### Light Success Formula

```java
private static double lightChance(Player player, Burning log) {
    int points = 20;
    int level = player.getStats().get(StatType.Firemaking).currentLevel;
    double difference = (level - log.levelReq) * (level > 95 ? 3 : 2);
    return Math.min(100, points + difference);
}
```

### Firemaking Loop

```java
private static void burn(Player player, Item inventoryLog, Burning burning, GroundItem groundItem, int animationId) {
    // Level check...
    // Placement check...
    
    player.startEvent(event -> {
        int attempts = 0;
        
        // Attempt to light with retries
        while (Random.get(100) > lightChance(player, burning)) {
            if (attempts++ % 12 == 0) {
                player.animate(animationId);
                event.delay(2);
            } else {
                event.delay(1);
            }
        }
        
        // Success - create fire
        GameObject fire = new GameObject(burning.fireId, player.getAbsX(), player.getAbsY(), 0, 10, 0);
        player.getStats().addXp(StatType.Firemaking, burning.exp, true);
        createFire(burning, fire);
    });
}

private static void createFire(Burning log, GameObject obj) {
    World.startEvent(event -> {
        GameObject fire = GameObject.spawn(obj.id, obj.x, obj.y, obj.z, obj.type, obj.direction);
        event.delay(log.lifeSpan + Random.get(15));
        fire.remove();
        new GroundItem(592, 1).position(fire.x, fire.y, fire.z).spawn(); // Ashes
    });
}
```

---

## Common Patterns

### 1. Event Loop Pattern

**Kronos Pattern:**
```java
player.startEvent(event -> {
    while (true) {
        // Check exit conditions
        if (shouldExit) { player.resetAnimation(); return; }
        
        // Check success (on specific ticks)
        if (attempts % 2 == 0 && successCheck()) {
            // Grant resource
            // Check depletion
        }
        
        // Refresh animation
        if (attempts++ % 4 == 0) player.animate(animationId);
        
        event.delay(1); // Wait 1 tick
    }
});
```

**RSMod v2 Equivalent:**
```kotlin
private fun ProtectedAccess.gather(loc: BoundLocInfo, type: UnpackedLocType) {
    // First click - setup delay
    if (actionDelay < mapClock) {
        actionDelay = mapClock + 3
        skillAnimDelay = mapClock + 3
        spam("You start...")
        opLoc1(loc)
        return
    }
    
    // Refresh animation
    if (skillAnimDelay <= mapClock) {
        skillAnimDelay = mapClock + 4
        anim(animId)
    }
    
    // Success on action tick
    if (actionDelay == mapClock) {
        val gotResource = statRandom(stats.skill, low, high, invisibleLvls)
        if (gotResource) {
            statAdvance(stats.skill, xp)
            invAdd(inv, product)
        }
        actionDelay = mapClock + 3
    }
    
    opLoc1(loc)
}
```

### 2. Tool Selection Pattern

**Kronos:**
```java
public static Tool find(Player player) {
    Tool best = null;
    // Check inventory
    for(Item item : player.getInventory().getItems())
        best = compare(player, item, best);
    // Check equipment
    Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
    return compare(player, weapon, best);
}
```

**RSMod v2:**
```kotlin
fun findTool(player: Player, objTypes: ObjTypeList): InvObj? {
    val worn = player.righthand?.takeIf { objTypes[it].isUsableTool(player.skillLvl) }
    val carried = player.inv.filterNotNull { objTypes[it].isUsableTool(player.skillLvl) }
        .maxByOrNull { objTypes[it].toolLevelReq }
    return when {
        worn != null && carried != null ->
            if (objTypes[worn].toolLevelReq >= objTypes[carried].toolLevelReq) worn else carried
        else -> worn ?: carried
    }
}
```

### 3. Success Rate Formula

**Pattern:**
```
points = (level - levelReq) + 1 + toolPoints
chance = min(0.95, points / difficulty)
```

**RSMod v2 statRandom:**
```kotlin
// Returns Pair(low, high) for statRandom
fun successRate(resource: UnpackedLocType, tool: UnpackedObjType): Pair<Int, Int> {
    val difficulty = resource.depleteChance.coerceIn(1, 255)
    val bonus = tool.tierBonus()
    val low = (bonus * difficulty / 512).coerceIn(1, 64)
    val high = ((bonus + 24) * difficulty / 384).coerceIn(low + 1, 255)
    return low to high
}
```

---

## Utility Classes

### Random (io.ruin.api.utils.Random)

```java
public class Random {
    public static double get() {
        return ThreadLocalRandom.current().nextDouble();
    }
    
    public static int get(int maxRange) {
        return (int) (get() * (maxRange + 1D));
    }
    
    public static int get(int minRange, int maxRange) {
        return minRange + get(maxRange - minRange);
    }
    
    public static boolean rollDie(int sides, int chance) {
        return get(1, sides) <= chance;
    }
    
    public static boolean rollPercent(int percent) {
        return get() <= (percent * 0.01);
    }
}
```

**RSMod v2 Equivalent:**
```kotlin
// Use injected Random instance
val random: Random

random.of(1, 255)  // Get random int in range
random.of(max)     // Get random int 0..max
random.rollPercent(50)  // 50% chance
```

### Event/Tick Delay

**Kronos:**
```java
player.startEvent(event -> {
    event.delay(4); // Wait 4 ticks
    // Continue...
});
```

**RSMod v2:**
```kotlin
delay(4) // Suspend for 4 ticks
```

### Animation

**Kronos:**
```java
player.animate(animationId);
player.resetAnimation();
```

**RSMod v2:**
```kotlin
anim(seqs.mining_bronze_pickaxe)
resetAnim()
```

### Object Registration

**Kronos:**
```java
ObjectAction.register(objectId, "mine", (player, obj) -> mine(...));
ObjectAction.register(objectId, "prospect", (player, obj) -> prospect(...));
```

**RSMod v2:**
```kotlin
onOpLoc1(content.copper_rocks) { gather(it.loc, it.type) }
onAiConTimer(controllers.rock_respawn) { controller.respawnTick() }
```

---

## RSMod v2 Translation Summary

| Kronos Concept | RSMod v2 Equivalent |
|----------------|---------------------|
| `player.startEvent` | Skill handler with `ProtectedAccess` |
| `event.delay(ticks)` | `delay(ticks)` |
| `player.animate(id)` | `anim(seq)` |
| `player.resetAnimation()` | `resetAnim()` |
| `player.sendMessage(msg)` | `mes(msg)` / `spam(msg)` |
| `player.getInventory().add(id, count)` | `invAdd(inv, obj, count)` |
| `player.getInventory().remove(id, count)` | `invDel(inv, obj, count)` |
| `player.getStats().addXp(skill, xp, true)` | `statAdvance(stats.skill, xp)` |
| `Random.get(max)` | `random.of(1, max)` |
| `World.startEvent` | Controller with `onAiConTimer` |
| `ObjectAction.register` | `onOpLoc1`, `onOpLoc2`, etc. |
| `ItemItemAction.register` | `onOpHeldU` |
| `GameObject.setId(id)` | `locRepo.change(loc, newType, respawnTime)` |

---

## Notes for RSMod v2 Implementation

1. **Resource Depletion**: Use `locRepo.change()` to swap depleted/respawned objects
2. **Controllers**: Handle respawn timing via `onAiConTimer` in controller scripts
3. **Params**: Define resource data in loc params (levelReq, xp, product, depleteChance)
4. **Animations**: Use seq refs from `seqs` object instead of hardcoded IDs
5. **Stat Random**: Use `statRandom(skill, low, high, invisibleLevels)` for success checks
6. **Tick Timing**: Use `actionDelay` and `skillAnimDelay` for proper tick-aligned actions
7. **First Click**: Always handle initial 3-tick delay on first interaction

---

*Extracted from Kronos-184-Fixed repository for RSMod v2 reference*
