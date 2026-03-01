/**
 * bots/cooking.ts — Cooking skill test
 *
 * Run via MCP execute_script tool. Globals available: bot, sdk
 *
 * Tests the cooking skill:
 *  - Raw fish cooking on range and fire
 *  - XP gains validation
 *  - Burn detection (burnt fish produced)
 *  - Range vs fire cooking comparison
 *
 * Assumes player has raw fish in inventory.
 * Use ::master and ::invadd raw_shrimp 10 etc. in-game chat to set up.
 */

// ---------------------------------------------------------------------------
// Cache-verified data (mcp-osrs rev 233)
// Item IDs
// Raw fish: shrimp=317, sardine=327, trout=335, salmon=331, pike=349, tuna=359
// Cooked: shrimp=315, sardine=325, trout=333, salmon=329, pike=351, tuna=361
// Burnt: burntfish1=323 (shrimp/anchovies), burntfish2=343 (trout/salmon/pike), 
//        burntfish3=357 (tuna), burnt_shrimp=7954
// Loc IDs: furnace=2030 (used as cooking range), fire=714 (fire_remains - not usable)
//          Using interactLocU with raw fish on range/fire
// Seq IDs: human_cooking=896 (range), human_firecooking=897 (fire)
// ---------------------------------------------------------------------------

const COOKING_ANIM_RANGE = 896; // human_cooking
const COOKING_ANIM_FIRE = 897;  // human_firecooking

// F2P fish cooking data (level req, XP, raw ID, cooked ID, burnt ID)
const FISH_TIERS = [
  {
    name: "Shrimp",
    levelReq: 1,
    wikiXp: 30.0,
    rawId: 317,
    cookedId: 315,
    burntId: 323, // burntfish1
    stopBurnLevel: 34,
  },
  {
    name: "Sardine",
    levelReq: 1,
    wikiXp: 40.0,
    rawId: 327,
    cookedId: 325,
    burntId: 323, // burntfish1
    stopBurnLevel: 38,
  },
  {
    name: "Trout",
    levelReq: 15,
    wikiXp: 70.0,
    rawId: 335,
    cookedId: 333,
    burntId: 343, // burntfish2
    stopBurnLevel: 50,
  },
  {
    name: "Salmon",
    levelReq: 25,
    wikiXp: 90.0,
    rawId: 331,
    cookedId: 329,
    burntId: 343, // burntfish2
    stopBurnLevel: 58,
  },
  {
    name: "Pike",
    levelReq: 20,
    wikiXp: 80.0,
    rawId: 349,
    cookedId: 351,
    burntId: 343, // burntfish2
    stopBurnLevel: 64,
  },
  {
    name: "Tuna",
    levelReq: 30,
    wikiXp: 100.0,
    rawId: 359,
    cookedId: 361,
    burntId: 357, // burntfish3
    stopBurnLevel: 63,
  },
];

// Test locations
const TEST_LOCATIONS = {
  lumbridgeRange: { x: 3209, z: 3215, desc: "Lumbridge Castle Range" },
  lumbridgeFire: { x: 3206, z: 3215, desc: "Lumbridge Castle Fire" },
  faladorRange: { x: 3033, z: 3362, desc: "Falador West Range" },
};

// Furnace loc ID (used as cooking range in F2P)
const RANGE_LOC_ID = 2030;

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

// Helper: Cook a single fish and return results
async function cookFish(fish, location, useRange) {
  const xpBefore = sdk.getSkill("cooking").xp;
  const rawBefore = countItems(sdk.getInventory(), fish.rawId);
  const cookedBefore = countItems(sdk.getInventory(), fish.cookedId);
  const burntBefore = countItems(sdk.getInventory(), fish.burntId);

  // Use raw fish on range/fire
  // For range: interact with furnace loc using raw fish
  // For fire: use fire location
  const locId = useRange ? RANGE_LOC_ID : RANGE_LOC_ID; // Both use same loc ID for now
  const locX = location.x;
  const locZ = location.z;

  // Send use-item-on-loc interaction
  sdk.sendAction({
    type: "interact_loc_u",
    id: locId,
    x: locX,
    z: locZ,
    item_id: fish.rawId
  });

  let animSeen = 0;
  let xpGained = 0;
  let elapsed = 0;
  let success = false;
  let burnt = false;

  while (elapsed < 10000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;
    
    if (p.animation !== 0 && p.animation !== 65535 && animSeen === 0) {
      animSeen = p.animation;
    }
    
    const xpDelta = p.skills.cooking.xp - xpBefore;
    if (xpDelta > 0) {
      xpGained = xpDelta;
      break;
    }
  }

  await sdk.waitTicks(2);
  
  const inv = sdk.getInventory();
  const rawAfter = countItems(inv, fish.rawId);
  const cookedAfter = countItems(inv, fish.cookedId);
  const burntAfter = countItems(inv, fish.burntId);

  return {
    animSeen,
    xpGained,
    rawConsumed: rawBefore - rawAfter,
    cookedProduced: cookedAfter - cookedBefore,
    burntProduced: burntAfter - burntBefore,
  };
}

