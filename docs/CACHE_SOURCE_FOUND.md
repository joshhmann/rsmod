# Cache Source Confirmed

## Source of Truth

RSMod cache downloads are pinned to OpenRS2 cache `runescape/2293`:

- Cache archive: `https://archive.openrs2.org/caches/runescape/2293/disk.zip`
- XTEAs: `https://archive.openrs2.org/caches/runescape/2293/keys.json`
- OpenRS2 page: `https://archive.openrs2.org/caches/runescape/2293`

From OpenRS2 metadata:
- `build.major = 233`
- `built = 2025-09-10T16:47:47Z`

## Implication

All runtime symbol/ID work must align to rev 233 as represented by `runescape/2293`.

Primary files:
- `rsmod/.data/symbols/obj.sym`
- `rsmod/.data/symbols/npc.sym`
- `rsmod/.data/symbols/loc.sym`

If wiki naming or external references conflict, cache symbols take priority.

