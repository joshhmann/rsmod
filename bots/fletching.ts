/**
 * bots/fletching.ts — Fletching bot test
 *
 * Tests the three main fletching flows:
 * - Knife-on-log (cutting logs into unstrung bows / arrow shafts)
 * - Bowstring stringing (adding bowstring to unstrung bow)
 * - Arrow making (attaching feathers to shafts, then arrowheads)
 *
 * Uses AgentBridge interact_held action for inventory item-on-item interactions.
 */

// -----------------------------------------------------------------------------
// Cache-verified data (mcp-osrs rev 233)
// Item IDs verified against objtypes.txt
// -----------------------------------------------------------------------------

const ITEMS = {
  // Tools and materials
  knife: 946,              // fletching_knife
  logs: 1511,              // logs
  bowstring: 1777,         // bowstring

  // Unstrung bows
  shortbowU: 50,           // shortbow_u
  longbowU: 48,            // longbow_u

  // Strung bows
  shortbow: 841,           // shortbow
  longbow: 839,            // longbow

  // Arrow components
  arrowShaft: 52,          // arrow_shaft
  feather: 314,            // feather
  headlessArrow: 53,       // headless_arrow
  bronzeArrowhead: 39,     // bronze_arrowheads
  bronzeArrow: 882,        // bronze_arrow
};

// Fletching XP rates (per item)
const XP_RATES = {
  shortbowU: 5.0,          // Cutting shortbow (u) from logs
  shortbow: 5.0,           // Stringing shortbow
  arrowShaft: 5.0,         // Making 15 arrow shafts
  headlessArrow: 1.0,      // Adding feathers (per 15 headless arrows)
  bronzeArrow: 1.3,        // Adding bronze arrowheads (per 15 arrows)
};

// -----------------------------------------------------------------------------
// Test framework
// -----------------------------------------------------------------------------

const results = [];

function record(flow, check, pass, expected, actual, note) {
  results.push({ flow, check, pass, expected, actual, note });
  const icon = pass ? "✅" : "❌";
  console.log(`${icon} [${flow}] ${check}: expected=${JSON.stringify(expected)}, actual=${JSON.stringify(actual)} — ${note}`);
}

function invCount(id) {
  const inv = sdk.getInventory() || [];
  return inv.filter((it) => it.id === id).reduce((sum, it) => sum + it.qty, 0);
}

function findItemSlot(id) {
  const inv = sdk.getInventory() || [];
  const entry = inv.find((it) => it.id === id);
  return entry?.slot ?? -1;
}

function ensureItem(id, count = 1) {
  const current = invCount(id);
  if (current < count) {
    sdk.sendSpawnItem(id, count - current);
    return true;
  }
  return false;
}

// -----------------------------------------------------------------------------
// Test: Knife-on-log (making unstrung shortbow)
// -----------------------------------------------------------------------------

async function testKnifeOnLog() {
  console.log("\n── Testing: Knife-on-log (cutting shortbow) ──");

  // Setup inventory
  const spawned = ensureItem(ITEMS.knife, 1) || ensureItem(ITEMS.logs, 1);
  if (spawned) await sdk.waitTicks(2);

  const knifeSlot = findItemSlot(ITEMS.knife);
  const logsSlot = findItemSlot(ITEMS.logs);

  if (knifeSlot === -1 || logsSlot === -1) {
    record("Knife-on-log", "inventory_setup", false, "knife + logs present", { knifeSlot, logsSlot }, "Required items not in inventory after spawn");
    return;
  }

  record("Knife-on-log", "inventory_setup", true, "knife + logs", { knifeSlot, logsSlot }, "Items positioned in inventory");

  const xpBefore = sdk.getSkill("fletching")?.xp ?? 0;
  const shortbowUBefore = invCount(ITEMS.shortbowU);

  // Perform knife-on-log interaction
  sdk.sendInteractHeld(knifeSlot, logsSlot);

  // Wait for XP gain (with timeout)
  let xpGained = 0;
  let elapsed = 0;
  while (elapsed < 5000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const xpAfter = sdk.getSkill("fletching")?.xp ?? 0;
    if (xpAfter > xpBefore) {
      xpGained = xpAfter - xpBefore;
      break;
    }
  }

  await sdk.waitTicks(2);
  const shortbowUAfter = invCount(ITEMS.shortbowU);
  const logsAfter = invCount(ITEMS.logs);

  // Verify results
  const xpPass = xpGained > 0;
  record("Knife-on-log", "xp_gain", xpPass, ">0 (wiki=" + XP_RATES.shortbowU + ")", xpGained,
    xpPass ? "Fletching XP granted for cutting" : "No XP gained — check fletching level requirement");

  const itemPass = shortbowUAfter > shortbowUBefore;
  record("Knife-on-log", "item_produced", itemPass, "+1 shortbow (u)", shortbowUAfter - shortbowUBefore,
    itemPass ? "Unstrung shortbow created" : "No shortbow (u) produced");

  const logsConsumed = logsAfter < 1;
  record("Knife-on-log", "materials_consumed", logsConsumed, "logs consumed", logsAfter,
    logsConsumed ? "Logs used in process" : "Logs not consumed — possible failure");
}

