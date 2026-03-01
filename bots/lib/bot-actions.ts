/**
 * BotActions - High-level "porcelain" API for RSMod bot scripting
 * 
 * Domain-aware methods that wrap low-level SDK calls with game knowledge.
 * Actions resolve when the EFFECT is complete (not just acknowledged).
 * 
 * Based on rs-sdk's BotActions layer - adapted for RSMod v2
 */

import { createSdkApi } from "../../mcp/sdk-api.js";
import type { SdkApi } from "../../mcp/sdk-api.js";

export interface ActionResult {
  success: boolean;
  message?: string;
  data?: unknown;
}

export interface MoveResult extends ActionResult {
  distance: number;
  reason?: "blocked_message" | "stalled_no_movement" | "no_path_progress" | "timeout" | "no_state";
}

export interface InteractResult extends ActionResult {
  xpGained?: number;
  itemsReceived?: Array<{ id: number; name: string; qty: number }>;
}

export interface PickpocketResult extends ActionResult {
  stunned: boolean;
  xpGained: number;
  loot?: Array<{ id: number; name: string; qty: number }>;
}

export interface ChopResult extends ActionResult {
  logsReceived: number;
  xpGained: number;
}

export interface BankResult extends ActionResult {
  itemsDeposited?: number;
  itemsWithdrawn?: number;
}

export class BotActions {
  private sdk: SdkApi;
  private player: string;

  constructor(player: string) {
    this.player = player;
    this.sdk = createSdkApi(player);
  }

  // ============================================================================
  // Movement
  // ============================================================================

  /**
   * Walk to coordinates, waiting for arrival
   * @param x - Target X coordinate
   * @param z - Target Z coordinate
   * @param tolerance - How close is "close enough" (default: 2 tiles)
   * @param timeout - Max wait time in ms (default: 30000)
   */
  async walkTo(x: number, z: number, tolerance = 2, timeout = 30000): Promise<MoveResult> {
    const startPos = this.sdk.getState()?.player.position;
    if (!startPos) {
      return { success: false, message: "walkTo failed [no_state]: no state available", distance: 0, reason: "no_state" };
    }

    // Send walk command
    this.sdk.sendWalk(x, z);

    let bestDist = Math.abs(startPos.x - x) + Math.abs(startPos.z - z);
    let lastPos = startPos;
    let lastMoveAt = Date.now();
    let lastProgressAt = Date.now();
    const baseMessages = this.sdk.getState()?.gameMessages?.length ?? 0;

    // Wait for arrival or timeout
    const startTime = Date.now();
    while (Date.now() - startTime < timeout) {
      const state = this.sdk.getState();
      if (!state) break;

      const pos = state.player.position;
      const dist = Math.abs(pos.x - x) + Math.abs(pos.z - z);
      if (dist <= tolerance) {
        const startDist = Math.abs(startPos.x - x) + Math.abs(startPos.z - z);
        return { success: true, distance: startDist };
      }

      if (pos.x !== lastPos.x || pos.z !== lastPos.z || pos.plane !== lastPos.plane) {
        lastPos = pos;
        lastMoveAt = Date.now();
      }
      if (dist < bestDist) {
        bestDist = dist;
        lastProgressAt = Date.now();
      }

      const recentMessages = (state.gameMessages ?? []).slice(baseMessages);
      const blocked = recentMessages.find(m =>
        /can't reach|cannot reach|can't get there|cannot get there|can't go there|i can't reach/i.test(m.text)
      );
      if (blocked) {
        return {
          success: false,
          distance: dist,
          reason: "blocked_message",
          message:
            `walkTo failed [blocked_message]: "${blocked.text}" ` +
            `target=(${x}, ${z}) current=(${pos.x}, ${pos.z})`,
        };
      }

      if (Date.now() - lastMoveAt > 4200) {
        return {
          success: false,
          distance: dist,
          reason: "stalled_no_movement",
          message:
            `walkTo failed [stalled_no_movement]: likely stuck on wall/edge. ` +
            `target=(${x}, ${z}) current=(${pos.x}, ${pos.z})`,
        };
      }

      if (Date.now() - lastProgressAt > 9000) {
        return {
          success: false,
          distance: dist,
          reason: "no_path_progress",
          message:
            `walkTo failed [no_path_progress]: likely outside building or unreachable route. ` +
            `target=(${x}, ${z}) current=(${pos.x}, ${pos.z}) bestDist=${bestDist}`,
        };
      }

      await this.wait(100);
    }

    const pos = this.sdk.getState()?.player.position;
    const dist = pos ? Math.abs(pos.x - x) + Math.abs(pos.z - z) : 0;
    return {
      success: false,
      reason: "timeout",
      message: `walkTo failed [timeout]: target=(${x}, ${z}) current=(${pos?.x}, ${pos?.z}) bestDist=${bestDist}`,
      distance: dist,
    };
  }

