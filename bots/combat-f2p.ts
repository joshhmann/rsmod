/**
 * bots/combat-f2p.ts — F2P Combat Training Test
 *
 * Tests F2P combat training progression:
 * - Low level: Chickens (level 1, Lumbridge)
 * - Low level: Cows (level 2, Lumbridge)  
 * - Medium level: Goblins (level 2, Lumbridge)
 * - High level: Hill Giants (level 28, Edgeville Dungeon)
 *
 * Verifies:
 * - Combat initiates and XP is gained
 * - Drops are collected (bones, cowhide, big bones)
 * - Combat state transitions correctly
 */

// Test targets with verified locations
const COMBAT_TARGETS = [
  {
    name: "Chicken",
    level: 1,
    hp: 3,
    npcName: "chicken",
    loc: { x: 3232, z: 3295, plane: 0 },
    drops: [
      { id: 526, name: "Bones", guaranteed: true },
    ],
  },
  {
    name: "Cow", 
    level: 2,
    hp: 8,
    npcName: "cow",
    loc: { x: 3254, z: 3267, plane: 0 },
    drops: [
      { id: 526, name: "Bones", guaranteed: true },
      { id: 1739, name: "Cowhide", guaranteed: true },
    ],
  },
  {
    name: "Goblin",
    level: 2,
    hp: 5,
    npcName: "goblin", 
    loc: { x: 3247, z: 3245, plane: 0 },
    drops: [
      { id: 526, name: "Bones", guaranteed: true },
    ],
  },
  {
    name: "Hill Giant",
    level: 28,
    hp: 35,
    npcName: "hill giant",
    loc: { x: 3118, z: 9853, plane: 0 },
    drops: [
      { id: 532, name: "Big bones", guaranteed: true },
    ],
  },
];

const results = [];

function record(target, check, pass, expected, actual, note) {
  results.push({ target, check, pass, expected, actual, note });
  const icon = pass ? "✅" : "❌";
  console.log(icon + " [" + target + "] " + check + ": expected=" + JSON.stringify(expected) + ", actual=" + JSON.stringify(actual) + " — " + note);
}

for (const target of COMBAT_TARGETS) {
  console.log("\n── Testing: " + target.name + " (level " + target.level + ") ──");

  sdk.sendTeleport(target.loc.x, target.loc.z, target.loc.plane);
  await sdk.waitTicks(4);

  const nearbyNpcs = sdk.findNearbyNpcs() ?? [];
  const npc = nearbyNpcs.find((n) => n.name.toLowerCase().includes(target.npcName.toLowerCase()));

  if (!npc) {
    record(target.name, "npc_found", false, target.npcName, "not found", "No " + target.npcName + " found nearby");
    continue;
  }

  record(target.name, "npc_found", true, target.npcName, npc.name, "id=" + npc.id + " hp=" + npc.hp + "/" + npc.maxHp);

  const xpBefore = sdk.getPlayer()?.skills?.attack?.xp || 0;
  const hpBefore = sdk.getPlayer()?.skills?.hitpoints?.xp || 0;
  const invBefore = sdk.getInventory().map((i) => ({ id: i.id, qty: i.qty }));

  sdk.sendInteractNpc(npc.index, 1);

  let combatStarted = false;
  let elapsed = 0;
  while (elapsed < 10000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    if (sdk.getState()?.player?.combat?.inCombat) {
      combatStarted = true;
      break;
    }
  }

  record(target.name, "combat_start", combatStarted, true, combatStarted, combatStarted ? "Combat started" : "Timeout");

  if (!combatStarted) continue;

  let xpGained = 0;
  let hpXpGained = 0;
  elapsed = 0;
  
  while (elapsed < 60000) {
    await sdk.waitTicks(1);
    elapsed += 600;
    
    const st = sdk.getState();
    xpGained = (st?.player?.skills?.attack?.xp || 0) - xpBefore;
    hpXpGained = (st?.player?.skills?.hitpoints?.xp || 0) - hpBefore;
    
    const stillNearby = (sdk.findNearbyNpcs() ?? []).find((n) => n.index === npc.index);
    if (!stillNearby || stillNearby.hp === 0 || xpGained > 0) break;
  }

  const xpPass = xpGained > 0 || hpXpGained > 0;
  record(target.name, "xp_gain", xpPass, ">0", { attack: xpGained, hp: hpXpGained }, xpPass ? "XP gained" : "No XP");

  await sdk.waitTicks(2);
  const invAfter = sdk.getInventory();

  for (const drop of target.drops) {
    if (drop.guaranteed) {
      const found = invAfter.find((i) => i.id === drop.id);
      const hadBefore = invBefore.find((i) => i.id === drop.id);
      const gained = (found?.qty || 0) - (hadBefore?.qty || 0);
      record(target.name, "drop_" + drop.name, gained > 0, "+1", gained > 0 ? "+" + gained : "none", gained > 0 ? "Collected" : "Missing");
    }
  }
}

const passed = results.filter((r) => r.pass).length;
const failed = results.filter((r) => !r.pass).length;
console.log("\n══ F2P Combat Test: " + passed + " passed, " + failed + " failed ══");

({ summary: { passed, failed }, results });
