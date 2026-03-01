// Romeo & Juliet Quest Complete Test Script
// A classic love story quest - deliver messages and create a potion to unite the lovers
// Locations:
//   - Romeo: Varrock Square (x: 3211, z: 3425)
//   - Juliet: Capulet house (x: 3158, z: 3429, floor 1)
//   - Father Lawrence: Varrock Church (x: 3254, z: 3483)
//   - Apothecary: South-west Varrock (x: 3196, z: 3403)
//   - Cadava bush: South of Varrock (x: 3273, z: 3369)
// Reward: 5 Quest Points

console.log("=== Romeo & Juliet Quest Test ===");

// Test configuration
const ROMEO_POS = { x: 3211, z: 3425, plane: 0 };
const JULIET_POS = { x: 3158, z: 3429, plane: 1 };
const FATHER_LAWRENCE_POS = { x: 3254, z: 3483, plane: 0 };
const APOTHECARY_POS = { x: 3196, z: 3403, plane: 0 };
const CADAVA_BUSH_POS = { x: 3273, z: 3369, plane: 0 };

// Quest varp ID (from quest configs)
const QUEST_VARP = 144; // Romeo & Juliet quest varp (rjquest)

// Helper to get quest stage from varp bits
function getQuestStage(state: any): number {
    // Quest varp bits: rjquest uses varp 144
    const varpValue = state?.player?.varps?.[QUEST_VARP];
    return varpValue || 0;
}

// Get initial state
const initialState = await sdk.getState();
console.log("Initial quest stage (varp 144):", getQuestStage(initialState));
console.log("Initial QP:", initialState.player.questPoints);

// Stage 0: Start quest with Romeo
console.log("\n--- Stage 0: Starting quest with Romeo ---");
await sdk.sendTeleport(ROMEO_POS.x, ROMEO_POS.z, ROMEO_POS.plane);
await sdk.waitTicks(3);

