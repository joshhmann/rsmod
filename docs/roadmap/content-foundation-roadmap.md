# Content Foundation Roadmap

## Overview

RSMod has a strong chassis (game engine, combat, drops, basic interactions). What it needs is a content operating layer — reusable foundations that make building skilling, minigames, bosses, and complex systems faster and safer.

**Current state (June 2026):** Drops automation is Level 5 certified. Skills exist as independent implementations with no shared framework. Minigames, bosses, and Construction don't exist yet. Everything that needs to be built will be built 3× faster with proper foundations.

## Foundation Layers

### Layer 1: Interaction Foundation ✅ (Mostly exists)

RSMod supports:
- `onOpLoc1`, `onOpLocU` — click object, use item on object
- `onOpNpc1`, `onOpNpc2` — click NPC, use item on NPC
- `onButton` — interface button clicks
- `onNpcHit`, `onPlayerQueue` — damage and timer events

**Gaps:**
- No generic "click object → check requirement → start action" pattern
- No shared requirement check (level, quest, item) helper
- Status effect system exists but not universally used

**Build priority:** Add a `Requirement` sealed class and `checkRequirements()` helper. This alone eliminates boilerplate in every skill script.

### Layer 2: Skill Action Foundation 🟡 (Partially exists)

RSMod skills are independently implemented. A shared `SkillAction` data class would reduce new skill entries to data + a few custom rules.

```kotlin
data class SkillAction(
    val name: String,
    val level: Int,
    val xp: Double,
    val input: ObjType?,       // What's consumed
    val output: ObjType?,      // What's produced
    val tool: ObjType?,        // Required tool (axe, pickaxe, etc.)
    val loc: LocType?,         // Required object (tree, rock, range)
    val anim: SeqType?,        // Animation
    val cycleTime: Int = 4,    // Game ticks per action
    val successChance: Double = 1.0,
    val depletionChance: Double = 0.0,  // For resource nodes
    val respawnTime: Int = 0   // For resource nodes in ticks
)
```

**Current skill implementations have this data inline** (CookingFood.kt has level, raw, cooked, burnt, XP inline). Building a `SkillAction` system means extracting this into a data-driven format, not replacing existing code.

**Build priority:** After proving the pattern with Cooking/Woodcutting 1–30 tests, extract shared config.

### Layer 3: Resource Node Foundation ❌ (Doesn't exist)

No shared system for trees, rocks, fishing spots, or herb patches. Each skill implements its own depletion/respawn:

- **Woodcutting:** `LocRepository.del()` + `onAiConTimer` for respawn (works, per-tree hardcoded)
- **Mining:** Similar pattern with explicit rock→depleted→rock transitions
- **Fishing:** NPC-based spots don't use resource node pattern at all

A shared `ResourceNode` framework would let new resources be defined as data:

```kotlin
data class ResourceNode(
    val loc: LocType,           // The clickable object
    val depletedLoc: LocType,   // What it becomes when depleted
    val product: ObjType,       // What's produced
    val tool: ObjType,          // Required tool
    val level: Int,
    val xp: Double,
    val depletionChance: Double,  // Per-action depletion chance
    val respawnTicks: Int,        // Ticks until respawn
    val anim: SeqType,
    val respawnAnim: SeqType? = null
)
```

**Build priority:** After the 1–30 Woodcutting/Mining POC validates the pattern, build the config.

### Layer 4: Reward Framework ✅ (Mostly exists via drops)

Drop automation is Level 5. The reward framework needs to extend beyond NPC death:

**Existing:**
- `NpcDropTableRegistry` + `dropTable {}` DSL for NPC drops
- `table()`, `item()`, `always()`, `nothing()` patterns
- Weighted random selection

**Extend to support:**
- Skilling rewards (bird nests, gem rocks, implings)
- Reward crates/chests (Wintertodt supply crate, Motherlode sack)
- Minigame shops (point-based purchases)
- Random rolls (clue scroll rewards, random events)
- Level-scaled rewards (fishing catches scale with level)
- Boss unique tables (rare drop table extensions)

**Build priority:** After the first minigame/skilling POC proves the need, extend the DSL.

### Layer 5: Area/Activity Controller ❌ (Doesn't exist)

See `docs/automation/activity-controller-design.md` for full design.

**Build priority:** M5 milestone. Needed for Motherlode Mine, Wintertodt, all boss arenas.

### Layer 6: Spec-First Workflow ✅ (Defined)

See `docs/automation/minigame-spec-workflow.md` for full workflow.

## Milestone Plan

