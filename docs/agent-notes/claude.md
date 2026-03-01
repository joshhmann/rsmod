# Claude Session Notes

## 2026-02-20 — LLM Testing Infrastructure (Sessions 1-3)

### What was built
- AgentBridge WebSocket plugin (`rsmod/content/other/agent-bridge/`) — port 43595
  - Per-tick state broadcast: skills, inventory, equipment, animation, nearbyNpcs (16-tile), nearbyLocs (16-tile)
  - Inbound action execution: walk, teleport, interact_loc, interact_npc
  - Injected: Hunt, EventBus, LocRegistry, LocTypeList, LocInteractions, NpcInteractions, NpcList
- MCP server (`mcp/`) — tools: execute_script, get_state, send_action, list_players, build_server, server_status, run_bot_file
- bot-api / sdk-api: walkTo, findAndInteractNpc(/pattern/i), findAndInteractLoc("name"), waitForXpGain
- Python agent-runner (`agent-runner/`) — SkillTesterAgent, DropTesterAgent, LLMAgent
- Bot test reference: `bots/woodcutting.ts`
- Custom skills: rsmod-skill-implementer, rsmod-alter-porting, rsmod-test-writer, rsmod-content-verifier, rsmod-wiki-oracle, rsmod-npc-combat-definer, implement-skill

### Skills implemented (all complete)
Woodcutting, Fishing, Cooking, Firemaking, Mining, Thieving, Prayer, Magic(combat), Poison/Venom
Drop tables: goblins, cows, chickens, guards, men/women, giant rats

### Key gotchas
- `LocTypeList` doesn't have `get(Int)` — use `locTypes.types[locInfo.id]` (backing map)
- `RouteRequestLoc` has a convenience constructor: `RouteRequestLoc(loc = locInfo, type = type)`
- `HuntVis` is at `org.rsmod.game.type.hunt.HuntVis` (not `org.rsmod.api.hunt`)
- `npc.type.id` works — `UnpackedNpcType` extends `CacheType` which has `.id`
- AgentBridge timer sym is at index 9 in `rsmod/.data/symbols/timer.sym`
- `player.level` = floor/plane (0-3), NOT skill level
- `player.avatar.name` = display name (used as queue key in AgentBridgeServer)
- `@OptIn(InternalApi::class)` needed on `statMap.getBaseLevel()` call

### What's next for Claude
1. Wait for Gemini to fix server startup (game.key missing — run generateRsa)
2. Port NPC combat definitions — start with goblins → cows → chickens
3. Implement Smithing (needs wiki-data/smithing.json from Kimi)
4. Implement Crafting, Fletching, Herblore

## 2026-02-20 — Server Startup Fix (Sym File Naming Gap)

### Root Cause
Gemini/Kimi implemented Fishing, Fletching, Herblore, and Poison using modern OSRS wiki item names
(e.g. `small_fishing_net`, `grimy_guam_leaf`, `earth_rune`) but the RSMod sym files use old internal
cache names (`net`, `unidentified_guam`, `earthrune`). This caused 77 "not defined in .sym file" errors
at startup.

### The Fix
RSMod loads sym files from TWO directories — the main `.data/symbols/` AND `.data/symbols/.local/`.
Both are merged into a single name→ID map. NameIdOverlap is checked **per-file only**, so the same ID
can appear in both files under different names (alias approach).

Added `.data/symbols/.local/obj.sym` entries that map modern names to the existing cache IDs:
- Fishing: `small_fishing_net=303`, `big_fishing_net=305`, `raw_shrimps=317`, `barbarian_rod=11323`, etc.
- Fletching: `shortbow_u=50`, `longbow_u=48`, all unstrung bows, `bowstring=1777`, arrowtips 39-44
- Herblore: `vial_of_water=227`, `vial=229`, all grimy herbs, unfinished potions 91-111
- Thieving: `bronze_bolts=877`, `buttons=688`, `rusty_sword=686`, `bear_fur=948`, `spice=2007`, `cowhide=1739`
- Rune aliases: `earth_rune=557`, `law_rune=563`, `chaos_rune=562`, `nature_rune=561`, `blood_rune=565`

Added `.data/symbols/.local/varp.sym` with custom server-side varps (free IDs):
- `poison_damage=4056`, `venom_damage=4058`, `poison_sub_tick=4060`
- `poison_immunity_ticks=4062`, `venom_immunity_ticks=4063`

Renamed `poison` → `hp_orb_toxin` in main `varp.sym` at ID 102.
Changed `BaseVarps.kt`: `find("hp_orb_toxin", 102)` → `find("hp_orb_toxin")` (removing fallback ID
prevents hash mismatch — the fallback ID is used as the `supposedHash` and fails cache validation).

