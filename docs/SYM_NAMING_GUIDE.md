# RSMod Symbol File Naming Guide

## The Problem

RSMod's `.data/symbols/obj.sym` uses **old internal cache names**, not modern OSRS wiki names.
When implementing content, always check the sym file first — your `find("name")` call must match
the sym file entry exactly.

## How Sym Files Work

- **Main sym**: `.data/symbols/` — generated from the OSRS cache, uses internal names
- **Local sym**: `.data/symbols/.local/` — loaded second, merged on top (later entries win)
- `NameIdOverlap` is checked **per-file** — same ID can appear in main + local under different names
- Both directories are loaded by `SymbolModule.kt` via `shallowSymbolDirectories()`

## Canonical Workflow

Read `docs/REV233_SYMBOL_WORKFLOW.md` before adding or changing symbols, refs, cache enrichers,
wiki-data IDs, generated drop tables, or content that depends on NPC/object/location IDs.

Hard rule: content adapts to rev 233 symbols. Do not rename symbols to make content fit.

## Adding New Content Items

If your `find("wiki_name")` is not in the main sym:

1. Search the relevant base sym file for the canonical cache name.
2. Update Kotlin/TOML/wiki-data to use that canonical name.
3. If a compatibility name is needed, add a Kotlin getter alias only; do not add a `.local/*.sym`
   alias for an existing base cache ID.
4. Add `.local/*.sym` entries only for genuinely server-only/generated types with no base cache
   identity.
5. Run the validation order in `docs/REV233_SYMBOL_WORKFLOW.md`.

Do not use `.local` as a vocabulary layer for wiki names. That is how later agents accidentally
revert IDs, introduce `NameIdOverlap`, or make `packCache` fail with `internalId=-1`.

## Common Name Mappings (obj.sym)

