/**
 * bots/combat.ts — AgentBridge combat smoke test
 *
 * Validates AGENTBRIDGE-6 basics:
 * - enhanced nearby NPC snapshot fields (hp/maxHp/inCombat/targetIndex)
 * - combat state snapshot (`player.combat`)
 * - attack interaction flow starts combat
 */

const TEST_SPOT = { x: 3222, z: 3296, plane: 0 }; // Lumbridge chickens
const TARGET_NAMES = ["chicken", "goblin", "cow"];

function isNumber(value) {
  return typeof value === "number" && Number.isFinite(value);
}

sdk.sendTeleport(TEST_SPOT.x, TEST_SPOT.z, TEST_SPOT.plane);
await sdk.waitTicks(4);

const state0 = sdk.getState();
if (!state0) {
  throw new Error("No bridge state available.");
}

const nearby = sdk.findNearbyNpcs() ?? [];
if (nearby.length === 0) {
  throw new Error("No nearby NPCs found at test location.");
}

const target =
  nearby.find((npc) =>
    TARGET_NAMES.some((name) => npc.name.toLowerCase().includes(name))
  ) ?? nearby[0];

const npcShapeOk =
  isNumber(target.hp) &&
  isNumber(target.maxHp) &&
  typeof target.inCombat === "boolean" &&
  isNumber(target.targetIndex);

if (!npcShapeOk) {
  throw new Error(
    `NearbyNpc snapshot missing combat fields for ${target.name} (index=${target.index}).`
  );
}

sdk.sendInteractNpc(target.index, 2);

let started = false;
for (let i = 0; i < 20; i++) {
  await sdk.waitTicks(1);
  const st = sdk.getState();
  const combat = st?.player?.combat;
  if (combat && combat.inCombat === true) {
    started = true;
    break;
  }
}

if (!started) {
  throw new Error("Combat did not start within timeout.");
}

const finalState = sdk.getState();
const combat = finalState?.player?.combat;
if (!combat || typeof combat.inCombat !== "boolean") {
  throw new Error("Player combat snapshot missing or malformed.");
}

console.log("Combat smoke test passed.");
console.log({
  target: { name: target.name, index: target.index },
  combat,
});

({
  ok: true,
  target: { name: target.name, index: target.index },
  combat,
});
