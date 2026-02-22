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
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.herbloreLvl
import org.rsmod.api.script.onOpHeldU
import org.rsmod.api.script.onOpObj1
import org.rsmod.api.stats.xpmod.XpModifiers
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

class Herblore @Inject constructor(private val xpMods: XpModifiers) : PluginScript() {

    override fun ScriptContext.startup() {
        // ---- Cleaning grimy herbs (click on herb directly) ----
        for (def in HERB_DEFS) {
            onOpObj1(def.grimy) { cleanHerb(def) }
        }

        // ---- Clean herb + vial of water → unfinished potion ----
        for (def in HERB_DEFS) {
            val unfPotion = HERB_TO_UNF[def.clean] ?: continue
            onOpHeldU(def.clean, objs.vial_of_water) { makeUnfPotion(def, unfPotion) }
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
        anim(seqs.human_herblore)
        val xp = def.cleanXp * xpMods.get(player, stats.herblore)
        statAdvance(stats.herblore, xp)
        invAdd(inv, def.clean)
    }

    private suspend fun ProtectedAccess.makeUnfPotion(def: HerbDef, unfPotion: ObjType) {
        if (player.herbloreLvl < def.levelReq) {
            mes("You need a Herblore level of ${def.levelReq} to make this potion.")
            return
        }
        invDel(inv, def.clean, count = 1)
        invDel(inv, objs.vial_of_water, count = 1)
        anim(seqs.human_herblore)
        delay(1)
        // No XP for adding herb to vial — XP is given when secondary is added.
        invAdd(inv, unfPotion)
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
        invDel(inv, def.secondary, count = 1)
        invDel(inv, def.unfPotion, count = 1)
        anim(seqs.human_herblore)
        delay(1)
        val xp = def.mixXp * xpMods.get(player, stats.herblore)
        statAdvance(stats.herblore, xp)
        invAdd(inv, def.product)
        mes("You mix the ingredients into a potion.")
    }

    companion object {
        // Wiki-accurate cleaning XP and level requirements
        private val HERB_DEFS: List<HerbDef> =
            listOf(
                HerbDef(objs.grimy_guam, objs.guam_leaf, levelReq = 3, cleanXp = 2.5),
                HerbDef(objs.grimy_marrentill, objs.marrentill, levelReq = 5, cleanXp = 3.8),
                HerbDef(objs.grimy_tarromin, objs.tarromin, levelReq = 11, cleanXp = 5.0),
                HerbDef(objs.grimy_harralander, objs.harralander, levelReq = 20, cleanXp = 6.3),
                HerbDef(objs.grimy_ranarr_weed, objs.ranarr_weed, levelReq = 25, cleanXp = 7.5),
                HerbDef(objs.grimy_toadflax, objs.toadflax, levelReq = 30, cleanXp = 8.0),
                HerbDef(objs.grimy_irit_leaf, objs.irit_leaf, levelReq = 40, cleanXp = 8.8),
                HerbDef(objs.grimy_avantoe, objs.avantoe, levelReq = 48, cleanXp = 10.0),
                HerbDef(objs.grimy_kwuarm, objs.kwuarm, levelReq = 54, cleanXp = 11.3),
                HerbDef(objs.grimy_snapdragon, objs.snapdragon, levelReq = 59, cleanXp = 12.8),
                HerbDef(objs.grimy_cadantine, objs.cadantine, levelReq = 65, cleanXp = 12.5),
                HerbDef(objs.grimy_lantadyme, objs.lantadyme, levelReq = 67, cleanXp = 13.1),
                HerbDef(objs.grimy_dwarf_weed, objs.dwarf_weed, levelReq = 70, cleanXp = 13.8),
                HerbDef(objs.grimy_torstol, objs.torstol, levelReq = 75, cleanXp = 15.0),
            )

        // Maps clean herb → its matching unfinished potion
        private val HERB_TO_UNF: Map<ObjType, ObjType> =
            mapOf(
                objs.guam_leaf to objs.guam_potion_unf,
                objs.marrentill to objs.marrentill_potion_unf,
                objs.tarromin to objs.tarromin_potion_unf,
                objs.harralander to objs.harralander_potion_unf,
                objs.ranarr_weed to objs.ranarr_potion_unf,
                objs.toadflax to objs.toadflax_potion_unf,
                objs.irit_leaf to objs.irit_potion_unf,
                objs.avantoe to objs.avantoe_potion_unf,
                objs.kwuarm to objs.kwuarm_potion_unf,
                objs.snapdragon to objs.snapdragon_potion_unf,
                objs.cadantine to objs.cadantine_potion_unf,
                objs.lantadyme to objs.lantadyme_potion_unf,
                objs.dwarf_weed to objs.dwarf_weed_potion_unf,
                objs.torstol to objs.torstol_potion_unf,
            )

        // Wiki-accurate potion mixing XP and level requirements (dose 3 products)
        private val POTION_DEFS: List<PotionDef> =
            listOf(
                // Level 1 — Attack potion (guam + eye of newt)
                PotionDef(
                    objs.guam_potion_unf,
                    objs.eye_of_newt,
                    objs.attack_potion_3,
                    levelReq = 1,
                    mixXp = 25.0,
                ),
                // Level 5 — Antipoison (marrentill + unicorn horn dust)
                PotionDef(
                    objs.marrentill_potion_unf,
                    objs.unicorn_horn_dust,
                    objs.antipoison_3,
                    levelReq = 5,
                    mixXp = 37.5,
                ),
                // Level 12 — Strength potion (tarromin + limpwurt root)
                PotionDef(
                    objs.tarromin_potion_unf,
                    objs.limpwurt_root,
                    objs.strength_potion_3,
                    levelReq = 12,
                    mixXp = 50.0,
                ),
                // Level 18 — Restore potion (harralander + red spider eggs)
                PotionDef(
                    objs.harralander_potion_unf,
                    objs.red_spider_eggs,
                    objs.restore_potion_3,
                    levelReq = 18,
                    mixXp = 62.5,
                ),
                // Level 22 — Energy potion (harralander + chocolate dust — TODO: add chocolate dust
                // obj)
                // Skipping for now — chocolate dust sym name unknown.
                // Level 26 — Defence potion (ranarr + white berries)
                PotionDef(
                    objs.ranarr_potion_unf,
                    objs.white_berries,
                    objs.defence_potion_3,
                    levelReq = 30,
                    mixXp = 75.0,
                ),
                // Level 38 — Prayer potion (ranarr + snape grass)
                PotionDef(
                    objs.ranarr_potion_unf,
                    objs.snape_grass,
                    objs.prayer_potion_3,
                    levelReq = 38,
                    mixXp = 87.5,
                ),
                // Level 45 — Super attack (irit + eye of newt)
                PotionDef(
                    objs.irit_potion_unf,
                    objs.eye_of_newt,
                    objs.super_attack_3,
                    levelReq = 45,
                    mixXp = 100.0,
                ),
                // Level 48 — Super antipoison (irit + unicorn horn dust)
                PotionDef(
                    objs.irit_potion_unf,
                    objs.unicorn_horn_dust,
                    objs.super_antipoison_3,
                    levelReq = 48,
                    mixXp = 106.3,
                ),
                // Level 50 — Fishing potion (avantoe + snape grass)
                PotionDef(
                    objs.avantoe_potion_unf,
                    objs.snape_grass,
                    objs.fishing_potion_3,
                    levelReq = 50,
                    mixXp = 112.5,
                ),
                // Level 52 — Super energy (avantoe + mort myre fungus)
                PotionDef(
                    objs.avantoe_potion_unf,
                    objs.mort_myre_fungus,
                    objs.super_energy_3,
                    levelReq = 52,
                    mixXp = 117.5,
                ),
                // Level 53 — Hunter potion (avantoe + kebbit teeth dust — TODO: add obj)
                // Skipping kebbit teeth dust for now.
                // Level 55 — Super strength (kwuarm + limpwurt root)
                PotionDef(
                    objs.kwuarm_potion_unf,
                    objs.limpwurt_root,
                    objs.super_strength_3,
                    levelReq = 55,
                    mixXp = 125.0,
                ),
                // Level 57 — Antidote+ (toadflax + yew roots — TODO: add obj)
                // Skipping yew roots for now.
                // Level 63 — Super restore (snapdragon + red spider eggs)
                PotionDef(
                    objs.snapdragon_potion_unf,
                    objs.red_spider_eggs,
                    objs.super_restore_3,
                    levelReq = 63,
                    mixXp = 142.5,
                ),
                // Level 66 — Super defence (cadantine + white berries)
                PotionDef(
                    objs.cadantine_potion_unf,
                    objs.white_berries,
                    objs.super_defence_3,
                    levelReq = 66,
                    mixXp = 150.0,
                ),
                // Level 69 — Antifire (lantadyme + dragon scale dust)
                PotionDef(
                    objs.lantadyme_potion_unf,
                    objs.dragon_scale_dust,
                    objs.antifire_3,
                    levelReq = 69,
                    mixXp = 157.5,
                ),
                // Level 72 — Ranging potion (dwarf weed + wine of zamorak)
                PotionDef(
                    objs.dwarf_weed_potion_unf,
                    objs.wine_of_zamorak,
                    objs.ranging_potion_3,
                    levelReq = 72,
                    mixXp = 162.5,
                ),
                // Level 76 — Magic potion (lantadyme + potato cactus)
                PotionDef(
                    objs.lantadyme_potion_unf,
                    objs.potato_cactus,
                    objs.magic_potion_3,
                    levelReq = 76,
                    mixXp = 172.5,
                ),
                // Level 78 — Zamorak brew (torstol + jangerberries)
                PotionDef(
                    objs.torstol_potion_unf,
                    objs.jangerberries,
                    objs.zamorak_brew_3,
                    levelReq = 78,
                    mixXp = 175.0,
                ),
                // Level 84 — Saradomin brew (toadflax + crushed nest)
                PotionDef(
                    objs.toadflax_potion_unf,
                    objs.crushed_nest,
                    objs.saradomin_brew_3,
                    levelReq = 81,
                    mixXp = 180.0,
                ),
                // Level 90 — Extended antifire (antifire + lava scale shard — TODO)
                // Level 94 — Super combat (torstol + all super pots — TODO: different mechanic)
            )
    }
}
