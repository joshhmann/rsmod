/**
 * Bots Library - High-level bot scripting utilities
 * 
 * Import from here for all bot scripting needs:
 * 
 * ```typescript
 * import { runScript, BotActions, retry, waitFor } from '../lib/index.js';
 * ```
 */

// Script runner
export { runScript, runScriptInteractive, sleep, retry, waitFor } from "./runner.js";
export type { ScriptContext, RunOptions, RunResult, LogEntry } from "./runner.js";

// BotActions high-level API
export { BotActions, createBotActions } from "./bot-actions.js";
export type {
  ActionResult,
  MoveResult,
  InteractResult,
  PickpocketResult,
  ChopResult,
  BankResult,
} from "./bot-actions.js";

// Re-export low-level APIs for convenience
export { createSdkApi } from "../../mcp/sdk-api.js";
export { createBotApi } from "../../mcp/bot-api.js";
