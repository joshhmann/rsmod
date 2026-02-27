package org.rsmod.content.interfaces.musicplayer

/**
 * F2P Music Tracks for RSMod v2.
 *
 * This file documents all Free-to-Play music tracks available in OSRS. The actual music unlocking
 * is handled by the core music system via dbrows/dbtables from the cache. This serves as a
 * reference for F2P track names and their unlock locations.
 *
 * Note: The music system automatically unlocks tracks when players enter the associated areas (via
 * onArea triggers in MusicAreaScript).
 */
public object MusicTracksF2P {
    /** Core F2P Area Tracks - automatically unlocked when entering areas. */
    public const val LUMBRIDGE: String = "Lumbridge"
    public const val FLUTE_SALAD: String = "Flute Salad"
    public const val DREAM: String = "Dream"
    public const val BOOK_OF_SPELLS: String = "Book of Spells"
    public const val HARMONY: String = "Harmony"
    public const val AUTUMN_VOYAGE: String = "Autumn Voyage"
    public const val YESTERYEAR: String = "Yesteryear"
    public const val HARMONY_2: String = "Harmony 2"

    public const val VARROCK: String = "Adventure"
    public const val MEDIEVAL: String = "Medieval"
    public const val GARDEN: String = "Garden"
    public const val SPIRIT: String = "Spirit"
    public const val GREATNESS: String = "Greatness"
    public const val EXPANSE: String = "Expanse"
    public const val STILL_NIGHT: String = "Still Night"
    public const val DOORWAYS: String = "Doorways"

    public const val FALADOR: String = "Arrival"
    public const val FANFARE: String = "Fanfare"
    public const val WORKSHOP: String = "Workshop"
    public const val LIGHTNESS: String = "Lightness"
    public const val NIGHTFALL: String = "Nightfall"
    public const val MILES_AWAY: String = "Miles Away"
    public const val LONG_WAY_HOME: String = "Long Way Home"

    public const val DRAYNOR: String = "Start"
    public const val UNKNOWN_LAND: String = "Unknown Land"
    public const val WANDER: String = "Wander"

    public const val PORT_SARIM: String = "Sea Shanty 2"
    public const val TOMORROW: String = "Tomorrow"

    public const val AL_KHARID: String = "Al Kharid"
    public const val ARABIAN: String = "Arabian"
    public const val ARABIAN_2: String = "Arabian 2"

    public const val WILDERNESS: String = "Wilderness"
    public const val WILDERNESS_2: String = "Wilderness 2"
    public const val WILDERNESS_3: String = "Wilderness 3"
    public const val DEEP_WILDY: String = "Deep Wildy"
    public const val SCAPE_WILD: String = "Scape Wild"
    public const val SCAPE_SAD: String = "Scape Sad"
    public const val WILD_ISLE: String = "Wild Isle"
    public const val WILD_SIDE: String = "Wild Side"
    public const val WILDWOOD: String = "Wildwood"
    public const val UNDERGROUND: String = "Underground"
    public const val UNDERCURRENT: String = "Undercurrent"
    public const val TROUBLED: String = "Troubled"
    public const val VENOMOUS: String = "Venomous"
    public const val WITCHING: String = "Witching"
    public const val WONDER: String = "Wonder"
    public const val REGAL: String = "Regal"
    public const val SHINING: String = "Shining"
    public const val MOODY: String = "Moody"
    public const val DARK: String = "Dark"
    public const val DANGEROUS: String = "Dangerous"
    public const val CLOSE_QUARTERS: String = "Close Quarters"
    public const val ARMY_OF_DARKNESS: String = "Army of Darkness"
    public const val CRYSTAL_SWORD: String = "Crystal Sword"
    public const val EVERLASTING_FIRE: String = "Everlasting Fire"
    public const val FORBIDDEN: String = "Forbidden"
    public const val FAITHLESS: String = "Faithless"
    public const val DEAD_CAN_DANCE: String = "Dead Can Dance"
    public const val INSPIRATION: String = "Inspiration"

