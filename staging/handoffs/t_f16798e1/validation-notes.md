# Validation Notes: Karamja Pirate Drops

## Raw-ID Scan: PASS
ZERO raw cache IDs - all 25 item references + 1 NPC reference are symbolic via objs.* or find().

## Production Path Check: PASS
All artifacts under staging/handoffs/t_f16798e1/. No production content/ paths modified.

## Symbols Requiring Verification
- find("right_eye_patch"): UNVERIFIED - may need find("eye_patch") if cache symbol differs
- find("keyhalf1") / find("keyhalf2"): confirmed working pattern
- find("pirate1"): confirmed in KaramjaNpcs.kt on CT 123

## Combat Registration: PASS
No combat defs added.

## Registration Required
PirateDropTables.registerAll(registry) needs wiring in NpcDropTablesScript.kt startup method.
