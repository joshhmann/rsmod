/**
 * Script Runner - RSMod Bot Script Framework
 * 
 * Provides structured script execution with:
 * - Automatic connection management
 * - Timeout handling
 * - Console capture
 * - State tracking
 * - Result formatting
 * 
 * Based on rs-sdk's runScript pattern
 */

import { createSdkApi } from "../../mcp/sdk-api.js";
import { createBotApi } from "../../mcp/bot-api.js";
import { createBotActions, type ActionResult } from "./bot-actions.js";

export interface ScriptContext {
  /** High-level bot actions (waits for effects) */
  actions: ReturnType<typeof createBotActions>;
  /** Low-level SDK (immediate responses) */
  sdk: ReturnType<typeof createSdkApi>;
  /** Mid-level bot API */
  bot: ReturnType<typeof createBotApi>;
  /** Logging (captured) */
  log: (...args: unknown[]) => void;
  /** Warning logging */
  warn: (...args: unknown[]) => void;
  /** Error logging */
  error: (...args: unknown[]) => void;
  /** Player name */
  player: string;
}

export interface RunOptions {
  /** Overall timeout in milliseconds (default: no timeout) */
  timeout?: number;
  /** Auto-connect if not connected (default: true) */
  autoConnect?: boolean;
  /** Disconnect when done (default: false) */
  disconnectAfter?: boolean;
  /** Show final state (default: true) */
  showFinalState?: boolean;
}

export interface LogEntry {
  time: number;
  level: "log" | "warn" | "error";
  message: string;
}

export interface RunResult<T = unknown> {
  /** Whether the script completed without errors */
  success: boolean;
  /** Return value from the script */
  result?: T;
  /** Error if script failed */
  error?: Error;
  /** Total execution time in ms */
  duration: number;
  /** Captured logs */
  logs: LogEntry[];
  /** Final world state */
  finalState: unknown;
}

/**
 * Run a bot script with full lifecycle management
 * 
 * @example
 * ```typescript
 * const result = await runScript("TestBot", async (ctx) => {
 *   const { actions, log } = ctx;
 *   
 *   log("Starting woodcutting...");
 *   const tree = await actions.findNearbyLoc(/tree/i);
 *   if (tree) {
 *     const result = await actions.chopTree(tree.id, tree.x, tree.z);
 *     log("Chopped:", result.logsReceived, "logs");
 *   }
 *   
 *   return { done: true };
 * }, { timeout: 60000 });
 * ```
 */
export async function runScript<T = unknown>(
  player: string,
  scriptFn: (ctx: ScriptContext) => Promise<T>,
  options: RunOptions = {}
): Promise<RunResult<T>> {
  const {
    timeout,
    autoConnect = true,
    showFinalState = true,
  } = options;

  const startTime = Date.now();
  const logs: LogEntry[] = [];

  // Create logging functions
  const makeLogger = (level: LogEntry["level"]) => (...args: unknown[]) => {
    logs.push({
      time: Date.now() - startTime,
      level,
      message: args.map(a => typeof a === "object" ? JSON.stringify(a) : String(a)).join(" "),
    });
  };

  const log = makeLogger("log");
  const warn = makeLogger("warn");
  const error = makeLogger("error");

  try {
    // Initialize APIs
    const sdk = createSdkApi(player);
    const bot = createBotApi(sdk);
    const actions = createBotActions(player);

    // Check connection
    if (autoConnect) {
      const state = sdk.getState();
      if (!state) {
        log("Waiting for player connection...");
        // Wait up to 10 seconds for connection
        let attempts = 0;
        while (!sdk.getState() && attempts < 100) {
          await sleep(100);
          attempts++;
        }
        if (!sdk.getState()) {
          throw new Error(`Could not connect to player: ${player}`);
        }
        log("Connected!");
      }
    }

    // Create script context
    const ctx: ScriptContext = {
      actions,
      sdk,
      bot,
      log,
      warn,
      error,
      player,
    };

    // Run script with optional timeout
    let result: T;
    if (timeout && timeout > 0) {
      result = await Promise.race([
        scriptFn(ctx),
        new Promise<never>((_, reject) => 
          setTimeout(() => reject(new Error(`Script timeout after ${timeout}ms`)), timeout)
        ),
      ]);
    } else {
      result = await scriptFn(ctx);
    }

    // Get final state
    const finalState = showFinalState ? sdk.getState() : null;
    const duration = Date.now() - startTime;

    return {
      success: true,
      result,
      duration,
      logs,
      finalState,
    };

  } catch (err) {
    const duration = Date.now() - startTime;
    const error = err instanceof Error ? err : new Error(String(err));

    return {
      success: false,
      error,
      duration,
      logs,
      finalState: null,
    };
  }
}

