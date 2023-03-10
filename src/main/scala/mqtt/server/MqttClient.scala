import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter, ChannelInitializer}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.mqtt.*
import io.netty.util.CharsetUtil

import java.util
import scala.jdk.CollectionConverters.*
import scala.collection.JavaConverters.*
import scala.util.Random

object MqttClientDemo {
  def main(args: Array[String]): Unit = {
    val group = new NioEventLoopGroup()

    try {
      val b = new Bootstrap()
      b.group(group)
        .channel(classOf[NioSocketChannel])
        .handler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel): Unit = {
            val pipeline = ch.pipeline()
            pipeline.addLast(MqttEncoder.INSTANCE)
            pipeline.addLast(new MqttDecoder())
            pipeline.addLast(new MqttClientHandler())
          }
        })

      val f = b.connect("localhost", 1883).sync()

      val clientId = s"client-${Random.nextInt(100)}"
      val connect = new MqttConnectMessage(
        new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0),
        new MqttConnectVariableHeader(MqttVersion.MQTT_3_1_1.protocolName(), MqttVersion.MQTT_3_1_1.protocolLevel(), true, true, false, 0, false, false, 30),
        new MqttConnectPayload(clientId, "topic", "message", "username", "password"))

      f.channel().writeAndFlush(connect)

      Thread.sleep(1000)

      val list = new java.util.ArrayList[MqttTopicSubscription]
      list.add(new MqttTopicSubscription("topic", MqttQoS.AT_LEAST_ONCE))

      val subscribe = new MqttSubscribeMessage(
        new MqttFixedHeader(MqttMessageType.SUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0),
        MqttMessageIdVariableHeader.from(123),
        new MqttSubscribePayload(list)
      )

      f.channel().writeAndFlush(subscribe)

      Thread.sleep(1000)

      val message = "Hello MQTT!"
      val publish = new MqttPublishMessage(
        new MqttFixedHeader(
          MqttMessageType.PUBLISH,
          false,
          MqttQoS.AT_MOST_ONCE,
          false,
          0),
        new MqttPublishVariableHeader("test-topic", 0),
        Unpooled.copiedBuffer(message, CharsetUtil.UTF_8))

      f.channel().writeAndFlush(publish)

      Thread.sleep(1000)

      f.channel().close()

    } finally {
      group.shutdownGracefully()
    }
  }
}

class MqttClientHandler extends ChannelInboundHandlerAdapter {

  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = {
    msg match {
      case mqttMessage: MqttMessage =>
        mqttMessage.fixedHeader().messageType() match {
          case MqttMessageType.CONNACK =>
            println("Received CONNACK from server")
            ctx.channel().close()

          case MqttMessageType.SUBACK =>
            println("Received SUBACK from server")

          case MqttMessageType.PUBLISH =>
            val payload = mqttMessage.asInstanceOf[MqttPublishMessage].payload().toString(CharsetUtil.UTF_8)
            println(s"Received PUBLISH from server: $payload")

          case _ =>
            println(s"Received unknown message type from server")
        }
    }
  }
}
