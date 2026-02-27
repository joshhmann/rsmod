package org.rsmod.content.skills.crafting.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.craftingLvl
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpHeldU
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.api.type.refs.seq.SeqReferences
import org.rsmod.content.skills.crafting.craft_objs
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/**
 * Gem cutting implementation for Crafting skill.
 *
 * Mechanics: Use chisel on uncut gem to cut it. Chance to crush gems (only for semi-precious: opal,
 * jade, red topaz).
 *
 * Level requirements (F2P + P2P): Opal: 1 Crafting, 15 XP (semi-precious, can crush) Jade: 13
 * Crafting, 20 XP (semi-precious, can crush) Red topaz: 16 Crafting, 25 XP (semi-precious, can
 * crush) Sapphire: 20 Crafting, 50 XP (precious, no crush) Emerald: 27 Crafting, 67.5 XP (precious,
 * no crush) Ruby: 34 Crafting, 85 XP (precious, no crush) Diamond: 43 Crafting, 107.5 XP (precious,
 * no crush) Dragonstone: 55 Crafting, 137.5 XP (precious, no crush)
 */
class GemCutting
@Inject
constructor(private val xpMods: XpModifiers, private val objRepo: ObjRepository) : PluginScript() {

    override fun ScriptContext.startup() {
        // Chisel on uncut gem
        onOpHeldU(craft_objs.chisel, craft_objs.uncut_opal) { cutGem(GemType.OPAL) }
        onOpHeldU(craft_objs.chisel, craft_objs.uncut_jade) { cutGem(GemType.JADE) }
        onOpHeldU(craft_objs.chisel, craft_objs.uncut_red_topaz) { cutGem(GemType.RED_TOPAZ) }
        onOpHeldU(craft_objs.chisel, craft_objs.uncut_sapphire) { cutGem(GemType.SAPPHIRE) }
        onOpHeldU(craft_objs.chisel, craft_objs.uncut_emerald) { cutGem(GemType.EMERALD) }
        onOpHeldU(craft_objs.chisel, craft_objs.uncut_ruby) { cutGem(GemType.RUBY) }
        onOpHeldU(craft_objs.chisel, craft_objs.uncut_diamond) { cutGem(GemType.DIAMOND) }
        onOpHeldU(craft_objs.chisel, craft_objs.uncut_dragonstone) { cutGem(GemType.DRAGONSTONE) }
    }

    private suspend fun ProtectedAccess.cutGem(gem: GemType) {
        if (player.craftingLvl < gem.levelReq) {
            mes("You need a Crafting level of ${gem.levelReq} to cut this gem.")
            return
        }

        if (!inv.contains(gem.uncut) || !inv.contains(craft_objs.chisel)) {
            mes("You need a chisel and an uncut gem to do this.")
            return
        }

        val count = countDialog("How many would you like to cut?")
        if (count == 0) {
            return
        }

        val startCoords = player.coords
        repeat(count) {
            if (player.coords != startCoords) {
                return
            }

            val removed = invDel(inv, gem.uncut, count = 1, strict = true)
            if (removed.failure) {
                return
            }

            // Animation for gem cutting (human_dragonstonecutting ID 885 works for all gems)
            anim(gemcutting_seqs.human_dragonstonecutting)
            delay(2)

            // Check for crushing (only semi-precious gems can be crushed)
            if (gem.canCrush && random.of(0, 100) < CRUSH_CHANCE) {
                // Gem is crushed - no XP given
                mes("You accidentally crush the ${gem.name.lowercase()}.")
            } else {
                // Success - cut gem and grant XP
                val xp = gem.xp * xpMods.get(player, stats.crafting)
                statAdvance(stats.crafting, xp)
                invAddOrDrop(objRepo, gem.cut)
                mes("You cut the ${gem.name.lowercase()}.")
            }
        }
    }

    private enum class GemType(
        val uncut: ObjType,
        val cut: ObjType,
        val levelReq: Int,
        val xp: Double,
        val canCrush: Boolean = false,
    ) {
        OPAL(
            uncut = craft_objs.uncut_opal,
            cut = craft_objs.opal,
            levelReq = 1,
            xp = 15.0,
            canCrush = true,
        ),
        JADE(
            uncut = craft_objs.uncut_jade,
            cut = craft_objs.jade,
            levelReq = 13,
            xp = 20.0,
            canCrush = true,
        ),
        RED_TOPAZ(
            uncut = craft_objs.uncut_red_topaz,
            cut = craft_objs.red_topaz,
            levelReq = 16,
            xp = 25.0,
            canCrush = true,
        ),
        SAPPHIRE(
            uncut = craft_objs.uncut_sapphire,
            cut = craft_objs.sapphire,
            levelReq = 20,
            xp = 50.0,
            canCrush = false,
        ),
        EMERALD(
            uncut = craft_objs.uncut_emerald,
            cut = craft_objs.emerald,
            levelReq = 27,
            xp = 67.5,
            canCrush = false,
        ),
        RUBY(
            uncut = craft_objs.uncut_ruby,
            cut = craft_objs.ruby,
            levelReq = 34,
            xp = 85.0,
            canCrush = false,
        ),
        DIAMOND(
            uncut = craft_objs.uncut_diamond,
            cut = craft_objs.diamond,
            levelReq = 43,
            xp = 107.5,
            canCrush = false,
        ),
        DRAGONSTONE(
            uncut = craft_objs.uncut_dragonstone,
            cut = craft_objs.dragonstone,
            levelReq = 55,
            xp = 137.5,
            canCrush = false,
        ),
    }

    companion object {
        /** Chance to crush semi-precious gems (opal, jade, red topaz) when cutting. */
        private const val CRUSH_CHANCE = 10 // ~10% chance based on OSRS mechanics
    }
}

private typealias gemcutting_seqs = GemCuttingSeqs

object GemCuttingSeqs : SeqReferences() {
    val human_dragonstonecutting = find("human_dragonstonecutting")
}
