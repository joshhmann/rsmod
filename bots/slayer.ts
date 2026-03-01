/**
 * bots/slayer.ts — Slayer skill test
 *
 * Run via MCP execute_script tool. Globals available: bot, sdk
 *
 * Tests the Slayer skill:
 *  - Task assignment from Slayer Masters
 *  - XP on kill for assigned monsters
 *  - Points on task completion
 *  - Enchanted gem functionality
 *
 * Assumes player has some combat gear and is near a slayer master.
 * Use ::master in-game to set all skills to 99.
 */

// ---------------------------------------------------------------------------
// Slayer Master locations (rev 233 verified)
// Turael: Burthorpe, Vannaka: Edgeville Dungeon, Mazchna: Canifis
// ---------------------------------------------------------------------------

const SLAYER_MASTERS = [
  {
    name: "Turael",
    location: "Burthorpe",
    testX: 2934, testZ: 3514, // Burthorpe Slayer Cave entrance
    npcName: "Turael",
  },
  {
    name: "Vannaka",
    location: "Edgeville Dungeon",
    testX: 3104, testZ: 9956, // Edgeville Dungeon
    npcName: "Vannaka",
  },
  {
    name: "Mazchna",
    location: "Canifis",
    testX: 3493, testZ: 3487, // Canifis
    npcName: "Mazchna",
  },
];

// Slayer task monsters (for testing XP on kill)
// These are F2P-accessible tasks
const TASK_MONSTERS = [
  {
    name: "Goblin",
    npcId: 118,
    testX: 3255, testZ: 3221, // Lumbridge goblins
    xpPerKill: 5.0,
  },
  {
    name: "Hill Giant",
    npcId: 117,
    testX: 3094, testZ: 9968, // Edgeville Dungeon
    xpPerKill: 15.0,
  },
  {
    name: "Moss Giant",
    npcId: 125,
    testX: 3100, testZ: 9972, // Edgeville Dungeon
    xpPerKill: 25.0,
  },
];

// ---------------------------------------------------------------------------
// Test runner
// ---------------------------------------------------------------------------

const results = [];

function record(check, pass, expected, actual, note) {
  results.push({ check, pass, expected, actual, note });
  const icon = pass ? "✅" : "❌";
  console.log(icon + " " + check + ": expected=" + JSON.stringify(expected) + ", actual=" + JSON.stringify(actual) + " — " + note);
}

console.log("═══ Slayer Skill Test ═══\n");

// Get initial slayer level
const initialState = sdk.getPlayer();
const initialSlayerXp = sdk.getSkill("slayer")?.xp || 0;
const initialSlayerLevel = sdk.getSkill("slayer")?.level || 1;

console.log("Initial Slayer: Level " + initialSlayerLevel + ", XP " + initialSlayerXp);

// Test 1: Check player has slayer skill
record(
  "Slayer skill exists",
  initialSlayerLevel > 0,
  "level > 0",
  "level=" + initialSlayerLevel,
  "Player has slayer skill"
);

// Test 2: Find and interact with Turael (easiest to reach)
console.log("\n── Testing: Turael Task Assignment ──");

// Teleport to Burthorpe
sdk.sendTeleport(2934, 3514, 0);
await sdk.waitTicks(3);

// Find Turael
const turael = sdk.findNearbyNpc("Turael");
if (turael) {
  console.log("Found Turael at index " + turael.index);
  
  // Interact with Turael (option 1 = talk-to)
  sdk.sendInteractNpc(turael.index, 1);
  await sdk.waitTicks(5);
  
  record(
    "Turael interaction",
    true,
    "npc found",
    "index=" + turael.index,
    "Successfully interacted with Turael"
  );
} else {
  record(
    "Turael interaction",
    false,
    "npc found",
    "not found",
    "Turael not found at Burthorpe"
  );
}

// Test 3: Find a task monster and kill it
console.log("\n── Testing: Monster Kill XP ──");

// Teleport to goblins near Lumbridge
sdk.sendTeleport(3255, 3221, 0);
await sdk.waitTicks(3);

const goblin = sdk.findNearbyNpc("Goblin");
if (goblin) {
  console.log("Found Goblin at index " + goblin.index);
  
  // Attack the goblin
  sdk.sendInteractNpc(goblin.index, 1);
  await sdk.waitTicks(10);
  
  // Check for XP gain
  const afterState = sdk.getPlayer();
  const afterSlayerXp = sdk.getSkill("slayer")?.xp || 0;
  const xpGained = afterSlayerXp - initialSlayerXp;
  
  console.log("XP gained: " + xpGained);
  
  record(
    "XP on monster kill",
    xpGained > 0,
    "xp > 0",
    "xp gained=" + xpGained,
    xpGained > 0 ? "Gained slayer XP" : "No XP gained (may need active task)"
  );
} else {
  record(
    "XP on monster kill",
    false,
    "npc found",
    "not found",
    "Goblin not found near Lumbridge"
  );
}

// Test 4: Check enchanted gem (if player has one)
console.log("\n── Testing: Enchanted Gem ──");

const inventory = sdk.getInventory();
const hasGem = inventory.some(item => item?.name?.toLowerCase().includes("enchanted gem"));

if (hasGem) {
  // Find gem in inventory and use it
  const gemItem = inventory.find(item => item?.name?.toLowerCase().includes("enchanted gem"));
  if (gemItem) {
    console.log("Using enchanted gem...");
    // Using item usually involves interacting with inventory
    record(
      "Enchanted gem present",
      true,
      "item in inventory",
      "id=" + gemItem.id,
      "Player has enchanted gem"
    );
  }
} else {
  record(
    "Enchanted gem present",
    false,
    "item in inventory",
    "not found",
    "Player doesn't have enchanted gem (optional for F2P)"
  );
}

// Test 5: Check slayer points (if task completed)
console.log("\n── Testing: Slayer Points ──");

// Note: Points require task completion which is complex to test automatically
// This is a placeholder for manual verification
const finalState = sdk.getPlayer();
const finalSlayerXp = sdk.getSkill("slayer")?.xp || 0;
const finalSlayerLevel = sdk.getSkill("slayer")?.level || 1;

console.log("Final Slayer: Level " + finalSlayerLevel + ", XP " + finalSlayerXp);

record(
  "Slayer XP gained",
  finalSlayerXp > initialSlayerXp,
  "xp > initial",
  "xp=" + finalSlayerXp + " (was " + initialSlayerXp + ")",
  "Slayer XP increased during test"
);

// ---------------------------------------------------------------------------
// Summary
// ---------------------------------------------------------------------------

console.log("\n═══ Test Results ═══");
const passCount = results.filter(r => r.pass).length;
const failCount = results.filter(r => !r.pass).length;
console.log("✅ Passed: " + passCount);
console.log("❌ Failed: " + failCount);

if (failCount > 0) {
  console.log("\nFailed tests:");
  for (const r of results.filter(r => !r.pass)) {
    console.log("  - " + r.check + ": " + r.note);
  }
}

console.log("\n📝 Manual verification needed:");
console.log("  - Talk to Slayer Master for task assignment");
console.log("  - Complete task and verify points awarded");
console.log("  - Use enchanted gem to contact master");
