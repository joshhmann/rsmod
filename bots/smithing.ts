/**
 * bots/smithing.ts — Smithing skill test
 *
 * Run via MCP execute_script tool. Globals available: bot, sdk
 *
 * Tests the smithing skill:
 *  - Smelting bars at furnace (ore → bar)
 *  - Anvil smithing (bar → items)
 *  - XP validation for both actions
 *  - Animation verification
 *
 * Assumes player has ores/bars and hammer in inventory.
 * Use ::master and ::invadd commands in-game chat to set up.
 */

// ---------------------------------------------------------------------------
// Cache-verified data (mcp-osrs rev 233)
// Item IDs:
//   copper_ore=436, tin_ore=438, iron_ore=440, silver_ore=442, gold_ore=444
//   coal=453, mithril_ore=447, adamantite_ore=449, runite_ore=451
//   bronze_bar=2349, iron_bar=2351, steel_bar=2353, silver_bar=2355
//   gold_bar=2357, mithril_bar=2359, adamantite_bar=2361, runite_bar=2363
//   hammer=2347
//   bronze_dagger=1205, bronze_axe=1351, bronze_med_helm=1139
// Loc IDs: furnace=2030, anvil=2097
// Seq IDs: human_smithing=898
// ---------------------------------------------------------------------------

const SMITHING_ANIM = 898; // human_smithing

// Bar smelting data (F2P)
const BAR_TIERS = [
  {
    name: "Bronze bar",
    levelReq: 1,
    wikiXp: 6.2,
    barId: 2349,
    oreIds: [436, 438], // copper + tin
    inputCounts: { 436: 1, 438: 1 },
  },
  {
    name: "Iron bar",
    levelReq: 15,
    wikiXp: 12.5,
    barId: 2351,
    oreIds: [440], // iron ore
    inputCounts: { 440: 1 },
    note: "50% success rate without ring of forging",
  },
  {
    name: "Silver bar",
    levelReq: 20,
    wikiXp: 13.7,
    barId: 2355,
    oreIds: [442], // silver ore
    inputCounts: { 442: 1 },
  },
  {
    name: "Steel bar",
    levelReq: 30,
    wikiXp: 17.5,
    barId: 2353,
    oreIds: [440, 453], // iron ore + coal
    inputCounts: { 440: 1, 453: 2 },
  },
  {
    name: "Gold bar",
    levelReq: 40,
    wikiXp: 22.5,
    barId: 2357,
    oreIds: [444], // gold ore
    inputCounts: { 444: 1 },
    note: "56.2 XP with goldsmith gauntlets",
  },
];

// Anvil smithing data (bronze items for F2P testing)
const SMITHING_ITEMS = [
  {
    name: "Bronze dagger",
    levelReq: 1,
    wikiXp: 12.5,
    barCount: 1,
    barId: 2349, // bronze bar
    itemId: 1205,
  },
  {
    name: "Bronze axe",
    levelReq: 1,
    wikiXp: 12.5,
    barCount: 1,
    barId: 2349,
    itemId: 1351,
  },
  {
    name: "Bronze mace",
    levelReq: 2,
    wikiXp: 12.5,
    barCount: 1,
    barId: 2349,
    itemId: 1422,
  },
  {
    name: "Bronze med helm",
    levelReq: 3,
    wikiXp: 12.5,
    barCount: 1,
    barId: 2349,
    itemId: 1139,
  },
  {
    name: "Bronze sword",
    levelReq: 4,
    wikiXp: 12.5,
    barCount: 1,
    barId: 2349,
    itemId: 1277,
  },
  {
    name: "Bronze scimitar",
    levelReq: 5,
    wikiXp: 25.0,
    barCount: 2,
    barId: 2349,
    itemId: 1321,
  },
  {
    name: "Bronze longsword",
    levelReq: 6,
    wikiXp: 25.0,
    barCount: 2,
    barId: 2349,
    itemId: 1291,
  },
];

// Test locations
const TEST_LOCATIONS = {
  lumbridgeFurnace: { x: 3228, z: 3256, desc: "Lumbridge Furnace" },
  alKharidFurnace: { x: 3277, z: 3185, desc: "Al Kharid Furnace" },
  varrockAnvilWest: { x: 3188, z: 3425, desc: "Varrock West Anvil" },
  varrockAnvilEast: { x: 3249, z: 3486, desc: "Varrock East Anvil" },
};

// Loc IDs
const FURNACE_LOC_ID = 2030;
const ANVIL_LOC_ID = 2097;

// Item IDs
const HAMMER_ID = 2347;

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

