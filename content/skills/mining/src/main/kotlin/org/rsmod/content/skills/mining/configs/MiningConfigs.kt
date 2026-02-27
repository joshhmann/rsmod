package org.rsmod.content.skills.mining.configs

import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.params
import org.rsmod.api.type.editors.obj.ObjEditor
import org.rsmod.api.type.refs.obj.ObjReferences
import org.rsmod.api.type.refs.seq.SeqReferences

// Mining animation sequences — not in BaseSeqs, so declared locally here.
internal object MiningSeqs : SeqReferences() {
    val human_mining_bronze_pickaxe = find("human_mining_bronze_pickaxe")
    val human_mining_iron_pickaxe = find("human_mining_iron_pickaxe")
    val human_mining_steel_pickaxe = find("human_mining_steel_pickaxe")
    val human_mining_black_pickaxe = find("human_mining_black_pickaxe")
    val human_mining_mithril_pickaxe = find("human_mining_mithril_pickaxe")
    val human_mining_adamant_pickaxe = find("human_mining_adamant_pickaxe")
    val human_mining_rune_pickaxe = find("human_mining_rune_pickaxe")
    val human_mining_gilded_pickaxe = find("human_mining_gilded_pickaxe")
    val human_mining_dragon_pickaxe = find("human_mining_dragon_pickaxe")
    val human_mining_dragon_pickaxe_pretty = find("human_mining_dragon_pickaxe_pretty")
    val human_mining_3a_pickaxe = find("human_mining_3a_pickaxe")
    val human_mining_infernal_pickaxe = find("human_mining_infernal_pickaxe")
    val human_mining_zalcano_pickaxe = find("human_mining_zalcano_pickaxe")
    val human_mining_crystal_pickaxe = find("human_mining_crystal_pickaxe")
    val human_mining_trailblazer_pickaxe_no_infernal =
        find("human_mining_trailblazer_pickaxe_no_infernal")
    val human_mining_trailblazer_pickaxe = find("human_mining_trailblazer_pickaxe")
}

/**
 * Pickaxe obj types not yet present in [objs] (BaseObjs).
 *
 * Cache sym names are sourced from obj.sym.
 */
internal object MiningPickaxeObjs : ObjReferences() {
    val bronze_pickaxe = find("bronze_pickaxe")
    val iron_pickaxe = find("iron_pickaxe")
    val steel_pickaxe = find("steel_pickaxe")
    val black_pickaxe = find("black_pickaxe")
    val mithril_pickaxe = find("mithril_pickaxe")
    val adamant_pickaxe = find("adamant_pickaxe")
    val rune_pickaxe = find("rune_pickaxe")
    val gilded_pickaxe = find("trail_gilded_pickaxe")
    val _3a_pickaxe = find("3a_pickaxe")
    val zalcano_pickaxe = find("zalcano_pickaxe")
    val trailblazer_pickaxe_no_infernal = find("trailblazer_pickaxe_no_infernal")
    val trailblazer_pickaxe = find("trailblazer_pickaxe")
}

/**
 * Assigns each pickaxe to the [content.mining_pickaxe] content group and sets:
 * - [params.levelrequire] — Mining level needed to use this pickaxe.
 * - [params.skill_anim] — The player animation played while mining.
 *
 * Level requirements (wiki-accurate): bronze 1, iron 1, steel 6, black 11, mithril 21, adamant 31,
 * rune 41, gilded 41, dragon 61, dragon(up) 61, 3a 61, infernal 61, zalcano 61, trailblazer 61,
 * crystal 71.
 */
internal object MiningPickaxes : ObjEditor() {
    init {
        edit(MiningPickaxeObjs.bronze_pickaxe) {
            contentGroup = content.mining_pickaxe
            param[params.levelrequire] = 1
            param[params.skill_anim] = MiningSeqs.human_mining_bronze_pickaxe
        }

        edit(MiningPickaxeObjs.iron_pickaxe) {
            contentGroup = content.mining_pickaxe
            param[params.levelrequire] = 1
            param[params.skill_anim] = MiningSeqs.human_mining_iron_pickaxe
        }

        edit(MiningPickaxeObjs.steel_pickaxe) {
            contentGroup = content.mining_pickaxe
            param[params.levelrequire] = 6
            param[params.skill_anim] = MiningSeqs.human_mining_steel_pickaxe
        }

        edit(MiningPickaxeObjs.black_pickaxe) {
            contentGroup = content.mining_pickaxe
            param[params.levelrequire] = 11
            param[params.skill_anim] = MiningSeqs.human_mining_black_pickaxe
        }

        edit(MiningPickaxeObjs.mithril_pickaxe) {
            contentGroup = content.mining_pickaxe
            param[params.levelrequire] = 21
            param[params.skill_anim] = MiningSeqs.human_mining_mithril_pickaxe
        }

        edit(MiningPickaxeObjs.adamant_pickaxe) {
            contentGroup = content.mining_pickaxe
            param[params.levelrequire] = 31
            param[params.skill_anim] = MiningSeqs.human_mining_adamant_pickaxe
        }

        edit(MiningPickaxeObjs.rune_pickaxe) {
            contentGroup = content.mining_pickaxe
            param[params.levelrequire] = 41
            param[params.skill_anim] = MiningSeqs.human_mining_rune_pickaxe
        }

        edit(MiningPickaxeObjs.gilded_pickaxe) {
            contentGroup = content.mining_pickaxe
            param[params.levelrequire] = 41
            param[params.skill_anim] = MiningSeqs.human_mining_gilded_pickaxe
        }

        edit(MiningPickaxeObjs._3a_pickaxe) {
            contentGroup = content.mining_pickaxe
            param[params.levelrequire] = 61
            param[params.skill_anim] = MiningSeqs.human_mining_3a_pickaxe
        }

        edit(objs.infernal_pickaxe) {
            contentGroup = content.mining_pickaxe
            param[params.levelrequire] = 61
            param[params.skill_anim] = MiningSeqs.human_mining_infernal_pickaxe
        }

        edit(MiningPickaxeObjs.zalcano_pickaxe) {
            contentGroup = content.mining_pickaxe
            param[params.levelrequire] = 61
            param[params.skill_anim] = MiningSeqs.human_mining_zalcano_pickaxe
        }

        edit(MiningPickaxeObjs.trailblazer_pickaxe_no_infernal) {
            contentGroup = content.mining_pickaxe
            param[params.levelrequire] = 61
            param[params.skill_anim] = MiningSeqs.human_mining_trailblazer_pickaxe_no_infernal
        }

        edit(MiningPickaxeObjs.trailblazer_pickaxe) {
            contentGroup = content.mining_pickaxe
            param[params.levelrequire] = 61
            param[params.skill_anim] = MiningSeqs.human_mining_trailblazer_pickaxe
        }

        edit(objs.crystal_pickaxe) {
            contentGroup = content.mining_pickaxe
            param[params.levelrequire] = 71
            param[params.skill_anim] = MiningSeqs.human_mining_crystal_pickaxe
        }
    }
}
