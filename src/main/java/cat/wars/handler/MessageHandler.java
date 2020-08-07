package cat.wars.handler;

import cat.wars.handler.cmd.CmdHandler;
import cat.wars.handler.cmd.CmdHandlerFactory;
import cat.wars.model.MessageProtocol;
import cat.wars.model.user.UserManager;
import com.google.protobuf.GeneratedMessageV3;
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

    MessageProtocol.UserQuitResult.Builder resultBuilder = MessageProtocol.UserQuitResult.newBuilder()
            .setQuitUserId(userId);
    Broadcaster.broadcast(resultBuilder.build());
  }

  @Override
  protected void channelRead0(ChannelHandlerContext context, Object msg) {
    if (null == msg) return;

    CmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory.create(msg.getClass());
    if (null == cmdHandler) {
      log.error("Does Not match the handler!");
      return;
    }

    cmdHandler.handle(context, cast(msg));
  }

  private static <Cmd extends GeneratedMessageV3> Cmd cast(Object msg) {
    if (null == msg) return null;
    return (Cmd) msg;
  }
}
