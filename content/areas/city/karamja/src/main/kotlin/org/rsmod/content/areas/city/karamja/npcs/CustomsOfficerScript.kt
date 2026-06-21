package org.rsmod.content.areas.city.karamja.npcs
import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.karamja.configs.karamja_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext
class CustomsOfficerScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(karamja_npcs.customs_officer) { startDialogue(it.npc) }
    }
    private suspend fun ProtectedAccess.startDialogue(npc: Npc) {
        startDialogue(npc) { officerDialogue() }
    }
    private suspend fun Dialogue.officerDialogue() {
        chatNpc(happy, "Hello there, traveller. Welcome to Karamja.")
        val choice = choice3("I'm just passing through.", 1, "What's the rules about taking items through customs?", 2, "Can I take my items back to the mainland?", 3)
        when (choice) {
            1 -> {
                chatPlayer(quiz, "I'm just passing through.")
                chatNpc(happy, "Enjoy your stay on Karamja!")
                chatNpc(sad, "Just remember - there are strict customs regulations on what you can take back.")
            }
            2 -> {
                chatPlayer(quiz, "What's the rules about taking items through customs?")
                chatNpc(neutral, "Any items you bring into Karamja can be taken back out.")
                chatNpc(sad, "But we do have restrictions on certain items leaving the island.")
                chatNpc(neutral, "Bananas are strictly regulated - they must be declared!")
                chatNpc(quiz, "You wouldn't be trying to smuggle anything, would you?")
                chatPlayer(neutral, "No, of course not!")
                chatNpc(happy, "Good! Enjoy your stay on Karamja!")
            }
            3 -> {
                chatPlayer(quiz, "Can I take my items back to the mainland?")
                chatNpc(neutral, "Of course. There's no restriction on taking personal belongings.")
                chatNpc(sad, "However, there are rumours of pirates in the area.")
                chatNpc(happy, "But don't worry, our captain knows the safe routes!")
            }
        }
    }
}
