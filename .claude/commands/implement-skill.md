Implement the OSRS skill: $ARGUMENTS

Follow this exact workflow. Do not skip steps.

---

## Phase 1 — Research

1. Read `docs/LEGACY_PLAYBOOK.md` — source priority order, Kronos paths, Alter paths, DSL examples.
2. Read `docs/TRANSLATION_CHEATSHEET.md` for the v1→v2 API mapping.
3. Read `docs/CONTENT_AUDIT.md` to confirm the skill is not already implemented.
4. Read `wiki-data/skills/$ARGUMENTS.json` if it exists. If not, create it with wiki-accurate values:
   - `level_req`, `xp`, `animation`, `object_ids` (or `npc_ids`), produced item IDs
   - Add `test_locations`: an array of `{ "desc": "...", "x": N, "z": N }` for training spots
4. Invoke specialized skill: `/java-reference-expert` to set context for Java-to-Kotlin porting.
5. Find the reference implementation:
   - **Primary**: Search `Alter/game-plugins/src/main/kotlin/org/alter/plugins/content/skills/` for a matching plugin.
   - **Secondary (Java)**: Search `Kronos-184-Fixed/Kronos-master/kronos-server/src/main/java/io/ruin/model/skills/` for the Java skill.
   - Read the reference fully before writing anything.
6. Read the woodcutting template for v2 patterns:
   `rsmod/content/skills/woodcutting/src/main/kotlin/org/rsmod/content/skills/woodcutting/scripts/Woodcutting.kt`

---

## Phase 2 — Implement

Create the skill module at:
`rsmod/content/skills/$ARGUMENTS/`

Required files:
- `build.gradle.kts` — copy from woodcutting, adjust module name
- `src/main/kotlin/org/rsmod/content/skills/$ARGUMENTS/scripts/${SkillName}.kt`

Key v2 patterns to follow:
```kotlin
class SkillName @Inject constructor(...) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpLoc1(content.thing) {           // content group handler
            // check level gate:
            val level = statMap.getBaseLevel(stats.skillname).toInt() and 0xFF
            if (level < LEVEL_REQ) { mes("You need level X to do this."); return@onOpLoc1 }
            anim(seqs.animation_name)
            delay(4)                          // ticks until result
            statAdvance(stats.skillname, XP)
            invAdd(invs.inv, objs.item_name, 1)
        }
    }
}
```

For resource depletion (mining/woodcutting/fishing): use `locChange` and a Controller for respawn.
For inventory item interactions: use `onOpObj` handlers.

After writing, check `rsmod/api/config/src/main/kotlin/org/rsmod/api/config/refs/` for correct symbol names.

---

## Phase 3 — Build

Use the MCP tool (preferred — no shell needed):
```javascript
build_server({ module: "$ARGUMENTS" })
// Returns full build output and pass/fail
```

Or for a full build:
```javascript
build_server({})
```

Fix any compilation errors before continuing. Common issues:
- Wrong import paths — check existing skill modules for the correct packages
- Missing content group syms — add to `BaseContent.kt` if needed
- Wrong stat/seq/obj symbol names — check `refs/` files

---

## Phase 4 — Verify Server

Check server status with the MCP tool:
```javascript
server_status()
// Returns: game port 43594 status, AgentBridge port 43595 status, observed players
```

If the server is not running, ask the user to start it — do not attempt to start it yourself via shell.

Once the server is up, check for a logged-in test player:
```javascript
list_players()
```

If no player appears within 30s, prompt the user to log in with their test account.

---

## Phase 5 — Run Bot Test

Create `bots/$ARGUMENTS.ts` following the template in `bots/woodcutting.ts`.

The bot script should:
1. Load expected values from `wiki-data/skills/$ARGUMENTS.json`
2. Teleport to the first `test_locations` entry
3. Wait a tick for nearby entities to populate
4. Find the relevant loc/NPC using `sdk.findNearbyLoc(/pattern/i)` or `sdk.findNearbyNpc(/pattern/i)`
5. For each action tier in the wiki oracle:
   - Interact with the object
   - `await bot.waitForXpGain("$ARGUMENTS", 0.5)`
   - Assert XP delta matches expected (±0.5 tolerance)
   - Assert animation matches expected
   - Assert item appeared in inventory if applicable
6. Log PASS/FAIL with actual vs expected for each check

Execute via MCP `execute_script` tool with the bot script content.

---

## Phase 6 — Evaluate and Fix

Interpret the test output:

| Symptom              | Likely cause                           | Fix                                              |
| -------------------- | -------------------------------------- | ------------------------------------------------ |
| No XP gained         | `statAdvance` not called or wrong stat | Check skill stat reference                       |
| Wrong XP amount      | Wrong constant in `*_DEFS`             | Cross-check wiki oracle value                    |
| Wrong animation      | Wrong seq ref                          | Check seqs.* for the right name                  |
| Action too fast/slow | Wrong `delay(n)` value                 | Typical: 4 ticks gathering, 5 fishing, 4 cooking |
| Item not produced    | `invAdd` missing or wrong obj          | Check objs.* symbol name                         |
| Level gate missing   | Level check omitted                    | Add `if (level < req) { mes(...); return }`      |

If tests fail, fix the Kotlin, rebuild (Phase 3), retest (Phase 5). Repeat until all checks pass.

---

## Phase 7 — Finalize

1. Update `docs/CONTENT_AUDIT.md` — change skill status to ✅
2. Update `docs/LLM_TESTING_GUIDE.md` Section 10 (Implemented Content Reference)
3. Update `wiki-data/skills/$ARGUMENTS.json` if you corrected any values during testing
4. Stop the server if started for this session:
   ```bash
   kill $(cat /tmp/rsmod-server.pid) 2>/dev/null
   ```

Report: skill name, all tiers tested, pass/fail count, any known gaps.
