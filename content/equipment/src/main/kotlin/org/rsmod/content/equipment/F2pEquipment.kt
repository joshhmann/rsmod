package org.rsmod.content.equipment

/**
 * F2P Equipment tiers: Bronze → Iron → Steel → Black → Mithril → Adamant → Rune Weapon and armor
 * stats are configured via ObjEditors in their respective modules This provides documentation for
 * F2P equipment tiers
 *
 * F2P Melee Weapons by Tier:
 * - Bronze (Level 1): dagger, sword, longsword, scimitar, mace, axe, 2h sword
 * - Iron (Level 1): dagger, sword, longsword, scimitar, mace, axe, 2h sword
 * - Steel (Level 5): dagger, sword, longsword, scimitar, mace, axe, 2h sword
 * - Black (Level 10): dagger, sword, longsword, scimitar, mace, axe, 2h sword
 * - Mithril (Level 20): dagger, sword, longsword, scimitar, mace, axe, 2h sword
 * - Adamant (Level 30): dagger, sword, longsword, scimitar, mace, axe, 2h sword
 * - Rune (Level 40): dagger, sword, longsword, scimitar, mace, axe, 2h sword
 *
 * F2P Armor by Tier:
 * - Bronze (Level 1): full helm, med helm, chainbody, platebody, platelegs, plateskirt, kite
 *   shield, sq shield
 * - Iron (Level 1): full helm, med helm, chainbody, platebody, platelegs, plateskirt, kite shield,
 *   sq shield
 * - Steel (Level 5): full helm, med helm, chainbody, platebody, platelegs, plateskirt, kite shield,
 *   sq shield
 * - Black (Level 10): full helm, med helm, chainbody, platebody, platelegs, plateskirt, kite
 *   shield, sq shield
 * - Mithril (Level 20): full helm, med helm, chainbody, platebody, platelegs, plateskirt, kite
 *   shield, sq shield
 * - Adamant (Level 30): full helm, med helm, chainbody, platebody, platelegs, plateskirt, kite
 *   shield, sq shield
 * - Rune (Level 40): full helm, med helm, chainbody, platebody, platelegs, plateskirt, kite shield,
 *   sq shield
 */
object F2pEquipmentTiers {
    const val BRONZE_ATTACK_REQ = 1
    const val IRON_ATTACK_REQ = 1
    const val STEEL_ATTACK_REQ = 5
    const val BLACK_ATTACK_REQ = 10
    const val MITHRIL_ATTACK_REQ = 20
    const val ADAMANT_ATTACK_REQ = 30
    const val RUNE_ATTACK_REQ = 40

    const val BRONZE_DEFENCE_REQ = 1
    const val IRON_DEFENCE_REQ = 1
    const val STEEL_DEFENCE_REQ = 5
    const val BLACK_DEFENCE_REQ = 10
    const val MITHRIL_DEFENCE_REQ = 20
    const val ADAMANT_DEFENCE_REQ = 30
    const val RUNE_DEFENCE_REQ = 40
}
