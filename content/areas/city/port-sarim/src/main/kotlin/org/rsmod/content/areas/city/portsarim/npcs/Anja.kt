package org.rsmod.content.areas.city.portsarim.npcs

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.portsarim.configs.portsarim_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Anja : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(portsarim_npcs.rimmington_anja) { anjaDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.anjaDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(neutral, "Good day to you!")
            chatPlayer(neutral, "Hi there. What do you do here?")
            chatNpc(
                neutral,
                "I live here in Rimmington. It's a quiet village, " +
                    "though the chemist can be a bit odd sometimes.",
            )
            chatPlayer(quiz, "The chemist?")
            chatNpc(
                neutral,
                "Yes, he lives in that house near here. He's always " +
                    "working on strange potions. But he's harmless enough.",
            )
            chatPlayer(neutral, "I'll keep that in mind. Thanks!")
        }
}
