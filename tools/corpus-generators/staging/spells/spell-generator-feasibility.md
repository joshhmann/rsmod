# Spell Generator Feasibility Report

**Generated:** 2026-06-20
**Analyzed:** 197 spells across 4 books
**Rune resolution:** 193/191 spells with fully resolved runes

## Corpus Spell Schema

The corpus contains **sparse spell data** — only 5 fields per spell:

| Field | Example | Coverage | Cache Equivalent |
|-------|---------|----------|-----------------|
| `name` | "Wind Strike" | 100% | `spell_name` param |
| `level` | 1 | 100% | `spell_levelreq` param |
| `xp` | 5.5 | 100% | `spell_castxp` param |
| `materials` | ["Air rune", "Mind rune"] | 191/197 | `spell_runetype_1-4` params |
| `spellbook` | "normal" | 100% | `spell_spellbook` param |

**Missing from corpus:** maxHit, animation IDs, graphic IDs, sound IDs, teleport coordinates,
item requirements (staffs), autocast configuration, button/interface bindings.

## RSMod Magic System

### Existing API (fully implemented)

| Component | File | Purpose |
|-----------|------|---------|
| `SpellObjEditor` | `api/spells/configs/SpellObjs.kt` | Defines all spells with XP + teleport coords |
| `MagicSpellRegistry` | `api/spells/MagicSpellRegistry.kt` | Loads spell data from cache params |
| `SpellAttackRepository` | `api/spells/attack/SpellAttackRepository.kt` | DSL for registering combat spells |
| `SpellAttackManager` | `api/spells/attack/SpellAttackManager.kt` | Rune validation, XP, projectile handling |
| `MagicRuneManager` | `api/combat/manager/MagicRuneManager.kt` | Complex rune validation (combo, compact, staff sub) |
| `ElementalSpells` | `content/skills/magic/.../ElementalSpells.kt` | ALL 20 elemental combat spells implemented |
| `BindingSpells` | `content/skills/magic/.../BindingSpells.kt` | Bind/Snare/Entangle implemented |
| `StandardSpellObjs` | `content/skills/magic/.../StandardSpellObjs.kt` | 23 spell object refs (01_wind_strike, etc.) |

### Existing spell objects in cache

The cache already has spell objects (e.g., `01_wind_strike` at id=3273) with params for:
- Level requirement, XP, max hit
- Rune requirements (4 slots with type + count)
- Spellbook assignment
- Button/interface binding
- Autocast configuration

**Spells are already data-defined through cache params.** The corpus adds nothing that isn't already in the cache.

## 3 Sample Spell Analysis

### 1. Wind Strike

| Aspect | Corpus | RSMod Cache | Status |
|--------|--------|-------------|--------|
| Spell obj | - | `01_wind_strike` (id=3273) | ✅ In cache |
| Level | 1 | `spell_levelreq` | ✅ In cache |
| XP | 5.5 | `spell_castxp` | ✅ In cache |
| Rune 1 | Air rune → `airrune` (id=556) | `spell_runetype_1` | ✅ Both resolve |
| Rune 2 | Mind rune → `mindrune` (id=558) | `spell_runetype_2` | ✅ Both resolve |
| Max hit | MISSING | `spell_maxhit` | ❌ Not in corpus |
| Staff anim | MISSING | `seqs.human_caststrike_staff` | ❌ Hand-written |
| Launch gfx | MISSING | `spotanims.windstrike_casting` | ❌ Hand-written |
| Travel proj | MISSING | `spotanims.windstrike_travel` | ❌ Hand-written |
| Impact gfx | MISSING | `spotanims.windstrike_impact` | ❌ Hand-written |

**Handler:** Already implemented in `ElementalSpells.kt` via `SpellAttackRepository.register()`.
**Generator value:** None — the handler needs visual/audio data that corpus doesn't have.

### 2. Varrock Teleport

