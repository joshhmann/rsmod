# BOTS — Integration Testing

## OVERVIEW
TypeScript bot scripts for RSMod v2 skill validation via MCP AgentBridge.

## STRUCTURE
```
bots/
├── woodcutting.ts             # ✅ Reference template (gold standard)
├── test_thieving.ts           # Pickpocket tests
├── test_thieving_enhanced.ts  # Extended thieving tests
├── test_woodcutting.ts        # Alt woodcutting variant
├── rev233_tester.ts           # Cache data verification
└── lib/                       # Shared utilities
    ├── index.ts
    ├── runner.ts
    └── bot-actions.ts
```

## TESTING PATTERN
1. Teleport to test location via `sdk.sendTeleport(x, z, plane)`
2. Find target object/NPC via `sdk.findNearbyLoc()` / `sdk.findNearbyNpc()`
3. Interact via `sdk.sendInteractLoc()` / `sdk.sendInteractNpc()`
4. Poll for XP gain, animation, item production
5. Record pass/fail with expected vs actual

## KEY API
```typescript
// Movement
sdk.sendTeleport(x, z, plane)
sdk.waitTicks(ticks)           // 1 tick = 600ms

// Interaction
sdk.sendInteractLoc(id, x, z, option)
sdk.findNearbyLoc(name)        // Returns {id, name, x, z} or null
sdk.findNearbyNpc(name)        // Returns {index, name} or null

// State
sdk.getPlayer()                // {animation, skills, inventory, ...}
sdk.getSkill("name")           // {xp, level}
sdk.getInventory()             // Item[] with {id, qty}
```

## CONVENTIONS
- **Template**: Use `woodcutting.ts` as the gold standard
- **Data comments**: Cache-verified IDs at top of file
- **Tiers array**: Test all skill tiers (bronze→rune, normal→yew)
- **Result logging**: Record each check with pass/fail icon
- **Summary block**: Final pass/fail count + failure details
- **Test locations**: Include coordinates from wiki-data/

## ANIMATION CHECKING
```typescript
const anim = sdk.getPlayer()?.animation;
// anim === 0 or 65535 means idle
// Compare against cache seq IDs (e.g., 867 = rune axe wc)
```

## RUNNING
```bash
# Via MCP tool (requires running server + AgentBridge)
run_bot_file { player: "TestBot", file: "woodcutting.ts" }

# Ad-hoc script
execute_script { player: "TestBot", code: "<typescript>" }
```

## PREREQUISITES
- Server running on port 43594
- AgentBridge running on port 43595 (starts after first login)
- Player spawned with required items (::master, ::invadd)

## ADDING NEW TESTS
1. Copy `woodcutting.ts` as template
2. Update TIERS array with skill-specific data
3. Cache-verify IDs via `mcp-osrs-lookup` skill
4. Add test locations to `wiki-data/skills/<name>.json`
