package org.rsmod.content.areas.city.varrock.museum.npcs

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.city.varrock.museum.configs.museum_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class MuseumGuard @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc1(museum_npcs.museum_guard) { guardDialogue(it.npc) }
        onOpNpc1(museum_npcs.museum_guard2) { guardDialogue(it.npc) }
        onOpNpc1(museum_npcs.museum_guard3) { guardDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.guardDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(
                neutral,
                "Welcome to the Varrock Museum. Please enjoy the exhibits, but don't touch anything!",
            )
        }
}
