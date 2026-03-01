/**
 * bots/farming.ts — Farming herb-patch baseline smoke test.
 *
 * Preconditions:
 * - Player has rake, seed dibber, spade, guam_seed, and plant_cure.
 * - Player can teleport to Catherby.
 */

const CATHERBY_PATCH = { x: 2813, z: 3462, plane: 0 };
const PATCH_IDS = {
  weeds3: 8135,
  weeds2: 8134,
  weeds1: 8133,
  weeded: 8132,
  guamSeed: 14210,
  guam1: 26826,
  guam2: 26827,
  guam3: 26828,
  guamFull: 26829,
  diseased: 8144,
  dead: 8147,
};
const ITEMS = {
  rake: 5341,
  seedDibber: 5343,
  spade: 952,
  guamSeed: 5291,
  plantCure: 6036,
  grimyGuam: 199,
};

function invCount(id) {
  return (sdk.getInventory() || [])
    .filter((it) => it.id === id)
    .reduce((sum, it) => sum + it.qty, 0);
}

function nearbyPatch() {
  const state = sdk.getState();
  const locs = state?.player?.nearbyLocs || [];
  return locs.find((l) => Object.values(PATCH_IDS).includes(l.id));
}

async function waitForPatch(ids, timeoutTicks = 200) {
  for (let i = 0; i < timeoutTicks; i++) {
    const patch = nearbyPatch();
    if (patch && ids.includes(patch.id)) {
      return patch;
    }
    await sdk.waitTicks(1);
  }
  return null;
}

for (const [name, id] of Object.entries(ITEMS)) {
  if ((name === "grimyGuam")) continue;
  if (invCount(id) <= 0) {
    throw new Error(`Missing required item '${name}' (id=${id}).`);
  }
}

const xpBefore = sdk.getSkill("farming").xp;
const herbsBefore = invCount(ITEMS.grimyGuam);

sdk.sendTeleport(CATHERBY_PATCH.x, CATHERBY_PATCH.z, CATHERBY_PATCH.plane);
await sdk.waitTicks(3);

let patch = await waitForPatch([PATCH_IDS.weeds3, PATCH_IDS.weeds2, PATCH_IDS.weeds1, PATCH_IDS.weeded], 40);
if (!patch) {
  throw new Error("Could not find a herb patch nearby.");
}

while ([PATCH_IDS.weeds3, PATCH_IDS.weeds2, PATCH_IDS.weeds1].includes(patch.id)) {
  sdk.sendInteractLoc(patch.id, patch.x, patch.z, 4, ITEMS.rake);
  await sdk.waitTicks(2);
  patch = await waitForPatch([PATCH_IDS.weeds3, PATCH_IDS.weeds2, PATCH_IDS.weeds1, PATCH_IDS.weeded], 20);
  if (!patch) {
    throw new Error("Patch disappeared while weeding.");
  }
}

if (patch.id !== PATCH_IDS.weeded) {
  throw new Error(`Expected weeded patch (${PATCH_IDS.weeded}), got ${patch.id}.`);
}

sdk.sendInteractLoc(patch.id, patch.x, patch.z, 4, ITEMS.guamSeed);
await sdk.waitTicks(2);

const planted = await waitForPatch([PATCH_IDS.guamSeed, PATCH_IDS.diseased], 40);
if (!planted) {
  throw new Error("Planting did not update patch state.");
}

console.log(`Planted patch state id=${planted.id}. Waiting for growth/disease...`);

const progressed =
  await waitForPatch(
    [PATCH_IDS.guam1, PATCH_IDS.guam2, PATCH_IDS.guam3, PATCH_IDS.guamFull, PATCH_IDS.diseased, PATCH_IDS.dead],
    220
  );
if (!progressed) {
  throw new Error("Patch did not progress to growth/disease/dead stage in time.");
}

if (progressed.id === PATCH_IDS.diseased) {
  sdk.sendInteractLoc(progressed.id, progressed.x, progressed.z, 4, ITEMS.plantCure);
  await sdk.waitTicks(3);
}

let finalPatch = await waitForPatch([PATCH_IDS.guamFull, PATCH_IDS.dead], 260);
if (!finalPatch) {
  throw new Error("Patch did not reach harvestable or dead state.");
}

if (finalPatch.id === PATCH_IDS.dead) {
  sdk.sendInteractLoc(finalPatch.id, finalPatch.x, finalPatch.z, 4, ITEMS.spade);
  await sdk.waitTicks(3);
  throw new Error("Patch died before harvest. Dead patch clear confirmed with spade.");
}

sdk.sendInteractLoc(finalPatch.id, finalPatch.x, finalPatch.z, 1);
await sdk.waitTicks(3);

const xpAfter = sdk.getSkill("farming").xp;
const herbsAfter = invCount(ITEMS.grimyGuam);

const xpGained = xpAfter - xpBefore;
const herbsGained = herbsAfter - herbsBefore;

if (xpGained <= 0) {
  throw new Error(`No farming XP gained (before=${xpBefore}, after=${xpAfter}).`);
}
if (herbsGained <= 0) {
  throw new Error("Harvest did not add grimy guam.");
}

console.log("Farming baseline smoke test passed.");
console.log({ xpGained, herbsGained });
({ ok: true, xpGained, herbsGained });
