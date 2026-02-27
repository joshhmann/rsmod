@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.goblindiplomacy.configs

import org.rsmod.api.type.refs.obj.ObjReferences

typealias goblin_diplomacy_objs = GoblinDiplomacyObjs

object GoblinDiplomacyObjs : ObjReferences() {
    // Goblin mail variants
    val goblin_armour = find("goblin_armour") // Brown goblin mail
    val goblin_armour_orange = find("goblin_armour_orange")
    val goblin_armour_darkblue = find("goblin_armour_darkblue")

    // Dyes
    val reddye = find("reddye")
    val yellowdye = find("yellowdye")
    val bluedye = find("bluedye")
    val orangedye = find("orangedye")

    // Dye ingredients
    val woadleaf = find("woadleaf")
    val redberries = find("redberries")
    val onion = find("onion")

    // Reward
    val gold_bar = find("gold_bar")
}
