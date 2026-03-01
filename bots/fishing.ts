/**
 * bots/fishing.ts — Fishing skill test
 *
 * Run via MCP execute_script tool. Globals available: bot, sdk
 *
 * Tests each tier of the fishing skill:
 *  - Fishing spot NPC found nearby after teleport
 *  - XP granted (any positive amount; logs actual vs wiki for rate comparison)
 *  - Animation plays (tool-specific seq ID from cache)
 *  - Correct raw fish item produced in inventory
 *  - Tool/bait requirements validated
 *
 * Assumes player has appropriate fishing tools in inventory.
 * Use ::master and add tools via ::invadd in-game chat to set up.
 */

// ---------------------------------------------------------------------------
// Cache-verified data (mcp-osrs rev 233)
// NPC IDs: freshfish (net/bait), saltfish (cage/harpoon), lurefish (lure/bait)
// Tool IDs: small_fishing_net=303, fishing_rod=307, fly_fishing_rod=309,
//           lobster_pot=301, harpoon=311, fishing_bait=313, feather=315
// Fish IDs: raw_shrimps=317, raw_anchovies=321, raw_sardine=327, raw_herring=345,
//           raw_trout=335, raw_salmon=331, raw_pike=349, raw_tuna=359,
//           raw_lobster=377, raw_swordfish=371
// Seq IDs: human_smallnet=621, human_fishing_casting=622 (rod/fly),
//          human_lobster=619 (cage), human_harpoon=618 (harpoon)
// ---------------------------------------------------------------------------

const SEQ_SMALL_NET = 621;
const SEQ_ROD_CASTING = 622;
const SEQ_LOBSTER_CAGE = 619;
const SEQ_HARPOON = 618;

// Tool and bait IDs
const TOOL_SMALL_NET = 303;
const TOOL_FISHING_ROD = 307;
const TOOL_FLY_ROD = 309;
const TOOL_LOBSTER_POT = 301;
const TOOL_HARPOON = 311;
const BAIT_FISHING_BAIT = 313;
const BAIT_FEATHER = 315;

// Fish item IDs
const FISH_RAW_SHRIMPS = 317;
const FISH_RAW_ANCHOVIES = 321;
const FISH_RAW_SARDINE = 327;
const FISH_RAW_HERRING = 345;
const FISH_RAW_TROUT = 335;
const FISH_RAW_SALMON = 331;
const FISH_RAW_PIKE = 349;
const FISH_RAW_TUNA = 359;
const FISH_RAW_LOBSTER = 377;
const FISH_RAW_SWORDFISH = 371;

