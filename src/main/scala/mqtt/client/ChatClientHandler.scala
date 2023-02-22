package mqtt.client

import chat.{ChatMessage, ChatUser, ImageMessage, TextMessage}
import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.util.CharsetUtil

class ChatClientHandler extends SimpleChannelInboundHandler[ByteBuf] {

  override def channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf): Unit =
    System.out.println("接收到服务端的消息： " + msg.toString(CharsetUtil.UTF_8));

  override def channelActive(ctx: ChannelHandlerContext) =
    println("client channel active!")
    // ctx.writeAndFlush(ChatMessage(ChatUser(1, "abc"), ChatUser(2, "bcd"), TextMessage("hello")))
    (1 to 10).foreach(i =>
      Thread.sleep(1000)
      ctx.writeAndFlush(ChatMessage(ChatUser(1, "abc"), ChatUser(2, "bcd"), TextMessage("hello")))
      ctx.writeAndFlush(ChatMessage(ChatUser(1, "abc"), ChatUser(2, "bcd"), ImageMessage(100, 30, "1.jpg", "2.jpg")))
    )

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit =
    cause.printStackTrace()
    ctx.close()
}
