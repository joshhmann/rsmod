# Task Documentation Index

**Generated:** 2026-02-22
**Purpose:** Comprehensive documentation for pending agent tasks

---

## Quick Reference — Pending Tasks by Priority

### Wave 2 (Core Gameplay Mechanics)
| Task ID | Description | Documentation |
|---------|-------------|---------------|
| FOOD-1 | Food eating consumables | `docs/CORE_SYSTEMS_GUIDE.md` §1 |
| FOOD-2 | Potion drinking (stat boosts) | `docs/CORE_SYSTEMS_GUIDE.md` §2 |
| MECH-1 | NPC Aggression Radius System | `docs/agent-notes/npc-aggression-guide.md` |
| MECH-2 | Freeze and Stun Mechanics | `docs/agent-notes/freeze-stun-guide.md` |
| PRAYER-1 | Prayer active effects | `docs/CORE_SYSTEMS_GUIDE.md` §3 |
| MAKEQ-1 | Make-X quantity dialog | `docs/CORE_SYSTEMS_GUIDE.md` §4 |
| SHOP-1 | F2P shop content | `docs/CORE_SYSTEMS_GUIDE.md` §5 |
| WORLD-1 | Lumbridge Population | `docs/agent-notes/lumbridge-population-guide.md` |
| QUEST-4-IMPL | Romeo & Juliet Quest | `wiki-data/quests/quest-data.json` |

### Wave 3 (Quest Completion + World Coverage)
| Task ID | Description | Documentation |
|---------|-------------|---------------|
| QUEST-9-IMPL | Count Draynor Boss (Vampyre Slayer) | Blocked by AGENTBRIDGE-6 |
| QUEST-10-IMPL | Elvarg Boss (Dragon Slayer I) | Blocked by AGENTBRIDGE-6 |
| AREA-2 to AREA-7 | City Populations | Area-specific guides needed |

### Wave 4 (Progression + UI Systems)
| Task ID | Description | Documentation |
|---------|-------------|---------------|
| SKILL-22 | Slayer Skill Baseline | `wiki-data/mechanics/slayer-data.json` |
| SYSTEM-UI-1 | Friends/Ignore Interfaces | Interface guide needed |
| SYSTEM-UI-2 | Grand Exchange Interface | Market API reference |
| SYSTEM-UI-3 | Music Player Content | Audio API reference |

---

## Documentation Files

### Core System Guides
- `docs/CORE_SYSTEMS_GUIDE.md` — Food, Potions, Prayer, Make-X, Shops (615 lines)
- `docs/TRANSLATION_CHEATSHEET.md` — Alter v1 → RSMod v2 API mapping (660 lines)
- `docs/LEGACY_IMPLEMENTATION_PLAYBOOK.md` — Safe porting from legacy codebases

### Implementation Patterns
- `docs/agent-notes/npc-aggression-guide.md` — MECH-1 implementation guide
- `docs/agent-notes/freeze-stun-guide.md` — MECH-2 implementation guide
- `docs/agent-notes/combat-events-guide.md` — AGENTBRIDGE-6 implementation guide
- `docs/agent-notes/lumbridge-population-guide.md` — WORLD-1 implementation guide

### Wiki Data References
- `wiki-data/skills/woodcutting-complete.json` — Reference skill implementation data
- `wiki-data/monsters/goblin.json` — Reference monster format
- `wiki-data/skills/smithing.json` — Smithing recipe data
- `wiki-data/quests/quest-data.json` — Quest data reference

---

## API Quick Reference

### Event Handler Registration (RSMod v2)
```kotlin
// NPC interaction
onOpNpc1(npcs.banker) { event -> /* event.npc */ }

// Loc (object) interaction
onOpLoc1(locs.tree) { event -> /* event.loc, event.type */ }

// Inventory item click
onOpObj1(objs.logs) { /* player inventory context */ }

// Item-on-item (use X on Y)
onOpHeldU(objs.knife, objs.logs) { event -> /* event.first, event.second */ }

// Item-on-loc
onOpLocU(locs.anvil, objs.bronze_bar) { event -> /* ... */ }

// Interface button
onButton(interfaces.prayer_tab, component = 19) { /* ... */ }
```

