/**
 * bots/herblore.ts — Herblore skill test
 *
 * Run via MCP execute_script tool. Globals available: bot, sdk
 *
 * Tests the herblore skill:
 *  - Herb cleaning (grimy → clean)
 *  - Unfinished potions (clean herb + vial of water)
 *  - Finished potions (unfinished + secondary)
 *
 * Assumes player has herbs and ingredients in inventory.
 * Use ::master and ::invadd commands in-game chat to set up.
 */

// ---------------------------------------------------------------------------
// Cache-verified data (mcp-osrs rev 233)
// Item IDs
// Grimy herbs: guam=199, marrentill=201, tarromin=203, harralander=205
// Clean herbs: guam_leaf=249, marrentill=251, tarromin=253, harralander=255
// Vials: vial_empty=229, guamvial=91 (unfinished), marrentillvial=93, tarrominvial=95, harralandervial=97
// Secondaries: eye_of_newt=221
// Finished potions: 3dose1attack=121, 3dose1strength=115
// Seq IDs: human_herbing_vial=363, human_herbing_grind=364
// ---------------------------------------------------------------------------

const HERBING_ANIM = 363; // human_herbing_vial
const GRIND_ANIM = 364;   // human_herbing_grind

// Herb cleaning data (grimy ID, clean ID, level req, XP)
const HERBS = [
  {
    name: "Guam",
    levelReq: 3,
    wikiXp: 2.5,
    grimyId: 199,      // unidentified_guam
    cleanId: 249,      // guam_leaf
  },
  {
    name: "Marrentill",
    levelReq: 5,
    wikiXp: 3.8,
    grimyId: 201,      // unidentified_marrentill
    cleanId: 251,      // marrentill
  },
  {
    name: "Tarromin",
    levelReq: 11,
    wikiXp: 5.0,
    grimyId: 203,      // unidentified_tarromin
    cleanId: 253,      // tarromin
  },
  {
    name: "Harralander",
    levelReq: 20,
    wikiXp: 6.3,
    grimyId: 205,      // unidentified_harralander
    cleanId: 255,      // harralander
  },
];

// Unfinished potion data (clean herb ID, unfinished vial ID, level req, XP)
const UNFINISHED_POTIONS = [
  {
    name: "Guam",
    levelReq: 3,
    wikiXp: 0.0,  // No XP for unfinished
    cleanId: 249,     // guam_leaf
    vialId: 91,       // guamvial
  },
  {
    name: "Marrentill",
    levelReq: 5,
    wikiXp: 0.0,
    cleanId: 251,     // marrentill
    vialId: 93,       // marrentillvial
  },
  {
    name: "Tarromin",
    levelReq: 12,
    wikiXp: 0.0,
    cleanId: 253,     // tarromin
    vialId: 95,       // tarrominvial
  },
  {
    name: "Harralander",
    levelReq: 22,
    wikiXp: 0.0,
    cleanId: 255,     // harralander
    vialId: 97,       // harralandervial
  },
];

// Finished potion data (unfinished vial ID, secondary ID, finished ID, level req, XP)
const FINISHED_POTIONS = [
  {
    name: "Attack potion",
    levelReq: 3,
    wikiXp: 25.0,
    unfinishedId: 91,   // guamvial
    secondaryId: 221,   // eye_of_newt
    finishedId: 121,    // 3dose1attack
  },
  {
    name: "Strength potion",
    levelReq: 12,
    wikiXp: 50.0,
    unfinishedId: 95,   // tarrominvial
    secondaryId: 221,   // eye_of_newt (simplified - actually uses limpwurt root)
    finishedId: 115,    // 3dose1strength
  },
];

// Test locations
const TEST_LOCATIONS = {
  bank: { x: 3209, z: 3215, desc: "Lumbridge Castle Bank" },
  shop: { x: 3211, z: 3246, desc: "Lumbridge General Store" },
};

// Item IDs
const VIAL_EMPTY = 229;
const VIAL_OF_WATER = 227;  // vial_water

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

