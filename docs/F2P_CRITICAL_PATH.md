# F2P Critical Path Summary

**Quick reference for immediate action items to achieve F2P completeness.**

---

## 🚨 CRITICAL BLOCKERS (Fix These First)

These items block entire skill trees or BIS equipment:

### Blocker #1: Rune Equipment Not Smithable
```
PROBLEM: Rune equipment requires 85-99 Smithing
         Currently may not be implemented
BLOCKS: BIS F2P melee gear
        High-level combat viability
        
SOLUTION: SKILL-SMITH-F2P-RUNE
TASK: Implement rune bar smelting and equipment smithing
- Rune bar: 85 Smithing, 1 runite ore + 8 coal
- Rune dagger: 85 Smithing
- Rune scimitar: 90 Smithing ⭐ BIS F2P weapon
- Rune platebody: 99 Smithing ⭐ BIS F2P body (after Dragon Slayer)
- Rune platelegs: 99 Smithing
- Rune full helm: 87 Smithing
```

### Blocker #2: Maple/Yew Trees Missing
```
PROBLEM: Maple (45 WC) and Yew (60 WC) trees not in F2P
BLOCKS: Fletching progression (maple bows)
        F2P money making (yew logs = 50k/hr)
        Firemaking training
        
SOLUTION: SKILL-WC-F2P-TREES
TASK: Add tree spawns to F2P locations
- Maple: 5-6 trees around Seers' Village (F2P?), Crafting Guild
- Yew: 2-3 trees at Edgeville, 3-4 at Falador, 2 at Varrock
```

### Blocker #3: Green D'hide Crafting
```
PROBLEM: Cannot craft green d'hide armor
BLOCKS: BIS F2P ranged armor
        Ranged combat viability
        
SOLUTION: SKILL-CRAFT-F2P-DHIDE
TASK: Implement d'hide crafting
- Green d'hide vambraces: 57 Crafting
- Green d'hide chaps: 60 Crafting
- Green d'hide body: 63 Crafting
- Requires: Green dragonhide from F2P dragons ( Corsair Cove?)
```

### Blocker #4: Hill/Moss Giants
```
PROBLEM: Key F2P training monsters incomplete
BLOCKS: F2P combat training
        Money making (big bones = 300gp each)
        Boss access (Obor, Bryophyta keys)
        Limpwurt roots (Herblore secondary)
        
SOLUTION: NPC-F2P-GIANTS
TASK: Implement with full drop tables
- Hill Giant (CB 28): Edgeville Dungeon, Giants Plateau
- Moss Giant (CB 42): Varrock Sewers, Crandor (F2P?), Moss Giant Island
- Drops: Big bones (100%), Limpwurt roots, Keys to bosses
```

---

## 📊 Visual Dependency Chain

```
F2P COMPLETE COMBAT SETUP

MELEE BUILD:
Rune Scimitar (40 Attack, 90 Smithing)
    ├── Mining 85: Runite ore
    ├── Mining 30: Coal x8
    └── Smithing 90: Smelt + Smith
    
Rune Platebody (40 Defence, 99 Smithing + Dragon Slayer)
    ├── Mining 85: Runite ore x5
    ├── Mining 30: Coal x40
    ├── Smithing 99: Smelt + Smith
    └── Quest: Dragon Slayer I

Rune Full Helm (87 Smithing)
Rune Platelegs (99 Smithing)

TOTAL RESOURCES:
- Runite ore: 14
- Coal: 112+ 
- Smithing: 99 (for full set)

RANGED BUILD:
Maple Shortbow (50 Fletching)
    ├── WC 45: Maple logs
    └── Fletching 50: Craft bow
        └── Crafting 10: Bow string from flax

Yew Shortbow (65 Fletching) - ENDGAME
    ├── WC 60: Yew logs
    └── Fletching 65: Craft bow

Green D'hide Set (57-63 Crafting)
    ├── Combat: Green dragons (CB 79)
    ├── Crafting 57: Vambraces
    ├── Crafting 60: Chaps
    └── Crafting 63: Body
        └── Requires tanning (Crafting 57)

Rune Arrows (75 Fletching + 90 Smithing)
    ├── WC: Logs for shafts
    ├── Smithing 90: Rune arrowheads
    └── Combat/Farming: Feathers

MAGIC BUILD:
Staff of Air (Buy from Zaff)
Wizard Robes (Drop from dark wizards)

Fire Strike (13 Magic)
    ├── Runecrafting 14: Fire runes
    ├── Runecrafting 1: Air runes
    └── Runecrafting 2: Mind runes
        └── Quest: Rune Mysteries
```

