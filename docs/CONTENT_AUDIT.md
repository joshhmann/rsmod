# RSMod v2 Content Audit — Rev 233

Status against vanilla OSRS rev 233. Last updated: 2026-02-26.

Legend: ✅ Implemented | 🟡 Partial / Placeholder | ❌ Not started | ⚠️ Needs engine fix | 🔴 Disabled at startup (broken)

> **Startup warning audit (2026-02-24):** BUILD-CRIT-14 fixed 11 disabled scripts. ~5 scripts may remain disabled pending BUILD-CRIT-15 (null internalId / missing sym entries). Always verify: `grep "Skipping script startup"` in boot log must return zero lines.
>
> **Codebase audit (2026-02-26):** See `docs/CODEBASE_AUDIT_2026-02-26.md` for full findings including ghost modules, orphaned files, dead code, and documentation gaps. CLEANUP-1 through CLEANUP-10 tasks created in agent-tasks registry.

---

## Skills

| Skill | Status | Notes |
|-------|--------|-------|
| Woodcutting | ✅ | Complete. Axe selection, XP, tree respawn via Controller, content-group matching. Use as template. |
| Fishing | ✅ | 20 fish types, 8 spot types, tool/bait system, per-catch statRandom roll, wiki-accurate XP. |
| Cooking | ✅ | 19 fish types, wiki-accurate XP/burn levels, cooking gauntlets, atomic invReplace(), 8 range locs + fire. |
| Firemaking | ✅ | All log types (incl. blisterwood), chain-light, fire loc placement, ashes drop, wiki-accurate XP. |
| Mining | ✅ | 10 ore types, param-driven rocks (ObjEditor+LocEditor), all 16 pickaxe anims, content.mining_pickaxe group. Gem rocks + guild boost TODO. |
| Thieving | ✅ | 14 pickpocket NPCs, 11 stall types (47 handlers), H.A.M. chests. Stun on fail, weighted loot. TODO: Ardy diary bonus, rogue outfit, dodgy necklace. |
| Prayer training | 🟡 | 26 bone types, bury mechanic functional. Altar interaction (PrayerAltar.kt) disabled at startup — null internalId on prayer altar loc ref (BUILD-CRIT-15). Standard altar prayer restore broken until fixed. |
| Magic (combat) | ✅ | Spell attack system, rune consumption. (`content/skills/magic/`) |
| Attack / Strength / Defence | ✅ | Combat engine handles XP via hit-plugin. |
| Hitpoints | ✅ | Handled by combat engine. |
| Smithing | ✅ | Smelting (all bars) + anvil smithing (all tiers). No smithing interface (makes first product per bar). Furnace loc interaction TODO. |
| Crafting | 🟡 | GemCutting, Jewelry, Leatherworking were disabled at startup (bidirectional onOpHeldU duplicates). Fixed in BUILD-CRIT-14. Spinning wheel, all leather/gem/jewellery functional. Ellis tanner (Al Kharid) works. No make-X interface. |
| Fletching | ✅ | Knife-on-log (all bow types), bowstring stringing, arrow making (all tiers). No make-X interface. |
| Herblore | ✅ | Herb cleaning (14 herbs), unfinished potions, finished potions (17 recipes). No make-X interface. |
| Agility | 🟡 | Gnome Stronghold course complete. Draynor/Al Kharid/Varrock rooftop courses give XP but obstacles don't teleport player to destination (CLEANUP-9). |
| Runecrafting | ✅ | F2P altars implemented: Air, Mind, Water, Earth, Fire. Rune Mysteries gate, talisman/tiara entry, and level-based rune multipliers. |
| Farming | ✅ | Complete — codex (SKILL-21). Herb patches, weeding, planting, staged growth, disease/cure/death, harvesting. |
| Slayer | ✅ | Core system: 6 masters, 60+ tasks, points, blocking, shop, XP on kill. Gear protection blocked (SLAYER-8). |
| Hunter | 🟡 | Baseline claimed (SKILL-23, HUNTER-1) but module directory has no `build.gradle.kts` — not compiled. Verify if source files exist or if implementation was lost. |
| Construction | 🟡 | Baseline scaffolding done (SKILL-24). No room building yet (CONSTRUCTION-IMPL pending). |
| Ranged training | ✅ | Combat engine + ammo recovery done (QUIRK-1). Cannon support added. |

