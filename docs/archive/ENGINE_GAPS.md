# RSMod v2 Engine & Data Gaps

Gaps discovered during content porting. Each entry has: what's missing, what needs adding, and which content is blocked.

Do NOT work around these — fix the underlying gap first.

---

## Missing Seq (Animation) Refs — BaseSeqs.kt

| Gap | OSRS Cache Name | Status |
|-----|----------------|--------|
| Fire lighting animation | `human_firemaking` | ✅ Added to BaseSeqs.kt — Firemaking.kt updated to use `seqs.human_firemaking` |

---

## Missing Obj (Item) Refs — BaseObjs.kt

| Gap | OSRS Item Name | Status |
|-----|----------------|--------|
| Ashes | `ashes` | ✅ Added to BaseObjs.kt — Firemaking ash drop uncommented |
| Blisterwood logs | `blisterwood_logs` | ✅ Added to BaseObjs.kt — Firemaking LOG_DEFS entry added (level 76, 96 XP) |

---

## Loc Refs — No BaseLocs.kt (workaround available)

RSMod v2 has no `BaseLocs.kt`. **Workaround:** use a local `LocReferences` subclass inside the plugin module (confirmed working by Cooking plugin):

```kotlin
private object FiremakingLocs : LocReferences() {
    val fire = find("fire")   // ID 26185 in loc.sym
}
```

This is the standard approach until a shared BaseLocs.kt is created. `loc.sym` is the source of truth for loc names.

| Loc | sym name | ID | Status |
|-----|---------|-----|--------|
| Fire | `fire` | 26185 | ✅ Confirmed in loc.sym — used by Firemaking + Cooking |
| Lumbridge kitchen range | unknown | unknown | ⚠️ Not yet found — needed for early Cooking |

**TODO:** Look up the Lumbridge kitchen range loc name in `loc.sym` and add to Cooking's `cookingRangeLocs` list. It should be named something like `lumbridge_kitchen_range` or similar.

---

## Missing Content Groups — BaseContent.kt

| Gap | Group Name | Needed By | Fix |
|-----|-----------|-----------|-----|
| Firemaking logs | `firemaking_log` | Firemaking (collapse per-log registrations) | Add `val firemaking_log = find("firemaking_log")` + tag all log obj types in cache |
| Cooking ranges | `cooking_range` | Cooking (collapse per-range registrations) | Add `val cooking_range = find("cooking_range")` + tag all range locs in cache |
| Raw food | `raw_food` | Cooking (collapse per-food registrations) | Add `val raw_food = find("raw_food")` + tag all raw food obj types in cache |

**Note:** Until content groups are added, all registrations are explicit loops (functional, just verbose).

---

## Missing Varp Refs — BaseVarps.kt

| Gap | Varp Name | Status |
|-----|-----------|--------|
| Poison damage per tick | `poison_damage` | ✅ Added to BaseVarps.kt |
| Venom damage per tick | `venom_damage` | ✅ Added to BaseVarps.kt |
| Poison sub-tick counter | `poison_sub_tick` | ✅ Added to BaseVarps.kt |
| Poison immunity ticks | `poison_immunity_ticks` | ✅ Added to BaseVarps.kt |
| Venom immunity ticks | `venom_immunity_ticks` | ✅ Added to BaseVarps.kt |
| HP orb toxin indicator (client varp 102) | `hp_orb_toxin` | ✅ Added to BaseVarps.kt with `find("hp_orb_toxin", 102)` |

---

## Missing Spotanim / Synth Refs (Poison)

PoisonScript has TODO stubs for these — blocked by not knowing the exact internal names:

| Gap | Notes |
|-----|-------|
| `poison_hit` spotanim | Green cloud on infliction — spotanim 84 in older caches |
| `venom_hit` spotanim | Purple cloud — spotanim 1303 in older caches |
| `poison_inflict` synth | Sound when poisoned |
| `venom_inflict` synth | Sound when venomed |

**Fix when ready:** Add to `BaseSpotanims.kt` and `BaseSynths.kt`, then uncomment the stubs in `PoisonScript.kt`.

