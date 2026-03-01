---
name: rsmod-content-verifier
description: Validate RSMod v2 content accuracy against OSRS wiki specifications. Use when verifying skill implementations, checking XP rates match wiki, validating drop tables, or running automated content audits.
---

# RSMod v2 Content Verifier

## Task Coordination — Do This First

Multiple agents (Claude, OpenCode, Kimi, Codex) work this codebase simultaneously.
Before writing a single line of code, coordinate via the `agent-tasks` MCP server:

1. `list_tasks({ status: "pending" })` — find available work
2. `get_task("TASK-ID")` — read the full task description
3. `claim_task("TASK-ID", "your-agent-name")` — atomically claim it (fails if taken)
4. `check_conflicts(["path/file"])` — verify no one else is editing your files
5. `lock_file("path/file", "your-agent-name", "TASK-ID")` — lock every file before editing
6. Implement.
7. `complete_task("TASK-ID", "your-agent-name", "what I built")` — releases locks, marks done

If blocked: `block_task("TASK-ID", "your-agent-name", "exact reason")` so others know.
See `START_HERE.md` for the full project orientation.

Validate implemented content against wiki specifications. Covers skill verification, drop rate validation, and content audit updates.

## Validation Categories

### 1. Skill Validation

Verify skill implementation matches wiki oracle:

```kotlin
// Automated checks in bot test:
val expectedXp = wiki.actions["willow_tree"]?.xp ?: 67.5
val actualXp = xpGained
val xpPass = Math.abs(actualXp - expectedXp) <= 0.5

val expectedAnim = wiki.actions["willow_tree"]?.animation ?: 879
val actualAnim = animSeen
val animPass = actualAnim == expectedAnim
```

#### Checklist per Skill

- [ ] **Access Control**: Blocked below level req, allowed at req
- [ ] **XP Grant**: Matches wiki (±0.5 tolerance)
- [ ] **Animation**: Correct sequence ID
- [ ] **Timing**: Expected tick duration
- [ ] **Item Produced**: Correct item appears
- [ ] **Item Consumed**: Tool/bait used correctly
- [ ] **Level-up**: Fires at correct XP threshold
- [ ] **Resource Depletion**: Respawns after correct ticks
- [ ] **Edge Cases**: Full inventory, wrong tool, etc.

### 2. Common Failure Patterns

| Pattern | Symptom | Likely Cause | Fix |
|---------|---------|--------------|-----|
| Wrong XP | `actual ≠ expected` | Wrong constant in `*_DEFS` | Cross-check wiki oracle |
| No XP | `delta == 0` | `statAdvance` not called | Check skill stat ref |
| Wrong animation | ID mismatch | Wrong seq ref | Check `seqs.*` names |
| Too fast | Ticks < expected | Wrong `delay(n)` | Verify tick counts |
| Too slow | Ticks > expected | Extra `delay` call | Remove excess delays |
| No item | Missing in inv | `invAdd` not called | Add item grant |
| Double XP | `delta` = 2× | Duplicate `statAdvance` | Remove duplicate call |

### 3. Tick Timing Reference

Standard OSRS tick counts:

| Action | Ticks | Time |
|--------|-------|------|
| Bury bone | 1 | 0.6s |
| Light fire | 3 | 1.8s |
| Woodcutting swing | 4 | 2.4s |
| Mining swing | 3 | 1.8s |
| Cooking (range) | 4 | 2.4s |
| Cooking (fire) | 5 | 3.0s |
| Fishing cast | 5 | 3.0s |
| Pickpocket | 2 | 1.2s |
| Potion drink | 3 | 1.8s |
| Food eat | 3 | 1.8s |

## Automated Verification Workflow

### Step 1: Gather Expected Data
```typescript
// Load wiki oracle
const wiki = JSON.parse(
  readFileSync("wiki-data/skills/skillname.json", "utf-8")
);
```

