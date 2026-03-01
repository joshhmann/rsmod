---
name: mcp-osrs-lookup
description: Query OSRS game data using the MCP OSRS server. Use for finding item IDs, NPC IDs, animation IDs, and other cache information. Provides instant access to objtypes, npctypes, seqtypes, and other game data files.
---

# MCP OSRS Lookup Tool

## Task Coordination — Do This First

Multiple agents (Claude, OpenCode, Kimi, Codex) work this codebase simultaneously.
Before writing a single line of code, coordinate via the `agent-tasks` MCP server:

1. `list_tasks({ status: "pending" })` — find available work
2. `get_task("TASK-ID")` — read the full task description
3. `claim_task("TASK-ID", "your-agent-name")` — atomically claim it (fails if taken)
4. `check_conflicts(["path/file"])` — verify no one else is editing your files
5. `lock_file("path/file", "your-agent-name", "TASK-ID")` — lock every file before editing
6. Implement.
7. `complete_task("TASK-ID", "your-agent-name", "what I built")` — releases locks, marks done

If blocked: `block_task("TASK-ID", "your-agent-name", "exact reason")` so others know.
See `START_HERE.md` for the full project orientation.

Five MCP servers are available for OSRS data lookups. Use the right one for each task.

---

## MCP Server Map

| Server | Best For |
|--------|----------|
| `osrs-cache` | Raw ID lookups — items, NPCs, anims, locs, varps, interfaces |
| `osrs-wiki-rev233` | Historical wiki data — NPC stats, item data, Varlamore Part 2 content check |
| `osrs-cache` (wiki tools) | Wiki page search, parse, drop tables |
| `context7` | RSMod/Kotlin API docs |
| `web-search` | General web, current OSRS info |
| `rsmod-game` | Game server control, bot testing |

---

## Server 1: osrs-cache — Raw Cache IDs

Data from Rev 233 cache dumps. Use for symbol name resolution and ID cross-reference.

### search_objtypes — Find Items

```javascript
search_objtypes({ query: "dragon claws", pageSize: 10 })
// → { id: 13652, name: "dragon_claws", ... }
```

### search_npctypes — Find NPCs

```javascript
search_npctypes({ query: "goblin", pageSize: 10 })
// → { id: 3078, name: "goblin", ... }
```

### search_seqtypes — Find Animations

```javascript
search_seqtypes({ query: "goblin attack", pageSize: 10 })
// → { id: 6184, name: "goblin_attack", ... }
```

### search_loctypes — Find World Objects

```javascript
search_loctypes({ query: "yew tree", pageSize: 10 })
// → { id: 10833, name: "yew_tree", ... }
```

### search_varptypes / search_varbittypes — Find Varps

```javascript
search_varptypes({ query: "quest", pageSize: 10 })
search_varbittypes({ query: "quest_stage", pageSize: 10 })
```

### Other cache tools

```javascript
search_iftypes({ query: "bank" })        // Interface definitions
search_invtypes({ query: "inventory" })  // Inventory types
search_spottypes({ query: "splash" })    // Spot animations
search_soundtypes({ query: "door" })     // Sound effects
```

### Wiki search tools (on osrs-cache server)

```javascript
osrs_wiki_search({ search: "Goblin" })              // Full wiki search
osrs_wiki_get_page_info({ titles: "Goblin,Guard" }) // Page info
osrs_wiki_parse_page({ page: "Goblin" })            // Full page HTML
```

---

## Server 2: osrs-wiki-rev233 — Historical Stats (PREFERRED for combat/item stats)

Scrapes the OSRS wiki at a **Rev 233-locked historical snapshot** aligned to OpenRS2 cache `runescape/2293` (build `233`, built `2025-09-10`). **Use this for NPC combat stats and item data** — more accurate than Kronos (rev 184).

### get_npc_rev233_openrs2_2293 — NPC Stats (canonical)

```javascript
get_npc_rev233_openrs2_2293({ name: "Goblin" })
// Returns: HP, attack, strength, defence, magic, ranged, attack_speed,
//          combat_level, slayer_xp, max_hit, attack_styles, drops
```

Legacy alias still accepted: `get_npc_rev233`

### get_item_rev233_openrs2_2293 — Item Data (canonical)

