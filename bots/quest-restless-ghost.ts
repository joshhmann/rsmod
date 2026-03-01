// Restless Ghost Quest Complete Test Script
// Location: Lumbridge Church (x: 3241, z: 3208)
// NPCs: Father Aereck (father_aereck), Father Urhney (father_urhney), Ghost (restless_ghost)
// Items: Ghostspeak amulet (552), Ghost's skull (553)
// Reward: 125 Prayer XP, 5 Quest Points

console.log("=== Restless Ghost Quest Complete Test ===");

// Test configuration
const FATHER_AERECK_POS = { x: 3241, z: 3208, plane: 0 };
const FATHER_URHNEY_POS = { x: 3146, z: 3177, plane: 0 }; // Swamp
const GHOST_POS = { x: 3246, z: 3194, plane: 0 }; // Lumbridge cemetery
const COFFIN_POS = { x: 3248, z: 3192, plane: 0 };

const ITEM_GHOSTSPEAK_AMULET = 552;
const ITEM_GHOST_SKULL = 553;

const REWARD_XP = 125;
const REWARD_QP = 5;

// Get initial state
const initialState = await sdk.getState();
console.log(`Initial Prayer XP: ${initialState.player.skills.prayer?.xp}`);
console.log(`Initial Prayer Level: ${initialState.player.skills.prayer?.level}`);
console.log(`Initial QP: ${initialState.player.questPoints}`);

// Stage 1: Start quest with Father Aereck
console.log("\n--- Stage 1: Starting quest with Father Aereck ---");
await sdk.sendTeleport(FATHER_AERECK_POS.x, FATHER_AERECK_POS.z, FATHER_AERECK_POS.plane);
await sdk.waitTicks(3);

const fatherAereck = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("aereck"));
if (fatherAereck) {
    console.log(`Found Father Aereck (index: ${fatherAereck.index}), starting quest...`);
    await sdk.sendInteractNpc(fatherAereck.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Father Aereck not found!");
}

// Stage 2: Talk to Father Urhney for amulet
console.log("\n--- Stage 2: Getting Ghostspeak amulet from Father Urhney ---");
await sdk.sendTeleport(FATHER_URHNEY_POS.x, FATHER_URHNEY_POS.z, FATHER_URHNEY_POS.plane);
await sdk.waitTicks(3);

const fatherUrhney = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("urhney"));
if (fatherUrhney) {
    console.log(`Found Father Urhney (index: ${fatherUrhney.index}), getting amulet...`);
    await sdk.sendInteractNpc(fatherUrhney.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Father Urhney not found!");
}

// Give amulet for testing
await sdk.sendGroundItem(ITEM_GHOSTSPEAK_AMULET, 1, FATHER_URHNEY_POS.x, FATHER_URHNEY_POS.z, FATHER_URHNEY_POS.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_GHOSTSPEAK_AMULET, FATHER_URHNEY_POS.x, FATHER_URHNEY_POS.z, 1);
await sdk.waitTicks(3);

const stateAfterAmulet = await sdk.getState();
const hasAmulet = stateAfterAmulet.player.inventory?.some(item => item?.id === ITEM_GHOSTSPEAK_AMULET);
console.log(`Has Ghostspeak amulet: ${hasAmulet}`);

// Stage 3: Get Ghost's skull from coffin
console.log("\n--- Stage 3: Getting Ghost's skull from coffin ---");
await sdk.sendTeleport(COFFIN_POS.x, COFFIN_POS.z, COFFIN_POS.plane);
await sdk.waitTicks(3);

// Spawn skull for testing
await sdk.sendGroundItem(ITEM_GHOST_SKULL, 1, COFFIN_POS.x, COFFIN_POS.z, COFFIN_POS.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_GHOST_SKULL, COFFIN_POS.x, COFFIN_POS.z, 1);
await sdk.waitTicks(3);

const stateAfterSkull = await sdk.getState();
const hasSkull = stateAfterSkull.player.inventory?.some(item => item?.id === ITEM_GHOST_SKULL);
console.log(`Has Ghost's skull: ${hasSkull}`);

// Stage 4: Talk to Ghost
console.log("\n--- Stage 4: Talking to Restless Ghost ---");
await sdk.sendTeleport(GHOST_POS.x, GHOST_POS.z, GHOST_POS.plane);
await sdk.waitTicks(3);

const ghost = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("ghost"));
if (ghost) {
    console.log(`Found Ghost (index: ${ghost.index}), talking...`);
    await sdk.sendInteractNpc(ghost.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Ghost not found!");
}

// Stage 5: Complete quest
console.log("\n--- Stage 5: Completing quest ---");
await sdk.sendTeleport(FATHER_AERECK_POS.x, FATHER_AERECK_POS.z, FATHER_AERECK_POS.plane);
await sdk.waitTicks(3);

const fatherAereck2 = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("aereck"));
if (fatherAereck2) {
    console.log(`Found Father Aereck (index: ${fatherAereck2.index}), completing quest...`);
    await sdk.sendInteractNpc(fatherAereck2.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Father Aereck not found!");
}

// Check final state
const finalState = await sdk.getState();
console.log("\n=== Results ===");
console.log("Final Inventory:", finalState.player.inventory);
console.log(`Final Prayer XP: ${finalState.player.skills.prayer?.xp}`);
console.log(`Final Prayer Level: ${finalState.player.skills.prayer?.level}`);
console.log(`Final QP: ${finalState.player.questPoints}`);

// Verify completion
const prayerXpGained = finalState.player.skills.prayer?.xp - initialState.player.skills.prayer?.xp;
const qpGained = (finalState.player.questPoints || 0) - (initialState.player.questPoints || 0);

console.log("\n=== VERIFICATION ===");
console.log(`✓ Prayer XP gained (expected ${REWARD_XP}): ${prayerXpGained >= REWARD_XP - 10 ? 'PASS' : 'FAIL'} (got ${prayerXpGained})`);
console.log(`✓ QP gained (expected ${REWARD_QP}): ${qpGained >= REWARD_QP ? 'PASS' : 'FAIL'} (got ${qpGained})`);

if (prayerXpGained >= REWARD_XP - 10 && qpGained >= REWARD_QP) {
    console.log("\n🎉 RESTLESS GHOST QUEST COMPLETION TEST: PASSED");
} else {
    console.log("\n❌ RESTLESS GHOST QUEST COMPLETION TEST: FAILED");
    console.log("Notes:");
    console.log("- Ensure server is running with quest loaded");
    console.log("- Check that Ghostspeak amulet and skull are properly defined");
    console.log("- Verify quest completion rewards are configured");
}
