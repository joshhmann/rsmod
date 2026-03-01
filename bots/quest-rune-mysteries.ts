// Rune Mysteries Quest Complete Test Script
// Location: Lumbridge Castle → Wizards' Tower → Varrock
// NPCs: Duke Horacio (duke_of_lumbridge), Sedridor (sedridor), Aubury (aubury)
// Items: Air talisman (1438), Research package (291)
// Reward: 1 Quest Point, ability to mine Rune Essence

console.log("=== Rune Mysteries Quest Complete Test ===");

// Test configuration
const DUKE_HORACIO_POS = { x: 3211, z: 3222, plane: 1 }; // Lumbridge Castle
const SEDROR_POS = { x: 3103, z: 3162, plane: 0 }; // Wizards' Tower
const AUBURY_POS = { x: 3254, z: 3404, plane: 0 }; // Varrock rune shop

const ITEM_AIR_TALISMAN = 1438;
const ITEM_RESEARCH_PACKAGE = 291;

const REWARD_QP = 1;

// Get initial state
const initialState = await sdk.getState();
console.log(`Initial QP: ${initialState.player.questPoints}`);

// Stage 1: Start quest with Duke Horacio
console.log("\n--- Stage 1: Starting quest with Duke Horacio ---");
await sdk.sendTeleport(DUKE_HORACIO_POS.x, DUKE_HORACIO_POS.z, DUKE_HORACIO_POS.plane);
await sdk.waitTicks(3);

const dukeHoracio = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("duke"));
if (dukeHoracio) {
    console.log(`Found Duke Horacio (index: ${dukeHoracio.index}), starting quest...`);
    await sdk.sendInteractNpc(dukeHoracio.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Duke Horacio not found!");
}

// Give talisman for testing
await sdk.sendGroundItem(ITEM_AIR_TALISMAN, 1, DUKE_HORACIO_POS.x, DUKE_HORACIO_POS.z, DUKE_HORACIO_POS.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_AIR_TALISMAN, DUKE_HORACIO_POS.x, DUKE_HORACIO_POS.z, 1);
await sdk.waitTicks(3);

const stateAfterTalisman = await sdk.getState();
const hasTalisman = stateAfterTalisman.player.inventory?.some(item => item?.id === ITEM_AIR_TALISMAN);
console.log(`Has Air talisman: ${hasTalisman}`);

// Stage 2: Deliver talisman to Sedridor
console.log("\n--- Stage 2: Delivering talisman to Sedridor ---");
await sdk.sendTeleport(SEDROR_POS.x, SEDROR_POS.z, SEDROR_POS.plane);
await sdk.waitTicks(3);

const sedridor = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("sedridor"));
if (sedridor) {
    console.log(`Found Sedridor (index: ${sedridor.index}), delivering talisman...`);
    await sdk.sendInteractNpc(sedridor.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Sedridor not found!");
}

// Stage 3: Get research package from Sedridor
console.log("\n--- Stage 3: Getting research package ---");
await sdk.sendGroundItem(ITEM_RESEARCH_PACKAGE, 1, SEDROR_POS.x, SEDROR_POS.z, SEDROR_POS.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_RESEARCH_PACKAGE, SEDROR_POS.x, SEDROR_POS.z, 1);
await sdk.waitTicks(3);

const stateAfterPackage = await sdk.getState();
const hasPackage = stateAfterPackage.player.inventory?.some(item => item?.id === ITEM_RESEARCH_PACKAGE);
console.log(`Has Research package: ${hasPackage}`);

// Stage 4: Deliver package to Aubury
console.log("\n--- Stage 4: Delivering package to Aubury ---");
await sdk.sendTeleport(AUBURY_POS.x, AUBURY_POS.z, AUBURY_POS.plane);
await sdk.waitTicks(3);

const aubury = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("aubury"));
if (aubury) {
    console.log(`Found Aubury (index: ${aubury.index}), delivering package...`);
    await sdk.sendInteractNpc(aubury.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Aubury not found!");
}

// Stage 5: Return to Sedridor to complete
console.log("\n--- Stage 5: Completing quest with Sedridor ---");
await sdk.sendTeleport(SEDROR_POS.x, SEDROR_POS.z, SEDROR_POS.plane);
await sdk.waitTicks(3);

const sedridor2 = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("sedridor"));
if (sedridor2) {
    console.log(`Found Sedridor (index: ${sedridor2.index}), completing quest...`);
    await sdk.sendInteractNpc(sedridor2.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Sedridor not found!");
}

// Check final state
const finalState = await sdk.getState();
console.log("\n=== Results ===");
console.log("Final Inventory:", finalState.player.inventory);
console.log(`Final QP: ${finalState.player.questPoints}`);

// Verify completion
const qpGained = (finalState.player.questPoints || 0) - (initialState.player.questPoints || 0);

console.log("\n=== VERIFICATION ===");
console.log(`✓ QP gained (expected ${REWARD_QP}): ${qpGained >= REWARD_QP ? 'PASS' : 'FAIL'} (got ${qpGained})`);

if (qpGained >= REWARD_QP) {
    console.log("\n🎉 RUNE MYSTERIES QUEST COMPLETION TEST: PASSED");
    console.log("\n✓ Player can now mine Rune Essence");
} else {
    console.log("\n❌ RUNE MYSTERIES QUEST COMPLETION TEST: FAILED");
    console.log("Notes:");
    console.log("- Ensure server is running with quest loaded");
    console.log("- Check that Air talisman and Research package are properly defined");
    console.log("- Verify quest completion rewards are configured");
}
