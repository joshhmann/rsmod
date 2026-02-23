package org.rsmod.content.areas.city.varrock.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpNpc3
import org.rsmod.api.shops.Shops
import org.rsmod.content.areas.city.varrock.configs.varrock_invs
import org.rsmod.content.areas.city.varrock.configs.varrock_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Lowe @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.lowe) { loweDialogue(it.npc) }
        onOpNpc3(varrock_npcs.lowe) { player.openArcheryShop(it.npc) }
    }

    private fun Player.openArcheryShop(npc: Npc) {
        shops.open(this, npc, "Lowe's Archery Emporium", varrock_invs.archery_shop)
    }

    private suspend fun ProtectedAccess.loweDialogue(npc: Npc) =
        startDialogue(npc) { loweChat(npc) }

    private suspend fun Dialogue.loweChat(npc: Npc) {
        chatNpc(happy, "Welcome to Lowe's Archery Emporium. Can I help you?")
        val choice = choice2("Yes please, what have you got?", 1, "No thanks.", 2)
        if (choice == 1) {
            player.openArcheryShop(npc)
        } else {
            chatPlayer(neutral, "No thanks.")
        }
    }
}