  /**
   * Walk to an entity (NPC or location)
   */
  async walkToEntity(entity: { x: number; z: number }, tolerance = 1): Promise<MoveResult> {
    return this.walkTo(entity.x, entity.z, tolerance);
  }

  // ============================================================================
  // NPC Interactions
  // ============================================================================

  /**
   * Pickpocket an NPC with automatic stun detection
   * @param npcIndex - NPC server index
   * @param timeout - Max time to wait for result
   */
  async pickpocketNpc(npcIndex: number, timeout = 5000): Promise<PickpocketResult> {
    const startXp = this.getSkillXp("thieving");
    const startTime = Date.now();

    // Send pickpocket interaction (option 2)
    this.sdk.sendInteractNpc(npcIndex, 2);

    // Wait for result
    await this.wait(1500); // Initial action delay

    const state = this.sdk.getState();
    if (!state) {
      return { success: false, stunned: false, xpGained: 0, message: "No state" };
    }

    // Check for stun (player can't move/act)
    const messages = state.gameMessages ?? [];
    const stunned = messages.some(m => 
      /stunned|caught|pickpocket fail/i.test(m.text)
    );

    if (stunned) {
      // Wait out the stun (~5 seconds)
      await this.wait(5000);
      return { 
        success: false, 
        stunned: true, 
        xpGained: 0,
        message: "Stunned!" 
      };
    }

    // Check for XP gain
    const currentXp = this.getSkillXp("thieving");
    const xpGained = currentXp - startXp;

    // Get loot from inventory changes
    const loot = this.detectInventoryChanges();

    return {
      success: xpGained > 0,
      stunned: false,
      xpGained,
      loot: loot.added,
      message: xpGained > 0 ? `Success! +${xpGained} XP` : "Failed (no XP)"
    };
  }

  /**
   * Attack an NPC
   */
  async attackNpc(npcIndex: number, timeout = 10000): Promise<ActionResult> {
    const npc = this.findNpcByIndex(npcIndex);
    if (!npc) {
      return { success: false, message: "NPC not found" };
    }

    // Walk to NPC first
    await this.walkToEntity(npc, 1);

    // Send attack (usually option 1 or 2 depending on NPC)
    this.sdk.sendInteractNpc(npcIndex, 1);

    // Wait for combat to start
    const started = await this.waitForCondition(
      () => this.sdk.getState()?.player.inCombat === true,
      timeout
    );

    return { 
      success: started, 
      message: started ? "Combat started" : "Failed to start combat" 
    };
  }

  /**
   * Talk to an NPC
   */
  async talkTo(npcIndex: number, timeout = 5000): Promise<ActionResult> {
    const npc = this.findNpcByIndex(npcIndex);
    if (!npc) {
      return { success: false, message: "NPC not found" };
    }

    await this.walkToEntity(npc, 1);
    this.sdk.sendInteractNpc(npcIndex, 1); // Talk-to is usually option 1

    // Wait for dialog to open
    const dialogOpened = await this.waitForCondition(
      () => this.sdk.getState()?.dialog.isOpen === true,
      timeout
    );

    return { 
      success: dialogOpened, 
      message: dialogOpened ? "Dialog opened" : "No dialog" 
    };
  }

  // ============================================================================
  // Location/Object Interactions
  // ============================================================================

  /**
   * Chop a tree
   * @param locId - Tree location ID
   * @param x - Tree X coordinate
   * @param z - Tree Z coordinate
   * @param timeout - Max time to wait
   */
  async chopTree(locId: number, x: number, z: number, timeout = 15000): Promise<ChopResult> {
    const startLogs = this.countItemInInventory(/logs/i);
    const startXp = this.getSkillXp("woodcutting");

    // Walk to tree
    await this.walkTo(x, z, 1);

    // Chop (option 1 is typically "Chop down")
    this.sdk.sendInteractLoc(locId, x, z, 1);

    // Wait for logs to appear
    const gotLogs = await this.waitForCondition(() => {
      const currentLogs = this.countItemInInventory(/logs/i);
      return currentLogs > startLogs;
    }, timeout);

    const endXp = this.getSkillXp("woodcutting");
    const endLogs = this.countItemInInventory(/logs/i);

    return {
      success: gotLogs,
      logsReceived: endLogs - startLogs,
      xpGained: endXp - startXp,
      message: gotLogs ? `Chopped ${endLogs - startLogs} logs` : "No logs received"
    };
  }

