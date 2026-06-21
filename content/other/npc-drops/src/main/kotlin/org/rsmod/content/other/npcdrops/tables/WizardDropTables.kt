package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.npc.NpcType

/**
 * Drop table registrations for Wizard (regular, non-dark).
 *
 * Data sources:
 * - OSRS wiki / corpus drops_by_source.json (rev 233)
 * - Uses same workflow as Lumbridge NPC families
 *
 * Created for Draynor second-zone validation.
 * Covers wizards near Draynor Manor and Wizards' Tower.
 *
 * Skipped: Staff, Blue wizard robe, Blue wizard hat (not in rev 233 cache
 * under those names — cache has wizards_robe, bluewizhat but resolver
 * doesn't map), Key (medium, post-2013).
 */
internal object WizardDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerWizard(registry)
    }

    private fun registerWizard(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.bones)

            // Runes
            table("Runes", weight = 1) {
                item(objs.airrune, quantity = 5, weight = 3)
                item(objs.airrune, quantity = 12, weight = 2)
                item(objs.bodyrune, quantity = 5, weight = 3)
                item(objs.bodyrune, quantity = 12, weight = 2)
                item(objs.chaosrune, quantity = 2, weight = 8)
                item(objs.earthrune, quantity = 5, weight = 3)
                item(objs.earthrune, quantity = 12, weight = 2)
                item(objs.firerune, quantity = 5, weight = 3)
                item(objs.firerune, quantity = 12, weight = 2)
                item(objs.mindrune, quantity = 5, weight = 3)
                item(objs.mindrune, quantity = 12, weight = 2)
                item(objs.naturerune, quantity = 2, weight = 8)
                item(objs.waterrune, quantity = 5, weight = 3)
                item(objs.waterrune, quantity = 12, weight = 2)
                item(objs.bloodrune, quantity = 2, weight = 1)
                item(objs.lawrune, quantity = 2, weight = 1)
            }

            // Talismans
            table("Talismans", weight = 1) {
                item(WizardObjs.mind_talisman, weight = 4)
                item(WizardObjs.water_talisman, weight = 3)
            }

            // Coins
            table("Coins", weight = 1) {
                item(objs.coins, quantity = 1, weight = 23)
                item(objs.coins, quantity = 2, weight = 9)
                item(objs.coins, quantity = 18, weight = 7)
                item(objs.coins, quantity = 30, weight = 1)
            }
        }

        val wizardNpcs: List<NpcType> =
            listOf(
                WizardNpcs.wizard,
                WizardNpcs.air_wizard,
                WizardNpcs.water_wizard,
                WizardNpcs.earth_wizard,
                WizardNpcs.fire_wizard,
            )
        registry.register(wizardNpcs.distinct(), table)
    }
}

internal object WizardNpcs : NpcReferences() {
    val wizard = find("wizard")
    val air_wizard = find("air_wizard")
    val water_wizard = find("water_wizard")
    val earth_wizard = find("earth_wizard")
    val fire_wizard = find("fire_wizard")
}

internal object WizardObjs : ObjReferences() {
    val mind_talisman = find("mind_talisman")
    val water_talisman = find("water_talisman")
}
