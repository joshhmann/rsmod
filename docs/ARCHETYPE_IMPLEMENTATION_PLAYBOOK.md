# Archetype Implementation Playbook

This guide captures five high-frequency RSMod content archetypes and gives a consistent implementation checklist for each:

1. gathering loop,
2. processing loop,
3. dialogue/state quest progression,
4. UI interaction flow,
5. NPC combat/drop wiring.

Use this as a quick design+review sheet before opening a task and again before marking a task complete.

---

## 1) Gathering Loop Archetype

Examples: woodcutting trees, mining rocks, fishing spots.

### Required handlers

- Primary world interaction handler:
  - `onOpLoc1(...)` for "Chop down" / "Mine" / "Net" style interactions.
- Optional alternate op handlers used in existing content:
  - `onOpLoc3(...)` for alternate menu op text.
  - `onOpLocU(...)` when item-on-loc flows are supported.
- Tick/loop control:
  - action coroutine using `delay(...)`, movement/interaction checks, and retry logic.

### State model

- **Static definition state**
  - node/tool definitions (required level, XP, depletion chance, respawn behavior).
- **Player runtime state**
  - skill level gates,
  - inventory capacity,
  - animation/sequence state,
  - current action loop lifecycle (active/cancelled).
- **World/runtime node state**
  - node availability/depletion and respawn scheduling.

### Failure paths

- Level requirement fails.
- Tool missing or invalid tool tier.
- Inventory full.
- Node depleted before reward roll completes.
- Pathing or distance mismatch interrupts loop.
- Combat/forced movement interrupts the action cycle.

### Validation commands

- `./gradlew :content:skills:woodcutting:build --console=plain`
- `./gradlew :content:skills:mining:build --console=plain`
- `./gradlew :content:skills:fishing:build --console=plain`

### Reference module path(s)

- `content/skills/woodcutting/src/main/kotlin/org/rsmod/content/skills/woodcutting/`
- `content/skills/mining/src/main/kotlin/org/rsmod/content/skills/mining/`
- `content/skills/fishing/src/main/kotlin/org/rsmod/content/skills/fishing/`

---

## 2) Processing Loop Archetype

Examples: smelting ores, smithing products, cooking raw food, fletching conversion loops.

### Required handlers

- Workstation/object interaction:
  - `onOpLoc1(...)` to open a processing menu/workflow.
- Item-on-workstation shortcut flows:
  - `onOpLocU(...)` for direct single-recipe routing.
- Optional menu continuation handlers:
  - Make-X/quantity selection handlers (interface button/selection callbacks) where enabled.

### State model

- **Recipe definition state**
  - inputs, outputs, level requirement, XP, optional secondary inputs.
- **Player runtime state**
  - selected recipe,
  - quantity target (1/5/10/X/All),
  - inventory material availability,
  - active processing loop and cancellation flags.
- **Workstation context**
  - valid object type and interaction proximity.

### Failure paths

- Missing primary or secondary materials.
- Level gate fails.
- Quantity request exceeds available resources.
- Menu opened but selection becomes invalid (inventory changed).
- Loop interrupted by movement/combat/forced UI closure.

### Validation commands

- `./gradlew :content:skills:smithing:build --console=plain`
- `./gradlew :content:skills:cooking:build --console=plain`
- `./gradlew :content:skills:fletching:build --console=plain`

### Reference module path(s)

- `content/skills/smithing/src/main/kotlin/org/rsmod/content/skills/smithing/`
- `content/skills/cooking/src/main/kotlin/org/rsmod/content/skills/cooking/`
- `content/skills/fletching/src/main/kotlin/org/rsmod/content/skills/fletching/`

---

## 3) Dialogue/State Quest Progression Archetype

Examples: quest start, branch dialogue, item turn-in, stage completion.

### Required handlers

- NPC conversation entrypoint:
  - `onOpNpc1(...)` for Talk-to.
- Optional quest interaction handlers (depending on quest design):
  - `onOpObj*`, `onOpLoc*`, `onOpNpcU`, item hand-ins, trigger objects, etc.
- Quest UI/state updates:
  - journal/progress refresh hooks after stage transitions.

### State model

- **Global quest state (3-state model)**
  - not started / in progress / completed.
- **Stage state (n-stage model)**
  - discrete step integer or enum for branch progression.
- **Quest-scoped transient flags**
  - sub-choice outcomes, NPC-specific branch memory, item-delivery checkpoints.
- **External dependencies**
  - required items, prerequisite quest states, location constraints.

### Failure paths

