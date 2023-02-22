package mqtt.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.{ByteBuf, UnpooledByteBufAllocator}
import io.netty.channel.{ChannelInitializer, ChannelOption}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.{MessageToMessageDecoder, MessageToMessageEncoder}
import io.netty.handler.codec.string.{StringDecoder, StringEncoder}
import io.netty.handler.logging.{LogLevel, LoggingHandler}
import io.netty.util.concurrent.{DefaultEventExecutorGroup, DefaultThreadFactory, EventExecutorGroup}
import mqtt.codec.{MessageDecoder, MessageEncoder}

object MqttServerBoostrap:
  def main(args: Array[String]): Unit =
    val bossGroup = new NioEventLoopGroup(1)
    val workerGroup = new NioEventLoopGroup()

    try {
      val bootstrap = new ServerBootstrap()
      bootstrap.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel): Unit = {
            ch.pipeline()
              .addLast(new MessageDecoder())
              .addLast(new MessageEncoder())
              .addLast(new ChatServerHandler())
          }
        })
      bootstrap.childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
      val future = bootstrap.bind(6657).sync()
      println("Server started and listening on port 6657")
      future.channel().closeFuture().sync()
    } finally {
      bossGroup.shutdownGracefully()
      workerGroup.shutdownGracefully()
    }


        

