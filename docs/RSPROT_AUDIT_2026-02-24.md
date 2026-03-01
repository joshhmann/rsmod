# rsprot Update Pipeline Audit (2026-02-24)

## Scope
- Verify whether rsprot already implements the main PlayerInfo/NpcInfo optimization ideas discussed in external design notes.
- Focused on locally cached sources for:
  - `net.rsprot:osrs-233-model:1.0.0-ALPHA-20250909`
  - `net.rsprot:osrs-233-internal:1.0.0-ALPHA-20250909`
  - `net.rsprot:osrs-233-desktop:1.0.0-ALPHA-20250909`

## Evidence: Already Implemented

1. Precompute vs on-demand extended info split exists.
- `internal/.../encoder/PrecomputedExtendedInfoEncoder.kt`
  - explicitly states data is encoded early and copied later.
- `internal/.../encoder/OnDemandExtendedInfoEncoder.kt`
  - explicitly reserved for observer-dependent encoding.

2. PlayerInfo tracks and batches extended info writes.
- `model/.../playerinfo/PlayerInfo.kt`
  - `extendedInfoIndices` and `extendedInfoCount` batching.
  - comments call out CPU cache locality and batched block writing.

3. Packet bit-writing path includes branchless skip/stationary encoding optimization.
- `model/.../playerinfo/PlayerInfo.kt`
  - `pStationary` uses branchless bit-count opcode computation.
  - comment notes branch-based variant has measurable extreme-case loss.

4. Movement update buffers are prepared and reused.
- `model/.../playerinfo/PlayerInfo.kt`
  - `prepareHighResMovement()` caches movement bit payload.
  - high-res movement uses compact precomputed bit buffer objects.

5. NpcInfo path assembles pre-built movement/extended info with buffered packet output.
- `model/.../npcinfo/NpcInfo.kt`
  - staged processing for high/low resolution and `putExtendedInfo(...)`.

6. Desktop message encoders just transfer prepared packet bytes.
- `desktop/.../codec/playerinfo/PlayerInfoEncoder.kt`
- `desktop/.../codec/npcinfo/NpcInfoSmallV5Encoder.kt`
- `desktop/.../codec/npcinfo/NpcInfoLargeV5Encoder.kt`
  - each writes `message.content()` directly.

## Inference
- Based on the source structure above, rsprot already contains the core architecture behind:
  - pre-built extended blocks,
  - batched writes,
  - compact bit-level update encoding,
  - buffer-copy final encode path.
- This does not prove there is zero performance headroom; it does indicate re-implementing a parallel packet system in RSMod is likely redundant and high-risk.

## Still Unknown / Needs Runtime Confirmation
- Real production bottleneck split between:
  - RSMod side prep (`RspCycle` + zone/entity prep),
  - rsprot packet assembly,
  - Netty write pressure/backpressure.
- Observer-dependent block frequency cost under large local populations.

## Next Actions (Implemented in this sprint)
- Keep rsprot as source of truth for protocol encoding.
- Add RSMod-side opt-in telemetry:
  - `rsmod.telemetry.rsprot-updates=true` (already added in `RspCycle`).
  - `rsmod.telemetry.zone-updates=true` (added in `PlayerZoneUpdateProcessor`).
- Profile with telemetry enabled under realistic bot population before any protocol rewrite decision.

