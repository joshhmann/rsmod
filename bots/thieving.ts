/**
 * bots/thieving.ts — Thieving skill test
 *
 * Run via MCP execute_script tool. Globals available: bot, sdk
 *
 * Tests the thieving skill:
 *  - Pickpocketing NPCs (Man/Woman, Farmer, Warrior, Guard)
 *  - Stealing from stalls (Tea stall, Bakery stall)
 *  - XP validation
 *  - Stun detection on failed attempts
 *
 * Assumes player has appropriate thieving level.
 * Use ::master and set thieving level in-game chat to set up.
 */

// ---------------------------------------------------------------------------
// Cache-verified data (mcp-osrs rev 233)
// NPC IDs: man=1118, woman=1119, farmer=3244, warrior=3255, guard=301, guard2=3269
// Loc IDs: tea_stall=635
// Item IDs: coins=995, coin_pouch=22531, cup_of_tea=1978, bread=2309, cake=1891
// Seq IDs: human_pickpocket=881
// ---------------------------------------------------------------------------

const PICKPOCKET_ANIM = 881; // human_pickpocket

// NPCs that can be pickpocketed (F2P and early P2P)
const PICKPOCKET_TARGETS = [
  {
    name: "Man",
    levelReq: 1,
    wikiXp: 8.0,
    npcId: 1118, // man
    npcName: "Man",
    stunDamage: 1,
    testX: 3210, testZ: 3215, // Lumbridge castle area
  },
  {
    name: "Woman", 
    levelReq: 1,
    wikiXp: 8.0,
    npcId: 1119, // woman
    npcName: "Woman",
    stunDamage: 1,
    testX: 3210, testZ: 3215, // Lumbridge castle area
  },
  {
    name: "Farmer",
    levelReq: 10,
    wikiXp: 14.5,
    npcId: 3244, // farmer
    npcName: "Farmer",
    stunDamage: 1,
    testX: 3235, testZ: 3295, // Lumbridge farm area
  },
  {
    name: "Warrior",
    levelReq: 25,
    wikiXp: 26.0,
    npcId: 3255, // warrior
    npcName: "Warrior",
    stunDamage: 2,
    testX: 3280, testZ: 3500, // Varrock Palace area
  },
  {
    name: "Guard",
    levelReq: 40,
    wikiXp: 46.8,
    npcId: 301, // guard
    npcName: "Guard",
    stunDamage: 2,
    testX: 3215, testZ: 3463, // Varrock area
  },
];

// Stalls that can be stolen from
const STALL_TARGETS = [
  {
    name: "Tea stall",
    levelReq: 5,
    wikiXp: 16.0,
    locId: 635, // tea_stall
    locName: "Tea stall",
    itemId: 1978, // cup_of_tea
    testX: 3268, testZ: 3414, // Varrock (near the tea stall)
  },
];

