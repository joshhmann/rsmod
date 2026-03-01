# Revision Lock Policy (Rev 233)

This project is **locked to OSRS revision 233** until explicitly changed by the project owner.

Canonical source for this lock:
- OpenRS2 cache: https://archive.openrs2.org/caches/runescape/2293
- Build major: `233`
- Built at: `2025-09-10T16:47:47Z`

## Policy

1. Treat rev 233 as the single source of truth for runtime behavior and IDs.
2. Use donor repos (Alter/Kronos/317/2004scape/etc.) for behavior patterns only, never direct ID/opcode copy.
3. Resolve all Kotlin refs through rev 233 symbols in `rsmod/.data/symbols/`.
4. Reject content introduced after rev 233 unless it is clearly flagged as out-of-scope research.
5. If a source conflicts with rev 233 symbols or packet expectations, rev 233 wins.

## Mandatory Checks Before Marking Work Complete

1. ID validation:
   - Items: `obj.sym`
   - NPCs: `npc.sym`
   - Locs: `loc.sym`
2. Protocol/packet changes: validate against rev 233 references before merge.
3. Wiki data: filter for rev 233 compatibility, then map to symbols.
4. Quest/skill constants: no raw wiki IDs in Kotlin when a symbol exists.

## Change Control

Only the project owner can authorize a revision migration.

Any migration proposal must include:
1. Target revision and reason.
2. Packet/protocol impact assessment.
3. Symbol/cache remap plan.
4. Regression plan for core systems and F2P content.

Until that plan is approved, all agents must continue implementing for rev 233.

