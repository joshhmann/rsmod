/**
 * bots/firemaking.ts — Firemaking skill test
 *
 * Run via MCP execute_script tool. Globals available: bot, sdk
 *
 * Tests the firemaking skill:
 *  - Log burning with tinderbox
 *  - XP gains validation
 *  - Fire creation and remains
 *  - Different log types (normal, oak, willow, maple)
 *
 * Assumes player has tinderbox and logs in inventory.
 * Use ::master and ::invadd tinderbox 1, ::invadd logs 10 etc. in-game chat to set up.
 */

// ---------------------------------------------------------------------------
// Cache-verified data (mcp-osrs rev 233)
// Item IDs
// Tinderbox: 590
// Logs: logs=1511, oak_logs=1521, willow_logs=1519, maple_logs=1517
// Loc IDs: fire_remains=714 (ashes after fire burns out)
// Seq IDs: human_firemaking (tinderbox on logs animation)
// ---------------------------------------------------------------------------

const TINDERBOX_ID = 590;
const FIREMAKING_ANIM = 733; // human_firemaking (cache seq id)

// Firemaking log data (level req, XP, log ID)
const LOG_TIERS = [
  {
    name: "Normal logs",
    levelReq: 1,
    wikiXp: 40.0,
    logId: 1511, // logs
  },
  {
    name: "Oak logs",
    levelReq: 15,
    wikiXp: 60.0,
    logId: 1521, // oak_logs
  },
  {
    name: "Willow logs",
    levelReq: 30,
    wikiXp: 90.0,
    logId: 1519, // willow_logs
  },
  {
    name: "Maple logs",
    levelReq: 45,
    wikiXp: 135.0,
    logId: 1517, // maple_logs
  },
];

// Test locations (open areas for firemaking)
const TEST_LOCATIONS = {
  lumbridgeCourtyard: { x: 3222, z: 3218, desc: "Lumbridge Castle Courtyard" },
  varrockSquare: { x: 3213, z: 3424, desc: "Varrock Grand Exchange" },
  draynorVillage: { x: 3093, z: 3244, desc: "Draynor Village" },
};

// ---------------------------------------------------------------------------
// Test runner
// ---------------------------------------------------------------------------

const results = [];

function record(test, check, pass, expected, actual, note) {
  results.push({ test, check, pass, expected, actual, note });
  const icon = pass ? "✅" : "❌";
  console.log(icon + " [" + test + "] " + check + ": expected=" + JSON.stringify(expected) + ", actual=" + JSON.stringify(actual) + " — " + note);
}

// Helper: Count items in inventory by ID
function countItems(inv, itemId) {
  return inv.filter(i => i.id === itemId).reduce((s, i) => s + i.qty, 0);
}

// Helper: Burn a single log and return results
async function burnLog(logTier, location) {
  const xpBefore = sdk.getSkill("firemaking").xp;
  const logsBefore = countItems(sdk.getInventory(), logTier.logId);
  const tinderboxBefore = countItems(sdk.getInventory(), TINDERBOX_ID);

  // Use tinderbox on logs (held item use - OpHeldU)
  // In RSMod this is onOpHeldU(log.obj, objs.tinderbox) - use item on item
  sdk.sendAction({
    type: "interact_held_u",
    item_id: TINDERBOX_ID,
    target_item_id: logTier.logId
  });

  let animSeen = 0;
  let xpGained = 0;
  let elapsed = 0;
  let success = false;

  while (elapsed < 10000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;
    
    if (p.animation !== 0 && p.animation !== 65535 && animSeen === 0) {
      animSeen = p.animation;
    }
    
    const xpDelta = p.skills.firemaking.xp - xpBefore;
    if (xpDelta > 0) {
      xpGained = xpDelta;
      success = true;
      break;
    }
  }

  await sdk.waitTicks(2);
  
  const inv = sdk.getInventory();
  const logsAfter = countItems(inv, logTier.logId);
  const tinderboxAfter = countItems(inv, TINDERBOX_ID);

  return {
    animSeen,
    xpGained,
    logsConsumed: logsBefore - logsAfter,
    tinderboxKept: tinderboxAfter > 0,
    success,
  };
}

