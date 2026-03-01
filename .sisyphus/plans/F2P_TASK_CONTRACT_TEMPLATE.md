# F2P Task Contract Template

**All F2P tasks must include explicit acceptance criteria following this format.**

---

## Contract Structure

Every task description should include:

```
Module: <path>
Template: <reference implementation>
<specific details>
Done when: <acceptance criteria>
```

---

## Examples by Category

### World/Location Tasks

```
Module: rsmod/content/generic/ladders/
Template: Follow existing ladder implementation in generic-locs
Details: All F2P ladder locations - Lumbridge castle floors, Varrock buildings, 
Falador buildings, church towers, dungeon entrances
Done when:
- All F2P ladders work with up/down options
- Proper destination coordinates configured for each ladder
- Ladder animations play correctly
- Build passes for generic-locs module
- Bot test verifies traversal between 3+ key locations
```

### NPC Combat Tasks

```
Module: rsmod/content/other/npc-combat/
Template: Follow F2PMonsterCombatScript.kt pattern
NPCs: Skeleton (level 13, 21, 25 variants)
Stats: HP, attack, defence, attack speed from Kronos data
Done when:
- All skeleton variants have combat stats defined
- Attack animation configured (seqs.skeleton_attack)
- Death animation configured (seqs.skeleton_death)
- Proper aggression settings (aggressive in wildy/dungeons)
- NPC retaliation works via combat engine
- Build passes for npc-drops module
- Bot test verifies combat with skeletons
```

### Drop Table Tasks

```
Module: rsmod/content/other/npc-drops/
Template: Follow F2pDropTables.kt pattern
NPC: Skeleton
Drops: Bones (100%), coins, iron equipment, clue scrolls
Done when:
- Drop table configured with proper weights
- Guaranteed drops (bones) always drop
- All items exist in rev 233 symbols
- Drop rates match OSRS wiki
- Build passes for npc-drops module
- Bot test verifies drops over 50+ kills
```

### Shop Tasks

```
Module: rsmod/content/areas/city/lumbridge/
Template: Follow existing shop implementations
Shop: Lumbridge General Store
Stock: pots, jugs, buckets, tinderboxes, hammers
Done when:
- Shopkeeper NPC spawned
- Shop interface opens on talk
- Buy/sell prices correct
- Stock replenishes over time
- Build passes for area module
- Bot test verifies buying and selling items
```

### Quest Bot Tests

```
Module: bots/
Template: Follow bots/woodcutting.ts pattern
Quest: Cook's Assistant
Steps: Talk to Cook → get ingredients → return → complete
Done when:
- Bot completes full quest flow
- Quest state transitions verified
- Rewards granted (1 QP, 300 Cooking XP)
- Test passes 3 times consecutively
- Build passes for bots module
```

### System Tasks

```
Module: rsmod/content/mechanics/<system>/
Template: Follow similar system implementations
System: F2P Death and Respawn
Done when:
- Death triggers proper interface
- 3-item protection works
- Respawn at Lumbridge
- Item loss calculation correct
- Build passes for mechanics module
- Bot test verifies death scenario
```

---

## Contract Checklist

Every task must specify:

- [ ] **Module** - Exact file path
- [ ] **Template** - Reference implementation to follow
- [ ] **Specifics** - NPCs, items, locations, etc.
- [ ] **Done when** - Acceptance criteria (5-8 bullet points)
- [ ] **Build requirement** - Must pass scoped build
- [ ] **Test requirement** - Bot test or unit test required

---

## Definition of Done Reference

Per AGENTS.md Section "Definition of Done":

1. **No stub logic** - Real implementations, not placeholders
2. **Core gameplay loop works** - Start to finish functional
3. **State persisted** - Quest/skill state saves correctly
4. **Real rewards** - Actual XP/items via APIs
5. **Build passes** - Scoped build green
6. **Test artifact** - Bot test exists
7. **Docs updated** - CONTENT_AUDIT.md reflects status
8. **No blockers** - Dependencies resolved

---

## Priority Contract Requirements

### Phase 1 Tasks (Critical)
Must include:
- Exact file paths
- Reference templates
- Specific test scenarios
- Build commands

### Phase 2+ Tasks (Standard)
Must include:
- Module path
- Template reference
- Done when criteria
- Test requirements

---

**Apply this contract template to all 255 F2P tasks.**
