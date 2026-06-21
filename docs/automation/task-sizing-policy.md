# Task Sizing Policy

> **Part of:** RSMod Orchestration System — v3.4.0
> **Purpose:** Define task size classes, delegation rules, auto-decomposition triggers, and iteration budget guardrails to prevent workers from burning resources on broad, already-existing, or incorrectly scoped tasks.
> **Status:** Definitive Reference

---

## 1. Sizing Classes

Every task dispatched through the kanban board must be assigned a size class. Size determines delegation permission, budget allocation, and approval gates.

### XS — Trivial

| Dimension | Limit |
|:----------|:------|
| **Scope** | Docs, worklog, status update, one scenario/spec file |
| **Files** | ≤2 |
| **Architecture** | None — no new patterns |
| **Decision required** | None — pure description or record-keeping |
| **Examples** | Update worklog, write 1-30 validation scenario, edit zone-readiness-checklist, create morning report |

**Delegation:** ✅ Can dispatch directly
**Max iterations:** 5
**Risk level:** 1

### S — Small

| Dimension | Limit |
|:----------|:------|
| **Scope** | One module, one known pattern |
| **Files** | ≤5 |
| **Architecture** | Follows existing pattern exactly |
| **Decision required** | Technical — minor API choices only |
| **Examples** | Single NPC drop table batch (≤10 families), port one zone's NPC configs, add missing dialogue handler to existing module |

**Delegation:** ✅ Can dispatch directly
**Max iterations:** 15
**Risk level:** 2

### M — Medium

| Dimension | Limit |
|:----------|:------|
| **Scope** | One content family or one zone slice |
| **Files** | ≤15 |
| **Architecture** | Known workflow, bounded validation |
| **Decision required** | Technical — may need spec reference |
| **Examples** | All drops for one region, one skill module (follows existing pattern), shop configs for 5+ shops, zone readiness audit |

**Delegation:** ✅ Can dispatch directly with pre-flight
**Max iterations:** 30
**Risk level:** 3

### L — Large

| Dimension | Limit |
|:----------|:------|
| **Scope** | Multiple modules, new behavior, cross-cutting systems |
| **Files** | ≤50 |
| **Architecture** | New system or significant extension |
| **Decision required** | Architectural — needs spec-first or decomposition |
| **Examples** | Implement ranged combat skill, complete all F2P magic spells, door/ladder/gate interaction system |

**Delegation:** ⚠️ Must become spec-first or be decomposed into M cards
**Max iterations:** 50 (only after spec approved)
**Risk level:** 3-4

### XL — Extra Large

| Dimension | Limit |
|:----------|:------|
| **Scope** | Multiple modules, new engine-level behavior, uncertain APIs |
| **Files** | >50 |
| **Architecture** | Engine-level, crosses systems, breaks new ground |
| **Decision required** | Architectural — must be spec-first |
| **Examples** | Teleports, run energy, magic framework, boat travel, minigames, quests, bosses, economy-wide systems |

**Delegation:** 🛑 **Never dispatch as implementation directly**
**Max iterations:** N/A — spec-first only
**Risk level:** 4-5

---

## 2. Dispatch Rules

| Size | Dispatch To Worker? | Pre-Flight Required? | Spec-First? | Human Approval? |
|:----:|:-------------------:|:--------------------:|:-----------:|:---------------:|
| XS | ✅ Yes | No | No | No |
| S | ✅ Yes | Yes | No | No |
| M | ✅ Yes | Yes | No | No (review on failures) |
| L | ⚠️ Spec-first or decomposed | Yes | Yes (before code) | Yes (spec review) |
| XL | 🛑 Never directly | Yes | Yes (mandatory) | Yes (spec + design review) |

### Decomposition Rule

**When the orchestrator receives an L or XL goal, it must:**

1. Classify the goal as L or XL
2. Do NOT dispatch it as-is
3. Either:
   a. **Decompose** into multiple S/M cards with dependency links, OR
   b. **Create a spec-first card** (triage → specifier fills spec → ready → implementation)
4. Complete its own card with the decomposition/spec summary
5. Never let an L or XL card reach a runner worker

---

## 3. Auto-Decomposition Triggers

These phrases in a card title or body **must** trigger auto-decomposition or spec-first routing. They are inherently XL or L scope.

### Trigger Table

| Phrase | Sizing | Action |
|:-------|:------:|:-------|
| `boat travel` | XL | Decompose: transport → spawns → shops → drops |
| `teleports` | XL | Decompose: spell API → individual spells → animation/fx |
| `run energy` | XL | Decompose: energy system → regeneration → consumables |
| `multi-combat` | L | Spec-first: verify engine support → zone area configs |
| `magic utility` | XL | Decompose: enchant/alchemy → teleports → combat spells → rune consumption |
| `implement all` | L/XL | Block — never dispatch. Decompose into specific items |
| `all F2P` | XL | Block — never dispatch. Split into per-region or per-system cards |
| `full system` | XL | Block — always spec-first |
| `every zone` | XL | Block — never dispatch. Create per-zone cards |
| `minigame` | XL | Spec-first: requirements → rewards → mechanics → NPCs |
| `quest` | XL | Spec-first: requirements → dialogue → rewards → varbits → items |
| `boss` | XL | Spec-first: mechanics → phases → drops → lair |
| `cross-cutting` | L/XL | Split by subsystem, dispatch sequentially |
| `economy-wide` | XL | Spec-first: design doc → impact analysis → phased implementation |

### Trigger Behavior