### ProtectedAccess Methods (Common)
```kotlin
// Inventory
invAdd(invs.inv, obj, count)      // Add to inventory
invDel(invs.inv, obj, count)      // Remove from inventory
invReplace(inv, oldObj, count, newObj)  // Replace item
inv.contains(obj)                 // Check presence

// Stats
statAdvance(stats.woodcutting, xp)  // Grant XP
statHeal(stats.hitpoints, amount, 0) // Heal HP (caps at base)
statBoost(stats.attack, constant, percent)  // Temp boost

// Movement/Animation
anim(seqs.woodcutting_axe)        // Play animation
delay(ticks)                       // Suspend for N ticks
mes("Message text")               // Send game message

// Dialogue
startDialogue(npc) {
    chatNpc(happy, "Hello!")
    chatPlayer(calm, "Hi there.")
    val choice = choice2("Option A", "Option B")
}
```

### NpcEditor Combat Stats
```kotlin
edit(npcs.cow) {
    hitpoints = 8
    attack = 1
    strength = 1
    defence = 1
    ranged = 1          // 1 if melee-only
    magic = 1           // 1 if melee-only
    attackRange = 1     // 1 = melee
    respawnRate = 50    // ticks
    giveChase = true    // pursue player
    huntRange = 5       // aggression radius (MECH-1)
}
```

---

## Symbol Naming Conventions

**CRITICAL:** RSMod `.data/symbols/obj.sym` uses OLD internal cache names, NOT modern wiki names.

| Modern Wiki Name | Sym File Name | ID |
|-----------------|---------------|-----|
| small_fishing_net | net | 303 |
| vial_of_water | vial_water | 227 |
| vial | vial_empty | 229 |
| grimy_guam_leaf | unidentified_guam | 199 |
| bowstring | bow_string | 1777 |
| earth_rune | earthrune | 557 |

**Rule:** Always check `rsmod/.data/symbols/obj.sym` before using `find("name")`.
If name not found, add alias to `rsmod/.data/symbols/.local/obj.sym`.

---

## Testing Infrastructure

### Bot Script Template
```typescript
// bots/<skill>.ts
const result = await actions.findAndInteractLoc("Tree name");
const xp = await bot.waitForXpGain("woodcutting", expectedXp, timeout);
console.log(`Gained ${xp} XP`);
```

### Build Commands
```bash
# Single module build
cd rsmod && gradlew.bat :content:skills:smithing:build --console=plain

# With spotless
cd rsmod && gradlew.bat :content:skills:smithing:spotlessApply :content:skills:smithing:build --console=plain
```

### MCP Tools (when server running)
- `server_status` — Check server + AgentBridge status
- `get_state` — Player state snapshot
- `execute_script` — Run ad-hoc bot code
- `run_bot_file` — Run test from `bots/` directory
- `build_server` — Build specific module

---

## Agent Coordination

### Task Registry Commands
```javascript
list_tasks({ status: "pending" })     // See available work
claim_task({ taskId: "FOOD-1", agent: "claude" })
complete_task({ taskId: "FOOD-1", agent: "claude", notes: "Implemented food eating" })
lock_file({ path: "rsmod/content/...", agent: "claude", taskId: "FOOD-1" })
agent_heartbeat({ agent: "claude", status: "working", current_task: "FOOD-1", message: "Writing FoodScript.kt" })
```

### File Locking Protocol
- Call `lock_file()` before editing any shared file
- Check `list_file_locks()` before starting work
- `complete_task()` automatically releases all locks for that task

---

## Definition of Done

A task is complete only when:
1. ✅ No stub logic (no placeholder flows, no TODO-only handlers)
2. ✅ Core gameplay loop works (start → progress → completion)
3. ✅ State persisted correctly (quest stages, item checks)
4. ✅ Rewards are real (XP/items actually awarded)
5. ✅ Build passes for the module
6. ✅ Test artifact exists (bot script or integration test)
7. ✅ Docs updated (CONTENT_AUDIT.md, AGENTS.md ownership)
8. ✅ No unresolved blockers

**Status markers:**
- ✅ Complete — All DoD criteria met
- 🟡 Partial — Implementation exists but blocked or incomplete
- ❌ Not started — No module or placeholder only

