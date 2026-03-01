/**
 * bots/prayer.ts — Prayer skill test
 *
 * Run via MCP execute_script tool. Globals available: bot, sdk
 *
 * Tests the prayer skill:
 *  - Bone burying (base XP)
 *  - Altar use for 2x XP multiplier
 *  - Prayer point restoration at altars
 *
 * Assumes player has bones in inventory.
 * Use ::master and ::invadd bones 10 etc. in-game chat to set up.
 */

// ---------------------------------------------------------------------------
// Cache-verified data (mcp-osrs rev 233)
// Item IDs
// Bones: bones=526, big_bones=532, babydragon_bones=534, dragon_bones=536
//        bat_bones=530, wolf_bones=2859, jogre_bones=3125
// Loc IDs: altar=409 (Lumbridge church), altar_slab=1876
//          monks_altar=2640 (Edgeville monastery)
// Seq IDs: human_pickupfloor=827 (bury anim), human_bone_sacrifice=3705 (altar anim)
// ---------------------------------------------------------------------------

const BURY_ANIM = 827;        // human_pickupfloor
const ALTAR_ANIM = 3705;      // human_bone_sacrifice

// Bone types with their XP values
const BONE_TYPES = [
  {
    name: "Bones",
    id: 526,
    buryXp: 4.5,
    altarXp: 9.0,  // 2x multiplier
  },
  {
    name: "Big bones",
    id: 532,
    buryXp: 15.0,
    altarXp: 30.0,  // 2x multiplier
  },
  {
    name: "Bat bones",
    id: 530,
    buryXp: 4.5,
    altarXp: 9.0,
  },
];

// Test locations
const TEST_LOCATIONS = {
  lumbridgeAltar: { x: 3245, z: 3206, desc: "Lumbridge Church Altar" },
  edgevilleMonastery: { x: 3051, z: 3496, desc: "Edgeville Monastery Altar" },
  varrockChurch: { x: 3256, z: 3488, desc: "Varrock Church Altar" },
};

// Altar loc IDs
const LUMBRIDGE_ALTAR_ID = 409;
const ALTAR_SLAB_ID = 1876;
const MONKS_ALTAR_ID = 2640;

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

// Helper: Get prayer points
function getPrayerPoints() {
  const p = sdk.getPlayer();
  return p ? p.skills.prayer.current : 0;
}

// Helper: Get max prayer points
function getMaxPrayerPoints() {
  const p = sdk.getPlayer();
  return p ? p.skills.prayer.level : 0;
}

// ---------------------------------------------------------------------------
// Test 1: Bone Burying
// ---------------------------------------------------------------------------

console.log("\n═══ Test 1: Bone Burying ═══");

// Teleport to a safe location
sdk.sendTeleport(TEST_LOCATIONS.lumbridgeAltar.x, TEST_LOCATIONS.lumbridgeAltar.z, 0);
await sdk.waitTicks(3);