- Start blocked by missing prerequisites.
- Dialogue branch mismatch due to stale/inconsistent stage.
- Item check fails at hand-in or partial turn-in edge cases.
- Reward handout fails (inventory full, script interrupted).
- Master varp/quest completion state not synced to final stage.

### Validation commands

- `./gradlew :content:quests:cooks-assistant:build --console=plain`
- `./gradlew :content:quests:dragon-slayer:build --console=plain`
- `./gradlew :content:quests:imp-catcher:build --console=plain`

### Reference module path(s)

- `content/quests/cooks-assistant/src/main/kotlin/org/rsmod/content/quests/cooksassistant/`
- `content/quests/dragon-slayer/src/main/kotlin/org/rsmod/content/quests/dragonslayer/`
- `content/quests/imp-catcher/src/main/kotlin/org/rsmod/content/quests/impcatcher/`
- `docs/QUESTING_AREA_GUIDELINES.md`

---

## 4) UI Interaction Flow Archetype

Examples: bank flows, settings toggles, tab panels, guide/tutorial interfaces.

### Required handlers

- Interface open/close orchestration:
  - `ifOpenSub(...)`, `ifClose(...)`, `ifCloseSub(...)`.
- Component event wiring:
  - `ifSetEvents(...)` for clickable/drag/pause components.
- Component content/state updates:
  - `ifSetText(...)`, `ifSetObj(...)`, and related output updates.
- Optional input/continuation handlers:
  - button, pause button, and modal-specific callbacks.

### State model

- **UI composition state**
  - currently open top-level interface + side/overlay subinterfaces.
- **Player view-model state**
  - selected tab/filter/sort mode,
  - temporary search/input state,
  - active modal/tutorial page index.
- **Data backing state**
  - inventory/bank snapshots,
  - stat values,
  - settings toggles.

### Failure paths

- Event mask not applied (component appears but is non-interactive).
- Wrong component ID or range causes dead buttons.
- Desync between server state and rendered values.
- Closing parent interface without closing children leaves stale UI state.
- Pause/modal flow exits without cleanup.

### Validation commands

- `./gradlew :content:interfaces:bank:build --console=plain`
- `./gradlew :content:interfaces:settings:build --console=plain`
- `./gradlew :content:interfaces:journal-tab:build --console=plain`

### Reference module path(s)

- `content/interfaces/bank/src/main/kotlin/org/rsmod/content/interfaces/bank/`
- `content/interfaces/settings/src/main/kotlin/org/rsmod/content/interfaces/settings/`
- `content/interfaces/journal-tab/src/main/kotlin/org/rsmod/content/interfaces/journal/tab/`
- `content/interfaces/gameframe/src/main/kotlin/org/rsmod/content/interfaces/gameframe/`

---

## 5) NPC Combat/Drop Wiring Archetype

Examples: adding a missing NPC combat profile and matching drop table.

### Required handlers

- Combat behavior registration:
  - NPC combat script/registration in `content/other/npc-combat`.
- Drop table registration:
  - `dropTable { ... }` definitions and NPC-to-table linkage in `content/other/npc-drops`.
- Optional category table registration:
  - shared/common tables (e.g., giant family, humanoid family).

### State model

- **Combat definition state**
  - combat level profile, attack style, animations, max hit, attack speed.
- **Encounter runtime state**
  - current target, aggression state, distance/pathing checks, death lifecycle.
- **Drop definition state**
  - guaranteed drops,
  - weighted variable table rolls,
  - quantity ranges,
  - "nothing" weights.

### Failure paths

- NPC has drop table but no combat wiring (or vice versa).
- Incorrect NPC ID binding (rev mismatch or wrong symbol).
- Guaranteed/weighted drop logic mis-specified (bad effective rate).
- No-drop behavior omitted causing inflated loot output.
- Death event path bypasses expected table due to wrong registration order.

### Validation commands

- `./gradlew :content:other:npc-combat:build --console=plain`
- `./gradlew :content:other:npc-drops:build --console=plain`
- `./gradlew :content:other:npc-drops:test --console=plain`

### Reference module path(s)

- `content/other/npc-combat/src/main/kotlin/org/rsmod/content/other/npc/combat/`
- `content/other/npc-drops/src/main/kotlin/org/rsmod/content/other/npcdrops/`
- `docs/MICRO_TASK_GUIDE.md`
- `docs/DTX_DROP_TABLE_GUIDE.md`

---

## Quick archetype handoff checklist

Before handoff, ensure each implemented archetype has:

- handlers registered for every intended entrypoint,
- state transitions that are explicit and monotonic where required,
- at least one intentionally tested failure path,
- module-scoped build/test command evidence,
- module path references documented in task notes.
