# UB3R Rev201 -> RSMod Rev233 Porting Matrix

## Purpose
Use `_references_archive/osrs-ub3r-monorepo` as a **pattern reference** for RSMod2 architecture, while keeping rev 233 symbols/opcodes/cache as the source of truth.

## Source Snapshot
- Reference repo: `_references_archive/osrs-ub3r-monorepo`
- Declared target there: rev 201
  - `_references_archive/osrs-ub3r-monorepo/README.md`
  - `_references_archive/osrs-ub3r-monorepo/docs/hosting-environment/game_world_setup.md`

## Porting Rules
1. Port architecture and flow, not literal opcodes/IDs.
2. Rebind all packet structures/opcodes for rev 233.
3. Re-verify all obj/npc/loc refs against `rsmod/.data/symbols/*.sym`.
4. Treat rev 201 content behavior as heuristic only.

## Safe To Port (High Confidence)
- Login/account pipeline patterns
  - `_references_archive/osrs-ub3r-monorepo/plugins/api/src/main/kotlin/org/rsmod/plugins/api/protocol/codec/login/LoginDecoder.kt`
  - `_references_archive/osrs-ub3r-monorepo/plugins/api/src/main/kotlin/org/rsmod/plugins/api/protocol/codec/account/AccountDispatcher.kt`
  - Why: control-flow and lifecycle are revision-agnostic at design level.
- Packet structure map architecture (device-scoped maps, handler dispatch)
  - `_references_archive/osrs-ub3r-monorepo/plugins/api/src/main/kotlin/org/rsmod/plugins/api/protocol/structure/PacketStructureCodec.kt`
  - Why: good for separation of structure vs handler logic.
- Action bus publish pattern for interactions
  - `_references_archive/osrs-ub3r-monorepo/plugins/api/src/main/kotlin/org/rsmod/plugins/api/protocol/packet/client/NpcPacket.kt`
  - `_references_archive/osrs-ub3r-monorepo/plugins/api/src/main/kotlin/org/rsmod/plugins/api/protocol/packet/client/InterfaceButtonPacket.kt`
  - Why: clean action emission and handler isolation.

## Port With Adaptation (Medium Confidence)
- Client packet registration coverage
  - `_references_archive/osrs-ub3r-monorepo/plugins/api/src/main/kotlin/org/rsmod/plugins/api/protocol/structure/client/desktop.plugin.kts`
  - Keep: broad registration strategy.
  - Adapt: every opcode/length mapping for rev 233.
- Server packet write structure
  - `_references_archive/osrs-ub3r-monorepo/plugins/api/src/main/kotlin/org/rsmod/plugins/api/protocol/structure/server/desktop.plugin.kts`
  - Keep: encoder organization.
  - Adapt: packet opcodes/field order/length for rev 233.
- Dev command ergonomics (tele, pos, item, npc)
  - `_references_archive/osrs-ub3r-monorepo/plugins/dev/src/main/kotlin/org/rsmod/plugins/dev/cmd/admin.plugin.kts`
  - Keep: QA workflow ideas.
  - Adapt: APIs and safety checks to current project conventions.

## Do Not Port Literally (Low Confidence)
- Revision-specific values:
  - Any hardcoded opcodes in client/server structure files.
  - Any hardcoded obj/npc/loc IDs.
  - Any xtea/cache assumptions tied to rev 201 deployment docs.
- "Complete gameplay" assumptions:
  - UB3R content modules are mostly framework scaffolding, not full F2P quest/skill implementations.
  - Do not mark feature parity from this reference alone.

## What UB3R Helps Us With Right Now
- Packet coverage strategy (reduce missing inbound packet gaps).
- Reconnect/auth flow shape for hardening login pipeline.
- Cleaner interaction decoding -> action bus publishing path.
- Better developer/QA command set patterns.

## What UB3R Does Not Solve For Us
- Rev 233 opcode correctness.
- Rev 233 symbol ID correctness.
- F2P content completeness (quests, skilling loops, edge-case mechanics).
- Live OSRS behavior parity by itself.

## Immediate Integration Plan (Rev 233)
1. Build a rev233 packet mapping table from our current net layer and missing handlers.
2. Port UB3R registration/dispatch pattern only where it improves maintainability.
3. Implement missing packet handlers and reconnect/auth TODOs in RSMod net layer.
4. Validate with AgentBridge bot scenarios (doors/buildings/dialogue/ui/quest start states).

## Validation Checklist Before Merge
- All new/changed packet structures verified against rev 233 references.
- No raw wiki IDs in Kotlin constants without `.sym` confirmation.
- Reconnect/login flow tested for both fresh login and reconnect path.
- Interaction handlers support required op ranges for current client behavior.
- Module-scoped build passes.