---

## Combat System

| Feature | Status | Notes |
|---------|--------|-------|
| Melee combat | ✅ | `api/combat`, `api/combat-accuracy`, `api/combat-maxhit` |
| Ranged combat | ✅ | Weapon system supports ranged. Ammo consumption needs validation. |
| Magic combat | ✅ | Spell attack system, rune consumption. |
| Special attacks | ✅ | `content/other/special-attacks/` and `api/specials` |
| Prayer combat bonuses | ✅ | Combat formula reads prayer bonuses. |
| Death & drops | ✅ | `api/death`, `api/death-plugin`, `api/drop-table`. Full weighted drop table framework. Basic F2P tables: goblins, cows, chickens, guards, men/women, giant rats. |
| NPC retaliation | ✅ | `queueCombatRetaliate()` used in generic NPCs. |
| Poison | ✅ | `content/mechanics/poison/`. 18-tick interval, damage decay, immunity windows. |
| Venom | ✅ | Part of PoisonScript. Starts at 6, +2/tick, capped at 20. Supersedes poison. |
| NPC aggression | ✅ | `content/mechanics/npc-aggression/`. Radius + tolerance timer (de-aggro). Completed MECH-1 + MECH-3. |
| Freeze / Stun | ✅ | Completed (MECH-2). Resistance params, duration, movement block. |

---

## LLM Testing Infrastructure

| Component | Status | Notes |
|-----------|--------|-------|
| AgentBridge plugin | ✅ | `content/other/agent-bridge/`. WebSocket port 43595. Per-tick state broadcast + inbound action execution. |
| State snapshot | ✅ | Broadcasts: position, skills (all 23), inventory, equipment, animation, **nearbyNpcs** (16-tile radius), **nearbyLocs** (16-tile radius). |
| Action execution | ✅ | Supports: `walk`, `teleport`, `interact_loc`, `interact_npc`. Dequeued on game thread each tick. |
| MCP server | ✅ | `mcp/` (TypeScript/bun). Tools: `execute_script`, `get_state`, `send_action`, `list_players`. Auto-discovered by Claude Code via `.mcp.json`. |
| bot-api / sdk-api | ✅ | `bot.walkTo()`, `bot.findAndInteractNpc(/pattern/i)`, `bot.findAndInteractLoc("name")`, `bot.waitForXpGain()`, `sdk.findNearbyNpc()`, `sdk.findNearbyLoc()`, etc. |
| Python agent-runner | ✅ | `agent-runner/`. SkillTesterAgent, DropTesterAgent, LLMAgent (Claude API per-tick). Smoke + nightly tiers. |
| Wiki oracle | ✅ | `wiki-data/skills/` — woodcutting, fishing, mining JSONs. `wiki-data/monsters/` — goblin, cow JSONs. |
| Bot test scripts | ✅ | `bots/woodcutting.ts` — reference test. Add `bots/<skill>.ts` for each new skill. |
| Build automation | ✅ | MCP `build_server` tool available via rsmod-game MCP server. `server_status` and `run_bot_file` also available. No start/stop server tools — server must be started manually. |
| `/implement-skill` command | ✅ | `.claude/commands/implement-skill.md` — Claude Code slash command for full implement+test workflow. |

---

## Interfaces

