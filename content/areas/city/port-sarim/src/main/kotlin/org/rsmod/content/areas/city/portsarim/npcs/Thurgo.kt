package org.rsmod.content.areas.city.portsarim.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.portsarim.configs.PortSarimNpcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class ThurgoScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(PortSarimNpcs.thurgo) { thurgoDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.thurgoDialogue(npc: Npc) =
        startDialogue(npc) {
            chatPlayer(quiz, "Hello there.")
            chatNpc(angry, "Go away! I'm busy.")
            val choice =
                choice3("Why are you so angry?", 1, "I need a sword made.", 2, "Goodbye.", 3)
            when (choice) {
                1 -> {
                    chatPlayer(quiz, "Why are you so angry?")
                    chatNpc(angry, "I'm hungry! I want my redberry pie!")
                    chatNpc(sad, "Nobody brings me pie anymore...")
                    chatPlayer(quiz, "Redberry pie?")
                    chatNpc(happy, "Yes! Redberry pie is the food of champions!")
                    chatNpc(neutral, "Bring me a redberry pie and I might help you.")
                }
                2 -> {
                    chatPlayer(quiz, "I need a sword made.")
                    chatNpc(angry, "Do I look like a blacksmith to you?")
                    chatNpc(neutral, "Wait... I am a blacksmith. An Imcando blacksmith!")
                    chatNpc(sad, "But I'm too hungry to work. My stomach is empty...")
                    chatNpc(happy, "Bring me a redberry pie and I'll make you the finest sword!")
                    // TODO: Check if player has redberry pie
                    // TODO: Start sword-making process for The Knight's Sword quest
                }
                3 -> chatPlayer(neutral, "Goodbye.")
            }
        }
}