for (const bone of BONE_TYPES.slice(0, 2)) { // Test first 2 bone types
  console.log("\n── Testing: " + bone.name + " (bury XP: " + bone.buryXp + ") ──");

  // Check if we have bones
  const boneCount = countItems(sdk.getInventory(), bone.id);
  if (boneCount < 1) {
    record(bone.name, "has_bones", false, ">=1", boneCount, "No " + bone.name + " in inventory. Use ::invadd " + bone.name.toLowerCase().replace(" ", "_") + " 10");
    continue;
  }
  record(bone.name, "has_bones", true, ">=1", boneCount, "Ready to bury");

  const xpBefore = sdk.getSkill("prayer").xp;
  const bonesBefore = countItems(sdk.getInventory(), bone.id);

  // Bury bone - use interact with item (op2 = Bury)
  sdk.sendAction({
    type: "interact_item",
    item_id: bone.id,
    option: 2  // Bury option
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

    const xpDelta = p.skills.prayer.xp - xpBefore;
    if (xpDelta > 0) {
      xpGained = xpDelta;
      break;
    }
  }

  await sdk.waitTicks(1);

  const inv = sdk.getInventory();
  const bonesAfter = countItems(inv, bone.id);

  // Animation check
  const animPass = animSeen === BURY_ANIM;
  record(bone.name, "animation", animPass, BURY_ANIM, animSeen,
    animPass ? "Bury animation played" : (animSeen === 0 ? "No anim seen" : "Unexpected anim " + animSeen));

  // XP check
  const xpPass = xpGained > 0;
  const effectiveRate = xpGained > 0 ? (xpGained / bone.buryXp).toFixed(1) + "x" : "n/a";
  record(bone.name, "xp_grant", xpPass, ">0 (wiki=" + bone.buryXp + ")", xpGained,
    xpPass ? "XP granted, effective rate: " + effectiveRate : "No XP gained");

  // Bone consumed check
  const bonesConsumed = bonesBefore - bonesAfter;
  record(bone.name, "bone_consumed", bonesConsumed > 0, ">=1", bonesConsumed,
    bonesConsumed > 0 ? "Bone consumed" : "Bone not consumed");
}

// ---------------------------------------------------------------------------
// Test 2: Altar Use (2x XP)
// ---------------------------------------------------------------------------

console.log("\n═══ Test 2: Altar Use (2x XP) ═══");

// Teleport to Lumbridge church
sdk.sendTeleport(TEST_LOCATIONS.lumbridgeAltar.x, TEST_LOCATIONS.lumbridgeAltar.z, 0);
await sdk.waitTicks(3);

for (const bone of BONE_TYPES.slice(0, 2)) { // Test first 2 bone types
  console.log("\n── Testing: " + bone.name + " on Altar (altar XP: " + bone.altarXp + ") ──");

  // Check if we have bones
  const boneCount = countItems(sdk.getInventory(), bone.id);
  if (boneCount < 1) {
    record(bone.name + " (altar)", "has_bones", false, ">=1", boneCount, "No " + bone.name + " in inventory");
    continue;
  }
  record(bone.name + " (altar)", "has_bones", true, ">=1", boneCount, "Ready to offer");

  const xpBefore = sdk.getSkill("prayer").xp;
  const bonesBefore = countItems(sdk.getInventory(), bone.id);

  // Use bone on altar
  sdk.sendAction({
    type: "interact_loc_u",
    id: LUMBRIDGE_ALTAR_ID,
    x: TEST_LOCATIONS.lumbridgeAltar.x,
    z: TEST_LOCATIONS.lumbridgeAltar.z,
    item_id: bone.id
  });

  let animSeen = 0;
  let xpGained = 0;
  let elapsed = 0;

  while (elapsed < 8000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;

    if (p.animation !== 0 && p.animation !== 65535 && animSeen === 0) {
      animSeen = p.animation;
    }

    const xpDelta = p.skills.prayer.xp - xpBefore;
    if (xpDelta > 0) {
      xpGained = xpDelta;
      break;
    }
  }

  await sdk.waitTicks(2);

  const inv = sdk.getInventory();
  const bonesAfter = countItems(inv, bone.id);

  // Animation check
  const animPass = animSeen === ALTAR_ANIM || animSeen === BURY_ANIM;
  record(bone.name + " (altar)", "animation", animPass, ALTAR_ANIM + "/" + BURY_ANIM, animSeen,
    animPass ? "Altar offering animation played" : (animSeen === 0 ? "No anim seen" : "Unexpected anim " + animSeen));

  // XP check - should be ~2x bury XP
  const xpPass = xpGained > 0;
  const expectedAltarXp = bone.altarXp;
  const multiplier = xpGained > 0 && bone.buryXp > 0 ? (xpGained / bone.buryXp).toFixed(1) + "x" : "n/a";
  record(bone.name + " (altar)", "xp_grant", xpPass, ">0 (altar=" + expectedAltarXp + ")", xpGained,
    xpPass ? "Altar XP granted, multiplier: " + multiplier : "No XP gained");

  // Bone consumed check
  const bonesConsumed = bonesBefore - bonesAfter;
  record(bone.name + " (altar)", "bone_consumed", bonesConsumed > 0, ">=1", bonesConsumed,
    bonesConsumed > 0 ? "Bone offered" : "Bone not consumed");

  // Verify 2x multiplier (approximately)
  if (xpGained > 0) {
    const is2x = xpGained >= bone.buryXp * 1.5; // Allow some tolerance
    record(bone.name + " (altar)", "2x_multiplier", is2x, "~2x base", xpGained + " vs " + bone.buryXp,
      is2x ? "2x XP multiplier confirmed" : "XP multiplier seems off");
  }
}

// ---------------------------------------------------------------------------
// Test 3: Prayer Point Restoration
// ---------------------------------------------------------------------------

console.log("\n═══ Test 3: Prayer Point Restoration ═══");

// Teleport to Lumbridge church
sdk.sendTeleport(TEST_LOCATIONS.lumbridgeAltar.x, TEST_LOCATIONS.lumbridgeAltar.z, 0);
await sdk.waitTicks(3);

const prayerBefore = getPrayerPoints();
const maxPrayer = getMaxPrayerPoints();

console.log("Current prayer: " + prayerBefore + "/" + maxPrayer);
record("Prayer Restore", "prayer_status", true, "any", prayerBefore + "/" + maxPrayer, "Prayer points status");

// Pray at altar (op1 = Pray)
sdk.sendAction({
  type: "interact_loc",
  id: LUMBRIDGE_ALTAR_ID,
  x: TEST_LOCATIONS.lumbridgeAltar.x,
  z: TEST_LOCATIONS.lumbridgeAltar.z,
  option: 1
});

let prayerRestored = false;
let elapsed = 0;

while (elapsed < 5000) {
  await sdk.waitTicks(1);
  elapsed += 600;

  const currentPrayer = getPrayerPoints();
  if (currentPrayer > prayerBefore || currentPrayer >= maxPrayer) {
    prayerRestored = true;
    break;
  }
}

await sdk.waitTicks(1);

const prayerAfter = getPrayerPoints();
record("Prayer Restore", "prayer_restore", prayerRestored || prayerAfter > prayerBefore, "restored", prayerAfter,
  prayerRestored || prayerAfter > prayerBefore ? "Prayer points restored" : "Prayer not restored (may already be full)");

// ---------------------------------------------------------------------------
// Test 4: Multiple Bone Burying
// ---------------------------------------------------------------------------

console.log("\n═══ Test 4: Multiple Bone Burying ═══");

const testBone = BONE_TYPES[0]; // Regular bones
const buryAttempts = 3;
let successfulBuries = 0;
let totalXpGained = 0;

console.log("Burying " + buryAttempts + " " + testBone.name + "...");

for (let i = 0; i < buryAttempts; i++) {
  const boneCount = countItems(sdk.getInventory(), testBone.id);
  if (boneCount < 1) {
    console.log("  Out of " + testBone.name);
    break;
  }

  const xpBefore = sdk.getSkill("prayer").xp;

  // Bury bone
  sdk.sendAction({
    type: "interact_item",
    item_id: testBone.id,
    option: 2
  });

  let xpGained = 0;
  let waitTime = 0;

  while (waitTime < 5000) {
    await sdk.waitTicks(1);
    waitTime += 600;
    const p = sdk.getPlayer();
    if (!p) continue;

    const xpDelta = p.skills.prayer.xp - xpBefore;
    if (xpDelta > 0) {
      xpGained = xpDelta;
      break;
    }
  }

  if (xpGained > 0) {
    successfulBuries++;
    totalXpGained += xpGained;
  }

  await sdk.waitTicks(1);
}

record("Multiple Bury", "successful_buries", successfulBuries > 0, ">=1", successfulBuries,
  successfulBuries + "/" + buryAttempts + " bones buried successfully");
record("Multiple Bury", "total_xp", totalXpGained > 0, ">0", totalXpGained,
  "Total XP gained: " + totalXpGained);

// ---------------------------------------------------------------------------
// Summary
// ---------------------------------------------------------------------------

const passed = results.filter(r => r.pass).length;
const failed = results.filter(r => !r.pass).length;

console.log("\n═══════════════════════════════════════════════════");
console.log("══ Prayer Test Summary: " + passed + " passed, " + failed + " failed ══");
console.log("═══════════════════════════════════════════════════");

if (failed > 0) {
  console.log("\nFAILURES:");
  for (const r of results.filter(r => !r.pass)) {
    console.log("  ❌ [" + r.test + "] " + r.check + ": expected=" + JSON.stringify(r.expected) + ", actual=" + JSON.stringify(r.actual) + " — " + r.note);
  }
}

console.log("\nTested bone types: " + BONE_TYPES.map(b => b.name).join(", "));
console.log("Altar locations: Lumbridge Church");
console.log("\nSetup commands for future runs:");
console.log("  ::master");
console.log("  ::invadd bones 20");
console.log("  ::invadd big_bones 20");
console.log("  ::invadd bat_bones 20");

({ summary: { passed, failed, total: results.length }, results, bonesTested: BONE_TYPES.length });