### Step 2: Execute Test Actions
```typescript
for (const [name, action] of Object.entries(wiki.actions)) {
  // Teleport to test location
  sdk.sendTeleport(wiki.test_locations[0].x, wiki.test_locations[0].z, 0);
  await sdk.waitTicks(2);
  
  // Find target
  const target = sdk.findNearbyLoc(new RegExp(name, "i"));
  if (!target) { results.push({fail: true, reason: "Target not found"}); continue; }
  
  // Record pre-state
  const xpBefore = sdk.getSkill(wiki.skill)!.xp;
  
  // Interact
  sdk.sendInteractLoc(target.id, target.x, target.z, 1);
  
  // Wait for result
  await bot.waitForXpGain(wiki.skill, 0.5, 15000);
  
  // Verify
  const xpAfter = sdk.getSkill(wiki.skill)!.xp;
  const xpDelta = xpAfter - xpBefore;
  
  results.push({
    action: name,
    xpPass: Math.abs(xpDelta - action.xp) <= 0.5,
    expectedXp: action.xp,
    actualXp: xpDelta
  });
}
```

### Step 3: Generate Report
```typescript
console.log("\n══ Verification Report ══");
for (const r of results) {
  const icon = r.xpPass ? "✅" : "❌";
  console.log(`${icon} ${r.action}: XP ${r.actualXp}/${r.expectedXp}`);
}
```

## Drop Rate Validation

### Statistical Testing

For rare drops (1/128 or lower), use statistical validation:

```python
# Minimum samples for confidence
# For 1/128 drop at 95% confidence:
# Need ~500 kills to verify within ±20% of expected rate

import random

def simulate_drops(rate_numerator, rate_denominator, samples):
    """Simulate drops to verify RNG distribution"""
    drops = 0
    for _ in range(samples):
        if random.randint(1, rate_denominator) <= rate_numerator:
            drops += 1
    return drops / samples

# Expected: 1/128 = 0.0078125
observed = simulate_drops(1, 128, 10000)
expected = 1/128
print(f"Expected: {expected:.6f}, Observed: {observed:.6f}")
```

### In-Game Drop Testing

1. Enable drop logging:
```kotlin
// Temporary debug logging
onNpcDeath(npcs.goblin) { event ->
    val drops = npc.dropTable.roll(event.killer)
    drops.forEach { drop ->
        logger.info { "Drop: ${drop.obj.name} x${drop.count}" }
    }
}
```

2. Kill NPC 100+ times, verify distribution

3. Check guaranteed drops (100% rate) always drop

## Content Audit Updates

After verifying content, update `docs/CONTENT_AUDIT.md`:

```markdown
## Skills

| Skill | Status | Notes |
|-------|--------|-------|
| Woodcutting | ✅ | Verified: all trees, XP rates match wiki |
| Mining | ✅ | Verified: all ores, pickaxe tiers |
| Cooking | ✅ | Verified: burn rates, XP, 19 fish types |
```

### Status Legend
- ✅ **Verified**: Tested and matches wiki
- 🟡 **Partial**: Some content working, gaps noted
- ❌ **Not started**: No implementation
- ⚠️ **Needs fix**: Implemented but has bugs

## Manual Verification Commands

In-game admin commands for testing:

```
::setlevel woodcutting 60    # Set skill level
::give logs 100              # Give items
::tele 3222 3219            # Teleport to location
::npc goblin                # Spawn NPC
::killnpc                   # Kill nearby NPC
```

## Regression Testing

After any content change, verify:

1. **No XP loss**: Existing skills still grant XP
2. **No animation loss**: Animations still play
3. **No item loss**: Items still produced/consumed correctly
4. **No timing changes**: Tick counts unchanged

## Verification Report Template

```markdown
# Skill Verification Report: [Skill Name]

Date: 2026-02-20
Tester: Automated / Manual

## Actions Tested

| Action | Level Req | XP Expected | XP Actual | Pass |
|--------|-----------|-------------|-----------|------|
| Action 1 | 1 | 25.0 | 25.0 | ✅ |
| Action 2 | 15 | 37.5 | 37.5 | ✅ |

## Issues Found

- [ ] None / List issues

## Conclusion

Skill is [verified / needs work] for production.
```

## Continuous Integration

Recommended CI checks:

```yaml
# .github/workflows/verify.yml (conceptual)
- name: Verify Skills
  run: |
    ./gradlew build
    ./gradlew run &
    sleep 30
    python agent-runner/run.py --test skills --all
    
- name: Verify Drops  
  run: python agent-runner/run.py --test drops --samples 1000
```

## See Also

- `docs/CONTENT_AUDIT.md` for current status
- `docs/LLM_TESTING_GUIDE.md` for test methodology
- `wiki-data/` for expected values
