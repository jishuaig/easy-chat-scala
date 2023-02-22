package mqtt.codec

import chat.ChatMessage
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import mqtt.codec.HessianSerializer

class MessageEncoder extends MessageToByteEncoder[ChatMessage] {
  override def encode(ctx: ChannelHandlerContext, chatMessage: ChatMessage, out: ByteBuf): Unit = {
    val bytes = HessianSerializer.serialize(chatMessage)
    out.writeBytes(bytes)
  }
}
