package mqtt.server

import chat.{ChatMessage, TextMessage}
import io.netty.buffer.Unpooled
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.util.CharsetUtil

import scala.collection.mutable

class ChatServerHandler extends SimpleChannelInboundHandler[ChatMessage] {

  private val clients = mutable.Set[ChannelHandlerContext]()

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    clients.add(ctx)
  }

  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    clients.remove(ctx)
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: ChatMessage): Unit = {
    println(s"receive msg : $msg")
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit =
    cause.printStackTrace()
    ctx.close()
}
