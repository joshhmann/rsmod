package org.rsmod.content.quests.dragonslayer

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.quests.dragonslayer.configs.dragon_slayer_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Dragon Slayer I quest implementation for RSMod v2.
 *
 * Defeat the dragon Elvarg on the island of Crandor. Requires obtaining map pieces, ship access,
 * and anti-dragon shield.
 *
 * Reward: Quest Point + ability to wear rune platebody
 */
class DragonSlayer : PluginScript() {
    override fun ScriptContext.startup() {
        // Guildmaster in Champions' Guild (quest start)
        onOpNpc1(dragon_slayer_npcs.guildmaster) { startGuildmasterDialogue(it.npc) }

        // Oziach in Edgeville (gives quest after starting)
        onOpNpc1(dragon_slayer_npcs.oziach) { startOziachDialogue(it.npc) }

        // Elvarg on Crandor (boss)
        onOpNpc1(dragon_slayer_npcs.elvarg) { startElvargCombat(it.npc) }
    }

    private suspend fun ProtectedAccess.startGuildmasterDialogue(npc: Npc) {
        // TODO: Implement Guildmaster dialogue
        // Start quest, explain dragon problem
    }

    private suspend fun ProtectedAccess.startOziachDialogue(npc: Npc) {
        // TODO: Implement Oziach dialogue
        // Map pieces, ship access, anti-dragon shield
    }

    private suspend fun ProtectedAccess.startElvargCombat(npc: Npc) {
        // TODO: Implement Elvarg boss combat
        // HIGH COMPLEXITY:
        // - Dragonfire breath attack
        // - Requires anti-dragon shield
        // - Map pieces/ship access required to reach
    }
}
