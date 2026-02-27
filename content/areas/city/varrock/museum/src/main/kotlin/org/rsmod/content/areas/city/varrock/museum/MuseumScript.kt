package org.rsmod.content.areas.city.varrock.museum

import jakarta.inject.Inject
import org.rsmod.api.script.onOpLoc1
import org.rsmod.content.areas.city.varrock.museum.configs.museum_locs
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class MuseumScript @Inject constructor() : PluginScript() {
    override fun ScriptContext.startup() {
        // Ground floor timeline displays
        onOpLoc1(museum_locs.display_saradomin_symbol_old) {
            player.say("An ancient symbol of Saradomin, from the early days of human settlement.")
        }
        onOpLoc1(museum_locs.display_saradomin_symbol_ancient) {
            player.say("A symbol of Saradomin from the Second Age, predating the God Wars.")
        }
        onOpLoc1(museum_locs.display_pottery) {
            player.say(
                "Ancient pottery fragments from the Dig Site, dating back thousands of years."
            )
        }
        onOpLoc1(museum_locs.display_talisman) {
            player.say("An elemental talisman used by early practitioners of magic.")
        }
        onOpLoc1(museum_locs.display_tablet) {
            player.say("A clay tablet with ancient writing, yet to be fully translated.")
        }
        onOpLoc1(museum_locs.display_coin_senntisten) {
            player.say("A coin from Senntisten, the ancient capital of the Zarosian empire.")
        }
        onOpLoc1(museum_locs.display_coin_saranthium) {
            player.say("A coin from Saranthium, a city built after the fall of Senntisten.")
        }

        // Natural History displays
        onOpLoc1(museum_locs.display_kalphite_queen) {
            player.say(
                "The Kalphite Queen - a fearsome insectoid creature from the Kharidian Desert."
            )
        }

        // Display cases
        onOpLoc1(museum_locs.displaycase_base) {
            player.say("A display case containing ancient artifacts.")
        }
    }
}
