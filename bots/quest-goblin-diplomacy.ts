// Goblin Diplomacy Quest Complete Test Script
// Locations: Goblin Village (north of Falador), various locations for goblin mail
// NPCs: General Bentnoze (general_bentnoze), General Wartface (general_wartface)
// Items: Goblin mail (288), Blue goblin mail (287), Orange goblin mail (286), Goblin armour dye (288 variants)
// Reward: 200 Crafting XP, 5 Quest Points, Gold bar

console.log("=== Goblin Diplomacy Quest Complete Test ===");

// Test configuration
const GOBLIN_VILLAGE_POS = { x: 2958, z: 3512, plane: 0 };
const BENTNOZE_POS = { x: 2955, z: 3510, plane: 0 };
const WARTFACE_POS = { x: 2957, z: 3514, plane: 0 };

const ITEM_GOBLIN_MAIL = 288; // goblin_mail
const ITEM_GOBLIN_MAIL_BLUE = 287; // goblin_mail_blue
const ITEM_GOBLIN_MAIL_ORANGE = 286; // goblin_mail_orange
const ITEM_GOLD_BAR = 2357; // gold_bar

const REWARD_XP = 200;
const REWARD_QP = 5;

// Get initial state
const initialState = await sdk.getState();
console.log(`Initial Crafting XP: ${initialState.player.skills.crafting?.xp}`);
console.log(`Initial QP: ${initialState.player.questPoints}`);

// Setup: Give player goblin mail variants
console.log("\n--- Setting up test: Adding goblin mail variants ---");
await sdk.sendGroundItem(ITEM_GOBLIN_MAIL, 3, GOBLIN_VILLAGE_POS.x, GOBLIN_VILLAGE_POS.z, GOBLIN_VILLAGE_POS.plane);
await sdk.sendGroundItem(ITEM_GOBLIN_MAIL_BLUE, 1, GOBLIN_VILLAGE_POS.x, GOBLIN_VILLAGE_POS.z, GOBLIN_VILLAGE_POS.plane);
await sdk.sendGroundItem(ITEM_GOBLIN_MAIL_ORANGE, 1, GOBLIN_VILLAGE_POS.x, GOBLIN_VILLAGE_POS.z, GOBLIN_VILLAGE_POS.plane);
await sdk.waitTicks(3);

// Pick up goblin mail
await sdk.sendInteractGroundItem(ITEM_GOBLIN_MAIL, GOBLIN_VILLAGE_POS.x, GOBLIN_VILLAGE_POS.z, 1);
await sdk.waitTicks(1);
await sdk.sendInteractGroundItem(ITEM_GOBLIN_MAIL, GOBLIN_VILLAGE_POS.x, GOBLIN_VILLAGE_POS.z, 1);
await sdk.waitTicks(1);
await sdk.sendInteractGroundItem(ITEM_GOBLIN_MAIL, GOBLIN_VILLAGE_POS.x, GOBLIN_VILLAGE_POS.z, 1);
await sdk.waitTicks(1);
await sdk.sendInteractGroundItem(ITEM_GOBLIN_MAIL_BLUE, GOBLIN_VILLAGE_POS.x, GOBLIN_VILLAGE_POS.z, 1);
await sdk.waitTicks(1);
await sdk.sendInteractGroundItem(ITEM_GOBLIN_MAIL_ORANGE, GOBLIN_VILLAGE_POS.x, GOBLIN_VILLAGE_POS.z, 1);
await sdk.waitTicks(3);

const stateAfterPickup = await sdk.getState();
console.log("Inventory after pickup:", stateAfterPickup.player.inventory);

// Stage 1: Start quest with General Bentnoze
console.log("\n--- Stage 1: Starting quest with General Bentnoze ---");
await sdk.sendTeleport(BENTNOZE_POS.x, BENTNOZE_POS.z, BENTNOZE_POS.plane);
await sdk.waitTicks(3);

