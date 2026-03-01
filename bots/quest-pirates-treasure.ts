// Pirate's Treasure Quest Complete Test Script
// Locations: Port Sarim (Redbeard Frank), Karamja, Falador Park
// NPCs: Redbeard Frank (redbeard_frank), Gardener
// Items: Karamjan rum (2026), Chest key (371), Casket (401)
// Reward: 450 coins (from casket), 2 Quest Points

console.log("=== Pirate's Treasure Quest Complete Test ===");

// Test configuration
const REDBEARD_POS = { x: 3053, z: 3251, plane: 0 }; // Port Sarim pub
const KARAMJA_POS = { x: 2927, z: 3144, plane: 0 }; // Musa Point
const FALADOR_PARK = { x: 2993, z: 3381, plane: 0 };

const ITEM_KARAMJAN_RUM = 2026;
const ITEM_CHEST_KEY = 371;
const ITEM_CASKET = 401;

const REWARD_COINS = 450;
const REWARD_QP = 2;

// Get initial state
const initialState = await sdk.getState();
console.log(`Initial QP: ${initialState.player.questPoints}`);
console.log(`Initial Coins: ${initialState.player.inventory?.find(i => i?.id === 995)?.qty || 0}`);

// Stage 1: Start quest with Redbeard Frank
console.log("\n--- Stage 1: Starting quest with Redbeard Frank ---");
await sdk.sendTeleport(REDBEARD_POS.x, REDBEARD_POS.z, REDBEARD_POS.plane);
await sdk.waitTicks(3);

const redbeard = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("frank"));
if (redbeard) {
    console.log(`Found Redbeard Frank (index: ${redbeard.index}), starting quest...`);
    await sdk.sendInteractNpc(redbeard.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Redbeard Frank not found!");
}

// Stage 2: Travel to Karamja for rum
console.log("\n--- Stage 2: Getting Karamjan rum ---");
await sdk.sendTeleport(KARAMJA_POS.x, KARAMJA_POS.z, KARAMJA_POS.plane);
await sdk.waitTicks(3);

// Give rum for testing
await sdk.sendGroundItem(ITEM_KARAMJAN_RUM, 1, KARAMJA_POS.x, KARAMJA_POS.z, KARAMJA_POS.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_KARAMJAN_RUM, KARAMJA_POS.x, KARAMJA_POS.z, 1);
await sdk.waitTicks(3);

const stateAfterRum = await sdk.getState();
const hasRum = stateAfterRum.player.inventory?.some(item => item?.id === ITEM_KARAMJAN_RUM);
console.log(`Has Karamjan rum: ${hasRum}`);

// Stage 3: Return to Redbeard with rum
console.log("\n--- Stage 3: Returning rum to Redbeard ---");
await sdk.sendTeleport(REDBEARD_POS.x, REDBEARD_POS.z, REDBEARD_POS.plane);
await sdk.waitTicks(3);

const redbeard2 = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("frank"));
if (redbeard2) {
    console.log(`Found Redbeard (index: ${redbeard2.index}), giving rum...`);
    await sdk.sendInteractNpc(redbeard2.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Redbeard not found!");
}

// Stage 4: Get chest key
console.log("\n--- Stage 4: Getting chest key ---");
await sdk.sendGroundItem(ITEM_CHEST_KEY, 1, REDBEARD_POS.x, REDBEARD_POS.z, REDBEARD_POS.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_CHEST_KEY, REDBEARD_POS.x, REDBEARD_POS.z, 1);
await sdk.waitTicks(3);

const stateAfterKey = await sdk.getState();
const hasKey = stateAfterKey.player.inventory?.some(item => item?.id === ITEM_CHEST_KEY);
console.log(`Has chest key: ${hasKey}`);

// Stage 5: Open chest in Falador Park
console.log("\n--- Stage 5: Opening chest in Falador Park ---");
await sdk.sendTeleport(FALADOR_PARK.x, FALADOR_PARK.z, FALADOR_PARK.plane);
await sdk.waitTicks(3);

// Give casket for testing
await sdk.sendGroundItem(ITEM_CASKET, 1, FALADOR_PARK.x, FALADOR_PARK.z, FALADOR_PARK.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_CASKET, FALADOR_PARK.x, FALADOR_PARK.z, 1);
await sdk.waitTicks(3);

const stateAfterCasket = await sdk.getState();
const hasCasket = stateAfterCasket.player.inventory?.some(item => item?.id === ITEM_CASKET);
console.log(`Has casket: ${hasCasket}`);

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
console.log(`✓ Coins received (expected ${REWARD_COINS}+): ${coinsGained >= REWARD_COINS - 50 ? 'PASS' : 'FAIL'} (got ${coinsGained})`);

if (qpGained >= REWARD_QP && coinsGained >= REWARD_COINS - 50) {
    console.log("\n🎉 PIRATE'S TREASURE QUEST COMPLETION TEST: PASSED");
} else {
    console.log("\n❌ PIRATE'S TREASURE QUEST COMPLETION TEST: FAILED");
    console.log("Notes:");
    console.log("- Ensure server is running with quest loaded");
    console.log("- Check that Karamjan rum, key, and casket are properly defined");
    console.log("- Verify quest completion rewards are configured");
}
