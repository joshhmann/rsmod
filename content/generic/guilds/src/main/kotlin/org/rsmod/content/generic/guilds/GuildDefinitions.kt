package org.rsmod.content.generic.guilds

import org.rsmod.api.player.output.mes
import org.rsmod.api.player.stat.baseCookingLvl
import org.rsmod.api.player.stat.baseCraftingLvl
import org.rsmod.api.player.stat.baseMiningLvl
import org.rsmod.game.entity.Player

/**
 * Predefined guild entry requirements for OSRS guilds.
 *
 * This object provides convenient helper functions for checking entry requirements for all standard
 * guilds in the game.
 */
public object GuildDefinitions {

    /* ==================== Skill Level Constants ==================== */

    /** Cooking Guild requirement: 32 Cooking */
    public const val COOKING_GUILD_LEVEL: Int = 32

    /** Crafting Guild requirement: 40 Crafting */
    public const val CRAFTING_GUILD_LEVEL: Int = 40

    /** Mining Guild requirement: 60 Mining */
    public const val MINING_GUILD_LEVEL: Int = 60

    /* ==================== Cooking Guild ==================== */

    /** Checks if player meets Cooking level requirement for Cooking Guild. */
    public fun checkCookingLevel(player: Player): Boolean {
        return if (player.baseCookingLvl >= COOKING_GUILD_LEVEL) {
            true
        } else {
            player.mes(
                "You need a Cooking level of $COOKING_GUILD_LEVEL to enter the Cooking Guild."
            )
            false
        }
    }

    /* ==================== Crafting Guild ==================== */

    /** Checks if player meets Crafting level requirement for Crafting Guild. */
    public fun checkCraftingLevel(player: Player): Boolean {
        return if (player.baseCraftingLvl >= CRAFTING_GUILD_LEVEL) {
            true
        } else {
            player.mes(
                "You need a Crafting level of $CRAFTING_GUILD_LEVEL to enter the Crafting Guild."
            )
            false
        }
    }

    /* ==================== Mining Guild ==================== */

    /** Checks if player meets Mining level requirement for Mining Guild. */
    public fun checkMiningLevel(player: Player): Boolean {
        return if (player.baseMiningLvl >= MINING_GUILD_LEVEL) {
            true
        } else {
            player.mes("You need a Mining level of $MINING_GUILD_LEVEL to enter the Mining Guild.")
            false
        }
    }

    /* ==================== Champions Guild ==================== */

    /** Checks if player meets Quest Point requirement for Champions' Guild. */
    public fun checkChampionsGuildQuestPoints(player: Player, questPoints: Int): Boolean {
        return if (questPoints >= 32) {
            true
        } else {
            player.mes("You need 32 Quest Points to enter the Champions' Guild.")
            false
        }
    }
}