    public const val BARBARIAN_VILLAGE: String = "Barbarianism"

    public const val GOBLIN_VILLAGE: String = "Goblin Village"

    public const val EDGE_VILLAGE: String = "Forever"

    public const val RIMMINGTON: String = "Attention"
    public const val EMPEROR: String = "Emperor"

    public const val KARAMJA: String = "Jungle Island"
    public const val MUSA_POINT: String = "Sea Shanty"

    public const val TAVERLEY: String = "Horizon"
    public const val SPLENDOUR: String = "Splendour"
    public const val DUNJUN: String = "Dunjun"

    public const val ICE_MOUNTAIN: String = "Alone"
    public const val DWARF_THEME: String = "Dwarf Theme"

    public const val DRAYNOR_MANOR: String = "Spooky"
    public const val TIPTOE: String = "Tiptoe"

    public const val WIZARDS_TOWER: String = "Vision"

    public const val BLACK_KNIGHTS_FORTRESS: String = "Knightmare"

    /** Dungeon Tracks. */
    public const val SCAPE_CAVE: String = "Scape Cave"
    public const val CAVE_BACKGROUND: String = "Cave Background"
    public const val DOWN_BELOW: String = "Down Below"
    public const val CELLAR_SONG: String = "Cellar Song"
    public const val DANGEROUS_ROAD: String = "Dangerous Road"
    public const val STARLIGHT: String = "Starlight"

    /** Runecrafting Altar Tracks. */
    public const val RUNE_ESSENCE: String = "Rune Essence"
    public const val DOWN_TO_EARTH: String = "Down to Earth"
    public const val HEART_AND_MIND: String = "Heart and Mind"
    public const val SERENE: String = "Serene"
    public const val QUEST: String = "Quest"
    public const val ZEALOT: String = "Zealot"
    public const val MIRACLE_DANCE: String = "Miracle Dance"

    /** Quest Tracks. */
    public const val ATTACK_2: String = "Attack 2"
    public const val EYE_OF_THE_STORM: String = "Eye of the Storm"
    public const val THE_MAZE: String = "The Maze"
    public const val THE_SHADOW: String = "The Shadow"
    public const val DELRITH: String = "Delrith"
    public const val WALLY_THE_HERO: String = "Wally the Hero"
    public const val TOO_MANY_COOKS: String = "Too Many Cooks..."

    /** Random Event Tracks. */
    public const val ARTISTRY: String = "Artistry"
    public const val CORPORAL_PUNISHMENT: String = "Corporal Punishment"
    public const val FROGLAND: String = "Frogland"
    public const val HEAD_TO_HEAD: String = "Head to Head"
    public const val IN_THE_CLINK: String = "In the Clink"
    public const val PHEASANT_PEASANT: String = "Pheasant Peasant"
    public const val PINBALL_WIZARD: String = "Pinball Wizard"
    public const val THE_QUIZMASTER: String = "The Quizmaster"
    public const val SCHOOLS_OUT: String = "School's Out"
    public const val TIME_OUT: String = "Time Out"

    /** Minigame Tracks. */
    public const val CASTLE_WARS: String = "Castle Wars"
    public const val CLAN_WARS: String = "Clan Wars"
    public const val WARPATH: String = "Warpath"
    public const val READY_FOR_BATTLE: String = "Ready for Battle"
    public const val LAST_MAN_STANDING: String = "Last Man Standing"

    /** Stronghold of Security Tracks. */
    public const val DANCE_OF_DEATH: String = "Dance of Death"
    public const val DOGS_OF_WAR: String = "Dogs of War"
    public const val FOOD_FOR_THOUGHT: String = "Food for Thought"
    public const val MALADY: String = "Malady"

