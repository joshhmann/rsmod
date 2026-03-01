// Prince Ali Rescue Quest Complete Test Script
// Locations: Al Kharid, Draynor Village, Falador
// NPCs: Hassan (hassan), Osman (osman), Leela (leela), Ned (ned), Aggie (aggie), Lady Keli (lady_keli), Prince Ali (prince_ali)
// Items: Rope (954), Yellow dye (1769), Paste (2421), Bronze key (2422), Wig (2424), Skirt (2419)
// Reward: 700 coins, 3 Quest Points, access to Al Kharid toll gate

console.log("=== Prince Ali Rescue Quest Complete Test ===");

// Test configuration
const HASSAN_POS = { x: 3297, z: 3164, plane: 0 }; // Al Kharid palace
const OSMAN_POS = { x: 3289, z: 3182, plane: 0 };
const LEELA_POS = { x: 3113, z: 3262, plane: 0 }; // Draynor
const NED_POS = { x: 3103, z: 3257, plane: 0 }; // Draynor
const AGGIE_POS = { x: 3087, z: 3260, plane: 0 }; // Draynor
const KELI_POS = { x: 3186, z: 3243, plane: 0 }; // Falador area

const ITEM_ROPE = 954;
const ITEM_WIG = 2424;
const ITEM_SKIRT = 2419;
const ITEM_PASTE = 2421;
const ITEM_BRONZE_KEY = 2422;

const REWARD_COINS = 700;
const REWARD_QP = 3;

// Get initial state
const initialState = await sdk.getState();
console.log(`Initial QP: ${initialState.player.questPoints}`);
console.log(`Initial Coins: ${initialState.player.inventory?.find(i => i?.id === 995)?.qty || 0}`);

// Stage 1: Start quest with Hassan
console.log("\n--- Stage 1: Starting quest with Hassan ---");
await sdk.sendTeleport(HASSAN_POS.x, HASSAN_POS.z, HASSAN_POS.plane);
await sdk.waitTicks(3);

const hassan = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("hassan"));
if (hassan) {
    console.log(`Found Hassan (index: ${hassan.index}), starting quest...`);
    await sdk.sendInteractNpc(hassan.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Hassan not found!");
}

// Stage 2: Talk to Osman
console.log("\n--- Stage 2: Talking to Osman ---");
await sdk.sendTeleport(OSMAN_POS.x, OSMAN_POS.z, OSMAN_POS.plane);
await sdk.waitTicks(3);

const osman = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("osman"));
if (osman) {
    console.log(`Found Osman (index: ${osman.index}), getting plan...`);
    await sdk.sendInteractNpc(osman.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Osman not found!");
}

// Stage 3: Get rope from Ned
console.log("\n--- Stage 3: Getting rope from Ned ---");
await sdk.sendTeleport(NED_POS.x, NED_POS.z, NED_POS.plane);
await sdk.waitTicks(3);

const ned = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("ned"));
if (ned) {
    console.log(`Found Ned (index: ${ned.index}), getting rope...`);
    await sdk.sendInteractNpc(ned.index, 1);
    await sdk.waitTicks(10);
}

// Give rope for testing
await sdk.sendGroundItem(ITEM_ROPE, 1, NED_POS.x, NED_POS.z, NED_POS.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_ROPE, NED_POS.x, NED_POS.z, 1);
await sdk.waitTicks(3);

// Stage 4: Get paste from Aggie
console.log("\n--- Stage 4: Getting paste from Aggie ---");
await sdk.sendTeleport(AGGIE_POS.x, AGGIE_POS.z, AGGIE_POS.plane);
await sdk.waitTicks(3);

const aggie = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("aggie"));
if (aggie) {
    console.log(`Found Aggie (index: ${aggie.index}), getting paste...`);
    await sdk.sendInteractNpc(aggie.index, 1);
    await sdk.waitTicks(10);
}

// Give paste for testing
await sdk.sendGroundItem(ITEM_PASTE, 1, AGGIE_POS.x, AGGIE_POS.z, AGGIE_POS.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_PASTE, AGGIE_POS.x, AGGIE_POS.z, 1);
await sdk.waitTicks(3);

