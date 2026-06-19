package org.rsmod.api.net.rsprot.provider

import io.netty.channel.ChannelHandlerContext
import net.rsprot.protocol.api.ChannelExceptionHandler
import net.rsprot.protocol.api.IncomingGameMessageConsumerExceptionHandler
import net.rsprot.protocol.api.Session
import net.rsprot.protocol.api.handlers.ExceptionHandlers
import net.rsprot.protocol.message.IncomingGameMessage
import org.rsmod.game.entity.Player

object ExceptionHandlersProvider {
    fun provide(): ExceptionHandlers<Player> {
        val channelHandler = ChannelExceptionHandler { ctx: ChannelHandlerContext, cause: Throwable ->
            System.err.println("NETTY CHANNEL EXCEPTION on ${ctx.channel()}: ${cause.message}")
            cause.printStackTrace()
            throw cause
        }
        val messageHandler =
            IncomingGameMessageConsumerExceptionHandler {
                session: Session<Player>,
                message: IncomingGameMessage,
                throwable: Throwable ->
                System.err.println("NETTY MESSAGE EXCEPTION on session $session: ${throwable.message} (message=$message)")
                throwable.printStackTrace()
                throw throwable
            }
        return ExceptionHandlers(channelHandler, messageHandler)
    }
}
