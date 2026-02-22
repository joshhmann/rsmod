package org.rsmod.content.other.agentbridge

/**
 * Per-tick state snapshot broadcast to LLM testing agents over WebSocket (port 43595). Schema
 * matches the osrs-llm-testing-methodology.docx specification.
 */
data class StateSnapshot(
    val tick: Int,
    val player: PlayerSnapshot,
    val dialog: DialogSnapshot,
    val gameMessages: List<GameMessageSnapshot>,
)

data class PlayerSnapshot(
    val name: String,
    val position: PositionSnapshot,
    val skills: Map<String, SkillSnapshot>,
    val inventory: List<InvItemSnapshot>,
    val equipment: List<InvItemSnapshot>,
    /** Current animation sequence ID (0 = idle). */
    val animation: Int,
    /** NPCs within ~16 tiles of the player. Use `index` for interact_npc commands. */
    val nearbyNpcs: List<NearbyNpcSnapshot>,
    /**
     * Game objects (locs) within ~16 tiles of the player. Use `id + x + z` for interact_loc
     * commands.
     */
    val nearbyLocs: List<NearbyLocSnapshot>,
    /** True when a dialogue/chat modal is currently open. */
    val dialog: DialogSnapshot,
    /** Recent game messages sent to the player (oldest -> newest). */
    val gameMessages: List<GameMessageSnapshot>,
)

data class PositionSnapshot(val x: Int, val z: Int, val plane: Int)

data class SkillSnapshot(
    /** Base level derived from XP — the "real" level, not boosted/drained. */
    val level: Int,
    /** Total XP (fine XP / 10). */
    val xp: Int,
)

data class InvItemSnapshot(val slot: Int, val id: Int, val qty: Int)

data class NearbyNpcSnapshot(
    /** NPC type ID. */
    val id: Int,
    /** Server list index — pass to `interact_npc.index`. */
    val index: Int,
    val name: String,
    val x: Int,
    val z: Int,
    /** Current animation sequence ID (0 = idle). */
    val animation: Int,
)

data class NearbyLocSnapshot(
    /** Loc type ID — pass to `interact_loc.id`. */
    val id: Int,
    val name: String,
    val x: Int,
    val z: Int,
)

data class DialogSnapshot(
    val isOpen: Boolean,
    /** Interface ids currently occupying modal slots. */
    val modalInterfaceIds: List<Int>,
    /**
     * Latest interface text updates observed for active modal interfaces.
     *
     * These are best-effort captures from outgoing IfSetText packets and are primarily meant for
     * quest/mechanics test assertions.
     */
    val lines: List<String>,
)

data class GameMessageSnapshot(val type: Int, val text: String)