const stateAtRomeo = await sdk.getState();
const romeo = stateAtRomeo.player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("romeo"));
if (romeo) {
    console.log(`Found Romeo (index: ${romeo.index}), starting quest...`);
    await sdk.sendInteractNpc(romeo.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Romeo not found!");
}

const stateAfterRomeo = await sdk.getState();
console.log("Quest stage after Romeo:", getQuestStage(stateAfterRomeo));
console.log("Inventory:", stateAfterRomeo.player.inventory);

// Stage 1: Deliver message to Juliet
console.log("\n--- Stage 1: Delivering message to Juliet ---");
await sdk.sendTeleport(JULIET_POS.x, JULIET_POS.z, JULIET_POS.plane);
await sdk.waitTicks(3);

const stateAtJuliet = await sdk.getState();
const juliet = stateAtJuliet.player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("juliet"));
if (juliet) {
    console.log(`Found Juliet (index: ${juliet.index}), delivering message...`);
    await sdk.sendInteractNpc(juliet.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Juliet not found!");
}

const stateAfterJuliet = await sdk.getState();
console.log("Quest stage after Juliet:", getQuestStage(stateAfterJuliet));

// Stage 2: Talk to Father Lawrence
console.log("\n--- Stage 2: Talking to Father Lawrence ---");
await sdk.sendTeleport(FATHER_LAWRENCE_POS.x, FATHER_LAWRENCE_POS.z, FATHER_LAWRENCE_POS.plane);
await sdk.waitTicks(3);

const stateAtPriest = await sdk.getState();
const fatherLawrence = stateAtPriest.player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("lawrence"));
if (fatherLawrence) {
    console.log(`Found Father Lawrence (index: ${fatherLawrence.index}), getting plan...`);
    await sdk.sendInteractNpc(fatherLawrence.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Father Lawrence not found!");
}

const stateAfterPriest = await sdk.getState();
console.log("Quest stage after Father Lawrence:", getQuestStage(stateAfterPriest));

// Stage 3: Talk to Apothecary
console.log("\n--- Stage 3: Talking to Apothecary ---");
await sdk.sendTeleport(APOTHECARY_POS.x, APOTHECARY_POS.z, APOTHECARY_POS.plane);
await sdk.waitTicks(3);

const stateAtApothecary = await sdk.getState();
const apothecary = stateAtApothecary.player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("apothecary"));
if (apothecary) {
    console.log(`Found Apothecary (index: ${apothecary.index}), requesting potion...`);
    await sdk.sendInteractNpc(apothecary.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Apothecary not found!");
}

const stateAfterApothecary = await sdk.getState();
console.log("Quest stage after Apothecary:", getQuestStage(stateAfterApothecary));

// Stage 4: Get Cadava berries (spawn and pick up)
console.log("\n--- Stage 4: Getting Cadava berries ---");
// For testing, spawn the berries directly
await sdk.sendGroundItem(753, 1, APOTHECARY_POS.x, APOTHECARY_POS.z, APOTHECARY_POS.plane);
await sdk.waitTicks(2);

const stateBeforeBerries = await sdk.getState();
console.log("Inventory before berries:", stateBeforeBerries.player.inventory);

// Try to pick up berries from ground
await sdk.sendInteractGroundItem(753, APOTHECARY_POS.x, APOTHECARY_POS.z, 1);
await sdk.waitTicks(3);

const stateAfterBerries = await sdk.getState();
console.log("Inventory after berries:", stateAfterBerries.player.inventory);
const hasBerries = stateAfterBerries.player.inventory?.some(item => item?.id === 753);
console.log(`Has cadava berries: ${hasBerries}`);

// Stage 5: Give berries to Apothecary and get potion
console.log("\n--- Stage 5: Making Cadava potion ---");
const apothecary2 = stateAfterBerries.player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("apothecary"));
if (apothecary2) {
    console.log(`Talking to Apothecary to make potion...`);
    await sdk.sendInteractNpc(apothecary2.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Apothecary not found for potion creation!");
}

const stateAfterPotion = await sdk.getState();
console.log("Quest stage after potion:", getQuestStage(stateAfterPotion));
console.log("Inventory:", stateAfterPotion.player.inventory);
const hasPotion = stateAfterPotion.player.inventory?.some(item => item?.id === 756);
console.log(`Has cadava potion: ${hasPotion}`);

// Stage 6: Give potion to Juliet
console.log("\n--- Stage 6: Giving potion to Juliet ---");
await sdk.sendTeleport(JULIET_POS.x, JULIET_POS.z, JULIET_POS.plane);
await sdk.waitTicks(3);

const stateAtJuliet2 = await sdk.getState();
const juliet2 = stateAtJuliet2.player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("juliet"));
if (juliet2) {
    console.log(`Found Juliet (index: ${juliet2.index}), giving potion...`);
    await sdk.sendInteractNpc(juliet2.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Juliet not found!");
}

const stateAfterJulietPotion = await sdk.getState();
console.log("Quest stage after giving Juliet potion:", getQuestStage(stateAfterJulietPotion));

// Stage 7: Return to Romeo to complete
console.log("\n--- Stage 7: Completing quest with Romeo ---");
await sdk.sendTeleport(ROMEO_POS.x, ROMEO_POS.z, ROMEO_POS.plane);
await sdk.waitTicks(3);

const stateAtRomeo2 = await sdk.getState();
const romeo2 = stateAtRomeo2.player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("romeo"));
if (romeo2) {
    console.log(`Found Romeo (index: ${romeo2.index}), completing quest...`);
    await sdk.sendInteractNpc(romeo2.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Romeo not found!");
}

// Check final state
const finalState = await sdk.getState();
console.log("\n=== Results ===");
const finalStage = getQuestStage(finalState);
console.log("Final quest stage:", finalStage);
console.log("Final QP:", finalState.player.questPoints);
const qpGained = (finalState.player.questPoints || 0) - (initialState.player.questPoints || 0);

console.log("\n=== VERIFICATION ===");
const questComplete = finalStage >= 8 || qpGained >= 5;
console.log(`✓ Quest completed: ${questComplete ? 'PASS' : 'FAIL'}`);
console.log(`✓ QP gained (expected 5): ${qpGained === 5 ? 'PASS' : 'PARTIAL'} (got ${qpGained})`);

if (questComplete) {
    console.log("\n🎉 ROMEO & JULIET QUEST TEST: PASSED");
} else {
    console.log("\n❌ ROMEO & JULIET QUEST TEST: FAILED");
    console.log("Notes:");
    console.log("- Ensure server is running with quest loaded");
    console.log("- Check quest varp configuration");
    console.log("- Verify all NPCs and items are properly defined");
}
