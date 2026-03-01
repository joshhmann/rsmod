/**
 * bots/agility.ts — Agility skill test
 *
 * Tests the Gnome Stronghold Agility Course with all 7 obstacles + completion bonus.
 *
 * Run via MCP execute_script tool. Globals available: bot, sdk
 */

// ---------------------------------------------------------------------------
// Cache-verified data (rev 228)
// Object IDs from wiki-data/skills/agility.json
// ---------------------------------------------------------------------------

const OBSTACLE_LOG_BALANCE = 23145;
const OBSTACLE_NET_1 = 23134;
const OBSTACLE_TREE_UP = 23559;
const OBSTACLE_ROPE = 23135;
const OBSTACLE_TREE_DOWN = 23560;
const OBSTACLE_NET_2 = 23136;
const OBSTACLE_PIPE = 23137;
const OBSTACLE_PIPE_EXIT = 23138;

// Animations
const ANIM_LOG_BALANCE = 762;
const ANIM_CLIMB = 828;
const ANIM_PIPE = 844;

// XP values from wiki-data
const XP_LOG_BALANCE = 10.0;
const XP_NET_1 = 10.0;
const XP_TREE_UP = 6.5;
const XP_ROPE = 10.0;
const XP_TREE_DOWN = 6.5;
const XP_NET_2 = 10.0;
const XP_PIPE = 7.5;
const XP_COMPLETION_BONUS = 50.0;
const XP_TOTAL_PER_LAP = 110.5;

