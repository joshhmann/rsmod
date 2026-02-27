package org.rsmod.content.areas.city.portsarim.npcs

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.portsarim.configs.portsarim_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Hengel : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(portsarim_npcs.rimmington_hengel) { hengelDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.hengelDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(neutral, "Hello there, traveler!")
            chatPlayer(neutral, "Hello. What is this place?")
            chatNpc(
                neutral,
                "This is Rimmington. It's a small village, but we " +
                    "have a chemist shop and a nice lady named " +
                    "Milli who sells items.",
            )
            chatPlayer(neutral, "Thanks. It's nice here.")
            chatNpc(happy, "Aye, it's a peaceful spot. Enjoy your visit!")
        }
}
