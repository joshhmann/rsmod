# F2P Task Registry Summary

**Date Created**: 2026-02-26  
**Based On**: `F2P_CONTENT_COMPLETENESS_GUIDE.md` and `F2P_CRITICAL_PATH.md`

---

## ✅ Tasks Created in Registry

### 🚨 Critical Blockers (Tier 1)

| Task ID | Title | Owner | Module | Wave | Status | Blocks |
|---------|-------|-------|--------|------|--------|--------|
| F2P-CRIT-1 | Add Maple Trees to F2P Areas | Unassigned | woodcutting | 1 | 🔴 Pending | FLETCHING, money making |
| F2P-CRIT-2 | Add Yew Trees to F2P Areas | Unassigned | woodcutting | 1 | 🔴 Pending | YEW BOWS, money making |
| F2P-CRIT-3 | Implement Rune Bar Smelting | Unassigned | smithing | 1 | 🔴 Pending | F2P-CRIT-4 |
| F2P-CRIT-4 | Implement Rune Equipment Smithing | Unassigned | smithing | 1 | 🔴 Pending | BIS F2P melee gear |
| F2P-CRIT-5 | Implement Hill Giants with Full Drops | Unassigned | npc-combat | 1 | 🔴 Pending | Obor, Herblore, money |
| F2P-CRIT-6 | Implement Moss Giants with Full Drops | Unassigned | npc-combat | 1 | 🔴 Pending | Bryophyta, RC bypass |

### ⚔️ Boss Content (Tier 2)

| Task ID | Title | Owner | Module | Wave | Status | Blocked By |
|---------|-------|-------|--------|------|--------|------------|
| F2P-BOSS-1 | Implement Obor Boss | Unassigned | bosses | 2 | 🔴 Pending | F2P-CRIT-5 |
| F2P-BOSS-2 | Implement Bryophyta Boss | Unassigned | bosses | 2 | 🔴 Pending | F2P-CRIT-6 |

### 🎯 Quests & BIS Gear (Tier 2)

| Task ID | Title | Owner | Module | Wave | Status | Blocked By |
|---------|-------|-------|--------|------|--------|------------|
| F2P-QUEST-1 | Implement Dragon Slayer I Quest | Unassigned | quests | 2 | 🔴 Pending | - |
| F2P-BIS-1 | Implement Green D'hide Armor Crafting | Unassigned | crafting | 2 | 🔴 Pending | F2P-QUEST-1 |
| F2P-BIS-2 | Implement Maple and Yew Bow Fletching | Unassigned | fletching | 1 | 🔴 Pending | F2P-CRIT-1, F2P-CRIT-2 |

---

## 📊 Task Statistics

| Category | Count | Wave 1 | Wave 2 |
|----------|-------|--------|--------|
| Critical Blockers | 6 | 6 | 0 |
| Bosses | 2 | 0 | 2 |
| Quests | 1 | 0 | 1 |
| BIS Gear | 2 | 0 | 2 |
| **TOTAL** | **11** | **7** | **4** |

---

## 🔗 Dependency Graph

```
WAVE 1 (Can Start Immediately):
├── F2P-CRIT-1: Maple trees
├── F2P-CRIT-2: Yew trees
├── F2P-CRIT-3: Rune bar smelting
│   └── (needs: Mining verification)
├── F2P-CRIT-4: Rune equipment
│   └── BLOCKED BY: F2P-CRIT-3
├── F2P-CRIT-5: Hill Giants
└── F2P-CRIT-6: Moss Giants

WAVE 2 (Depends on Wave 1):
├── F2P-BOSS-1: Obor
│   └── BLOCKED BY: F2P-CRIT-5 (Hill Giants)
├── F2P-BOSS-2: Bryophyta
│   └── BLOCKED BY: F2P-CRIT-6 (Moss Giants)
├── F2P-QUEST-1: Dragon Slayer I
│   └── (standalone quest)
├── F2P-BIS-1: Green d'hide
│   └── BLOCKED BY: F2P-QUEST-1 (Dragon Slayer for equip)
└── F2P-BIS-2: Maple/Yew bows
    └── BLOCKED BY: F2P-CRIT-1, F2P-CRIT-2 (trees)
```

---

## 🎯 What These Tasks Unblock

### Combat Viability
- **Rune Scimitar** (F2P-CRIT-4): BIS F2P melee weapon
- **Rune Platebody** (F2P-CRIT-4 + F2P-QUEST-1): BIS F2P melee armor
- **Green D'hide** (F2P-BIS-1): BIS F2P ranged armor
- **Maple/Yew Shortbows** (F2P-BIS-2): F2P ranged weapons

