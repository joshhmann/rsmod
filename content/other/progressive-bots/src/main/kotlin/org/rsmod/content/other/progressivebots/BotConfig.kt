package org.rsmod.content.other.progressivebots

/**
 * Configuration for a single progressive bot.
 */
data class BotDef(
    val username: String,
    val planner: BotPlanner,
    val spawnX: Int,
    val spawnZ: Int,
    val spawnPlane: Int = 0,
    val male: Boolean = true,
    val head: Int = 0,
    val body: Int = 18,
    val legs: Int = 26,
    val skinColor: Int = 0,
)

/**
 * Reads bot definitions from an embedded list.
 * JSON file loading can be added later.
 */
object BotConfig {
    val bots: List<BotDef> by lazy {
        listOf(
            // — SKILLERS (25) —
            BotDef("fallenhero11", BotPlanner.Skiller, 3222, 3222),
            BotDef("steelchief29", BotPlanner.Skiller, 3230, 3215),
            BotDef("ancientcrusa", BotPlanner.Skiller, 3225, 3228),
            BotDef("warpeddragon", BotPlanner.Skiller, 3240, 3210),
            BotDef("goldvagabond", BotPlanner.Skiller, 3215, 3235),
            BotDef("divineknight", BotPlanner.Skiller, 3235, 3225),
            BotDef("fierceknight", BotPlanner.Skiller, 3200, 3240),
            BotDef("swiftpaladin", BotPlanner.Skiller, 3245, 3205),
            BotDef("savageranger", BotPlanner.Skiller, 3210, 3245),
            BotDef("savagemonk", BotPlanner.Skiller, 3232, 3218),
            BotDef("evilnomad", BotPlanner.Skiller, 3228, 3220),
            BotDef("steelsamurai", BotPlanner.Skiller, 3218, 3230),
            BotDef("ragingshaman", BotPlanner.Skiller, 3220, 3210),
            BotDef("toxiclord", BotPlanner.Skiller, 3242, 3222),
            BotDef("goldcrusade1", BotPlanner.Skiller, 3205, 3238),
            BotDef("swiftvindica", BotPlanner.Skiller, 3238, 3212),
            BotDef("phantomshama", BotPlanner.Skiller, 3212, 3242),
            BotDef("wiseninja263", BotPlanner.Skiller, 3248, 3208),
            BotDef("noblemage", BotPlanner.Skiller, 3208, 3248),
            BotDef("crazedangel", BotPlanner.Skiller, 3226, 3226),
            BotDef("lonemerc950", BotPlanner.Skiller, 3230, 3230),
            BotDef("fallensentin", BotPlanner.Skiller, 3215, 3215),
            BotDef("grimglad364", BotPlanner.Skiller, 3240, 3240),
            BotDef("ragingoutlaw", BotPlanner.Skiller, 3200, 3220),
            BotDef("zensentinel6", BotPlanner.Skiller, 3220, 3200),

            // — FIGHTERS (20) —
            BotDef("arcaneraider", BotPlanner.Fighter, 3222, 3222),
            BotDef("brokenrogue", BotPlanner.Fighter, 3230, 3215),
            BotDef("bloodraider", BotPlanner.Fighter, 3240, 3220),
            BotDef("darkhero", BotPlanner.Fighter, 3210, 3230),
            BotDef("ironhunter", BotPlanner.Fighter, 3225, 3218),
            BotDef("royalwarrior", BotPlanner.Fighter, 3235, 3228),
            BotDef("wildranger93", BotPlanner.Fighter, 3205, 3240),
            BotDef("chaossage340", BotPlanner.Fighter, 3242, 3210),
            BotDef("savagevagabo", BotPlanner.Fighter, 3218, 3235),
            BotDef("swiftpirate8", BotPlanner.Fighter, 3228, 3225),
            BotDef("stormdruid", BotPlanner.Fighter, 3200, 3215),
            BotDef("grimking", BotPlanner.Fighter, 3245, 3220),
            BotDef("mythicwarrio", BotPlanner.Fighter, 3212, 3240),
            BotDef("stormviking", BotPlanner.Fighter, 3238, 3205),
            BotDef("darkguard", BotPlanner.Fighter, 3220, 3235),
            BotDef("blazingsenti", BotPlanner.Fighter, 3230, 3212),
            BotDef("deadlyscout", BotPlanner.Fighter, 3208, 3245),
            BotDef("burninghawk2", BotPlanner.Fighter, 3248, 3218),
            BotDef("fierceduke", BotPlanner.Fighter, 3215, 3228),
            BotDef("bravehero", BotPlanner.Fighter, 3232, 3220),

            // — BALANCED (20) —
            BotDef("fallenprophe", BotPlanner.Balanced, 3222, 3222),
            BotDef("bravereaper5", BotPlanner.Balanced, 3235, 3210),
            BotDef("wisecrusade", BotPlanner.Balanced, 3210, 3235),
            BotDef("divinebaron", BotPlanner.Balanced, 3240, 3225),
            BotDef("cursedslayer", BotPlanner.Balanced, 3225, 3240),
            BotDef("lonedruid", BotPlanner.Balanced, 3205, 3220),
            BotDef("nobleoutlaw", BotPlanner.Balanced, 3238, 3215),
            BotDef("feralrogue", BotPlanner.Balanced, 3218, 3238),
            BotDef("steelviking", BotPlanner.Balanced, 3245, 3200),
            BotDef("noblehero", BotPlanner.Balanced, 3200, 3245),
            BotDef("bloodsage", BotPlanner.Balanced, 3228, 3228),
            BotDef("crazedsentin", BotPlanner.Balanced, 3215, 3210),
            BotDef("noblehawk860", BotPlanner.Balanced, 3242, 3232),
            BotDef("divinewolf93", BotPlanner.Balanced, 3230, 3242),
            BotDef("astralglad", BotPlanner.Balanced, 3210, 3218),
            BotDef("mightyprophe", BotPlanner.Balanced, 3220, 3240),
            BotDef("savagechief", BotPlanner.Balanced, 3240, 3218),
            BotDef("spectralpira", BotPlanner.Balanced, 3208, 3225),
            BotDef("warpedberser", BotPlanner.Balanced, 3225, 3208),
            BotDef("fallenvindic", BotPlanner.Balanced, 3232, 3235),

            // — SOCIAL (10) —
            BotDef("braveglad", BotPlanner.Social, 3222, 3222),
            BotDef("ironwolf", BotPlanner.Social, 3210, 3240),
            BotDef("holylord", BotPlanner.Social, 3240, 3210),
            BotDef("ancientreape", BotPlanner.Social, 3230, 3230),
            BotDef("silentscout", BotPlanner.Social, 3215, 3225),
            BotDef("firevagabond", BotPlanner.Social, 3228, 3215),
            BotDef("savageronin", BotPlanner.Social, 3205, 3230),
            BotDef("wildreaper", BotPlanner.Social, 3235, 3220),
            BotDef("chaosknight6", BotPlanner.Social, 3220, 3230),
            BotDef("fireghost", BotPlanner.Social, 3240, 3240),

            // — VENDORS (15) —
            BotDef("stormzealot", BotPlanner.Vendor, 3222, 3222),
            BotDef("zenlord", BotPlanner.Vendor, 3210, 3210),
            BotDef("stormreaper", BotPlanner.Vendor, 3235, 3235),
            BotDef("burningsamur", BotPlanner.Vendor, 3225, 3225),
            BotDef("twistedslaye", BotPlanner.Vendor, 3240, 3245),
            BotDef("loneduke49", BotPlanner.Vendor, 3218, 3218),
            BotDef("nobleranger", BotPlanner.Vendor, 3230, 3225),
            BotDef("burningwarri", BotPlanner.Vendor, 3205, 3205),
            BotDef("arcanespy", BotPlanner.Vendor, 3245, 3245),
            BotDef("bloodknight", BotPlanner.Vendor, 3222, 3230),
            BotDef("zenseeker585", BotPlanner.Vendor, 3215, 3240),
            BotDef("brokenseeker", BotPlanner.Vendor, 3238, 3218),
            BotDef("silentreaper", BotPlanner.Vendor, 3208, 3235),
            BotDef("evilreaper21", BotPlanner.Vendor, 3242, 3208),
            BotDef("loneghost620", BotPlanner.Vendor, 3210, 3220),

            // — PKERS (10) —
            BotDef("crazedwraith", BotPlanner.PKer, 3098, 3520, spawnPlane = 0),
            BotDef("ironsage717", BotPlanner.PKer, 3105, 3518, spawnPlane = 0),
            BotDef("grimtitan", BotPlanner.PKer, 3087, 3525, spawnPlane = 0),
            BotDef("mythicronin", BotPlanner.PKer, 3110, 3510, spawnPlane = 0),
            BotDef("toxicseeker", BotPlanner.PKer, 3095, 3530, spawnPlane = 0),
            BotDef("crystalmysti", BotPlanner.PKer, 3100, 3525, spawnPlane = 0),
            BotDef("fallenrogue", BotPlanner.PKer, 3085, 3515, spawnPlane = 0),
            BotDef("burningwarde", BotPlanner.PKer, 3115, 3520, spawnPlane = 0),
            BotDef("frostraider5", BotPlanner.PKer, 3090, 3535, spawnPlane = 0),
            BotDef("royalghost", BotPlanner.PKer, 3108, 3508, spawnPlane = 0),

            // — ROMANCE SCAMMERS (5) —
            BotDef("loveslave", BotPlanner.Social, 3222, 3222),
            BotDef("cutiepie", BotPlanner.Social, 3230, 3230),
            BotDef("sweetheart", BotPlanner.Social, 3215, 3215),
            BotDef("babydoll", BotPlanner.Social, 3240, 3240),
            BotDef("honeybun", BotPlanner.Social, 3225, 3225),
        )
    }
}
