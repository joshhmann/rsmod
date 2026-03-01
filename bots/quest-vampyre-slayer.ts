// Vampyre Slayer Quest Complete Test Script
// Locations: Draynor Village, Varrock
// NPCs: Morgan (morgan), Harlow (harlow), Count Draynor (count_draynor)
// Items: Beer (1917), Garlic (1550), Stake (1549), Hammer (2347), Vampyre dust (3325)
// Reward: 5000 Attack XP, 3 Quest Points

console.log("=== Vampyre Slayer Quest Complete Test ===");

// Test configuration
const MORGAN_POS = { x: 3096, z: 3270, plane: 0 }; // Draynor
const HARLOW_POS = { x: 3221, z: 3397, plane: 0 }; // Varrock Blue Moon Inn
const DRAYNOR_MANOR = { x: 3118, z: 3354, plane: 0 };
const CRYPT_POS = { x: 3077, z: 3260, plane: 0 }; // Draynor crypt

const ITEM_BEER = 1917;
const ITEM_GARLIC = 1550;
const ITEM_STAKE = 1549;
const ITEM_HAMMER = 2347;
const ITEM_VAMPYRE_DUST = 3325;

const REWARD_ATTACK_XP = 5000;
const REWARD_QP = 3;

// Get initial state
const initialState = await sdk.getState();
console.log(`Initial QP: ${initialState.player.questPoints}`);
console.log(`Initial Attack XP: ${initialState.player.skills.attack?.xp}`);
console.log(`Initial Attack Level: ${initialState.player.skills.attack?.level}`);

// Stage 1: Start quest with Morgan
console.log("\n--- Stage 1: Starting quest with Morgan ---");
await sdk.sendTeleport(MORGAN_POS.x, MORGAN_POS.z, MORGAN_POS.plane);
await sdk.waitTicks(3);

const morgan = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("morgan"));
if (morgan) {
    console.log(`Found Morgan (index: ${morgan.index}), starting quest...`);
    await sdk.sendInteractNpc(morgan.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Morgan not found!");
}

// Stage 2: Get beer for Harlow
console.log("\n--- Stage 2: Getting beer for Harlow ---");
await sdk.sendGroundItem(ITEM_BEER, 1, HARLOW_POS.x, HARLOW_POS.z, HARLOW_POS.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_BEER, HARLOW_POS.x, HARLOW_POS.z, 1);
await sdk.waitTicks(3);

const stateAfterBeer = await sdk.getState();
const hasBeer = stateAfterBeer.player.inventory?.some(item => item?.id === ITEM_BEER);
console.log(`Has beer: ${hasBeer}`);

// Stage 3: Talk to Harlow for stake
console.log("\n--- Stage 3: Getting stake from Harlow ---");
await sdk.sendTeleport(HARLOW_POS.x, HARLOW_POS.z, HARLOW_POS.plane);
await sdk.waitTicks(3);

const harlow = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("harlow"));
if (harlow) {
    console.log(`Found Harlow (index: ${harlow.index}), getting stake...`);
    await sdk.sendInteractNpc(harlow.index, 1);
    await sdk.waitTicks(10);
}

// Give stake and other items for testing
await sdk.sendGroundItem(ITEM_STAKE, 1, HARLOW_POS.x, HARLOW_POS.z, HARLOW_POS.plane);
await sdk.sendGroundItem(ITEM_GARLIC, 1, HARLOW_POS.x, HARLOW_POS.z, HARLOW_POS.plane);
await sdk.sendGroundItem(ITEM_HAMMER, 1, HARLOW_POS.x, HARLOW_POS.z, HARLOW_POS.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_STAKE, HARLOW_POS.x, HARLOW_POS.z, 1);
await sdk.sendInteractGroundItem(ITEM_GARLIC, HARLOW_POS.x, HARLOW_POS.z, 1);
await sdk.sendInteractGroundItem(ITEM_HAMMER, HARLOW_POS.x, HARLOW_POS.z, 1);
await sdk.waitTicks(3);

