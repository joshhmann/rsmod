package org.rsmod.content.skills.crafting.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.craftingLvl
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpHeldU
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.content.skills.crafting.craft_objs
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Leatherworking implementation for Crafting skill.
 *
 * Mechanics:
 * 1. Tan cowhide to leather (normal or hard) - via NPC (Ellis in Al Kharid)
 * 2. Use needle + thread + leather to craft leather items
 * 3. Hard leather body requires hard leather specifically
 *
 * Level requirements (F2P items only): Leather gloves: 1 Crafting, 1 leather, 1 thread, 13.8 XP
 * Leather boots: 7 Crafting, 1 leather, 1 thread, 16.25 XP Leather cowl: 9 Crafting, 1 leather, 1
 * thread, 18.5 XP Leather vambraces: 11 Crafting, 1 leather, 1 thread, 22.0 XP Leather body: 14
 * Crafting, 1 leather, 1 thread, 25.0 XP Leather chaps: 18 Crafting, 1 leather, 1 thread, 27.0 XP
 * Hard leather body: 28 Crafting, 1 hard leather, 1 thread, 35.0 XP
 */
class Leatherworking
@Inject
constructor(private val xpMods: XpModifiers, private val objRepo: ObjRepository) : PluginScript() {

    override fun ScriptContext.startup() {
        // Needle on leather -> leather item menu
        onOpHeldU(craft_objs.needle, craft_objs.leather) { craftLeatherItem(isHardLeather = false) }

        // Needle on hard leather -> hard leather body only
        onOpHeldU(craft_objs.needle, craft_objs.hard_leather) { craftHardLeatherBody() }

        // Needle on base leather armour pieces + studs -> studded leather items
        onOpHeldU(craft_objs.needle, craft_objs.leather_armour) {
            craftStuddedItem(StuddedItem.BODY)
        }
        onOpHeldU(craft_objs.needle, craft_objs.leather_chaps) {
            craftStuddedItem(StuddedItem.CHAPS)
        }

        // Ellis the Tanner interaction (NPC ID 3231)
        onOpNpc1(leatherworking_npcs.ellis_tanner) { openTanningInterface() }
    }

    private suspend fun ProtectedAccess.craftLeatherItem(isHardLeather: Boolean) {
        val leatherType = if (isHardLeather) craft_objs.hard_leather else craft_objs.leather
        val leatherName = if (isHardLeather) "hard leather" else "leather"

        if (
            !inv.contains(leatherType) ||
                !inv.contains(craft_objs.needle) ||
                !inv.contains(craft_objs.thread)
        ) {
            when {
                !inv.contains(craft_objs.needle) -> mes("You need a needle to craft leather.")
                !inv.contains(craft_objs.thread) -> mes("You need thread to craft leather.")
                else -> mes("You need $leatherName to craft this item.")
            }
            return
        }

        // Show leather crafting options
        val item = if (isHardLeather) LeatherItem.HARD_BODY else leatherOptionsDialog() ?: return

        if (player.craftingLvl < item.levelReq) {
            mes("You need a Crafting level of ${item.levelReq} to craft ${item.displayName}.")
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

            // Check materials again
            if (!inv.contains(leatherType) || !inv.contains(craft_objs.thread)) {
                mes("You don't have enough materials.")
                return
            }

            // Delete materials (keep needle, it doesn't get consumed)
            val removedLeather = invDel(inv, leatherType, count = 1, strict = true)
            val removedThread = invDel(inv, craft_objs.thread, count = 1, strict = true)

            if (removedLeather.failure || removedThread.failure) {
                mes("You don't have enough materials.")
                return
            }

            // Leather crafting animation (ID 1249)
            anim(leatherworking_seqs.human_leather_crafting)
            delay(3)

            // Grant XP and give item
            val xp = item.xp * xpMods.get(player, stats.crafting)
            statAdvance(stats.crafting, xp)
            invAddOrDrop(objRepo, item.product)
            mes("You craft a ${item.displayName.lowercase()}.")
        }
    }

    private suspend fun ProtectedAccess.craftHardLeatherBody() {
        craftLeatherItem(isHardLeather = true)
    }

    private suspend fun ProtectedAccess.leatherOptionsDialog(): LeatherItem? {
        return when (
            choice5(
                "Leather gloves",
                LeatherItem.GLOVES,
                "Leather boots",
                LeatherItem.BOOTS,
                "Leather cowl",
                LeatherItem.COWL,
                "Leather vambraces",
                LeatherItem.VAMBRACES,
                "More options...",
                null,
                "What would you like to make?",
            ) ?: choice2("Leather body", LeatherItem.BODY, "Leather chaps", LeatherItem.CHAPS)
        ) {
            LeatherItem.GLOVES -> LeatherItem.GLOVES
            LeatherItem.BOOTS -> LeatherItem.BOOTS
            LeatherItem.COWL -> LeatherItem.COWL
            LeatherItem.VAMBRACES -> LeatherItem.VAMBRACES
            LeatherItem.BODY -> LeatherItem.BODY
            LeatherItem.CHAPS -> LeatherItem.CHAPS
            null -> null
            else -> null
        }
    }

    private suspend fun ProtectedAccess.craftStuddedItem(item: StuddedItem) {
        if (!inv.contains(craft_objs.needle)) {
            mes("You need a needle to craft studded leather.")
            return
        }
        if (!inv.contains(craft_objs.thread)) {
            mes("You need thread to craft studded leather.")
            return
        }
        if (!inv.contains(leatherworking_objs.studs)) {
            mes("You need steel studs to make ${item.displayName.lowercase()}.")
            return
        }
        if (!inv.contains(item.baseItem)) {
            mes("You need ${item.baseName} to make ${item.displayName.lowercase()}.")
            return
        }
        if (player.craftingLvl < item.levelReq) {
            mes("You need a Crafting level of ${item.levelReq} to make ${item.displayName}.")
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

            if (
                !inv.contains(item.baseItem) ||
                    !inv.contains(leatherworking_objs.studs) ||
                    !inv.contains(craft_objs.thread)
            ) {
                mes("You don't have enough materials.")
                return
            }

            val removedBase = invDel(inv, item.baseItem, count = 1, strict = true)
            val removedStuds = invDel(inv, leatherworking_objs.studs, count = 1, strict = true)
            val removedThread = invDel(inv, craft_objs.thread, count = 1, strict = true)
            if (removedBase.failure || removedStuds.failure || removedThread.failure) {
                mes("You don't have enough materials.")
                return
            }

            anim(leatherworking_seqs.human_leather_crafting)
            delay(3)

            val xp = item.xp * xpMods.get(player, stats.crafting)
            statAdvance(stats.crafting, xp)
            invAddOrDrop(objRepo, item.product)
            mes("You craft ${item.displayName.lowercase()}.")
        }
    }

    private suspend fun ProtectedAccess.openTanningInterface() {
        // Simple tanning dialog - Ellis can tan cowhide to leather or hard leather
        val choice =
            choice3(
                "Leather (1gp per hide)",
                1,
                "Hard leather (3gp per hide)",
                2,
                "Never mind",
                3,
                "What would you like to tan?",
            )

        when (choice) {
            1 -> tanHides(tanToHard = false)
            2 -> tanHides(tanToHard = true)
        }
    }

    private suspend fun ProtectedAccess.tanHides(tanToHard: Boolean) {
        val productName = if (tanToHard) "hard leather" else "leather"
        val costPerHide = if (tanToHard) 3 else 1

        if (!inv.contains(craft_objs.cow_hide)) {
            mes("You don't have any cow hides to tan.")
            return
        }

        val hideCount = invTotal(inv, craft_objs.cow_hide)
        val totalCost = hideCount * costPerHide
        val playerCoins = invTotal(inv, objs.coins)

        if (playerCoins < totalCost) {
            mes("You need $totalCost coins to tan $hideCount hides.")
            return
        }

        // Delete cow hides and coins, add leather
        invDel(inv, craft_objs.cow_hide, count = hideCount)
        invDel(inv, objs.coins, count = totalCost)

        val product = if (tanToHard) craft_objs.hard_leather else craft_objs.leather
        invAddOrDrop(objRepo, product, count = hideCount)

        mes("Ellis tans your cow hides into $productName.")
    }

    private enum class LeatherItem(
        val product: ObjType,
        val displayName: String,
        val levelReq: Int,
        val xp: Double,
    ) {
        GLOVES(
            product = craft_objs.leather_gloves,
            displayName = "Leather gloves",
            levelReq = 1,
            xp = 13.8,
        ),
        BOOTS(
            product = craft_objs.leather_boots,
            displayName = "Leather boots",
            levelReq = 7,
            xp = 16.25,
        ),
        COWL(
            product = craft_objs.leather_cowl,
            displayName = "Leather cowl",
            levelReq = 9,
            xp = 18.5,
        ),
        VAMBRACES(
            product = craft_objs.leather_vambraces,
            displayName = "Leather vambraces",
            levelReq = 11,
            xp = 22.0,
        ),
        BODY(
            product = craft_objs.leather_armour,
            displayName = "Leather body",
            levelReq = 14,
            xp = 25.0,
        ),
        CHAPS(
            product = craft_objs.leather_chaps,
            displayName = "Leather chaps",
            levelReq = 18,
            xp = 27.0,
        ),
        HARD_BODY(
            product = craft_objs.hardleather_body,
            displayName = "Hard leather body",
            levelReq = 28,
            xp = 35.0,
        ),
    }

    private enum class StuddedItem(
        val baseItem: ObjType,
        val baseName: String,
        val product: ObjType,
        val displayName: String,
        val levelReq: Int,
        val xp: Double,
    ) {
        BODY(
            baseItem = craft_objs.leather_armour,
            baseName = "a leather body",
            product = leatherworking_objs.studded_body,
            displayName = "Studded body",
            levelReq = 41,
            xp = 86.0,
        ),
        CHAPS(
            baseItem = craft_objs.leather_chaps,
            baseName = "leather chaps",
            product = leatherworking_objs.studded_chaps,
            displayName = "Studded chaps",
            levelReq = 44,
            xp = 87.0,
        ),
    }
}

internal typealias leatherworking_objs = LeatherworkingObjs

internal object LeatherworkingObjs : ObjReferences() {
    val studs = find("studs")
    val studded_body = find("studded_body")
    val studded_chaps = find("studded_chaps")
}

internal typealias leatherworking_npcs = LeatherworkingNpcs

internal object LeatherworkingNpcs : NpcReferences() {
    val ellis_tanner = find("ellis_tanner")
}

internal typealias leatherworking_seqs = LeatherworkingSeqs

internal object LeatherworkingSeqs : SeqReferences() {
    val human_leather_crafting = find("human_leather_crafting")
}
