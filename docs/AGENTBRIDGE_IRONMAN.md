# AgentBridge Ironman Mode

Ironman mode enforces legitimate gameplay for bot automation, ensuring bots behave like real players.

## Overview

In **Ironman Mode**:
- ❌ No teleporting (must walk everywhere)
- ❌ No spawning items (must earn them)
- ✅ All porcelain actions available
- ✅ Banking, shops, trading
- ✅ Real skilling and combat

In **Test Mode** (default):
- ✅ All commands available
- ✅ Teleport, spawn items for rapid testing
- ✅ Quick iteration during development

## Quick Start

### Enable Ironman Mode

```kotlin
// In AgentBridgeScript or config
ironmanMode.toggle(true)
```

### Ironman Bot Example

```typescript
// bots/ironman-woodcutting.ts
export default async function(bot) {
  // Start with nothing - earn your axe!
  await bot.talkTo("Bob");  // Talk to Bob in Lumbridge
  
  // Walk to general store (no teleport!)
  await bot.openShop("Lumbridge General Store");
  await bot.buyByName("Bronze axe", 1);
  await bot.closeShop();
  
  // Walk to trees
  await bot.chopTree("Tree");
  
  // Bank the logs
  await bot.openBank();
  await bot.depositAll("logs");
  await bot.closeBank();
}
```

## Blocked Actions

| Action | Ironman | Test Mode | Alternative |
|--------|---------|-----------|-------------|
| `teleport()` | ❌ | ✅ | `walkWithDoors()` |
| `spawn_item()` | ❌ | ✅ | Earn via gameplay |
| `clear_inventory()` | ❌ | ✅ | Drop items manually |
| `ensure_item()` | ❌ | ✅ | Buy/craft/obtain |

## Porcelain Actions (Always Allowed)

All porcelain actions work in ironman mode because they simulate real player behavior:

- `chopTree()`, `burnLogs()` - Woodcutting/Firemaking
- `openBank()`, `deposit()`, `withdraw()` - Banking
- `openShop()`, `buyByName()`, `sellToShop()` - Shopping
- `pickupNearest()`, `lootArea()` - Looting
- `activatePrayer()` - Prayer

## Migration Guide

### Porting from Test Mode to Ironman

**Before (Test Mode):**
```typescript
await bot.teleport(3222, 3218);  // Instant travel
await bot.spawnItem(1351, 1);    // Free axe
await bot.chopTree("Tree");
```

**After (Ironman):**
```typescript
// Walk to location (door-aware!)
await bot.walkWithDoors(3222, 3218);

// Earn your axe - buy from shop
await bot.openShop("Lumbridge General Store");
await bot.buyByName("Bronze axe", 1);
await bot.closeShop();

// Now chop trees legitimately
await bot.chopTree("Tree");
```

## Best Practices

1. **Start simple** - Begin with tutorial island progression
2. **Use walkWithDoors()** - It handles doors automatically
3. **Check inventory** - Use `hasItem()` before assuming you have tools
4. **Plan routes** - Use `findPath()` to check routes before walking
5. **Handle failures** - Ironman mode can fail (out of money, no stock, etc.)

## Example: Complete Tutorial Island

See `bots/ironman-tutorial.ts` for a complete ironman bot that goes from tutorial island to Lumbridge with legitimate gameplay.

## Example: Money Making

See `bots/ironman-woodcutting.ts` for a money-making bot that:
1. Starts with no money
2. Sells starting items for seed money
3. Buys a bronze axe
4. Chops and banks logs
5. Sells logs for profit

## Troubleshooting

**"Teleport is disabled in ironman mode"**
→ Use `await bot.walkWithDoors(x, z)` instead

**"Spawning items is disabled in ironman mode"**
→ Buy from shops, craft, or obtain through skilling

**Bot is stuck at a door**
→ The door system auto-handles most doors. Check `docs/AGENTBRIDGE_DOORS.md`

## See Also

- `docs/AGENTBRIDGE_PORCELAIN.md` - Full porcelain action reference
- `bots/ironman-*.ts` - Example ironman bot scripts
- `docs/LLM_TESTING_GUIDE.md` - General testing guide

