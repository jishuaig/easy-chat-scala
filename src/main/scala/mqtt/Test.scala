package mqtt

import chat.{ChatMessage, ChatUser, ImageMessage, TextMessage}
import io.netty.buffer.Unpooled
import io.netty.channel.embedded.EmbeddedChannel
import mqtt.codec.{HessianSerializer, MessageDecoder}

object Test {

  def main(args: Array[String]): Unit = {
    val buffer = Unpooled.buffer
    val bytes = HessianSerializer.serialize(ChatMessage(ChatUser(1, "abc"), ChatUser(2, "bcd"), ImageMessage(100, 30, "1.jpg", "2.jpg")))
    buffer.writeBytes(bytes)

    val channel = new EmbeddedChannel(new MessageDecoder());
    channel.writeInbound(buffer)
    channel.finish()

    println(channel.readInbound)
    println(channel.readInbound)
    println(channel.readInbound)
    println(channel.readInbound)
    println(channel.readInbound)
  }

}
