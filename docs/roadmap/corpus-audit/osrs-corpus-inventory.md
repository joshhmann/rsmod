# OSRS Research Corpus — Inventory Audit

**Archive:** `/var/lib/pixelrag/osrs.tar.gz` (on 192.168.0.17)
**Audit date:** 2026-06-20
**Auditor:** Mai (ops)

## Tarball Integrity

| Property | Value |
|----------|-------|
| Exists | ✅ Yes |
| Size (compressed) | 20 MB |
| Size (extracted) | 41 MB |
| File count | 1,019 (archive: 1,046 — includes `.` dir entries) |
| Extraction target | `/tmp/osrs-corpus-audit/osrs/` |

## Top-Level Structure

```
osrs/
├── buckets/         (5.1 MB, 16 files) — Raw wiki-scraped JSON
├── cache-diffs/     (19 MB, 593 files) — OSRS revision change logs
│   ├── html/        (2019–2025)
│   ├── wikitext/    (2019–2025)
│   └── parsed/      (all.json — 4.9 MB)
├── content/         (4.2 MB) — Wiki article HTML pages
├── dependencies/    (170 KB) — Quest dependency trees
├── ironman-guide/   (540 KB) — Ironman progression phases
├── parsed-data/     (6.2 MB, 21 files) — Cleaned/processed datasets
├── quests/          (5.1 MB, 217 files) — Quest guide HTML pages
├── skills/          (1.1 MB, 39 files) — Skill training guides
├── __pycache__/     (9 KB) — Python cache
└── osrs_data.py     (7 KB) — Python data access module
```

## File Type Breakdown

| Extension | Count | Notes |
|-----------|-------|-------|
| `.html` | 674 | Wiki pages, quest guides, skill guides |
| `.txt` | 296 | Cache diff wikitext files |
| `.json` | 47 | Structured data (buckets, parsed-data, dependencies) |
| `.py` | 1 | `osrs_data.py` — data access layer |
| `.pyc` | 1 | Compiled cache |

## Bucket Files (Raw Wiki Data)

| File | Size | Description |
|------|------|-------------|
| `dropsline.json` | 21 MB | Raw drop lines from wiki |
| `infobox_item.json` | 8.0 MB | Item infobox data |
| `infobox_monster.json` | 3.5 MB | Monster infobox data |
| `infobox_npc.json` | 3.2 MB | NPC infobox data |
| `varbit.json` | 585 KB | Varplayer / varbit definitions |
| `quest.json` | 576 KB | Quest metadata |
| `infobox_construction.json` | 282 KB | Construction skill data |
| `transcript.json` | 222 KB | Dialogue transcripts |
| `combat_achievement.json` | 181 KB | Combat achievements |
| `infobox_shop.json` | 146 KB | Shop data |
| `infobox_location.json` | 126 KB | Location data |
| `infobox_spell.json` | 122 KB | Spell data |
| `money_making_guide.json` | 118 KB | Moneymaking methods |
| `dependency_list.json` | 97 KB | Dependency trees |
| `infobox_activity.json` | 20 KB | Activity data |
| `_index.json` | 4 KB | Master index |

## Parsed Data Files (Cleaned Datasets)

| File | Size | Description |
|------|------|-------------|
| `drop_tables.json` | 8.3 MB | Structured drop tables |
| `drops_by_source.json` | 7.7 MB | Drops indexed by monster name |
| `item_by_id.json` | 5.6 MB | Items by game ID |
| `item_index.json` | 5.4 MB | Item master index |
| `item_by_name.json` | 4.2 MB | Items by lowercase name |
| `monster_compendium.json` | 2.0 MB | Monster stats compendium |
| `npc_by_id.json` | 1.5 MB | NPCs by game ID |
| `npc_directory.json` | 1.5 MB | NPC master directory |
| `monster_by_name.json` | 935 KB | Monsters by lowercase name |
| `npc_by_name.json` | 883 KB | NPCs by lowercase name |
| `quest_progression.json` | 601 KB | Quest progression data |
| `quest_lookup.json` | 382 KB | Quest lookup index |
| `varbit_index.json` | 361 KB | Varbit lookup index |
| `construction.json` | 170 KB | Construction POH data |
| `combat_achievements_by_tier.json` | 123 KB | CA by tier |
| `shops.json` | 102 KB | Shop data |
| `money_making.json` | 83 KB | Moneymaking data |
| `spellbook.json` | 40 KB | Spellbook data |
| `activities.json` | 7 KB | Activity data |
| `_index.json` | 308 B | Master index |

## Dataset Counts

| Dataset | Count |
|---------|-------|
| Items (by name) | 11,872 |
| Items (by ID) | 16,133 |
| NPCs (by name) | 3,906 |
| NPCs (by ID) | 6,970 |
| Monsters | 1,372 |
| Quests | 217 |
| Drop sources | 2,061 |
| Skills | 23 skill guides |
| Cache diffs | 593 |

## Validation Scripts

A single Python module exists: `osrs_data.py` (217 lines). No separate test scripts, check scripts, or Makefile are included. The module provides:

- `OSRS` class — lazy-loads all datasets on first access
- `db.item(name=...)` — item lookup by name (case-insensitive, fallback to substring)
- `db.monster(name=...)` — monster lookup by name
- `db.npc(name=...)` — NPC lookup by name or ID
- `db.quest(name=...)` — quest lookup with fuzzy fallback
- `db.drops(source=...)` — drop table lookup
- `db.skill_reqs(level, skill_name=...)` — quest skill requirement queries

**Limitation:** No whitespace trimming on lookup keys (leading/trailing spaces cause MISS).

## osrs_data.py Normalization Tests

| Test | Result |
|------|--------|
| `item("Rune scimitar")` | ✅ Found (id=26262) |
| `item("rune scimitar")` | ✅ Found |
| `item("Rune Scimitar")` | ✅ Found |
| `item("  rune scimitar  ")` | ❌ MISS (no whitespace trim) |
| `npc("Goblin")` | ✅ Found (id=4905) |
| `npc("goblin")` | ✅ Found |
| `npc("Cow")` | ✅ Found (id=hist10617) |
| `npc("chicken")` | ✅ Found (id=10556) |

**Note:** NPC IDs from the corpus often have `hist` prefix (historical wiki IDs), not rev 233 cache IDs.

## Quests Included (217)

All 217 quest guides in HTML format covering the full OSRS quest list. Quest dependency trees and progression data are available in `dependencies/` and `parsed-data/quest_progression.json`.

## Skills Included (23)

Full skill training guides for: Agility, Attack, Construction, Cooking, Crafting, Defence, Farming, Firemaking, Fishing, Fletching, Herblore, Hitpoints, Hunter, Magic, Mining, Prayer, Ranged, Runecraft, Slayer, Smithing, Strength, Thieving, Woodcutting.
Plus minigames: Giants' Foundry, Guardians of the Rift, Mahogany Homes, Pyramid Plunder, Shooting Stars, Sulliusceps, Tempoross, Volcanic Mine, Wintertodt.