// Test locations
const TEST_LOCATIONS = {
    course_start: { x: 2474, z: 3436, desc: "Log Balance (Course Start)" },
    course_finish: { x: 2485, z: 3430, desc: "Course Finish" },
    bank: { x: 2442, z: 3485, desc: "Grand Tree Bank" },
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

// Test 1: Complete full lap
async function testFullLap() {
  console.log("\n── Testing: Full Agility Course Lap ──");

  // Teleport to course start
  sdk.sendTeleport(TEST_LOCATIONS.course_start.x, TEST_LOCATIONS.course_start.z, 0);
  await sdk.waitTicks(3);

  const xpBefore = sdk.getSkill("agility").xp;
  console.log("Starting XP:", xpBefore);

  // Obstacle 1: Log Balance
  await testObstacle("Log Balance", OBSTACLE_LOG_BALANCE, ANIM_LOG_BALANCE, XP_LOG_BALANCE, 1);

  // Obstacle 2: Obstacle Net
  await testObstacle("Obstacle Net 1", OBSTACLE_NET_1, ANIM_CLIMB, XP_NET_1, 2);

  // Obstacle 3: Tree Branch Up
  await testObstacle("Tree Branch Up", OBSTACLE_TREE_UP, ANIM_CLIMB, XP_TREE_UP, 3);

  // Obstacle 4: Balancing Rope
  await testObstacle("Balancing Rope", OBSTACLE_ROPE, ANIM_LOG_BALANCE, XP_ROPE, 4);

  // Obstacle 5: Tree Branch Down
  await testObstacle("Tree Branch Down", OBSTACLE_TREE_DOWN, ANIM_CLIMB, XP_TREE_DOWN, 5);

  // Obstacle 6: Obstacle Net (second)
  await testObstacle("Obstacle Net 2", OBSTACLE_NET_2, ANIM_CLIMB, XP_NET_2, 6);

  // Obstacle 7: Obstacle Pipe
  await testObstacle("Obstacle Pipe", OBSTACLE_PIPE, ANIM_PIPE, XP_PIPE, 7);

  // Course completion
  await testCourseCompletion();

  // Verify total XP gained
  const xpAfter = sdk.getSkill("agility").xp;
  const xpGained = xpAfter - xpBefore;
  const totalXPPass = Math.abs(xpGained - XP_TOTAL_PER_LAP) < 0.1;
  record("Full Lap", "total_xp", totalXPPass, XP_TOTAL_PER_LAP, xpGained, 
    totalXPPass ? "Total XP correct" : "XP mismatch");
}

// Helper: Test individual obstacle
async function testObstacle(name, locId, expectedAnim, expectedXP, obstacleNum) {
  console.log("  Testing obstacle: " + name);

  const xpBefore = sdk.getSkill("agility").xp;

  // Find and interact with obstacle
  const obstacle = sdk.findNearbyLoc(name.toLowerCase().replace(/ /g, "_"));
  if (!obstacle) {
    record(name, "loc_found", false, locId, "not found", "Obstacle not found");
    return;
  }
  record(name, "loc_found", true, locId, obstacle.id, "Found obstacle");

  // Interact
  sdk.sendInteractLoc(obstacle.id, obstacle.x, obstacle.z, 1);

  let animSeen = 0;
  let xpGained = 0;
  let elapsed = 0;

  // Wait for animation and XP
  while (elapsed < 10000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;
    if (p.animation !== 0 && p.animation !== 65535 && animSeen === 0) {
      animSeen = p.animation;
    }
    const delta = p.skills.agility.xp - xpBefore;
    if (delta > 0) {
      xpGained = delta;
      break;
    }
  }

  // Animation check
  const animPass = animSeen === expectedAnim;
  record(name, "animation", animPass, expectedAnim, animSeen,
    animPass ? "Animation correct" : (animSeen === 0 ? "No anim seen" : "Wrong animation"));

  // XP check
  const xpPass = Math.abs(xpGained - expectedXP) < 0.1;
  record(name, "xp_grant", xpPass, expectedXP, xpGained,
    xpPass ? "XP granted correctly" : "XP mismatch");
}

// Helper: Test course completion
async function testCourseCompletion() {
  console.log("  Testing: Course Completion");

  const xpBefore = sdk.getSkill("agility").xp;

  // Find pipe exit
  const exit = sdk.findNearbyLoc("obstacle_pipe_exit");
  if (!exit) {
    record("Completion", "exit_found", false, OBSTACLE_PIPE_EXIT, "not found", "Pipe exit not found");
    return;
  }

  // Interact with exit
  sdk.sendInteractLoc(exit.id, exit.x, exit.z, 1);

  let xpGained = 0;
  let elapsed = 0;
  while (elapsed < 10000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    const p = sdk.getPlayer();
    if (!p) continue;
    const delta = p.skills.agility.xp - xpBefore;
    if (delta > 0) {
      xpGained = delta;
      break;
    }
  }

  // Completion bonus XP check
  const xpPass = Math.abs(xpGained - XP_COMPLETION_BONUS) < 0.1;
  record("Completion", "bonus_xp", xpPass, XP_COMPLETION_BONUS, xpGained,
    xpPass ? "Completion bonus granted" : "Bonus XP mismatch");
}

// Test 2: Out of order obstacle (should fail)
async function testOutOfOrder() {
  console.log("\n── Testing: Out of Order Attempt ──");

  // Teleport to course start
  sdk.sendTeleport(TEST_LOCATIONS.course_start.x, TEST_LOCATIONS.course_start.z, 0);
  await sdk.waitTicks(3);

  // Try to start at obstacle 3 without doing 1 and 2
  const treeUp = sdk.findNearbyLoc("tree_branch_up");
  if (treeUp) {
    const xpBefore = sdk.getSkill("agility").xp;
    sdk.sendInteractLoc(treeUp.id, treeUp.x, treeUp.z, 1);

    await sdk.waitTicks(5);
    const xpAfter = sdk.getSkill("agility").xp;

    // Should NOT get XP if out of order
    const noXPPass = xpAfter === xpBefore;
    record("Out of Order", "no_xp", noXPPass, 0, xpAfter - xpBefore,
      noXPPass ? "Correctly blocked" : "Should not grant XP");
  } else {
    record("Out of Order", "tree_found", false, OBSTACLE_TREE_UP, "not found", "Tree obstacle not found");
  }
}

// Run all tests
await testFullLap();
await testOutOfOrder();

// ---------------------------------------------------------------------------
// Summary
// ---------------------------------------------------------------------------

const passed = results.filter(r => r.pass).length;
const failed = results.filter(r => !r.pass).length;
console.log("\n══ Agility Test Summary: " + passed + " passed, " + failed + " failed ══");
if (failed > 0) {
  console.log("FAILURES:");
  for (const r of results.filter(r => !r.pass)) {
    console.log("  ❌ [" + r.test + "] " + r.check + ": expected=" + JSON.stringify(r.expected) + ", actual=" + JSON.stringify(r.actual) + " — " + r.note);
  }
}

({ summary: { passed, failed }, results });
