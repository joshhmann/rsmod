package org.rsmod.content.mechanics.trade

import jakarta.inject.Inject
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.invs
import org.rsmod.api.player.output.ClientScripts.interfaceInvInit
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.script.advanced.onOpPlayer4
import org.rsmod.api.script.onIfClose
import org.rsmod.api.script.onIfModalButton
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.interf.IfEvent
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

public class TradeScript
@Inject
constructor(
    private val eventBus: EventBus,
    private val protectedAccess: ProtectedAccessLauncher,
    private val objTypes: ObjTypeList,
) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpPlayer4 {
            val partner = it.target
            if (partner == player) {
                return@onOpPlayer4
            }
            if (player.isTrading() || partner.isTrading()) {
                mes("That player is busy.")
                return@onOpPlayer4
            }

            val accepted = player.consumeTradeRequestFrom(partner)
            if (!accepted) {
                player.setTradeRequest(partner)
                mes("Sending trade request...")
                partner.mes("${player.username} wishes to trade with you.")
                return@onOpPlayer4
            }

            val session = TradeSession(player, partner)
            player.linkTradeSession(partner, session)
            protectedAccess.launch(player) { openOfferScreen(session) }
            protectedAccess.launch(partner) { openOfferScreen(session) }
        }

        onIfModalButton(TradeComponents.offer_side_inventory) {
            val session = player.tradeSession ?: return@onIfModalButton
            if (session.stage != TradeStage.Offer) {
                return@onIfModalButton
            }
            val partner = session.other(player) ?: return@onIfModalButton
            val slot = it.comsub
            val amount =
                when (it.op) {
                    IfButtonOp.Op1 -> 1
                    IfButtonOp.Op2 -> 5
                    IfButtonOp.Op3 -> 10
                    IfButtonOp.Op4 -> Int.MAX_VALUE
                    IfButtonOp.Op5 -> countDialog()
                    else -> return@onIfModalButton
                }
            moveFromInventoryToOffer(session, partner, slot, amount)
        }

        onIfModalButton(TradeComponents.offer_player_items) {
            val session = player.tradeSession ?: return@onIfModalButton
            if (session.stage != TradeStage.Offer) {
                return@onIfModalButton
            }
            val partner = session.other(player) ?: return@onIfModalButton
            val slot = it.comsub
            val amount =
                when (it.op) {
                    IfButtonOp.Op1 -> 1
                    IfButtonOp.Op2 -> 5
                    IfButtonOp.Op3 -> 10
                    IfButtonOp.Op4 -> Int.MAX_VALUE
                    IfButtonOp.Op5 -> countDialog()
                    else -> return@onIfModalButton
                }
            moveFromOfferToInventory(session, partner, slot, amount)
        }

        onIfModalButton(TradeComponents.offer_accept) {
            val session = player.tradeSession ?: return@onIfModalButton
            if (session.stage != TradeStage.Offer) {
                return@onIfModalButton
            }
            val partner = session.other(player) ?: return@onIfModalButton
            acceptOfferStage(session, partner)
        }

        onIfModalButton(TradeComponents.offer_decline) {
            val session = player.tradeSession ?: return@onIfModalButton
            cancelTrade(session, "Declined trade.")
        }

        onIfModalButton(TradeComponents.confirm_accept) {
            val session = player.tradeSession ?: return@onIfModalButton
            if (session.stage != TradeStage.Confirm) {
                return@onIfModalButton
            }
            val partner = session.other(player) ?: return@onIfModalButton
            acceptConfirmStage(session, partner)
        }

        onIfModalButton(TradeComponents.confirm_decline) {
            val session = player.tradeSession ?: return@onIfModalButton
            cancelTrade(session, "Declined trade.")
        }

        onIfClose(interfaces.trade_main) {
            val session = player.tradeSession ?: return@onIfClose
            protectedAccess.launch(player) { cancelTrade(session, "Declined trade.") }
        }

        onIfClose(interfaces.trade_confirm) {
            val session = player.tradeSession ?: return@onIfClose
            protectedAccess.launch(player) { cancelTrade(session, "Declined trade.") }
        }
    }

    private suspend fun ProtectedAccess.openOfferScreen(session: TradeSession) {
        val partner = session.other(player) ?: return
        val offerInv = player.invMap[invs.tradeoffer] ?: return

        if (
            session.stage == TradeStage.Offer &&
                !session.player1Accepted &&
                !session.player2Accepted
        ) {
            invClear(offerInv)
        }

        invTransmit(player.inv)
        invTransmit(offerInv)
        ifOpenMainSidePair(interfaces.trade_main, interfaces.trade_side)
        ifSetText(TradeComponents.offer_title, "Trading with: ${partner.username}")
        ifSetEvents(
            TradeComponents.offer_side_inventory,
            0..27,
            IfEvent.Op1,
            IfEvent.Op2,
            IfEvent.Op3,
            IfEvent.Op4,
            IfEvent.Op5,
        )
        ifSetEvents(
            TradeComponents.offer_player_items,
            0..27,
            IfEvent.Op1,
            IfEvent.Op2,
            IfEvent.Op3,
            IfEvent.Op4,
            IfEvent.Op5,
        )
        interfaceInvInit(player, inv, TradeComponents.offer_side_inventory, 4, 7)
        interfaceInvInit(player, offerInv, TradeComponents.offer_player_items, 4, 7)
        ifSetText(TradeComponents.offer_status, "Waiting for other player...")
    }

    private suspend fun ProtectedAccess.openConfirmScreen(session: TradeSession) {
        val partner = session.other(player) ?: return
        val offerInv = player.invMap[invs.tradeoffer] ?: return
        ifOpenMain(interfaces.trade_confirm)
        invTransmit(offerInv)
        interfaceInvInit(player, offerInv, TradeComponents.confirm_player_items, 4, 7)
        ifSetText(TradeComponents.confirm_partner_name, partner.username)
    }

    private fun ProtectedAccess.moveFromInventoryToOffer(
        session: TradeSession,
        partner: Player,
        slot: Int,
        amount: Int,
    ) {
        val offerInv = player.invMap[invs.tradeoffer] ?: return
        val moved = invMoveFromSlot(from = inv, into = offerInv, fromSlot = slot, count = amount)
        if (moved.noneCompleted()) {
            return
        }
        resetAcceptance(session, partner)
    }

    private fun ProtectedAccess.moveFromOfferToInventory(
        session: TradeSession,
        partner: Player,
        slot: Int,
        amount: Int,
    ) {
        val offerInv = player.invMap[invs.tradeoffer] ?: return
        val moved = invMoveFromSlot(from = offerInv, into = inv, fromSlot = slot, count = amount)
        if (moved.noneCompleted()) {
            return
        }
        resetAcceptance(session, partner)
    }

    private fun ProtectedAccess.acceptOfferStage(session: TradeSession, partner: Player) {
        session.setAccepted(player, true)
        if (!session.bothAccepted()) {
            ifSetText(TradeComponents.offer_status, "Waiting for other player...")
            return
        }

        session.stage = TradeStage.Confirm
        session.resetAccepted()
        protectedAccess.launch(player) { openConfirmScreen(session) }
        protectedAccess.launch(partner) { openConfirmScreen(session) }
    }

    private fun ProtectedAccess.acceptConfirmStage(session: TradeSession, partner: Player) {
        session.setAccepted(player, true)
        if (!session.bothAccepted()) {
            return
        }
        completeTrade(session, partner)
    }

    private fun ProtectedAccess.completeTrade(session: TradeSession, partner: Player) {
        val offerInv = player.invMap[invs.tradeoffer] ?: return
        val partnerOfferInv = partner.invMap[invs.tradeoffer] ?: return

        if (!canReceiveAll(inv, partnerOfferInv) || !canReceiveAll(partner.inv, offerInv)) {
            session.stage = TradeStage.Offer
            session.resetAccepted()
            mes("You don't have enough inventory space to complete that trade.")
            partner.mes("You don't have enough inventory space to complete that trade.")
            protectedAccess.launch(player) { openOfferScreen(session) }
            protectedAccess.launch(partner) { openOfferScreen(session) }
            return
        }

        invMoveInv(from = offerInv, into = partner.inv)
        invMoveInv(from = partnerOfferInv, into = inv)

        player.clearTradeSession(partner)
        ifClose()
        partner.ifClose(eventBus)

        mes("Accepted trade.")
        partner.mes("Accepted trade.")
    }

    private fun ProtectedAccess.cancelTrade(session: TradeSession, message: String) {
        val partner = session.other(player)
        val offerInv = player.invMap[invs.tradeoffer] ?: return
        val partnerOfferInv = partner?.invMap?.get(invs.tradeoffer)

        invMoveInv(from = offerInv, into = player.inv)
        if (partner != null && partnerOfferInv != null) {
            protectedAccess.launch(partner) {
                invMoveInv(from = partnerOfferInv, into = partner.inv)
                partner.clearTradeSession(player)
                ifClose()
                mes(message)
            }
        }

        player.clearTradeSession(partner)
        ifClose()
        mes(message)
    }

    private fun ProtectedAccess.resetAcceptance(session: TradeSession, partner: Player) {
        session.resetAccepted()
        ifSetText(TradeComponents.offer_status, "Waiting for other player...")
        protectedAccess.launch(partner) {
            ifSetText(TradeComponents.offer_status, "Other player has changed their offer.")
        }
    }

    private fun canReceiveAll(
        target: org.rsmod.game.inv.Inventory,
        offered: org.rsmod.game.inv.Inventory,
    ): Boolean {
        val freeSlots = target.count { it == null }
        var requiredSlots = 0

        for (obj in offered.filterNotNull()) {
            val type = objTypes[obj]
            if (!type.isStackable) {
                requiredSlots += obj.count
                continue
            }

            val hasStackAlready = target.any { it?.id == obj.id }
            if (!hasStackAlready) {
                requiredSlots++
            }
        }
        return requiredSlots <= freeSlots
    }
}
