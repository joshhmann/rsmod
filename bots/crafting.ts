/**
 * bots/crafting.ts — Crafting skill test
 *
 * Run via MCP execute_script tool. Globals available: bot, sdk
 *
 * Tests the crafting skill:
 *  - Spinning wheel (wool → ball of wool)
 *  - Leather working (leather → items with needle/thread)
 *  - Gem cutting (uncut → cut gems with chisel)
 *  - Pottery (soft clay → unfired → fired pots/bowls)
 *
 * Assumes player has materials in inventory.
 * Use ::master and ::invadd commands in-game chat to set up.
 */

// ---------------------------------------------------------------------------
// Cache-verified data (mcp-osrs rev 233)
// Item IDs
// Tools: needle=1733, thread=1734, chisel=1755
// Spinning: wool=1737, ball_of_wool=1759
// Leather: leather=1741, leather_gloves=1059, leather_boots=1061, leather_vambraces=1063
//          leather_chaps=1095, leather_armour=1129
// Gems (uncut): uncut_opal=1625, uncut_jade=1627, uncut_sapphire=1623, uncut_emerald=1621
//               uncut_ruby=1619, uncut_diamond=1617
// Gems (cut): opal=1609, jade=1611, sapphire=1607, emerald=1605, ruby=1603, diamond=1601
// Pottery: softclay=1761, pot_unfired=1787, bowl_unfired=1791, pot_empty=1931, bowl_empty=1923
// Loc IDs: spinningwheel=14889, potterywheel=14887, potteryoven=14888
// Seq IDs: human_spinningwheel=894, human_dragonstonecutting=885, human_potterywheel=883, potteryoven_quick=1317
// ---------------------------------------------------------------------------

const NEEDLE_ID = 1733;
const THREAD_ID = 1734;
const CHISEL_ID = 1755;

const SPINNING_ANIM = 894;
const GEM_CUTTING_ANIM = 885;
const POTTERY_WHEEL_ANIM = 883;
const POTTERY_OVEN_ANIM = 1317;

// Spinning data
const SPINNING_TIERS = [
  {
    name: "Ball of wool",
    levelReq: 1,
    wikiXp: 2.5,
    inputId: 1737, // wool
    outputId: 1759, // ball_of_wool
  },
];

// Leatherworking data (F2P items only)
const LEATHER_TIERS = [
  {
    name: "Leather gloves",
    levelReq: 1,
    wikiXp: 13.8,
    leatherId: 1741,
    productId: 1059,
  },
  {
    name: "Leather boots",
    levelReq: 7,
    wikiXp: 16.25,
    leatherId: 1741,
    productId: 1061,
  },
  {
    name: "Leather vambraces",
    levelReq: 11,
    wikiXp: 22.0,
    leatherId: 1741,
    productId: 1063,
  },
  {
    name: "Leather chaps",
    levelReq: 18,
    wikiXp: 27.0,
    leatherId: 1741,
    productId: 1095,
  },
];

// Gem cutting data
const GEM_TIERS = [
  {
    name: "Opal",
    levelReq: 1,
    wikiXp: 15.0,
    uncutId: 1625,
    cutId: 1609,
    canCrush: true,
  },
  {
    name: "Jade",
    levelReq: 13,
    wikiXp: 20.0,
    uncutId: 1627,
    cutId: 1611,
    canCrush: true,
  },
  {
    name: "Sapphire",
    levelReq: 20,
    wikiXp: 50.0,
    uncutId: 1623,
    cutId: 1607,
    canCrush: false,
  },
  {
    name: "Emerald",
    levelReq: 27,
    wikiXp: 67.5,
    uncutId: 1621,
    cutId: 1605,
    canCrush: false,
  },
  {
    name: "Ruby",
    levelReq: 34,
    wikiXp: 85.0,
    uncutId: 1619,
    cutId: 1603,
    canCrush: false,
  },
  {
    name: "Diamond",
    levelReq: 43,
    wikiXp: 107.5,
    uncutId: 1617,
    cutId: 1601,
    canCrush: false,
  },
];

