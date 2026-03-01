// Doric's Quest Complete Test Script
// Location: Doric's hut north of Falador (x: 2959, z: 3441)
// NPC: Doric (doric, ID: 3261)
// Items: clay (434), copper_ore (436), iron_ore (440)
// Reward: 1300 Mining XP, 180 coins, use of Doric's anvils

console.log("=== Doric's Quest Complete Test ===");

// Test configuration
const DORIC_POS = { x: 2959, z: 3441, plane: 0 };
const ITEM_CLAY = 434;
const ITEM_COPPER_ORE = 436;
const ITEM_IRON_ORE = 440;

const CLAY_REQ = 6;
const COPPER_REQ = 4;
const IRON_REQ = 2;

const REWARD_XP = 1300;
const REWARD_COINS = 180;

// Get initial state
const initialState = await sdk.getState();
console.log(`Initial Mining XP: ${initialState.player.skills.mining?.xp}`);
console.log(`Initial Mining Level: ${initialState.player.skills.mining?.level}`);
console.log(`Initial Coins: ${initialState.player.inventory?.find(i => i?.id === 995)?.qty || 0}`);

// Setup: Give player required ores
console.log("\n--- Setting up test: Adding ores to inventory ---");

// Spawn ores on ground and pick up (or use admin commands if available)
for (let i = 0; i < CLAY_REQ; i++) {
    await sdk.sendGroundItem(ITEM_CLAY, 1, DORIC_POS.x, DORIC_POS.z, DORIC_POS.plane);
}
for (let i = 0; i < COPPER_REQ; i++) {
    await sdk.sendGroundItem(ITEM_COPPER_ORE, 1, DORIC_POS.x, DORIC_POS.z, DORIC_POS.plane);
}
for (let i = 0; i < IRON_REQ; i++) {
    await sdk.sendGroundItem(ITEM_IRON_ORE, 1, DORIC_POS.x, DORIC_POS.z, DORIC_POS.plane);
}
await sdk.waitTicks(3);

// Teleport to Doric
console.log(`\nTeleporting to Doric's hut (${DORIC_POS.x}, ${DORIC_POS.z})...`);
await sdk.sendTeleport(DORIC_POS.x, DORIC_POS.z, DORIC_POS.plane);
await sdk.waitTicks(5);

// Pick up all ores
console.log("\n--- Picking up ores ---");
for (let i = 0; i < CLAY_REQ + COPPER_REQ + IRON_REQ; i++) {
    await sdk.sendInteractGroundItem(ITEM_CLAY, DORIC_POS.x, DORIC_POS.z, 1);
    await sdk.sendInteractGroundItem(ITEM_COPPER_ORE, DORIC_POS.x, DORIC_POS.z, 1);
    await sdk.sendInteractGroundItem(ITEM_IRON_ORE, DORIC_POS.x, DORIC_POS.z, 1);
    await sdk.waitTicks(2);
}

const stateAfterPickup = await sdk.getState();
console.log("Inventory after pickup:", stateAfterPickup.player.inventory);

// Interact with Doric to start quest
const stateBeforeQuest = await sdk.getState();
const doric = stateBeforeQuest.player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("doric"));
if (doric) {
    console.log(`Found NPC: ${doric.name} (index: ${doric.index})`);
    console.log("Starting quest...");
    await sdk.sendInteractNpc(doric.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Doric not found!");
}

// Wait for quest to start
const stateAfterQuestStart = await sdk.getState();
console.log("Inventory after quest start:", stateAfterQuestStart.player.inventory);
console.log(`Mining XP after quest start: ${stateAfterQuestStart.player.skills.mining?.xp}`);

// Complete the quest: Talk to Doric with all ores
console.log("\n--- Testing Quest Completion ---");
const stateBeforeCompletion = await sdk.getState();
const doricComplete = stateBeforeCompletion.player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("doric"));
if (doricComplete) {
    console.log("Talking to Doric to complete quest...");
    await sdk.sendInteractNpc(doricComplete.index, 1);
    await sdk.waitTicks(10);
}

// Check final state
const finalState = await sdk.getState();
console.log("\n=== Results ===");
console.log("Final Inventory:", finalState.player.inventory);
console.log(`Final Mining XP: ${finalState.player.skills.mining?.xp}`);
console.log(`Final Mining Level: ${finalState.player.skills.mining?.level}`);

// Verify completion
const miningXpGained = finalState.player.skills.mining?.xp - initialState.player.skills.mining?.xp;
const coinsReward = finalState.player.inventory?.find(i => i?.id === 995)?.qty || 0;
const coinsGained = coinsReward - (initialState.player.inventory?.find(i => i?.id === 995)?.qty || 0);

console.log("\n=== VERIFICATION ===");
console.log(`✓ Mining XP gained (expected ${REWARD_XP}): ${miningXpGained >= REWARD_XP - 100 ? 'PASS' : 'FAIL'} (got ${miningXpGained})`);
console.log(`✓ Coins received (expected ${REWARD_COINS}): ${coinsGained >= REWARD_COINS - 10 ? 'PASS' : 'FAIL'} (got ${coinsGained})`);

if (miningXpGained >= REWARD_XP - 100 && coinsGained >= REWARD_COINS - 10) {
    console.log("\n🎉 DORIC'S QUEST COMPLETION TEST: PASSED");
} else {
    console.log("\n❌ DORIC'S QUEST COMPLETION TEST: FAILED");
    console.log("Notes:");
    console.log("- Ensure server is running with quest loaded");
    console.log("- Check that ores are properly defined in cache");
    console.log("- Verify quest completion rewards are configured");
}
