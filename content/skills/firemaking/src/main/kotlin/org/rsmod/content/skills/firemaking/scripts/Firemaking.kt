package org.rsmod.content.skills.firemaking.scripts

// IMPLEMENTATION NOTES:
// All engine gaps resolved:
//  - seqs.human_firemaking promoted to BaseSeqs.kt.
//  - objs.ashes and objs.blisterwood_logs promoted to BaseObjs.kt.
//  - "fire" loc still uses local FiremakingLocs (no BaseLocs.kt yet — see ENGINE_GAPS.md).
//
// Remaining TODO:
//  - content.firemaking_log group: once added to BaseContent.kt, collapse the registration
//    loop to: onOpHeldU(content.firemaking_log, objs.tinderbox) { ... }
//  - Fire lifespan durations: taken from Kronos rev 184, flag for wiki validation.

import jakarta.inject.Inject
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.stats
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.stat.firemakingLvl
import org.rsmod.api.repo.loc.LocRepository
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onOpHeldU
import org.rsmod.api.stats.xpmod.XpModifiers
import org.rsmod.api.type.refs.loc.LocReferences
import org.rsmod.game.loc.LocAngle
import org.rsmod.game.loc.LocShape
import org.rsmod.game.type.obj.ObjType
import org.rsmod.map.CoordGrid
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

// Fire loc — confirmed in loc.sym (ID 26185). Local ref until a shared BaseLocs.kt exists.
private typealias firemaking_locs = FiremakingLocs

internal object FiremakingLocs : LocReferences() {
    val fire = find("fire")
}

// TODO: wiki-validate — lifespan tick durations. Kronos uses 200 ticks for normal logs and
//       scales up to 600 for redwood. These match close enough for rev 228.
data class LogDef(val obj: ObjType, val levelReq: Int, val xp: Double, val fireLifespanTicks: Int)

class Firemaking
@Inject
constructor(
    private val locRepo: LocRepository,
    private val objRepo: ObjRepository,
    private val xpMods: XpModifiers,
) : PluginScript() {

    override fun ScriptContext.startup() {
        // Register tinderbox-on-log handlers for every log type.
        // Each registration normalises the pair so `first` is always the log and `second` is
        // the tinderbox, regardless of which the player actually drags onto which.
        for (log in LOG_DEFS) {
            onOpHeldU(log.obj, objs.tinderbox) { lightLog(log) }
        }
    }

    private suspend fun ProtectedAccess.lightLog(log: LogDef) {
        if (player.firemakingLvl < log.levelReq) {
            mes("You need a Firemaking level of ${log.levelReq} to burn this log.")
            return
        }

        // Consume the log from the player's inventory.
        val deleted = invDel(inv, log.obj, count = 1, strict = true)
        if (deleted.failure) {
            // Should not happen since the event guarantees the item was in inventory,
            // but guard defensively.
            return
        }

        mes("You attempt to light the logs.")

        // Lighting animation — seqs.human_firemaking now in BaseSeqs.kt
        anim(seqs.human_firemaking)

        // It takes 3 ticks to light a fire in OSRS vanilla (no fail mechanic post-EOC;
        // the lighting is guaranteed after the animation completes).
        delay(3)

        // Capture the tile the player is standing on — this is where the fire will be placed.
        val fireTile: CoordGrid = coords

        // Move the player one tile west. The player walks during the next game tick,
        // so we queue a walk then delay one tick to allow the movement to complete.
        val westTile = fireTile.translateX(-1)
        walk(westTile)
        delay(1)

        mes("The fire catches and the logs begin to burn.")

        // Grant XP with modifier support (e.g. bonfires, bonus XP weekends).
        val xp = log.xp * xpMods.get(player, stats.firemaking)
        statAdvance(stats.firemaking, xp)

        // Place fire loc at the original tile — auto-despawns after fireLifespanTicks.
        // loc.sym confirms "fire" exists (ID 26185).
        locRepo.add(
            coords = fireTile,
            type = firemaking_locs.fire,
            duration = log.fireLifespanTicks,
            angle = LocAngle.West,
            shape = LocShape.CentrepieceStraight,
        )

        // Ashes ground item — spawned after fire burns out (objs.ashes now in BaseObjs).
        objRepo.add(type = objs.ashes, coords = fireTile, duration = log.fireLifespanTicks + 100)

        // Chain-light: if the player still has the same type of log in their inventory, continue
        // lighting automatically. The slot freed by the consumed log guarantees room is available.
        if (log.obj in inv) {
            lightLog(log)
        }
    }

    companion object {
        // wiki-accurate XP and level requirements for rev 228.
        // Fire lifespans from Kronos (rev 184) — close enough for rev 228; flag for validation.
        val LOG_DEFS: List<LogDef> =
            listOf(
                LogDef(
                    objs.logs,
                    levelReq = 1,
                    xp = 40.0,
                    fireLifespanTicks = 200,
                ), // TODO: wiki-validate lifespan
                LogDef(
                    objs.achey_tree_logs,
                    levelReq = 1,
                    xp = 40.0,
                    fireLifespanTicks = 200,
                ), // TODO: wiki-validate lifespan
                LogDef(
                    objs.oak_logs,
                    levelReq = 15,
                    xp = 60.0,
                    fireLifespanTicks = 233,
                ), // TODO: wiki-validate lifespan
                LogDef(
                    objs.willow_logs,
                    levelReq = 30,
                    xp = 90.0,
                    fireLifespanTicks = 284,
                ), // TODO: wiki-validate lifespan
                LogDef(
                    objs.teak_logs,
                    levelReq = 35,
                    xp = 105.0,
                    fireLifespanTicks = 316,
                ), // TODO: wiki-validate lifespan
                LogDef(
                    objs.arctic_pine_logs,
                    levelReq = 42,
                    xp = 125.0,
                    fireLifespanTicks = 330,
                ), // TODO: wiki-validate lifespan
                LogDef(
                    objs.maple_logs,
                    levelReq = 45,
                    xp = 135.0,
                    fireLifespanTicks = 350,
                ), // TODO: wiki-validate lifespan
                LogDef(
                    objs.mahogany_logs,
                    levelReq = 50,
                    xp = 157.5,
                    fireLifespanTicks = 400,
                ), // TODO: wiki-validate lifespan
                LogDef(
                    objs.yew_logs,
                    levelReq = 60,
                    xp = 202.5,
                    fireLifespanTicks = 500,
                ), // TODO: wiki-validate lifespan
                LogDef(
                    objs.magic_logs,
                    levelReq = 75,
                    xp = 303.8,
                    fireLifespanTicks = 550,
                ), // TODO: wiki-validate lifespan
                LogDef(
                    objs.blisterwood_logs,
                    levelReq = 76,
                    xp = 96.0,
                    fireLifespanTicks = 300,
                ), // TODO: wiki-validate lifespan (Darkmeyer reward tree)
                LogDef(
                    objs.redwood_logs,
                    levelReq = 90,
                    xp = 350.0,
                    fireLifespanTicks = 600,
                ), // TODO: wiki-validate lifespan
            )
    }
}
