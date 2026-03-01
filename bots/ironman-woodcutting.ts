/**
 * Ironman Woodcutting Bot
 * 
 * This bot demonstrates legitimate ironman gameplay:
 * 1. Starts with no money
 * 2. Sells starting items for seed money
 * 3. Buys a bronze axe from the general store
 * 4. Chops trees and banks logs
 * 5. Eventually sells logs for profit
 */

export default async function(bot: any) {
  console.log("=== Ironman Woodcutting Bot Started ===");
  
  // Get initial state
  const state = await bot.getState();
  console.log(`Starting at: (${state.position.x}, ${state.position.z})`);
  console.log(`Inventory slots: ${state.inventory.length}/28`);
  
  // Step 1: Check if we have an axe
  const hasAxe = state.inventory.some((item: any) => 
    item?.name?.toLowerCase().includes("axe")
  );
  
  if (!hasAxe) {
    console.log("No axe found! Need to earn money and buy one.");
    
    // Step 2: Get seed money by selling starting items (if any)
    // In a real scenario, you might pick up and sell ashes, bones, etc.
    
    // Step 3: Walk to Lumbridge General Store
    console.log("Walking to Lumbridge General Store...");
    await bot.walkWithDoors(3210, 3244);
    
    // Step 4: Buy bronze axe
    console.log("Buying bronze axe...");
    await bot.openShop("Lumbridge General Store");
    await bot.buyByName("Bronze axe", 1);
    await bot.closeShop();
    
    console.log("Bronze axe acquired!");
  } else {
    console.log("Already have an axe, let's chop!");
  }
  
  // Step 5: Walk to trees (Lumbridge has trees east of the castle)
  console.log("Walking to trees...");
  await bot.walkWithDoors(3222, 3218);
  
  // Step 6: Chop trees until inventory is full
  console.log("Chopping trees...");
  while (true) {
    const currentState = await bot.getState();
    
    // Check if inventory is full
    if (currentState.inventory.filter((i: any) => i !== null).length >= 28) {
      console.log("Inventory full! Going to bank...");
      break;
    }
    
    // Chop a tree
    const result = await bot.chopTree("Tree");
    if (!result.success) {
      console.log("Failed to chop tree, trying again...");
      await bot.waitTicks(5);
    }
  }
  
  // Step 7: Walk to bank and deposit logs
  console.log("Walking to bank...");
  await bot.walkWithDoors(3209, 3220);
  
  console.log("Banking logs...");
  await bot.openBank("Lumbridge Castle Bank");
  await bot.depositAll("logs");
  await bot.closeBank();
  
  console.log("=== Ironman Woodcutting Cycle Complete ===");
  console.log("Logs safely banked! Ready for next cycle.");
}
