package org.rsmod.content.skills.crafting.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.craftingLvl
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpHeldU
import org.rsmod.api.script.onOpLocU
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.content.skills.crafting.craft_objs
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class Jewelry
@Inject
constructor(private val xpMods: XpModifiers, private val objRepo: ObjRepository) : PluginScript() {

    override fun ScriptContext.startup() {
        // Gold bar on furnace (requires a jewellery mould in inventory).
        onOpLocU(jewelry_locs.furnace, craft_objs.gold_bar) { smeltJewelry() }

        // Stringing all unstrung amulet variants to enchantment-ready forms.
        onOpHeldU(craft_objs.ball_of_wool, craft_objs.unstrung_gold_amulet) {
            stringAmulet(AmuletStringRecipe.GOLD)
        }

        onOpHeldU(craft_objs.ball_of_wool, jewelry_objs.unstrung_sapphire_amulet) {
            stringAmulet(AmuletStringRecipe.SAPPHIRE)
        }

        onOpHeldU(craft_objs.ball_of_wool, jewelry_objs.unstrung_emerald_amulet) {
            stringAmulet(AmuletStringRecipe.EMERALD)
        }

        onOpHeldU(craft_objs.ball_of_wool, jewelry_objs.unstrung_ruby_amulet) {
            stringAmulet(AmuletStringRecipe.RUBY)
        }

        onOpHeldU(craft_objs.ball_of_wool, jewelry_objs.unstrung_diamond_amulet) {
            stringAmulet(AmuletStringRecipe.DIAMOND)
        }

        onOpHeldU(craft_objs.ball_of_wool, jewelry_objs.unstrung_dragonstone_amulet) {
            stringAmulet(AmuletStringRecipe.DRAGONSTONE)
        }
    }

    private suspend fun ProtectedAccess.smeltJewelry() {
        if (!inv.contains(craft_objs.gold_bar)) {
            mes("You need a gold bar to make jewellery.")
            return
        }

        val availableRecipes =
            JewelryRecipe.entries.filter { recipe ->
                inv.contains(recipe.category.mouldObj) &&
                    (recipe.gemObj == null || inv.contains(recipe.gemObj)) &&
                    player.craftingLvl >= recipe.levelReq
            }

        if (availableRecipes.isEmpty()) {
            mes("You need a ring, necklace, amulet, or bracelet mould to make jewellery.")
            return
        }
        val recipe =
            availableRecipes.maxWithOrNull(
                compareBy<JewelryRecipe> { it.levelReq }.thenBy { it.xp }
            ) ?: return

        val count = countDialog("How many would you like to make?")
        if (count == 0) {
            return
        }

        val startCoords = player.coords
        repeat(count) {
            if (player.coords != startCoords) {
                return
            }

            if (!inv.contains(craft_objs.gold_bar)) {
                mes("You don't have any gold bars left.")
                return
            }

            if (recipe.gemObj != null && !inv.contains(recipe.gemObj)) {
                mes("You don't have any ${recipe.gemName} left.")
                return
            }

            val removedBar = invDel(inv, craft_objs.gold_bar, count = 1, strict = true)
            if (removedBar.failure) {
                return
            }

            if (recipe.gemObj != null) {
                val removedGem = invDel(inv, recipe.gemObj, count = 1, strict = true)
                if (removedGem.failure) {
                    invAddOrDrop(objRepo, craft_objs.gold_bar)
                    mes("You don't have the required gem.")
                    return
                }
            }

            anim(seqs.human_smithing)
            delay(4)

            val xp = recipe.xp * xpMods.get(player, stats.crafting)
            statAdvance(stats.crafting, xp)
            invAddOrDrop(objRepo, recipe.product)
            mes(recipe.makeMessage)
        }
    }

    private suspend fun ProtectedAccess.stringAmulet(recipe: AmuletStringRecipe) {
        if (player.craftingLvl < recipe.levelReq) {
            mes("You need a Crafting level of ${recipe.levelReq} to string this amulet.")
            return
        }

        if (!inv.contains(recipe.unstrung) || !inv.contains(craft_objs.ball_of_wool)) {
            mes("You need an unstrung amulet and a ball of wool.")
            return
        }

        val count = countDialog("How many would you like to string?")
        if (count == 0) {
            return
        }

        val startCoords = player.coords
        repeat(count) {
            if (player.coords != startCoords) {
                return
            }

            val removedAmulet = invDel(inv, recipe.unstrung, count = 1, strict = true)
            val removedWool = invDel(inv, craft_objs.ball_of_wool, count = 1, strict = true)
            if (removedAmulet.failure || removedWool.failure) {
                mes("You don't have the required materials.")
                return
            }

            anim(seqs.human_smithing)
            delay(2)

            val xp = STRINGING_XP * xpMods.get(player, stats.crafting)
            statAdvance(stats.crafting, xp)
            invAddOrDrop(objRepo, recipe.strung)
            mes("You string the amulet.")
        }
    }

    private enum class JewelryCategory(val mouldObj: ObjType, val displayName: String) {
        RING(craft_objs.ring_mould, "Ring"),
        NECKLACE(craft_objs.necklace_mould, "Necklace"),
        AMULET(craft_objs.amulet_mould, "Amulet"),
        BRACELET(jewelry_objs.bracelet_mould, "Bracelet"),
    }

    private enum class JewelryRecipe(
        val category: JewelryCategory,
        val gemObj: ObjType?,
        val gemName: String,
        val product: ObjType,
        val displayName: String,
        val levelReq: Int,
        val xp: Double,
        val makeMessage: String,
    ) {
        GOLD_RING(
            category = JewelryCategory.RING,
            gemObj = null,
            gemName = "",
            product = craft_objs.gold_ring,
            displayName = "Gold ring",
            levelReq = 5,
            xp = 15.0,
            makeMessage = "You make a gold ring.",
        ),
        SAPPHIRE_RING(
            category = JewelryCategory.RING,
            gemObj = craft_objs.sapphire,
            gemName = "sapphires",
            product = jewelry_objs.sapphire_ring,
            displayName = "Sapphire ring",
            levelReq = 20,
            xp = 40.0,
            makeMessage = "You set the sapphire into a ring.",
        ),
        EMERALD_RING(
            category = JewelryCategory.RING,
            gemObj = craft_objs.emerald,
            gemName = "emeralds",
            product = jewelry_objs.emerald_ring,
            displayName = "Emerald ring",
            levelReq = 27,
            xp = 55.0,
            makeMessage = "You set the emerald into a ring.",
        ),
        RUBY_RING(
            category = JewelryCategory.RING,
            gemObj = craft_objs.ruby,
            gemName = "rubies",
            product = jewelry_objs.ruby_ring,
            displayName = "Ruby ring",
            levelReq = 34,
            xp = 70.0,
            makeMessage = "You set the ruby into a ring.",
        ),
        DIAMOND_RING(
            category = JewelryCategory.RING,
            gemObj = craft_objs.diamond,
            gemName = "diamonds",
            product = jewelry_objs.diamond_ring,
            displayName = "Diamond ring",
            levelReq = 43,
            xp = 85.0,
            makeMessage = "You set the diamond into a ring.",
        ),
        DRAGONSTONE_RING(
            category = JewelryCategory.RING,
            gemObj = jewelry_objs.dragonstone,
            gemName = "dragonstones",
            product = jewelry_objs.dragonstone_ring,
            displayName = "Dragonstone ring",
            levelReq = 55,
            xp = 100.0,
            makeMessage = "You set the dragonstone into a ring.",
        ),
        GOLD_NECKLACE(
            category = JewelryCategory.NECKLACE,
            gemObj = null,
            gemName = "",
            product = craft_objs.gold_necklace,
            displayName = "Gold necklace",
            levelReq = 6,
            xp = 20.0,
            makeMessage = "You make a gold necklace.",
        ),
        SAPPHIRE_NECKLACE(
            category = JewelryCategory.NECKLACE,
            gemObj = craft_objs.sapphire,
            gemName = "sapphires",
            product = jewelry_objs.sapphire_necklace,
            displayName = "Sapphire necklace",
            levelReq = 22,
            xp = 55.0,
            makeMessage = "You set the sapphire into a necklace.",
        ),
        EMERALD_NECKLACE(
            category = JewelryCategory.NECKLACE,
            gemObj = craft_objs.emerald,
            gemName = "emeralds",
            product = jewelry_objs.emerald_necklace,
            displayName = "Emerald necklace",
            levelReq = 29,
            xp = 60.0,
            makeMessage = "You set the emerald into a necklace.",
        ),
        RUBY_NECKLACE(
            category = JewelryCategory.NECKLACE,
            gemObj = craft_objs.ruby,
            gemName = "rubies",
            product = jewelry_objs.ruby_necklace,
            displayName = "Ruby necklace",
            levelReq = 40,
            xp = 75.0,
            makeMessage = "You set the ruby into a necklace.",
        ),
        DIAMOND_NECKLACE(
            category = JewelryCategory.NECKLACE,
            gemObj = craft_objs.diamond,
            gemName = "diamonds",
            product = jewelry_objs.diamond_necklace,
            displayName = "Diamond necklace",
            levelReq = 56,
            xp = 90.0,
            makeMessage = "You set the diamond into a necklace.",
        ),
        DRAGONSTONE_NECKLACE(
            category = JewelryCategory.NECKLACE,
            gemObj = jewelry_objs.dragonstone,
            gemName = "dragonstones",
            product = jewelry_objs.dragonstone_necklace,
            displayName = "Dragonstone necklace",
            levelReq = 72,
            xp = 105.0,
            makeMessage = "You set the dragonstone into a necklace.",
        ),
        GOLD_AMULET(
            category = JewelryCategory.AMULET,
            gemObj = null,
            gemName = "",
            product = craft_objs.unstrung_gold_amulet,
            displayName = "Gold amulet (u)",
            levelReq = 8,
            xp = 30.0,
            makeMessage = "You make an unstrung gold amulet.",
        ),
        SAPPHIRE_AMULET(
            category = JewelryCategory.AMULET,
            gemObj = craft_objs.sapphire,
            gemName = "sapphires",
            product = jewelry_objs.unstrung_sapphire_amulet,
            displayName = "Sapphire amulet (u)",
            levelReq = 24,
            xp = 65.0,
            makeMessage = "You make an unstrung sapphire amulet.",
        ),
        EMERALD_AMULET(
            category = JewelryCategory.AMULET,
            gemObj = craft_objs.emerald,
            gemName = "emeralds",
            product = jewelry_objs.unstrung_emerald_amulet,
            displayName = "Emerald amulet (u)",
            levelReq = 31,
            xp = 70.0,
            makeMessage = "You make an unstrung emerald amulet.",
        ),
        RUBY_AMULET(
            category = JewelryCategory.AMULET,
            gemObj = craft_objs.ruby,
            gemName = "rubies",
            product = jewelry_objs.unstrung_ruby_amulet,
            displayName = "Ruby amulet (u)",
            levelReq = 50,
            xp = 85.0,
            makeMessage = "You make an unstrung ruby amulet.",
        ),
        DIAMOND_AMULET(
            category = JewelryCategory.AMULET,
            gemObj = craft_objs.diamond,
            gemName = "diamonds",
            product = jewelry_objs.unstrung_diamond_amulet,
            displayName = "Diamond amulet (u)",
            levelReq = 70,
            xp = 100.0,
            makeMessage = "You make an unstrung diamond amulet.",
        ),
        DRAGONSTONE_AMULET(
            category = JewelryCategory.AMULET,
            gemObj = jewelry_objs.dragonstone,
            gemName = "dragonstones",
            product = jewelry_objs.unstrung_dragonstone_amulet,
            displayName = "Dragonstone amulet (u)",
            levelReq = 80,
            xp = 150.0,
            makeMessage = "You make an unstrung dragonstone amulet.",
        ),
        GOLD_BRACELET(
            category = JewelryCategory.BRACELET,
            gemObj = null,
            gemName = "",
            product = jewelry_objs.gold_bracelet,
            displayName = "Gold bracelet",
            levelReq = 7,
            xp = 25.0,
            makeMessage = "You make a gold bracelet.",
        ),
        SAPPHIRE_BRACELET(
            category = JewelryCategory.BRACELET,
            gemObj = craft_objs.sapphire,
            gemName = "sapphires",
            product = jewelry_objs.sapphire_bracelet,
            displayName = "Sapphire bracelet",
            levelReq = 23,
            xp = 60.0,
            makeMessage = "You set the sapphire into a bracelet.",
        ),
        EMERALD_BRACELET(
            category = JewelryCategory.BRACELET,
            gemObj = craft_objs.emerald,
            gemName = "emeralds",
            product = jewelry_objs.emerald_bracelet,
            displayName = "Emerald bracelet",
            levelReq = 30,
            xp = 65.0,
            makeMessage = "You set the emerald into a bracelet.",
        ),
        RUBY_BRACELET(
            category = JewelryCategory.BRACELET,
            gemObj = craft_objs.ruby,
            gemName = "rubies",
            product = jewelry_objs.ruby_bracelet,
            displayName = "Ruby bracelet",
            levelReq = 42,
            xp = 80.0,
            makeMessage = "You set the ruby into a bracelet.",
        ),
        DIAMOND_BRACELET(
            category = JewelryCategory.BRACELET,
            gemObj = craft_objs.diamond,
            gemName = "diamonds",
            product = jewelry_objs.diamond_bracelet,
            displayName = "Diamond bracelet",
            levelReq = 58,
            xp = 95.0,
            makeMessage = "You set the diamond into a bracelet.",
        ),
        DRAGONSTONE_BRACELET(
            category = JewelryCategory.BRACELET,
            gemObj = jewelry_objs.dragonstone,
            gemName = "dragonstones",
            product = jewelry_objs.dragonstone_bracelet,
            displayName = "Dragonstone bracelet",
            levelReq = 74,
            xp = 110.0,
            makeMessage = "You set the dragonstone into a bracelet.",
        ),
    }

    private enum class AmuletStringRecipe(
        val unstrung: ObjType,
        val strung: ObjType,
        val levelReq: Int,
    ) {
        GOLD(craft_objs.unstrung_gold_amulet, craft_objs.gold_amulet, 8),
        SAPPHIRE(jewelry_objs.unstrung_sapphire_amulet, jewelry_objs.strung_sapphire_amulet, 24),
        EMERALD(jewelry_objs.unstrung_emerald_amulet, jewelry_objs.strung_emerald_amulet, 31),
        RUBY(jewelry_objs.unstrung_ruby_amulet, jewelry_objs.strung_ruby_amulet, 50),
        DIAMOND(jewelry_objs.unstrung_diamond_amulet, jewelry_objs.strung_diamond_amulet, 70),
        DRAGONSTONE(
            jewelry_objs.unstrung_dragonstone_amulet,
            jewelry_objs.strung_dragonstone_amulet,
            80,
        ),
    }

    companion object {
        private const val STRINGING_XP = 4.0
    }
}

