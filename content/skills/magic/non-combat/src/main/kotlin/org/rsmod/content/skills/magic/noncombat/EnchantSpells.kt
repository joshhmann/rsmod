package org.rsmod.content.skills.magic.noncombat

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.magic.MagicSpell
import org.rsmod.api.combat.manager.MagicRuneManager
import org.rsmod.api.combat.manager.MagicRuneManager.Companion.isFailure
import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.statAdvance
import org.rsmod.api.player.ui.IfModalButtonT
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onIfModalButton
import org.rsmod.api.script.onIfModalButtonT
import org.rsmod.api.spells.MagicSpellRegistry
import org.rsmod.content.skills.magic.noncombat.configs.NonCombatSpellObjs as spellObjs
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class EnchantSpells
@Inject
constructor(
    private val runes: MagicRuneManager,
    private val spellRegistry: MagicSpellRegistry,
    private val objTypes: ObjTypeList,
    private val objRepo: ObjRepository,
) : PluginScript() {

    private val enchantLvl1: MagicSpell? by lazy { spellRegistry.getObjSpell(spellObjs.spell_enchant_lvl1) }
    private val enchantLvl2: MagicSpell? by lazy { spellRegistry.getObjSpell(spellObjs.spell_enchant_lvl2) }

    override fun ScriptContext.startup() {
        val lvl1 = enchantLvl1 ?: return@startup
        val lvl2 = enchantLvl2 ?: return@startup

        onIfModalButton(lvl1.component) { /* select spell */ }
        onIfModalButtonT(lvl1.component, components.inventory_items) {
            castEnchant(lvl1)
        }

        onIfModalButton(lvl2.component) { /* select spell */ }
        onIfModalButtonT(lvl2.component, components.inventory_items) {
            castEnchant(lvl2)
        }
    }

    private suspend fun ProtectedAccess.castEnchant(spell: MagicSpell) {
        val event = IfModalButtonT
        val targetId = event.targetObj?.id ?: return

        val (enchantedId, xp) = lookupEnchantment(spell, targetId) ?: run {
            mes("You cannot enchant that item.")
            return
        }

        val result = runes.attemptCast(player, spell)
        if (result.isFailure()) return

        if (!invDel(inv, targetId, count = 1).success) return
        invAddOrDrop(objRepo, enchantedId, count = 1)

        anim(seqs.human_castteleport)
        val itemName = objTypes[enchantedId].lowercaseName
        mes("You enchant the item into a $itemName.")
        statAdvance(stats.magic, xp)
    }

    private fun lookupEnchantment(spell: MagicSpell, itemId: Int): Pair<ObjType, Double>? {
        return when {
            isLvl1Enchant(spell) -> lvl1Enchantments[itemId]
            isLvl2Enchant(spell) -> lvl2Enchantments[itemId]
            else -> null
        }
    }

    private fun isLvl1Enchant(spell: MagicSpell): Boolean =
        spell.obj.isType(spellObjs.spell_enchant_lvl1)

    private fun isLvl2Enchant(spell: MagicSpell): Boolean =
        spell.obj.isType(spellObjs.spell_enchant_lvl2)

    private val lvl1Enchantments: Map<Int, Pair<ObjType, Double>> = mapOf(
        objs.sapphire_ring.id to (objs.ring_of_recoil to 17.5),
        objs.sapphire_amulet.id to (objs.amulet_of_magic to 17.5),
        objs.sapphire_necklace.id to (objs.games_necklace to 17.5),
        objs.sapphire_bracelet.id to (objs.bracelet_of_clay to 17.5),
        objs.opal_ring.id to (objs.ring_of_pursuit to 17.5),
        objs.opal_necklace.id to (objs.necklace_of_passage to 17.5),
        objs.opal_amulet.id to (objs.amulet_of_bounty to 17.5),
        objs.opal_bracelet.id to (objs.bracelet_of_clay to 17.5),
    )

    private val lvl2Enchantments: Map<Int, Pair<ObjType, Double>> = mapOf(
        objs.emerald_ring.id to (objs.ring_of_dueling to 37.0),
        objs.emerald_amulet.id to (objs.amulet_of_defence to 37.0),
        objs.emerald_necklace.id to (objs.binding_necklace to 37.0),
        objs.emerald_bracelet.id to (objs.bracelet_of_clay to 37.0),
        objs.jade_ring.id to (objs.ring_of_pursuit to 37.0),
        objs.jade_necklace.id to (objs.necklace_of_passage to 37.0),
        objs.jade_amulet.id to (objs.amulet_of_bounty to 37.0),
        objs.jade_bracelet.id to (objs.bracelet_of_clay to 37.0),
        objs.topaz_ring.id to (objs.ring_of_pursuit to 37.0),
        objs.topaz_necklace.id to (objs.necklace_of_passage to 37.0),
        objs.topaz_amulet.id to (objs.amulet_of_bounty to 37.0),
        objs.topaz_bracelet.id to (objs.bracelet_of_clay to 37.0),
    )
}
