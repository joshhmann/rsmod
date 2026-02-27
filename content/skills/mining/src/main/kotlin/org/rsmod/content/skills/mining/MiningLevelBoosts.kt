package org.rsmod.content.skills.mining

import org.rsmod.api.config.refs.stats
import org.rsmod.api.stats.levelmod.InvisibleLevelMod
import org.rsmod.game.entity.Player
import org.rsmod.map.square.MapSquareKey

/**
 * Provides invisible Mining level boosts.
 * - Mining Guild: +7 boost when inside the guild (members' area)
 * - Other boosts can be added here (e.g., mining gloves effects)
 */
class MiningLevelBoosts : InvisibleLevelMod(stats.mining) {
    override fun Player.calculateBoost(): Int {
        var boost = 0

        // Mining Guild invisible +7 boost (members only area)
        if (isInMiningGuild()) {
            boost += MINING_GUILD_BOOST
        }

        return boost
    }

    /**
     * Checks if the player is inside the Mining Guild.
     *
     * The Mining Guild is located in:
     * - Region 47, 52 (MapSquareKey) - above ground entrance area
     * - Coordinates roughly: x=3008-3071, z=3328-3391 (plane 0)
     *
     * The actual guild area (where the +7 boost applies) is the underground members' area with coal
     * and mithril rocks.
     */
    private fun Player.isInMiningGuild(): Boolean {
        val mapSquare = MapSquareKey.from(coords)
        // Mining Guild is in region 47, 52
        return mapSquare == MINING_GUILD_REGION
    }

    companion object {
        /** The invisible boost amount provided in the Mining Guild. */
        const val MINING_GUILD_BOOST: Int = 7

        /** MapSquareKey for the Mining Guild region (47, 52). */
        val MINING_GUILD_REGION: MapSquareKey = MapSquareKey(47, 52)
    }
}