// Helper: Clean a herb and return results
async function cleanHerb(herb) {
  const xpBefore = sdk.getSkill("herblore").xp;
  const grimyBefore = countItems(sdk.getInventory(), herb.grimyId);
  const cleanBefore = countItems(sdk.getInventory(), herb.cleanId);

  // Use grimy herb (clean it)
  sdk.sendAction({
    type: "interact_item",
    item_id: herb.grimyId,
    option: 1  // Clean option
  });

  let animSeen = 0;
  let xpGained = 0;
  let elapsed = 0;

  while (elapsed < 5000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;
    
    if (p.animation !== 0 && p.animation !== 65535 && animSeen === 0) {
      animSeen = p.animation;
    }
    
    const xpDelta = p.skills.herblore.xp - xpBefore;
    if (xpDelta > 0) {
      xpGained = xpDelta;
      break;
    }
  }

  await sdk.waitTicks(2);
  
  const inv = sdk.getInventory();
  const grimyAfter = countItems(inv, herb.grimyId);
  const cleanAfter = countItems(inv, herb.cleanId);

  return {
    animSeen,
    xpGained,
    grimyConsumed: grimyBefore - grimyAfter,
    cleanProduced: cleanAfter - cleanBefore,
  };
}

// Helper: Make unfinished potion
async function makeUnfinishedPotion(herb) {
  const xpBefore = sdk.getSkill("herblore").xp;
  const cleanBefore = countItems(sdk.getInventory(), herb.cleanId);
  const vialBefore = countItems(sdk.getInventory(), VIAL_OF_WATER);
  const unfinishedBefore = countItems(sdk.getInventory(), herb.vialId);

  // Use clean herb on vial of water
  sdk.sendAction({
    type: "interact_item_u",
    item_id: herb.cleanId,
    target_item_id: VIAL_OF_WATER
  });

  let animSeen = 0;
  let xpGained = 0;
  let elapsed = 0;

  while (elapsed < 5000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;
    
    if (p.animation !== 0 && p.animation !== 65535 && animSeen === 0) {
      animSeen = p.animation;
    }
    
    const xpDelta = p.skills.herblore.xp - xpBefore;
    if (xpDelta > 0 || elapsed > 3000) {
      xpGained = xpDelta;
      break;
    }
  }

  await sdk.waitTicks(2);
  
  const inv = sdk.getInventory();
  const cleanAfter = countItems(inv, herb.cleanId);
  const vialAfter = countItems(inv, VIAL_OF_WATER);
  const unfinishedAfter = countItems(inv, herb.vialId);

  return {
    animSeen,
    xpGained,
    cleanConsumed: cleanBefore - cleanAfter,
    vialConsumed: vialBefore - vialAfter,
    unfinishedProduced: unfinishedAfter - unfinishedBefore,
  };
}

// Helper: Make finished potion
async function makeFinishedPotion(potion) {
  const xpBefore = sdk.getSkill("herblore").xp;
  const unfinishedBefore = countItems(sdk.getInventory(), potion.unfinishedId);
  const secondaryBefore = countItems(sdk.getInventory(), potion.secondaryId);
  const finishedBefore = countItems(sdk.getInventory(), potion.finishedId);

  // Use secondary on unfinished potion
  sdk.sendAction({
    type: "interact_item_u",
    item_id: potion.secondaryId,
    target_item_id: potion.unfinishedId
  });

  let animSeen = 0;
  let xpGained = 0;
  let elapsed = 0;

  while (elapsed < 5000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;
    
    if (p.animation !== 0 && p.animation !== 65535 && animSeen === 0) {
      animSeen = p.animation;
    }
    
    const xpDelta = p.skills.herblore.xp - xpBefore;
    if (xpDelta > 0 || elapsed > 3000) {
      xpGained = xpDelta;
      break;
    }
  }

  await sdk.waitTicks(2);
  
  const inv = sdk.getInventory();
  const unfinishedAfter = countItems(inv, potion.unfinishedId);
  const secondaryAfter = countItems(inv, potion.secondaryId);
  const finishedAfter = countItems(inv, potion.finishedId);

  return {
    animSeen,
    xpGained,
    unfinishedConsumed: unfinishedBefore - unfinishedAfter,
    secondaryConsumed: secondaryBefore - secondaryAfter,
    finishedProduced: finishedAfter - finishedBefore,
  };
}

// ---------------------------------------------------------------------------
// Test 1: Herb Cleaning
// ---------------------------------------------------------------------------

