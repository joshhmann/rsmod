# Lumbridge Castle Stairs and Ladders — Verified Coverage Map

> Generated: 2026-06-19
> Source: `tools:placed-loc-exporter` (authoritative OpenRS2 name resolution)
> Generic systems: `generic-locs.ladders.LaddersConfig`, `generic-locs.staircase.StaircaseConfig`

## Coverage Summary

All Lumbridge Castle stairs and ladders are handled by the existing generic loc systems.
No Lumbridge-specific handlers are needed. The generic content groups were already
registered in the upstream rsmod configs and verified functional.

## Coverage Table

| Symbol | Loc ID | Location | Levels | Content Group | Handler |
|--------|--------|----------|--------|---------------|---------|
| `spiralstairsbottom_3` | 56230 | (3204, 3207, 0) | G | `spiralstaircase_up` | SpiralStaircaseScript |
| `spiralstairsmiddle` | 16672 | (3204, 3207, 1) | 1st | `spiralstaircase_option` | Option dialog |
| `spiralstairstop_3` | 56231 | (3205, 3208, 2) | 2nd | `spiralstaircase_down` | Climb down |
| `spiralstairsbottom_3` | 56230 | (3204, 3229, 0) | G | Same as above | Same as above |
| `spiralstairsmiddle` | 16672 | (3204, 3229, 1) | 1st | Same | Same |
| `spiralstairstop_3` | 56231 | (3205, 3229, 2) | 2nd | Same | Same |
| `ladder` | 16683 | (3229, 3213, 0) | G | `ladder_up` | LadderScript |
| `laddermiddle` | 16684 | (3229, 3213, 1) | 1st | `ladder_option` | Option dialog |
| `laddertop` | 16679 | (3229, 3213, 2) | 2nd | `ladder_down` | Climb down |
| `ladder` | 16683 | (3229, 3224, 0) | G | Same as above | Same as above |
| `laddermiddle` | 16684 | (3229, 3224, 1) | 1st | Same | Same |
| `laddertop` | 16679 | (3229, 3224, 2) | 2nd | Same | Same |
| `ladder` | 16683 | (3211, 3242, 0) | G | `ladder_up` | LadderScript |
| `laddertop` | 16679 | (3211, 3242, 1) | 1st | `ladder_down` | Climb down |

## Objects NOT Covered

Stepladder (17390) at (3212, 3250, 0) - Decorative. No matching upper-level placed loc exists.
Cannot be a functional level transition.

## Verification

Build: SUCCESS via both modules
0 raw IDs in Lumbridge content
