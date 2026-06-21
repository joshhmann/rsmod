package org.rsmod.content.other.npcdrops

import org.rsmod.api.config.refs.objs
import org.rsmod.api.drop.table.NpcDropTableRegistry
import org.rsmod.api.drop.table.dropTable
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.game.type.npc.NpcType

/**
 * Drop table registrations for Dark Wizard.
 *
 * Data sources:
 * - OSRS wiki / corpus drops_by_source.json (rev 233)
 * - Existing inline handler preserved and enriched
 *
 * Enriched from corpus June 2026: added black robe, air/fire runes,
 * blood/cosmic runes, expanded coin entries, water/fire talisman,
 * expanded rune quantities matching corpus rates.
 *
 * Skipped: staff, wizard hat (not in rev 233 cache),
 * looting bag (wilderness-only), clue scrolls (post-2013).
 */
internal object DarkWizardDropTables {
    fun registerAll(registry: NpcDropTableRegistry) {
        registerDarkWizard(registry)
    }

    private fun registerDarkWizard(registry: NpcDropTableRegistry) {
        val table = dropTable {
            always(objs.bones)

            // Robes / Equipment
            table("Equipment", weight = 1) {
                item(DarkWizardObjs.black_robe, weight = 3)
            }

            // Runes
            table("Runes", weight = 1) {
                item(objs.waterrune, quantity = 5..15, weight = 10)
                item(objs.bodyrune, quantity = 5..15, weight = 10)
                item(objs.mindrune, quantity = 5..15, weight = 10)
                item(objs.earthrune, quantity = 5..15, weight = 10)
                item(objs.chaosrune, quantity = 2..5, weight = 6)
                item(objs.naturerune, quantity = 2..5, weight = 4)
                item(objs.lawrune, quantity = 2..3, weight = 2)
                item(objs.bloodrune, quantity = 2, weight = 2)
                item(DarkWizardObjs.cosmicrune, quantity = 2, weight = 1)
                item(objs.airrune, quantity = 10, weight = 3)
                item(objs.airrune, quantity = 18, weight = 2)
                item(objs.firerune, quantity = 10, weight = 3)
                item(objs.firerune, quantity = 18, weight = 2)
                item(objs.waterrune, quantity = 10, weight = 3)
                item(objs.waterrune, quantity = 18, weight = 2)
                item(objs.earthrune, quantity = 10, weight = 3)
                item(objs.earthrune, quantity = 18, weight = 2)
                item(objs.mindrune, quantity = 10, weight = 3)
                item(objs.mindrune, quantity = 18, weight = 2)
                item(objs.bodyrune, quantity = 10, weight = 3)
                item(objs.bodyrune, quantity = 18, weight = 2)
                item(objs.chaosrune, quantity = 5, weight = 6)
                item(objs.naturerune, quantity = 4, weight = 7)
            }

            // Talismans
            table("Talismans", weight = 1) {
                item(DarkWizardObjs.water_talisman, weight = 2)
                item(DarkWizardObjs.fire_talisman, weight = 2)
            }

            // Coins
            table("Coins", weight = 1) {
                item(objs.coins, quantity = 1, weight = 17)
                item(objs.coins, quantity = 2, weight = 16)
                item(objs.coins, quantity = 4, weight = 7)
                item(objs.coins, quantity = 29, weight = 3)
                item(objs.coins, quantity = 30, weight = 1)
            }
        }

        val darkWizardNpcs: List<NpcType> =
            listOf(
                DarkWizardNpcs.bearded_dark_wizard,
                DarkWizardNpcs.young_dark_wizard,
            )
        registry.register(darkWizardNpcs.distinct(), table)
    }
}

internal object DarkWizardNpcs : NpcReferences() {
    val bearded_dark_wizard = find("bearded_dark_wizard")
    val young_dark_wizard = find("young_dark_wizard")
}

internal object DarkWizardObjs : ObjReferences() {
    val black_robe = find("black_robe")
    val cosmicrune = find("cosmicrune")
    val water_talisman = find("water_talisman")
    val fire_talisman = find("fire_talisman")
}
