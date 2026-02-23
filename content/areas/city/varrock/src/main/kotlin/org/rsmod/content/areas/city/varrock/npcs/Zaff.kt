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

class Zaff @Inject constructor(private val shops: Shops) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(varrock_npcs.zaff) { zaffDialogue(it.npc) }
        onOpNpc3(varrock_npcs.zaff) { player.openStaffShop(it.npc) }
    }

    private fun Player.openStaffShop(npc: Npc) {
        shops.open(this, npc, "Zaff's Superior Staffs!", varrock_invs.staff_shop)
    }

    private suspend fun ProtectedAccess.zaffDialogue(npc: Npc) =
        startDialogue(npc) { zaffChat(npc) }

    private suspend fun Dialogue.zaffChat(npc: Npc) {
        chatNpc(happy, "Welcome to Zaff's Superior Staffs! Can I help you?")
        val choice = choice2("Yes please, what have you got?", 1, "No thanks.", 2)
        if (choice == 1) {
            player.openStaffShop(npc)
        } else {
            chatPlayer(neutral, "No thanks.")
        }
    }
}