// Helper: Check if player has required materials
function hasMaterials(inv, materials) {
  for (const [itemId, count] of Object.entries(materials)) {
    const have = countItems(inv, parseInt(itemId));
    if (have < count) return false;
  }
  return true;
}

// ---------------------------------------------------------------------------
// Test 1: Bar Smelting at Furnace
// ---------------------------------------------------------------------------

console.log("\n═══ Test 1: Bar Smelting at Furnace ═══");

// Teleport to Lumbridge furnace
sdk.sendTeleport(TEST_LOCATIONS.lumbridgeFurnace.x, TEST_LOCATIONS.lumbridgeFurnace.z, 0);
await sdk.waitTicks(3);

for (const bar of BAR_TIERS.slice(0, 3)) { // Test first 3 tiers (bronze, iron, silver)
  console.log("\n── Testing: " + bar.name + " (req lv" + bar.levelReq + ", " + bar.wikiXp + " XP) ──");
  
  // Check if we have required ores
  const inv = sdk.getInventory();
  if (!hasMaterials(inv, bar.inputCounts)) {
    const needed = Object.entries(bar.inputCounts).map(([id, count]) => count + "x item " + id).join(", ");
    record(bar.name, "has_materials", false, needed, "missing", "Missing required ores. Use ::invadd commands to add ores.");
    continue;
  }
  record(bar.name, "has_materials", true, "yes", "yes", "Ready to smelt");

  const xpBefore = sdk.getSkill("smithing").xp;
  const barBefore = countItems(inv, bar.barId);
  
  // Count input ores before
  const oresBefore = {};
  for (const oreId of bar.oreIds) {
    oresBefore[oreId] = countItems(inv, oreId);
  }

  // Use primary ore on furnace
  const primaryOre = bar.oreIds[0];
  sdk.sendAction({
    type: "interact_loc_u",
    id: FURNACE_LOC_ID,
    x: TEST_LOCATIONS.lumbridgeFurnace.x,
    z: TEST_LOCATIONS.lumbridgeFurnace.z,
    item_id: primaryOre
  });

  let animSeen = 0;
  let xpGained = 0;
  let elapsed = 0;
  let success = false;

  while (elapsed < 15000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;
    
    if (p.animation !== 0 && p.animation !== 65535 && animSeen === 0) {
      animSeen = p.animation;
    }
    
    const xpDelta = p.skills.smithing.xp - xpBefore;
    if (xpDelta > 0) {
      xpGained = xpDelta;
      success = true;
      break;
    }
  }

  await sdk.waitTicks(2);
  
  const invAfter = sdk.getInventory();
  const barAfter = countItems(invAfter, bar.barId);
  
  // Check ores consumed
  let oresConsumed = true;
  for (const oreId of bar.oreIds) {
    const oreAfter = countItems(invAfter, oreId);
    const expectedConsumed = bar.inputCounts[oreId];
    const actualConsumed = oresBefore[oreId] - oreAfter;
    if (actualConsumed < expectedConsumed) {
      oresConsumed = false;
    }
  }

  // Animation check
  const animPass = animSeen === SMITHING_ANIM;
  record(bar.name, "animation", animPass, SMITHING_ANIM, animSeen,
    animPass ? "Smelting animation played" : (animSeen === 0 ? "No anim seen" : "Unexpected anim " + animSeen));

  // XP check
  const xpPass = xpGained > 0;
  const effectiveRate = xpGained > 0 ? (xpGained / bar.wikiXp).toFixed(1) + "x" : "n/a";
  record(bar.name, "xp_grant", xpPass, ">0 (wiki=" + bar.wikiXp + ")", xpGained,
    xpPass ? "XP granted, effective rate: " + effectiveRate : "No XP gained");

  // Bar produced check
  const barProduced = barAfter > barBefore;
  record(bar.name, "bar_produced", barProduced, "+1 (id=" + bar.barId + ")", barAfter - barBefore,
    barProduced ? "Bar successfully smelted" : (bar.name === "Iron bar" ? "Iron may have failed (50% rate)" : "No bar produced"));

  // Ores consumed check
  record(bar.name, "ores_consumed", oresConsumed, "yes", oresConsumed ? "yes" : "no",
    oresConsumed ? "Input ores consumed" : "Ores not consumed properly");
}

// ---------------------------------------------------------------------------
// Test 2: Anvil Smithing
// ---------------------------------------------------------------------------

console.log("\n═══ Test 2: Anvil Smithing ═══");

