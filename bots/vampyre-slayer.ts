/**
 * bots/vampyre-slayer.ts — Vampyre Slayer combat smoke test
 *
 * Verifies Count Draynor encounter plumbing:
 * - nearby NPC snapshot includes combat fields
 * - combat starts via NPC attack interaction
 * - quest-required items can be provided to test account
 */

const COUNT_BASEMENT = { x: 3115, z: 3356, plane: 0 };

function wait(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

sdk.sendTeleport(COUNT_BASEMENT.x, COUNT_BASEMENT.z, COUNT_BASEMENT.plane);
await sdk.waitTicks(3);

sdk.sendSpawnItem(1549, 1); // stake
sdk.sendSpawnItem(1550, 1); // garlic
await sdk.waitTicks(1);

const nearby = sdk.findNearbyNpcs() ?? [];
const count = nearby.find((n) => /count draynor/i.test(n.name));
if (!count) {
  throw new Error("Count Draynor not found near basement test location.");
}

if (
  typeof count.hp !== "number" ||
  typeof count.maxHp !== "number" ||
  typeof count.inCombat !== "boolean"
) {
  throw new Error("Count snapshot missing expected combat fields.");
}

sdk.sendInteractNpc(count.index, 2); // Attack

let combatStarted = false;
for (let i = 0; i < 20; i++) {
  await sdk.waitTicks(1);
  const st = sdk.getState();
  const combat = st?.player?.combat;
  if (combat && combat.inCombat === true) {
    combatStarted = true;
    break;
  }
}

if (!combatStarted) {
  throw new Error("Combat did not start against Count Draynor.");
}

const after = sdk.findNearbyNpcs()?.find((n) => n.index === count.index);
console.log("Vampyre Slayer smoke test passed.");
console.log({
  count: {
    index: count.index,
    hpBefore: count.hp,
    hpAfter: after?.hp ?? null,
    inCombat: after?.inCombat ?? null,
  },
});

({
  ok: true,
  countIndex: count.index,
  hpBefore: count.hp,
  hpAfter: after?.hp ?? null,
});