| Interface | Status | Notes |
|-----------|--------|-------|
| Bank | ✅ | `content/interfaces/bank/` |
| Gameframe / main HUD | ✅ | `content/interfaces/gameframe/` |
| Equipment tab | ✅ | `content/interfaces/equipment/` |
| Prayer tab | ✅ | `content/interfaces/prayer-tab/` |
| Combat tab | ✅ | `content/interfaces/combat-tab/` |
| Skill guide | ✅ | `content/interfaces/skill-guides/` |
| Logout tab | ✅ | `content/interfaces/logout-tab/` |
| Settings | ✅ | `content/interfaces/settings/` |
| Emotes | ✅ | `content/interfaces/emotes/` |
| Journal / Quest tab | 🟡 | Quest engine complete. GoblinDiplomacy + ErnestChicken were disabled at startup (bidirectional item-use), fixed in BUILD-CRIT-14. PiratesTreasure disabled (null internalId — BUILD-CRIT-15). Quest journal UI functional. Boss fights for Vampyre Slayer / Dragon Slayer I scripted. |
| Friends / Ignore list | 🟡 | UI done (SYSTEM-UI-1). Backend system pending (SYSTEM-FRIENDS). |
| Grand Exchange | 🟡 | UI baseline done (SYSTEM-UI-2). Trading logic pending (SYSTEM-GE-IMPL). |
| Music player | ✅ | Content wired (SYSTEM-UI-3). Real MidiType data loaded (ENGINE-4). |

---

## Generic World Content

| Content | Status | Notes |
|---------|--------|-------|
| Doors (single + double) | ✅ | `content/generic/generic-locs/` |
| Picket gates | ✅ | BaseContent content groups. |
| Ladders (up/down) | ✅ | Content group matching. |
| Spiral staircases | ✅ | Content group matching. |
| Bank booths / chests / deposit boxes | ✅ | Content groups. |
| Windmill | ✅ | `content/other/windmill/` |
| Canoe (transport) | ✅ | `content/travel/canoe/` |
| Crates / Sacks / Boxes | ✅ | Generic locs. |
| Fishing spots | ✅ | Implemented with Fishing skill. |
| Mining rocks | ✅ | Implemented with Mining skill. |
| Altars (prayer) | ✅ | Implemented with Prayer skill. |
| Furnace / Anvil | 🟡 | World objects placed (F2P-WORLD-1, F2P-WORLD-2, SMITH-1). Furnace loc interaction (SMITH-2) still pending. |
| Spinning wheel | ✅ | Implemented in Crafting module. |
| Agility obstacles | 🟡 | Gnome Stronghold obstacles implemented; broader obstacle/world coverage still missing. |
| Farming patches | 🟡 | Herb patch baseline implemented (rake weeds, seed dibber planting, plant cure, spade clear dead crop, harvest flow). |

---

## NPC Content

| NPC / Category | Status | Notes |
|----------------|--------|-------|
| Generic person dialogue | ✅ | `content/generic/generic-npcs/` |
| Banker | ✅ | `content.banker` content group — dialogue, bank open. |
| Bob (Lumbridge) | ✅ | Bob's Axes shop. |
| Goblin, Cow, Chicken, Guard, Giant Rat, Imp, Dark Wizard, Lesser Demon, Black Knight, Scorpion | ✅ | Combat defs + drop tables done (NPC-1, NPC-2). |
| Hill Giant, Skeleton, Zombie, Moss Giant | 🟡 | Hill Giant + Skeleton combat + drops done. Zombie/Moss Giant still pending. **Note:** NPC-TRAINING-F2P task reclassified as P2P (Combat Training Camp is members-only). |
| Mugger, Barbarian, Dwarf, Warrior Woman, Bear, Unicorn | ❌ | Combat + drops pending. |
| Count Draynor, Elvarg | ✅ | Boss scripting done (QUEST-9-IMPL, QUEST-10-IMPL). |
| Quest NPCs | ✅ | All 13 F2P quest NPCs scripted and complete. |
| Slayer masters | ✅ | Full system: task assignment, shops, XP, points, blocking (SLAYER-W4-CORE-1 + SLAYER-1 through 7). |

