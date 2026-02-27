package org.rsmod.content.areas.city.miningguild.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.baseMiningLvl
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.miningguild.configs.mining_guild_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Guildmaster Gadrin - Master of the Mining Guild.
 *
 * Gadrin is the guildmaster who welcomes miners to the guild and sells the Mining cape to those who
 * have achieved 99 Mining.
 *
 * Note: Mining cape purchase requires the cape to exist in the cache. This is a placeholder
 * implementation for the guildmaster dialogue.
 */
class GuildmasterGadrin @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(mining_guild_npcs.guildmaster) { guildmasterDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.guildmasterDialogue(npc: Npc) =
        startDialogue(npc) {
            // Welcome dialogue based on player's mining level
            when {
                player.baseMiningLvl >= 99 -> guildmasterMaxedDialogue()
                player.baseMiningLvl >= 80 -> guildmasterExpertDialogue()
                player.baseMiningLvl >= 60 -> guildmasterWelcomeDialogue()
                else -> guildmasterConfusedDialogue()
            }
        }

    private suspend fun Dialogue.guildmasterMaxedDialogue() {
        // Maxed mining - offer cape
        chatNpc(happy, "Greetings, master miner! I see you've achieved level 99 Mining.")
        chatNpc(happy, "Would you like to buy a Mining cape for 99,000 gold coins?")
        player.mes("Gadrin offers to sell you a Mining cape.")
        // TODO: Implement Mining cape purchase
        // Requires Mining cape obj to exist in cache
    }

    private suspend fun Dialogue.guildmasterExpertDialogue() {
        // High level - impressed
        chatNpc(happy, "Welcome back, expert miner!")
        chatNpc(happy, "You're getting close to mastering the craft. Keep at it!")
    }

    private suspend fun Dialogue.guildmasterWelcomeDialogue() {
        // Just meets requirement
        chatNpc(happy, "Welcome to the Mining Guild!")
        chatNpc(happy, "Here you'll find the best coal and mithril rocks in the land.")
        chatNpc(happy, "Train hard and you may become a master miner.")
    }

    private suspend fun Dialogue.guildmasterConfusedDialogue() {
        // Below requirement (shouldn't happen inside guild)
        chatNpc(confused, "How did you get in here?")
        chatNpc(confused, "You don't have the required Mining level of 60!")
    }
}