// Pottery data
const POTTERY_TIERS = [
  {
    name: "Pot",
    levelReq: 1,
    wheelXp: 6.3,
    fireXp: 6.3,
    softClayId: 1761,
    unfiredId: 1787,
    firedId: 1931,
  },
  {
    name: "Bowl",
    levelReq: 8,
    wheelXp: 15.0,
    fireXp: 15.0,
    softClayId: 1761,
    unfiredId: 1791,
    firedId: 1923,
  },
];

// Test locations
const TEST_LOCATIONS = {
  lumbridgeSpinning: { x: 3208, z: 3213, desc: "Lumbridge Castle Spinning Wheel" },
  lumbridgePottery: { x: 3228, z: 3256, desc: "Lumbridge Pottery Wheel/Oven" },
  faladorSpinning: { x: 3012, z: 3357, desc: "Falador Spinning Wheel" },
};

// Loc IDs
const SPINNING_WHEEL_LOC_ID = 14889;
const POTTERY_WHEEL_LOC_ID = 14887;
const POTTERY_OVEN_LOC_ID = 14888;

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

// Helper: Check if player has item
function hasItem(itemId) {
  return countItems(sdk.getInventory(), itemId) > 0;
}

// ---------------------------------------------------------------------------
// Test 1: Spinning Wheel (wool → ball of wool)
// ---------------------------------------------------------------------------

console.log("\n═══ Test 1: Spinning Wheel (Wool → Ball of Wool) ═══");

// Teleport to Lumbridge spinning wheel
sdk.sendTeleport(TEST_LOCATIONS.lumbridgeSpinning.x, TEST_LOCATIONS.lumbridgeSpinning.z, 0);
await sdk.waitTicks(3);

// Check for materials
const hasWool = hasItem(1737);
record("Spinning Setup", "has_wool", hasWool, true, hasWool, hasWool ? "Wool ready" : "No wool! Use ::invadd wool 10");

async function spinWool() {
  const xpBefore = sdk.getSkill("crafting").xp;
  const woolBefore = countItems(sdk.getInventory(), 1737);
  const ballBefore = countItems(sdk.getInventory(), 1759);

  // Use wool on spinning wheel
  sdk.sendAction({
    type: "interact_loc_u",
    id: SPINNING_WHEEL_LOC_ID,
    x: TEST_LOCATIONS.lumbridgeSpinning.x,
    z: TEST_LOCATIONS.lumbridgeSpinning.z,
    item_id: 1737
  });

  let animSeen = 0;
  let xpGained = 0;
  let elapsed = 0;

  while (elapsed < 10000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;

    if (p.animation !== 0 && p.animation !== 65535 && animSeen === 0) {
      animSeen = p.animation;
    }

    const xpDelta = p.skills.crafting.xp - xpBefore;
    if (xpDelta > 0) {
      xpGained = xpDelta;
      break;
    }
  }

  await sdk.waitTicks(2);

  const inv = sdk.getInventory();
  const woolAfter = countItems(inv, 1737);
  const ballAfter = countItems(inv, 1759);

  return {
    animSeen,
    xpGained,
    woolConsumed: woolBefore - woolAfter,
    ballProduced: ballAfter - ballBefore,
  };
}

if (hasWool) {
  const spinResult = await spinWool();

  // Animation check
  const animPass = spinResult.animSeen === SPINNING_ANIM;
  record("Spinning", "animation", animPass, SPINNING_ANIM, spinResult.animSeen,
    animPass ? "Spinning animation played" : (spinResult.animSeen === 0 ? "No anim seen" : "Unexpected anim " + spinResult.animSeen));

  // XP check
  const xpPass = spinResult.xpGained > 0;
  const effectiveRate = spinResult.xpGained > 0 ? (spinResult.xpGained / 2.5).toFixed(1) + "x" : "n/a";
  record("Spinning", "xp_grant", xpPass, ">0 (wiki=2.5)", spinResult.xpGained,
    xpPass ? "XP granted, effective rate: " + effectiveRate : "No XP gained");

  // Item transformation check
  record("Spinning", "wool_consumed", spinResult.woolConsumed > 0, ">=1", spinResult.woolConsumed,
    spinResult.woolConsumed > 0 ? "Wool consumed" : "Wool not consumed");
  record("Spinning", "ball_produced", spinResult.ballProduced > 0, ">=1", spinResult.ballProduced,
    spinResult.ballProduced > 0 ? "Ball of wool produced" : "No ball of wool produced");
} else {
  console.log("  Skipping spinning test - no wool in inventory");
}

