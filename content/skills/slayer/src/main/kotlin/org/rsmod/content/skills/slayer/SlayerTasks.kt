package org.rsmod.content.skills.slayer

public enum class SlayerTask(
    public val taskName: String,
    public val npcNames: Set<String>,
    public val slayerLevel: Int = 1,
    public val weight: Int = 1,
    public val minAmount: Int = 15,
    public val maxAmount: Int = 30,
) {
    Banshees("Banshees", setOf("Banshee"), 15, 8),
    Bats("Bats", setOf("Bat", "Giant bat"), 1, 7),
    Bears("Bears", setOf("Black bear", "Grizzly bear", "Bear cub"), 1, 7),
    Birds("Birds", setOf("Chicken", "Duck", "Seagull", "Bird"), 1, 6),
    CaveBugs("Cave bugs", setOf("Cave bug"), 7, 8),
    CaveCrawlers("Cave crawlers", setOf("Cave crawler"), 10, 8),
    CaveSlime("Cave slime", setOf("Cave slime"), 17, 8),
    Cows("Cows", setOf("Cow", "Cow calf"), 1, 8),
    CrawlingHands("Crawling hands", setOf("Crawling Hand"), 5, 8),
    Dogs("Dogs", setOf("Dog", "Guard dog", "Wild dog", "Jackal"), 1, 7),
    Dwarves("Dwarves", setOf("Dwarf", "Chaos dwarf"), 1, 7),
    Ghosts("Ghosts", setOf("Ghost"), 1, 7),
    Goblins("Goblins", setOf("Goblin"), 1, 7),
    Icefiends("Icefiends", setOf("Icefiend"), 1, 8),
    Kalphites("Kalphites", setOf("Kalphite worker", "Kalphite soldier", "Kalphite guardian"), 1, 6),
    Lizards("Lizards", setOf("Lizard", "Small lizard", "Desert lizard"), 22, 8),
    Minotaurs("Minotaurs", setOf("Minotaur"), 1, 7),
    Monkeys("Monkeys", setOf("Monkey"), 1, 6),
    Rats("Rats", setOf("Rat", "Giant rat"), 1, 7),
    Scorpions("Scorpions", setOf("Scorpion", "King scorpion"), 1, 7),
    Skeletons("Skeletons", setOf("Skeleton"), 1, 7),
    Spiders("Spiders", setOf("Spider", "Giant spider"), 1, 6),
    Wolves("Wolves", setOf("Wolf", "White wolf"), 1, 7),
    Zombies("Zombies", setOf("Zombie"), 1, 7);

    public companion object {
        public val values: Array<SlayerTask> = values()

        public operator fun get(index: Int): SlayerTask? = values.getOrNull(index)
    }
}
