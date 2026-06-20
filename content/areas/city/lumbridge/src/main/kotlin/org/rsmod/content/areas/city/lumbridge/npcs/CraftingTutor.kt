package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class CraftingTutor : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.crafting_tutor) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc): Unit =
        startDialogue(npc) {
            chatPlayer(happy, "Hello there.")
            chatNpc(happy, "Greetings, ${player.displayName}. I am the Crafting Tutor. I can teach you the art of making things.")
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Tell me about crafting.",
            1,
            "How do I start crafting?",
            2,
            "Thanks, that is enough.",
            3,
        )
        when (choice) {
            1 -> skillExplanation()
            2 -> skillAdvice()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.skillExplanation() {
        chatPlayer(quiz, "Tell me about crafting.")
        chatNpc(
            neutral,
            "Crafting is the art of creating items from raw materials. " +
                "You can make leather armour, pottery, jewellery, and more!",
        )
        chatNpc(
            happy,
            "Start by gathering materials \u2014 animal hides can be tanned " +
                "into leather, and gems can be cut and set into jewellery.",
        )
        chatNpc(neutral, "Is there anything else you would like to know?")
        val choice = choice2("How do I start crafting?", 1, "Thanks, that is enough.", 2)
        if (choice == 1) skillAdvice() else goodbye()
    }

    private suspend fun Dialogue.skillAdvice() {
        chatPlayer(quiz, "How do I start crafting?")
        chatNpc(
            neutral,
            "You can spin wool into string using a spinning wheel, or craft " +
                "leather gloves and boots from cowhides.",
        )
        chatNpc(
            happy,
            "Crafting is a rewarding skill that lets you make your own " +
                "equipment and accessories. Give it a try!",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Thanks, that is enough.")
        chatNpc(happy, "Feel free to come back if you need more advice!")
    }
}
