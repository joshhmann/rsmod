package org.rsmod.api.net.rsprot.provider

import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlin.jvm.java
import net.rsprot.protocol.game.incoming.buttons.If3Button
import net.rsprot.protocol.game.incoming.buttons.IfButtonD
import net.rsprot.protocol.game.incoming.buttons.IfButtonT
import net.rsprot.protocol.game.incoming.events.EventAppletFocus
import net.rsprot.protocol.game.incoming.events.EventCameraPosition
import net.rsprot.protocol.game.incoming.events.EventKeyboard
import net.rsprot.protocol.game.incoming.events.EventMouseClickV1
import net.rsprot.protocol.game.incoming.events.EventMouseClickV2
import net.rsprot.protocol.game.incoming.events.EventMouseMove
import net.rsprot.protocol.game.incoming.events.EventMouseScroll
import net.rsprot.protocol.game.incoming.events.EventNativeMouseMove
import net.rsprot.protocol.game.incoming.locs.OpLoc
import net.rsprot.protocol.game.incoming.locs.OpLoc6
import net.rsprot.protocol.game.incoming.locs.OpLocT
import net.rsprot.protocol.game.incoming.messaging.MessagePrivate
import net.rsprot.protocol.game.incoming.messaging.MessagePublic
import net.rsprot.protocol.game.incoming.misc.client.Idle
import net.rsprot.protocol.game.incoming.misc.client.MapBuildComplete
import net.rsprot.protocol.game.incoming.misc.client.NoTimeout
import net.rsprot.protocol.game.incoming.misc.client.WindowStatus
import net.rsprot.protocol.game.incoming.misc.user.ClickWorldMap
import net.rsprot.protocol.game.incoming.misc.user.ClientCheat
import net.rsprot.protocol.game.incoming.misc.user.CloseModal
import net.rsprot.protocol.game.incoming.misc.user.MoveGameClick
import net.rsprot.protocol.game.incoming.misc.user.MoveMinimapClick
import net.rsprot.protocol.game.incoming.misc.user.Teleport
import net.rsprot.protocol.game.incoming.npcs.OpNpc
import net.rsprot.protocol.game.incoming.npcs.OpNpc6
import net.rsprot.protocol.game.incoming.npcs.OpNpcT
import net.rsprot.protocol.game.incoming.objs.OpObj
import net.rsprot.protocol.game.incoming.objs.OpObj6
import net.rsprot.protocol.game.incoming.objs.OpObjT
import net.rsprot.protocol.game.incoming.players.OpPlayer
import net.rsprot.protocol.game.incoming.players.OpPlayerT
import net.rsprot.protocol.game.incoming.resumed.ResumePCountDialog
import net.rsprot.protocol.game.incoming.resumed.ResumePNameDialog
import net.rsprot.protocol.game.incoming.resumed.ResumePObjDialog
import net.rsprot.protocol.game.incoming.resumed.ResumePStringDialog
import net.rsprot.protocol.game.incoming.resumed.ResumePauseButton
import net.rsprot.protocol.game.incoming.social.FriendListAdd
import net.rsprot.protocol.game.incoming.social.FriendListDel
import net.rsprot.protocol.game.incoming.social.IgnoreListAdd
import net.rsprot.protocol.game.incoming.social.IgnoreListDel
import net.rsprot.protocol.message.codec.incoming.GameMessageConsumerRepositoryBuilder
import net.rsprot.protocol.message.codec.incoming.provider.DefaultGameMessageConsumerRepositoryProvider
import org.rsmod.api.net.rsprot.handlers.ClickWorldMapHandler
import org.rsmod.api.net.rsprot.handlers.ClientCheatHandler
import org.rsmod.api.net.rsprot.handlers.CloseModalHandler
import org.rsmod.api.net.rsprot.handlers.EventAppletFocusHandler
import org.rsmod.api.net.rsprot.handlers.EventCameraPositionHandler
import org.rsmod.api.net.rsprot.handlers.EventKeyboardHandler
import org.rsmod.api.net.rsprot.handlers.EventMouseClickV1Handler
import org.rsmod.api.net.rsprot.handlers.EventMouseClickV2Handler
import org.rsmod.api.net.rsprot.handlers.EventMouseMoveHandler
import org.rsmod.api.net.rsprot.handlers.EventMouseScrollHandler
import org.rsmod.api.net.rsprot.handlers.EventNativeMouseMoveHandler
import org.rsmod.api.net.rsprot.handlers.FriendListAddHandler
import org.rsmod.api.net.rsprot.handlers.FriendListDelHandler
import org.rsmod.api.net.rsprot.handlers.IdleHandler
import org.rsmod.api.net.rsprot.handlers.If3ButtonHandler
import org.rsmod.api.net.rsprot.handlers.IfButtonDHandler
import org.rsmod.api.net.rsprot.handlers.IfButtonTHandler
import org.rsmod.api.net.rsprot.handlers.IgnoreListAddHandler
import org.rsmod.api.net.rsprot.handlers.IgnoreListDelHandler
import org.rsmod.api.net.rsprot.handlers.MapBuildCompleteHandler
import org.rsmod.api.net.rsprot.handlers.MessagePrivateHandler
import org.rsmod.api.net.rsprot.handlers.MessagePublicHandler
import org.rsmod.api.net.rsprot.handlers.MoveGameClickHandler
import org.rsmod.api.net.rsprot.handlers.MoveMinimapClickHandler
import org.rsmod.api.net.rsprot.handlers.NoTimeoutHandler
import org.rsmod.api.net.rsprot.handlers.OpLoc6Handler
import org.rsmod.api.net.rsprot.handlers.OpLocHandler
import org.rsmod.api.net.rsprot.handlers.OpLocTHandler
import org.rsmod.api.net.rsprot.handlers.OpNpc6Handler
import org.rsmod.api.net.rsprot.handlers.OpNpcHandler
import org.rsmod.api.net.rsprot.handlers.OpNpcTHandler
import org.rsmod.api.net.rsprot.handlers.OpObj6Handler
import org.rsmod.api.net.rsprot.handlers.OpObjHandler
import org.rsmod.api.net.rsprot.handlers.OpObjTHandler
import org.rsmod.api.net.rsprot.handlers.OpPlayerHandler
import org.rsmod.api.net.rsprot.handlers.OpPlayerTHandler
import org.rsmod.api.net.rsprot.handlers.ResumePCountDialogHandler
import org.rsmod.api.net.rsprot.handlers.ResumePNameDialogHandler
import org.rsmod.api.net.rsprot.handlers.ResumePObjDialogHandler
import org.rsmod.api.net.rsprot.handlers.ResumePStringDialogHandler
import org.rsmod.api.net.rsprot.handlers.ResumePauseButtonHandler
import org.rsmod.api.net.rsprot.handlers.TeleportHandler
import org.rsmod.api.net.rsprot.handlers.WindowStatusHandler
import org.rsmod.game.entity.Player

