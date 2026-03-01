// Test Imp Catcher Quest Script
// Location: Wizards' Tower (x: 3109, z: 3164, floor 2)
// NPC: Wizard Mizgog (wizard_mizgog, ID: 5005)
// Items needed: black_bead (1474), red_bead (1470), white_bead (1476), yellow_bead (1472)
// Reward: Amulet of accuracy (1478), 875 Magic XP

console.log("=== Starting Imp Catcher Quest Test ===");

// Get initial state
const state = await sdk.getState();
console.log(`Position: (${state.player.position.x}, ${state.player.position.z}, plane ${state.player.position.plane})`);
console.log("Magic level:", state.player.skills.magic?.level);

// Quest varp check - imp_catcher is quest ID 5
const questVarp = 36320802; // BaseVarps.imp
console.log("Quest varp:", questVarp);

// Teleport to Wizards' Tower - 2nd floor
const towerX = 3109;
const towerZ = 3164;
const towerPlane = 2;
console.log(`\nTeleporting to Wizards' Tower (${towerX}, ${towerZ}, floor ${towerPlane})...`);
await sdk.sendTeleport(towerX, towerZ, towerPlane);
await sdk.waitTicks(3);

// Find Mizgog NPC
const mizgog = state.player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("mizgog"));
if (mizgog) {
    console.log(`Found NPC: ${mizgog.name} (index: ${mizgog.index})`);
    
    // Interact with Mizgog - option 1 (talk-to)
    console.log("Talking to Wizard Mizgog...");
    await sdk.sendInteractNpc(mizgog.index, 1);
    
    // Wait for dialogue (simple delay for now)
    await sdk.waitTicks(5);
    
    console.log("Dialogue initiated. Testing quest flow...");
    console.log("NOTE: Full quest testing requires beads to be added to inventory.");
    console.log("This test verifies Mizgog can be interacted with.");
} else {
    console.log("Mizgog not found nearby. Try different coordinates.");
}

// Check final position
const finalState = await sdk.getState();
console.log(`\nFinal position: (${finalState.player.position.x}, ${finalState.player.position.z}, plane ${finalState.player.position.plane})`);

console.log("\n=== Test Complete ===");
console.log("To complete the quest test:");
console.log("1. Start the quest with Mizgog");
console.log("2. Add beads to inventory (black, red, white, yellow)");
console.log("3. Return to Mizgog with all 4 beads");
console.log("4. Verify: Amulet of accuracy received, 875 Magic XP gained");