console.log("\n═══ Test 1: Herb Cleaning ═══");

// Teleport to safe location
sdk.sendTeleport(TEST_LOCATIONS.bank.x, TEST_LOCATIONS.bank.z, 0);
await sdk.waitTicks(3);

const currentLevel = sdk.getSkill("herblore").level;
console.log("Current Herblore Level: " + currentLevel);
record("Level Check", "herblore_level", true, ">=1", currentLevel, "Player herblore level");

for (const herb of HERBS) {
  console.log("\n── Testing: " + herb.name + " (req lv" + herb.levelReq + ", " + herb.wikiXp + " XP) ──");
  
  if (currentLevel < herb.levelReq) {
    console.log("  Skipping " + herb.name + " - requires level " + herb.levelReq + ", have " + currentLevel);
    record(herb.name, "level_requirement", false, ">=" + herb.levelReq, currentLevel, "Level too low");
    continue;
  }
  
  // Check if we have grimy herb
  const grimyCount = countItems(sdk.getInventory(), herb.grimyId);
  if (grimyCount < 1) {
    record(herb.name, "has_grimy_herb", false, ">=1", grimyCount, "No grimy " + herb.name + " in inventory. Use ::invadd unidentified_" + herb.name.toLowerCase() + " 10");
    continue;
  }
  record(herb.name, "has_grimy_herb", true, ">=1", grimyCount, "Ready to clean");

  const result = await cleanHerb(herb);

  // XP check
  const xpPass = result.xpGained > 0;
  record(herb.name, "xp_grant", xpPass, ">0 (wiki=" + herb.wikiXp + ")", result.xpGained,
    xpPass ? "XP granted" : "No XP gained");

  // Item transformation check
  if (result.cleanProduced > 0) {
    record(herb.name, "clean_produced", true, "+1 (id=" + herb.cleanId + ")", "+" + result.cleanProduced,
      "Successfully cleaned " + herb.name);
  } else {
    record(herb.name, "clean_produced", false, "+1", "+" + result.cleanProduced,
      "No clean herb produced");
  }

  // Grimy herb consumed check
  record(herb.name, "grimy_consumed", result.grimyConsumed > 0, ">=1", result.grimyConsumed,
    result.grimyConsumed > 0 ? "Grimy herb used" : "Grimy herb not consumed");
}

// ---------------------------------------------------------------------------
// Test 2: Unfinished Potions
// ---------------------------------------------------------------------------

console.log("\n═══ Test 2: Unfinished Potions ═══");

for (const herb of UNFINISHED_POTIONS) {
  console.log("\n── Testing: " + herb.name + " unf potion (req lv" + herb.levelReq + ") ──");
  
  if (currentLevel < herb.levelReq) {
    console.log("  Skipping " + herb.name + " - requires level " + herb.levelReq + ", have " + currentLevel);
    record(herb.name + " Unf", "level_requirement", false, ">=" + herb.levelReq, currentLevel, "Level too low");
    continue;
  }
  
  // Check if we have clean herb and vial of water
  const cleanCount = countItems(sdk.getInventory(), herb.cleanId);
  const vialCount = countItems(sdk.getInventory(), VIAL_OF_WATER);
  
  if (cleanCount < 1) {
    record(herb.name + " Unf", "has_clean_herb", false, ">=1", cleanCount, "No clean " + herb.name + " in inventory");
    continue;
  }
  if (vialCount < 1) {
    record(herb.name + " Unf", "has_vial_water", false, ">=1", vialCount, "No vial of water in inventory. Use ::invadd vial_water 10");
    continue;
  }
  
  record(herb.name + " Unf", "has_materials", true, "clean+water", cleanCount + "+" + vialCount, "Ready to mix");

  const result = await makeUnfinishedPotion(herb);

  // Item transformation check
  if (result.unfinishedProduced > 0) {
    record(herb.name + " Unf", "unfinished_produced", true, "+1 (id=" + herb.vialId + ")", "+" + result.unfinishedProduced,
      "Successfully made " + herb.name + " unfinished potion");
  } else {
    record(herb.name + " Unf", "unfinished_produced", false, "+1", "+" + result.unfinishedProduced,
      "No unfinished potion produced");
  }

  // Materials consumed check
  record(herb.name + " Unf", "clean_consumed", result.cleanConsumed > 0, ">=1", result.cleanConsumed,
    result.cleanConsumed > 0 ? "Clean herb used" : "Clean herb not consumed");
  record(herb.name + " Unf", "vial_consumed", result.vialConsumed > 0, ">=1", result.vialConsumed,
    result.vialConsumed > 0 ? "Vial of water used" : "Vial not consumed");
}

