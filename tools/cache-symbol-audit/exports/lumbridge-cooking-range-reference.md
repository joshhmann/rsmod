# Lumbridge Cooking Range — Coverage Verification

> Generated: 2026-06-19
> Source: `tools:placed-loc-exporter` (authoritative OpenRS2 name resolution)
> Cooking system: `content/skills/cooking/scripts/Cooking.kt`

## Coverage Summary

Cooking in Lumbridge is fully handled by the existing generic cooking system.
**No Lumbridge-specific handlers are needed.** Both Lumbridge ranges are already
bound in `CookingLocs` and registered for all recipes.

## Verified Ranges

| Symbol | Loc ID | Location | Type | Status |
|--------|--------|----------|------|--------|
| `range` | 26181 | (3230, 3196, 0) | Lumbridge Castle kitchen range | Registered via `cookingRangeLocs` |
| `cooksquestrange` | 114 | (3212, 3215, 0) | Bob's Axes / kitchen cooking range | Registered via `cookingRangeLocs` |

## Supported Recipes (all work on both ranges)

### Fish (21 types)
- Shrimp (raw_shrimp -> shrimp, lvl 1, 30xp)
- Anchovies, Sardine, Mackerel, Trout, Cod, Pike, Salmon, Tuna
- Lobster, Bass, Swordfish, Monkfish, Shark
- Karambwan, Anglerfish, Dark Crab, Manta Ray
- Full burn mechanics, cooking gauntlets support

### Non-Fish Foods (~20)
- Meats: beef, chicken, rat, bear, rabbit, chompy, oomlie, ugthanki
- Breads: bread, pitta
- Pies: redberry, meat, apple
- Cakes: regular cake (range-only)
- Pizzas: plain pizza
- Stews: stew, curry (range-only)
- Wines: jug of wine
- Potatoes: baked potato (range-only)

## Content Group Bindings

Cooking uses `onOpLocU(rangeLoc, rawObj)` pattern — use-item-on-loc.
No `contentGroup` assignments needed; range locs are matched by specific loc type.

## No Lumbridge-Specific Duplication

Confirmed: zero duplicate range handlers in Lumbridge area content.
Lumbridge references to "cook"/"range" are all NPC/quest related (Cook NPC,
cooking tutor, Cook's Assistant dialogue), not cooking interaction logic.

## Verification Commands

```text
./gradlew :content:skills:cooking:compileKotlin -> BUILD SUCCESS
0 raw IDs in cooking content (26181 found in comment-only context)
```
