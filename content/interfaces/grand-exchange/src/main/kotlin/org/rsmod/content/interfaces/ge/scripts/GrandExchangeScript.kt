package org.rsmod.content.interfaces.ge.scripts

import jakarta.inject.Inject
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.objs
import org.rsmod.api.config.refs.varps
import org.rsmod.api.market.MarketPrices
import org.rsmod.api.player.dialogue.Dialogue
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.api.script.onIfModalButton
import org.rsmod.api.script.onOpNpc1
import org.rsmod.api.script.onOpNpc2
import org.rsmod.api.utils.format.formatAmount
import org.rsmod.content.interfaces.ge.configs.ge_components
import org.rsmod.content.interfaces.ge.configs.ge_npcs
import org.rsmod.game.entity.Npc
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

private const val GE_OFFER_TYPE_BUY = 0
private const val GE_OFFER_TYPE_SELL = 1

class GrandExchangeScript
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val marketPrices: MarketPrices,
    private val objRepo: ObjRepository,
) : PluginScript() {

    private val UnpackedObjType.guidePrice: Int
        get() = marketPrices[this] ?: cost

    override fun ScriptContext.startup() {
        // GE clerk interactions
        for (clerk in listOf(ge_npcs.clerk_1, ge_npcs.clerk_2, ge_npcs.clerk_3, ge_npcs.clerk_4)) {
            onOpNpc1(clerk) { exchangeWithClerk(it.npc) }
            onOpNpc2(clerk) { talkToClerk(it.npc) }
        }

        // Interface buttons
        onIfModalButton(ge_components.setup_confirm) { confirmOffer() }
        onIfModalButton(ge_components.details_collect) { closeGe() }
        onIfModalButton(ge_components.collectall) { closeGe() }
        onIfModalButton(ge_components.back) { closeGe() }

        // Slot buttons (open inline offer creation dialogue when clicked)
        for (slot in
            listOf(
                ge_components.index_0,
                ge_components.index_1,
                ge_components.index_2,
                ge_components.index_3,
                ge_components.index_4,
                ge_components.index_5,
                ge_components.index_6,
                ge_components.index_7,
            )) {
            onIfModalButton(slot) { openOfferDialogue() }
        }
    }

    // ---- Clerk op1: Exchange ----

    private suspend fun ProtectedAccess.exchangeWithClerk(npc: Npc) {
        ifOpenMainSidePair(main = interfaces.ge_exchange_main, side = interfaces.ge_exchange_side)
        startDialogue(npc) { exchangeMenu() }
    }

    private suspend fun Dialogue.exchangeMenu() {
        val choice =
            choice3(
                "I'd like to buy something.",
                1,
                "I'd like to sell something.",
                2,
                "Nothing, thanks.",
                3,
            )
        when (choice) {
            1 -> buyOffer()
            2 -> sellOffer()
        // choice 3: nothing
        }
    }

    private suspend fun Dialogue.buyOffer() {
        val item = access.objDialog("What would you like to buy?")
        val type = objTypes[item]
        val guidePrice = type.guidePrice
        if (guidePrice <= 0) {
            chatNpc(confused, "I'm afraid that item isn't tradeable.")
            return
        }

        val maxAffordable = access.invTotal(access.inv, objs.coins) / guidePrice
        if (maxAffordable <= 0) {
            chatNpc(
                worried,
                "You don't have enough coins to buy even one ${type.name}. " +
                    "The guide price is ${guidePrice.formatAmount} coins each.",
            )
            return
        }

        val qty = access.countDialog("How many would you like to buy? (max $maxAffordable)")
        if (qty <= 0) return
        val actualQty = minOf(qty, maxAffordable)
        val totalCost = actualQty * guidePrice.toLong()

        access.invDel(access.inv, objs.coins, totalCost.toInt())
        access.invAddOrDrop(objRepo, item, actualQty)

        chatNpc(
            happy,
            "Here you go! $actualQty x ${type.name} for ${totalCost.formatAmount} coins.",
        )
    }

    private suspend fun Dialogue.sellOffer() {
        val obj = access.objDialog("What would you like to sell?")
        val type = objTypes[obj]
        val guidePrice = type.guidePrice

        if (guidePrice <= 0 || !type.isTradeable()) {
            chatNpc(confused, "I'm afraid that item isn't tradeable.")
            return
        }

        val heldQty = access.invTotal(access.inv, obj)
        if (heldQty <= 0) {
            chatNpc(worried, "You don't have any ${type.name} to sell.")
            return
        }

        val qty = access.countDialog("How many would you like to sell? (max $heldQty)")
        if (qty <= 0) return
        val actualQty = minOf(qty, heldQty)
        val totalEarned = actualQty * guidePrice.toLong()

        access.invDel(access.inv, obj, actualQty)
        access.invAddOrDrop(objRepo, objs.coins, totalEarned.toInt())

        chatNpc(happy, "Sold! ${actualQty}x ${type.name} for ${totalEarned.formatAmount} coins.")
    }

    // ---- Offer setup_confirm: CS2-driven offer path ----

    /**
     * Handles the "Confirm offer" button in the ge_offers setup panel. The CS2 sets
     * `ge_last_offer_item`, `ge_last_offer_quantity`, and `ge_last_offer_type` varps before the
     * player clicks this button.
     */
    private fun ProtectedAccess.confirmOffer() {
        val itemId = player.vars[varps.ge_last_offer_item]
        val quantity = player.vars[varps.ge_last_offer_quantity]
        val offerType = player.vars[varps.ge_last_offer_type]

        if (itemId <= 0 || quantity <= 0) {
            mes("Please select an item and quantity before confirming.")
            return
        }

        val objType =
            objTypes[itemId]
                ?: run {
                    mes("Unknown item ID: $itemId.")
                    return
                }
        val guidePrice = objType.guidePrice

        when (offerType) {
            GE_OFFER_TYPE_BUY -> {
                val totalCost = quantity * guidePrice.toLong()
                val heldGp = invTotal(inv, objs.coins)
                if (heldGp < totalCost) {
                    mes("You don't have enough coins to buy $quantity x ${objType.name}.")
                    return
                }
                invDel(inv, objs.coins, totalCost.toInt())
                invAddOrDrop(objRepo, objType, quantity)
                mes("Bought ${quantity}x ${objType.name} for ${totalCost.formatAmount} coins.")
            }
            GE_OFFER_TYPE_SELL -> {
                val heldQty = invTotal(inv, objType)
                if (heldQty < quantity) {
                    mes("You don't have $quantity x ${objType.name} to sell.")
                    return
                }
                val totalEarned = quantity * guidePrice.toLong()
                invDel(inv, objType, quantity)
                invAddOrDrop(objRepo, objs.coins, totalEarned.toInt())
                mes("Sold ${quantity}x ${objType.name} for ${totalEarned.formatAmount} coins.")
            }
            else -> mes("Unknown offer type.")
        }

        ifCloseSub(interfaces.ge_exchange_main)
        ifCloseSub(interfaces.ge_exchange_side)
    }

    // ---- Slot button: inline offer creation ----

    private suspend fun ProtectedAccess.openOfferDialogue() {
        startDialogue { slotOfferMenu() }
    }

    private suspend fun Dialogue.slotOfferMenu() {
        val choice = choice2("Buy an item.", 1, "Sell an item.", 2)
        when (choice) {
            1 -> buyOffer()
            2 -> sellOffer()
        }
    }

    // ---- Utilities ----

    private fun ProtectedAccess.closeGe() {
        ifCloseSub(interfaces.ge_exchange_main)
        ifCloseSub(interfaces.ge_exchange_side)
    }

    // ---- Talk-to dialogue (op2) ----

    private suspend fun ProtectedAccess.talkToClerk(npc: Npc) {
        startDialogue(npc) { clerkInfo() }
    }

    private suspend fun Dialogue.clerkInfo() {
        chatNpc(
            happy,
            "Welcome to the Grand Exchange! I can buy and sell almost anything for you. " +
                "All trades are completed instantly at the guide price.",
        )
    }
}

private fun UnpackedObjType.isTradeable(): Boolean = !members || true // PS: all items tradeable
