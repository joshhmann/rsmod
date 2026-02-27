package org.rsmod.content.mechanics.clues.configs

/**
 * Easy tier clue scroll steps.
 *
 * Based on OSRS easy clue scroll data. These are F2P-accessible locations. Easy clues have 2-6
 * steps per clue scroll.
 *
 * Source: https://oldschool.runescape.wiki/w/Clue_scroll_(easy)
 */
object EasyClueSteps {
    val steps =
        listOf(
            // ============================================================
            // EMOTE CLUES (require specific emote at location)
            // ============================================================

            // F2P Emote Clues
            ClueStep(
                101,
                ClueTier.EASY,
                ClueStepType.EMOTE,
                "Bow in the entrance to the Edgeville Monastery.",
                coordX = 3053,
                coordZ = 3482,
                emote = "BOW",
            ),
            ClueStep(
                102,
                ClueTier.EASY,
                ClueStepType.EMOTE,
                "Dance at the crossroads north of Draynor Village.",
                coordX = 3109,
                coordZ = 3294,
                emote = "DANCE",
            ),
            ClueStep(
                103,
                ClueTier.EASY,
                ClueStepType.EMOTE,
                "Wave on the bridge at the Lumber Yard.",
                coordX = 3302,
                coordZ = 3492,
                emote = "WAVE",
            ),
            ClueStep(
                104,
                ClueTier.EASY,
                ClueStepType.EMOTE,
                "Cheer at the Draynor Village market.",
                coordX = 3082,
                coordZ = 3250,
                emote = "CHEER",
            ),
            ClueStep(
                105,
                ClueTier.EASY,
                ClueStepType.EMOTE,
                "Clap on the bridge to the Wizards' Tower.",
                coordX = 3113,
                coordZ = 3178,
                emote = "CLAP",
            ),
            ClueStep(
                106,
                ClueTier.EASY,
                ClueStepType.EMOTE,
                "Spin on the bridge south of the Lumber Yard.",
                coordX = 3301,
                coordZ = 3471,
                emote = "SPIN",
            ),
            ClueStep(
                107,
                ClueTier.EASY,
                ClueStepType.EMOTE,
                "Think in the middle of the wheat field by the Lumbridge mill.",
                coordX = 3160,
                coordZ = 3295,
                emote = "THINK",
            ),
            ClueStep(
                108,
                ClueTier.EASY,
                ClueStepType.EMOTE,
                "Wave on the north side of the Falador wheat field.",
                coordX = 3038,
                coordZ = 3312,
                emote = "WAVE",
            ),
            ClueStep(
                109,
                ClueTier.EASY,
                ClueStepType.EMOTE,
                "Bow at the entrance of the Varrock Palace courtyard.",
                coordX = 3211,
                coordZ = 3461,
                emote = "BOW",
            ),
            ClueStep(
                110,
                ClueTier.EASY,
                ClueStepType.EMOTE,
                "Dance in the middle of the Varrock fountain.",
                coordX = 3213,
                coordZ = 3427,
                emote = "DANCE",
            ),

            // ============================================================
            // DIG CLUES (require spade to dig at location)
            // ============================================================

            ClueStep(
                201,
                ClueTier.EASY,
                ClueStepType.DIG,
                "Dig in the middle of the stone circle at the south of Varrock.",
                coordX = 3230,
                coordZ = 3383,
            ),
            ClueStep(
                202,
                ClueTier.EASY,
                ClueStepType.DIG,
                "Dig at the entrance to the Varrock Sewers.",
                coordX = 3237,
                coordZ = 3458,
            ),
            ClueStep(
                203,
                ClueTier.EASY,
                ClueStepType.DIG,
                "Dig in the middle of the wheat field west of the Lumbridge mill.",
                coordX = 3160,
                coordZ = 3290,
            ),
            ClueStep(
                204,
                ClueTier.EASY,
                ClueStepType.DIG,
                "Dig near the mining spot north of the Al Kharid bank.",
                coordX = 3298,
                coordZ = 3294,
            ),
            ClueStep(
                205,
                ClueTier.EASY,
                ClueStepType.DIG,
                "Dig at the crossroads south of the Champions' Guild.",
                coordX = 3193,
                coordZ = 3359,
            ),
            ClueStep(
                206,
                ClueTier.EASY,
                ClueStepType.DIG,
                "Dig in the middle of the cow pen north of the Lumbridge windmill.",
                coordX = 3175,
                coordZ = 3318,
            ),
            ClueStep(
                207,
                ClueTier.EASY,
                ClueStepType.DIG,
                "Dig at the entrance to the Draynor Village jail.",
                coordX = 3127,
                coordZ = 3245,
            ),

            // ============================================================
            // SIMPLE CLUES (talk to NPC)
            // ============================================================

            ClueStep(
                301,
                ClueTier.EASY,
                ClueStepType.SIMPLE,
                "Talk to the squire in the White Knights' Castle.",
                answer = "Squire",
            ),
            ClueStep(
                302,
                ClueTier.EASY,
                ClueStepType.SIMPLE,
                "Talk to Gerrant in the Port Sarim fishing shop.",
                answer = "Gerrant",
            ),
            ClueStep(
                303,
                ClueTier.EASY,
                ClueStepType.SIMPLE,
                "Talk to the shopkeeper in the Varrock General Store.",
                answer = "Shopkeeper",
            ),
            ClueStep(
                304,
                ClueTier.EASY,
                ClueStepType.SIMPLE,
                "Talk to the bartender at the Blue Moon Inn in Varrock.",
                answer = "Bartender",
            ),
            ClueStep(
                305,
                ClueTier.EASY,
                ClueStepType.SIMPLE,
                "Talk to the monk at the Edgeville Monastery.",
                answer = "Monk",
            ),
            ClueStep(
                306,
                ClueTier.EASY,
                ClueStepType.SIMPLE,
                "Talk to the toll gate guard at the entrance to Al Kharid.",
                answer = "Border Guard",
            ),
            ClueStep(
                307,
                ClueTier.EASY,
                ClueStepType.SIMPLE,
                "Talk to the Wise Old Man in Draynor Village.",
                answer = "Wise Old Man",
            ),
            ClueStep(
                308,
                ClueTier.EASY,
                ClueStepType.SIMPLE,
                "Talk to the magic instructor at the Lumbridge swamp.",
                answer = "Magic Instructor",
            ),
            ClueStep(
                309,
                ClueTier.EASY,
                ClueStepType.SIMPLE,
                "Talk to the mining tutor at the Lumbridge swamp.",
                answer = "Mining Instructor",
            ),
            ClueStep(
                310,
                ClueTier.EASY,
                ClueStepType.SIMPLE,
                "Talk to the head chef in the Lumbridge Castle kitchen.",
                answer = "Cook",
            ),

            // ============================================================
            // MAP CLUES (follow treasure map)
            // ============================================================

            ClueStep(
                401,
                ClueTier.EASY,
                ClueStepType.MAP,
                "Search the crate in the guard house at the Gnome Ball field.",
                coordX = 2397,
                coordZ = 3485,
            ),
            ClueStep(
                402,
                ClueTier.EASY,
                ClueStepType.MAP,
                "Search the drawers in the upstairs of the Lumbridge General Store.",
                coordX = 3212,
                coordZ = 3244,
                plane = 1,
            ),
            ClueStep(
                403,
                ClueTier.EASY,
                ClueStepType.MAP,
                "Search the boxes in the goblin house near the Lumbridge mill.",
                coordX = 3190,
                coordZ = 3308,
            ),

            // ============================================================
            // CRYPTIC CLUES (riddle-style clues)
            // ============================================================

            ClueStep(
                501,
                ClueTier.EASY,
                ClueStepType.CRYPTIC,
                "In the place where wizards study, search the bookshelves for knowledge.",
                answer = "Wizards' Tower bookshelf",
                coordX = 3113,
                coordZ = 3158,
            ),
            ClueStep(
                502,
                ClueTier.EASY,
                ClueStepType.CRYPTIC,
                "Where miners dig for gold, search the crates for treasure.",
                answer = "Al Kharid mine crate",
                coordX = 3298,
                coordZ = 3294,
            ),
            ClueStep(
                503,
                ClueTier.EASY,
                ClueStepType.CRYPTIC,
                "In the church where prayers are heard, search the crates for a reward.",
                answer = "Lumbridge Church crate",
                coordX = 3244,
                coordZ = 3206,
            ),
            ClueStep(
                504,
                ClueTier.EASY,
                ClueStepType.CRYPTIC,
                "Where the blacksmith works his trade, search the crates for a clue.",
                answer = "Varrock Anvil crate",
                coordX = 3188,
                coordZ = 3426,
            ),
        )
}