// Teleport to Varrock anvil
sdk.sendTeleport(TEST_LOCATIONS.varrockAnvilWest.x, TEST_LOCATIONS.varrockAnvilWest.z, 0);
await sdk.waitTicks(3);

// Check for hammer
const hasHammer = countItems(sdk.getInventory(), HAMMER_ID) > 0;
if (!hasHammer) {
  record("Setup", "has_hammer", false, "hammer (id=" + HAMMER_ID + ")", "missing", "Need hammer for anvil smithing. Use ::invadd hammer 1");
} else {
  record("Setup", "has_hammer", true, "yes", "yes", "Hammer available");
}

for (const item of SMITHING_ITEMS.slice(0, 4)) { // Test first 4 items
  console.log("\n── Testing: " + item.name + " (req lv" + item.levelReq + ", " + item.wikiXp + " XP, " + item.barCount + " bar) ──");
  
  // Check if we have enough bars
  const inv = sdk.getInventory();
  const barCount = countItems(inv, item.barId);
  if (barCount < item.barCount) {
    record(item.name, "has_bars", false, item.barCount + " bronze bars", barCount, "Not enough bars. Use ::invadd bronze_bar " + item.barCount);
    continue;
  }
  record(item.name, "has_bars", true, item.barCount + " bars", barCount + " bars", "Ready to smith");

  const xpBefore = sdk.getSkill("smithing").xp;
  const itemBefore = countItems(inv, item.itemId);
  const barBefore = countItems(inv, item.barId);

  // Use bar on anvil
  sdk.sendAction({
    type: "interact_loc_u",
    id: ANVIL_LOC_ID,
    x: TEST_LOCATIONS.varrockAnvilWest.x,
    z: TEST_LOCATIONS.varrockAnvilWest.z,
    item_id: item.barId
  });

  let animSeen = 0;
  let xpGained = 0;
  let elapsed = 0;

  while (elapsed < 15000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;
    
    if (p.animation !== 0 && p.animation !== 65535 && animSeen === 0) {
      animSeen = p.animation;
    }
    
    const xpDelta = p.skills.smithing.xp - xpBefore;
    if (xpDelta > 0) {
      xpGained = xpDelta;
      break;
    }
  }

  await sdk.waitTicks(2);
  
  const invAfter = sdk.getInventory();
  const itemAfter = countItems(invAfter, item.itemId);
  const barAfter = countItems(invAfter, item.barId);

  // Animation check
  const animPass = animSeen === SMITHING_ANIM;
  record(item.name, "animation", animPass, SMITHING_ANIM, animSeen,
    animPass ? "Smithing animation played" : (animSeen === 0 ? "No anim seen" : "Unexpected anim " + animSeen));

  // XP check
  const xpPass = xpGained > 0;
  const effectiveRate = xpGained > 0 ? (xpGained / item.wikiXp).toFixed(1) + "x" : "n/a";
  record(item.name, "xp_grant", xpPass, ">0 (wiki=" + item.wikiXp + ")", xpGained,
    xpPass ? "XP granted, effective rate: " + effectiveRate : "No XP gained");

  // Item produced check
  const itemProduced = itemAfter > itemBefore;
  record(item.name, "item_produced", itemProduced, "+1 (id=" + item.itemId + ")", itemAfter - itemBefore,
    itemProduced ? "Item successfully smithed" : "No item produced");

  // Bars consumed check
  const barsConsumed = barBefore - barAfter;
  const barsPass = barsConsumed === item.barCount;
  record(item.name, "bars_consumed", barsPass, item.barCount, barsConsumed,
    barsPass ? "Correct number of bars used" : "Wrong number of bars consumed");
}

// ---------------------------------------------------------------------------
// Test 3: Higher Tier Bars (if levels permit)
// ---------------------------------------------------------------------------

console.log("\n═══ Test 3: Higher Tier Bar Validation ═══");

const currentLevel = sdk.getSkill("smithing").level;
console.log("Current Smithing Level: " + currentLevel);
record("Level Check", "smithing_level", true, ">=1", currentLevel, "Player smithing level");

