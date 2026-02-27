package org.rsmod.api.player.stat

import kotlin.math.min
import org.rsmod.annotations.InternalApi
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.synths
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.output.soundSynth
import org.rsmod.api.player.ui.PlayerInterfaceUpdates
import org.rsmod.api.utils.skills.CombatLevel
import org.rsmod.game.entity.Player
import org.rsmod.game.stat.PlayerSkillXPTable
import org.rsmod.game.stat.PlayerStatMap
import org.rsmod.game.type.stat.StatType

public object PlayerSkillXP {
    public fun internalAddXP(player: Player, stat: StatType, xp: Double, rate: Double): Int =
        player.addXP(stat, xp, rate)

    private fun Player.addXP(stat: StatType, xp: Double, rate: Double): Int {
        val fineXp = PlayerStatMap.toFineXP(xp * rate)
        if (fineXp.isInfinite()) {
            throw IllegalArgumentException("Total XP being added is too high! (xp=$xp, rate=$rate)")
        }
        val addedFineXp = statMap.addXP(stat, fineXp)
        if (addedFineXp == 0) {
            // UpdateStat packet is sent even if stat is maxed.
            updateStat(stat)
            return 0
        }
        checkLevelUp(stat)
        updateStat(stat)
        return PlayerStatMap.normalizeFineXP(addedFineXp)
    }

    private fun PlayerStatMap.addXP(stat: StatType, fineXp: Double): Int {
        val currXp = getFineXP(stat)
        val sumXp = min(PlayerStatMap.MAX_FINE_XP.toDouble(), currXp + fineXp).toInt()
        val addedXp = sumXp - currXp
        if (addedXp > 0) {
            setFineXP(stat, sumXp)
        }
        return addedXp
    }

    @OptIn(InternalApi::class)
    private fun Player.checkLevelUp(stat: StatType) {
        val baseLevel = statBase(stat)
        if (baseLevel >= stat.maxLevel) {
            return
        }
        val nextLevelXp = PlayerSkillXPTable.getXPFromLevel(baseLevel + 1)
        val currentXp = statMap.getXP(stat)
        if (currentXp >= nextLevelXp) {
            val newLevel = min(stat.maxLevel, PlayerSkillXPTable.getLevelFromXP(currentXp))
            val oldLevel = baseLevel
            statMap.setBaseLevel(stat, newLevel.toByte())

            val setCurrLevel = stat(stat) == baseLevel
            if (setCurrLevel) {
                statMap.setCurrentLevel(stat, newLevel.toByte())
            }

            // Level up effects: message, sound, and milestone announcements
            if (newLevel > oldLevel) {
                handleLevelUp(stat, newLevel)
            }

            engineQueueChangeStat(stat)
            engineQueueAdvanceStat(stat)
        }

        val combatLevel = calculateCombatLevel(this)
        if (combatLevel != this.combatLevel) {
            appearance.combatLevel = combatLevel
            // TODO: Should this update the entire combat tab or just the combat level vars?
            PlayerInterfaceUpdates.updateCombatLevel(this)
        }
    }

    /** Handles level-up celebration: message, sound, and milestone announcements. */
    private fun Player.handleLevelUp(stat: StatType, newLevel: Int) {
        val skillName = getSkillName(stat)

        // Congratulatory message in green color (<col=00FF00>)
        mes(
            "<col=00FF00>Congratulations, you've just advanced ${article(skillName)} ${skillName} level! You have reached level ${newLevel}.",
            type = org.rsmod.api.player.output.ChatType.GameMessage,
        )

        // Play level-up sound
        soundSynth(synths.magic_dart_hit)

        // Global announcement for level 99
        if (newLevel == 99) {
            broadcastGlobal(
                "<col=FF0000>News: ${displayName} has just achieved ${article(skillName)} level 99 in ${skillName}!"
            )
        }
    }

    /** Returns the skill name for the given stat type. */
    private fun getSkillName(stat: StatType): String =
        when (stat) {
            stats.attack -> "Attack"
            stats.defence -> "Defence"
            stats.strength -> "Strength"
            stats.hitpoints -> "Hitpoints"
            stats.ranged -> "Ranged"
            stats.prayer -> "Prayer"
            stats.magic -> "Magic"
            stats.cooking -> "Cooking"
            stats.woodcutting -> "Woodcutting"
            stats.fletching -> "Fletching"
            stats.fishing -> "Fishing"
            stats.firemaking -> "Firemaking"
            stats.crafting -> "Crafting"
            stats.smithing -> "Smithing"
            stats.mining -> "Mining"
            stats.herblore -> "Herblore"
            stats.agility -> "Agility"
            stats.thieving -> "Thieving"
            stats.slayer -> "Slayer"
            stats.farming -> "Farming"
            stats.runecrafting -> "Runecrafting"
            stats.hunter -> "Hunter"
            stats.construction -> "Construction"
            else -> "Unknown"
        }

    /** Returns the appropriate article (a/an) for the skill name. */
    private fun article(skillName: String): String =
        if (skillName.lowercase().first() in listOf('a', 'e', 'i', 'o', 'u')) "an" else "a"

    /** Broadcasts a global message to all players. */
    private fun Player.broadcastGlobal(message: String) {
        // TODO: Implement global broadcast when world broadcast API is available
        // For now, just send to self as well
        mes(message, type = org.rsmod.api.player.output.ChatType.Broadcast)
    }

    public fun calculateCombatLevel(player: Player): Int =
        CombatLevel.calculate(
            attack = player.baseAttackLvl,
            strength = player.baseStrengthLvl,
            defence = player.baseDefenceLvl,
            hitpoints = player.baseHitpointsLvl,
            ranged = player.baseRangedLvl,
            magic = player.baseMagicLvl,
            prayer = player.basePrayerLvl,
        )
}