---

## Bone Sym Name Reference (Confirmed Against obj.sym)

Non-obvious cache sym names for prayer bones — use these, NOT the wiki item names:

| Item | Sym name used | Notes |
|---|---|---|
| Burnt bones | `bones_burnt` | Not `burnt_bones` |
| Jogre bones | `tbwt_jogre_bones` | Tai Bwo Wannai quest prefix |
| Fayrg bones | `zogre_ancestral_bones_fayg` | Ancestral family |
| Raurg bones | `zogre_ancestral_bones_raurg` | |
| Ourg bones | `zogre_ancestral_bones_ourg` | |
| Dagannoth bones | `dagannoth_king_bones` | |
| Superior dragon bones | `dragon_bones_superior` | |
| Monkey bones (5+ variants) | `mm_*_monkey_bones` / `mm_*_gorilla_monkey_bones` | Monkey Madness prefix |
| Shaikahan bones | N/A | Not in current rev 233 cache symbols — omit |

All above are now in **BaseObjs.kt**.

---

## NPC Refs Needing Hashes — BaseNpcs.kt

Fishing spots in BaseNpcs use the `find(name, hash)` pattern (required for NPC type-checking).
The Fishing plugin uses local `FishingNpcs` refs for now. When sym hashes are known, add these:

| Kotlin name | Sym name | NPC ID | Note |
|---|---|---|---|
| `rod_fishing_spot_1527` | `0_50_50_freshfish` | 1518 | ✅ Already in BaseNpcs |
| `fishing_spot_1530` | `0_50_49_saltfish` | 1519 | ✅ Already in BaseNpcs |
| `lure_bait_spot` | `0_50_50_lurefish` | 1506 | ❌ Add with hash |
| `big_net_harpoon_spot` | `0_50_49_bignet` | 1520 | ❌ Add with hash |
| `monkfish_spot` | `0_50_49_monkfish` | 4316 | ❌ Add with hash |
| `barb_rod_spot` | `0_50_50_barb` | 1542 | ❌ Add with hash |
| `dark_crab_spot` | `0_50_49_darkcrab` | 1535 | ❌ Add with hash |
| `anglerfish_spot` | `0_50_50_anglerfish` | 6825 | ❌ Add with hash |

Until hashes are known, all 8 spots work via local `FishingNpcs` (no-hash `find()`).

---

## Raw Fish Sym Name Uncertainty

`objs.raw_shrimps` added to BaseObjs as `find("raw_shrimps")` — but in OSRS the item is simply
named "Shrimps" (not "Raw shrimps"). The cache sym may use `"shrimp"` (ID 317).
`objs.shrimps = find("shrimp")` already existed — it may be the raw version.
**Verify** against obj.sym when the server first starts. Fishing.kt uses local `FishingObjs` so
this won't block compilation; only startup will reveal the correct sym name.

---

## Engine Capabilities Confirmed Working

| Feature | API | Notes |
|---------|-----|-------|
| Inventory item-on-item | `onOpHeldU(objA, objB)` in HeldScriptEventExtensions.kt | Order-normalised — register once |
| Loc placement at runtime | `locRepo.add(coords, type, duration, angle, shape)` | Auto-despawns after duration ticks |
| Ground item placement | `objRepo.add(type, coords, duration)` | Standard ground item with timer |
| Player walk during suspend | `walk(dest)` + `delay(1)` | Works inside suspend handler |
| Chain action (loop) | Recursive call at end of suspend fun | Used in Firemaking chain-light |
| XP with modifier | `statAdvance(stat, xp * xpMods.get(player, stat))` | Handles outfit bonuses |
| Atomic inventory item swap | `invReplace(inv, fromObj, count, toObj)` | Used in Cooking raw→cooked/burnt |
| Soft player timer | `onPlayerSoftTimer(timers.x)` | Non-protected, used for poison ticks |
| NPC drop table framework | `NpcDropTableRegistry.register(npc, dropTable { ... })` | Integrated into NpcDeath.deathWithDrops |

