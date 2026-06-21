package org.rsmod.content.areas.city.falador.npcs

import jakarta.inject.Inject
import org.rsmod.api.invtx.invAdd
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.content.areas.city.falador.configs.falador_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

object FaladorCookObjs : ObjReferences() {
    val redberry_pie = find("redberry_pie")
}

class FaladorCook @Inject constructor(private val objRepo: ObjRepository) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(falador_npcs.cook) { cookDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.cookDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(happy, "Hello there! I'm the castle cook.")
            chatNpc(happy, "I'm preparing the Duke's meals for today.")
            val choice = choice2(
                "Can I have a redberry pie?",
                1,
                "That sounds interesting. Goodbye!",
                2,
            )
            when (choice) {
                1 -> {
                    chatPlayer(happy, "Can I have a redberry pie?")
                    chatNpc(happy, "A redberry pie? I just baked a fresh batch!")
                    chatNpc(happy, "Here you go, enjoy!")
                    val added = player.invAdd(player.inv, FaladorCookObjs.redberry_pie, 1).success
                    if (added) {
                        chatNpc(happy, "There you go! One fresh redberry pie!")
                        chatPlayer(happy, "Thank you!")
                    } else {
                        chatNpc(sad, "Oh dear, it seems you don't have room in your inventory.")
                        chatNpc(neutral, "Come back when you have space!")
                    }
                }
                2 -> chatPlayer(neutral, "That sounds interesting. Goodbye!")
            }
        }
}
