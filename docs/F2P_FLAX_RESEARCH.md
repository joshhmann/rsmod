# F2P Flax Research - AREA-FLAX Task

**Task ID:** AREA-FLAX  
**Agent:** kimi-world  
**Date:** 2026-02-24  
**Status:** ✅ COMPLETE - Research Concluded

---

## Summary

**Flax picking is NOT available in F2P (Free-to-Play) OSRS.**

All flax fields in OSRS are located in **Members-only (P2P) areas**. F2P players can only obtain flax through trading or the Grand Exchange, and can spin it at Lumbridge Castle's spinning wheel.

---

## Research Findings

### Flax Item Information
- **Item ID:** 1779 (`flax`)
- **Members:** Yes (P2P only)
- **Examine:** "I should use this with a spinning wheel."
- **Value:** 5 coins
- **Use:** Spun into bow strings at a spinning wheel (requires 10 Crafting, gives 15 XP)

### Flax Plant Location
- **Loc ID:** 14896 (`flax`)

### All Flax Field Locations (All P2P)

| Location | Region | Spawns | F2P? |
|----------|--------|--------|------|
| Tree Gnome Stronghold | Kandarin | 68 | ❌ P2P |
| Land's End | Kourend | 5 | ❌ P2P |
| North Hosidius | Kourend | 39 | ❌ P2P |
| Lunar Isle | Fremennik | 25 | ❌ P2P |
| South of Rellekka | Fremennik | 20 | ❌ P2P |
| Prifddinas | Tirannwn | 26 | ❌ P2P |
| Sorcerer's Tower | Kandarin | 3 | ❌ P2P |
| **Seers' Village** | **Kandarin** | **48** | ❌ **P2P** |
| Taverley | Asgarnia | 3 | ❌ P2P |
| Lletya | Tirannwn | 10 | ❌ P2P |
| Isle of Souls | Misthalin | 13 | ❌ P2P |
| Quetzacalli Gorge | Varlamore | 7 | ❌ P2P |
| Nemus Retreat | Varlamore | 17 | ❌ P2P |

### What F2P Players CAN Do With Flax

1. **Trade for flax** on the Grand Exchange
2. **Spin flax** at the spinning wheel in **Lumbridge Castle** (2nd floor)
3. **Create bow strings** (10 Crafting required, 15 XP per flax)

### What F2P Players CANNOT Do

1. **Pick flax** from any field (all fields are in P2P areas)
2. Access any flax field location

---

## RSMod Implementation Status

### Already Implemented
- ✅ Flax item (obj ID 1779)
- ✅ Flax plant location (loc ID 14896)
- ✅ Pickable crop system (`rsmod/content/generic/generic-locs/pickables/`)
- ✅ Spinning wheel functionality (Crafting module)

### Not Required for F2P
- ❌ No F2P flax fields to implement (none exist in OSRS)

---

## Conclusion

**No implementation work is required for F2P flax content** because:

1. Flax picking is a **P2P-only activity**
2. There are **no F2P-accessible flax fields** in OSRS
3. The existing pickable system already supports flax if P2P areas are implemented later

When P2P areas are implemented (e.g., Seers' Village, Tree Gnome Stronghold), the flax fields can be added using the existing `Pickable` system with the following configuration:

```kotlin
// Example flax configuration (for future P2P implementation)
edit(pickable_locs.flax) {
    contentGroup = content.pickable_crop
    param[params.game_message] = "You pick some flax."
    param[params.game_message2] = "You can't carry any more flax."
    param[params.rewarditem] = objs.flax  // ID 1779
    param[params.respawn_time] = 50  // Approximate respawn time
}
```

---

## References

- [OSRS Wiki - Flax](https://oldschool.runescape.wiki/w/Flax)
- [OSRS Wiki - Flax field](https://oldschool.runescape.wiki/w/Flax_field)
- RSMod Symbols: `rsmod/.data/symbols/obj.sym` (line 1780: `1779	flax`)
- RSMod Symbols: `rsmod/.data/symbols/loc.sym` (line 14896: `14896	flax`)

