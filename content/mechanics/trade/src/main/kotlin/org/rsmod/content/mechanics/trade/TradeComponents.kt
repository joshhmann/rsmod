package org.rsmod.content.mechanics.trade

import org.rsmod.api.type.refs.comp.ComponentReferences

public object TradeComponents : ComponentReferences() {
    public val offer_side_inventory = find("tradeside:side_layer")
    public val offer_player_items = find("trademain:your_offer")
    public val offer_other_items = find("trademain:other_offer")
    public val offer_accept = find("trademain:accept")
    public val offer_decline = find("trademain:decline")
    public val offer_status = find("trademain:status")
    public val offer_title = find("trademain:title")

    public val confirm_player_items = find("tradeconfirm:your_offer")
    public val confirm_other_items = find("tradeconfirm:other_offer")
    public val confirm_partner_name = find("tradeconfirm:tradeopponent")
    public val confirm_accept = find("tradeconfirm:trade2accept")
    public val confirm_decline = find("tradeconfirm:trade2decline")
}
