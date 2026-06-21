package org.rsmod.content.skills.ranged

import org.rsmod.plugin.module.PluginModule

class RangedSkillModule : PluginModule() {
    override fun bind() {
        // Ranged skill module binding.
        //
        // Core ranged combat is handled by:
        //   - api/combat/ (PvNCombat.attackRanged, RangedAmmoManager,
        //     PlayerAttackManager.giveCombatXp)
        //   - content/mechanics/ranged/configs/RangedObjs.kt (bow/arrow ObjEditor configs)
        //
        // This module registers the ranged skill so the game's content system
        // can discover it. No additional bindings are needed here because the
        // combat API and mechanics layer already handle all F2P ranged gameplay:
        // attack flow, arrow consumption, XP awarding, and projectile rendering.
    }
}
