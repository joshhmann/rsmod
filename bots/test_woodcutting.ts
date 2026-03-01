// Test Woodcutting Script for kimi bot
// Trees at Lumbridge: 3192, 3243

console.log("=== Starting Woodcutting Test ===");

// Get initial state
const state = await sdk.getState();
console.log(`Position: (${state.player.position.x}, ${state.player.position.z})`);
console.log("Woodcutting level:", state.player.skills.woodcutting?.level);
console.log("Inventory slots used:", state.player.inventory.length);

// Walk to tree area (if not already there)
const treeX = 3192;
const treeZ = 3243;
console.log(`Walking to tree at (${treeX}, ${treeZ})...`);
await bot.walkTo(treeX, treeZ);

// Find and interact with tree
console.log("Looking for regular tree...");
const tree = state.player.nearbyLocs?.find(loc => loc.id === 1276 || loc.name?.toLowerCase().includes("tree"));

if (tree) {
  console.log(`Found tree: ${tree.name} (ID: ${tree.id}) at (${tree.x}, ${tree.z})`);
  
  // Click tree to chop
  console.log("Clicking tree...");
  await bot.interactLoc(tree.id, tree.x, tree.z, 1);
  
  // Wait for animation and XP
  console.log("Waiting for logs...");
  try {
    const xpGain = await bot.waitForXpGain("woodcutting", 25, 10000);
    console.log("✓ SUCCESS! Got logs!");
    console.log("XP gained:", xpGain.delta);
    console.log("New XP total:", xpGain.current);
  } catch (e) {
    console.log("Timed out waiting for logs. Do you have an axe?");
  }
} else {
  console.log("No tree found nearby. Check coordinates.");
}

// Check final inventory
const finalState = await sdk.getState();
console.log("\n=== Final Inventory ===");
finalState.player.inventory.forEach(item => {
  console.log(`  Slot ${item.slot}: ID=${item.id}, Qty=${item.qty}`);
});

console.log("\n=== Test Complete ===");