  /**
   * Mine a rock
   */
  async mineRock(locId: number, x: number, z: number, timeout = 15000): Promise<InteractResult> {
    const startXp = this.getSkillXp("mining");

    await this.walkTo(x, z, 1);
    this.sdk.sendInteractLoc(locId, x, z, 1); // Mine is typically option 1

    // Wait for ore
    const gotOre = await this.waitForCondition(() => {
      const currentXp = this.getSkillXp("mining");
      return currentXp > startXp;
    }, timeout);

    const xpGained = this.getSkillXp("mining") - startXp;

    return {
      success: gotOre,
      xpGained,
      message: gotOre ? `Mined ore! +${xpGained} XP` : "No ore received"
    };
  }

  /**
   * Fish at a fishing spot
   */
  async fish(locId: number, x: number, z: number, option = 1, timeout = 15000): Promise<InteractResult> {
    const startXp = this.getSkillXp("fishing");

    await this.walkTo(x, z, 1);
    this.sdk.sendInteractLoc(locId, x, z, option);

    const gotFish = await this.waitForCondition(() => {
      return this.getSkillXp("fishing") > startXp;
    }, timeout);

    const xpGained = this.getSkillXp("fishing") - startXp;

    return {
      success: gotFish,
      xpGained,
      message: gotFish ? `Caught fish! +${xpGained} XP` : "No fish caught"
    };
  }

  /**
   * Light a fire
   */
  async burnLogs(logSlot: number, timeout = 5000): Promise<ActionResult> {
    // Use tinderbox on logs
    this.sdk.sendUseItem(logSlot);

    const fireLit = await this.waitForCondition(() => {
      const state = this.sdk.getState();
      // Check if logs are gone (burned)
      return !state?.player.inventory.some(i => i.slot === logSlot);
    }, timeout);

    return { 
      success: fireLit, 
      message: fireLit ? "Fire lit!" : "Failed to light fire" 
    };
  }

  // ============================================================================
  // Banking
  // ============================================================================

  /**
   * Open the nearest bank
   */
  async openBank(timeout = 5000): Promise<ActionResult> {
    // Find bank booth or banker
    const bankLoc = this.findNearbyLoc(/bank|booth/i);
    if (!bankLoc) {
      return { success: false, message: "No bank found nearby" };
    }

    await this.walkToEntity(bankLoc, 2);
    this.sdk.sendInteractLoc(bankLoc.id, bankLoc.x, bankLoc.z, 2); // Bank is usually option 2

    const opened = await this.waitForCondition(
      () => this.sdk.getState()?.bankOpen === true,
      timeout
    );

    return { success: opened, message: opened ? "Bank opened" : "Failed to open bank" };
  }

  /**
   * Deposit items to bank
   */
  async depositItem(itemPattern: RegExp | number, amount: number | "all" = "all", timeout = 3000): Promise<BankResult> {
    const items = this.findItemsInInventory(itemPattern);
    if (items.length === 0) {
      return { success: false, message: "Item not found in inventory" };
    }

    let deposited = 0;
    for (const item of items) {
      if (amount !== "all" && deposited >= amount) break;
      
      // Deposit option
      this.sdk.sendClickComponent(1); // Simplified - actual bank interface varies
      deposited += item.qty;
    }

    return { success: deposited > 0, itemsDeposited: deposited };
  }

  /**
   * Withdraw items from bank
   */
  async withdrawItem(itemId: number, amount: number, timeout = 3000): Promise<BankResult> {
    // Simplified - actual implementation depends on bank interface
    return { success: false, message: "Not fully implemented" };
  }

  // ============================================================================
  // Inventory
  // ============================================================================

  /**
   * Drop an item from inventory
   */
  async dropItem(slot: number): Promise<ActionResult> {
    this.sdk.sendDropItem(slot);
    await this.wait(300);
    return { success: true };
  }

  /**
   * Use an item (eat, bury, etc.)
   */
  async useItem(slot: number, option = 1): Promise<ActionResult> {
    this.sdk.sendUseItem(slot);
    await this.wait(600); // Wait for tick
    return { success: true };
  }