// ---------------------------------------------------------------------------
// Test 1: Basic log burning
// ---------------------------------------------------------------------------

console.log("\n═══ Test 1: Basic Log Burning ═══");

// Teleport to Lumbridge courtyard
sdk.sendTeleport(TEST_LOCATIONS.lumbridgeCourtyard.x, TEST_LOCATIONS.lumbridgeCourtyard.z, 0);
await sdk.waitTicks(3);

// Check for tinderbox
const tinderboxCount = countItems(sdk.getInventory(), TINDERBOX_ID);
record("Setup", "has_tinderbox", tinderboxCount > 0, ">=1", tinderboxCount, 
  tinderboxCount > 0 ? "Tinderbox ready" : "No tinderbox! Use ::invadd tinderbox 1");

if (tinderboxCount === 0) {
  console.log("\n❌ CRITICAL: No tinderbox in inventory. Cannot proceed with firemaking tests.");
  console.log("Setup command: ::invadd tinderbox 1");
}

for (const log of LOG_TIERS) {
  console.log("\n── Testing: " + log.name + " (req lv" + log.levelReq + ", " + log.wikiXp + " XP) ──");
  
  const currentLevel = sdk.getSkill("firemaking").level;
  
  // Check if we have logs
  const logCount = countItems(sdk.getInventory(), log.logId);
  if (logCount < 1) {
    record(log.name, "has_logs", false, ">=1", logCount, 
      "No " + log.name + " in inventory. Use ::invadd logs 10 (or oak_logs, willow_logs, maple_logs)");
    continue;
  }
  record(log.name, "has_logs", true, ">=1", logCount, "Ready to burn");
  
  // Check level requirement
  if (currentLevel < log.levelReq) {
    record(log.name, "level_requirement", false, ">=" + log.levelReq, currentLevel, 
      "Level too low to burn " + log.name + ". Use ::master to set all levels to 99");
    continue;
  }
  record(log.name, "level_requirement", true, ">=" + log.levelReq, currentLevel, "Level requirement met");

  const result = await burnLog(log, TEST_LOCATIONS.lumbridgeCourtyard);

  // Animation check
  const animPass = result.animSeen !== 0 && result.animSeen !== 65535;
  record(log.name, "animation", animPass, "valid anim", result.animSeen,
    animPass ? "Firemaking animation played (anim=" + result.animSeen + ")" : (result.animSeen === 0 ? "No anim seen" : "Unexpected anim " + result.animSeen));

  // XP check
  const xpPass = result.xpGained > 0;
  const effectiveRate = result.xpGained > 0 ? (result.xpGained / log.wikiXp).toFixed(1) + "x" : "n/a";
  record(log.name, "xp_grant", xpPass, ">0 (wiki=" + log.wikiXp + ")", result.xpGained,
    xpPass ? "XP granted, effective rate: " + effectiveRate : "No XP gained");

  // Log consumption check
  record(log.name, "log_consumed", result.logsConsumed > 0, ">=1", result.logsConsumed,
    result.logsConsumed > 0 ? "Log consumed in fire" : "Log not consumed");

  // Tinderbox preserved check
  record(log.name, "tinderbox_preserved", result.tinderboxKept, true, result.tinderboxKept,
    result.tinderboxKept ? "Tinderbox preserved (reusable)" : "Tinderbox consumed!");
}

// ---------------------------------------------------------------------------
// Test 2: Success rate observation (burn multiple logs)
// ---------------------------------------------------------------------------

console.log("\n═══ Test 2: Fire Creation Success Rate ═══");

const testLog = LOG_TIERS[0]; // Normal logs
const burnAttempts = 5;
let successCount = 0;
let failCount = 0;

console.log("Attempting to light " + burnAttempts + " " + testLog.name + "...");

