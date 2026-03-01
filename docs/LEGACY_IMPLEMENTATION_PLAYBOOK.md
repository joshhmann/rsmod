# Legacy Implementation Playbook (317 / 2004scape / Older OSRS Revs)

Purpose: use older implementations as **behavior references** to accelerate RSMod v2 development, without importing revision-unsafe IDs or engine assumptions.

Target runtime: **RSMod rev 233**.

---

## 1) What Legacy Sources Are Good For

Use legacy sources for:
- Quest and skill flow ordering.
- Edge-case handling (inventory full, wrong stage, missing tool, fail path messaging).
- Timing patterns (tick cadence, retries, action loops).
- UI flow expectations (when prompts open/close, option routing, completion moments).
- Data model ideas (state machines, patch states, NPC behavior structure).

Do **not** use legacy sources as direct truth for:
- Item/NPC/Loc IDs.
- Opcode-level packet assumptions.
- Engine internals and scheduler behavior.
- Exact interfaces/components without rev-233 verification.

---

## 2) Source Reliability Ladder

When sources conflict, prefer in this order:
1. Rev-233 symbol tables and RSMod APIs (`rsmod/.data/symbols/*`, RSMod codebase).
2. OSRS wiki behavior descriptions and mechanics.
3. High-quality legacy implementations (Kronos/Alter/317/2004scape).
4. Forum snippets or unverified community code.

Rule: legacy code is a **hint**, rev-233 symbols and current RSMod APIs are **authority**.

---

## 3) Translation Workflow (Required)

For every borrowed behavior:
1. Extract behavior spec from legacy source.
2. Rewrite as RSMod-native flow (handlers, state changes, rewards).
3. Map all refs through rev-233 symbols:
   - `rsmod/.data/symbols/obj.sym`
   - `rsmod/.data/symbols/npc.sym`
   - `rsmod/.data/symbols/loc.sym`
4. Prefer `BaseObjs`/`BaseNpcs`/base refs first.
5. If missing in base refs but present in `.sym`, define module-local refs (`*Objs`, `*Locs`, `*Npcs`).
6. Build scoped module.
7. Add or update a bot script (`bots/<feature>.ts`) for integration verification.
8. Update audit docs only after green build and real flow checks.

---

## 4) Anti-Patterns To Avoid

- Copy-pasting legacy constants and IDs into Kotlin.
- Marking tasks complete with dialogue-only scaffolds.
- Reproducing packet or interface behavior from a different revision without validation.
- Silently changing behavior to “make it compile” without documenting deltas.
- Ignoring failure paths (inventory, level gate, quest stage mismatch).

---

## 5) “Borrowed Logic” Review Checklist

Before merging:
1. Does this code implement full start → progress → completion flow?
2. Are fail paths implemented and player-visible?
3. Are all hard refs rev-233 verified?
4. Is reward logic real (XP/items/quest points), not placeholder?
5. Did scoped `spotlessApply` + `build` pass?
6. Is there a bot test artifact or updated test?
7. Are known behavior differences vs legacy documented?

If any answer is “no”, status stays in-progress/partial.

---

## 6) Practical Template For New Work

Use this per feature:

```text
Legacy Source:
- Project/commit:
- File(s):
- Feature section:

Behavior Intent (from legacy):
- ...

Rev-233 Mapping:
- Objects:
- NPCs:
- Locs:
- RSMod APIs used:

Differences from legacy:
- ...

Validation:
- Build command:
- Bot script:
- Result:
```

---

## 7) Example: Safe Porting Mindset

Bad:
- “317 quest used item ID X, copied directly to Kotlin.”

Good:
- “317 quest showed required item sequence. We reimplemented sequence, mapped refs from `obj.sym`, used RSMod inventory APIs, and verified with bot + scoped build.”

---

## 8) Scope Note

This playbook is intentionally strict because our roadmap is large and multi-agent. It optimizes for:
- faster implementation velocity,
- lower regression risk,
- reliable Definition-of-Done enforcement.

---

## 9) High-Value Source Map (Current Workspace)

Use these first before deep repo crawling:

- `Kronos-184-Fixed/Kronos-master/kronos-server/data/npcs/combat/`
  - Best for NPC combat baselines (stats, styles, speed patterns).
- `Kronos-184-Fixed/Kronos-master/kronos-server/data/npcs/drops/`
  - Best for drop-table structure and weighted loot references.
- `Alter/game-server/src/main/kotlin/...`
  - Best for modern Kotlin RSPS flow patterns and plugin architecture ideas.
- `docs/TRANSLATION_CHEATSHEET.md`
  - First stop for Alter v1 → RSMod v2 API mapping.
- `docs/RUNESERVER_NOTES.md`
  - Prior RuneServer-derived timing/formula findings.

Working rule:
- Query narrow paths first (single feature), then expand only if blocked.