// ---------------------------------------------------------------------------
// Test 2: Leather Working (leather → items with needle/thread)
// ---------------------------------------------------------------------------

console.log("\n═══ Test 2: Leather Working ═══");

// Check for materials
const hasNeedle = hasItem(NEEDLE_ID);
const hasThread = hasItem(THREAD_ID);
const hasLeather = hasItem(1741);

record("Leather Setup", "has_needle", hasNeedle, true, hasNeedle, hasNeedle ? "Needle ready" : "No needle! Use ::invadd needle 1");
record("Leather Setup", "has_thread", hasThread, true, hasThread, hasThread ? "Thread ready" : "No thread! Use ::invadd thread 10");
record("Leather Setup", "has_leather", hasLeather, true, hasLeather, hasLeather ? "Leather ready" : "No leather! Use ::invadd leather 10");

async function craftLeatherItem(item) {
  const xpBefore = sdk.getSkill("crafting").xp;
  const leatherBefore = countItems(sdk.getInventory(), item.leatherId);
  const threadBefore = countItems(sdk.getInventory(), THREAD_ID);
  const productBefore = countItems(sdk.getInventory(), item.productId);

  // Use needle on leather (held item use)
  sdk.sendAction({
    type: "interact_held_u",
    item_id: NEEDLE_ID,
    target_item_id: item.leatherId
  });

  let animSeen = 0;
  let xpGained = 0;
  let elapsed = 0;

  while (elapsed < 10000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;

    if (p.animation !== 0 && p.animation !== 65535 && animSeen === 0) {
      animSeen = p.animation;
    }

    const xpDelta = p.skills.crafting.xp - xpBefore;
    if (xpDelta > 0) {
      xpGained = xpDelta;
      break;
    }
  }

  await sdk.waitTicks(2);

  const inv = sdk.getInventory();
  const leatherAfter = countItems(inv, item.leatherId);
  const threadAfter = countItems(inv, THREAD_ID);
  const productAfter = countItems(inv, item.productId);

  return {
    animSeen,
    xpGained,
    leatherConsumed: leatherBefore - leatherAfter,
    threadConsumed: threadBefore - threadAfter,
    productProduced: productAfter - productBefore,
  };
}

if (hasNeedle && hasThread && hasLeather) {
  const currentLevel = sdk.getSkill("crafting").level;

  for (const item of LEATHER_TIERS) {
    // Check if we have enough materials
    const leatherCount = countItems(sdk.getInventory(), 1741);
    const threadCount = countItems(sdk.getInventory(), THREAD_ID);

    if (leatherCount < 1 || threadCount < 1) {
      console.log("  Out of materials for " + item.name);
      break;
    }

    // Check level requirement
    if (currentLevel < item.levelReq) {
      console.log("  Skipping " + item.name + " - requires level " + item.levelReq + ", have " + currentLevel);
      continue;
    }

    console.log("\n── Testing: " + item.name + " (req lv" + item.levelReq + ", " + item.wikiXp + " XP) ──");

    const result = await craftLeatherItem(item);

    // Animation check (leather crafting uses anim 1249)
    const animPass = result.animSeen !== 0 && result.animSeen !== 65535;
    record(item.name, "animation", animPass, "valid anim", result.animSeen,
      animPass ? "Leather crafting animation played (anim=" + result.animSeen + ")" : "No animation");

    // XP check
    const xpPass = result.xpGained > 0;
    record(item.name, "xp_grant", xpPass, ">0 (wiki=" + item.wikiXp + ")", result.xpGained,
      xpPass ? "XP granted" : "No XP gained");

    // Materials consumed
    record(item.name, "leather_consumed", result.leatherConsumed > 0, ">=1", result.leatherConsumed,
      result.leatherConsumed > 0 ? "Leather consumed" : "Leather not consumed");
    record(item.name, "thread_consumed", result.threadConsumed > 0, ">=1", result.threadConsumed,
      result.threadConsumed > 0 ? "Thread consumed" : "Thread not consumed");

    // Product produced
    record(item.name, "product_produced", result.productProduced > 0, ">=1", result.productProduced,
      result.productProduced > 0 ? item.name + " produced" : "No product produced");

    await sdk.waitTicks(1);
  }
} else {
  console.log("  Skipping leather working test - missing materials");
}

