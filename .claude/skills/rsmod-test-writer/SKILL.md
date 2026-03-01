---
name: rsmod-test-writer
description: Write bot test scripts for RSMod v2 skill validation. Use when creating TypeScript test bots for skills, implementing automated game testing, or validating skill mechanics (XP, animations, items) via the AgentBridge MCP system.
---

# RSMod v2 Test Writer

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

Write TypeScript bot test scripts for RSMod v2 skills. Tests run via MCP `execute_script` tool with access to `bot` and `sdk` APIs.

## Test Script Structure

```typescript
/**
 * bots/skillname.ts - [Skill] skill test
 * 
 * Tests: level gates, XP grants, animations, item production
 */

// ---------------------------------------------------------------------------
// Test data (mirrors wiki-data/skills/skillname.json)
// ---------------------------------------------------------------------------

const TIERS = [
  {
    name: "Action Name",
    levelReq: 1,
    xp: 25.0,
    animation: 879,
    itemId: 1511,        // Produced item ID
    locPattern: /^Tree$/i,  // Regex to match loc name
    npcPattern: /^Fishing spot$/i, // For NPC interactions
    testX: 3184, testZ: 3436,
  },
  // ... more tiers
] as const;

// ---------------------------------------------------------------------------
// Test runner
// ---------------------------------------------------------------------------

const results: Array<{
  tier: string;
  check: string;
  pass: boolean;
  expected: unknown;
  actual: unknown;
  note: string;
}> = [];

function record(tier: string, check: string, pass: boolean, expected: unknown, actual: unknown, note: string) {
  results.push({ tier, check, pass, expected, actual, note });
  const icon = pass ? "✅" : "❌";
  console.log(`${icon} [${tier}] ${check}: expected=${JSON.stringify(expected)}, actual=${JSON.stringify(actual)} — ${note}`);
}

// ---------------------------------------------------------------------------
// Main test loop
// ---------------------------------------------------------------------------

for (const tier of TIERS) {
  console.log(`\n── Testing: ${tier.name} (req lv${tier.levelReq}) ──`);
  
  // 1. Teleport to test location
  sdk.sendTeleport(tier.testX, tier.testZ, 0);
  await sdk.waitTicks(2);
  
  // 2. Check current skill level
  const skill = sdk.getSkill("skillname");
  if (!skill) { console.log("No state — skipping"); continue; }
  const level = skill.level;
  
  // 3. Access control test (if below level)
  if (level < tier.levelReq) {
    const target = sdk.findNearbyLoc(tier.locPattern);
    if (target) {
      const xpBefore = sdk.getSkill("skillname")!.xp;
      sdk.sendInteractLoc(target.id, target.x, target.z, 1);
      await sdk.waitTicks(4);
      const xpAfter = sdk.getSkill("skillname")!.xp;
      const blocked = xpAfter === xpBefore;
      record(tier.name, "access_control", blocked, "blocked", blocked ? "blocked" : "allowed", 
        `Level ${level} < req ${tier.levelReq}`);
    }
    continue;
  }
  
  // 4. Find and interact with target
  const target = sdk.findNearbyLoc(tier.locPattern);
  if (!target) {
    record(tier.name, "loc_found", false, tier.locPattern.toString(), "not found", 
      "No matching loc within 16 tiles");
    continue;
  }
  record(tier.name, "loc_found", true, tier.locPattern.toString(), target.name, 
    `Found id=${target.id} at (${target.x},${target.z})`);
  
  // 5. Record pre-state
  const itemCountBefore = sdk.getInventory()?.filter(i => i.id === tier.itemId)
    .reduce((s, i) => s + i.qty, 0) ?? 0;
  const xpBefore = sdk.getSkill("skillname")!.xp;
  
  // 6. Interact
  sdk.sendInteractLoc(target.id, target.x, target.z, 1);
  
  // 7. Wait for XP gain (up to 15s = 25 ticks)
  let xpGained = 0;
  let animSeen = 0;
  let elapsed = 0;
  while (elapsed < 15_000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const state = sdk.getPlayer();
    if (!state) continue;
    const currentXp = state.skills["skillname"].xp;
    const delta = currentXp - xpBefore;
    if (state.animation !== 0 && animSeen === 0) animSeen = state.animation;
    if (delta > 0) { xpGained = delta; break; }
  }
  
  // 8. Check animation
  record(tier.name, "animation", animSeen === tier.animation, tier.animation, animSeen,
    animSeen === tier.animation ? "Correct animation" : `Got ${animSeen}, expected ${tier.animation}`);
  
  // 9. Check XP
  const xpPass = Math.abs(xpGained - tier.xp) <= 0.5;
  record(tier.name, "xp_grant", xpPass, tier.xp, xpGained,
    xpPass ? "XP matches wiki" : `Delta: ${xpGained - tier.xp}`);
  
  // 10. Check item produced
  await sdk.waitTicks(1);
  const itemCountAfter = sdk.getInventory()?.filter(i => i.id === tier.itemId)
    .reduce((s, i) => s + i.qty, 0) ?? 0;
  const itemProduced = itemCountAfter > itemCountBefore;
  record(tier.name, "item_produced", itemProduced, `+1 (id=${tier.itemId})`,
    itemProduced ? `+${itemCountAfter - itemCountBefore}` : "none",
    itemProduced ? "Item added to inventory" : "No item found");
}

// ---------------------------------------------------------------------------
// Summary
// ---------------------------------------------------------------------------

const passed = results.filter(r => r.pass).length;
const failed = results.filter(r => !r.pass).length;
console.log(`\n══ Test Summary: ${passed} passed, ${failed} failed ══`);
if (failed > 0) {
  console.log("FAILURES:");
  for (const r of results.filter(r => !r.pass)) {
    console.log(`  ❌ [${r.tier}] ${r.check}: expected=${JSON.stringify(r.expected)}, actual=${JSON.stringify(r.actual)}`);
  }
}

({ summary: { passed, failed }, results });
```

