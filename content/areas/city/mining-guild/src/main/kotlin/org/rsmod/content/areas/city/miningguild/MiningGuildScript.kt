package org.rsmod.content.areas.city.miningguild

import jakarta.inject.Inject
import org.rsmod.content.generic.guilds.GuildDefinitions
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Mining Guild script - Guild for expert miners in Falador.
 *
 * The Mining Guild is located in south-east Falador and requires 60 Mining to enter. It contains
 * the largest concentration of coal rocks in the game, as well as mithril rocks for higher-level
 * miners.
 *
 * Features:
 * - Entrance guarded by dwarves who check Mining level (60 required)
 * - Guildmaster Gadrin (Mining cape seller for 99 Mining)
 * - Yarsul's Prodigious Pickaxes (pickaxe shop)
 * - Hendor's Awesome Ores (ore shop - zero stock)
 * - 37 Coal rocks, 5 Mithril rocks, 4 Iron rocks, 2 Adamantite rocks (F2P area)
 *
 * Note: The invisible +7 Mining boost in the members' area is handled by the MINING-GEMS task
 * (mining skill module).
 *
 * Location: South-east Falador (above ground entrance) and connected to Dwarven Mine (underground
 * entrance)
 */
class MiningGuildScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // NPC spawns are handled via npcs.toml
        // Shop NPCs and guildmaster are defined in MiningGuildNpcs.kt
        // Entrance requirement check is handled by GuildDoorDwarf.kt

        // TODO: Add shop implementations for Yarsul and Hendor
        // TODO: Add Mining cape purchase from Gadrin at 99 Mining
    }

    /** Checks if a player can enter the Mining Guild. Requires 60 Mining (can be boosted). */
    public fun canEnterMiningGuild(player: org.rsmod.game.entity.Player): Boolean {
        return GuildDefinitions.checkMiningLevel(player)
    }
}
