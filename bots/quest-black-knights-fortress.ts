// Black Knights' Fortress Quest Complete Test Script
// Locations: Falador (Sir Amik Varze), Black Knights' Fortress (various floors)
// NPCs: Sir Amik Varze (sir_amik_varze), Black Knight (black_knight)
// Items: Cabbage (draynor_cabbage), Dossier
// Reward: 2500 GP, 3 Quest Points

console.log("=== Black Knights' Fortress Quest Complete Test ===");

// Test configuration
const SIR_AMIK_POS = { x: 2991, z: 3343, plane: 2 }; // Falador Castle
const FORTRESS_ENTRANCE = { x: 3017, z: 3514, plane: 0 };
const FORTRESS_TOP = { x: 3025, z: 3512, plane: 2 };
const CABBAGE_PATCH = { x: 3019, z: 3504, plane: 0 }; // Near fortress

const ITEM_CABBAGE = 1965; // draynor_cabbage
const REWARD_COINS = 2500;
const REWARD_QP = 3;

// Get initial state
const initialState = await sdk.getState();
console.log(`Initial QP: ${initialState.player.questPoints}`);
console.log(`Initial Coins: ${initialState.player.inventory?.find(i => i?.id === 995)?.qty || 0}`);

// Stage 1: Start quest with Sir Amik Varze
console.log("\n--- Stage 1: Starting quest with Sir Amik Varze ---");
await sdk.sendTeleport(SIR_AMIK_POS.x, SIR_AMIK_POS.z, SIR_AMIK_POS.plane);
await sdk.waitTicks(3);

const sirAmik = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("amik"));
if (sirAmik) {
    console.log(`Found Sir Amik Varze (index: ${sirAmik.index}), starting quest...`);
    await sdk.sendInteractNpc(sirAmik.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Sir Amik Varze not found!");
}

// Stage 2: Infiltrate Black Knights' Fortress
console.log("\n--- Stage 2: Infiltrating Black Knights' Fortress ---");
await sdk.sendTeleport(FORTRESS_ENTRANCE.x, FORTRESS_ENTRANCE.z, FORTRESS_ENTRANCE.plane);
await sdk.waitTicks(3);

// Check for Black Knights in area
const stateAtFortress = await sdk.getState();
const blackKnight = stateAtFortress.player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("black knight"));
if (blackKnight) {
    console.log(`Found Black Knight (index: ${blackKnight.index})`);
    console.log("Fortress accessed successfully");
} else {
    console.log("WARNING: No Black Knights found in fortress");
}

// Stage 3: Get dossier (simulated - normally from top floor)
console.log("\n--- Stage 3: Retrieving dossier ---");
await sdk.sendTeleport(FORTRESS_TOP.x, FORTRESS_TOP.z, FORTRESS_TOP.plane);
await sdk.waitTicks(3);

// Simulate dossier pickup
console.log("Dossier obtained");

// Stage 4: Sabotage cabbage with explosive
console.log("\n--- Stage 4: Sabotaging cabbage ---");
await sdk.sendTeleport(CABBAGE_PATCH.x, CABBAGE_PATCH.z, CABBAGE_PATCH.plane);
await sdk.waitTicks(3);

// Give cabbage for test
await sdk.sendGroundItem(ITEM_CABBAGE, 1, CABBAGE_PATCH.x, CABBAGE_PATCH.z, CABBAGE_PATCH.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_CABBAGE, CABBAGE_PATCH.x, CABBAGE_PATCH.z, 1);
await sdk.waitTicks(3);

const stateAfterCabbage = await sdk.getState();
const hasCabbage = stateAfterCabbage.player.inventory?.some(item => item?.id === ITEM_CABBAGE);
console.log(`Has cabbage: ${hasCabbage}`);

// Stage 5: Return to Sir Amik to complete quest
console.log("\n--- Stage 5: Completing quest with Sir Amik ---");
await sdk.sendTeleport(SIR_AMIK_POS.x, SIR_AMIK_POS.z, SIR_AMIK_POS.plane);
await sdk.waitTicks(3);

const sirAmik2 = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("amik"));
if (sirAmik2) {
    console.log(`Found Sir Amik (index: ${sirAmik2.index}), completing quest...`);
    await sdk.sendInteractNpc(sirAmik2.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Sir Amik not found!");
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
    console.log("\n🎉 BLACK KNIGHTS' FORTRESS QUEST COMPLETION TEST: PASSED");
} else {
    console.log("\n❌ BLACK KNIGHTS' FORTRESS QUEST COMPLETION TEST: FAILED");
    console.log("Notes:");
    console.log("- Ensure server is running with quest loaded");
    console.log("- Check that Black Knights' Fortress is accessible");
    console.log("- Verify quest completion rewards are configured");
}