// ---------------------------------------------------------------------------
// Test 3: Gem Cutting (uncut → cut with chisel)
// ---------------------------------------------------------------------------

console.log("\n═══ Test 3: Gem Cutting ═══");

// Check for chisel
const hasChisel = hasItem(CHISEL_ID);
record("Gem Setup", "has_chisel", hasChisel, true, hasChisel, hasChisel ? "Chisel ready" : "No chisel! Use ::invadd chisel 1");

async function cutGem(gem) {
  const xpBefore = sdk.getSkill("crafting").xp;
  const uncutBefore = countItems(sdk.getInventory(), gem.uncutId);
  const cutBefore = countItems(sdk.getInventory(), gem.cutId);

  // Use chisel on uncut gem (held item use)
  sdk.sendAction({
    type: "interact_held_u",
    item_id: CHISEL_ID,
    target_item_id: gem.uncutId
  });

  let animSeen = 0;
  let xpGained = 0;
  let elapsed = 0;

  while (elapsed < 10000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;

    if (p.animation !== 0 && p.animation !== 65535 && animSeen === 0) {
      animSeen = p.animation;
    }

    const xpDelta = p.skills.crafting.xp - xpBefore;
    if (xpDelta > 0) {
      xpGained = xpDelta;
      break;
    }
  }

  await sdk.waitTicks(2);

  const inv = sdk.getInventory();
  const uncutAfter = countItems(inv, gem.uncutId);
  const cutAfter = countItems(inv, gem.cutId);

  return {
    animSeen,
    xpGained,
    uncutConsumed: uncutBefore - uncutAfter,
    cutProduced: cutAfter - cutBefore,
  };
}

if (hasChisel) {
  const currentLevel = sdk.getSkill("crafting").level;

  for (const gem of GEM_TIERS) {
    // Check if we have the uncut gem
    const uncutCount = countItems(sdk.getInventory(), gem.uncutId);
    if (uncutCount < 1) {
      console.log("  No uncut " + gem.name + " in inventory, skipping");
      continue;
    }

    // Check level requirement
    if (currentLevel < gem.levelReq) {
      console.log("  Skipping " + gem.name + " - requires level " + gem.levelReq + ", have " + currentLevel);
      continue;
    }

    console.log("\n── Testing: Cut " + gem.name + " (req lv" + gem.levelReq + ", " + gem.wikiXp + " XP) ──");

    const result = await cutGem(gem);

    // Animation check
    const animPass = result.animSeen === GEM_CUTTING_ANIM;
    record(gem.name, "animation", animPass, GEM_CUTTING_ANIM, result.animSeen,
      animPass ? "Gem cutting animation played" : (result.animSeen === 0 ? "No anim seen" : "Unexpected anim " + result.animSeen));

    // XP check
    const xpPass = result.xpGained > 0;
    record(gem.name, "xp_grant", xpPass, ">0 (wiki=" + gem.wikiXp + ")", result.xpGained,
      xpPass ? "XP granted" : "No XP gained (gem may have been crushed)");

    // Item transformation
    record(gem.name, "uncut_consumed", result.uncutConsumed > 0, ">=1", result.uncutConsumed,
      result.uncutConsumed > 0 ? "Uncut gem consumed" : "Uncut gem not consumed");
    record(gem.name, "cut_produced", result.cutProduced > 0, ">=1", result.cutProduced,
      result.cutProduced > 0 ? "Cut gem produced" : "No cut gem produced (may have been crushed)");

    await sdk.waitTicks(1);
  }
} else {
  console.log("  Skipping gem cutting test - no chisel in inventory");
}

// ---------------------------------------------------------------------------
// Test 4: Pottery (soft clay → unfired → fired)
// ---------------------------------------------------------------------------

