# Zone Update Ordering Review (OSRS Parity)

## Overview
This document reviews the current implementation of zone updates in RSMod v2 and assesses its parity with OSRS regarding packet ordering within a single zone update.

## Current Implementation
In RSMod v2, zone updates are collected in `ZoneUpdateList` using an `ArrayDeque<ZoneProt>`. 
- `ZoneUpdateMap` appends updates to this list as they occur during the game tick.
- `PlayerZoneUpdateProcessor` and `SharedZoneEnclosedBuffers` iterate through these updates in the order they were added (First-In-First-Out).

### Risk
If multiple updates occur in the same zone on the same tick, the order of packets sent to the client is determined solely by the execution order of the server-side logic. In OSRS, the client expects a specific priority order to avoid visual glitches or logic errors (e.g., adding an object before deleting the one previously in its place).

## OSRS Priority Order
Based on standard OSRS protocol documentation, the expected priority order for zone updates is:

1.  **LocDel** (Clear existing locations)
2.  **ObjDel** (Remove existing ground items)
3.  **ObjAdd** (Add new ground items)
4.  **LocAddChange** (Add new locations or change existing ones)
5.  **ObjCount** (Update ground item stack sizes)
6.  **ObjReveal** (Make private items public)
7.  **MapAnim** (Graphics on the map)
8.  **LocAnim** (Location-specific animations)
9.  **SoundArea** (Area sound effects)
10. **MapProjAnim** (Projectiles)

## Recommendations
To ensure strict parity and prevent potential issues, RSMod should sort `ZoneProt` lists before they are enclosed or sent to the client.

### Proposed Priority Mapping
| Protocol Message | Priority |
|------------------|----------|
| `LocDel` | 10 |
| `ObjDel` | 9 |
| `ObjAdd` | 8 |
| `LocAddChange` | 7 |
| `ObjCount` | 6 |
| `ObjReveal` | 5 |
| `MapAnim` | 4 |
| `LocAnim` | 3 |
| `SoundArea` | 2 |
| `MapProjAnim` | 1 |

### Implementation Strategy
1.  Define a `priority` extension property for `ZoneProt` in `ZoneUpdateTransformer.kt`.
2.  Update `SharedZoneEnclosedBuffers.computeSharedBuffers` to sort the `protList` by priority before encoding.
3.  Update `PlayerZoneUpdateProcessor.sendZonePartialFollowsUpdates` to sort the `filtered` list by priority before writing to the client.

## Conclusion
The current "as-it-happens" ordering is functional for simple scenarios but lacks the robustness of the official OSRS protocol. Implementing priority-based sorting will ensure long-term mechanical accuracy and stability.

