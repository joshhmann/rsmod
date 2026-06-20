# Bob's Brilliant Axes — Coverage Verification

> Implemented: 2026-06-19

## Summary

Bob's Brilliant Axes shop in Lumbridge is fully implemented. All components
(NPC ref, NPC spawn, shop inventory, interaction handler) are in place.

## NPC: Bob

| Attribute | Value |
|-----------|-------|
| Symbol | `bob` |
| NPC ID | (from `find("bob")` — rev 233 cache) |
| Spawn | (3232, 3203, 0) — `0_50_50_32_3` |
| Movement | `moveRestrict = indoors` |
| Options | `Trade` (`onOpNpc2`) |

## Shop Config

| Field | Value |
|-------|-------|
| Shop name | "Bob's Brilliant Axes" |
| Inventory ref | `lumbridge_invs.axeshop` |
| Scope | `Shared` |
| Stack | `Always` |
| Restock | Yes |

## Stock

| Item | Count | Restock Cycles |
|------|-------|----------------|
| Bronze pickaxe | 5 | 100 |
| Bronze axe | 10 | 100 |
| Iron axe | 5 | 200 |
| Steel axe | 3 | 400 |
| Iron battleaxe | 5 | 100 |
| Steel battleaxe | 2 | 200 |
| Mithril battleaxe | 1 | 3000 |

## Handler Chain

```
Player right-clicks Bob NPC -> "Trade"
  -> LumbridgeShopHandlers.onOpNpc2(bob)
  -> shops.open(player, npc, "Bob's Brilliant Axes", axeshop)
  -> Shop interface opens with stock from axeshop inventory
```

## Files

| File | Status |
|------|--------|
| `configs/LumbridgeNpcs.kt` | Committed — `val bob = find("bob")` |
| `configs/LumbridgeInvs.kt` | Committed — `axeshop` inventory + stock |
| `map/LumbridgeNpcSpawns.kt` | Committed — `.toml` resource file |
| `resources/.../npcs.toml` | Committed — `bob` spawn at `0_50_50_32_3` |
| `npcs/ShopHandlers.kt` | ✅ Committed — `onOpNpc2(bob) → openBobShop()` |

## Verification Commands

```text
./gradlew :content:areas:city:lumbridge:compileKotlin -> BUILD SUCCESS
0 raw IDs in lumbridge content
```