console.log("\n═══ Test 4: Pottery (Soft Clay → Unfired → Fired) ═══");

// Teleport to Lumbridge pottery area
sdk.sendTeleport(TEST_LOCATIONS.lumbridgePottery.x, TEST_LOCATIONS.lumbridgePottery.z, 0);
await sdk.waitTicks(3);

// Check for soft clay
const hasSoftClay = hasItem(1761);
record("Pottery Setup", "has_soft_clay", hasSoftClay, true, hasSoftClay, hasSoftClay ? "Soft clay ready" : "No soft clay! Use ::invadd softclay 10");

async function usePotteryWheel(item) {
  const xpBefore = sdk.getSkill("crafting").xp;
  const clayBefore = countItems(sdk.getInventory(), item.softClayId);
  const unfiredBefore = countItems(sdk.getInventory(), item.unfiredId);

  // Use soft clay on pottery wheel
  sdk.sendAction({
    type: "interact_loc_u",
    id: POTTERY_WHEEL_LOC_ID,
    x: TEST_LOCATIONS.lumbridgePottery.x,
    z: TEST_LOCATIONS.lumbridgePottery.z,
    item_id: item.softClayId
  });

  let animSeen = 0;
  let xpGained = 0;
  let elapsed = 0;

  while (elapsed < 10000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;

    if (p.animation !== 0 && p.animation !== 65535 && animSeen === 0) {
      animSeen = p.animation;
    }

    const xpDelta = p.skills.crafting.xp - xpBefore;
    if (xpDelta > 0) {
      xpGained = xpDelta;
      break;
    }
  }

  await sdk.waitTicks(2);

  const inv = sdk.getInventory();
  const clayAfter = countItems(inv, item.softClayId);
  const unfiredAfter = countItems(inv, item.unfiredId);

  return {
    animSeen,
    xpGained,
    clayConsumed: clayBefore - clayAfter,
    unfiredProduced: unfiredAfter - unfiredBefore,
  };
}

async function firePottery(item) {
  const xpBefore = sdk.getSkill("crafting").xp;
  const unfiredBefore = countItems(sdk.getInventory(), item.unfiredId);
  const firedBefore = countItems(sdk.getInventory(), item.firedId);

  // Use unfired item on pottery oven
  sdk.sendAction({
    type: "interact_loc_u",
    id: POTTERY_OVEN_LOC_ID,
    x: TEST_LOCATIONS.lumbridgePottery.x,
    z: TEST_LOCATIONS.lumbridgePottery.z,
    item_id: item.unfiredId
  });

  let animSeen = 0;
  let xpGained = 0;
  let elapsed = 0;

  while (elapsed < 10000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;

    if (p.animation !== 0 && p.animation !== 65535 && animSeen === 0) {
      animSeen = p.animation;
    }

    const xpDelta = p.skills.crafting.xp - xpBefore;
    if (xpDelta > 0) {
      xpGained = xpDelta;
      break;
    }
  }

  await sdk.waitTicks(2);

  const inv = sdk.getInventory();
  const unfiredAfter = countItems(inv, item.unfiredId);
  const firedAfter = countItems(inv, item.firedId);

  return {
    animSeen,
    xpGained,
    unfiredConsumed: unfiredBefore - unfiredAfter,
    firedProduced: firedAfter - firedBefore,
  };
}

