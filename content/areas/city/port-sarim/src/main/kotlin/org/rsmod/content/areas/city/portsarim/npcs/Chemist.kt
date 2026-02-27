package org.rsmod.content.areas.city.portsarim.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.portsarim.configs.portsarim_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Chemist @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(portsarim_npcs.chemist) { chemistDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.chemistDialogue(npc: Npc) =
        startDialogue(npc) { chemist(npc) }

    private suspend fun Dialogue.chemist(npc: Npc) {
        chatNpc(happy, "Welcome! Do you need any herblore supplies?")
        val choice =
            choice3("Yes please. What do you have?", 1, "What is herblore?", 2, "No thanks.", 3)
        when (choice) {
            1 -> {
                chatPlayer(neutral, "Yes please. What do you have?")
                chatNpc(happy, "I have all sorts of herblore supplies! We sell herbs,")
                chatNpc(happy, "vials, pestles and mortars - everything you need to")
                chatNpc(happy, "make potions. Take a look!")
            }
            2 -> {
                chatPlayer(neutral, "What is herblore?")
                chatNpc(happy, "Herblore is the skill of making potions! You can")
                chatNpc(happy, "combine herbs with other ingredients to create")
                chatNpc(happy, "powerful potions. It's very useful for adventurers.")
            }
            3 -> {
                chatPlayer(neutral, "No thanks.")
                chatNpc(happy, "Come back if you change your mind!")
            }
        }
    }
}