---

## 🎯 Immediate Task Queue

### Priority 1: Critical Blockers (This Week)

| Task ID | Description | Owner | Est. Time | Dependencies |
|---------|-------------|-------|-----------|--------------|
| `F2P-CRIT-1` | Add Maple trees to F2P | kimi | 2h | None |
| `F2P-CRIT-2` | Add Yew trees to F2P | kimi | 2h | None |
| `F2P-CRIT-3` | Implement Rune bar smelting | kimi | 4h | Mining verification |
| `F2P-CRIT-4` | Implement Rune equipment smithing | kimi | 6h | Rune bars |
| `F2P-CRIT-5` | Implement Hill Giant + drops | codex | 8h | None |
| `F2P-CRIT-6` | Implement Moss Giant + drops | codex | 8h | None |

### Priority 2: BIS Gear (Week 2)

| Task ID | Description | Owner | Est. Time | Dependencies |
|---------|-------------|-------|-----------|--------------|
| `F2P-BIS-1` | Implement Maple bow fletching | Claude | 4h | Maple trees |
| `F2P-BIS-2` | Implement Yew bow fletching | Claude | 4h | Yew trees |
| `F2P-BIS-3` | Implement Green d'hide crafting | kimi | 6h | Crafting system |
| `F2P-BIS-4` | Add Green dragons to F2P | codex | 4h | Corsair Cove? |
| `F2P-BIS-5` | Verify Rune arrow smithing | kimi | 2h | Smithing system |

### Priority 3: Quests (Week 3)

| Task ID | Description | Owner | Est. Time | Dependencies |
|---------|-------------|-------|-----------|--------------|
| `F2P-QUEST-1` | Dragon Slayer I | opencode | 16h | Oziach, Elvarg |
| `F2P-QUEST-2` | The Knight's Sword | kimi | 8h | Blurite, Thurgo |
| `F2P-QUEST-3` | Doric's Quest | kimi | 4h | None |
| `F2P-QUEST-4` | Other F2P quests | various | 40h | Various |

### Priority 4: Bosses (Week 4)

| Task ID | Description | Owner | Est. Time | Dependencies |
|---------|-------------|-------|-----------|--------------|
| `F2P-BOSS-1` | Obor (Hill Giant boss) | codex | 12h | Hill giants, key |
| `F2P-BOSS-2` | Bryophyta (Moss Giant boss) | codex | 12h | Moss giants, key |

---

## 🧪 F2P Verification Checklist

Use this to test F2P completeness:

### Character Creation Test
```
1. Create new F2P character
2. Can obtain Bronze pickaxe? [ ]
3. Can obtain Bronze axe? [ ]
4. Can mine Copper/Tin? [ ]
5. Can chop Normal trees? [ ]
6. Can fish Shrimp? [ ]
7. Can make Bronze bar? [ ]
8. Can make Bronze dagger? [ ]
```

### Mid-Game Test (Level 40-50)
```
1. Can obtain Rune pickaxe? [ ]
2. Can obtain Rune axe? [ ]
3. Can chop Maple trees? [ ]
4. Can chop Yew trees? [ ]
5. Can smith Rune scimitar? [ ]
6. Can craft Green d'hide body? [ ]
7. Can fletch Maple shortbow? [ ]
8. Can fight Hill Giants? [ ]
9. Can get Obor key? [ ]
10. Can fight Obor? [ ]
```

### Endgame Test (Level 99s)
```
1. Can smith Rune platebody? [ ]
2. Can complete Dragon Slayer I? [ ]
3. Can equip Rune platebody? [ ]
4. Can fight Bryophyta? [ ]
5. Can make Yew shortbow? [ ]
6. Can cut Yew logs for money? [ ]
7. Can High Alch items? [ ]
8. Can access all F2P teleports? [ ]
```

---

## 💰 F2P Money Making Verification

| Method | Expected GP/Hr | Test Result | Status |
|--------|----------------|-------------|--------|
| Yew logs | 50-100k | [ ] | 🔴 Not ready (no yews) |
| Lobsters | 30-60k | [ ] | 🟡 Test needed |
| Swordfish | 40-80k | [ ] | 🟡 Test needed |
| Big bones | 30-50k | [ ] | 🔴 Not ready (no giants) |
| Cowhides | 20-40k | [ ] | 🟢 Should work |
| Iron ore | 30-50k | [ ] | 🟢 Should work |
| High Alchemy | Varies | [ ] | 🟡 Test needed |
| Wine of Zamorak | 100k+ | [ ] | 🔴 Not implemented |