| Your code name        | Main sym name          | Cache ID |
|-----------------------|------------------------|----------|
| small_fishing_net     | net                    | 303      |
| big_fishing_net       | big_net                | 305      |
| raw_shrimps           | raw_shrimp             | 317      |
| barbarian_rod         | brut_fishing_rod       | 11323    |
| dark_crab_pot         | hundred_ilm_incorrectly_stuffed_snake | 7578 |
| leaping_trout         | brut_spawning_trout    | 11328    |
| leaping_salmon        | brut_spawning_salmon   | 11330    |
| leaping_sturgeon      | brut_sturgeon          | 11332    |
| bowstring             | bow_string             | 1777     |
| shortbow_u            | unstrung_shortbow      | 50       |
| longbow_u             | unstrung_longbow       | 48       |
| oak_shortbow_u        | unstrung_oak_shortbow  | 54       |
| oak_longbow_u         | unstrung_oak_longbow   | 56       |
| willow_shortbow_u     | unstrung_willow_shortbow | 60     |
| willow_longbow_u      | unstrung_willow_longbow | 58      |
| maple_shortbow_u      | unstrung_maple_shortbow | 64      |
| maple_longbow_u       | unstrung_maple_longbow  | 62      |
| yew_shortbow_u        | unstrung_yew_shortbow  | 68       |
| yew_longbow_u         | unstrung_yew_longbow   | 66       |
| magic_shortbow_u      | unstrung_magic_shortbow | 72      |
| magic_longbow_u       | unstrung_magic_longbow  | 70      |
| bronze_arrowtips      | bronze_arrowheads      | 39       |
| iron_arrowtips        | iron_arrowheads        | 40       |
| steel_arrowtips       | steel_arrowheads       | 41       |
| mithril_arrowtips     | mithril_arrowheads     | 42       |
| adamant_arrowtips     | adamant_arrowheads     | 43       |
| rune_arrowtips        | rune_arrowheads        | 44       |
| vial_of_water         | vial_water             | 227      |
| vial                  | vial_empty             | 229      |
| grimy_guam_leaf       | unidentified_guam      | 199      |
| grimy_marrentill      | unidentified_marentill | 201      |
| grimy_tarromin        | unidentified_tarromin  | 203      |
| grimy_harralander     | unidentified_harralander | 205    |
| grimy_ranarr_weed     | unidentified_ranarr    | 207      |
| grimy_irit_leaf       | unidentified_irit      | 209      |
| grimy_avantoe         | unidentified_avantoe   | 211      |
| grimy_kwuarm          | unidentified_kwuarm    | 213      |
| grimy_cadantine       | unidentified_cadantine | 215      |
| grimy_dwarfweed       | unidentified_dwarf_weed | 217     |
| grimy_torstol         | unidentified_torstol   | 219      |
| grimy_lantadyme       | unidentified_lantadyme | 2485     |
| grimy_toadflax        | unidentified_toadflax  | 3049     |
| grimy_snapdragon      | unidentified_snapdragon | 3051    |
| guam_potion_unf       | guamvial               | 91       |
| marrentill_potion_unf | marrentillvial         | 93       |
| tarromin_potion_unf   | tarrominvial           | 95       |
| harralander_potion_unf| harralandervial        | 97       |
| ranarr_potion_unf     | ranarrvial             | 99       |
| irit_potion_unf       | iritvial               | 101      |
| avantoe_potion_unf    | avantoevial            | 103      |
| kwuarm_potion_unf     | kwuarmvial             | 105      |
| cadantine_potion_unf  | cadantinevial          | 107      |
| dwarfweed_potion_unf  | dwarfweedvial          | 109      |
| torstol_potion_unf    | torstolvial            | 111      |
| toadflax_potion_unf   | toadflaxvial           | 3002     |
| snapdragon_potion_unf | snapdragonvial         | 3004     |
| lantadyme_potion_unf  | lantadymevial          | 2483     |
| marrentill            | marentill              | 251      |
| dwarfweed             | dwarf_weed             | 267      |
| bronze_bolts          | bolt                   | 877      |
| buttons               | digsitebuttons         | 688      |
| rusty_sword           | digsitesword           | 686      |
| bear_fur              | fur                    | 948      |
| cowhide               | cow_hide               | 1739     |
| spice                 | spicespot              | 2007     |
| earth_rune            | earthrune              | 557      |
| nature_rune           | naturerune             | 561      |
| chaos_rune            | chaosrune              | 562      |
| law_rune              | lawrune                | 563      |
| blood_rune            | bloodrune              | 565      |
| hp_orb_toxin          | hp_orb_toxin           | 102      |

## Common NPC Name Quirks (npc.sym)

NPC names are often role/location variants, not wiki display names. Do not assume `guard`,
`banker`, or `shop_keeper` exists just because the wiki or in-game right-click text says so.

| Wiki/display name | Rev 233 sym candidates | Notes |
|-------------------|------------------------|-------|
| Banker | `banker1`, `banker2`, `banker1_west`, `banker1_east`, `banker1_new`, `banker2_new` | Pick by spawn/location context. Do not create a generic `banker` alias. |
| Al Kharid banker | `kharidbanker1`, `kharidbanker2` | Use area-specific symbols where present. |
| Falador banker | `falador_banker` | Area-specific banker symbol. |
| Guard | `fai_varrock_guard`, `fai_falador_guard1`..`fai_falador_guard6`, many quest/area variants | Search by area first; generic wiki "Guard" is not enough. |
| Man | `man` | Verify combat/dialogue variant before using for spawns. |
| Woman | `woman` | Verify combat/dialogue variant before using for spawns. |
| Cook | `cook` | Used for Cook's Assistant/Lumbridge context. |
| Father Aereck | `father_aereck` | Quest NPC symbol. |
| Aubury | `aubury` | Varrock/Rune Mysteries context. |
| Reldo | `reldo` | Varrock Palace library. |
| Shop keeper | Search `shop`, `keeper`, and area name | Historical duplicate issue: do not map a generic `shop_keeper` to an area-specific NPC ID. |

## Common Loc Name Quirks (loc.sym)

Location/object names are even more collision-prone than item names. Many doors, trees, ranges,
and gates are specialized variants.

