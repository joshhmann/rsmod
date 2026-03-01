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

## Adding New Content Items

If your `find("wiki_name")` isn't in the main sym:

1. Find the item's OSRS cache ID (check main sym by searching for partial matches, or use Kronos data)
2. Add an alias to `.data/symbols/.local/obj.sym`:
   ```
   <id>	<your_modern_name>
   ```
3. No code changes needed — both names resolve to the same cache ID

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