```javascript
get_item_rev233_openrs2_2293({ name: "Abyssal whip" })
// Returns: id, examine, weight, equipable, attack_bonuses, defence_bonuses,
//          attack_speed, members, quest_item
```

Legacy alias still accepted: `get_item_rev233`

### check_varlamore2_rev233_openrs2_2293 — Revision Content Check (canonical)

```javascript
check_varlamore2_rev233_openrs2_2293({ name: "Quetzin" })
// Returns: { is_varlamore2: true, has_elemental_weakness: false, ... }
// Use to verify content is valid for our Rev 233 target
```

Legacy alias still accepted: `check_varlamore2`

---

## Server 3: context7 — API Docs

Search RSMod, Kotlin, or any other library docs.

```javascript
// Step 1: Resolve library
resolve-library-id({ libraryName: "rsmod", query: "PluginScript events" })

// Step 2: Query docs
query-docs({ libraryId: "/rsmod/rsmod", query: "onOpLoc1 event handler" })
```

---

## Server 4: web-search — Web

```javascript
brave_web_search({ query: "OSRS goblin drop table wiki 2024" })
```

---

## Decision Guide: Which Tool to Use?

| Task | Tool |
|------|------|
| What is the sym name for "small fishing net"? | `search_objtypes` on `osrs-cache` |
| What seq ID is the goblin attack anim? | `search_seqtypes` on `osrs-cache` |
| What are a goblin's HP/stats for combat def? | `get_npc_rev233` on `osrs-wiki-rev233` |
| What are the goblin drop table rates? | `get_npc_rev233` + `osrs_wiki_parse_page` |
| What are the attack bonuses on an abyssal whip? | `get_item_rev233` on `osrs-wiki-rev233` |
| Is this content valid for our revision? | `check_varlamore2` on `osrs-wiki-rev233` |
| How does onOpLoc1 work? | `context7` |
| Latest OSRS patch notes? | `web-search` |

---

## Common Workflow: NPC Combat Definition

**RECOMMENDED: Use the Python tools first for bulk work:**
```bash
# 1. Lookup complete NPC data (combines all sources)
python tools/npc_lookup.py "Goblin"
# → Shows: rev 233 symbol, wiki stats, drops, Kronos animations, variants

# 2. Generate Kotlin drop table skeleton
python tools/npc_lookup.py "Goblin" --output kotlin

# 3. Batch process multiple NPCs
python tools/batch_npc_processor.py --tier 1
```

**For individual lookups or verification, use MCP:**
```javascript
// 1. Get combat stats from rev233 wiki (authoritative for our revision)
get_npc_rev233({ name: "Goblin" })
// → hitpoints: 8, attack: 1, strength: 1, defence: 1, attack_speed: 6

// 2. Get cache NPC IDs
search_npctypes({ query: "goblin", pageSize: 20 })
// → id: 3078 (goblin), 3077 (goblin_chieftain), etc.

// 3. Get animation seq IDs
search_seqtypes({ query: "goblin attack", pageSize: 10 })
// → id: 6184 → name "goblin_attack"

// 4. Verify sym name in .data/symbols/seq.sym
// Look up "goblin_attack" → seqs.goblin_attack in BaseSeqs.kt
```

## Common Workflow: Skill Implementation

```javascript
// 1. Get item ID from cache
search_objtypes({ query: "yew logs", pageSize: 5 })
// → id: 1515, name: "yew_logs"

// 2. Verify sym name: objs.yew_logs → find("yew_logs")

// 3. Get loc ID
search_loctypes({ query: "yew tree", pageSize: 5 })
// → id: 10833, name: "yew_tree_10833"

// 4. Get anim ID
search_seqtypes({ query: "woodcutting", pageSize: 10 })
// → seqs.human_woodcutting_axe_rune
```

---

## Tips

1. **`osrs-cache` for IDs, `osrs-wiki-rev233` for stats** — use both together
2. **Sym names differ from wiki names** — always cross-check `.data/symbols/obj.sym`
3. **Rev 233 = Varlamore Part 2** — `check_varlamore2` to confirm content is in-scope
4. **Kronos is rev 184** — wiki-rev233 data is more accurate for our target

*See `docs/SYM_NAMING_GUIDE.md` for known sym name quirks.*