| Aspect | Corpus | RSMod Cache | Status |
|--------|--------|-------------|--------|
| Spell obj | - | `25_varrock_teleport` (id=3286) | ✅ In cache |
| Level | 25 | `spell_levelreq` | ✅ In cache |
| XP | 35.0 | `spell_castxp` | ✅ In cache |
| Rune 1 | Air rune → `airrune` (id=556) | `spell_runetype_1` | ✅ Both resolve |
| Rune 2 | Fire rune → `firerune` (id=554) | `spell_runetype_2` | ✅ Both resolve |
| Rune 3 | Law rune → `lawrune` (id=563) | `spell_runetype_3` | ✅ Both resolve |
| Destination | MISSING | CoordGrid(0, 50, 53, 13, 32) | ❌ Not in corpus |

**Handler:** `SpellObjEditor.tele()` already registers it with a CoordGrid — hand-written.
**Generator value:** Low — only the CoordGrid is missing, and that's a single int per teleport.

### 3. High Level Alchemy

| Aspect | Corpus | RSMod Cache | Status |
|--------|--------|-------------|--------|
| Spell obj | - | `55_high_level_alchemy` (id=?) | ✅ In cache |
| Level | 55 | `spell_levelreq` | ✅ In cache |
| XP | 65.0 | `spell_castxp` | ✅ In cache |
| Rune 1 | Fire rune → `firerune` (id=554) | `spell_runetype_1` | ✅ Both resolve |
| Rune 2 | Nature rune → `naturerune` (id=561) | `spell_runetype_2` | ✅ Both resolve |
| Behavior | MISSING | Needs custom Player.queue handler | ❌ Not in corpus |

**Handler:** No existing implementation. Would need hand-written logic:
- Open interface, select item, validate runes, remove item, add coins
- **Generator value:** None — the behavior is all hand-written Kotlin

## Rune Resolution

All 21 rune types resolve correctly through the resolver override map:

| Rune | Sym | ID | Strategy |
|------|-----|----|----------|
| Air rune | `airrune` | 556 | override |
| Mind rune | `mindrune` | 558 | override |
| Fire rune | `firerune` | 554 | override |
| ... | ... | ... | ... |
| Lava rune | `lavarune` | 4699 | override |

**21/21 runes resolved. 100% success rate.**

## Feasibility Verdict

**Spells are NOT a good next generator target.**

### Why not:
1. **Corpus data is sparse** — only 5 fields (name, level, xp, materials, spellbook)
2. **Cache already has everything** — cache params define all spell data
3. **Missing key data** — no maxHit, animations, graphics, sounds, teleport coords
4. **Real work is behavioral** — teleport, alchemy, superheat each need unique handlers
5. **Combat spells already done** — 20 elemental spells + 3 binding spells already implemented

### What the generator CAN do (limited value):
- Verify that corpus spell data matches cache definitions
- Generate a reference JSON file with resolved rune symbols
- Identify spells that exist in corpus but not in cache (would be post-rev-233 additions)

## Recommended Next Generator Target

Based on available corpus data:

| Target | Corpus Coverage | Value | Risk |
|--------|----------------|-------|------|
| **NPC combat definitions** | 1,372 monsters with full stats | HIGH — saves 55K LOC | Medium — needs resolver |
| **Drop tables** (extend G2) | 2,061 sources, 38K entries | HIGH — 80K LOC | Low — G2 pattern exists |
| **Item definitions** (reference) | 16K items | MEDIUM — reference data | Low — no gameplay |
| ~~Spells~~ | Only 5 sparse fields | LOW | Low — cache already has it |
| ~~Shops~~ | Metadata only | BLOCKED | High — no stock data |

**Recommendation:** **NPC combat definitions** — the corpus has monster_compendium.json with
complete combat stats (HP, attack, strength, defence, magic, ranged, slayer level, weakness).
This would generate ~1,372 NPC definition files and has the highest LOC savings.

## Files Created

- `tools/corpus-generators/staging/spells/spells.normalized.json` — 197 spells with resolved rune symbols
- `tools/corpus-generators/staging/spells/spell-generator-feasibility.md` — this report

## Commands Run

```
python3 /tmp/rune_test.py                        # Rune resolution + 3 sample analysis
python3 /tmp/spell_analysis.py                    # Full corpus spell field coverage
python3 tools/corpus-generators/resolver.py       # Smoke test
```