for (const bar of BAR_TIERS.slice(3)) { // Test steel, gold
  if (currentLevel >= bar.levelReq) {
    console.log("\n── Testing: " + bar.name + " (req lv" + bar.levelReq + ") ──");
    
    const inv = sdk.getInventory();
    if (!hasMaterials(inv, bar.inputCounts)) {
      record(bar.name, "has_materials", false, "yes", "no", "Missing required ores");
      continue;
    }
    
    const xpBefore = sdk.getSkill("smithing").xp;
    const primaryOre = bar.oreIds[0];
    
    sdk.sendAction({
      type: "interact_loc_u",
      id: FURNACE_LOC_ID,
      x: TEST_LOCATIONS.lumbridgeFurnace.x,
      z: TEST_LOCATIONS.lumbridgeFurnace.z,
      item_id: primaryOre
    });

    let xpGained = 0;
    let elapsed = 0;
    while (elapsed < 15000) {
      await sdk.waitTicks(1);
      elapsed += 600;
      const p = sdk.getPlayer();
      if (!p) continue;
      const xpDelta = p.skills.smithing.xp - xpBefore;
      if (xpDelta > 0) { xpGained = xpDelta; break; }
    }

    const xpPass = xpGained > 0;
    record(bar.name, "xp_grant", xpPass, ">0", xpGained,
      xpPass ? "Higher tier bar gives XP" : "No XP gained");
  } else {
    console.log("  Skipping " + bar.name + " - requires level " + bar.levelReq + ", have " + currentLevel);
  }
}

// ---------------------------------------------------------------------------
// Test 4: Multi-bar Smithing Items
// ---------------------------------------------------------------------------

console.log("\n═══ Test 4: Multi-bar Smithing Items ═══");

for (const item of SMITHING_ITEMS.slice(4)) { // Test multi-bar items (scimitar, longsword)
  const inv = sdk.getInventory();
  const barCount = countItems(inv, item.barId);
  
  if (barCount < item.barCount) {
    console.log("Skipping " + item.name + " - need " + item.barCount + " bars, have " + barCount);
    continue;
  }
  
  console.log("\n── Testing: " + item.name + " (" + item.barCount + " bars) ──");
  
  const xpBefore = sdk.getSkill("smithing").xp;
  const itemBefore = countItems(inv, item.itemId);
  const barBefore = countItems(inv, item.barId);

  sdk.sendAction({
    type: "interact_loc_u",
    id: ANVIL_LOC_ID,
    x: TEST_LOCATIONS.varrockAnvilWest.x,
    z: TEST_LOCATIONS.varrockAnvilWest.z,
    item_id: item.barId
  });

  let xpGained = 0;
  let elapsed = 0;
  while (elapsed < 15000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;
    const xpDelta = p.skills.smithing.xp - xpBefore;
    if (xpDelta > 0) { xpGained = xpDelta; break; }
  }

  await sdk.waitTicks(2);
  
  const invAfter = sdk.getInventory();
  const itemAfter = countItems(invAfter, item.itemId);
  const barAfter = countItems(invAfter, item.barId);
  
  const barsConsumed = barBefore - barAfter;
  
  record(item.name, "xp_grant", xpGained > 0, ">0", xpGained,
    xpGained > 0 ? "Multi-bar item gives XP" : "No XP gained");
  record(item.name, "item_produced", itemAfter > itemBefore, "+1", itemAfter - itemBefore,
    itemAfter > itemBefore ? "Multi-bar item produced" : "No item produced");
  record(item.name, "bars_consumed", barsConsumed === item.barCount, item.barCount, barsConsumed,
    barsConsumed === item.barCount ? "Correct bars consumed" : "Wrong bar count");
}

// ---------------------------------------------------------------------------
// Summary
// ---------------------------------------------------------------------------

const passed = results.filter(r => r.pass).length;
const failed = results.filter(r => !r.pass).length;

console.log("\n═══════════════════════════════════════════════════");
console.log("══ Smithing Test Summary: " + passed + " passed, " + failed + " failed ══");
console.log("═══════════════════════════════════════════════════");

if (failed > 0) {
  console.log("\nFAILURES:");
  for (const r of results.filter(r => !r.pass)) {
    console.log("  ❌ [" + r.test + "] " + r.check + ": expected=" + JSON.stringify(r.expected) + ", actual=" + JSON.stringify(r.actual) + " — " + r.note);
  }
}

console.log("\nTested bar tiers: " + BAR_TIERS.map(b => b.name).join(", "));
console.log("Tested smithing items: " + SMITHING_ITEMS.map(i => i.name).join(", "));
console.log("\nSetup commands for future runs:");
console.log("  ::master");
console.log("  ::invadd copper_ore 10");
console.log("  ::invadd tin_ore 10");
console.log("  ::invadd iron_ore 10");
console.log("  ::invadd silver_ore 10");
console.log("  ::invadd coal 20");
console.log("  ::invadd bronze_bar 10");
console.log("  ::invadd hammer 1");

({ summary: { passed, failed, total: results.length }, results, barsTested: BAR_TIERS.length, itemsTested: SMITHING_ITEMS.length });