## SDK API Reference

### State Access
```typescript
sdk.getState()                    // Full state snapshot
sdk.getPlayer()                   // Player state (skills, inventory, etc.)
sdk.getSkill("woodcutting")       // Specific skill { level, xp }
sdk.getInventory()                // Array of { slot, id, qty }
sdk.getEquipment()                // Array of { slot, id, qty }
```

### Actions
```typescript
sdk.sendWalk(x: number, z: number)
sdk.sendTeleport(x: number, z: number, plane: number)
sdk.sendInteractLoc(id: number, x: number, z: number, option: number)
sdk.sendInteractNpc(index: number, option: number)
```

### Finders
```typescript
sdk.findNearbyLoc(pattern: RegExp)     // Find loc by name pattern
sdk.findNearbyNpc(pattern: RegExp)     // Find NPC by name pattern
```

### Waiting
```typescript
await sdk.waitTicks(n: number)         // Wait N game ticks (600ms each)
await sdk.waitForXpGain(skill: string, minAmount: number, timeoutMs: number)
await sdk.waitForAnimation(animId: number, timeoutMs: number)
await sdk.waitForItem(itemId: number, timeoutMs: number)
```

### High-level Bot API
```typescript
await bot.walkTo(x: number, z: number)
await bot.interactLoc(pattern: RegExp | string, option?: number)
await bot.interactNpc(pattern: RegExp | string, option?: number)
await bot.waitForXpGain(skill: string, minDelta?: number, timeout?: number)
```

## Test Patterns by Skill Type

### Gathering Skills (Woodcutting/Mining)

Key checks:
- Tool requirement verification
- Level gate blocking
- Animation on swing
- XP on success (not every tick)
- Item to inventory
- Resource depletion

```typescript
// Wait for XP (may take multiple ticks)
let xpGained = 0;
for (let i = 0; i < 30; i++) {
  await sdk.waitTicks(1);
  const currentXp = sdk.getSkill("woodcutting")!.xp;
  xpGained = currentXp - xpBefore;
  if (xpGained > 0) break;
}
```