Fixed `Fletching.kt`: removed duplicate `onOpHeldU(objs.logs, objs.knife)` that conflicted with the
knife+logs handler already registered in the BOW_DEFS loop.

### RSMod Sym File Naming Conventions (CRITICAL for future agents)
The `.data/symbols/obj.sym` uses OLD internal cache names, NOT modern OSRS wiki names:
| Modern wiki name      | Sym file name          | ID    |
|-----------------------|------------------------|-------|
| small_fishing_net     | net                    | 303   |
| big_fishing_net       | big_net                | 305   |
| raw_shrimps           | raw_shrimp             | 317   |
| barbarian_rod         | brut_fishing_rod       | 11323 |
| dark_crab_pot         | hundred_ilm_incorrectly_stuffed_snake | 7578 |
| leaping_trout         | brut_spawning_trout    | 11328 |
| leaping_salmon        | brut_spawning_salmon   | 11330 |
| leaping_sturgeon      | brut_sturgeon          | 11332 |
| bowstring             | bow_string             | 1777  |
| shortbow_u            | unstrung_shortbow      | 50    |
| vial_of_water         | vial_water             | 227   |
| vial                  | vial_empty             | 229   |
| grimy_guam_leaf       | unidentified_guam      | 199   |
| grimy_marrentill      | unidentified_marentill | 201   |
| guam_potion_unf       | guamvial               | 91    |
| marrentill            | marentill              | 251   |
| dwarfweed             | dwarf_weed             | 267   |
| bronze_bolts          | bolt                   | 877   |
| buttons               | digsitebuttons         | 688   |
| rusty_sword           | digsitesword           | 686   |
| bear_fur              | fur                    | 948   |
| cowhide               | cow_hide               | 1739  |
| spice                 | spicespot              | 2007  |
| earth_rune            | earthrune              | 557   |
| bronze_arrowtips      | bronze_arrowheads      | 39    |

**RULE**: Always check `.data/symbols/obj.sym` for the actual sym name before using `find("name")`.
If your name isn't there, add an alias to `.data/symbols/.local/obj.sym` with the correct cache ID.

### Key Technical Facts
- `NameIdOverlap` is per-file, NOT global — safe to have same ID in main + local sym files
- Hash verification: `find("name", fallbackId)` — the fallbackId becomes `supposedHash` and WILL fail
  cache hash check if ID exists in cache. Always use `find("name")` (no fallback) once name is in sym.
- Custom server-side varps (not in OSRS cache): use free IDs in .local/varp.sym. TypeVerifier
  apparently doesn't hash-check varps whose IDs don't exist in the cache.
- `SymbolModule.kt` loads both root and root/.local, merged with `map +=` (later files win on name clash)
- TypeVerifier error messages: "not defined in .sym file" = name missing; "hash mismatch" = wrong ID used

### Server Status
Server now starts successfully: port 43594 active, revision 233, AgentBridge on 43595.
Skills implemented: Woodcutting, Fishing, Firemaking, Cooking, Mining, Thieving, Prayer,
Magic(combat), Poison/Venom, Drop tables (6 F2P NPCs), Fletching, Herblore (partial)
## 2026-02-26 — Engineering Mandate: Preventing the 'Blankobject Trap'

### What happened
During a final stabilization pass, I (Gemini) attempted to clear compile errors in `api:player` and `api:spells` by mapping missing item symbols to `objs.nothing_` (`blankobject` ID 6512). While the server compiled, it crashed at boot with `Duplicate key` errors. 

### Root Cause
RSMod's **Enum Builders** and **Obj Editors** use item symbols as map keys. Mapping multiple symbols (e.g. `ahrims_hood` and `dharoks_helm`) to the same ID (`blankobject`) causes a collision in the server's internal logic tables during the world-load phase.

### New Mandatory Protocols
1.  **NO Duplicate Functional Stubs**: NEVER map more than one functional symbol to a dummy ID like `blankobject`. Every symbol added to `BaseObjs` MUST be unique and grepped from `rsmod/.data/symbols/obj.sym` first.
2.  **Full Boot Gate**: The "Definition of Done" for any task involving symbols or global configs is a successful server boot reaching the `World is live` state. Compilation alone is shallow verification and will miss runtime builder crashes.
3.  **Source of Truth Only**: Do not guess or invent symbol names. Use `Get-Content rsmod/.data/symbols/obj.sym | Select-String -Pattern "name"` to find the real internal cache name.

### Fixed in this session
- Reconciled 100+ symbols in `BaseObjs.kt` with real cache IDs.
- Fixed `EquipmentChecks.kt` and `AutocastEnums.kt` to use unique real symbols.
- Achieved a 100% clean, crash-free server boot.

