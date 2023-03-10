import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBufAllocator
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter, ChannelInitializer, SimpleChannelInboundHandler}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.mqtt._
import io.netty.util.CharsetUtil
import scala.jdk.CollectionConverters._

import scala.collection.mutable

object MqttServer {
  def main(args: Array[String]): Unit = {
    val bossGroup = new NioEventLoopGroup()
    val workerGroup = new NioEventLoopGroup()

    try {
      val b = new ServerBootstrap()
      b.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .childHandler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel): Unit = {
            val pipeline = ch.pipeline()
            pipeline.addLast(new MqttDecoder())
            pipeline.addLast(MqttEncoder.INSTANCE)
            pipeline.addLast(new MqttServerHandler())
          }
        })

      val f = b.bind(1883).sync()
      f.channel().closeFuture().sync()
    } finally {
      bossGroup.shutdownGracefully()
      workerGroup.shutdownGracefully()
    }
  }

  class MqttServerHandler extends SimpleChannelInboundHandler[MqttMessage] {
    private val topics: mutable.Map[String, mutable.Set[ChannelHandlerContext]] = mutable.Map.empty

    override def channelRead0(ctx: ChannelHandlerContext, msg: MqttMessage): Unit = {
      msg.fixedHeader().messageType() match {
        case MqttMessageType.CONNECT =>
          val connect = msg.asInstanceOf[MqttConnectMessage]
          println(s"Received CONNECT from client ${connect.payload().clientIdentifier()}")

          val connAck = new MqttConnAckMessage(new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
            new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, false))

          ctx.writeAndFlush(connAck)

        case MqttMessageType.SUBSCRIBE =>
          val subscribe = msg.asInstanceOf[MqttSubscribeMessage]
          println(s"Received SUBSCRIBE from client ${ctx.channel().remoteAddress()}")

          val topicsToSubscribe = subscribe.payload().topicSubscriptions().asScala
          for (topic <- topicsToSubscribe) {
            val topicName = topic.topicName()
            topics.getOrElseUpdate(topicName, mutable.Set.empty) += ctx
          }

          val subAck = new MqttSubAckMessage(
            new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_LEAST_ONCE, false, 0),
            MqttMessageIdVariableHeader.from(subscribe.variableHeader().messageId()),
            new MqttSubAckPayload(1))

          ctx.writeAndFlush(subAck)

        case MqttMessageType.PUBLISH =>
          val publish = msg.asInstanceOf[MqttPublishMessage]
          val topicName = publish.variableHeader().topicName()
          val payload = publish.payload().toString(CharsetUtil.UTF_8)

          println(s"Received PUBLISH from client ${ctx.channel().remoteAddress()}: topic=$topicName, payload=$payload")
          topics.get(topicName).foreach(_.foreach(_.writeAndFlush(publish)))

        case MqttMessageType.DISCONNECT =>
          println(s"Received DISCONNECT from client ${ctx.channel().remoteAddress()}")
          ctx.close()

        case _ =>
          println(s"Received unknown message type from client ${ctx.channel().remoteAddress()}")
      }
    }

    override def channelInactive(ctx: ChannelHandlerContext): Unit = {
      topics.foreach { case (topicName, contexts) => contexts -= ctx }
    }
  }
}
