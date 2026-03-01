# Rev 233 Date Clarification

## Canonical Answer

RSMod's revision lock is:
- Revision: `233`
- OpenRS2 source: `https://archive.openrs2.org/caches/runescape/2293`
- OpenRS2 metadata:
  - `build.major = 233`
  - `built = 2025-09-10T16:47:47Z`

## Why Confusion Happened

Earlier tooling/docs mixed two different anchors:
1. **Cache/build revision anchor** (OpenRS2 cache metadata; authoritative for IDs/symbols).
2. **Historical wiki snapshot date** (used for content lookup heuristics).

For implementation, symbols and IDs must always follow the cache/build anchor above.

## Operational Rule

1. Resolve IDs/symbols from:
   - `rsmod/.data/symbols/*.sym`
   - `mcp-osrs` / `osrs-cache` tools
2. Treat wiki data as secondary behavior/context data.
3. If wiki conflicts with symbols, symbols win.

