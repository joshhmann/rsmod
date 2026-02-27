package org.rsmod.content.skills.herblore.scripts

// IMPLEMENTATION NOTES:
// Herblore is fully inventory-based (no loc interaction needed).
// Three interaction types:
//
// 1. CLEAN HERB: click grimy herb (onOpObj1) → clean herb + XP (no tool needed)
//
// 2. CLEAN HERB + VIAL OF WATER → unfinished potion
//    onOpHeldU(clean_herb, vial_of_water) — 1 tick, produces xxx_potion_unf
//
// 3. SECONDARY INGREDIENT + UNFINISHED POTION → finished potion (dose 3)
//    onOpHeldU(secondary, unf_potion) — 1 tick, XP awarded here
//
// Animations:
//   seqs.human_herblore — added to BaseSeqs.kt alongside this module.
//
// TODO:
//   - 4-dose potions (more common for mixing, 3-dose for drops)
//   - Decanting potions
//   - Potion of zamorak (wine_of_zamorak secondary — needs grimy_ranarr)

import jakarta.inject.Inject
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.herbloreLvl
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpHeldU
import org.rsmod.api.script.onOpObj1
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.content.skills.herblore.configs.HerbloreObjs as herbObjs
import org.rsmod.content.skills.herblore.configs.HerbloreSeqs as herbSeqs
import org.rsmod.game.type.obj.ObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

private data class HerbDef(
    val grimy: ObjType,
    val clean: ObjType,
    val levelReq: Int,
    val cleanXp: Double,
)

private data class PotionDef(
    val unfPotion: ObjType,
    val secondary: ObjType,
    val product: ObjType,
    val levelReq: Int,
    val mixXp: Double,
)

