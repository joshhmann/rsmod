package org.rsmod.api.quest

import org.rsmod.api.player.vars.VarPlayerIntMapSetter
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript

public abstract class QuestScript(public val quest: Quest) : PluginScript() {

    public fun getStage(player: Player): Int = player.vars[quest.varp]

    public fun setStage(player: Player, stage: Int) {
        VarPlayerIntMapSetter.set(player, quest.varp, stage.coerceIn(0, quest.maxStage))
    }

    public fun isComplete(player: Player): Boolean = getStage(player) >= quest.maxStage

    public abstract fun getJournal(player: Player): List<String>
}