### M1: Test Existing Skills (1–30) — Current Sprint
Prove the interaction and resource node foundations work before abstracting them.

**Actions:**
- [ ] Login asslord in Lumbridge
- [ ] Test Cooking 1–30 (shrimp, beef, chicken, trout, salmon)
- [ ] Test Woodcutting 1–30 (normal tree, oak, willow)
- [ ] Test Mining 1–30 (copper, tin, iron, coal)
- [ ] Test Fishing 1–30 (shrimp, sardine, trout, salmon)
- [ ] Document gaps and missing configurations
- [ ] Continue drops automation as background (Karamja, Wilderness policy spike)

### M2: Skill Action Data Abstraction
Extract shared patterns from working skills into reusable framework.

**Actions:**
- [ ] Build `SkillAction` data class
- [ ] Build `ResourceNode` data class  
- [ ] Convert cooking food entries to data-driven (just configs, no code)
- [ ] Convert tree entries to resource node configs
- [ ] Convert rock entries to resource node configs
- [ ] Add shared requirement-check helper
- [ ] Add shared tool-check helper

### M3: Reward Framework Extension
Extend drop system beyond NPC death.

**Actions:**
- [ ] Add `rewardTable {}` DSL for non-NPC rewards
- [ ] Add weighted crate/chest rewards
- [ ] Add skill-sourced rewards (bird nests, geodes)
- [ ] Add point-based reward shop pattern

### M4: Shop Stock Automation
Use corpus data to generate shop stock configs.

**Actions:**
- [ ] Audit wiki `{{StoreLine}}` templates for stock data
- [ ] Build shop stock scraper (or accept manual fills)
- [ ] Build G4 shop generator POC
- [ ] Generate 1–3 shops (General Store, Rimmington)
- [ ] Compile and test

### M5: Activity Controller
Build the generic activity controller for minigames.

**Actions:**
- [ ] Build base `Activity` class
- [ ] Add participant tracking + phase management
- [ ] Add tick loop integration
- [ ] Add object state reset
- [ ] Add reward payout integration
- [ ] Integration test with a simple activity shell

### M6: Motherlode Mine MVP
First minigame using spec-first workflow + activity controller.

**Actions:**
- [ ] Audit MLM cache symbols (veins, hopper, sack, wheel, struts)
- [ ] Build MLM spec from wiki data
- [ ] Implement vein mining (resource node + activity)
- [ ] Implement hopper/sack/water wheel processing
- [ ] Implement golden nugget rewards
- [ ] Implement Prospector shop
- [ ] Test: mine → dump → collect → buy outfit

### M7: Wintertodt MVP
Second minigame — proves round-based activity controller.

**Actions:**
- [ ] Audit Wintertodt cache symbols
- [ ] Build Wintertodt spec
- [ ] Implement brazier state machine
- [ ] Implement bruma root collection (resource node)
- [ ] Implement boss damage + healing mechanics
- [ ] Implement supply crate rewards
- [ ] Implement scoring + scaling
- [ ] Test: solo run → collect crate

### M8+: Expansion
- **Karamja** — continue drops automation (pirates, demons, monkeys)
- **Wilderness policy** — define zone-filtered drop system
- **Bosses** — KBD, Elvarg, KQ using spec-first + activity controller
- **Quests** — Prince Ali Rescue, Dragon Slayer, etc.
- **Construction** — POH engine (major system, defer)
- **Sailing** — No rev 233 assets; speculative only

## Drop Automation Background

Drops automation continues in parallel at Level 5. Pending regions:

| Region | Targets | Effort | Complexity |
|--------|---------|:------:|:----------:|
| Karamja | Pirates, lesser demons, jungle spiders, monkeys, snakes | Medium | Multi-region NPCs |
| Wilderness | Many NPCs | High | Zone-filtered drops needed |
| Ice Mountain surface | Ice warriors, ice giants | Done — moved to Ice Caves batch |
| Goblin Village | Goblins | Already handled |
| Crafting Guild | Dwarves, scorpions | Already handled |

## Skills vs. Drops: Complementary Work

| Work | Drop Automation | Skill Foundation |
|------|----------------|-----------------|
| Data source | Corpus drops_by_source.json | Wiki skill_guides.json |
| Output | Kotlin drop tables | Config data + behavior code |
| Automation | High (Level 5) | Low-Medium |
| Risk | Low (data = safe) | Medium (behavior = bugs) |
| Time per item | ~2 min per NPC family | ~1–2 hours per skill config |

**Strategy:** Do both in parallel. Drops run in the background (10–15 min per region). Skills get focused sprints (2–4 hours per foundation layer).
