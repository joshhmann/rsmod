# Lumbridge Castle Inner Double Doors — Implementation

> Implemented: 2026-06-19

## Summary

The Lumbridge Castle inner double doors (`hundred_lumbridge_doubledoorl/r`) are now
wired into the generic `DoubleDoorScript` system via content group bindings.

The Lumbridge doors open into the generic open castle double door variants
(`opencastledoubledoorl/r`). On close, the generic doors revert to generic closed
castle doors (`castledoubledoorl/r`) — visually identical, so no gameplay impact.

## Verified Locs

| Symbol | Loc ID | Location | Content Group | next_loc_stage |
|--------|--------|----------|---------------|----------------|
| `hundred_lumbridge_doubledoorl` | 12349 | (3213, 3221, 0) | `content.closed_left_door` | `opencastledoubledoorl` (1522) |
| `hundred_lumbridge_doubledoorr` | 12350 | (3213, 3222, 0) | `content.closed_right_door` | `opencastledoubledoorr` (1525) |

## Files Modified

| File | Change |
|------|--------|
| `LumbridgeLocs.kt` | +4 loc refs (2 closed + 2 open variants), +1 LocEditor object |

## How It Works

1. Player clicks left or right hundred_lumbridge_doubledoor
2. Generic `DoubleDoorScript` fires for `content.closed_left_door` or `content.closed_right_door`
3. Reads `next_loc_stage` → `opencastledoubledoorl/r` (same model, open frame)
4. Reads `opensound` → `synths.door_open`
5. Deletes closed door, adds open variant at swung-open coordinates
6. After 500 cycles, door reverts to closed state
7. Open variant's `next_loc_stage` points to generic `castledoubledoorl/r` (visually identical)

## Commands Run

```text
./gradlew :content:areas:city:lumbridge:compileKotlin → BUILD SUCCESS
0 raw IDs in Lumbridge content
```