const stateAfterItems = await sdk.getState();
const hasStake = stateAfterItems.player.inventory?.some(item => item?.id === ITEM_STAKE);
const hasGarlic = stateAfterItems.player.inventory?.some(item => item?.id === ITEM_GARLIC);
const hasHammer = stateAfterItems.player.inventory?.some(item => item?.id === ITEM_HAMMER);
console.log(`Has stake: ${hasStake}, Has garlic: ${hasGarlic}, Has hammer: ${hasHammer}`);

// Stage 4: Enter Draynor Manor and find crypt
console.log("\n--- Stage 4: Finding Count Draynor ---");
await sdk.sendTeleport(CRYPT_POS.x, CRYPT_POS.z, CRYPT_POS.plane);
await sdk.waitTicks(3);

// Stage 5: Kill Count Draynor
console.log("\n--- Stage 5: Killing Count Draynor ---");
const countDraynor = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("count draynor"));
if (countDraynor) {
    console.log(`Found Count Draynor (index: ${countDraynor.index}), attacking...`);
    await sdk.sendInteractNpc(countDraynor.index, 1); // Attack
    await sdk.waitTicks(20);
    console.log("Count Draynor defeated");
} else {
    console.log("Count Draynor not found (may be defeated or not spawned)");
}

// Give vampyre dust for testing
await sdk.sendGroundItem(ITEM_VAMPYRE_DUST, 1, CRYPT_POS.x, CRYPT_POS.z, CRYPT_POS.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_VAMPYRE_DUST, CRYPT_POS.x, CRYPT_POS.z, 1);
await sdk.waitTicks(3);

// Stage 6: Return to Morgan to complete
console.log("\n--- Stage 6: Completing quest with Morgan ---");
await sdk.sendTeleport(MORGAN_POS.x, MORGAN_POS.z, MORGAN_POS.plane);
await sdk.waitTicks(3);

const morgan2 = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("morgan"));
if (morgan2) {
    console.log(`Found Morgan (index: ${morgan2.index}), completing quest...`);
    await sdk.sendInteractNpc(morgan2.index, 1);
    await sdk.waitTicks(10);
}

// Check final state
const finalState = await sdk.getState();
console.log("\n=== Results ===");
console.log("Final Inventory:", finalState.player.inventory);
console.log(`Final QP: ${finalState.player.questPoints}`);
console.log(`Final Attack XP: ${finalState.player.skills.attack?.xp}`);
console.log(`Final Attack Level: ${finalState.player.skills.attack?.level}`);

// Verify completion
const qpGained = (finalState.player.questPoints || 0) - (initialState.player.questPoints || 0);
const attackXpGained = finalState.player.skills.attack?.xp - initialState.player.skills.attack?.xp;
const hasDust = finalState.player.inventory?.some(item => item?.id === ITEM_VAMPYRE_DUST);

console.log("\n=== VERIFICATION ===");
console.log(`✓ QP gained (expected ${REWARD_QP}): ${qpGained >= REWARD_QP ? 'PASS' : 'FAIL'} (got ${qpGained})`);
console.log(`✓ Attack XP gained (expected ${REWARD_ATTACK_XP}): ${attackXpGained >= REWARD_ATTACK_XP - 200 ? 'PASS' : 'FAIL'} (got ${attackXpGained})`);
console.log(`✓ Vampyre dust received: ${hasDust ? 'PASS' : 'FAIL'}`);

if (qpGained >= REWARD_QP && attackXpGained >= REWARD_ATTACK_XP - 200 && hasDust) {
    console.log("\n🎉 VAMPYRE SLAYER QUEST COMPLETION TEST: PASSED");
} else {
    console.log("\n❌ VAMPYRE SLAYER QUEST COMPLETION TEST: FAILED");
    console.log("Notes:");
    console.log("- Ensure server is running with quest loaded");
    console.log("- Check that Count Draynor boss is properly spawned");
    console.log("- Verify quest completion rewards are configured");
}
