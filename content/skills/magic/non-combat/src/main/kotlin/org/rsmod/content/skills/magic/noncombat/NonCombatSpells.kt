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
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onIfModalButton
import org.rsmod.api.script.onIfModalButtonT
import org.rsmod.api.spells.MagicSpellRegistry
import org.rsmod.content.skills.magic.noncombat.configs.NonCombatSpellComponents as spellComponents
import org.rsmod.content.skills.magic.noncombat.configs.NonCombatSpellObjs as spellObjs
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class NonCombatSpells
@Inject
constructor(
    private val runes: MagicRuneManager,
    private val spellRegistry: MagicSpellRegistry,
    private val objTypes: ObjTypeList,
    private val objRepo: ObjRepository,
) : PluginScript() {

    private val varrockTeleport: MagicSpell? by lazy { spellRegistry.getObjSpell(spellObjs.spell_varrock_teleport) }
    private val lumbridgeTeleport: MagicSpell? by lazy { spellRegistry.getObjSpell(spellObjs.spell_lumbridge_teleport) }
    private val faladorTeleport: MagicSpell? by lazy { spellRegistry.getObjSpell(spellObjs.spell_falador_teleport) }
    private val lowAlchemy: MagicSpell? by lazy { spellRegistry.getObjSpell(spellObjs.spell_low_alchemy) }
    private val highAlchemy: MagicSpell? by lazy { spellRegistry.getObjSpell(spellObjs.spell_high_alchemy) }
    private val superheat: MagicSpell? by lazy { spellRegistry.getObjSpell(spellObjs.spell_superheat) }

    override fun ScriptContext.startup() {
        /* ------------ Teleports ------------ */
        onIfModalButton(components.magic_spellbook_teleport_home_standard) {
            castTeleport(null, HOME_LUMBRIDGE, homeTeleport = true)
        }
        onIfModalButton(components.magic_spellbook_varrock_teleport) {
            castTeleport(varrockTeleport ?: return@onIfModalButton, VARROCK)
        }
        onIfModalButton(components.magic_spellbook_lumbridge_teleport) {
            castTeleport(lumbridgeTeleport ?: return@onIfModalButton, LUMBRIDGE)
        }
        onIfModalButton(components.magic_spellbook_falador_teleport) {
            castTeleport(faladorTeleport ?: return@onIfModalButton, FALADOR)
        }

        /* ------------ Alchemy ------------ */
        onIfModalButton(components.magic_spellbook_low_alchemy) { /* select spell */ }
        onIfModalButtonT(spellComponents.low_alchemy, components.inventory_items) { event ->
            val targetId = event.targetObj?.id ?: return@onIfModalButtonT
            castAlchemy(lowAlchemy ?: return@onIfModalButtonT, AlchemyType.Low, targetId)
        }

        onIfModalButton(components.magic_spellbook_high_alchemy) { /* select spell */ }
        onIfModalButtonT(spellComponents.high_alchemy, components.inventory_items) { event ->
            val targetId = event.targetObj?.id ?: return@onIfModalButtonT
            castAlchemy(highAlchemy ?: return@onIfModalButtonT, AlchemyType.High, targetId)
        }

        /* ------------ Superheat ------------ */
        onIfModalButton(components.magic_spellbook_superheat) { /* select spell */ }
        onIfModalButtonT(spellComponents.superheat, components.inventory_items) { event ->
            val targetId = event.targetObj?.id ?: return@onIfModalButtonT
            castSuperheat(superheat ?: return@onIfModalButtonT, targetId)
        }
    }

    private suspend fun ProtectedAccess.castTeleport(spell: MagicSpell?, dest: CoordGrid, homeTeleport: Boolean = false) {
        if (spell != null) {
            val result = runes.attemptCast(player, spell)
            if (result.isFailure()) return
        }
        anim(seqs.human_castteleport)
        delay(if (homeTeleport) 17 else 2)
        teleport(dest)
        if (spell != null) {
            statAdvance(stats.magic, spell.castXp)
        }
    }

    private suspend fun ProtectedAccess.castAlchemy(spell: MagicSpell, type: AlchemyType, targetId: Int) {
        val result = runes.attemptCast(player, spell)
        if (result.isFailure()) return

        val itemType = objTypes[targetId]
        val storePrice = itemType?.cost ?: return
        val alchValue = when (type) {
            AlchemyType.Low -> (storePrice * 0.4).toInt().coerceAtLeast(1)
            AlchemyType.High -> (storePrice * 0.6).toInt().coerceAtLeast(1)
        }

        invDel(inv, objTypes[targetId]!!, count = 1)
        invAddOrDrop(objRepo, objs.coins, count = alchValue)

        anim(seqs.human_castteleport)
        val name = if (type == AlchemyType.Low) "Low Level Alchemy" else "High Level Alchemy"
        mes("$name -- You receive $alchValue coins.")
        statAdvance(stats.magic, spell.castXp)
    }

    private suspend fun ProtectedAccess.castSuperheat(spell: MagicSpell, targetId: Int) {
        val result = runes.attemptCast(player, spell)
        if (result.isFailure()) return

        val barId = oreToBar(targetId) ?: run {
            mes("You can only cast Superheat on appropriate ores.")
            return
        }

        invDel(inv, objTypes[targetId]!!, count = 1)
        invAddOrDrop(objRepo, barId, count = 1)

        anim(seqs.human_castteleport)
        val barName = objTypes[barId].lowercaseName
        mes("You smelt the ore into a $barName.")
        statAdvance(stats.magic, spell.castXp)
    }

    private fun oreToBar(oreId: Int): ObjType? = when (oreId) {
        objs.copper_ore.id, objs.tin_ore.id -> objs.bronze_bar
        objs.iron_ore.id -> objs.iron_bar
        objs.silver_ore.id -> objs.silver_bar
        objs.gold_ore.id -> objs.gold_bar
        objs.mithril_ore.id -> objs.mithril_bar
        else -> null
    }

    private enum class AlchemyType { Low, High }

    private companion object {
        private val VARROCK = CoordGrid(3212, 3424)
        private val LUMBRIDGE = CoordGrid(3222, 3219)
        private val FALADOR = CoordGrid(2964, 3378)
        private val HOME_LUMBRIDGE = CoordGrid(3222, 3218)
    }
}
