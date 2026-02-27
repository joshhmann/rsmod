package org.rsmod.content.interfaces.bank.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.type.editors.npc.NpcEditor
import org.rsmod.api.type.refs.npc.NpcReferences

internal object BankNpcs : NpcReferences() {
    val clan_hall_banker_0 = find("clan_hall_banker_0")
    val clan_hall_banker_1 = find("clan_hall_banker_1")
    val clan_hall_banker_2 = find("clan_hall_banker_2")
    val clan_hall_banker_3 = find("clan_hall_banker_3")
}

internal object BankNpcEditor : NpcEditor() {
    init {
        edit(BankNpcs.clan_hall_banker_0) { contentGroup = content.banker }
        edit(BankNpcs.clan_hall_banker_1) { contentGroup = content.banker }
        edit(BankNpcs.clan_hall_banker_2) { contentGroup = content.banker }
        edit(BankNpcs.clan_hall_banker_3) { contentGroup = content.banker }
    }
}
