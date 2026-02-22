package org.rsmod.api.quest

import org.rsmod.api.config.refs.BaseStats
import org.rsmod.api.config.refs.BaseVarps
import org.rsmod.api.config.refs.objs

public object QuestList {
    public val cooks_assistant: Quest =
        Quest(
            id = 1,
            name = "Cook's Assistant",
            varp = BaseVarps.cookquest,
            maxStage = 2, // 0: Not started, 1: In progress, 2: Finished
            rewards =
                questRewards {
                    xp(BaseStats.cooking, 300)
                    extra("1 Quest Point")
                },
        )

    public val sheep_shearer: Quest =
        Quest(
            id = 2,
            name = "Sheep Shearer",
            varp = BaseVarps.sheep,
            maxStage = 2,
            rewards =
                questRewards {
                    xp(BaseStats.crafting, 150)
                    item(objs.coins, 60)
                    extra("1 Quest Point")
                },
        )

    public val restless_ghost: Quest =
        Quest(
            id = 3,
            name = "The Restless Ghost",
            varp = BaseVarps.haunted,
            maxStage = 2,
            rewards =
                questRewards {
                    xp(BaseStats.prayer, 1162)
                    extra("1 Quest Point")
                },
        )

    public val romeo_and_juliet: Quest =
        Quest(
            id = 4,
            name = "Romeo & Juliet",
            varp = BaseVarps.rjquest,
            maxStage = 2,
            rewards = questRewards { extra("5 Quest Points") },
        )

    public val imp_catcher: Quest =
        Quest(
            id = 5,
            name = "Imp Catcher",
            varp = BaseVarps.imp,
            maxStage = 2,
            rewards =
                questRewards {
                    xp(BaseStats.magic, 875)
                    extra("1 Quest Point")
                },
        )

    public val witchs_potion: Quest =
        Quest(
            id = 6,
            name = "Witch's Potion",
            varp = BaseVarps.hetty,
            maxStage = 2,
            rewards =
                questRewards {
                    xp(BaseStats.magic, 325)
                    extra("1 Quest Point")
                },
        )

    public val dorics_quest: Quest =
        Quest(
            id = 7,
            name = "Doric's Quest",
            varp = BaseVarps.doricquest,
            maxStage = 2,
            rewards =
                questRewards {
                    xp(BaseStats.mining, 1300)
                    extra("1 Quest Point")
                },
        )

    public val rune_mysteries: Quest =
        Quest(
            id = 8,
            name = "Rune Mysteries",
            varp = BaseVarps.runemysteries,
            maxStage = 2,
            rewards =
                questRewards {
                    extra("1 Quest Point")
                    extra("Access to Runecrafting skill")
                },
        )

    public val vampyre_slayer: Quest =
        Quest(
            id = 9,
            name = "Vampyre Slayer",
            varp = BaseVarps.vampire,
            maxStage = 2,
            rewards =
                questRewards {
                    xp(BaseStats.attack, 4825)
                    extra("3 Quest Points")
                },
        )

    public val dragon_slayer_i: Quest =
        Quest(
            id = 10,
            name = "Dragon Slayer I",
            varp = BaseVarps.dragonquest,
            maxStage = 2,
            rewards =
                questRewards {
                    xp(BaseStats.strength, 18650)
                    xp(BaseStats.defence, 18650)
                    extra("2 Quest Points")
                    extra("Ability to equip rune platebody and green d'hide body")
                },
        )

    public val black_knights_fortress: Quest =
        Quest(
            id = 11,
            name = "Black Knights' Fortress",
            varp = BaseVarps.hunt,
            maxStage = 2,
            rewards =
                questRewards {
                    item(objs.coins, 2500)
                    extra("3 Quest Points")
                },
        )

    public val prince_ali_rescue: Quest =
        Quest(
            id = 12,
            name = "Prince Ali Rescue",
            varp = BaseVarps.desertrescue,
            maxStage = 2,
            rewards =
                questRewards {
                    item(objs.coins, 700)
                    extra("3 Quest Points")
                    extra("Free passage through Al Kharid toll gate")
                },
        )

    public val pirates_treasure: Quest =
        Quest(
            id = 13,
            name = "Pirate's Treasure",
            varp = BaseVarps.pirate_quest,
            maxStage = 2,
            rewards =
                questRewards {
                    item(objs.coins, 450)
                    extra("2 Quest Points")
                },
        )
}