class Herblore
@Inject
constructor(private val xpMods: XpModifiers, private val objRepo: ObjRepository) : PluginScript() {

    override fun ScriptContext.startup() {
        // ---- Cleaning grimy herbs (click on herb directly) ----
        for (def in HERB_DEFS) {
            onOpObj1(def.grimy) { cleanHerb(def) }
        }

        // ---- Clean herb + vial of water → unfinished potion ----
        for (def in HERB_DEFS) {
            val unfPotion = HERB_TO_UNF[def.clean] ?: continue
            onOpHeldU(def.clean, herbObjs.vial_of_water) { makeUnfPotion(def, unfPotion) }
        }

        // ---- Secondary + unfinished potion → finished potion ----
        for (def in POTION_DEFS) {
            onOpHeldU(def.secondary, def.unfPotion) { mixPotion(def) }
        }
    }

    // ---- Herblore helpers ----

    private fun ProtectedAccess.cleanHerb(def: HerbDef) {
        if (player.herbloreLvl < def.levelReq) {
            mes("You need a Herblore level of ${def.levelReq} to clean this herb.")
            return
        }
        val deleted = invDel(inv, def.grimy, count = 1, strict = true)
        if (deleted.failure) return
        anim(herbSeqs.human_herblore)
        val xp = def.cleanXp * xpMods.get(player, stats.herblore)
        statAdvance(stats.herblore, xp)
        invAddOrDrop(objRepo, def.clean)
    }

    private suspend fun ProtectedAccess.makeUnfPotion(def: HerbDef, unfPotion: ObjType) {
        if (player.herbloreLvl < def.levelReq) {
            mes("You need a Herblore level of ${def.levelReq} to make this potion.")
            return
        }
        if (!inv.contains(def.clean) || !inv.contains(herbObjs.vial_of_water)) {
            mes("You don't have the required ingredients.")
            return
        }
        val count = countDialog("How many would you like to make?")
        if (count == 0) {
            return
        }
        val startCoords = player.coords
        repeat(count) {
            if (player.coords != startCoords) {
                return
            }
            val removedHerb = invDel(inv, def.clean, count = 1, strict = true)
            val removedVial = invDel(inv, herbObjs.vial_of_water, count = 1, strict = true)
            if (removedHerb.failure || removedVial.failure) {
                mes("You don't have the required ingredients.")
                return
            }
            anim(herbSeqs.human_herblore)
            delay(1)
            // No XP for adding herb to vial — XP is given when secondary is added.
            invAddOrDrop(objRepo, unfPotion)
        }
    }

    private suspend fun ProtectedAccess.mixPotion(def: PotionDef) {
        if (player.herbloreLvl < def.levelReq) {
            mes("You need a Herblore level of ${def.levelReq} to mix this potion.")
            return
        }
        if (!inv.contains(def.secondary) || !inv.contains(def.unfPotion)) {
            mes("You don't have the required ingredients.")
            return
        }
        val count = countDialog("How many would you like to make?")
        if (count == 0) {
            return
        }
        val startCoords = player.coords
        repeat(count) {
            if (player.coords != startCoords) {
                return
            }
            val removedSecondary = invDel(inv, def.secondary, count = 1, strict = true)
            val removedUnf = invDel(inv, def.unfPotion, count = 1, strict = true)
            if (removedSecondary.failure || removedUnf.failure) {
                mes("You don't have the required ingredients.")
                return
            }
            anim(herbSeqs.human_herblore)
            delay(1)
            val xp = def.mixXp * xpMods.get(player, stats.herblore)
            statAdvance(stats.herblore, xp)
            invAddOrDrop(objRepo, def.product)
            mes("You mix the ingredients into a potion.")
        }
    }

    companion object {
        // Wiki-accurate cleaning XP and level requirements
        private val HERB_DEFS: List<HerbDef> =
            listOf(
                HerbDef(herbObjs.grimy_guam, herbObjs.clean_guam, levelReq = 3, cleanXp = 2.5),
                HerbDef(
                    herbObjs.grimy_marrentill,
                    herbObjs.clean_marrentill,
                    levelReq = 5,
                    cleanXp = 3.8,
                ),
                HerbDef(
                    herbObjs.grimy_tarromin,
                    herbObjs.clean_tarromin,
                    levelReq = 11,
                    cleanXp = 5.0,
                ),
                HerbDef(
                    herbObjs.grimy_harralander,
                    herbObjs.clean_harralander,
                    levelReq = 20,
                    cleanXp = 6.3,
                ),
                HerbDef(
                    herbObjs.grimy_ranarr_weed,
                    herbObjs.clean_ranarr,
                    levelReq = 25,
                    cleanXp = 7.5,
                ),
                HerbDef(
                    herbObjs.grimy_toadflax,
                    herbObjs.clean_toadflax,
                    levelReq = 30,
                    cleanXp = 8.0,
                ),
                HerbDef(
                    herbObjs.grimy_irit_leaf,
                    herbObjs.clean_irit,
                    levelReq = 40,
                    cleanXp = 8.8,
                ),
                HerbDef(
                    herbObjs.grimy_avantoe,
                    herbObjs.clean_avantoe,
                    levelReq = 48,
                    cleanXp = 10.0,
                ),
                HerbDef(
                    herbObjs.grimy_kwuarm,
                    herbObjs.clean_kwuarm,
                    levelReq = 54,
                    cleanXp = 11.3,
                ),
                HerbDef(
                    herbObjs.grimy_snapdragon,
                    herbObjs.clean_snapdragon,
                    levelReq = 59,
                    cleanXp = 12.8,
                ),
                HerbDef(
                    herbObjs.grimy_cadantine,
                    herbObjs.clean_cadantine,
                    levelReq = 65,
                    cleanXp = 12.5,
                ),
                HerbDef(
                    herbObjs.grimy_lantadyme,
                    herbObjs.clean_lantadyme,
                    levelReq = 67,
                    cleanXp = 13.1,
                ),
                HerbDef(
                    herbObjs.grimy_dwarf_weed,
                    herbObjs.clean_dwarf_weed,
                    levelReq = 70,
                    cleanXp = 13.8,
                ),
                HerbDef(
                    herbObjs.grimy_torstol,
                    herbObjs.clean_torstol,
                    levelReq = 75,
                    cleanXp = 15.0,
                ),
            )

        // Maps clean herb → its matching unfinished potion
        private val HERB_TO_UNF: Map<ObjType, ObjType> =
            mapOf(
                herbObjs.clean_guam to herbObjs.guam_potion_unf,
                herbObjs.clean_marrentill to herbObjs.marrentill_potion_unf,
                herbObjs.clean_tarromin to herbObjs.tarromin_potion_unf,
                herbObjs.clean_harralander to herbObjs.harralander_potion_unf,
                herbObjs.clean_ranarr to herbObjs.ranarr_potion_unf,
                herbObjs.clean_toadflax to herbObjs.toadflax_potion_unf,
                herbObjs.clean_irit to herbObjs.irit_potion_unf,
                herbObjs.clean_avantoe to herbObjs.avantoe_potion_unf,
                herbObjs.clean_kwuarm to herbObjs.kwuarm_potion_unf,
                herbObjs.clean_snapdragon to herbObjs.snapdragon_potion_unf,
                herbObjs.clean_cadantine to herbObjs.cadantine_potion_unf,
                herbObjs.clean_lantadyme to herbObjs.lantadyme_potion_unf,
                herbObjs.clean_dwarf_weed to herbObjs.dwarf_weed_potion_unf,
                herbObjs.clean_torstol to herbObjs.torstol_potion_unf,
            )

        // Wiki-accurate potion mixing XP and level requirements (dose 3 products)
        private val POTION_DEFS: List<PotionDef> =
            listOf(
                // Level 1 — Attack potion (guam + eye of newt)
                PotionDef(
                    herbObjs.guam_potion_unf,
                    herbObjs.eye_of_newt,
                    herbObjs.attack_potion_3,
                    levelReq = 1,
                    mixXp = 25.0,
                ),
                // Level 5 — Antipoison (marrentill + unicorn horn dust)
                PotionDef(
                    herbObjs.marrentill_potion_unf,
                    herbObjs.unicorn_horn_dust,
                    herbObjs.antipoison_3,
                    levelReq = 5,
                    mixXp = 37.5,
                ),
                // Level 12 — Strength potion (tarromin + limpwurt root)
                PotionDef(
                    herbObjs.tarromin_potion_unf,
                    herbObjs.limpwurt_root,
                    herbObjs.strength_potion_3,
                    levelReq = 12,
                    mixXp = 50.0,
                ),
                // Level 18 — Restore potion (harralander + red spider eggs)
                PotionDef(
                    herbObjs.harralander_potion_unf,
                    herbObjs.red_spider_eggs,
                    herbObjs.restore_potion_3,
                    levelReq = 18,
                    mixXp = 62.5,
                ),
                // Level 22 — Energy potion (harralander + chocolate dust — TODO: add chocolate dust
                // obj)
                // Skipping for now — chocolate dust sym name unknown.
                // Level 26 — Defence potion (ranarr + white berries)
                PotionDef(
                    herbObjs.ranarr_potion_unf,
                    herbObjs.white_berries,
                    herbObjs.defence_potion_3,
                    levelReq = 30,
                    mixXp = 75.0,
                ),
                // Level 38 — Prayer potion (ranarr + snape grass)
                PotionDef(
                    herbObjs.ranarr_potion_unf,
                    herbObjs.snape_grass,
                    herbObjs.prayer_potion_3,
                    levelReq = 38,
                    mixXp = 87.5,
                ),
                // Level 45 — Super attack (irit + eye of newt)
                PotionDef(
                    herbObjs.irit_potion_unf,
                    herbObjs.eye_of_newt,
                    herbObjs.super_attack_3,
                    levelReq = 45,
                    mixXp = 100.0,
                ),
                // Level 48 — Super antipoison (irit + unicorn horn dust)
                PotionDef(
                    herbObjs.irit_potion_unf,
                    herbObjs.unicorn_horn_dust,
                    herbObjs.super_antipoison_3,
                    levelReq = 48,
                    mixXp = 106.3,
                ),
                // Level 50 — Fishing potion (avantoe + snape grass)
                PotionDef(
                    herbObjs.avantoe_potion_unf,
                    herbObjs.snape_grass,
                    herbObjs.fishing_potion_3,
                    levelReq = 50,
                    mixXp = 112.5,
                ),
                // Level 52 — Super energy (avantoe + mort myre fungus)
                PotionDef(
                    herbObjs.avantoe_potion_unf,
                    herbObjs.mort_myre_fungus,
                    herbObjs.super_energy_3,
                    levelReq = 52,
                    mixXp = 117.5,
                ),
                // Level 53 — Hunter potion (avantoe + kebbit teeth dust — TODO: add obj)
                // Skipping kebbit teeth dust for now.
                // Level 55 — Super strength (kwuarm + limpwurt root)
                PotionDef(
                    herbObjs.kwuarm_potion_unf,
                    herbObjs.limpwurt_root,
                    herbObjs.super_strength_3,
                    levelReq = 55,
                    mixXp = 125.0,
                ),
                // Level 57 — Antidote+ (toadflax + yew roots — TODO: add obj)
                // Skipping yew roots for now.
                // Level 63 — Super restore (snapdragon + red spider eggs)
                PotionDef(
                    herbObjs.snapdragon_potion_unf,
                    herbObjs.red_spider_eggs,
                    herbObjs.super_restore_3,
                    levelReq = 63,
                    mixXp = 142.5,
                ),
                // Level 66 — Super defence (cadantine + white berries)
                PotionDef(
                    herbObjs.cadantine_potion_unf,
                    herbObjs.white_berries,
                    herbObjs.super_defence_3,
                    levelReq = 66,
                    mixXp = 150.0,
                ),
                // Level 69 — Antifire (lantadyme + dragon scale dust)
                PotionDef(
                    herbObjs.lantadyme_potion_unf,
                    herbObjs.dragon_scale_dust,
                    herbObjs.antifire_3,
                    levelReq = 69,
                    mixXp = 157.5,
                ),
                // Level 72 — Ranging potion (dwarf weed + wine of zamorak)
                PotionDef(
                    herbObjs.dwarf_weed_potion_unf,
                    herbObjs.wine_of_zamorak,
                    herbObjs.ranging_potion_3,
                    levelReq = 72,
                    mixXp = 162.5,
                ),
                // Level 76 — Magic potion (lantadyme + potato cactus)
                PotionDef(
                    herbObjs.lantadyme_potion_unf,
                    herbObjs.potato_cactus,
                    herbObjs.magic_potion_3,
                    levelReq = 76,
                    mixXp = 172.5,
                ),
                // Level 78 — Zamorak brew (torstol + jangerberries)
                PotionDef(
                    herbObjs.torstol_potion_unf,
                    herbObjs.jangerberries,
                    herbObjs.zamorak_brew_3,
                    levelReq = 78,
                    mixXp = 175.0,
                ),
                // Level 84 — Saradomin brew (toadflax + crushed nest)
                PotionDef(
                    herbObjs.toadflax_potion_unf,
                    herbObjs.crushed_nest,
                    herbObjs.saradomin_brew_3,
                    levelReq = 81,
                    mixXp = 180.0,
                ),
                // Level 90 — Extended antifire (antifire + lava scale shard — TODO)
                // Level 94 — Super combat (torstol + all super pots — TODO: different mechanic)
            )
    }
}