| Wiki/display name | Rev 233 sym candidates | Notes |
|-------------------|------------------------|-------|
| Tree | `tree`, `tree2`, `tree3`, `lighttree`, `lighttree2` | Use content groups or exact map loc data when available. |
| Oak tree | Search `oak` and verify map loc/context | Do not invent `oak_tree` if absent. |
| Willow/Yew/Magic tree | Search the exact tree family and verify map loc/context | Woodcutting content often uses content groups instead of one loc ref. |
| Dead tree | `deadtree1`, `deadtree2`, `deadtree3`, `deadtree4`, `deadtree6`, stump variants | Pick the exact loc from map data. |
| Furnace | `furnace`, plus quest/minigame variants such as `plaguesheep_furnace` | Use `furnace` only when the map loc actually resolves to that symbol. |
| Range | `range`, plus quest-specific variants such as `cooksquestrange` | Cooking interactions must account for generic and special ranges separately. |
| Anvil | `anvil` | Verify exact loc for area placement. |
| Bank door | `bankdoor_r`, `bankdoor_l`, `openbankdoor_r`, `openbankdoor_l` | Door state/direction matters. |
| Generic door | Many variants: `castledoor`, `thickpoordoor`, `archeddoorclosed`, etc. | Prefer existing door systems/content groups over new one-off refs. |
| Gate | Many variants: `gnome_gate`, `fishinggateclosedl`, `fishinggateclosedr`, etc. | Left/right/open/closed variants matter. |

## Symbol Hygiene Workflow

To fix "invalid symbol reference" errors reported by `validateSymbols` while maintaining project stability:

1.  **Identify Canonical Name**: Look up the property in its corresponding `.sym` file (e.g., `.data/symbols/obj.sym`).
2.  **Update Reference File**: In the `Base` reference class (e.g., `BaseObjs.kt`), rename the property to match the canonical name exactly.
3.  **Add Compatibility Alias**: Add a `get()` alias for the old name to prevent massive compilation errors in other modules.
    ```kotlin
    val chaosrune = find("chaosrune") // Canonical name (Validator passes)
    val chaos_rune get() = chaosrune  // Compatibility alias (Compiler passes)
    ```
4.  **Surgical Code Updates**: Update call sites in your target module to use the new canonical name (e.g., change `objs.chaos_rune` to `objs.chaosrune`).
5.  **Verify**:
    - Run `./gradlew validateSymbols` to confirm the error is gone for your module.
    - Run module-specific compilation (e.g., `./gradlew :content:skills:herblore:compileKotlin`).
    - For symbol/cache/global changes, run `./gradlew packCache --console=plain` before boot/testing.

## Custom Server-Side Varps

For server-only varps not in the OSRS cache, use free IDs in `.data/symbols/.local/varp.sym`:

| Varp name              | ID   | Purpose                          |
|------------------------|------|----------------------------------|
| hp_orb_toxin           | 102  | Client HP orb poison/venom color |
| poison_damage          | 4056 | Active poison dmg per tick       |
| venom_damage           | 4058 | Active venom dmg per tick        |
| poison_sub_tick        | 4060 | Sub-tick counter (0-5)           |
| poison_immunity_ticks  | 4062 | Antipoison immunity ticks        |
| venom_immunity_ticks   | 4063 | Antivenom immunity ticks         |

## The `find(name, fallbackId)` Trap

**NEVER** use `find("name", hardcodedId)` if the ID exists in the OSRS cache.
The fallbackId becomes the `supposedHash` and will fail hash verification at startup:

```
Invalid hash: 102 | Cache hash: 23155904 | Reference: VarpType(supposedHash=102, ...)
```

Use `find("name")` always — add the name to the sym file instead.

## Duplicate Event Handler Bug

If you register the same `onOpHeldU(item1, item2)` twice (e.g., in a loop AND standalone),
RSMod throws `IllegalStateException: Event with id already registered`. Check your BOW_DEFS /
ARROW_DEFS loops against any standalone handlers using the same items.

