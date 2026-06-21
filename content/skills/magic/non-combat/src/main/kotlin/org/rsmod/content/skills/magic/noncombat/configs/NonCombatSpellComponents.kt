package org.rsmod.content.skills.magic.noncombat.configs

import org.rsmod.api.type.refs.comp.ComponentReferences

internal typealias StandardSpells = NonCombatSpellComponents

internal object NonCombatSpellComponents : ComponentReferences() {
    val low_alchemy = find("magic_spellbook:low_alchemy")
    val high_alchemy = find("magic_spellbook:high_alchemy")
    val superheat = find("magic_spellbook:superheat")
}