@Singleton
class MessageConsumerProvider
@Inject
constructor(
    private val windowStatus: WindowStatusHandler,
    private val moveGameClick: MoveGameClickHandler,
    private val moveMinimapClick: MoveMinimapClickHandler,
    private val opLoc: OpLocHandler,
    private val opLocT: OpLocTHandler,
    private val opLoc6: OpLoc6Handler,
    private val clientCheat: ClientCheatHandler,
    private val opNpc: OpNpcHandler,
    private val opNpcT: OpNpcTHandler,
    private val opNpc6: OpNpc6Handler,
    private val opPlayer: OpPlayerHandler,
    private val opPlayerT: OpPlayerTHandler,
    private val messagePublic: MessagePublicHandler,
    private val if3Button: If3ButtonHandler,
    private val closeModal: CloseModalHandler,
    private val resumePauseButton: ResumePauseButtonHandler,
    private val opObj: OpObjHandler,
    private val opObj6: OpObj6Handler,
    private val resumePCountDialog: ResumePCountDialogHandler,
    private val resumePNameDialog: ResumePNameDialogHandler,
    private val resumePStringDialog: ResumePStringDialogHandler,
    private val resumePObjDialog: ResumePObjDialogHandler,
    private val ifButtonD: IfButtonDHandler,
    private val ifButtonT: IfButtonTHandler,
    private val mapBuildComplete: MapBuildCompleteHandler,
    private val friendListAdd: FriendListAddHandler,
    private val friendListDel: FriendListDelHandler,
    private val ignoreListAdd: IgnoreListAddHandler,
    private val ignoreListDel: IgnoreListDelHandler,
    private val messagePrivate: MessagePrivateHandler,
    private val eventCameraPosition: EventCameraPositionHandler,
    private val eventAppletFocus: EventAppletFocusHandler,
    private val eventKeyboard: EventKeyboardHandler,
    private val eventMouseClickV1: EventMouseClickV1Handler,
    private val eventMouseClickV2: EventMouseClickV2Handler,
    private val eventMouseMove: EventMouseMoveHandler,
    private val eventMouseScroll: EventMouseScrollHandler,
    private val eventNativeMouseMove: EventNativeMouseMoveHandler,
    private val idle: IdleHandler,
    private val noTimeout: NoTimeoutHandler,
    private val clickWorldMap: ClickWorldMapHandler,
    private val teleport: TeleportHandler,
    private val opObjT: OpObjTHandler,
) {
    fun get(): DefaultGameMessageConsumerRepositoryProvider<Player> {
        val builder = GameMessageConsumerRepositoryBuilder<Player>()
        builder.addListener(WindowStatus::class.java, windowStatus)
        builder.addListener(MoveGameClick::class.java, moveGameClick)
        builder.addListener(MoveMinimapClick::class.java, moveMinimapClick)
        builder.addListener(OpLoc::class.java, opLoc)
        builder.addListener(OpLocT::class.java, opLocT)
        builder.addListener(OpLoc6::class.java, opLoc6)
        builder.addListener(ClientCheat::class.java, clientCheat)
        builder.addListener(OpNpc::class.java, opNpc)
        builder.addListener(OpNpcT::class.java, opNpcT)
        builder.addListener(OpNpc6::class.java, opNpc6)
        builder.addListener(OpPlayer::class.java, opPlayer)
        builder.addListener(OpPlayerT::class.java, opPlayerT)
        builder.addListener(MessagePublic::class.java, messagePublic)
        builder.addListener(If3Button::class.java, if3Button)
        builder.addListener(CloseModal::class.java, closeModal)
        builder.addListener(ResumePauseButton::class.java, resumePauseButton)
        builder.addListener(OpObj::class.java, opObj)
        builder.addListener(OpObj6::class.java, opObj6)
        builder.addListener(ResumePCountDialog::class.java, resumePCountDialog)
        builder.addListener(ResumePNameDialog::class.java, resumePNameDialog)
        builder.addListener(ResumePStringDialog::class.java, resumePStringDialog)
        builder.addListener(ResumePObjDialog::class.java, resumePObjDialog)
        builder.addListener(IfButtonD::class.java, ifButtonD)
        builder.addListener(IfButtonT::class.java, ifButtonT)
        builder.addListener(MapBuildComplete::class.java, mapBuildComplete)
        builder.addListener(FriendListAdd::class.java, friendListAdd)
        builder.addListener(FriendListDel::class.java, friendListDel)
        builder.addListener(IgnoreListAdd::class.java, ignoreListAdd)
        builder.addListener(IgnoreListDel::class.java, ignoreListDel)
        builder.addListener(MessagePrivate::class.java, messagePrivate)
        builder.addListener(EventCameraPosition::class.java, eventCameraPosition)
        builder.addListener(EventAppletFocus::class.java, eventAppletFocus)
        builder.addListener(EventKeyboard::class.java, eventKeyboard)
        builder.addListener(EventMouseClickV1::class.java, eventMouseClickV1)
        builder.addListener(EventMouseClickV2::class.java, eventMouseClickV2)
        builder.addListener(EventMouseMove::class.java, eventMouseMove)
        builder.addListener(EventMouseScroll::class.java, eventMouseScroll)
        builder.addListener(EventNativeMouseMove::class.java, eventNativeMouseMove)
        builder.addListener(Idle::class.java, idle)
        builder.addListener(NoTimeout::class.java, noTimeout)
        builder.addListener(ClickWorldMap::class.java, clickWorldMap)
        builder.addListener(Teleport::class.java, teleport)
        builder.addListener(OpObjT::class.java, opObjT)
        return DefaultGameMessageConsumerRepositoryProvider(builder.build())
    }
}