if (hasSoftClay) {
  const currentLevel = sdk.getSkill("crafting").level;

  for (const item of POTTERY_TIERS) {
    // Check level requirement
    if (currentLevel < item.levelReq) {
      console.log("  Skipping " + item.name + " - requires level " + item.levelReq + ", have " + currentLevel);
      continue;
    }

    console.log("\n── Testing: " + item.name + " (wheel xp=" + item.wheelXp + ", fire xp=" + item.fireXp + ") ──");

    // Step 1: Use pottery wheel
    const softClayCount = countItems(sdk.getInventory(), 1761);
    if (softClayCount < 1) {
      console.log("  Out of soft clay");
      break;
    }

    console.log("  Step 1: Using pottery wheel...");
    const wheelResult = await usePotteryWheel(item);

    // Wheel animation check
    const wheelAnimPass = wheelResult.animSeen === POTTERY_WHEEL_ANIM;
    record(item.name + " Wheel", "animation", wheelAnimPass, POTTERY_WHEEL_ANIM, wheelResult.animSeen,
      wheelAnimPass ? "Pottery wheel animation played" : (wheelResult.animSeen === 0 ? "No anim seen" : "Unexpected anim " + wheelResult.animSeen));

    // Wheel XP check
    const wheelXpPass = wheelResult.xpGained > 0;
    record(item.name + " Wheel", "xp_grant", wheelXpPass, ">0 (wiki=" + item.wheelXp + ")", wheelResult.xpGained,
      wheelXpPass ? "Wheel XP granted" : "No XP gained");

    // Unfired produced
    record(item.name + " Wheel", "unfired_produced", wheelResult.unfiredProduced > 0, ">=1", wheelResult.unfiredProduced,
      wheelResult.unfiredProduced > 0 ? "Unfired " + item.name + " produced" : "No unfired item produced");

    await sdk.waitTicks(1);

    // Step 2: Fire pottery
    const unfiredCount = countItems(sdk.getInventory(), item.unfiredId);
    if (unfiredCount < 1) {
      console.log("  No unfired " + item.name + " to fire");
      continue;
    }

    console.log("  Step 2: Firing in pottery oven...");
    const fireResult = await firePottery(item);

    // Fire animation check
    const fireAnimPass = fireResult.animSeen === POTTERY_OVEN_ANIM;
    record(item.name + " Fire", "animation", fireAnimPass, POTTERY_OVEN_ANIM, fireResult.animSeen,
      fireAnimPass ? "Pottery oven animation played" : (fireResult.animSeen === 0 ? "No anim seen" : "Unexpected anim " + fireResult.animSeen));

    // Fire XP check
    const fireXpPass = fireResult.xpGained > 0;
    record(item.name + " Fire", "xp_grant", fireXpPass, ">0 (wiki=" + item.fireXp + ")", fireResult.xpGained,
      fireXpPass ? "Fire XP granted" : "No XP gained");

    // Fired produced
    record(item.name + " Fire", "fired_produced", fireResult.firedProduced > 0, ">=1", fireResult.firedProduced,
      fireResult.firedProduced > 0 ? "Fired " + item.name + " produced" : "No fired item produced");

    await sdk.waitTicks(1);
  }
} else {
  console.log("  Skipping pottery test - no soft clay in inventory");
}

// ---------------------------------------------------------------------------
// Summary
// ---------------------------------------------------------------------------

const passed = results.filter(r => r.pass).length;
const failed = results.filter(r => !r.pass).length;

console.log("\n═══════════════════════════════════════════════════");
console.log("══ Crafting Test Summary: " + passed + " passed, " + failed + " failed ══");
console.log("═══════════════════════════════════════════════════");

if (failed > 0) {
  console.log("\nFAILURES:");
  for (const r of results.filter(r => !r.pass)) {
    console.log("  ❌ [" + r.test + "] " + r.check + ": expected=" + JSON.stringify(r.expected) + ", actual=" + JSON.stringify(r.actual) + " — " + r.note);
  }
}

console.log("\nTested crafting activities:");
console.log("  - Spinning: Wool → Ball of wool");
console.log("  - Leather: Leather → Gloves, Boots, Vambraces, Chaps");
console.log("  - Gem Cutting: Opal, Jade, Sapphire, Emerald, Ruby, Diamond");
console.log("  - Pottery: Soft clay → Unfired → Fired (Pots, Bowls)");

console.log("\nSetup commands for future runs:");
console.log("  ::master");
console.log("  ::invadd wool 10");
console.log("  ::invadd needle 1");
console.log("  ::invadd thread 20");
console.log("  ::invadd leather 10");
console.log("  ::invadd chisel 1");
console.log("  ::invadd uncut_opal 5");
console.log("  ::invadd uncut_sapphire 5");
console.log("  ::invadd uncut_emerald 5");
console.log("  ::invadd softclay 10");

({ summary: { passed, failed, total: results.length }, results });
