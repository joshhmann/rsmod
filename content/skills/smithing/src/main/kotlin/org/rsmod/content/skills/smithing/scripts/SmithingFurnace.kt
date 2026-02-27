package org.rsmod.content.skills.smithing.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.smithingLvl
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpLoc1
import org.rsmod.api.script.onOpLocU
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.content.skills.smithing.configs.SmithingLocs
import org.rsmod.content.skills.smithing.configs.SmithingObjs as objs
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Furnace interactions for Smithing skill.
 *
 * Supports:
 * - Clicking furnace to open smelting selection dialog
 * - Using ores on furnace to smelt bars directly
 */
class SmithingFurnace
@Inject
constructor(private val xpMods: XpModifiers, private val objRepo: ObjRepository) : PluginScript() {

    override fun ScriptContext.startup() {
        // ---- Click furnace (open smelting menu) ----
        onOpLoc1(SmithingLocs.furnace) { openSmeltingMenu() }
        onOpLoc1(SmithingLocs.furnace2) { openSmeltingMenu() }

        // ---- Use ore on furnace ----
        // Bronze: copper + tin
        onOpLocU(SmithingLocs.furnace, objs.copper_ore) {
            smeltWithSecondary(SMELT_BRONZE, objs.tin_ore)
        }
        onOpLocU(SmithingLocs.furnace2, objs.copper_ore) {
            smeltWithSecondary(SMELT_BRONZE, objs.tin_ore)
        }

        // Iron: iron ore alone
        onOpLocU(SmithingLocs.furnace, objs.iron_ore) { smeltSingle(SMELT_IRON) }
        onOpLocU(SmithingLocs.furnace2, objs.iron_ore) { smeltSingle(SMELT_IRON) }

        // Steel: iron + coal
        onOpLocU(SmithingLocs.furnace, objs.coal) { smeltWithSecondary(SMELT_STEEL, objs.iron_ore) }
        onOpLocU(SmithingLocs.furnace2, objs.coal) {
            smeltWithSecondary(SMELT_STEEL, objs.iron_ore)
        }

        // Mithril: mithril + coal
        onOpLocU(SmithingLocs.furnace, objs.mithril_ore) {
            smeltWithSecondary(SMELT_MITHRIL, objs.coal)
        }
        onOpLocU(SmithingLocs.furnace2, objs.mithril_ore) {
            smeltWithSecondary(SMELT_MITHRIL, objs.coal)
        }

        // Adamant: adamantite + coal
        onOpLocU(SmithingLocs.furnace, objs.adamantite_ore) {
            smeltWithSecondary(SMELT_ADAMANT, objs.coal)
        }
        onOpLocU(SmithingLocs.furnace2, objs.adamantite_ore) {
            smeltWithSecondary(SMELT_ADAMANT, objs.coal)
        }

        // Rune: runite + 8 coal
        onOpLocU(SmithingLocs.furnace, objs.runite_ore) { smeltRune() }
        onOpLocU(SmithingLocs.furnace2, objs.runite_ore) { smeltRune() }

        // Gold / silver — single ore
        onOpLocU(SmithingLocs.furnace, objs.gold_ore) { smeltSingle(SMELT_GOLD) }
        onOpLocU(SmithingLocs.furnace2, objs.gold_ore) { smeltSingle(SMELT_GOLD) }
        onOpLocU(SmithingLocs.furnace, objs.silver_ore) { smeltSingle(SMELT_SILVER) }
        onOpLocU(SmithingLocs.furnace2, objs.silver_ore) { smeltSingle(SMELT_SILVER) }
    }

    /** Opens smelting menu when clicking furnace - shows available bars based on inventory ores. */
    private suspend fun ProtectedAccess.openSmeltingMenu() {
        val availableSmelts = mutableListOf<SmeltDef>()

        // Check each smelt type in order of tier (bronze to rune)
        if (canSmeltBronze()) availableSmelts.add(SMELT_BRONZE)
        if (canSmeltIron()) availableSmelts.add(SMELT_IRON)
        if (canSmeltSilver()) availableSmelts.add(SMELT_SILVER)
        if (canSmeltSteel()) availableSmelts.add(SMELT_STEEL)
        if (canSmeltGold()) availableSmelts.add(SMELT_GOLD)
        if (canSmeltMithril()) availableSmelts.add(SMELT_MITHRIL)
        if (canSmeltAdamant()) availableSmelts.add(SMELT_ADAMANT)
        if (canSmeltRune()) availableSmelts.add(SMELT_RUNE)

        if (availableSmelts.isEmpty()) {
            mes("You don't have any ore to smelt.")
            return
        }

        // For single available smelt, just do it directly
        if (availableSmelts.size == 1) {
            val def = availableSmelts.first()
            if (def.ore2 != null) {
                smeltWithSecondary(def, def.ore2)
            } else {
                smeltSingle(def)
            }
            return
        }

        // Multiple options - use count dialog for quantity, then ask which bar
        // For now, default to the highest tier available
        val def = availableSmelts.last()
        if (def.ore2 != null) {
            smeltWithSecondary(def, def.ore2)
        } else {
            smeltSingle(def)
        }
    }

    // ---- Availability checks ----

    private fun ProtectedAccess.canSmeltBronze(): Boolean =
        inv.contains(objs.copper_ore) &&
            inv.contains(objs.tin_ore) &&
            player.smithingLvl >= SMELT_BRONZE.levelReq

    private fun ProtectedAccess.canSmeltIron(): Boolean =
        inv.contains(objs.iron_ore) && player.smithingLvl >= SMELT_IRON.levelReq

    private fun ProtectedAccess.canSmeltSilver(): Boolean =
        inv.contains(objs.silver_ore) && player.smithingLvl >= SMELT_SILVER.levelReq

    private fun ProtectedAccess.canSmeltSteel(): Boolean =
        inv.contains(objs.iron_ore) &&
            inv.contains(objs.coal) &&
            player.smithingLvl >= SMELT_STEEL.levelReq

    private fun ProtectedAccess.canSmeltGold(): Boolean =
        inv.contains(objs.gold_ore) && player.smithingLvl >= SMELT_GOLD.levelReq

    private fun ProtectedAccess.canSmeltMithril(): Boolean =
        inv.contains(objs.mithril_ore) &&
            inv.contains(objs.coal) &&
            player.smithingLvl >= SMELT_MITHRIL.levelReq

    private fun ProtectedAccess.canSmeltAdamant(): Boolean =
        inv.contains(objs.adamantite_ore) &&
            inv.contains(objs.coal) &&
            player.smithingLvl >= SMELT_ADAMANT.levelReq

    private fun ProtectedAccess.canSmeltRune(): Boolean =
        inv.contains(objs.runite_ore) &&
            countCoalInInv() >= 8 &&
            player.smithingLvl >= SMELT_RUNE.levelReq

    private fun ProtectedAccess.countCoalInInv(): Int =
        inv.mapSlots { _, obj -> obj?.id == objs.coal.id }.sumOf { inv[it]?.count ?: 0 }

    private data class SmeltDef(
        val bar: ObjType,
        val ore1: ObjType,
        val ore2: ObjType? = null,
        val levelReq: Int,
        val xp: Double,
    )

    private suspend fun ProtectedAccess.smeltWithSecondary(def: SmeltDef, secondaryOre: ObjType) {
        if (player.smithingLvl < def.levelReq) {
            mes("You need a Smithing level of ${def.levelReq} to smelt this bar.")
            return
        }
        if (!inv.contains(def.ore1) || !inv.contains(secondaryOre)) {
            mes("You don't have the required ores to smelt this bar.")
            return
        }

        val count = countDialog("How many would you like to smelt?")
        if (count == 0) {
            return
        }

        val startCoords = player.coords
        repeat(count) {
            if (player.coords != startCoords) {
                return
            }
            if (!inv.contains(def.ore1) || !inv.contains(secondaryOre)) {
                mes("You don't have the required ores to smelt this bar.")
                return
            }
            invDel(inv, def.ore1, count = 1)
            invDel(inv, secondaryOre, count = 1)
            anim(seqs.human_smithing)
            delay(5)
            val xp = def.xp * xpMods.get(player, stats.smithing)
            statAdvance(stats.smithing, xp)
            invAddOrDrop(objRepo, def.bar)
            mes("You smelt the ores into a bar.")
        }
    }

    private suspend fun ProtectedAccess.smeltRune() {
        if (player.smithingLvl < SMELT_RUNE.levelReq) {
            mes("You need a Smithing level of ${SMELT_RUNE.levelReq} to smelt this bar.")
            return
        }
        if (!inv.contains(objs.runite_ore) || countCoalInInv() < 8) {
            mes("You need 1 runite ore and 8 coal to smelt a rune bar.")
            return
        }

        val count = countDialog("How many would you like to smelt?")
        if (count == 0) {
            return
        }

        val startCoords = player.coords
        repeat(count) {
            if (player.coords != startCoords) {
                return
            }
            if (!inv.contains(objs.runite_ore) || countCoalInInv() < 8) {
                mes("You don't have enough ores to smelt this bar.")
                return
            }
            invDel(inv, objs.runite_ore, count = 1)
            invDel(inv, objs.coal, count = 8)
            anim(seqs.human_smithing)
            delay(5)
            val xp = SMELT_RUNE.xp * xpMods.get(player, stats.smithing)
            statAdvance(stats.smithing, xp)
            invAddOrDrop(objRepo, objs.runite_bar)
            mes("You smelt the ores into a rune bar.")
        }
    }

    private suspend fun ProtectedAccess.smeltSingle(def: SmeltDef) {
        if (player.smithingLvl < def.levelReq) {
            mes("You need a Smithing level of ${def.levelReq} to smelt this bar.")
            return
        }
        if (!inv.contains(def.ore1)) {
            mes("You don't have the required ore to smelt this bar.")
            return
        }

        val count = countDialog("How many would you like to smelt?")
        if (count == 0) {
            return
        }

        val startCoords = player.coords
        repeat(count) {
            if (player.coords != startCoords) {
                return
            }
            if (!inv.contains(def.ore1)) {
                mes("You don't have the required ore to smelt this bar.")
                return
            }
            invDel(inv, def.ore1, count = 1)
            anim(seqs.human_smithing)
            delay(5)
            val xp = def.xp * xpMods.get(player, stats.smithing)
            statAdvance(stats.smithing, xp)
            invAddOrDrop(objRepo, def.bar)
            mes("You smelt the ore into a bar.")
        }
    }

    companion object {
        private val SMELT_BRONZE =
            SmeltDef(objs.bronze_bar, objs.copper_ore, objs.tin_ore, levelReq = 1, xp = 6.2)
        private val SMELT_IRON =
            SmeltDef(objs.iron_bar, objs.iron_ore, null, levelReq = 15, xp = 12.5)
        private val SMELT_STEEL =
            SmeltDef(objs.steel_bar, objs.iron_ore, objs.coal, levelReq = 30, xp = 17.5)
        private val SMELT_MITHRIL =
            SmeltDef(objs.mithril_bar, objs.mithril_ore, objs.coal, levelReq = 50, xp = 30.0)
        private val SMELT_ADAMANT =
            SmeltDef(objs.adamantite_bar, objs.adamantite_ore, objs.coal, levelReq = 70, xp = 37.5)
        private val SMELT_RUNE =
            SmeltDef(objs.runite_bar, objs.runite_ore, objs.coal, levelReq = 85, xp = 50.0)
        private val SMELT_GOLD =
            SmeltDef(objs.gold_bar, objs.gold_ore, null, levelReq = 40, xp = 22.5)
        private val SMELT_SILVER =
            SmeltDef(objs.silver_bar, objs.silver_ore, null, levelReq = 20, xp = 13.7)
    }
}
