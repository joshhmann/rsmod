package org.rsmod.content.areas.city.karamja.npcs
import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.karamja.configs.karamja_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext
class LuthasScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(karamja_npcs.luthas) { startDialogue(it.npc) }
    }
    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        startDialogue(npc) { luthasDialogue() }
    }
    private suspend fun Dialogue.luthasDialogue() {
        chatNpc(happy, "Hello there, traveller! Welcome to my banana plantation.")
        val choice = choice3("Tell me about your banana plantation.", 1, "Can I help pick bananas?", 2, "Goodbye.", 3)
        when (choice) {
            1 -> {
                chatPlayer(quiz, "Tell me about your banana plantation.")
                chatNpc(happy, "These are the finest bananas in all of Gielinor!")
                chatNpc(neutral, "I ship them all over the world. They're particularly popular in Port Sarim.")
                chatNpc(sad, "Unfortunately, I've been having trouble getting enough ripe bananas picked lately.")
                chatNpc(neutral, "There's a crate near the dock. Fill it with 10 bananas and I'll pay you 30 gold!")
            }
            2 -> {
                chatPlayer(quiz, "Can I help pick bananas?")
                chatNpc(happy, "Certainly! Pick 10 bananas from the trees and put them in the crate near the dock.")
                chatNpc(happy, "I'll pay you 30 gold pieces when you're done!")
            }
            3 -> { chatPlayer(neutral, "Goodbye."); chatNpc(happy, "Take care!") }
        }
    }
}