---

## Areas / Cities

| Area | Status | Notes |
|------|--------|-------|
| Lumbridge | 🟡 | Bob's shop, Hans, Duke Horacio, Cook functional. General store (shop_keeper NPC) disabled at startup — null internalId (BUILD-CRIT-15). Church altar prayer restore broken (PrayerAltar disabled). FatherAereck Op1 deferred to RestlessGhost quest. |
| Varrock | 🟡 | Banks, general store, Lowe's archery, Horvik's armour, Zaff's staffs functional. Aubury Op1 deferred to RuneMysteries quest (shop Op3 still works). Apothecary Op1 deferred to RomeoJuliet quest. Thessalia makeover broken (known tech debt). |
| Multi-combat zones | ✅ | `content/areas/misc/multiways/` |
| Draynor Village | ✅ | AREA-2 complete. Bank, general store, Morgan, Aggie, Count Draynor, Leela. |
| Al Kharid | ✅ | AREA-3 complete. 6 shops, Ellis tanner, Border Guard toll gate, all NPCs. |
| Falador | ✅ | AREA-5 complete. Wayne's Chains, Flynn's Maces, both banks, White Knights, Falador Guards, Doric, Party Pete. |
| Port Sarim / Rimmington | ✅ | AREA-6 complete. All Port Sarim NPCs + shops done. Rimmington surface populated. |
| Edgeville / Barbarian Village | ✅ | AREA-7 complete. General Store, Peksa, Brother Jered, missing NPC refs fixed. |
| Asgarnian Ice Dungeon | ✅ | `content/areas/dungeons/asgarnian-ice/`. Ice warriors, ice giants, hobgoblins (F2P). WORLD-ASGARNIA-ICE complete. |

---

## Other Systems

| System | Status | Notes |
|--------|--------|-------|
| Login sequence | ✅ | `content/other/login/` |
| Admin commands | ✅ | `content/other/commands/` |
| Drop table framework | ✅ | `api/drop-table` + `content/other/npc-drops/`. 6 F2P NPC tables. |
| XP modifier system | ✅ | `api/stats/xpmod` — worn outfit bonuses, custom mods. |
| Invisible level boost | ✅ | `api/stats/levelmod` — e.g. Woodcutting Guild +7. |
| Item charges | ✅ | `api/obj-charges` — framework for degrading items. |
| **Food eating** | ✅ | `content/other/consumables/` — completed by kimi (FOOD-1). |
| **Potions (stat boosts)** | ✅ | `content/other/consumables/` — completed by kimi (FOOD-2). |
| **Prayer active effects** | ✅ | Already fully implemented in `content/interfaces/prayer-tab/` — toggle, drain, overhead icons, Rapid Restore/Heal, Preserve. Wiki-accurate. |
| **Stat restore / HP regen** | 🟡 | `content/mechanics/stat-restore/` — implementation complete. May be disabled at startup (investigate BUILD-CRIT-15). If enabled: 1pt/100 ticks, Rapid Restore halves interval, HP regen separate timer. |
| **Make-X interface** | 🟡 | Framework + Smithing wired (MAKEQ-1). Fletching, Herblore, Crafting still need wiring (MAKEQ-FLETCH/HERB/CRAFT). |
| **Run energy / Weight** | ✅ | `api/inv-weight` + `api/game-process`. |
| Shops | ✅ | Lumbridge general store + Bob's Axes done. Varrock: 6 shops wired (AREA-4 complete). Remaining cities pending AREA tasks. |
| NPC aggression radius | ✅ | `content/mechanics/npc-aggression/` — tolerance zone utilities. Full per-NPC hook wiring pending (MECH-3). |
| Freeze / Stun | ✅ | Completed by codex (MECH-2). |
| Grand Exchange | 🟡 | `api/market` framework exists. No GE interface or offer queue. |
| Slayer system | ✅ | Full system complete. See Skills section. |
| Player-to-player trading | ✅ | Complete (TRADE-1). Trade request/accept UI + item exchange. |
| Wilderness / PvP | ❌ | No skulling, item protection on death, combat level range check. |
| Quests | 🟡 | Quest engine complete (22 quests in QuestList). 10 quests fully implemented. 6 quest stubs with empty startup(). Ghost module `romeo-and-juliet/` exists alongside real `romeo-juliet/` (CLEANUP-2). PiratesTreasure may still be disabled (BUILD-CRIT-15 null internalId — verify). |

