package org.rsmod.content.skills.firemaking.scripts

import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.type.refs.seq.SeqReferences

internal object FiremakingSeqs : SeqReferences() {
    val human_firemaking = find("human_firecooking")
}

internal object FiremakingObjs : ObjReferences() {
    val arctic_pine_logs = find("arctic_pine_log")
}
