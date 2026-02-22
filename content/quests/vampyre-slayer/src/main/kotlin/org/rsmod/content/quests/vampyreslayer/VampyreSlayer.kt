package org.rsmod.content.quests.vampyreslayer

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.vampyreslayer.configs.vampyre_slayer_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Vampyre Slayer quest implementation for RSMod v2.
 *
 * Slay Count Draynor the vampyre using garlic to weaken him and a stake to finish him off.
 *
 * Reward: 4,825 Attack XP + 1 Quest Point
 */
class VampyreSlayer : PluginScript() {
    override fun ScriptContext.startup() {
        // Morgan in Draynor Village (quest start)
        onOpNpc1(vampyre_slayer_npcs.morgan) { startMorganDialogue(it.npc) }

        // Dr Harlow in Blue Moon Inn (gives stake)
        onOpNpc1(vampyre_slayer_npcs.dr_harlow) { startHarlowDialogue(it.npc) }

        // Count Draynor in Draynor Manor (boss)
        onOpNpc1(vampyre_slayer_npcs.count_draynor) { startDraynorDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startMorganDialogue(npc: Npc) {
        // TODO: Implement Morgan dialogue
        // Start quest, explain vampyre problem
    }

    private suspend fun ProtectedAccess.startHarlowDialogue(npc: Npc) {
        // TODO: Implement Dr Harlow dialogue
        // Give stake, explain garlic weakness
    }

    private suspend fun ProtectedAccess.startDraynorDialogue(npc: Npc) {
        // TODO: Implement Count Draynor boss combat
        // Needs custom combat script:
        // - Garlic in inventory weakens him
        // - Must use stake to kill
        // - 4,825 Attack XP reward on defeat
    }
}
