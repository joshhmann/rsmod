/**
 * bots/woodcutting.ts — Woodcutting skill test
 *
 * Run via MCP execute_script tool. Globals available: bot, sdk
 *
 * Loc names, IDs, and seq IDs sourced from rev 233 cache via mcp-osrs.
 * Coordinates verified in-game against loaded map regions.
 *
 * Tests each tier of the woodcutting skill:
 *  - Loc found nearby after teleport
 *  - XP granted (any positive amount; logs actual vs wiki for rate comparison)
 *  - Animation plays (axe-specific seq ID from cache)
 *  - Correct log item produced in inventory
 *
 * Assumes player has a rune axe equipped.
 * Use ::master and ::invadd rune_axe in-game chat to set up.
 */

// ---------------------------------------------------------------------------
// Cache-verified data (mcp-osrs rev 233)
// Loc IDs:  tree2=1278, oaktree=10820, willowtree=10819, yewtree=10822
// Log IDs:  logs=1511, oak_logs=1521, willow_logs=1519, yew_logs=1515
// Seq IDs:  human_woodcutting_rune_axe=867 (used for all tiers when rune axe equipped)
// ---------------------------------------------------------------------------

const RUNE_AXE_ANIM = 867; // human_woodcutting_rune_axe (cache seq id)

const TIERS = [
  {
    name: "Normal tree",
    levelReq: 1,
    wikiXp: 25.0,
    logId: 1511,         // logs
    locName: "Tree",
    locId: 1278,         // tree2
    testX: 3171, testZ: 3444,
  },
  {
    name: "Oak tree",
    levelReq: 15,
    wikiXp: 37.5,
    logId: 1521,         // oak_logs
    locName: "Oak tree",
    locId: 10820,        // oaktree
    testX: 3086, testZ: 3234,
  },
  {
    name: "Willow tree",
    levelReq: 30,
    wikiXp: 67.5,
    logId: 1519,         // willow_logs
    locName: "Willow tree",
    locId: 10819,        // willowtree
    testX: 3086, testZ: 3234,
  },
  {
    name: "Yew tree",
    levelReq: 60,
    wikiXp: 175.0,
    logId: 1515,         // yew_logs
    locName: "Yew tree",
    locId: 10822,        // yewtree
    testX: 3195, testZ: 3230,
  },
];

// ---------------------------------------------------------------------------
// Test runner
// ---------------------------------------------------------------------------

const results = [];

function record(tier, check, pass, expected, actual, note) {
  results.push({ tier, check, pass, expected, actual, note });
  const icon = pass ? "✅" : "❌";
  console.log(icon + " [" + tier + "] " + check + ": expected=" + JSON.stringify(expected) + ", actual=" + JSON.stringify(actual) + " — " + note);
}

for (const tier of TIERS) {
  console.log("\n── Testing: " + tier.name + " (req lv" + tier.levelReq + ") ──");

  sdk.sendTeleport(tier.testX, tier.testZ, 0);
  await sdk.waitTicks(3);

  // Find the tree
  const tree = sdk.findNearbyLoc(tier.locName);
  if (!tree) {
    record(tier.name, "loc_found", false, tier.locName, "not found", "No matching loc within 16 tiles after teleport");
    continue;
  }
  const locIdMatch = tree.id === tier.locId;
  record(tier.name, "loc_found", true, tier.locName, tree.name, "id=" + tree.id + (locIdMatch ? "" : " (expected " + tier.locId + ")") + " at (" + tree.x + "," + tree.z + ")");

  const xpBefore = sdk.getSkill("woodcutting").xp;
  const logsBefore = sdk.getInventory().filter(i => i.id === tier.logId).reduce((s, i) => s + i.qty, 0);

  sdk.sendInteractLoc(tree.id, tree.x, tree.z, 1);

  let xpGained = 0;
  let animSeen = 0;
  let elapsed = 0;
  while (elapsed < 15000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;
    if (p.animation !== 0 && p.animation !== 65535 && animSeen === 0) {
      animSeen = p.animation;
    }
    const delta = p.skills.woodcutting.xp - xpBefore;
    if (delta > 0) { xpGained = delta; break; }
  }

  // Animation check — rune axe should play seq 867
  const animPass = animSeen === RUNE_AXE_ANIM;
  record(tier.name, "animation", animPass, RUNE_AXE_ANIM, animSeen,
    animPass ? "Rune axe anim correct" : (animSeen === 0 ? "No anim seen (polling miss)" : "Unexpected anim id " + animSeen));

  // XP check — server may have a rate multiplier; verify XP > 0 and log effective rate
  const xpPass = xpGained > 0;
  const effectiveRate = xpGained > 0 ? (xpGained / tier.wikiXp).toFixed(1) + "x" : "n/a";
  record(tier.name, "xp_grant", xpPass, ">0 (wiki=" + tier.wikiXp + ")", xpGained,
    xpPass ? "XP granted, effective rate: " + effectiveRate : "No XP gained — axe missing or level req not met");

  await sdk.waitTicks(1);
  const logsAfter = sdk.getInventory().filter(i => i.id === tier.logId).reduce((s, i) => s + i.qty, 0);
  const itemPass = logsAfter > logsBefore;
  record(tier.name, "item_produced", itemPass, "+1 (id=" + tier.logId + ")", itemPass ? "+" + (logsAfter - logsBefore) : "none",
    itemPass ? "Correct log in inventory" : "No log found — check item ID");
}

// ---------------------------------------------------------------------------
// Summary
// ---------------------------------------------------------------------------

const passed = results.filter(r => r.pass).length;
const failed = results.filter(r => !r.pass).length;
console.log("\n══ Woodcutting Test Summary: " + passed + " passed, " + failed + " failed ══");
if (failed > 0) {
  console.log("FAILURES:");
  for (const r of results.filter(r => !r.pass)) {
    console.log("  ❌ [" + r.tier + "] " + r.check + ": expected=" + JSON.stringify(r.expected) + ", actual=" + JSON.stringify(r.actual) + " — " + r.note);
  }
}

({ summary: { passed, failed }, results });
