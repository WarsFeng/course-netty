package cat.wars.handler;

import cat.wars.model.MessageProtocol;
import cat.wars.model.user.UserManager;
import cat.wars.thread.MainThreadProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: netty-demo
 * @description: Game message handler
 * @author: Wars
 * @created: 2020-07-20 15:23
 */
@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<Object> {

  @Override
  public void channelActive(ChannelHandlerContext context) throws Exception {
    super.channelActive(context);
    Broadcaster.addChannel(context.channel());
  }

  @Override
  public void handlerRemoved(ChannelHandlerContext context) throws Exception {
    super.handlerRemoved(context);
    Integer userId = (Integer) context.channel().attr(AttributeKey.valueOf("userId")).get();
    if (null == userId) return;

    Broadcaster.removeChannel(context.channel());
    UserManager.removeUser(userId);

    MessageProtocol.UserQuitResult.Builder resultBuilder =
        MessageProtocol.UserQuitResult.newBuilder().setQuitUserId(userId);
    Broadcaster.broadcast(resultBuilder.build());
  }

  @Override
  protected void channelRead0(ChannelHandlerContext context, Object msg) {
    MainThreadProcessor.getInstance().handler(context, msg);
  }
}