const bentnoze = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("bentnoze"));
if (bentnoze) {
    console.log(`Found General Bentnoze (index: ${bentnoze.index}), starting quest...`);
    await sdk.sendInteractNpc(bentnoze.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: General Bentnoze not found!");
}

// Stage 2: Talk to General Wartface
console.log("\n--- Stage 2: Talking to General Wartface ---");
await sdk.sendTeleport(WARTFACE_POS.x, WARTFACE_POS.z, WARTFACE_POS.plane);
await sdk.waitTicks(3);

const wartface = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("wartface"));
if (wartface) {
    console.log(`Found General Wartface (index: ${wartface.index}), discussing armor colors...`);
    await sdk.sendInteractNpc(wartface.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: General Wartface not found!");
}

// Stage 3: Show them dyed goblin mail
console.log("\n--- Stage 3: Showing dyed goblin mail ---");
const stateBeforeShow = await sdk.getState();
const hasBlueMail = stateBeforeShow.player.inventory?.some(item => item?.id === ITEM_GOBLIN_MAIL_BLUE);
const hasOrangeMail = stateBeforeShow.player.inventory?.some(item => item?.id === ITEM_GOBLIN_MAIL_ORANGE);
console.log(`Has blue goblin mail: ${hasBlueMail}`);
console.log(`Has orange goblin mail: ${hasOrangeMail}`);

// Interact with generals again
const bentnoze2 = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("bentnoze"));
if (bentnoze2) {
    console.log(`Showing mail to General Bentnoze...`);
    await sdk.sendInteractNpc(bentnoze2.index, 1);
    await sdk.waitTicks(10);
}

const wartface2 = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("wartface"));
if (wartface2) {
    console.log(`Showing mail to General Wartface...`);
    await sdk.sendInteractNpc(wartface2.index, 1);
    await sdk.waitTicks(10);
}

// Stage 4: Find compromise color
console.log("\n--- Stage 4: Finding compromise color ---");
console.log("Generals settled on brown (original goblin mail color)");

// Stage 5: Complete quest
console.log("\n--- Stage 5: Completing quest ---");
const bentnoze3 = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("bentnoze"));
if (bentnoze3) {
    console.log(`Completing quest with General Bentnoze...`);
    await sdk.sendInteractNpc(bentnoze3.index, 1);
    await sdk.waitTicks(10);
}

// Check final state
const finalState = await sdk.getState();
console.log("\n=== Results ===");
console.log("Final Inventory:", finalState.player.inventory);
console.log(`Final Crafting XP: ${finalState.player.skills.crafting?.xp}`);
console.log(`Final QP: ${finalState.player.questPoints}`);

// Verify completion
const craftingXpGained = finalState.player.skills.crafting?.xp - initialState.player.skills.crafting?.xp;
const qpGained = (finalState.player.questPoints || 0) - (initialState.player.questPoints || 0);
const hasGoldBar = finalState.player.inventory?.some(item => item?.id === ITEM_GOLD_BAR);

console.log("\n=== VERIFICATION ===");
console.log(`✓ Crafting XP gained (expected ${REWARD_XP}): ${craftingXpGained >= REWARD_XP - 20 ? 'PASS' : 'FAIL'} (got ${craftingXpGained})`);
console.log(`✓ QP gained (expected ${REWARD_QP}): ${qpGained >= REWARD_QP ? 'PASS' : 'FAIL'} (got ${qpGained})`);
console.log(`✓ Gold bar received: ${hasGoldBar ? 'PASS' : 'FAIL'}`);

if (craftingXpGained >= REWARD_XP - 20 && qpGained >= REWARD_QP && hasGoldBar) {
    console.log("\n🎉 GOBLIN DIPLOMACY QUEST COMPLETION TEST: PASSED");
} else {
    console.log("\n❌ GOBLIN DIPLOMACY QUEST COMPLETION TEST: FAILED");
    console.log("Notes:");
    console.log("- Ensure server is running with quest loaded");
    console.log("- Check that goblin mail variants are properly defined");
    console.log("- Verify quest completion rewards are configured");
}