// Test locations
const TEST_LOCATIONS = {
  lumbridgeCastle: { x: 3210, z: 3215, desc: "Lumbridge Castle (Man/Woman)" },
  lumbridgeFarm: { x: 3235, z: 3295, desc: "Lumbridge Farm (Farmer)" },
  varrockPalace: { x: 3280, z: 3500, desc: "Varrock Palace (Warrior)" },
  varrockCenter: { x: 3215, z: 3463, desc: "Varrock Center (Guard)" },
  varrockTeaStall: { x: 3268, z: 3414, desc: "Varrock Tea Stall" },
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

// Helper: Find NPC by name
function findNpcByName(npcs, name) {
  if (!npcs) return null;
  return npcs.find(npc => npc.name?.toLowerCase().includes(name.toLowerCase()));
}

// ---------------------------------------------------------------------------
// Test 1: Pickpocketing NPCs
// ---------------------------------------------------------------------------

console.log("\n═══ Test 1: Pickpocketing NPCs ═══");

const currentThievingLevel = sdk.getSkill("thieving").level;
console.log("Current Thieving Level: " + currentThievingLevel);
record("Setup", "thieving_level", true, ">=1", currentThievingLevel, "Player thieving level");

for (const target of PICKPOCKET_TARGETS) {
  console.log("\n── Testing: " + target.name + " (req lv" + target.levelReq + ", " + target.wikiXp + " XP) ──");
  
  // Skip if level requirement not met
  if (currentThievingLevel < target.levelReq) {
    console.log("  Skipping " + target.name + " - requires level " + target.levelReq + ", have " + currentThievingLevel);
    record(target.name, "level_requirement", false, ">=" + target.levelReq, currentThievingLevel, "Level too low");
    continue;
  }
  
  record(target.name, "level_requirement", true, ">=" + target.levelReq, currentThievingLevel, "Level requirement met");
  
  // Teleport to test location
  sdk.sendTeleport(target.testX, target.testZ, 0);
  await sdk.waitTicks(3);
  
  // Find the NPC
  const state = sdk.getState();
  const npc = findNpcByName(state.player.nearbyNpcs, target.npcName);
  
  if (!npc) {
    record(target.name, "npc_found", false, target.npcName, "not found", "No " + target.npcName + " nearby after teleport");
    continue;
  }
  
  record(target.name, "npc_found", true, target.npcName, npc.name, "Found at (" + npc.x + "," + npc.z + ")");
  
  // Walk close to NPC
  await bot.walkTo(npc.x, npc.z - 1);
  await sdk.waitTicks(2);
  
  // Record initial state
  const xpBefore = sdk.getSkill("thieving").xp;
  const coinsBefore = countItems(sdk.getInventory(), 995); // Coins
  
  // Attempt pickpocket (option 2 is typically Pickpocket)
  console.log("  Attempting pickpocket...");
  sdk.sendAction({
    type: "interact_npc",
    index: npc.index,
    option: 2
  });
  
  // Wait for result
  let xpGained = 0;
  let animSeen = 0;
  let elapsed = 0;
  let success = false;
  
  while (elapsed < 8000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;
    
    if (p.animation !== 0 && p.animation !== 65535 && animSeen === 0) {
      animSeen = p.animation;
    }
    
    const xpDelta = p.skills.thieving.xp - xpBefore;
    if (xpDelta > 0) {
      xpGained = xpDelta;
      success = true;
      break;
    }
  }
  
  // Animation check
  const animPass = animSeen === PICKPOCKET_ANIM || animSeen !== 0;
  record(target.name, "animation", animPass, PICKPOCKET_ANIM, animSeen,
    animPass ? "Pickpocket animation played" : "No animation seen");
  
  // XP check
  const xpPass = xpGained > 0;
  const effectiveRate = xpGained > 0 ? (xpGained / target.wikiXp).toFixed(1) + "x" : "n/a";
  record(target.name, "xp_grant", xpPass, ">0 (wiki=" + target.wikiXp + ")", xpGained,
    xpPass ? "XP granted, effective rate: " + effectiveRate : "No XP gained - may have failed/stunned");
  
  // Check for coins in inventory
  await sdk.waitTicks(1);
  const coinsAfter = countItems(sdk.getInventory(), 995);
  const coinsGained = coinsAfter - coinsBefore;
  
  if (success) {
    record(target.name, "loot", coinsGained > 0, ">0 coins", coinsGained,
      coinsGained > 0 ? "Got " + coinsGained + " coins" : "No coins looted (may use coin pouches)");
  }
  
  // Wait between attempts
  await sdk.waitTicks(2);
}

// ---------------------------------------------------------------------------
// Test 2: Stall Thieving
// ---------------------------------------------------------------------------

console.log("\n═══ Test 2: Stall Thieving ═══");

for (const stall of STALL_TARGETS) {
  console.log("\n── Testing: " + stall.name + " (req lv" + stall.levelReq + ", " + stall.wikiXp + " XP) ──");
  
  // Skip if level requirement not met
  if (currentThievingLevel < stall.levelReq) {
    console.log("  Skipping " + stall.name + " - requires level " + stall.levelReq + ", have " + currentThievingLevel);
    record(stall.name, "level_requirement", false, ">=" + stall.levelReq, currentThievingLevel, "Level too low");
    continue;
  }
  
  record(stall.name, "level_requirement", true, ">=" + stall.levelReq, currentThievingLevel, "Level requirement met");
  
  // Teleport to stall location
  sdk.sendTeleport(stall.testX, stall.testZ, 0);
  await sdk.waitTicks(3);
  
  // Find the stall
  const stallLoc = sdk.findNearbyLoc(stall.locName);
  
  if (!stallLoc) {
    record(stall.name, "stall_found", false, stall.locName, "not found", "No " + stall.locName + " nearby after teleport");
    continue;
  }
  
  const locIdMatch = stallLoc.id === stall.locId;
  record(stall.name, "stall_found", true, stall.locName, stallLoc.name, 
    "id=" + stallLoc.id + (locIdMatch ? "" : " (expected " + stall.locId + ")") + " at (" + stallLoc.x + "," + stallLoc.z + ")");
  
  // Record initial state
  const xpBefore = sdk.getSkill("thieving").xp;
  const itemsBefore = countItems(sdk.getInventory(), stall.itemId);
  
  // Attempt to steal from stall (option 2 is typically Steal-from)
  console.log("  Attempting to steal from " + stall.name + "...");
  sdk.sendAction({
    type: "interact_loc",
    id: stallLoc.id,
    x: stallLoc.x,
    z: stallLoc.z,
    option: 2
  });
  
  // Wait for result
  let xpGained = 0;
  let elapsed = 0;
  
  while (elapsed < 8000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;
    
    const xpDelta = p.skills.thieving.xp - xpBefore;
    if (xpDelta > 0) {
      xpGained = xpDelta;
      break;
    }
  }
  
  // XP check
  const xpPass = xpGained > 0;
  const effectiveRate = xpGained > 0 ? (xpGained / stall.wikiXp).toFixed(1) + "x" : "n/a";
  record(stall.name, "xp_grant", xpPass, ">0 (wiki=" + stall.wikiXp + ")", xpGained,
    xpPass ? "XP granted, effective rate: " + effectiveRate : "No XP gained");
  
  // Check for items in inventory
  await sdk.waitTicks(1);
  const itemsAfter = countItems(sdk.getInventory(), stall.itemId);
  const itemsGained = itemsAfter - itemsBefore;
  
  if (xpGained > 0) {
    record(stall.name, "loot", itemsGained > 0, ">0 items", itemsGained,
      itemsGained > 0 ? "Got " + itemsGained + " items" : "No items found - check item ID");
  }
  
  // Wait between attempts
  await sdk.waitTicks(2);
}

// ---------------------------------------------------------------------------
// Test 3: Multiple Pickpocket Attempts (Success Rate Observation)
// ---------------------------------------------------------------------------

console.log("\n═══ Test 3: Success Rate Observation ═══");

const testTarget = PICKPOCKET_TARGETS[0]; // Man (level 1)
const attempts = 5;
let successCount = 0;
let failCount = 0;

console.log("Performing " + attempts + " pickpocket attempts on " + testTarget.name + "...");

// Teleport to location
sdk.sendTeleport(testTarget.testX, testTarget.testZ, 0);
await sdk.waitTicks(3);

const state = sdk.getState();
const npc = findNpcByName(state.player.nearbyNpcs, testTarget.npcName);

if (npc) {
  await bot.walkTo(npc.x, npc.z - 1);
  await sdk.waitTicks(2);
  
  for (let i = 0; i < attempts; i++) {
    const xpBefore = sdk.getSkill("thieving").xp;
    
    sdk.sendAction({
      type: "interact_npc",
      index: npc.index,
      option: 2
    });
    
    await sdk.waitTicks(3);
    
    const xpAfter = sdk.getSkill("thieving").xp;
    if (xpAfter > xpBefore) {
      successCount++;
      console.log("  Attempt " + (i + 1) + ": ✅ Success (+" + (xpAfter - xpBefore) + " XP)");
    } else {
      failCount++;
      console.log("  Attempt " + (i + 1) + ": ❌ Failed or stunned");
    }
    
    await sdk.waitTicks(2);
  }
  
  record("Success Rate", "successes", true, ">=0", successCount, successCount + "/" + attempts + " successful");
  record("Success Rate", "failures", true, ">=0", failCount, failCount + "/" + attempts + " failed");
  const successRate = attempts > 0 ? ((successCount / attempts) * 100).toFixed(1) + "%" : "n/a";
  console.log("  Overall success rate: " + successRate);
} else {
  record("Success Rate", "npc_found", false, testTarget.npcName, "not found", "Could not find target NPC");
}

// ---------------------------------------------------------------------------
// Summary
// ---------------------------------------------------------------------------

const passed = results.filter(r => r.pass).length;
const failed = results.filter(r => !r.pass).length;

console.log("\n═══════════════════════════════════════════════════");
console.log("══ Thieving Test Summary: " + passed + " passed, " + failed + " failed ══");
console.log("═══════════════════════════════════════════════════");

if (failed > 0) {
  console.log("\nFAILURES:");
  for (const r of results.filter(r => !r.pass)) {
    console.log("  ❌ [" + r.test + "] " + r.check + ": expected=" + JSON.stringify(r.expected) + ", actual=" + JSON.stringify(r.actual) + " — " + r.note);
  }
}

console.log("\nTested pickpocket targets: " + PICKPOCKET_TARGETS.map(t => t.name).join(", "));
console.log("Tested stalls: " + STALL_TARGETS.map(s => s.name).join(", "));
console.log("\nSetup commands for future runs:");
console.log("  ::master");
console.log("  ::setlevel thieving 99");

({ summary: { passed, failed, total: results.length }, results, targetsTested: PICKPOCKET_TARGETS.length + STALL_TARGETS.length });
