package cat.wars;

import cat.wars.handler.MessageDecoder;
import cat.wars.handler.MessageEncoder;
import cat.wars.handler.MessageHandler;
import cat.wars.handler.MessageRecognizer;
import cat.wars.handler.cmd.CmdHandlerFactory;
import cat.wars.util.MySQLSessionFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: netty-demo
 * @description: Server application main
 * @author: Wars
 * @created: 2020-07-18 20:23
 */
@Slf4j
public class Application {

  public static void main(String[] args) {
    CmdHandlerFactory.init();
    MessageRecognizer.init();
    ServerBootstrap server = new ServerBootstrap();

    server.group(new NioEventLoopGroup(), new NioEventLoopGroup());
    server.channel(NioServerSocketChannel.class);
    server.childHandler(
        new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel channel) {
            channel
                .pipeline()
                .addLast(
                    new HttpServerCodec(),
                    new HttpObjectAggregator(65535),
                    new WebSocketServerProtocolHandler("/websocket"),
                    new MessageDecoder() // Decoder
                    ,
                    new MessageEncoder() // Encoder
                    ,
                    new MessageHandler() // Handler
                    );
          }
        });

    try {
      ChannelFuture channelFuture = server.bind(12345).sync();

      MySQLSessionFactory.init();
      if (channelFuture.isSuccess()) log.info("Application 启动成功！");

      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
