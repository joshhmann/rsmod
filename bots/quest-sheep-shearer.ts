// Sheep Shearer Quest Complete Test Script
// Location: Lumbridge sheep pen area (x: 3192, z: 3275)
// NPC: Fred the Farmer (fred_the_farmer)
// Items: Wool (1737), Ball of wool (1759)
// Reward: 150 Crafting XP, 60 coins, 1 Quest Point

console.log("=== Sheep Shearer Quest Complete Test ===");

// Test configuration
const FRED_POS = { x: 3192, z: 3275, plane: 0 };
const SHEEP_POS = { x: 3196, z: 3266, plane: 0 }; // Near sheep

const ITEM_WOOL = 1737;
const ITEM_BALL_OF_WOOL = 1759;
const ITEM_SHEARS = 1735;

const WOOL_REQ = 20;
const REWARD_XP = 150;
const REWARD_COINS = 60;
const REWARD_QP = 1;

// Get initial state
const initialState = await sdk.getState();
console.log(`Initial Crafting XP: ${initialState.player.skills.crafting?.xp}`);
console.log(`Initial Crafting Level: ${initialState.player.skills.crafting?.level}`);
console.log(`Initial QP: ${initialState.player.questPoints}`);

// Setup: Give player shears and wool
console.log("\n--- Setting up test: Adding shears and wool ---");
await sdk.sendGroundItem(ITEM_SHEARS, 1, FRED_POS.x, FRED_POS.z, FRED_POS.plane);
await sdk.waitTicks(2);
await sdk.sendInteractGroundItem(ITEM_SHEARS, FRED_POS.x, FRED_POS.z, 1);
await sdk.waitTicks(3);

// Spawn wool (simulating shearing)
for (let i = 0; i < WOOL_REQ; i++) {
    await sdk.sendGroundItem(ITEM_WOOL, 1, SHEEP_POS.x, SHEEP_POS.z, SHEEP_POS.plane);
}
await sdk.waitTicks(3);

// Pick up wool
for (let i = 0; i < WOOL_REQ; i++) {
    await sdk.sendInteractGroundItem(ITEM_WOOL, SHEEP_POS.x, SHEEP_POS.z, 1);
    await sdk.waitTicks(1);
}

// Convert wool to balls of wool (simulating spinning)
console.log("\n--- Converting wool to balls of wool ---");
const stateAfterWool = await sdk.getState();
const woolCount = stateAfterWool.player.inventory?.filter(item => item?.id === ITEM_WOOL).length || 0;
console.log(`Wool collected: ${woolCount}`);

// Spawn balls of wool (simulating spinning wheel)
for (let i = 0; i < WOOL_REQ; i++) {
    await sdk.sendGroundItem(ITEM_BALL_OF_WOOL, 1, FRED_POS.x, FRED_POS.z, FRED_POS.plane);
}
await sdk.waitTicks(3);

// Pick up balls of wool
for (let i = 0; i < WOOL_REQ; i++) {
    await sdk.sendInteractGroundItem(ITEM_BALL_OF_WOOL, FRED_POS.x, FRED_POS.z, 1);
    await sdk.waitTicks(1);
}

const stateAfterBalls = await sdk.getState();
const ballsCount = stateAfterBalls.player.inventory?.filter(item => item?.id === ITEM_BALL_OF_WOOL).length || 0;
console.log(`Balls of wool: ${ballsCount}`);

// Stage 1: Start quest with Fred
console.log("\n--- Stage 1: Starting quest with Fred ---");
await sdk.sendTeleport(FRED_POS.x, FRED_POS.z, FRED_POS.plane);
await sdk.waitTicks(3);

const fred = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("fred"));
if (fred) {
    console.log(`Found Fred the Farmer (index: ${fred.index}), starting quest...`);
    await sdk.sendInteractNpc(fred.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Fred not found!");
}

// Stage 2: Complete quest with Fred
console.log("\n--- Stage 2: Completing quest with Fred ---");
const fred2 = (await sdk.getState()).player.nearbyNpcs?.find(npc => npc.name?.toLowerCase().includes("fred"));
if (fred2) {
    console.log(`Found Fred (index: ${fred2.index}), completing quest...`);
    await sdk.sendInteractNpc(fred2.index, 1);
    await sdk.waitTicks(10);
} else {
    console.log("ERROR: Fred not found!");
}

// Check final state
const finalState = await sdk.getState();
console.log("\n=== Results ===");
console.log("Final Inventory:", finalState.player.inventory);
console.log(`Final Crafting XP: ${finalState.player.skills.crafting?.xp}`);
console.log(`Final Crafting Level: ${finalState.player.skills.crafting?.level}`);
console.log(`Final QP: ${finalState.player.questPoints}`);

// Verify completion
const craftingXpGained = finalState.player.skills.crafting?.xp - initialState.player.skills.crafting?.xp;
const qpGained = (finalState.player.questPoints || 0) - (initialState.player.questPoints || 0);
const coinsReward = finalState.player.inventory?.find(i => i?.id === 995)?.qty || 0;
const coinsGained = coinsReward - (initialState.player.inventory?.find(i => i?.id === 995)?.qty || 0);

console.log("\n=== VERIFICATION ===");
console.log(`✓ Crafting XP gained (expected ${REWARD_XP}): ${craftingXpGained >= REWARD_XP - 10 ? 'PASS' : 'FAIL'} (got ${craftingXpGained})`);
console.log(`✓ Coins received (expected ${REWARD_COINS}): ${coinsGained >= REWARD_COINS - 5 ? 'PASS' : 'FAIL'} (got ${coinsGained})`);
console.log(`✓ QP gained (expected ${REWARD_QP}): ${qpGained >= REWARD_QP ? 'PASS' : 'FAIL'} (got ${qpGained})`);

if (craftingXpGained >= REWARD_XP - 10 && coinsGained >= REWARD_COINS - 5 && qpGained >= REWARD_QP) {
    console.log("\n🎉 SHEEP SHEARER QUEST COMPLETION TEST: PASSED");
} else {
    console.log("\n❌ SHEEP SHEARER QUEST COMPLETION TEST: FAILED");
    console.log("Notes:");
    console.log("- Ensure server is running with quest loaded");
    console.log("- Check that wool and balls of wool are properly defined");
    console.log("- Verify quest completion rewards are configured");
}