### Processing Skills (Cooking/Smithing)

Key checks:
- Success/burn outcomes
- Item transformation (raw → cooked/burnt)
- XP only on success

```typescript
// Check for EITHER success or burn
const rawCountAfter = sdk.getInventory()?.filter(i => i.id === tier.rawId)
  .reduce((s, i) => s + i.qty, 0) ?? 0;
const cookedCountAfter = sdk.getInventory()?.filter(i => i.id === tier.cookedId)
  .reduce((s, i) => s + i.qty, 0) ?? 0;
const burntCountAfter = sdk.getInventory()?.filter(i => i.id === tier.burntId)
  .reduce((s, i) => s + i.qty, 0) ?? 0;

const success = cookedCountAfter > cookedCountBefore;
const burnt = burntCountAfter > burntCountBefore;
record(tier.name, "outcome", success || burnt, "processed", success ? "cooked" : burnt ? "burnt" : "none", "");
```

### Artisan Skills (Fletching/Herblore)

Key checks:
- Item-on-item interaction
- Multi-step chains
- Correct output quantity

```typescript
// Pre-check materials
const logCountBefore = sdk.countItem(tier.logId);
const knifeCount = sdk.countItem(objs.knife);
if (knifeCount === 0) {
  console.log("No knife - skipping");
  continue;
}
```

### NPC Interaction (Thieving/Pickpocket)

```typescript
const npc = sdk.findNearbyNpc(tier.npcPattern);
if (!npc) { /* fail */ }

const xpBefore = sdk.getSkill("thieving")!.xp;
sdk.sendInteractNpc(npc.index, 2);  // Option 2 = pickpocket

let xpGained = 0;
for (let i = 0; i < 10; i++) {
  await sdk.waitTicks(1);
  const state = sdk.getPlayer();
  // Check for stun animation or damage
  if (state?.animation === 424) {
    record(tier.name, "stun", true, "stunned", "stunned", "Failed pickpocket - stunned");
    break;
  }
  xpGained = (sdk.getSkill("thieving")?.xp ?? 0) - xpBefore;
  if (xpGained > 0) break;
}
```

## Common Test Locations

| Content | Location | Coords |
|---------|----------|--------|
| Normal trees | Draynor Village | 3184, 3436 |
| Oak trees | Draynor Village | 3184, 3436 |
| Willow trees | Draynor Village | 3088, 3237 |
| Yew trees | Lumbridge | 3221, 3426 |
| Fishing spots | Draynor | 3086, 3230 |
| Range | Lumbridge castle | 3209, 3215 |
| Furnace | Al Kharid | 3274, 3186 |
| Anvil | Varrock | 3187, 3426 |

## Result Format

Tests should return:
```typescript
({ 
  summary: { 
    passed: number, 
    failed: number 
  }, 
  results: Array<{
    tier: string;
    check: string;
    pass: boolean;
    expected: unknown;
    actual: unknown;
    note: string;
  }>
});
```

## Check Types

| Check | When to use |
|-------|-------------|
| `access_control` | Level gate verification |
| `xp_grant` | XP amount validation |
| `animation` | Animation ID verification |
| `item_produced` | Inventory item added |
| `item_consumed` | Material removed |
| `loc_found` | Target entity exists |
| `outcome` | Success/fail determination |

## Running Tests

Via MCP `execute_script` tool:
```json
{
  "player": "TestBot",
  "code": "/* test script content */",
  "timeout": 120000
}
```

## Debugging Failed Tests

| Symptom | Likely Cause |
|---------|--------------|
| No XP gained | Wrong stat ref, statAdvance not called |
| Wrong XP | Wrong constant in skill plugin |
| Wrong animation | Wrong seq ref |
| Action too fast/slow | Wrong delay() value |
| Item not produced | Wrong obj ref in invAdd |
| Target not found | Wrong coordinates or pattern |
