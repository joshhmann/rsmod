@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.mechanics.aggression.configs

import org.rsmod.api.config.refs.varns
import org.rsmod.api.config.refs.varps
import org.rsmod.api.type.builders.hunt.HuntModeBuilder
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.hunt.HuntModeReferences
import org.rsmod.api.type.refs.npc.NpcReferences
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.type.hunt.HuntCheckNotTooStrong
import org.rsmod.game.type.hunt.HuntType
import org.rsmod.game.type.hunt.HuntVis
import org.rsmod.game.type.npc.NpcType

typealias aggression_npcs = AggressionNpcs

typealias aggression_hunt = AggressionHuntModes

object AggressionNpcs : NpcReferences() {
    // Lumbridge + Edgeville goblin sets.
    val goblin_armed = find("goblin_armed")
    val goblin_unarmed_melee_1 = find("goblin_unarmed_melee_1")
    val goblin_unarmed_melee_2 = find("goblin_unarmed_melee_2")
    val goblin_unarmed_melee_3 = find("goblin_unarmed_melee_3")
    val goblin_unarmed_melee_4 = find("goblin_unarmed_melee_4")
    val goblin_unarmed_melee_5 = find("goblin_unarmed_melee_5")
    val goblin_unarmed_melee_6 = find("goblin_unarmed_melee_6")
    val goblin_unarmed_melee_7 = find("goblin_unarmed_melee_7")
    val goblin_unarmed_melee_8 = find("goblin_unarmed_melee_8")

    // Common F2P aggressive NPCs.
    val guard1 = find("guard1")
    val bearded_dark_wizard = find("bearded_dark_wizard")
    val young_dark_wizard = find("young_dark_wizard")
    val black_knight = find("black_knight")
    val aggressive_black_knight = find("aggressive_black_knight")
}

object AggressionHuntModes : HuntModeReferences() {
    val f2p_aggressive_melee = find("f2p_aggressive_melee")
}

object AggressionHuntBuilder : HuntModeBuilder() {
    init {
        // Matches aggressive melee behavior, but respects "2x combat level" aggro exemption.
        build("f2p_aggressive_melee") {
            type = HuntType.Player
            checkVis = HuntVis.LineOfSight
            checkNotTooStrong = HuntCheckNotTooStrong.OutsideWilderness
            checkNotCombat = varps.lastcombat
            checkNotCombatSelf = varns.lastcombat
            checkNotBusy = false
            findKeepHunting = false
            findNewMode = NpcMode.OpPlayer2
        }
    }
}

object AggressionNpcEditor : NpcEditor() {
    init {
        val goblins =
            setOf(
                aggression_npcs.goblin_armed,
                aggression_npcs.goblin_unarmed_melee_1,
                aggression_npcs.goblin_unarmed_melee_2,
                aggression_npcs.goblin_unarmed_melee_3,
                aggression_npcs.goblin_unarmed_melee_4,
                aggression_npcs.goblin_unarmed_melee_5,
                aggression_npcs.goblin_unarmed_melee_6,
                aggression_npcs.goblin_unarmed_melee_7,
                aggression_npcs.goblin_unarmed_melee_8,
            )
        goblins.forEach(::aggressiveMelee)

        val f2pAggressiveHumanoids =
            setOf(
                aggression_npcs.guard1,
                aggression_npcs.bearded_dark_wizard,
                aggression_npcs.young_dark_wizard,
                aggression_npcs.black_knight,
                aggression_npcs.aggressive_black_knight,
            )
        f2pAggressiveHumanoids.forEach(::aggressiveMelee)
    }

    private fun aggressiveMelee(type: NpcType) {
        edit(type) {
            huntMode = aggression_hunt.f2p_aggressive_melee
            huntRange = 5
        }
    }
}