for (let i = 0; i < burnAttempts; i++) {
  const logCount = countItems(sdk.getInventory(), testLog.logId);
  if (logCount < 1) {
    console.log("  Out of " + testLog.name);
    break;
  }
  
  const result = await burnLog(testLog, TEST_LOCATIONS.lumbridgeCourtyard);
  
  if (result.success) {
    successCount++;
  } else {
    failCount++;
  }
  
  await sdk.waitTicks(1);
}

record("Success Rate", "successful_fires", true, ">=0", successCount, successCount + " fires lit successfully");
record("Success Rate", "failed_attempts", true, ">=0", failCount, failCount + " failed attempts");
const successRate = burnAttempts > 0 ? ((successCount / burnAttempts) * 100).toFixed(1) + "%" : "n/a";
console.log("  Success rate observed: " + successRate + " (" + successCount + "/" + burnAttempts + ")");

// ---------------------------------------------------------------------------
// Test 3: Fire remains check (fire burns out and leaves ashes)
// ---------------------------------------------------------------------------

console.log("\n═══ Test 3: Fire to Ashes Transition ═══");

console.log("Note: Fire remains (ashes) appear after fire burns out (~1-2 minutes)");
console.log("This test verifies the fire was created; remains check is manual observation");

const fireLog = LOG_TIERS[0]; // Normal logs
const fireResult = await burnLog(fireLog, TEST_LOCATIONS.lumbridgeCourtyard);

record("Fire Creation", "fire_lit", fireResult.success, true, fireResult.success,
  fireResult.success ? "Fire was successfully lit" : "Failed to light fire");

// ---------------------------------------------------------------------------
// Test 4: Higher tier logs (if levels permit)
// ---------------------------------------------------------------------------

console.log("\n═══ Test 4: Higher Tier Log Validation ═══");

const currentLevel = sdk.getSkill("firemaking").level;
console.log("Current Firemaking Level: " + currentLevel);
record("Level Check", "firemaking_level", true, ">=1", currentLevel, "Player firemaking level");

for (const log of LOG_TIERS.slice(1)) { // Skip normal logs (already tested)
  if (currentLevel >= log.levelReq) {
    console.log("\n── Testing: " + log.name + " (req lv" + log.levelReq + ", " + log.wikiXp + " XP) ──");
    
    const logCount = countItems(sdk.getInventory(), log.logId);
    if (logCount < 1) {
      record(log.name, "has_logs", false, ">=1", logCount, "No " + log.name + " in inventory");
      continue;
    }
    
    const result = await burnLog(log, TEST_LOCATIONS.lumbridgeCourtyard);
    
    const xpPass = result.xpGained > 0;
    record(log.name, "xp_grant", xpPass, ">0", result.xpGained,
      xpPass ? "Higher tier log gives XP" : "No XP gained");
  } else {
    console.log("  Skipping " + log.name + " - requires level " + log.levelReq + ", have " + currentLevel);
  }
}

// ---------------------------------------------------------------------------
// Summary
// ---------------------------------------------------------------------------

const passed = results.filter(r => r.pass).length;
const failed = results.filter(r => !r.pass).length;

console.log("\n═══════════════════════════════════════════════════");
console.log("══ Firemaking Test Summary: " + passed + " passed, " + failed + " failed ══");
console.log("═══════════════════════════════════════════════════");

if (failed > 0) {
  console.log("\nFAILURES:");
  for (const r of results.filter(r => !r.pass)) {
    console.log("  ❌ [" + r.test + "] " + r.check + ": expected=" + JSON.stringify(r.expected) + ", actual=" + JSON.stringify(r.actual) + " — " + r.note);
  }
}

console.log("\nTested log tiers: " + LOG_TIERS.map(l => l.name).join(", "));
console.log("Test location: " + TEST_LOCATIONS.lumbridgeCourtyard.desc);
console.log("\nSetup commands for future runs:");
console.log("  ::master");
console.log("  ::invadd tinderbox 1");
console.log("  ::invadd logs 20");
console.log("  ::invadd oak_logs 20");
console.log("  ::invadd willow_logs 20");
console.log("  ::invadd maple_logs 20");

({ summary: { passed, failed, total: results.length }, results, logsTested: LOG_TIERS.length });
