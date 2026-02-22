@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.cooksassistant.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias cooks_assistant_objs = CooksAssistantObjs

internal object CooksAssistantObjs : ObjReferences() {
    val bucket_milk = find("bucket_milk")
    val pot_flour = find("pot_flour")
    val egg = find("egg")
}
