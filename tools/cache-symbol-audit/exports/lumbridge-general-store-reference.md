# Lumbridge General Store — Coverage Verification

> Verified: 2026-06-19

## Summary

Lumbridge General Store is fully implemented. Shopkeeper, assistant NPCs,
stock inventory, and trade handlers are all committed.

## NPCs

| NPC | Symbol | Spawn | Shop ref |
|-----|--------|-------|----------|
| Shop keeper | `generalshopkeeper1` | (3209, 3247, 0) — `0_50_50_9_47` | `lumbridge_invs.generalshop1` |
| Shop assistant | `generalassistant1` | (3212, 3247, 0) — `0_50_50_12_47` | `lumbridge_invs.generalshop1` |

Both have `contentGroup = content.shop_keeper` / `content.shop_assistant`
and `moveRestrict = indoors`.

## Handler Chain (from `ShopHandlers.kt`)

```
Player right-clicks shop keeper/assistant -> "Trade"
  -> LumbridgeShopHandlers.onOpNpc2(...)
  -> shops.open(player, npc, "Lumbridge General Store", generalshop1)
  -> Shop interface opens with stock from generalshop1 inventory
```

## Stock (from `LumbridgeInvs.kt`)

| Item | Count | Restock Cycles |
|------|-------|----------------|
| Pot (empty) | 5 | 10 |
| Jug (empty) | 2 | 100 |
| Jug pack | 5 | 20 |
| Shears | 2 | 100 |
| Knife | 5 | 100 |
| Bucket (empty) | 3 | 10 |
| Bucket pack | 15 | 10 |
| Bowl (empty) | 2 | 50 |
| Cake tin | 2 | 50 |
| Tinderbox | 3 | 100 |
| Chisel | 2 | 100 |
| Hammer | 5 | 100 |
| Newcomer map | 5 | 100 |
| Security book | 5 | 100 |
| Rope | 5 | 100 |

All items use symbolic `objs.*` references.

## Files

| File | Status |
|------|--------|
| `configs/LumbridgeNpcs.kt` | Committed — NPC refs + content groups |
| `configs/LumbridgeInvs.kt` | Committed — `generalshop1` + stock |
| `npcs/ShopHandlers.kt` | Committed — `onOpNpc2` handlers |
| `resources/.../npcs.toml` | Committed — spawns at (3209, 3247, 0) and (3212, 3247, 0) |

## Verification

```text
./gradlew :content:areas:city:lumbridge:compileKotlin -> BUILD SUCCESS
0 raw IDs
```
