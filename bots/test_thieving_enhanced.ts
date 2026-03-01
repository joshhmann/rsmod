/**
 * Enhanced Thieving Bot - Using the new BotActions API
 * 
 * This bot uses the high-level porcelain methods from BotActions
 * instead of raw SDK calls. Much cleaner and more reliable!
 */

import { runScriptInteractive, sleep } from "./lib/index.js";

const PLAYER = process.argv[2] || "Kimi";

await runScriptInteractive(
  PLAYER,
  async (ctx) => {
    const { actions, sdk, log } = ctx;
    
    log("🥷 Starting thieving session in Lumbridge");
    
    // Walk to Lumbridge castle men
    log("Walking to Lumbridge castle...");
    await actions.walkTo(3222, 3218, 5);
    
    // Find a man
    const state = sdk.getState();
    if (!state) {
      throw new Error("No state available");
    }
    
    // Find man NPC
    const man = state.player.nearbyNpcs?.find(n => 
      n.name?.toLowerCase().includes("man")
    );
    
    if (!man) {
      log("❌ No man found nearby, waiting...");
      await sleep(2000);
      return { status: "no_target" };
    }
    
    log(`🎯 Found target: ${man.name} (index: ${man.index})`);
    
    // Pickpocket using the high-level API!
    log("Attempting pickpocket...");
    const result = await actions.pickpocketNpc(man.index);
    
    log(`Result: ${result.message}`);
    
    if (result.stunned) {
      log("😵 We got stunned! Waiting for recovery...");
    } else if (result.success) {
      log(`💰 Success! Gained ${result.xpGained} XP`);
      if (result.loot && result.loot.length > 0) {
        for (const item of result.loot) {
          log(`  📦 ${item.name} x${item.qty}`);
        }
      }
    } else {
      log("❌ Pickpocket failed");
    }
    
    return {
      status: result.success ? "success" : result.stunned ? "stunned" : "failed",
      xpGained: result.xpGained,
      stunned: result.stunned,
    };
  },
  { timeout: 30000 }
);