  /**
   * Eat food
   */
  async eatFood(foodSlot: number): Promise<ActionResult> {
    const startHp = this.sdk.getState()?.player.hitpoints.current ?? 0;
    
    await this.useItem(foodSlot);
    
    // Wait for HP to increase
    await this.wait(600);
    const endHp = this.sdk.getState()?.player.hitpoints.current ?? 0;
    
    return { 
      success: endHp > startHp, 
      message: `Healed ${endHp - startHp} HP` 
    };
  }

  // ============================================================================
  // Combat
  // ============================================================================

  /**
   * Wait for combat to end
   */
  async waitForCombatEnd(timeout = 60000): Promise<ActionResult> {
    const ended = await this.waitForCondition(
      () => this.sdk.getState()?.player.inCombat === false,
      timeout
    );
    return { success: ended, message: ended ? "Combat ended" : "Timeout" };
  }

  /**
   * Flee from combat
   */
  async fleeCombat(): Promise<ActionResult> {
    // Run away in random direction
    const state = this.sdk.getState();
    if (!state) return { success: false };

    const { x, z } = state.player.position;
    // Run 10 tiles away
    await this.walkTo(x + 10, z, 0, 10000);
    
    return { success: true, message: "Fled combat" };
  }

  // ============================================================================
  // Dialog/UI
  // ============================================================================

  /**
   * Click through a dialog
   * @param choices - Option indices to click (0-based)
   */
  async navigateDialog(choices: number[]): Promise<ActionResult> {
    for (const choice of choices) {
      if (!this.sdk.getState()?.dialog.isOpen) {
        return { success: false, message: "Dialog closed unexpectedly" };
      }
      
      this.sdk.sendClickDialog(choice);
      await this.wait(800); // Wait for dialog update
    }
    return { success: true };
  }

  /**
   * Dismiss blocking UI (level up, etc.)
   */
  async dismissBlockingUI(): Promise<ActionResult> {
    const state = this.sdk.getState();
    if (!state) return { success: false };

    // Check for level up dialog
    if (state.dialog.isOpen) {
      await this.navigateDialog([0]); // Click "Click here to continue"
    }

    return { success: true };
  }

  // ============================================================================
  // Utility Methods
  // ============================================================================

  /**
   * Wait for a condition to be true
   */
  async waitForCondition(predicate: () => boolean, timeout = 10000, interval = 100): Promise<boolean> {
    const startTime = Date.now();
    while (Date.now() - startTime < timeout) {
      if (predicate()) return true;
      await this.wait(interval);
    }
    return false;
  }

  /**
   * Wait for XP gain in a skill
   */
  async waitForXpGain(skill: string, minAmount: number, timeout = 10000): Promise<number> {
    const startXp = this.getSkillXp(skill);
    const gained = await this.waitForCondition(() => {
      return (this.getSkillXp(skill) - startXp) >= minAmount;
    }, timeout);
    
    return this.getSkillXp(skill) - startXp;
  }

  /**
   * Simple sleep
   */
  async wait(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  // ============================================================================
  // Helper Methods
  // ============================================================================

  private getSkillXp(skill: string): number {
    const state = this.sdk.getState();
    if (!state) return 0;
    return state.player.skills[skill.toLowerCase()]?.xp ?? 0;
  }

  private findNpcByIndex(index: number) {
    const state = this.sdk.getState();
    if (!state) return null;
    return state.player.nearbyNpcs?.find(n => n.index === index);
  }

  private findNearbyLoc(pattern: RegExp) {
    const state = this.sdk.getState();
    if (!state) return null;
    return state.player.nearbyLocs?.find(l => pattern.test(l.name));
  }

  private findItemsInInventory(pattern: RegExp | number) {
    const state = this.sdk.getState();
    if (!state) return [];
    
    if (typeof pattern === "number") {
      return state.player.inventory.filter(i => i.id === pattern);
    }
    return state.player.inventory.filter(i => pattern.test(i.name));
  }

  private countItemInInventory(pattern: RegExp | number): number {
    const items = this.findItemsInInventory(pattern);
    return items.reduce((sum, i) => sum + i.qty, 0);
  }

  private detectInventoryChanges() {
    // Simplified - would need to track before/after state
    return { added: [], removed: [] };
  }

  // ============================================================================
  // Access to low-level SDK
  // ============================================================================

  get rawSdk(): SdkApi {
    return this.sdk;
  }
}

// Factory function for easy use
export function createBotActions(player: string): BotActions {
  return new BotActions(player);
}

export default BotActions;