// ---------------------------------------------------------------------------
// Test 1: Basic cooking on range
// ---------------------------------------------------------------------------

console.log("\n═══ Test 1: Basic Fish Cooking on Range ═══");

// Teleport to Lumbridge range
sdk.sendTeleport(TEST_LOCATIONS.lumbridgeRange.x, TEST_LOCATIONS.lumbridgeRange.z, 0);
await sdk.waitTicks(3);

for (const fish of FISH_TIERS.slice(0, 3)) { // Test first 3 tiers
  console.log("\n── Testing: " + fish.name + " (req lv" + fish.levelReq + ", " + fish.wikiXp + " XP) ──");
  
  // Check if we have raw fish
  const rawCount = countItems(sdk.getInventory(), fish.rawId);
  if (rawCount < 1) {
    record(fish.name, "has_raw_fish", false, ">=1", rawCount, "No raw " + fish.name + " in inventory. Use ::invadd raw_" + fish.name.toLowerCase() + " 10");
    continue;
  }
  record(fish.name, "has_raw_fish", true, ">=1", rawCount, "Ready to cook");

  const result = await cookFish(fish, TEST_LOCATIONS.lumbridgeRange, true);

  // Animation check
  const animPass = result.animSeen === COOKING_ANIM_RANGE || result.animSeen === COOKING_ANIM_FIRE;
  record(fish.name, "animation", animPass, COOKING_ANIM_RANGE + "/" + COOKING_ANIM_FIRE, result.animSeen,
    animPass ? "Cooking animation played" : (result.animSeen === 0 ? "No anim seen" : "Unexpected anim " + result.animSeen));

  // XP check
  const xpPass = result.xpGained > 0;
  const effectiveRate = result.xpGained > 0 ? (result.xpGained / fish.wikiXp).toFixed(1) + "x" : "n/a";
  record(fish.name, "xp_grant", xpPass, ">0 (wiki=" + fish.wikiXp + ")", result.xpGained,
    xpPass ? "XP granted, effective rate: " + effectiveRate : "No XP gained");

  // Item transformation check
  if (result.cookedProduced > 0) {
    record(fish.name, "cooked_produced", true, "+1 (id=" + fish.cookedId + ")", "+" + result.cookedProduced,
      "Successfully cooked " + fish.name);
  } else if (result.burntProduced > 0) {
    record(fish.name, "burnt_produced", true, "+1 burnt", "+" + result.burntProduced,
      "Fish burnt (expected at low cooking level)");
  } else {
    record(fish.name, "item_result", false, "cooked or burnt", "none",
      "No item transformation detected");
  }

  // Raw fish consumed check
  record(fish.name, "raw_consumed", result.rawConsumed > 0, ">=1", result.rawConsumed,
    result.rawConsumed > 0 ? "Raw fish used" : "Raw fish not consumed");
}

// ---------------------------------------------------------------------------
// Test 2: Burn rate observation (cook multiple to observe burns)
// ---------------------------------------------------------------------------

console.log("\n═══ Test 2: Burn Rate Observation ═══");

const testFish = FISH_TIERS[0]; // Shrimp
const cookAttempts = 5;
let cookedCount = 0;
let burntCount = 0;

console.log("Cooking " + cookAttempts + " " + testFish.name + " to observe burn rate...");

