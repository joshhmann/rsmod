package org.rsmod.content.skills.magic.teleports

import jakarta.inject.Inject
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.stat.magicLvl
import org.rsmod.api.player.stat.statAdvance
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class TeleportScript
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {

    override fun ScriptContext.startup() {
        /* Home Teleport are always available, no runes or level required. */
        onIfOverlayButton(TeleportComponents.spellbook_home_teleport) {
            player.castTeleport(TeleportSpell.HOME_TELEPORT, TeleportDestinations.lumbridge)
        }

        /* Standard spellbook teleports (F2P). */
        onIfOverlayButton(components.magic_spellbook_varrock_teleport) {
            player.castTeleport(TeleportSpell.VARROCK_TELEPORT, TeleportDestinations.varrock)
        }
        onIfOverlayButton(components.magic_spellbook_lumbridge_teleport) {
            player.castTeleport(TeleportSpell.LUMBRIDGE_TELEPORT, TeleportDestinations.lumbridge)
        }
        onIfOverlayButton(components.magic_spellbook_falador_teleport) {
            player.castTeleport(TeleportSpell.FALADOR_TELEPORT, TeleportDestinations.falador)
        }
    }

    private fun Player.castTeleport(spell: TeleportSpell, destination: CoordGrid) {
        protectedAccess.launch(this) {
            if (spell.level > 0 && player.magicLvl < spell.level) {
                mes("You need a Magic level of ${spell.level} to cast this spell.")
                return@launch
            }

            if (spell.xp > 0.0) {
                statAdvance(stats.magic, constant = 0, percent = spell.xp)
            }

            anim(seqs.human_castteleport)
            delay(spell.castTicks)
            telejump(destination)
            resetAnim()
        }
    }
}

private typealias Player = org.rsmod.game.entity.Player
private typealias components = org.rsmod.api.config.refs.components