### Money Making
- **Yew Logs** (F2P-CRIT-2): 50-100k/hr
- **Big Bones** (F2P-CRIT-5,6): 30-50k/hr
- **Limpwurt Roots** (F2P-CRIT-5): Herblore money + supplies
- **Obor/Bryophyta** (F2P-BOSS-1,2): Boss money (100-300k/hr)

### Skill Training
- **Maple Trees** (F2P-CRIT-1): 40k WC XP/hr
- **Yew Trees** (F2P-CRIT-2): 25k WC XP/hr, money
- **Giants** (F2P-CRIT-5,6): Combat training, prayer XP (big bones)

### Quest Completion
- **Dragon Slayer I** (F2P-QUEST-1): Required for Rune platebody
- **BIS Equipment Access**: Most BIS gear requires this quest

---

## 👥 Recommended Task Assignment

### For Kimi (Content Implementer)
**Can claim now (Wave 1):**
- F2P-CRIT-1: Maple trees
- F2P-CRIT-2: Yew trees
- F2P-CRIT-3: Rune bar smelting
- F2P-CRIT-4: Rune equipment smithing

**Can claim when ready (Wave 2):**
- F2P-QUEST-1: Dragon Slayer I
- F2P-BIS-1: Green d'hide crafting

### For Codex (Combat/NPCs)
**Can claim now (Wave 1):**
- F2P-CRIT-5: Hill Giants
- F2P-CRIT-6: Moss Giants

**Can claim when ready (Wave 2):**
- F2P-BOSS-1: Obor
- F2P-BOSS-2: Bryophyta

### For Claude (Content Verification)
**Can claim now (Wave 1):**
- F2P-BIS-2: Maple/Yew bow fletching
- Verify existing F2P content
- Create F2P test scripts

---

## ⏱️ Estimated Timeline

### Week 1: Foundation
- Days 1-2: F2P-CRIT-1, F2P-CRIT-2 (trees)
- Days 3-4: F2P-CRIT-3, F2P-CRIT-4 (smithing)
- Days 5-7: F2P-CRIT-5, F2P-CRIT-6 (giants)

### Week 2: Quests & BIS
- Days 8-10: F2P-QUEST-1 (Dragon Slayer)
- Days 11-12: F2P-BIS-1 (d'hide), F2P-BIS-2 (bows)
- Days 13-14: Testing and fixes

### Week 3: Bosses
- Days 15-17: F2P-BOSS-1 (Obor)
- Days 18-19: F2P-BOSS-2 (Bryophyta)
- Days 20-21: Integration testing

**Total: 3 weeks for critical F2P completeness**

---

## ✅ F2P Definition of Done (After These Tasks)

A player should be able to:

### Early Game (Level 1-20)
- [ ] Obtain all bronze/iron/steel tools
- [ ] Train all gathering skills
- [ ] Smith basic equipment

### Mid Game (Level 20-40)
- [ ] Obtain mithril equipment
- [ ] Train on willow/maple trees
- [ ] Cook lobsters, swordfish

### Late Game (Level 40-60)
- [ ] Smith and equip Rune scimitar
- [ ] Smith Rune platelegs/helm (body requires quest)
- [ ] Craft and equip Green d'hide armor
- [ ] Fletch Maple/Yew bows
- [ ] Train at Hill/Moss Giants
- [ ] Obtain Obor/Bryophyta keys

### End Game (Level 60-99)
- [ ] Complete Dragon Slayer I
- [ ] Equip Rune platebody
- [ ] Fight Obor and Bryophyta
- [ ] Cut Yews for money
- [ ] Achieve 99s in all F2P skills

---

## 🔍 Verification Commands

After completing tasks, verify with:

```bash
# Check tree spawns
/list_objtypes tree

# Check smithing recipes
/list_smithing_recipes rune

# Check NPC spawns
/list_npc_spawns hill_giant

# Check quest availability
/list_quests available

# Check boss instances
/list_boss_instances obor
```

---

## 📋 Next Steps

1. **Agents claim tasks** via `claim_task(taskId, agent)`
2. **Check for conflicts** with `check_conflicts([paths])`
3. **Lock files** before editing
4. **Run build gate** after implementation
5. **Complete tasks** and update documentation

---

## 📚 Related Documents

- `docs/F2P_CONTENT_COMPLETENESS_GUIDE.md` - Full F2P audit
- `docs/F2P_CRITICAL_PATH.md` - Quick reference
- `docs/COMPLETE_CONTENT_DEPENDENCY_MAP.md` - Dependency research
- `docs/CONTENT_AUDIT.md` - Overall project status

---

**Registry Status**: 11 tasks created, ready for claiming  
**Priority**: Start with Wave 1 (Critical Blockers)  
**Coordinator**: Tasks ready for agent assignment

