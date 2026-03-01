# AgentBridge Porcelain Actions Reference

Complete reference for all high-level bot actions.

## Movement

### walkWithDoors(x, z, tolerance?)
Walk to destination, automatically opening doors along the way.
```typescript
await bot.walkWithDoors(3222, 3218);
await bot.walkWithDoors(3222, 3218, 2);  // 2 tile tolerance
```

### findPath(x, z)
Find path to destination without walking.
```typescript
const path = await bot.findPath(3222, 3218);
console.log(`Path has ${path.waypoints.length} waypoints`);
```

## Woodcutting

### chopTree(targetName?, maxLogs?)
Find tree, walk to it, chop, wait for logs.
```typescript
await bot.chopTree();           // Any tree
await bot.chopTree("oak");      // Oak specifically
await bot.chopTree("willow", 5);  // Chop 5 willow logs
```

## Firemaking

### burnLogs(logType?, count?)
Use tinderbox on logs.
```typescript
await bot.burnLogs();           // Burn first available logs
await bot.burnLogs("oak logs"); // Burn oak logs specifically
await bot.burnLogs("logs", 10); // Burn 10 logs
```

## Banking

### openBank(bankName?)
Walk to nearest bank (or specific bank) and open.
```typescript
await bot.openBank();                    // Nearest bank
await bot.openBank("Varrock West Bank"); // Specific bank
```

### deposit(slot, amount)
Deposit items from inventory.
```typescript
await bot.deposit(0, 28);     // Deposit slot 0 (all)
await bot.deposit(1, 10);     // Deposit 10 from slot 1
```

### depositAll(pattern?)
Deposit all items matching pattern.
```typescript
await bot.depositAll();           // Deposit everything
await bot.depositAll("logs");     // Deposit all logs
await bot.depositAll("raw", 5);   // Deposit all "raw" items, keep 5
```

### withdrawByName(itemName, amount)
Withdraw item by name.
```typescript
await bot.withdrawByName("Coins", 1000);
await bot.withdrawByName("Logs", 28);
```

## Shopping

### openShop(shopkeeperName?)
Open shop interface.
```typescript
await bot.openShop();              // Nearest shop
await bot.openShop("Shopkeeper");  // Specific shopkeeper
```

### buyByName(itemName, amount)
Buy item from shop.
```typescript
await bot.buyByName("Pot", 5);
await bot.buyByName("Bronze axe", 1);
```

### sellToShop(slot, amount)
Sell inventory item to shop.
```typescript
await bot.sellToShop(0, 1);  // Sell 1 of slot 0
```

### sellAll(pattern)
Sell all matching items.
```typescript
await bot.sellAll("logs");
await bot.sellAll("raw fish");
```

## Ground Items

### scanGroundItems(radius?, pattern?)
Scan for items on the ground.
```typescript
await bot.scanGroundItems();              // Default 10 tile radius
await bot.scanGroundItems(20);            // 20 tile radius
await bot.scanGroundItems(10, "Coins");   // Only coins
```

### pickupNearest(pattern)
Pick up nearest matching item.
```typescript
await bot.pickupNearest("Coins");
await bot.pickupNearest("Bones");
```

### lootArea(radius, patterns)
Loot all matching items in area.
```typescript
await bot.lootArea(5, ["Coins", "Bones"]);
await bot.lootArea(10, []);  // Loot everything
```

## Prayer

### activatePrayer(prayerName)
Activate specific prayer.
```typescript
await bot.activatePrayer("PROTECT_FROM_MELEE");
await bot.activatePrayer("ULTIMATE_STRENGTH");
```

### activateBestCombatPrayer(type)
Auto-select best prayer.
```typescript
await bot.activateBestCombatPrayer("MELEE");      // Strength prayer
await bot.activateBestCombatPrayer("DEFENSE");    // Defense prayer
await bot.activateBestCombatPrayer("PROTECTION"); // Protection prayer
```

### deactivateAllPrayers()
Turn off all prayers.
```typescript
await bot.deactivateAllPrayers();
```

## NPC Interaction

### talkTo(npcName)
Walk to NPC and talk.
```typescript
await bot.talkTo("Lumbridge Guide");
await bot.talkTo("Banker");
```

## Complete Action List

| Action | Ironman | Description |
|--------|---------|-------------|
| walk | ✅ | Basic walk |
| walkWithDoors | ✅ | Walk with door handling |
| teleport | ❌ | Instant teleport (test only) |
| chopTree | ✅ | Woodcutting |
| burnLogs | ✅ | Firemaking |
| openBank | ✅ | Bank access |
| deposit | ✅ | Deposit items |
| withdrawByName | ✅ | Withdraw items |
| openShop | ✅ | Shop access |
| buyByName | ✅ | Buy items |
| sellAll | ✅ | Sell items |
| scanGroundItems | ✅ | Item scanning |
| pickupNearest | ✅ | Pick up items |
| lootArea | ✅ | Area looting |
| activatePrayer | ✅ | Prayer activation |
| talkTo | ✅ | NPC dialog |
| spawnItem | ❌ | Item spawning (test only) |
| clearInventory | ❌ | Clear inventory (test only) |

