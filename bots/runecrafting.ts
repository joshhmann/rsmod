/**
 * bots/runecrafting.ts — F2P Runecrafting smoke test
 *
 * Preconditions:
 * - Rune Mysteries completed.
 * - Inventory contains rune essence (`blankrune` id 1436) and an air talisman (id 1438).
 *
 * This script validates the Air altar flow:
 * - Use talisman on mysterious ruins to enter.
 * - Use rune essence on altar.
 * - Verify Runecrafting XP and air rune count increase.
 */

const AIR_RUINS = { id: 1884, x: 3127, z: 3405 };
const ALTAR_INSIDE = { id: 1887, x: 2841, z: 4829 };

const RUNE_ESSENCE_ID = 1436; // blankrune
const AIR_TALISMAN_ID = 1438; // air_talisman
const AIR_RUNE_ID = 556; // airrune

function invCount(id) {
  return sdk.getInventory()
    .filter((it) => it.id === id)
    .reduce((sum, it) => sum + it.qty, 0);
}

sdk.sendTeleport(3126, 3404, 0);
await sdk.waitTicks(3);

const talismanBefore = invCount(AIR_TALISMAN_ID);
const essenceBefore = invCount(RUNE_ESSENCE_ID);
const runeBefore = invCount(AIR_RUNE_ID);
const xpBefore = sdk.getSkill("runecrafting").xp;

if (talismanBefore <= 0) {
  throw new Error("Missing air talisman (id 1438).");
}
if (essenceBefore <= 0) {
  throw new Error("Missing rune essence (id 1436).");
}

sdk.sendInteractLoc(AIR_RUINS.id, AIR_RUINS.x, AIR_RUINS.z, 4, AIR_TALISMAN_ID);
await sdk.waitTicks(4);

const inside = sdk.getPlayer();
if (!inside || inside.z < 4700) {
  throw new Error("Failed to enter altar interior.");
}

sdk.sendInteractLoc(ALTAR_INSIDE.id, ALTAR_INSIDE.x, ALTAR_INSIDE.z, 4, RUNE_ESSENCE_ID);
await sdk.waitTicks(4);

const xpAfter = sdk.getSkill("runecrafting").xp;
const essenceAfter = invCount(RUNE_ESSENCE_ID);
const runeAfter = invCount(AIR_RUNE_ID);

const xpGained = xpAfter - xpBefore;
const essenceUsed = essenceBefore - essenceAfter;
const runesGained = runeAfter - runeBefore;

if (xpGained <= 0) {
  throw new Error(`No Runecrafting XP gained (before=${xpBefore}, after=${xpAfter}).`);
}
if (essenceUsed <= 0) {
  throw new Error("Rune essence was not consumed.");
}
if (runesGained <= 0) {
  throw new Error("No air runes were produced.");
}

console.log("Runecrafting smoke test passed.");
console.log({ xpGained, essenceUsed, runesGained });

({ ok: true, xpGained, essenceUsed, runesGained });