---

## 📈 F2P XP Rates (Verification Targets)

| Skill | Method | Level | Expected XP/Hr | Test |
|-------|--------|-------|----------------|------|
| Woodcutting | Willow | 30 | 30k | [ ] |
| Woodcutting | Maple | 45 | 40k | [ ] |
| Woodcutting | Yew | 60 | 25k | [ ] |
| Mining | Iron | 15 | 15k | [ ] |
| Mining | Coal | 30 | 20k | [ ] |
| Fishing | Trout/Salmon | 20-30 | 20k | [ ] |
| Fishing | Lobster | 40 | 15k | [ ] |
| Cooking | Lobster | 40 | 150k | [ ] |
| Smithing | Steel bars | 30 | 40k | [ ] |
| Crafting | Gems | 20-43 | 50k | [ ] |
| Firemaking | Oak | 15 | 45k | [ ] |

---

## 🎮 F2P Playthrough Test Script

```python
# Pseudo-code for F2P completeness test

def test_f2p_complete():
    player = create_f2p_account()
    
    # Phase 1: Tutorial Island (skip for test)
    
    # Phase 2: Early Game (Levels 1-20)
    assert player.can_mine("copper_ore")
    assert player.can_mine("tin_ore")
    assert player.can_smith("bronze_bar")
    assert player.can_equip("bronze_scimitar")
    
    assert player.can_cut("normal_tree")
    assert player.can_cut("oak_tree")
    assert player.can_fletch("shortbow")
    
    assert player.can_fish("shrimp")
    assert player.can_cook("shrimp")
    
    # Phase 3: Mid Game (Levels 20-40)
    assert player.can_mine("iron_ore")
    assert player.can_smith("iron_bar")
    assert player.can_smith("steel_bar")
    
    assert player.can_cut("willow_tree")
    assert player.can_fletch("willow_shortbow")
    
    assert player.can_fish("trout")
    assert player.can_fish("salmon")
    
    assert player.can_craft("leather_body")
    
    # Phase 4: Late Game (Levels 40-60)
    assert player.can_mine("gold_ore")
    
    assert player.can_cut("maple_tree")  # 🔴 CRITICAL
    assert player.can_fletch("maple_shortbow")  # 🔴 CRITICAL
    
    assert player.can_craft("green_dhide_body")  # 🔴 CRITICAL
    
    assert player.can_smith("rune_scimitar")  # 🔴 CRITICAL
    
    assert player.can_kill("hill_giant")  # 🔴 CRITICAL
    assert player.can_kill("moss_giant")  # 🔴 CRITICAL
    
    # Phase 5: End Game (Levels 60-99)
    assert player.can_cut("yew_tree")  # 🔴 CRITICAL
    assert player.can_fletch("yew_shortbow")  # 🔴 CRITICAL
    
    assert player.can_smith("rune_platebody")  # 🔴 CRITICAL
    assert player.has_completed_quest("dragon_slayer_i")  # 🔴 CRITICAL
    
    assert player.can_kill("obor")  # 🔴 CRITICAL
    assert player.can_kill("bryophyta")  # 🔴 CRITICAL
    
    print("✅ F2P content is complete!")
```

---

## 📋 Quick Reference: What To Ask Kimi/Claude/Codex

### Ask Kimi (Full Implementation):
- "Implement Rune equipment smithing (F2P-CRIT-3, F2P-CRIT-4)"
- "Implement Green d'hide crafting (F2P-BIS-3)"
- "Implement Maple/Yew tree spawns (F2P-CRIT-1, F2P-CRIT-2)"
- "Implement F2P quests (F2P-QUEST-2, F2P-QUEST-3)"

### Ask Claude (Content/Verification):
- "Verify all F2P magic spells work"
- "Create F2P bot test scripts"
- "Implement Maple bow fletching (F2P-BIS-1)"

### Ask Codex (NPCs/Combat):
- "Implement Hill/Moss Giants with drops (F2P-CRIT-5, F2P-CRIT-6)"
- "Implement Obor boss (F2P-BOSS-1)"
- "Implement Bryophyta boss (F2P-BOSS-2)"

---

**Status**: Ready for immediate implementation
**Priority**: CRITICAL blockers first (Rune gear, trees, giants)
**Timeline**: 4-6 weeks for complete F2P