// Local object refs for jewellery symbols not yet exposed in craft_objs.
internal typealias jewelry_objs = JewelryObjs

internal object JewelryObjs : ObjReferences() {
    val bracelet_mould = find("jewl_bracelet_mould")
    val dragonstone = find("dragonstone")

    val sapphire_ring = find("sapphire_ring")
    val emerald_ring = find("emerald_ring")
    val ruby_ring = find("ruby_ring")
    val diamond_ring = find("diamond_ring")
    val dragonstone_ring = find("dragonstone_ring")

    val sapphire_necklace = find("sapphire_necklace")
    val emerald_necklace = find("emerald_necklace")
    val ruby_necklace = find("ruby_necklace")
    val diamond_necklace = find("diamond_necklace")
    val dragonstone_necklace = find("dragonstone_necklace")

    val unstrung_sapphire_amulet = find("unstrung_sapphire_amulet")
    val unstrung_emerald_amulet = find("unstrung_emerald_amulet")
    val unstrung_ruby_amulet = find("unstrung_ruby_amulet")
    val unstrung_diamond_amulet = find("unstrung_diamond_amulet")
    val unstrung_dragonstone_amulet = find("unstrung_dragonstone_amulet")

    val strung_sapphire_amulet = find("strung_sapphire_amulet")
    val strung_emerald_amulet = find("strung_emerald_amulet")
    val strung_ruby_amulet = find("strung_ruby_amulet")
    val strung_diamond_amulet = find("strung_diamond_amulet")
    val strung_dragonstone_amulet = find("strung_dragonstone_amulet")

    val gold_bracelet = find("jewl_gold_bracelet")
    val sapphire_bracelet = find("jewl_sapphire_bracelet")
    val emerald_bracelet = find("jewl_emerald_bracelet")
    val ruby_bracelet = find("jewl_ruby_bracelet")
    val diamond_bracelet = find("jewl_diamond_bracelet")
    val dragonstone_bracelet = find("jewl_dragonstone_bracelet")
}

// LocReferences for jewelry locations (furnaces)
internal typealias jewelry_locs = JewelryLocs

internal object JewelryLocs : LocReferences() {
    val furnace = find("furnace")
}
