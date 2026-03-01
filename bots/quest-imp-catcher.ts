// Imp Catcher Quest Complete Test Script
// Location: Wizards' Tower (x: 3109, z: 3164, floor 2)
// NPC: Wizard Mizgog (wizard_mizgog, ID: 5005)
// Items: black_bead (1474), red_bead (1470), white_bead (1476), yellow_bead (1472)
// Reward: Amulet of accuracy (1478), 875 Magic XP, 1 Quest Point

console.log("=== Imp Catcher Quest Complete Test ===");

// Test configuration
const MIZGOG_POS = { x: 3109, z: 3164, plane: 2 };
const ITEM_BLACK_BEAD = 1474;
const ITEM_RED_BEAD = 1470;
const ITEM_WHITE_BEAD = 1476;
const ITEM_YELLOW_BEAD = 1472;
const ITEM_AMULET_ACCURACY = 1478;

// Get initial state
const initialState = await sdk.getState();
console.log(`Initial Magic XP: ${initialState.player.skills.magic?.xp}`);
console.log(`Initial Magic Level: ${initialState.player.skills.magic?.level}`);

// Setup: Give player required items and start quest
console.log("\n--- Setting up test: Adding beads + starting quest ---");

// Add beads to inventory
await sdk.sendGroundItem(ITEM_BLACK_BEAD, 1, MIZGOG_POS.x, MIZGOG_POS.z, MIZGOG_POS.plane);
await sdk.sendGroundItem(ITEM_RED_BEAD, 1, MIZGOG_POS.x, MIZGOG_POS.z, MIZGOG_POS.plane);
await sdk.sendGroundItem(ITEM_WHITE_BEAD, 1, MIZGOG_POS.x, MIZGOG_POS.z, MIZGOG_POS.plane);
await sdk.sendGroundItem(ITEM_YELLOW_BEAD, 1, MIZGOG_POS.x, MIZGOG_POS.z, MIZGOG_POS.plane);
await sdk.waitTicks(3);

// Pick up beads
const stateBeforePickup = await sdk.getState();
console.log("Inventory before pickup:", stateBeforePickup.player.inventory);

// Teleport to Mizgog
console.log(`\nTeleporting to Wizards' Tower (${MIZGOG_POS.x}, ${MIZGOG_POS.z}, floor ${MIZGOG_POS.plane})...`);
await sdk.sendTeleport(MIZGOG_POS.x, MIZGOG_POS.z, MIZGOG_POS.plane);
await sdk.waitTicks(5);

// Interact with Mizgog to start quest
const stateBeforeQuest = await sdk.getState();
const mizgog = stateBeforeQuest.player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("mizgog"));
if (mizgog) {
    console.log(`Found NPC: ${mizgog.name} (index: ${mizgog.index})`);
    console.log("Starting quest...");
    await sdk.sendInteractNpc(mizgog.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Mizgog not found!");
}

// Wait for quest to start
const stateAfterQuestStart = await sdk.getState();
console.log("Inventory after quest start:", stateAfterQuestStart.player.inventory);
console.log(`Magic XP after quest start: ${stateAfterQuestStart.player.skills.magic?.xp}`);

// Complete the quest: Talk to Mizgog with all beads
console.log("\n--- Testing Quest Completion ---");
const stateBeforeCompletion = await sdk.getState();
const mizgogComplete = stateBeforeCompletion.player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("mizgog"));
if (mizgogComplete) {
    console.log("Talking to Mizgog to complete quest...");
    await sdk.sendInteractNpc(mizgogComplete.index, 1);
    await sdk.waitTicks(10);
}

// Check final state
const finalState = await sdk.getState();
console.log("\n--- Results ---");
console.log("Final Inventory:", finalState.player.inventory);
console.log(`Final Magic XP: ${finalState.player.skills.magic?.xp}`);
console.log(`Final Magic Level: ${finalState.player.skills.magic?.level}`);

// Verify completion
const hasAmulet = finalState.player.inventory?.some(item => item?.id === ITEM_AMULET_ACCURACY);
const magicXpGained = finalState.player.skills.magic?.xp - initialState.player.skills.magic?.xp;

console.log("\n=== VERIFICATION ===");
console.log(`✓ Amulet of Accuracy received: ${hasAmulet ? 'PASS' : 'FAIL'}`);
console.log(`✓ Magic XP gained (expected ~875): ${magicXpGained > 800 ? 'PASS' : 'FAIL'} (got ${magicXpGained})`);

if (hasAmulet && magicXpGained > 800) {
    console.log("\n🎉 QUEST COMPLETION TEST: PASSED");
} else {
    console.log("\n❌ QUEST COMPLETION TEST: FAILED");
    console.log("Notes:");
    console.log("- Ensure server is running with quest loaded");
    console.log("- Check that beads are properly defined in cache");
    console.log("- Verify showCompletionScroll is working");
}
