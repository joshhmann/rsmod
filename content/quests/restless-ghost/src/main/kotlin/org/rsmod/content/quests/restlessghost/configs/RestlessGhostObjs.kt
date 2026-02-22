@file:Suppress("unused", "SpellCheckingInspection")

package org.rsmod.content.quests.restlessghost.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias restless_ghost_objs = RestlessGhostObjs

/**
 * Item type references for The Restless Ghost quest. Items not present in
 * [org.rsmod.api.config.refs.BaseObjs] are declared here.
 */
internal object RestlessGhostObjs : ObjReferences() {
    val ghostskull = find("ghostskull")
    val amulet_of_ghostspeak = find("amulet_of_ghostspeak")
}