    /** Automatic Tracks (unlocked on login). */
    public const val SCAPE_MAIN: String = "Scape Main"
    public const val SCAPE_ORIGINAL: String = "Scape Original"
    public const val SCAPE_FIVE: String = "Scape Five"
    public const val SCAPE_GROUND: String = "Scape Ground"
    public const val SCAPE_HUNTER: String = "Scape Hunter"
    public const val SCAPE_CRYSTAL: String = "Scape Crystal"
    public const val SCAPE_APE: String = "Scape Ape"
    public const val SCAPE_SAIL: String = "Scape Sail"
    public const val SCAPE_SCARED: String = "Scape Scared"
    public const val SCAPE_HOME: String = "Scape Home"
    public const val GNOME_VILLAGE_PARTY: String = "Gnome Village Party"
    public const val CREST_OF_A_WAVE: String = "Crest of a Wave"
    public const val ORGAN_MUSIC_1: String = "Organ Music 1"
    public const val ORGAN_MUSIC_2: String = "Organ Music 2"
    public const val SHATTERED_RELICS: String = "Shattered Relics"

    /** Grand Exchange. */
    public const val THE_TRADE_PARADE: String = "The Trade Parade"

    /** Varrock Museum. */
    public const val LOOKING_BACK: String = "Looking Back"

    /** Other F2P Tracks. */
    public const val MUDSKIPPER_MELODY: String = "Mudskipper Melody"
    public const val MELODRAMA: String = "Melodrama"
    public const val GRUMPY: String = "Grumpy"
    public const val JUNGLE_HUNT: String = "Jungle Hunt"
    public const val THE_ENCLAVE: String = "The Enclave"
    public const val ON_THE_SHORE: String = "On the Shore"
    public const val THE_RUINS_OF_CAMDOZAAL: String = "The Ruins of Camdozaal"
    public const val BARBARIAN_WORKOUT: String = "Barbarian Workout"
    public const val RACE_AGAINST_THE_CLOCK: String = "Race Against the Clock"
    public const val MUSEUM_MEDLEY: String = "Museum Medley"
    public const val SHINE: String = "Shine"
    public const val REST_IN_PEACE: String = "Rest in Peace"
    public const val CLANLINESS: String = "Clanliness"
    public const val RHAPSODY: String = "Rhapsody"
    public const val THE_EMIRS_ARENA: String = "The Emir's Arena"
    public const val EVIL_BOBS_ISLAND: String = "Evil Bob's Island"
    public const val ROOTS_AND_FLUTES: String = "Roots and Flutes"
    public const val NEWBIE_MELODY: String = "Newbie Melody"
    public const val EGYPT: String = "Egypt"

    /**
     * Total count of F2P music tracks (approximately). This includes all automatically unlocked and
     * area-unlocked tracks.
     */
    public const val TOTAL_F2P_TRACKS: Int = 154

