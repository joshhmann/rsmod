package org.rsmod.content.areas.city.lumbridge.npcs

import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.lumbridge.configs.lumbridge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Cook : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(lumbridge_npcs.cook) { startDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.startDialogue(npc: Npc) =
        startDialogue(npc) {
            chatPlayer(neutral, "What are you doing?")
            chatNpc(
                sad,
                "Oh, I am in a terrible, terrible mess! It is the Duke's birthday today and " +
                    "I need to bake a cake, but I have no flour, no eggs, and no milk! " +
                    "Whatever shall I do?",
            )
            mainMenu()
        }

    private suspend fun Dialogue.mainMenu() {
        val choice = choice3(
            "Perhaps I could help?",
            1,
            "Can you teach me about cooking?",
            2,
            "Goodbye.",
            3,
        )
        when (choice) {
            1 -> offerHelp()
            2 -> cookingAdvice()
            3 -> goodbye()
        }
    }

    private suspend fun Dialogue.offerHelp() {
        chatPlayer(happy, "Perhaps I could help?")
        chatNpc(happy, "Oh, would you really? That would be wonderful!")
        chatNpc(
            neutral,
            "I need a pot of flour, a bucket of milk, and an egg. " +
                "You can find eggs in the farm north-east of Lumbridge " +
                "Castle. Milk the dairy cows there for milk, and pick " +
                "some wheat from the fields to make flour with " +
                "the windmill north of the castle.",
        )
        chatNpc(sad, "Do be careful though - the farmers can be protective of their livestock!")
    }

    private suspend fun Dialogue.cookingAdvice() {
        chatPlayer(quiz, "Can you teach me about cooking?")
        chatNpc(
            neutral,
            "Cooking is simple! Find some food, cook it on a range or fire. " +
                "Be careful not to burn it though - the more you practice, " +
                "the better you will get.",
        )
    }

    private suspend fun Dialogue.goodbye() {
        chatPlayer(neutral, "Goodbye.")
        chatNpc(happy, "Goodbye, and thank you for listening to my troubles!")
    }
}
