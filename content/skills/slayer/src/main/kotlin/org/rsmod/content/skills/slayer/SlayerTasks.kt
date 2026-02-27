package org.rsmod.content.skills.slayer

public enum class SlayerTask(
    public val taskName: String,
    public val npcNames: Set<String>,
    public val slayerLevel: Int = 1,
    public val combatLevel: Int = 1,
    public val weight: Int = 1,
    public val minAmount: Int = 15,
    public val maxAmount: Int = 30,
) {
    // Low level tasks (Turael - combat req 0)
    Bats("Bats", setOf("Bat", "Giant bat"), 1, 1, 7),
    Bears("Bears", setOf("Black bear", "Grizzly bear", "Bear cub"), 1, 1, 7),
    Birds("Birds", setOf("Chicken", "Duck", "Seagull", "Bird"), 1, 1, 6),
    Cows("Cows", setOf("Cow", "Cow calf"), 1, 1, 8),
    Dogs("Dogs", setOf("Dog", "Guard dog", "Wild dog", "Jackal"), 1, 1, 7),
    Dwarves("Dwarves", setOf("Dwarf", "Chaos dwarf"), 1, 1, 7),
    Ghosts("Ghosts", setOf("Ghost", "GhostWolf"), 1, 1, 7),
    Goblins("Goblins", setOf("Goblin", "Goblin red", "Goblin green"), 1, 1, 8),
    Icefiends("Icefiends", setOf("Icefiend"), 1, 1, 8),
    Minotaurs("Minotaurs", setOf("Minotaur"), 1, 1, 7),
    Monkeys("Monkeys", setOf("Monkey", "Karamjan monkey"), 1, 1, 6),
    Rats("Rats", setOf("Rat", "Giant rat", "Dungeon rat"), 1, 1, 7),
    Scorpions("Scorpions", setOf("Scorpion", "King scorpion", "Poison Scorpion"), 1, 1, 7),
    Skeletons("Skeletons", setOf("Skeleton", "Skeleton unarmed", "Skeleton armed"), 1, 1, 7),
    Spiders("Spiders", setOf("Spider", "Giant spider", "Deadly red spider"), 1, 1, 6),
    Wolves("Wolves", setOf("Wolf", "White wolf", "Desert wolf"), 1, 1, 7),
    Zombies("Zombies", setOf("Zombie"), 1, 1, 7),

    // Level 5+ tasks
    CrawlingHands("Crawling hands", setOf("Crawling Hand"), 5, 1, 8, 10, 25),

    // Level 7+ tasks
    CaveBugs("Cave bugs", setOf("Cave bug"), 7, 1, 8, 10, 25),

    // Level 10+ tasks
    CaveCrawlers("Cave crawlers", setOf("Cave crawler"), 10, 1, 8, 10, 25),

    // Level 15+ tasks
    Banshees("Banshees", setOf("Banshee"), 15, 1, 8, 15, 30),

    // Level 17+ tasks
    CaveSlime("Cave slime", setOf("Cave slime"), 17, 1, 8, 10, 25),

    // Level 20+ tasks (Mazchna - combat req 20)
    Rockslugs("Rockslugs", setOf("Rockslug"), 20, 1, 8, 10, 25),

    // Level 22+ tasks
    Lizards("Lizards", setOf("Lizard", "Small lizard", "Desert lizard"), 22, 1, 8, 15, 30),

    // Level 25+ tasks
    Cockatrice("Cockatrice", setOf("Cockatrice"), 25, 1, 7, 15, 30),

    // Level 30+ tasks (Vannaka - combat req 40)
    Pyrefiends("Pyrefiends", setOf("Pyrefiend"), 30, 1, 7, 20, 40),

    // Level 32+ tasks
    Mogres("Mogres", setOf("Mogre"), 32, 1, 6, 15, 30),

    // Level 33+ tasks
    HarpieBugSwarms("Harpie bug swarms", setOf("Harpie Bug Swarm"), 33, 1, 8, 20, 40),

    // Level 35+ tasks
    WallBeasts("Wall beasts", setOf("Wall beast"), 35, 1, 7, 10, 25),

    // Level 39+ tasks
    Killerwatts("Killerwatts", setOf("Killerwatt"), 37, 1, 6, 15, 30),

    // Level 40+ tasks (Chaeldar - combat req 70)
    Molanisks("Molanisks", setOf("Molanisk"), 39, 1, 7, 15, 30),
    FeverSpiders("Fever spiders", setOf("Fever spider"), 42, 1, 7, 15, 30),
    DustDevils("Dust devils", setOf("Dust devil"), 65, 1, 5, 15, 30),

    // Level 45+ tasks
    InfernalMages("Infernal mages", setOf("Infernal Mage"), 45, 1, 6, 15, 30),

    // Level 47+ tasks
    Bloodvelds("Bloodvelds", setOf("Bloodveld"), 50, 1, 6, 20, 40),

    // Level 50+ tasks
    Jellies("Jellies", setOf("Jelly"), 52, 1, 6, 20, 40),
    AberrantSpectres("Aberrant spectres", setOf("Aberrant spectre"), 60, 1, 5, 15, 30),

    // Level 55+ tasks
    WarpedTortoises("Warped tortoises", setOf("Warped tortoise"), 56, 1, 6, 20, 40),

    // Level 58+ tasks
    WarpedBirds("Warped birds", setOf("Warped bird"), 56, 1, 7, 20, 40),

    // Level 60+ tasks
    Kurasks("Kurasks", setOf("Kurask"), 70, 1, 4, 15, 30),

    // Level 65+ tasks
    SkeletalWyverns("Skeletal wyverns", setOf("Skeletal wyvern"), 72, 1, 3, 10, 20),

    // Level 70+ tasks
    Gargoyles("Gargoyles", setOf("Gargoyle"), 75, 1, 5, 10, 25),

    // Level 75+ tasks
    Nechryael("Nechryael", setOf("Nechryael"), 80, 1, 4, 10, 25),

    // Level 80+ tasks
    SpiritualCreatures(
        "Spiritual creatures",
        setOf("Spiritual ranger", "Spiritual warrior", "Spiritual mage"),
        63,
        1,
        5,
        15,
        30,
    ),

    // Level 85+ tasks (Nieve/Steve - combat req 85)
    DarkBeasts("Dark beasts", setOf("Dark beast"), 90, 1, 4, 10, 20),

    // Level 90+ tasks (Duradel - combat req 100)
    CaveHorrors("Cave horrors", setOf("Cave horror"), 58, 1, 5, 15, 30),
    SmokeDevils("Smoke devils", setOf("Smoke devil"), 93, 1, 3, 10, 20),

    // Special tasks
    Kalphites(
        "Kalphites",
        setOf("Kalphite worker", "Kalphite soldier", "Kalphite guardian", "Kalphite Queen"),
        1,
        1,
        6,
        30,
        60,
    ),
    Dagannoth("Dagannoth", setOf("Dagannoth"), 1, 1, 6, 20, 40),
    FireGiants("Fire giants", setOf("Fire giant"), 1, 1, 6, 20, 40),
    Trolls("Trolls", setOf("Troll", "Mountain troll", "River troll"), 1, 1, 6, 20, 40),
    HillGiants("Hill giants", setOf("Hill giant"), 1, 1, 7, 20, 40),
    MossGiants("Moss giants", setOf("Moss giant"), 1, 1, 6, 20, 40),
    IceGiants("Ice giants", setOf("Ice giant"), 1, 1, 6, 20, 40),
    Ogres("Ogres", setOf("Ogre", "Ogre chieftain", "Enclave guard"), 1, 1, 6, 20, 40),
    Hellhounds("Hellhounds", setOf("Hellhound"), 1, 1, 5, 20, 40),
    LesserDemons("Lesser demons", setOf("Lesser demon"), 1, 1, 5, 20, 40),
    GreaterDemons("Greater demons", setOf("Greater demon"), 1, 1, 4, 15, 30),
    BlackDemons("Black demons", setOf("Black demon"), 1, 1, 4, 15, 30),
    GreenDragons("Green dragons", setOf("Green dragon"), 1, 1, 3, 10, 20),
    BlueDragons("Blue dragons", setOf("Blue dragon"), 1, 1, 3, 10, 20),
    RedDragons("Red dragons", setOf("Red dragon"), 1, 1, 2, 10, 20),
    BlackDragons("Black dragons", setOf("Black dragon"), 1, 1, 2, 5, 15),
    IronDragons("Iron dragons", setOf("Iron dragon"), 1, 1, 3, 10, 20),
    SteelDragons("Steel dragons", setOf("Steel dragon"), 1, 1, 2, 10, 20),
    BronzeDragons("Bronze dragons", setOf("Bronze dragon"), 1, 1, 3, 10, 20),
    MithrilDragons("Mithril dragons", setOf("Mithril dragon"), 1, 1, 2, 5, 15),
    Aviansies("Aviansies", setOf("Aviansie"), 1, 1, 4, 15, 30),
    Turoths("Turoths", setOf("Turoth"), 55, 1, 5, 20, 40);

    public companion object {
        public val values: Array<SlayerTask> = values()

        public operator fun get(index: Int): SlayerTask? = values.getOrNull(index)
    }
}