    /** List of all F2P music track names for reference. */
    public val ALL_F2P_TRACKS: List<String> =
        listOf(
            // Lumbridge
            LUMBRIDGE,
            FLUTE_SALAD,
            DREAM,
            BOOK_OF_SPELLS,
            HARMONY,
            AUTUMN_VOYAGE,
            YESTERYEAR,
            HARMONY_2,

            // Varrock
            VARROCK,
            MEDIEVAL,
            GARDEN,
            SPIRIT,
            GREATNESS,
            EXPANSE,
            STILL_NIGHT,
            DOORWAYS,

            // Falador
            FALADOR,
            FANFARE,
            WORKSHOP,
            LIGHTNESS,
            NIGHTFALL,
            MILES_AWAY,
            LONG_WAY_HOME,

            // Draynor
            DRAYNOR,
            UNKNOWN_LAND,
            WANDER,

            // Port Sarim
            PORT_SARIM,
            TOMORROW,

            // Al Kharid
            AL_KHARID,
            ARABIAN,
            ARABIAN_2,

            // Wilderness
            WILDERNESS,
            WILDERNESS_2,
            WILDERNESS_3,
            DEEP_WILDY,
            SCAPE_WILD,
            SCAPE_SAD,
            WILD_ISLE,
            WILD_SIDE,
            WILDWOOD,
            UNDERGROUND,
            UNDERCURRENT,
            TROUBLED,
            VENOMOUS,
            WITCHING,
            WONDER,
            REGAL,
            SHINING,
            MOODY,
            DARK,
            DANGEROUS,
            CLOSE_QUARTERS,
            ARMY_OF_DARKNESS,
            CRYSTAL_SWORD,
            EVERLASTING_FIRE,
            FORBIDDEN,
            FAITHLESS,
            DEAD_CAN_DANCE,
            INSPIRATION,

            // Barbarian Village
            BARBARIAN_VILLAGE,

            // Goblin Village
            GOBLIN_VILLAGE,

            // Edgeville
            EDGE_VILLAGE,

            // Rimmington
            RIMMINGTON,
            EMPEROR,

            // Karamja
            KARAMJA,
            MUSA_POINT,

            // Taverley
            TAVERLEY,
            SPLENDOUR,
            DUNJUN,

            // Ice Mountain
            ICE_MOUNTAIN,
            DWARF_THEME,

            // Draynor Manor
            DRAYNOR_MANOR,
            TIPTOE,

            // Wizards Tower
            WIZARDS_TOWER,

            // Black Knights Fortress
            BLACK_KNIGHTS_FORTRESS,

            // Dungeons
            SCAPE_CAVE,
            CAVE_BACKGROUND,
            DOWN_BELOW,
            CELLAR_SONG,
            DANGEROUS_ROAD,
            STARLIGHT,

            // Runecrafting Altars
            RUNE_ESSENCE,
            DOWN_TO_EARTH,
            HEART_AND_MIND,
            SERENE,
            QUEST,
            ZEALOT,
            MIRACLE_DANCE,

            // Quests
            ATTACK_2,
            EYE_OF_THE_STORM,
            THE_MAZE,
            THE_SHADOW,
            DELRITH,
            WALLY_THE_HERO,
            TOO_MANY_COOKS,

            // Random Events
            ARTISTRY,
            CORPORAL_PUNISHMENT,
            FROGLAND,
            HEAD_TO_HEAD,
            IN_THE_CLINK,
            PHEASANT_PEASANT,
            PINBALL_WIZARD,
            THE_QUIZMASTER,
            SCHOOLS_OUT,
            TIME_OUT,

            // Minigames
            CASTLE_WARS,
            CLAN_WARS,
            WARPATH,
            READY_FOR_BATTLE,
            LAST_MAN_STANDING,

            // Stronghold of Security
            DANCE_OF_DEATH,
            DOGS_OF_WAR,
            FOOD_FOR_THOUGHT,
            MALADY,

            // Automatic
            SCAPE_MAIN,
            SCAPE_ORIGINAL,
            SCAPE_FIVE,
            SCAPE_GROUND,
            SCAPE_HUNTER,
            SCAPE_CRYSTAL,
            SCAPE_APE,
            SCAPE_SAIL,
            SCAPE_SCARED,
            SCAPE_HOME,
            GNOME_VILLAGE_PARTY,
            CREST_OF_A_WAVE,
            ORGAN_MUSIC_1,
            ORGAN_MUSIC_2,
            SHATTERED_RELICS,

            // Other
            THE_TRADE_PARADE,
            LOOKING_BACK,
            MUDSKIPPER_MELODY,
            MELODRAMA,
            GRUMPY,
            JUNGLE_HUNT,
            THE_ENCLAVE,
            ON_THE_SHORE,
            THE_RUINS_OF_CAMDOZAAL,
            BARBARIAN_WORKOUT,
            RACE_AGAINST_THE_CLOCK,
            MUSEUM_MEDLEY,
            SHINE,
            REST_IN_PEACE,
            CLANLINESS,
            RHAPSODY,
            THE_EMIRS_ARENA,
            EVIL_BOBS_ISLAND,
            ROOTS_AND_FLUTES,
            NEWBIE_MELODY,
            EGYPT,
        )
}