const TIERS = [
  {
    name: "Net fishing (Shrimps/Anchovies)",
    levelReq: 1,
    wikiXp: 10.0,
    fishId: FISH_RAW_SHRIMPS,
    npcName: "Fishing spot", // freshfish - net/bait spot
    npcId: 1528, // 0_50_50_freshfish
    testX: 3236, testZ: 3150, // Lumbridge swamp
    toolId: TOOL_SMALL_NET,
    baitId: null,
    animId: SEQ_SMALL_NET,
  },
  {
    name: "Bait fishing (Sardine/Herring)",
    levelReq: 5,
    wikiXp: 20.0,
    fishId: FISH_RAW_SARDINE,
    npcName: "Fishing spot", // freshfish - net/bait spot
    npcId: 1528, // 0_50_50_freshfish
    testX: 3236, testZ: 3150, // Lumbridge swamp
    toolId: TOOL_FISHING_ROD,
    baitId: BAIT_FISHING_BAIT,
    animId: SEQ_ROD_CASTING,
  },
  {
    name: "Lure fishing (Trout/Salmon)",
    levelReq: 20,
    wikiXp: 50.0,
    fishId: FISH_RAW_TROUT,
    npcName: "Rod Fishing spot", // lurefish
    npcId: 1506, // 0_37_53_freshfish (lure/bait spot)
    testX: 3105, testZ: 3430, // Gunnarsgrunn (Barbarian Village)
    toolId: TOOL_FLY_ROD,
    baitId: BAIT_FEATHER,
    animId: SEQ_ROD_CASTING,
  },
  {
    name: "Cage fishing (Lobster)",
    levelReq: 40,
    wikiXp: 90.0,
    fishId: FISH_RAW_LOBSTER,
    npcName: "Cage/Harpoon spot", // saltfish
    npcId: 1531, // 0_50_49_saltfish
    testX: 2924, testZ: 3180, // Karamja
    toolId: TOOL_LOBSTER_POT,
    baitId: null,
    animId: SEQ_LOBSTER_CAGE,
  },
  {
    name: "Harpoon fishing (Tuna/Swordfish)",
    levelReq: 35,
    wikiXp: 80.0,
    fishId: FISH_RAW_TUNA,
    npcName: "Cage/Harpoon spot", // saltfish
    npcId: 1531, // 0_50_49_saltfish
    testX: 2924, testZ: 3180, // Karamja
    toolId: TOOL_HARPOON,
    baitId: null,
    animId: SEQ_HARPOON,
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

  // Teleport to test location
  sdk.sendTeleport(tier.testX, tier.testZ, 0);
  await sdk.waitTicks(3);

  // Check for required tool in inventory
  const hasTool = sdk.getInventory().some(i => i.id === tier.toolId);
  if (!hasTool) {
    record(tier.name, "tool_check", false, "tool id " + tier.toolId, "not in inventory", 
      "Missing required fishing tool - add with ::invadd");
    continue;
  }

  // Check for bait if required
  if (tier.baitId !== null) {
    const hasBait = sdk.getInventory().some(i => i.id === tier.baitId);
    if (!hasBait) {
      record(tier.name, "bait_check", false, "bait id " + tier.baitId, "not in inventory",
        "Missing required bait - add with ::invadd");
      continue;
    }
  }

  // Find the fishing spot NPC
  const spot = sdk.findNearbyNpc(tier.npcName);
  if (!spot) {
    record(tier.name, "npc_found", false, tier.npcName, "not found", "No matching NPC within 16 tiles after teleport");
    continue;
  }
  const npcIdMatch = spot.id === tier.npcId;
  record(tier.name, "npc_found", true, tier.npcName, spot.name, 
    "id=" + spot.id + (npcIdMatch ? "" : " (expected " + tier.npcId + ")") + " at (" + spot.x + "," + spot.z + ")");

  // Record state before fishing
  const xpBefore = sdk.getSkill("fishing").xp;
  const fishBefore = sdk.getInventory().filter(i => i.id === tier.fishId).reduce((s, i) => s + i.qty, 0);
  const baitBefore = tier.baitId ? sdk.getInventory().filter(i => i.id === tier.baitId).reduce((s, i) => s + i.qty, 0) : null;

  // Start fishing (op1 = first option)
  sdk.sendInteractNpc(spot.index, 1);

  let xpGained = 0;
  let animSeen = 0;
  let elapsed = 0;
  const timeout = 20000; // 20 second timeout for fishing

  while (elapsed < timeout) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;
    
    // Track animation
    if (p.animation !== 0 && p.animation !== 65535 && animSeen === 0) {
      animSeen = p.animation;
    }
    
    // Check for XP gain
    const delta = p.skills.fishing.xp - xpBefore;
    if (delta > 0) { 
      xpGained = delta; 
      break; 
    }
  }

  // Animation check
  const animPass = animSeen === tier.animId;
  record(tier.name, "animation", animPass, tier.animId, animSeen,
    animPass ? "Correct fishing animation" : (animSeen === 0 ? "No anim seen (polling miss)" : "Unexpected anim id " + animSeen));

  // XP check
  const xpPass = xpGained > 0;
  const effectiveRate = xpGained > 0 ? (xpGained / tier.wikiXp).toFixed(1) + "x" : "n/a";
  record(tier.name, "xp_grant", xpPass, ">0 (wiki=" + tier.wikiXp + ")", xpGained,
    xpPass ? "XP granted, effective rate: " + effectiveRate : "No XP gained — check level req or tool");

  // Item produced check
  await sdk.waitTicks(1);
  const fishAfter = sdk.getInventory().filter(i => i.id === tier.fishId).reduce((s, i) => s + i.qty, 0);
  const itemPass = fishAfter > fishBefore;
  record(tier.name, "item_produced", itemPass, "+1 (id=" + tier.fishId + ")", itemPass ? "+" + (fishAfter - fishBefore) : "none",
    itemPass ? "Correct fish in inventory" : "No fish found — check item ID");

  // Bait consumption check (if applicable)
  if (tier.baitId !== null) {
    const baitAfter = sdk.getInventory().filter(i => i.id === tier.baitId).reduce((s, i) => s + i.qty, 0);
    const baitConsumed = baitBefore - baitAfter;
    const baitPass = baitConsumed > 0;
    record(tier.name, "bait_consumed", baitPass, "-1 bait", "-" + baitConsumed,
      baitPass ? "Bait consumed correctly" : "Bait not consumed — may indicate issue");
  }
}

// ---------------------------------------------------------------------------
// Summary
// ---------------------------------------------------------------------------

const passed = results.filter(r => r.pass).length;
const failed = results.filter(r => !r.pass).length;
console.log("\n══ Fishing Test Summary: " + passed + " passed, " + failed + " failed ══");
if (failed > 0) {
  console.log("FAILURES:");
  for (const r of results.filter(r => !r.pass)) {
    console.log("  ❌ [" + r.tier + "] " + r.check + ": expected=" + JSON.stringify(r.expected) + ", actual=" + JSON.stringify(r.actual) + " — " + r.note);
  }
}

({ summary: { passed, failed }, results });
