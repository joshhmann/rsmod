---
name: rsmod-symbol-guardian
description: Guard RSMod rev 233 symbol, cache-ID, and content-reference workflows. Use before adding or changing `.sym` files, `Base*` refs, cache enrichers, wiki-data IDs, NPC/obj/loc/seq IDs, generated drop tables, or content that depends on OSRS cache names.
---

# RSMod Symbol Guardian

Use this skill as a hard gate before symbol-sensitive RSMod work. The goal is boring correctness:
content adapts to rev 233 symbols; symbols are not renamed to make content compile.

## Non-Negotiables

1. Treat `rsmod/.data/symbols/*.sym` from rev 233 OpenRS2 `runescape/2293` as the runtime ID authority.
2. Do not edit base `.data/symbols/*.sym`.
3. Do not add `.local/*.sym` aliases for existing base IDs just to get wiki-style names.
4. Do not map functional refs to `blankobject`, `nothing_`, or any shared dummy ID.
5. Do not use raw wiki IDs directly in Kotlin when a symbol exists.
6. Do not use `find("name", fallbackId)` for cache-backed IDs.
7. Do not promote generated scraper output into runtime code until `validateSymbols` and `packCache` pass.

## Required Workflow

1. Read `rsmod/docs/REV233_SYMBOL_WORKFLOW.md`.
2. Build a symbol manifest before editing Kotlin/TOML/wiki-data:
   ```text
   Feature: <name>
   objs:
   - wiki: <name> | sym: <canonical obj.sym name> | id: <id> | source: .data/symbols/obj.sym:<line>
   npcs:
   - wiki: <name> | sym: <canonical npc.sym name> | id: <id> | source: .data/symbols/npc.sym:<line>
   locs:
   - wiki: <name> | sym: <canonical loc.sym name> | id: <id> | source: .data/symbols/loc.sym:<line>
   seqs/spots/projanims:
   - wiki: <name> | sym: <canonical .sym name> | id: <id> | source: .data/symbols/<type>.sym:<line>
   ```
3. Search local symbols with `rg` first:
   ```powershell
   rg -n "partial_name" .data\symbols\obj.sym .data\symbols\.local\obj.sym
   rg -n "partial_name" .data\symbols\npc.sym .data\symbols\loc.sym
   ```
4. Use MCP cache tools when available to confirm ambiguous candidates, but still record the final `.sym` line.
5. Update code to use canonical refs or module-local wrapper names that resolve to canonical `find("<sym>")` calls.
6. If a symbol is genuinely custom/server-only, add it only in `.data/symbols/.local/<type>.sym` using an unused ID and document why it is not cache-backed.
7. Validate in this order:
   ```powershell
   & 'C:\Program Files\PowerShell\7\pwsh.exe' -File "scripts\preflight-ref-hygiene.ps1" -FailOnIssues
   & 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat validateSymbols --console=plain"
   & 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat packCache --console=plain"
   & 'C:\Program Files\PowerShell\7\pwsh.exe' -Command ".\gradlew.bat <scoped-module>:build --console=plain"
   ```
8. If the change touches startup/global config/cache builders, run strict boot:
   ```powershell
   & 'C:\Program Files\PowerShell\7\pwsh.exe' -File "scripts\strict-boot-probe.ps1" -TimeoutSeconds 120
   ```

## Decision Rules

- If wiki name differs from `.sym`, use `.sym`.
- If multiple `.sym` candidates exist, pick by exact cache identity and content context, not by prettiest name.
- If no `.sym` candidate exists, stop and classify the need:
  - post-rev/out-of-scope content,
  - custom server-only type,
  - generated cache edit bug,
  - missing symbol extraction.
- If `packCache` reports `internalId=-1`, fix the generator/ref source. Do not paper over it with aliases.
- If a prior agent added an alias for a base ID, prefer removing the alias and updating Kotlin to canonical names.

## Common Failure Signatures

- `NameIdOverlap`: two names claim the same ID in one loaded file. Remove the duplicate or use the canonical base symbol.
- `cache edits use names that are not defined in a .sym file`: generated type builders or enrichers are producing names that do not resolve. Fix the generator/config input.
- `Duplicate key` during enum/object editor resolution: multiple functional refs were mapped to the same dummy object.
- `Invalid hash`: a fallback ID or wrong local mapping disagrees with cache identity.

## Handoff Output

Always include:

- Symbol manifest.
- Changed symbol/ref files.
- Exact validation commands and results.
- First failing line if blocked.
