package org.rsmod.content.areas.misc.shantaypass

import jakarta.inject.Inject
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onOpNpc1
import org.rsmod.content.areas.misc.shantaypass.configs.ShantayPassNpcs
import org.rsmod.game.entity.Npc
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Shantay Pass - F2P desert border content.
 *
 * Features:
 * - Shantay disclaimer for new players entering the desert
 * - Border guards preventing F2P access to members desert
 * - Shantay guards for the pass area
 */
class ShantayPassScript @Inject constructor() : PluginScript() {

    override fun ScriptContext.startup() {
        onOpNpc1(ShantayPassNpcs.shantay) { shantayDialogue(it.npc) }
        onOpNpc1(ShantayPassNpcs.shantay_guard) { shantayGuardDialogue(it.npc) }
        onOpNpc1(ShantayPassNpcs.borderguard1) { borderGuardDialogue(it.npc) }
        onOpNpc1(ShantayPassNpcs.borderguard2) { borderGuardDialogue(it.npc) }
    }

    private suspend fun ProtectedAccess.shantayDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(
                neutral,
                "Hello friend! Welcome to the Shantay Pass. Be careful in the desert - " +
                    "it's a dangerous place!",
            )
            val choice =
                choice3(
                    "What do you do here?",
                    1,
                    "I'd like to buy some supplies.",
                    2,
                    "Goodbye.",
                    3,
                )
            when (choice) {
                1 -> {
                    chatPlayer(quiz, "What do you do here?")
                    chatNpc(
                        neutral,
                        "I guard this pass and help travelers. The desert beyond is members-only. " +
                            "If you're F2P, you can explore the border area but not cross into the desert.",
                    )
                }
                2 -> {
                    chatPlayer(neutral, "I'd like to buy some supplies.")
                    chatNpc(
                        sad,
                        "Sorry friend, my shop is currently closed for inventory. Come back later!",
                    )
                }
                3 -> {
                    chatPlayer(neutral, "Goodbye.")
                    chatNpc(happy, "Safe travels, friend!")
                }
            }
        }

    private suspend fun ProtectedAccess.shantayGuardDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(
                neutral,
                "Halt! This is the Shantay Pass. The desert beyond is dangerous and " +
                    "requires membership to access.",
            )
        }

    private suspend fun ProtectedAccess.borderGuardDialogue(npc: Npc) =
        startDialogue(npc) {
            chatNpc(
                neutral,
                "Stop! You cannot pass into the desert. This area is for members only. " +
                    "F2P players may explore the border area but cannot venture further south.",
            )
        }
}