// ---------------------------------------------------------------------------
// Test 3: Finished Potions
// ---------------------------------------------------------------------------

console.log("\n═══ Test 3: Finished Potions ═══");

for (const potion of FINISHED_POTIONS) {
  console.log("\n── Testing: " + potion.name + " (req lv" + potion.levelReq + ", " + potion.wikiXp + " XP) ──");
  
  if (currentLevel < potion.levelReq) {
    console.log("  Skipping " + potion.name + " - requires level " + potion.levelReq + ", have " + currentLevel);
    record(potion.name, "level_requirement", false, ">=" + potion.levelReq, currentLevel, "Level too low");
    continue;
  }
  
  // Check if we have unfinished potion and secondary
  const unfinishedCount = countItems(sdk.getInventory(), potion.unfinishedId);
  const secondaryCount = countItems(sdk.getInventory(), potion.secondaryId);
  
  if (unfinishedCount < 1) {
    record(potion.name, "has_unfinished", false, ">=1", unfinishedCount, "No unfinished potion in inventory");
    continue;
  }
  if (secondaryCount < 1) {
    record(potion.name, "has_secondary", false, ">=1", secondaryCount, "No secondary (eye of newt) in inventory. Use ::invadd eye_of_newt 10");
    continue;
  }
  
  record(potion.name, "has_materials", true, "unf+secondary", unfinishedCount + "+" + secondaryCount, "Ready to mix");

  const result = await makeFinishedPotion(potion);

  // XP check
  const xpPass = result.xpGained > 0;
  record(potion.name, "xp_grant", xpPass, ">0 (wiki=" + potion.wikiXp + ")", result.xpGained,
    xpPass ? "XP granted" : "No XP gained");

  // Item transformation check
  if (result.finishedProduced > 0) {
    record(potion.name, "finished_produced", true, "+1 (id=" + potion.finishedId + ")", "+" + result.finishedProduced,
      "Successfully made " + potion.name);
  } else {
    record(potion.name, "finished_produced", false, "+1", "+" + result.finishedProduced,
      "No finished potion produced");
  }

  // Materials consumed check
  record(potion.name, "unfinished_consumed", result.unfinishedConsumed > 0, ">=1", result.unfinishedConsumed,
    result.unfinishedConsumed > 0 ? "Unfinished potion used" : "Unfinished potion not consumed");
  record(potion.name, "secondary_consumed", result.secondaryConsumed > 0, ">=1", result.secondaryConsumed,
    result.secondaryConsumed > 0 ? "Secondary used" : "Secondary not consumed");
}

// ---------------------------------------------------------------------------
// Summary
// ---------------------------------------------------------------------------

const passed = results.filter(r => r.pass).length;
const failed = results.filter(r => !r.pass).length;

console.log("\n═══════════════════════════════════════════════════");
console.log("══ Herblore Test Summary: " + passed + " passed, " + failed + " failed ══");
console.log("═══════════════════════════════════════════════════");

if (failed > 0) {
  console.log("\nFAILURES:");
  for (const r of results.filter(r => !r.pass)) {
    console.log("  ❌ [" + r.test + "] " + r.check + ": expected=" + JSON.stringify(r.expected) + ", actual=" + JSON.stringify(r.actual) + " — " + r.note);
  }
}

console.log("\nTested herbs: " + HERBS.map(h => h.name).join(", "));
console.log("Setup commands for future runs:");
console.log("  ::master");
console.log("  ::invadd unidentified_guam 10");
console.log("  ::invadd unidentified_marrentill 10");
console.log("  ::invadd unidentified_tarromin 10");
console.log("  ::invadd unidentified_harralander 10");
console.log("  ::invadd vial_water 10");
console.log("  ::invadd eye_of_newt 10");

({ summary: { passed, failed, total: results.length }, results, herbsTested: HERBS.length });