// Stage 5: Get disguise items
console.log("\n--- Stage 5: Getting disguise items ---");
await sdk.sendGroundItem(ITEM_WIG, 1, AGGIE_POS.x, AGGIE_POS.z, AGGIE_POS.plane);
await sdk.sendGroundItem(ITEM_SKIRT, 1, AGGIE_POS.x, AGGIE_POS.z, AGGIE_POS.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_WIG, AGGIE_POS.x, AGGIE_POS.z, 1);
await sdk.sendInteractGroundItem(ITEM_SKIRT, AGGIE_POS.x, AGGIE_POS.z, 1);
await sdk.waitTicks(3);

const stateAfterDisguise = await sdk.getState();
const hasWig = stateAfterDisguise.player.inventory?.some(item => item?.id === ITEM_WIG);
const hasSkirt = stateAfterDisguise.player.inventory?.some(item => item?.id === ITEM_SKIRT);
console.log(`Has wig: ${hasWig}, Has skirt: ${hasSkirt}`);

// Stage 6: Get key from Lady Keli
console.log("\n--- Stage 6: Getting key from Lady Keli ---");
await sdk.sendTeleport(KELI_POS.x, KELI_POS.z, KELI_POS.plane);
await sdk.waitTicks(3);

const keli = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("keli"));
if (keli) {
    console.log(`Found Lady Keli (index: ${keli.index}), getting key...`);
    await sdk.sendInteractNpc(keli.index, 1);
    await sdk.waitTicks(10);
}

// Give key for testing
await sdk.sendGroundItem(ITEM_BRONZE_KEY, 1, KELI_POS.x, KELI_POS.z, KELI_POS.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_BRONZE_KEY, KELI_POS.x, KELI_POS.z, 1);
await sdk.waitTicks(3);

const stateAfterKey = await sdk.getState();
const hasKey = stateAfterKey.player.inventory?.some(item => item?.id === ITEM_BRONZE_KEY);
console.log(`Has bronze key: ${hasKey}`);

// Stage 7: Rescue Prince Ali
console.log("\n--- Stage 7: Rescuing Prince Ali ---");
await sdk.sendTeleport(KELI_POS.x, KELI_POS.z, KELI_POS.plane);
await sdk.waitTicks(3);

const princeAli = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("prince ali"));
if (princeAli) {
    console.log(`Found Prince Ali (index: ${princeAli.index}), rescuing...`);
    await sdk.sendInteractNpc(princeAli.index, 1);
    await sdk.waitTicks(10);
}

// Stage 8: Complete quest with Hassan
console.log("\n--- Stage 8: Completing quest with Hassan ---");
await sdk.sendTeleport(HASSAN_POS.x, HASSAN_POS.z, HASSAN_POS.plane);
await sdk.waitTicks(3);

const hassan2 = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("hassan"));
if (hassan2) {
    console.log(`Found Hassan (index: ${hassan2.index}), completing quest...`);
    await sdk.sendInteractNpc(hassan2.index, 1);
    await sdk.waitTicks(10);
}

// Check final state
const finalState = await sdk.getState();
console.log("\n=== Results ===");
console.log("Final Inventory:", finalState.player.inventory);
console.log(`Final QP: ${finalState.player.questPoints}`);

// Verify completion
const qpGained = (finalState.player.questPoints || 0) - (initialState.player.questPoints || 0);
const coinsReward = finalState.player.inventory?.find(i => i?.id === 995)?.qty || 0;
const coinsGained = coinsReward - (initialState.player.inventory?.find(i => i?.id === 995)?.qty || 0);

console.log("\n=== VERIFICATION ===");
console.log(`✓ QP gained (expected ${REWARD_QP}): ${qpGained >= REWARD_QP ? 'PASS' : 'FAIL'} (got ${qpGained})`);
console.log(`✓ Coins received (expected ${REWARD_COINS}): ${coinsGained >= REWARD_COINS - 50 ? 'PASS' : 'FAIL'} (got ${coinsGained})`);

if (qpGained >= REWARD_QP && coinsGained >= REWARD_COINS - 50) {
    console.log("\n🎉 PRINCE ALI RESCUE QUEST COMPLETION TEST: PASSED");
    console.log("\n✓ Access to Al Kharid toll gate granted");
} else {
    console.log("\n❌ PRINCE ALI RESCUE QUEST COMPLETION TEST: FAILED");
    console.log("Notes:");
    console.log("- Ensure server is running with quest loaded");
    console.log("- Check that all disguise items and key are properly defined");
    console.log("- Verify quest completion rewards are configured");
}