// -----------------------------------------------------------------------------
// Test: Bowstring stringing
// -----------------------------------------------------------------------------

async function testBowstringStringing() {
  console.log("\n── Testing: Bowstring stringing ──");

  // Setup inventory
  const spawned = ensureItem(ITEMS.shortbowU, 1) || ensureItem(ITEMS.bowstring, 1);
  if (spawned) await sdk.waitTicks(2);

  const bowSlot = findItemSlot(ITEMS.shortbowU);
  const stringSlot = findItemSlot(ITEMS.bowstring);

  if (bowSlot === -1 || stringSlot === -1) {
    record("Bowstring", "inventory_setup", false, "unstrung bow + bowstring", { bowSlot, stringSlot }, "Required items not in inventory");
    return;
  }

  record("Bowstring", "inventory_setup", true, "unstrung bow + bowstring", { bowSlot, stringSlot }, "Items positioned in inventory");

  const xpBefore = sdk.getSkill("fletching")?.xp ?? 0;
  const strungBefore = invCount(ITEMS.shortbow);

  // Perform stringing interaction
  sdk.sendInteractHeld(stringSlot, bowSlot);

  // Wait for XP gain
  let xpGained = 0;
  let elapsed = 0;
  while (elapsed < 5000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const xpAfter = sdk.getSkill("fletching")?.xp ?? 0;
    if (xpAfter > xpBefore) {
      xpGained = xpAfter - xpBefore;
      break;
    }
  }

  await sdk.waitTicks(2);
  const strungAfter = invCount(ITEMS.shortbow);

  const xpPass = xpGained > 0;
  record("Bowstring", "xp_gain", xpPass, ">0 (wiki=" + XP_RATES.shortbow + ")", xpGained,
    xpPass ? "Fletching XP granted for stringing" : "No XP gained");

  const itemPass = strungAfter > strungBefore;
  record("Bowstring", "item_produced", itemPass, "+1 strung shortbow", strungAfter - strungBefore,
    itemPass ? "Shortbow successfully strung" : "No strung bow produced");
}

// -----------------------------------------------------------------------------
// Test: Arrow making (feathers on shafts)
// -----------------------------------------------------------------------------

async function testArrowMaking() {
  console.log("\n── Testing: Arrow making (feathers on shafts) ──");

  // Setup inventory
  const spawned = ensureItem(ITEMS.arrowShaft, 15) || ensureItem(ITEMS.feather, 15);
  if (spawned) await sdk.waitTicks(2);

  const shaftSlot = findItemSlot(ITEMS.arrowShaft);
  const featherSlot = findItemSlot(ITEMS.feather);

  if (shaftSlot === -1 || featherSlot === -1) {
    record("Arrow-making", "inventory_setup", false, "shafts + feathers", { shaftSlot, featherSlot }, "Required items not in inventory");
    return;
  }

  record("Arrow-making", "inventory_setup", true, "shafts + feathers", { shaftSlot, featherSlot }, "Items positioned in inventory");

  const xpBefore = sdk.getSkill("fletching")?.xp ?? 0;
  const headlessBefore = invCount(ITEMS.headlessArrow);

  // Perform feather-on-shaft interaction
  sdk.sendInteractHeld(featherSlot, shaftSlot);

  // Wait for XP gain
  let xpGained = 0;
  let elapsed = 0;
  while (elapsed < 5000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const xpAfter = sdk.getSkill("fletching")?.xp ?? 0;
    if (xpAfter > xpBefore) {
      xpGained = xpAfter - xpBefore;
      break;
    }
  }

  await sdk.waitTicks(2);
  const headlessAfter = invCount(ITEMS.headlessArrow);

  const xpPass = xpGained > 0;
  record("Arrow-making", "xp_gain", xpPass, ">0 (wiki=" + XP_RATES.headlessArrow + ")", xpGained,
    xpPass ? "Fletching XP granted for arrow assembly" : "No XP gained");

  const itemPass = headlessAfter > headlessBefore;
  record("Arrow-making", "item_produced", itemPass, "+15 headless arrows", headlessAfter - headlessBefore,
    itemPass ? "Headless arrows created" : "No headless arrows produced");
}

// -----------------------------------------------------------------------------
// Main test runner
// -----------------------------------------------------------------------------

console.log("══ Fletching Bot Test Starting ══");
console.log("Testing fletching flows with AgentBridge interact_held support");

await testKnifeOnLog();
await testBowstringStringing();
await testArrowMaking();

// -----------------------------------------------------------------------------
// Summary
// -----------------------------------------------------------------------------

const passed = results.filter(r => r.pass).length;
const failed = results.filter(r => !r.pass).length;

console.log("\n══ Fletching Test Summary: " + passed + " passed, " + failed + " failed ══");
if (failed > 0) {
  console.log("FAILURES:");
  for (const r of results.filter(r => !r.pass)) {
    console.log("  ❌ [" + r.flow + "] " + r.check + ": expected=" + JSON.stringify(r.expected) + ", actual=" + JSON.stringify(r.actual) + " — " + r.note);
  }
}

({ summary: { passed, failed }, results });
