# Minigame Spec Workflow

## Purpose

Spec-first workflow for minigames, bosses, and complex content. The wiki generates specs (NOT production code) — specs drive manual implementation with confidence.

## Why Spec-First

Drop automation succeeded because drops are **data-driven**: wiki has tables with rarities, quantities, and item names. Minigames are **behavior-driven**: they need NPC AI, object state machines, round logic, scoring, and reward rules. The wiki can document these, but a code generator can't produce working behavior.

A spec captures everything a developer needs to implement accurately without guessing or alt-tabbing to the wiki.

## Spec Template

```markdown
# Minigame Spec: [Name]

## Overview
- Location:
- Requirements:
- Recommended stats:
- Duration:
- Reward summary:

## Objects
| Name | Loc Sym | Purpose | States |
|------|---------|---------|--------|
| e.g. "Brazier" | `wintertodt_brazier` | Broken/repairing/lit | idle → repairing → lit → broken |

## NPCs
| Name | Npc Sym | Purpose | Behavior |
|------|---------|---------|----------|
| e.g. "Wintertodt" | `wintertodt` | Boss | Damageable, phase changes |

## Items
| Name | Obj Sym | Purpose | 
|------|---------|---------|
| e.g. "Bruma root" | `bruma_root` | Fuel for braziers |

## Interfaces
| ID | Widget | Purpose |
|----|--------|---------|
| e.g. 396 | Timer + score overlay | Show points, damage, phase |

## Phases
| Phase | Trigger | Behavior | Duration |
|-------|---------|----------|----------|
| e.g. WAITING | First player enters | Players can prepare | Until start |
| e.g. ACTIVE | Players light braziers | Boss damages braziers | Until all braziers lit or boss defeated |

## Player Actions
| Action | Object/NPC | Check | Result |
|--------|-----------|-------|--------|
| Light brazier | Broken brazier | Tinderbox in inv | Brazier becomes lit after delay |
| Feed brazier | Lit brazier + bruma root | None | Adds fuel, grants points |

## Reward Table
| Item | Quantity | Roll/Pool | Source |
|-----|----------|-----------|--------|
| e.g. Bruma torch | 1 | Always | Supply crate |

## Edge Cases
- What happens if all players die?
- What happens on disconnect?
- What happens when timer runs out?
- What happens when a brazier is destroyed?

## Implementation Checklist
- [ ] Object loc references added to configs
- [ ] NPC references added to configs
- [ ] Item references added to configs
- [ ] Activity controller created or extended
- [ ] Phase machine wired
- [ ] Actions registered
- [ ] Reward table built
- [ ] Edge cases handled
- [ ] Localized messaging defined
- [ ] Integration tested

## Symbol Map
All required `.sym` names from cache:
- `wintertodt_brazier` → loc.sym
- `bruma_root` → obj.sym
- `wintertodt` → npc.sym
```

## Motherlode Mine Spec (Summary)

| Aspect | Detail |
|--------|--------|
| **Location** | Dwarven Mine (south of Falador) |
| **Requirements** | Mining level 30 (boostable) |
| **Object setup** | `mlm_vein` (ore vein), `mlm_depleted_vein` (depleted state), `hopper` (ore dump), `sack` (ore collected), `water_wheel` (processing), `struts` (wheel repair) |
| **Phases** | MINING → DUMPING → PROCESSING → REPEAT. No round-based phases — continuous loop |
| **Key action** | Mine vein → get pay-dirt → walk to hopper → dump → sack fills → wait for wheel to process → collect from sack |
| **Upper level** | Requires 30 Mining (boostable). Shorter run to hopper. |
| **Reward** | Golden nuggets (1/82.5 from pay-dirt). Prospector outfit, coal bag, gem bag |
| **Controller fit** | Does NOT need round phases. Needs: resource respawn (veins), inventory check (sack tracking), point tracking (nuggets) |
| **Symbols needed** | Cache audit needed for `mlm_*` loc/obj symbols |

## Wintertodt Spec (Summary)

| Aspect | Detail |
|--------|--------|
| **Location** | Wintertodt Camp (north of Great Kourend) |
| **Requirements** | Firemaking 50, optional others |
| **Object setup** | Brazier (multiple states: broken, repairing, lit), brazier (unlit variant), brazier (lit variant), bruma root (item), pyromancer outfit |
| **Phases** | WAITING → ACTIVE → REWARD → RESET |
| **Active phase** | Brazier break → player repairs + feeds bruma root → Wintertodt attacks with cold. If brazier dies too long, Wintertodt heals. Damage Wintertodt by keeping braziers lit. |
| **Score** | Points based on damage dealt, braziers kept lit |
| **Reward** | Supply crate (scaled by points). Rewards include tome of fire, pyromancer outfit, dragon axe, burnt pages |
| **Controller fit** | NEEDS full activity controller: phases, participant tracking, damage contribution, object state management, reward payout |
| **Symbols needed** | Cache audit needed for Wintertodt-specific loc/obj symbols |

## Spec-First Workflow Steps

1. **Wiki scrape** — Extract all object names, NPC names, item names, interface IDs, phase descriptions
2. **Cache symbol audit** — Resolve each name against `.sym` files (npc, obj, loc, seq)
3. **Build gap table** — What's in cache vs what's in wiki — missing symbols = blocked elements
4. **Generate spec** — Fill template from wiki data + cache audit
5. **Manual review** — Verify phase logic, edge cases, reward accuracy
6. **File as reference** — Spec lives in `docs/minigames/spec-<name>.md`
7. **Implement from spec** — Developer works exclusively from spec, not wiki

## Automation Confidence

| Stage | What Can Be Automated |
|-------|----------------------|
| Object/NPC/Item extraction | ✅ High — wiki tables parse cleanly |
| Symbol resolution | ✅ Medium — depends on rev 233 cache coverage |
| Interface ID extraction | 🟡 Low — wiki often missing widget IDs |
| Phase logic | ❌ Manual — must be hand-coded |
| Edge cases | ❌ Manual — requires game knowledge |
| Reward tables | ✅ High — same pattern as drop automation |
| Symbol maps | ✅ High — structured compilation of resolved names |
