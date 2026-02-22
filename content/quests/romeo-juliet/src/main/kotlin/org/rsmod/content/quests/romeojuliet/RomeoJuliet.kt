package org.rsmod.content.quests.romeojuliet

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.romeojuliet.configs.romeo_juliet_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Romeo & Juliet quest implementation for RSMod v2.
 *
 * Help Romeo and Juliet be together by delivering messages, obtaining a Cadava potion, and
 * reuniting the lovers. Reward: 5 Quest Points.
 */
class RomeoJuliet : PluginScript() {
    override fun ScriptContext.startup() {
        // Romeo in Varrock Square
        onOpNpc1(romeo_juliet_npcs.romeo) { startRomeoDialogue(it.npc) }

        // Juliet in Capulet house
        onOpNpc1(romeo_juliet_npcs.juliet) { startJulietDialogue(it.npc) }

        // Father Lawrence in Varrock Church
        onOpNpc1(romeo_juliet_npcs.father_lawrence) { startFatherLawrenceDialogue(it.npc) }

        // Apothecary in Varrock
        onOpNpc1(romeo_juliet_npcs.apothecary) { startApothecaryDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startRomeoDialogue(npc: Npc) {
        // TODO: Implement Romeo dialogue stages
        // Stage 0: Start quest
        // Stage 1: Deliver message to Juliet
        // Stage 2-7: Quest progression
        // Stage 8: Complete
    }

    private suspend fun ProtectedAccess.startJulietDialogue(npc: Npc) {
        // TODO: Implement Juliet dialogue
    }

    private suspend fun ProtectedAccess.startFatherLawrenceDialogue(npc: Npc) {
        // TODO: Implement Father Lawrence dialogue
    }

    private suspend fun ProtectedAccess.startApothecaryDialogue(npc: Npc) {
        // TODO: Implement Apothecary dialogue
    }
}
