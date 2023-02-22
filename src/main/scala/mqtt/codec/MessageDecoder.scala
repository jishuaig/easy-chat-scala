package mqtt.codec

import chat.{ChatMessage, TextMessage}
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import mqtt.codec.HessianSerializer

import java.util
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class MessageDecoder extends ByteToMessageDecoder {

  override def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: util.List[AnyRef]): Unit =
    val duplicateIn = in.retainedDuplicate()
    in.skipBytes(in.readableBytes()); //跳过所有的字节，表示已经读取过了
    if duplicateIn.hasArray then
      val obj = HessianSerializer.deserialize(duplicateIn.array(), classOf[ChatMessage])
      out.add(obj)
    else
      val dst = new Array[Byte](duplicateIn.readableBytes)
      duplicateIn.getBytes(duplicateIn.readerIndex, dst)
      val obj = HessianSerializer.deserialize(dst, classOf[ChatMessage])
      out.add(obj)
}
