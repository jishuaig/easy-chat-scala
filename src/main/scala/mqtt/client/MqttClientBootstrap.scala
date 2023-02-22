package mqtt.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.{Channel, ChannelInitializer}
import mqtt.client.ChatClientHandler
import mqtt.codec.{MessageDecoder, MessageEncoder}

object MqttClientBootstrap:

  def main(args: Array[String]): Unit =
    val worker = new NioEventLoopGroup()
    try
      val bootstrap = new Bootstrap
      bootstrap.group(worker)
      bootstrap.channel(classOf[NioSocketChannel])
        .handler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel): Unit = {
            ch.pipeline()
              .addLast(new MessageDecoder())
              .addLast(new MessageEncoder())
              .addLast(new ChatClientHandler())
          }
        })
      val future = bootstrap.connect("127.0.0.1", 6657).sync()
      future.channel().closeFuture().sync()
    finally
      worker.shutdownGracefully()

