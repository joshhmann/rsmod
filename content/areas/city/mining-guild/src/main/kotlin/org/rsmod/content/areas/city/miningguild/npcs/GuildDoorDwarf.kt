package org.rsmod.content.areas.city.miningguild.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.baseMiningLvl
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.miningguild.configs.mining_guild_npcs
import org.rsmod.content.generic.guilds.GuildDefinitions
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Mining Guild door guard dialogue and entry requirement check.
 *
 * The dwarf at the Mining Guild entrance stops players who don't have 60 Mining and allows entry to
 * those who meet the requirement.
 */
class GuildDoorDwarf @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(mining_guild_npcs.door_guard) { doorGuardDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.doorGuardDialogue(npc: Npc) =
        startDialogue(npc) {
            // Check if player has 60 Mining
            if (player.baseMiningLvl >= GuildDefinitions.MINING_GUILD_LEVEL) {
                // Player meets requirement - allow entry dialogue
                doorGuardAllowEntry()
            } else {
                // Player doesn't meet requirement
                doorGuardDenyEntry()
            }
        }

    private suspend fun Dialogue.doorGuardAllowEntry() {
        chatNpc(happy, "Welcome to the Mining Guild, ${player.displayName}.")
        chatNpc(happy, "You have proven yourself a skilled miner.")
        player.mes("The dwarf lets you pass.")
    }

    private suspend fun Dialogue.doorGuardDenyEntry() {
        chatNpc(neutral, "Sorry, but you need a Mining level of 60 to enter the Mining Guild.")
        chatNpc(neutral, "Come back when you've trained your Mining skill more.")
        player.mes("You need a Mining level of 60 to enter the Mining Guild.")
    }
}