When any trigger phrase is detected:
1. **Stop the card** — do not dispatch as-is
2. **Create decomposition notice** on the card comment
3. **Orchestrator decomposes** into child cards:
   - Each child is S or M sized
   - Each child has explicit scope boundaries
   - Dependencies are expressed via `parents: []`
4. **Original card stays blocked** — awaiting all children to complete

---

## 4. Iteration Budget Guards

Every dispatched worker operates under an iteration budget. If the guard fires, the card is blocked for human review.

### Budget Limits per Size

| Size | Pre-Flight Deadline | First Change Deadline | Validation Deadline | Hard Cap |
|:----:|:-------------------:|:---------------------:|:------------------:|:--------:|
| XS | — | 5 | 5 | 5 |
| S | 10 | 15 | 15 | 20 |
| M | 10 | 25 | 30 | 35 |
| L (spec) | — | 15 (spec only) | 20 | 25 |
| XL (spec) | — | 30 (spec only) | 40 | 50 |

### Guard Rules

| Condition | Action |
|:----------|:-------|
| 10 iterations without **pre-flight report** | Stop/block the card |
| 25 iterations without **file changes or clear decision** | Block for human review |
| 50 iterations without **validation command result** | Block for human review |
| Hard cap exceeded | Block — orchestration failure review |
| Worker stalled on full build compile >2 iterations | Force-kill, redirect to module compile |

### Pre-Flight Report Deadline

The pre-flight report must be posted as a card comment within the first 10 iterations. If not:

1. The orchestrator issues a warning
2. On the 11th iteration without pre-flight report, the card is blocked
3. Human reviews whether the worker needs a course-correction or replacement

---

## 5. Already-Exists Closure

If pre-flight finds the system already exists on CT 123:

### Protocol

1. **Do NOT write code** — the implementation already exists
2. **Cite files found** in a card comment with exact paths
3. **Close the card** as already-implemented
4. If there are specific gaps (missing interaction, missing area config), **create a smaller validation/patch card**

### Example Scenarios

| Card Title | Pre-Flight Result | Action |
|:-----------|:-----------------|:-------|
| "Implement bank booth handler" | `BankBooth.kt` exists at `content/objects/bank/` | Close card, cite `BankBooth.kt`, `Banker.kt`, `BankOpenScript.kt` |
| "Implement F2P Cooking" | `content/skills/cooking/` has `Cooking.kt` + fire/cook/items | Close card, optionally spawn validation card |
| "Add Cook dialogue" | No `content/npcs/dialogue/` dir at all | Proceed — genuine gap |
| "Wilderness ditch crossing" | `grep -rl ditch` finds handler in f2p-wilderness | Close card, cite paths |

### Expected Worker Behavior

```
pre-flight: find content/objects/bank/BankBooth.kt
  → EXISTS: card says "implement" but file exists
  → Decision: CLOSE_ALREADY_EXISTS
  → Citation: BankBooth.kt, Banker.kt, BankOpenScript.kt
  → Close card
  → Budget saved: ~90 iterations
```

---

## 6. Reclassification Reference

### Failed Card Patterns

| Failure Pattern | Cause | Fix |
|:----------------|:------|:----|
| "Implement X" but X already exists | Pre-flight skipped | Close card, cite evidence |
| Full build compile timeout | Worker used `:server:app:compileKotlin` | Force module compile |
| Generic "all F2P" scope | Card was XL but dispatched as S | Decompose into per-region cards |
| Dialogue handlers stuck | Worker didn't find existing patterns | Load pattern + module compile |
| Cross-cutting system (ranged, magic) | Card was L/XL but no spec | Route to spec-first |
| Quest card dispatched directly | Not decomposed | Always spec-first for quests |

### Reclassification Flow

```
All cards → Size class check → Size OK? → Dispatch
                                    ↓ No
                            Auto-decomposition triggers?
                                    ↓ Yes
                            Decompose into S/M cards
                                    ↓
                            Create spec-first card if needed
                                    ↓
                            Block original card as decomposing
                                    ↓
                            Dispatch child cards
```

---

## 7. Delegation Decision Tree

```ascii
                         ┌──────────────────────────┐
                         │    New task arrives       │
                         └───────────┬──────────────┘
                                     │
                         ┌───────────▼──────────────┐
                         │  Classify by size:        │
                         │  XS / S / M / L / XL     │
                         └───────────┬──────────────┘
                                     │
                  ┌──────────────────┼──────────────────┐
                  │                  │                  │
            ┌─────▼─────┐    ┌──────▼──────┐    ┌─────▼──────┐
            │ XS/S/M    │    │     L       │    │    XL      │
            └─────┬─────┘    └──────┬──────┘    └─────┬──────┘
                  │                 │                  │
                  ▼                 ▼                  ▼
         ┌────────────────┐ ┌────────────────┐ ┌─────────────────┐
         │ Dispatch to    │ │ Auto-trigger   │ │ Decompose       │
         │ worker with    │ │ phrases?       │ │ into S/M cards  │
         │ pre-flight     │ │   ↓ Yes        │ │ + spec-first    │
         └────────────────┘ │ Decompose to   │ └─────────────────┘
                            │ S/M cards      │
                            └────────────────┘
```

---

## Revision History

| Version | Date | Author | Changes |
|:--------|:-----|:-------|:--------|
| 1.0 | 2026-06-21 | Mai | Initial policy — XS/S/M/L/XL classes, auto-decomposition triggers, iteration budget guards, already-exists closure protocol |
