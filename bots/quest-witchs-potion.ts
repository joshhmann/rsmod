// Witch's Potion Quest Complete Test Script
// Location: Rimmington (x: 2969, z: 3206)
// NPC: Hetty (hetty)
// Items: Rat's tail (300), Burnt meat (2146), Onion (1957), Eye of newt (221)
// Reward: 325 Magic XP, 1 Quest Point

console.log("=== Witch's Potion Quest Complete Test ===");

// Test configuration
const HETTY_POS = { x: 2969, z: 3206, plane: 0 };

const ITEM_RAT_TAIL = 300;
const ITEM_BURNT_MEAT = 2146;
const ITEM_ONION = 1957;
const ITEM_EYE_OF_NEWT = 221;

const REWARD_XP = 325;
const REWARD_QP = 1;

// Get initial state
const initialState = await sdk.getState();
console.log(`Initial Magic XP: ${initialState.player.skills.magic?.xp}`);
console.log(`Initial Magic Level: ${initialState.player.skills.magic?.level}`);
console.log(`Initial QP: ${initialState.player.questPoints}`);

// Setup: Give player required items
console.log("\n--- Setting up test: Adding quest ingredients ---");
await sdk.sendGroundItem(ITEM_RAT_TAIL, 1, HETTY_POS.x, HETTY_POS.z, HETTY_POS.plane);
await sdk.sendGroundItem(ITEM_BURNT_MEAT, 1, HETTY_POS.x, HETTY_POS.z, HETTY_POS.plane);
await sdk.sendGroundItem(ITEM_ONION, 1, HETTY_POS.x, HETTY_POS.z, HETTY_POS.plane);
await sdk.sendGroundItem(ITEM_EYE_OF_NEWT, 1, HETTY_POS.x, HETTY_POS.z, HETTY_POS.plane);
await sdk.waitTicks(3);

// Pick up all ingredients
await sdk.sendInteractGroundItem(ITEM_RAT_TAIL, HETTY_POS.x, HETTY_POS.z, 1);
await sdk.waitTicks(1);
await sdk.sendInteractGroundItem(ITEM_BURNT_MEAT, HETTY_POS.x, HETTY_POS.z, 1);
await sdk.waitTicks(1);
await sdk.sendInteractGroundItem(ITEM_ONION, HETTY_POS.x, HETTY_POS.z, 1);
await sdk.waitTicks(1);
await sdk.sendInteractGroundItem(ITEM_EYE_OF_NEWT, HETTY_POS.x, HETTY_POS.z, 1);
await sdk.waitTicks(3);

const stateAfterPickup = await sdk.getState();
console.log("Inventory after pickup:", stateAfterPickup.player.inventory);

// Stage 1: Start quest with Hetty
console.log("\n--- Stage 1: Starting quest with Hetty ---");
await sdk.sendTeleport(HETTY_POS.x, HETTY_POS.z, HETTY_POS.plane);
await sdk.waitTicks(3);

const hetty = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("hetty"));
if (hetty) {
    console.log(`Found Hetty (index: ${hetty.index}), starting quest...`);
    await sdk.sendInteractNpc(hetty.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Hetty not found!");
}

// Stage 2: Complete quest with Hetty
console.log("\n--- Stage 2: Completing quest with Hetty ---");
const hetty2 = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("hetty"));
if (hetty2) {
    console.log(`Found Hetty (index: ${hetty2.index}), completing quest...`);
    await sdk.sendInteractNpc(hetty2.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Hetty not found!");
}

// Check final state
const finalState = await sdk.getState();
console.log("\n=== Results ===");
console.log("Final Inventory:", finalState.player.inventory);
console.log(`Final Magic XP: ${finalState.player.skills.magic?.xp}`);
console.log(`Final Magic Level: ${finalState.player.skills.magic?.level}`);
console.log(`Final QP: ${finalState.player.questPoints}`);

// Verify completion
const magicXpGained = finalState.player.skills.magic?.xp - initialState.player.skills.magic?.xp;
const qpGained = (finalState.player.questPoints || 0) - (initialState.player.questPoints || 0);

console.log("\n=== VERIFICATION ===");
console.log(`✓ Magic XP gained (expected ${REWARD_XP}): ${magicXpGained >= REWARD_XP - 20 ? 'PASS' : 'FAIL'} (got ${magicXpGained})`);
console.log(`✓ QP gained (expected ${REWARD_QP}): ${qpGained >= REWARD_QP ? 'PASS' : 'FAIL'} (got ${qpGained})`);

if (magicXpGained >= REWARD_XP - 20 && qpGained >= REWARD_QP) {
    console.log("\n🎉 WITCH'S POTION QUEST COMPLETION TEST: PASSED");
} else {
    console.log("\n❌ WITCH'S POTION QUEST COMPLETION TEST: FAILED");
    console.log("Notes:");
    console.log("- Ensure server is running with quest loaded");
    console.log("- Check that ingredients are properly defined in cache");
    console.log("- Verify quest completion rewards are configured");
}
