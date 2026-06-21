package org.rsmod.content.skills.magic.noncombat

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.magic.MagicSpell
import org.rsmod.api.combat.manager.MagicRuneManager
import org.rsmod.api.config.refs.components
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.script.onIfModalButton
import org.rsmod.api.script.onIfModalButtonT
import org.rsmod.api.spells.MagicSpellRegistry
import org.rsmod.content.skills.magic.noncombat.configs.NonCombatSpellObjs as spellObjs
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class EnchantSpells
@Inject
constructor(
    private val runes: MagicRuneManager,
    private val spellRegistry: MagicSpellRegistry,
) : PluginScript() {

    private val enchantLvl1: MagicSpell? by lazy { spellRegistry.getObjSpell(spellObjs.spell_enchant_lvl1) }
    private val enchantLvl2: MagicSpell? by lazy { spellRegistry.getObjSpell(spellObjs.spell_enchant_lvl2) }

    override fun ScriptContext.startup() {
        val lvl1 = enchantLvl1 ?: return@startup
        val lvl2 = enchantLvl2 ?: return@startup

        onIfModalButton(lvl1.component) { /* select spell */ }
        onIfModalButtonT(lvl1.component, components.inventory_items) { _ ->
            /* TODO: Enchant Lvl-1 implementation.
             * Requires enchantment result items (ring_of_recoil, games_necklace, etc.)
             * to be added to BaseObjs first. See NEI audit gap. */
        }

        onIfModalButton(lvl2.component) { /* select spell */ }
        onIfModalButtonT(lvl2.component, components.inventory_items) { _ ->
            /* TODO: Enchant Lvl-2 implementation. */
        }
    }
}