/**
 * Run a script and print formatted results to console
 */
export async function runScriptInteractive<T = unknown>(
  player: string,
  scriptFn: (ctx: ScriptContext) => Promise<T>,
  options: RunOptions = {}
): Promise<void> {
  console.log(`🤖 Running script for ${player}...\n`);

  const result = await runScript(player, scriptFn, options);

  // Print logs
  if (result.logs.length > 0) {
    console.log("═══ Logs ═══");
    for (const log of result.logs) {
      const time = (log.time / 1000).toFixed(1);
      const icon = log.level === "error" ? "❌" : log.level === "warn" ? "⚠️" : "📝";
      console.log(`[${time}s] ${icon} ${log.message}`);
    }
    console.log();
  }

  // Print result
  console.log("═══ Result ═══");
  if (result.success) {
    console.log("✅ Success");
    if (result.result !== undefined) {
      console.log("Return value:", JSON.stringify(result.result, null, 2));
    }
  } else {
    console.log("❌ Failed:", result.error?.message);
  }
  console.log(`Duration: ${(result.duration / 1000).toFixed(1)}s`);

  // Print final state summary
  if (result.finalState) {
    console.log("\n═══ Final State ═══");
    const state = result.finalState as any;
    console.log(`Position: (${state.player.position.x}, ${state.player.position.z})`);
    console.log(`Inventory: ${state.player.inventory.length} items`);
    console.log(`Nearby: ${state.player.nearbyNpcs?.length ?? 0} NPCs, ${state.player.nearbyLocs?.length ?? 0} objects`);
  }
}

/**
 * Sleep helper
 */
export function sleep(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * Retry a function with exponential backoff
 */
export async function retry<T>(
  fn: () => Promise<T>,
  options: {
    maxAttempts?: number;
    delay?: number;
    backoff?: number;
    onRetry?: (attempt: number, error: Error) => void;
  } = {}
): Promise<T> {
  const {
    maxAttempts = 3,
    delay = 1000,
    backoff = 2,
    onRetry,
  } = options;

  let lastError: Error;
  let currentDelay = delay;

  for (let attempt = 1; attempt <= maxAttempts; attempt++) {
    try {
      return await fn();
    } catch (err) {
      lastError = err instanceof Error ? err : new Error(String(err));
      
      if (attempt === maxAttempts) {
        throw lastError;
      }

      if (onRetry) {
        onRetry(attempt, lastError);
      }

      await sleep(currentDelay);
      currentDelay *= backoff;
    }
  }

  throw lastError!;
}

/**
 * Wait for a condition with timeout
 */
export async function waitFor(
  condition: () => boolean | Promise<boolean>,
  options: {
    timeout?: number;
    interval?: number;
    message?: string;
  } = {}
): Promise<boolean> {
  const {
    timeout = 10000,
    interval = 100,
    message = "Condition not met",
  } = options;

  const startTime = Date.now();
  
  while (Date.now() - startTime < timeout) {
    if (await condition()) {
      return true;
    }
    await sleep(interval);
  }

  return false;
}

export default { runScript, runScriptInteractive, sleep, retry, waitFor };
