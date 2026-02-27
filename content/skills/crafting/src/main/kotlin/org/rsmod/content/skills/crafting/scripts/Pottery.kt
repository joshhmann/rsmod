package org.rsmod.content.skills.crafting.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.craftingLvl
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpLocU
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.content.skills.crafting.craft_objs
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Pottery implementation for Crafting skill.
 *
 * Mechanics:
 * 1. Use clay on water source → Soft clay
 * 2. Use soft clay on pottery wheel → Unfired pot/bowl
 * 3. Use unfired item on pottery oven → Finished fired item
 *
 * Level requirements: Pot: 1 Crafting
 * - Wheel: 6.3 XP
 * - Fire: 6.3 XP Bowl: 8 Crafting
 * - Wheel: 15.0 XP
 * - Fire: 15.0 XP
 *
 * Animations: Pottery wheel: 883 (human_potterywheel) Pottery oven: 1317 (potteryoven_quick)
 */
class Pottery
@Inject
constructor(private val xpMods: XpModifiers, private val objRepo: ObjRepository) : PluginScript() {

    override fun ScriptContext.startup() {
        // Soft clay on pottery wheel → unfired item
        onOpLocU(pottery_locs.wheel, craft_objs.softclay) { usePotteryWheel() }

        // Unfired pot on pottery oven → pot
        onOpLocU(pottery_locs.oven, craft_objs.pot_unfired) { firePottery(PotteryItem.POT) }

        // Unfired bowl on pottery oven → bowl
        onOpLocU(pottery_locs.oven, craft_objs.bowl_unfired) { firePottery(PotteryItem.BOWL) }
    }

    private suspend fun ProtectedAccess.usePotteryWheel() {
        if (!inv.contains(craft_objs.softclay)) {
            mes("You need soft clay to use the pottery wheel.")
            return
        }

        val item =
            choice2(
                "Pot",
                PotteryItem.POT,
                "Bowl",
                PotteryItem.BOWL,
                title = "What would you like to make?",
            ) ?: return

        if (player.craftingLvl < item.wheelLevelReq) {
            mes("You need a Crafting level of ${item.wheelLevelReq} to make this.")
            return
        }

        val count = countDialog("How many would you like to make?")
        if (count == 0) {
            return
        }

        val startCoords = player.coords
        repeat(count) {
            if (player.coords != startCoords) {
                return
            }

            val removed = invDel(inv, craft_objs.softclay, count = 1, strict = true)
            if (removed.failure) {
                return
            }

            // Pottery wheel animation
            anim(pottery_seqs.wheel)
            delay(3)

            // Grant XP and give unfired item
            val xp = item.wheelXp * xpMods.get(player, stats.crafting)
            statAdvance(stats.crafting, xp)
            invAddOrDrop(objRepo, item.unfired)
            mes("You shape the soft clay into a ${item.displayName.lowercase()}.")
        }
    }

    private suspend fun ProtectedAccess.firePottery(item: PotteryItem) {
        if (player.craftingLvl < item.fireLevelReq) {
            mes("You need a Crafting level of ${item.fireLevelReq} to fire this.")
            return
        }

        if (!inv.contains(item.unfired)) {
            mes("You don't have any unfired ${item.displayName.lowercase()}s to fire.")
            return
        }

        val count = countDialog("How many would you like to fire?")
        if (count == 0) {
            return
        }

        val startCoords = player.coords
        repeat(count) {
            if (player.coords != startCoords) {
                return
            }

            val removed = invDel(inv, item.unfired, count = 1, strict = true)
            if (removed.failure) {
                return
            }

            // Fire pottery animation
            anim(pottery_seqs.oven)
            delay(3)

            // Grant XP and give fired item
            val xp = item.fireXp * xpMods.get(player, stats.crafting)
            statAdvance(stats.crafting, xp)
            invAddOrDrop(objRepo, item.fired)
            mes("You fire the ${item.displayName.lowercase()} in the oven.")
        }
    }

    private enum class PotteryItem(
        val unfired: ObjType,
        val fired: ObjType,
        val displayName: String,
        val wheelLevelReq: Int,
        val wheelXp: Double,
        val fireLevelReq: Int,
        val fireXp: Double,
    ) {
        POT(
            unfired = craft_objs.pot_unfired,
            fired = craft_objs.pot_empty,
            displayName = "Pot",
            wheelLevelReq = 1,
            wheelXp = 6.3,
            fireLevelReq = 1,
            fireXp = 6.3,
        ),
        BOWL(
            unfired = craft_objs.bowl_unfired,
            fired = craft_objs.bowl_empty,
            displayName = "Bowl",
            wheelLevelReq = 8,
            wheelXp = 15.0,
            fireLevelReq = 8,
            fireXp = 15.0,
        ),
    }

    companion object {
        // Animation IDs
        private const val WHEEL_ANIM = 883 // human_potterywheel
        private const val OVEN_ANIM = 1317 // potteryoven_quick
    }
}

// LocReferences for pottery locations
internal typealias pottery_locs = PotteryLocs

internal object PotteryLocs : LocReferences() {
    // Pottery wheel (ID 14887)
    val wheel = find("potterywheel")
    // Pottery oven (ID 14888)
    val oven = find("potteryoven")
}

// SeqReferences for pottery animations
internal typealias pottery_seqs = PotterySeqs

internal object PotterySeqs : org.rsmod.api.type.refs.seq.SeqReferences() {
    val wheel = find("human_potterywheel") // ID 883
    val oven = find("potteryoven_quick") // ID 1317
}
