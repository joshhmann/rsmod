// Test Thieving Script for kimi bot
// Pickpocket Man/Woman in Lumbridge

console.log("=== Starting Thieving Test ===");

// Get initial state
const state = await sdk.getState();
console.log(`Position: (${state.player.position.x}, ${state.player.position.z})`);
console.log("Thieving level:", state.player.skills.thieving?.level);
console.log("Current XP:", state.player.skills.thieving?.xp);
console.log("Inventory:", state.player.inventory.length, "items");

// Look for Man or Woman NPC to pickpocket
console.log("\nLooking for Man or Woman to pickpocket...");
const targetNpc = state.player.nearbyNpcs?.find(npc => 
  npc.name?.toLowerCase().includes("man") || 
  npc.name?.toLowerCase().includes("woman")
);

if (!targetNpc) {
  console.log("❌ No Man/Woman found nearby!");
  console.log("Nearby NPCs:", state.player.nearbyNpcs?.map(n => `${n.name} (ID:${n.id})`).join(", "));
  
  // Walk to Lumbridge castle area where men/women spawn
  console.log("\nWalking to Lumbridge castle area...");
  await bot.walkTo(3210, 3215);
  
  const newState = await sdk.getState();
  const newTarget = newState.player.nearbyNpcs?.find(npc => 
    npc.name?.toLowerCase().includes("man") || 
    npc.name?.toLowerCase().includes("woman")
  );
  
  if (!newTarget) {
    console.log("❌ Still no target found. Try manually walking to find a Man/Woman.");
    process.exit(1);
  }
}

console.log(`✓ Found target: ${targetNpc.name} (Index: ${targetNpc.index}) at (${targetNpc.x}, ${targetNpc.z})`);

// Walk close to the NPC
console.log(`\nWalking to ${targetNpc.name}...`);
await bot.walkTo(targetNpc.x, targetNpc.z - 1); // Stand next to them

// Wait a moment
await new Promise(r => setTimeout(r, 1000));

// Get initial XP for comparison
const initialState = await sdk.getState();
const initialXp = initialState.player.skills.thieving?.xp || 0;
console.log(`Initial thieving XP: ${initialXp}`);

// Pickpocket attempt
console.log(`\n🦝 Attempting to pickpocket ${targetNpc.name}...`);
console.log("(Option 2 is 'Pickpocket')");

// Send pickpocket interaction - option 2 (onOpNpc2 is pickpocket)
await sdk.sendInteractNpc(targetNpc.index, 2);

// Wait for animation and result
console.log("Waiting for pickpocket result...");
await new Promise(r => setTimeout(r, 3000));

// Check result
const finalState = await sdk.getState();
const finalXp = finalState.player.skills.thieving?.xp || 0;
const xpGained = finalXp - initialXp;

console.log("\n=== RESULT ===");
if (xpGained > 0) {
  console.log(`✅ SUCCESS! Pickpocketed ${targetNpc.name}`);
  console.log(`XP Gained: ${xpGained}`);
  console.log(`New XP Total: ${finalXp}`);
} else {
  console.log(`⚠️ No XP gained. Either failed or stunned.`);
  console.log("Check game messages - you may have been caught!");
}

// Check inventory for loot
console.log("\n=== Inventory Check ===");
const coins = finalState.player.inventory.find(item => item.id === 995); // Coins ID
if (coins) {
  console.log(`💰 Got coins: ${coins.qty}`);
} else {
  console.log("No coins in inventory yet");
}

console.log("Total items in inventory:", finalState.player.inventory.length);

console.log("\n=== Thieving Test Complete ===");
