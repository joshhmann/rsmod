package org.rsmod.content.generic.generic_npcs.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.generic.generic_npcs.configs.GenericNpcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class ZombieDialogue
@Inject
constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(GenericNpcs.zombie) {
            player.zombieDialogue(it.npc)
        }
    }

    private suspend fun ProtectedAccess.zombieDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Hello! I am Zombie.")
            
            // TODO: Add dialogue options
            val choice = choice2(
                "Tell me about yourself.", 1,
                "Goodbye.", 2
            )
            
            when (choice) {
                1 -> {
                    chatPlayer(quiz, "Tell me about yourself.")
                    chatNpc(happy, "TODO: Add NPC backstory here.")
                }
                2 -> {
                    chatPlayer(neutral, "Goodbye.")
                    chatNpc(happy, "Farewell!")
                }
            }
        }
}