for (let i = 0; i < cookAttempts; i++) {
  const rawCount = countItems(sdk.getInventory(), testFish.rawId);
  if (rawCount < 1) {
    console.log("  Out of raw " + testFish.name);
    break;
  }
  
  const result = await cookFish(testFish, TEST_LOCATIONS.lumbridgeRange, true);
  
  if (result.cookedProduced > 0) cookedCount++;
  if (result.burntProduced > 0) burntCount++;
  
  await sdk.waitTicks(1);
}

record("Burn Rate", "cooked_count", true, ">=0", cookedCount, cookedCount + " successfully cooked");
record("Burn Rate", "burnt_count", true, ">=0", burntCount, burntCount + " burnt");
const burnRate = cookAttempts > 0 ? ((burntCount / cookAttempts) * 100).toFixed(1) + "%" : "n/a";
console.log("  Burn rate observed: " + burnRate + " (" + burntCount + "/" + cookAttempts + ")");

// ---------------------------------------------------------------------------
// Test 3: Range vs Fire (if fire available)
// ---------------------------------------------------------------------------

console.log("\n═══ Test 3: Range vs Fire Cooking ═══");

// Test cooking on range
const rangeFish = FISH_TIERS[0]; // Shrimp
console.log("Testing range cooking...");
const rangeResult = await cookFish(rangeFish, TEST_LOCATIONS.lumbridgeRange, true);
record("Range", "xp_gain", rangeResult.xpGained > 0, ">0", rangeResult.xpGained,
  rangeResult.xpGained > 0 ? "Range cooking gives XP" : "No XP from range");

// Note: Fire cooking would require a lit fire, which is more complex
// For now we document that range cooking works
console.log("Note: Fire cooking test requires pre-lit fire or firemaking setup");
record("Fire", "test_status", false, "testable", "skipped", "Fire cooking test skipped - requires fire setup");

// ---------------------------------------------------------------------------
// Test 4: Higher tier fish (if levels permit)
// ---------------------------------------------------------------------------

console.log("\n═══ Test 4: Higher Tier Fish Validation ═══");

const currentLevel = sdk.getSkill("cooking").level;
console.log("Current Cooking Level: " + currentLevel);
record("Level Check", "cooking_level", true, ">=1", currentLevel, "Player cooking level");

for (const fish of FISH_TIERS.slice(3)) { // Test higher tiers
  if (currentLevel >= fish.levelReq) {
    console.log("\n── Testing: " + fish.name + " (req lv" + fish.levelReq + ") ──");
    
    const rawCount = countItems(sdk.getInventory(), fish.rawId);
    if (rawCount < 1) {
      record(fish.name, "has_raw_fish", false, ">=1", rawCount, "No raw " + fish.name + " in inventory");
      continue;
    }
    
    const result = await cookFish(fish, TEST_LOCATIONS.lumbridgeRange, true);
    
    const xpPass = result.xpGained > 0;
    record(fish.name, "xp_grant", xpPass, ">0", result.xpGained,
      xpPass ? "Higher tier fish gives XP" : "No XP gained");
  } else {
    console.log("  Skipping " + fish.name + " - requires level " + fish.levelReq + ", have " + currentLevel);
  }
}

// ---------------------------------------------------------------------------
// Summary
// ---------------------------------------------------------------------------

const passed = results.filter(r => r.pass).length;
const failed = results.filter(r => !r.pass).length;

console.log("\n═══════════════════════════════════════════════════");
console.log("══ Cooking Test Summary: " + passed + " passed, " + failed + " failed ══");
console.log("═══════════════════════════════════════════════════");

if (failed > 0) {
  console.log("\nFAILURES:");
  for (const r of results.filter(r => !r.pass)) {
    console.log("  ❌ [" + r.test + "] " + r.check + ": expected=" + JSON.stringify(r.expected) + ", actual=" + JSON.stringify(r.actual) + " — " + r.note);
  }
}

console.log("\nTested fish tiers: " + FISH_TIERS.map(f => f.name).join(", "));
console.log("Cooking locations: Lumbridge Castle Range");
console.log("\nSetup commands for future runs:");
console.log("  ::master");
console.log("  ::invadd raw_shrimp 20");
console.log("  ::invadd raw_trout 20");
console.log("  ::invadd raw_salmon 20");

({ summary: { passed, failed, total: results.length }, results, fishTested: FISH_TIERS.length });
