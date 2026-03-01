// Ernest the Chicken Quest Complete Test Script
// Locations: Draynor Manor (x: 3105-3130, z: 3355-3380)
// NPCs: Veronica (veronica), Professor Oddenstein (professor_oddenstein)
// Items: Rubber tube (1882), Pressure gauge (1881), Oil can (1880)
// Reward: 3000 GP, 4 Quest Points

console.log("=== Ernest the Chicken Quest Complete Test ===");

// Test configuration
const VERONICA_POS = { x: 3111, z: 3331, plane: 0 }; // Outside Draynor Manor
const MANOR_ENTRANCE = { x: 3118, z: 3354, plane: 0 };
const ODDENSTEIN_POS = { x: 3112, z: 3368, plane: 2 }; // Top floor
const BASEMENT_POS = { x: 3112, z: 3363, plane: -1 };

const ITEM_RUBBER_TUBE = 1882;
const ITEM_PRESSURE_GAUGE = 1881;
const ITEM_OIL_CAN = 1880;

const REWARD_COINS = 3000;
const REWARD_QP = 4;

// Get initial state
const initialState = await sdk.getState();
console.log(`Initial QP: ${initialState.player.questPoints}`);
console.log(`Initial Coins: ${initialState.player.inventory?.find(i => i?.id === 995)?.qty || 0}`);

// Stage 1: Start quest with Veronica
console.log("\n--- Stage 1: Starting quest with Veronica ---");
await sdk.sendTeleport(VERONICA_POS.x, VERONICA_POS.z, VERONICA_POS.plane);
await sdk.waitTicks(3);

const veronica = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("veronica"));
if (veronica) {
    console.log(`Found Veronica (index: ${veronica.index}), starting quest...`);
    await sdk.sendInteractNpc(veronica.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Veronica not found!");
}

// Stage 2: Enter Draynor Manor
console.log("\n--- Stage 2: Entering Draynor Manor ---");
await sdk.sendTeleport(MANOR_ENTRANCE.x, MANOR_ENTRANCE.z, MANOR_ENTRANCE.plane);
await sdk.waitTicks(3);
console.log("Entered Draynor Manor");

// Stage 3: Find Rubber Tube (hidden room)
console.log("\n--- Stage 3: Finding Rubber Tube ---");
await sdk.sendTeleport(BASEMENT_POS.x, BASEMENT_POS.z, BASEMENT_POS.plane);
await sdk.waitTicks(3);

// Give rubber tube for testing
await sdk.sendGroundItem(ITEM_RUBBER_TUBE, 1, BASEMENT_POS.x, BASEMENT_POS.z, BASEMENT_POS.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_RUBBER_TUBE, BASEMENT_POS.x, BASEMENT_POS.z, 1);
await sdk.waitTicks(3);

const stateAfterTube = await sdk.getState();
const hasTube = stateAfterTube.player.inventory?.some(item => item?.id === ITEM_RUBBER_TUBE);
console.log(`Has rubber tube: ${hasTube}`);

// Stage 4: Find Pressure Gauge (fountain)
console.log("\n--- Stage 4: Finding Pressure Gauge ---");
await sdk.sendTeleport(ODDENSTEIN_POS.x, ODDENSTEIN_POS.z, ODDENSTEIN_POS.plane);
await sdk.waitTicks(3);

// Give pressure gauge for testing
await sdk.sendGroundItem(ITEM_PRESSURE_GAUGE, 1, ODDENSTEIN_POS.x, ODDENSTEIN_POS.z, ODDENSTEIN_POS.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_PRESSURE_GAUGE, ODDENSTEIN_POS.x, ODDENSTEIN_POS.z, 1);
await sdk.waitTicks(3);

const stateAfterGauge = await sdk.getState();
const hasGauge = stateAfterGauge.player.inventory?.some(item => item?.id === ITEM_PRESSURE_GAUGE);
console.log(`Has pressure gauge: ${hasGauge}`);

// Stage 5: Find Oil Can (basement)
console.log("\n--- Stage 5: Finding Oil Can ---");
await sdk.sendTeleport(BASEMENT_POS.x, BASEMENT_POS.z, BASEMENT_POS.plane);
await sdk.waitTicks(3);

// Give oil can for testing
await sdk.sendGroundItem(ITEM_OIL_CAN, 1, BASEMENT_POS.x, BASEMENT_POS.z, BASEMENT_POS.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_OIL_CAN, BASEMENT_POS.x, BASEMENT_POS.z, 1);
await sdk.waitTicks(3);

const stateAfterOil = await sdk.getState();
const hasOil = stateAfterOil.player.inventory?.some(item => item?.id === ITEM_OIL_CAN);
console.log(`Has oil can: ${hasOil}`);

// Stage 6: Complete quest with Professor Oddenstein
console.log("\n--- Stage 6: Completing quest with Professor Oddenstein ---");
await sdk.sendTeleport(ODDENSTEIN_POS.x, ODDENSTEIN_POS.z, ODDENSTEIN_POS.plane);
await sdk.waitTicks(3);

const oddenstein = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("oddenstein"));
if (oddenstein) {
    console.log(`Found Professor Oddenstein (index: ${oddenstein.index}), completing quest...`);
    await sdk.sendInteractNpc(oddenstein.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Professor Oddenstein not found!");
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
console.log(`✓ Coins received (expected ${REWARD_COINS}): ${coinsGained >= REWARD_COINS - 100 ? 'PASS' : 'FAIL'} (got ${coinsGained})`);

if (qpGained >= REWARD_QP && coinsGained >= REWARD_COINS - 100) {
    console.log("\n🎉 ERNEST THE CHICKEN QUEST COMPLETION TEST: PASSED");
} else {
    console.log("\n❌ ERNEST THE CHICKEN QUEST COMPLETION TEST: FAILED");
    console.log("Notes:");
    console.log("- Ensure server is running with quest loaded");
    console.log("- Check that all 3 machine parts are properly defined");
    console.log("- Verify quest completion rewards are configured");
}