---

## Data Sources for Porting

| Data Need | Best Source |
|-----------|-------------|
| NPC combat stats (HP, attack, defence, anims) | `Kronos-184-Fixed/.../data/npcs/combat/` — 900+ JSON files |
| NPC drop tables | `Kronos-184-Fixed/.../data/npcs/drops/eco/` — weighted JSON |
| Skill XP rates & level reqs | OSRS wiki (cross-reference Kronos — it's rev 184) |
| Item stats / equipment bonuses | `OSRSWikiScraper/` — `python main.py -e "Dragon Scimitar"` |
| Live drop tables | `OSRSWikiScraper/` — `python main.py -n "Green dragon"` |
| All weapon stats | `OSRSWikiScraper/` — `python main.py -aw` |
| Rev 233 animations / sounds | RSMod v2 `.data/symbols/` — use `seqs.*` symbol names |
| NPC drop table generation | `tools/npc_lookup.py` + `tools/batch_npc_processor.py` — auto-generates Kotlin from wiki-data + Kronos |

---

## Porting Priority (Updated 2026-02-24)

See `docs/F2P_SPRINT_REVIEW.md` for full playthrough readiness breakdown.

### DONE ✅
All 17 F2P skills have code. All 13 F2P quests complete (including boss fights). 7 cities fully populated. Combat, death, prayer, food, potions, shops, trading, aggression, freeze/stun all done.

### Reclassified Tasks (P2P Scope Correction)
- **`NPC-TRAINER-RECLASSIFY-1`**: Combat Training Camp reclassified as P2P content (members-only). Original NPC-TRAINING-F2P task superseded. F2P training monsters (Hill Giants, Skeletons, Zombies, Moss Giants) tracked under individual NPC-*-F2P task IDs.

### Tier 1 — Critical path to playable (~85% parity)
1. **`SYSTEM-LEVELUP`** — Level-up messages + interface
2. **`MAGIC-TELE`** — Teleport spells (Varrock/Lumbridge/Falador)
3. **`MAGIC-ALCH`** — High/Low alchemy
4. **`MAGIC-SUPERHEAT`** — Superheat Item
5. **`NPC-HILL-GIANT-F2P` + `NPC-DROP-HILL-GIANT`** — #1 F2P training monster (use `python tools/npc_lookup.py "Hill Giant" --output kotlin`)
6. **`AREA-EDGEVILLE-DUNG`** — Edgeville Dungeon (Hill Giants, Skeletons)
7. **`NPC-SKELETON-F2P` / `NPC-ZOMBIE-F2P` / `NPC-MOSS-GIANT-F2P`** + drops (batch process: `python tools/batch_npc_processor.py --tier 1`)

### Tier 2 — Noticeable gaps
8. `AREA-VARROCK-SEWER`, `AREA-KARAMJA-F2P`, `WORLD-DWARVEN-MINE`
9. `MAKEQ-FLETCH/HERB/CRAFT`, `SMITH-2`, `SYSTEM-COMBAT-LEVEL`
10. Remaining Draynor / Al Kharid NPCs

### Tier 3 — Polish
11. Wilderness PvP (`WILD-1/2/3`)
12. Remaining NPC combat types + drop tables
13. Bot test scripts, emotes, examine texts, random events

