package cat.wars.handler.cmd;

import cat.wars.handler.Broadcaster;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import static cat.wars.model.MessageProtocol.UserMoveToCmd;
import static cat.wars.model.MessageProtocol.UserMoveToResult;

/**
 * @program: netty-demo
 * @description:
 * @author: Wars
 * @created: 2020-07-24 23:13
 */
public class UserMoveToCmdHandler implements CmdHandler<UserMoveToCmd> {

  public void handle(ChannelHandlerContext context, UserMoveToCmd cmd) {
    Integer userId = (Integer) context.channel().attr(AttributeKey.valueOf("userId")).get();
    if (null == userId) return;

    UserMoveToResult.Builder resultBuilder = UserMoveToResult.newBuilder()
            .setMoveUserId(userId)
            .setMoveToPosX(cmd.getMoveToPosX())
            .setMoveToPosY(cmd.getMoveToPosY());

    Broadcaster.broadcast(resultBuilder.build());
  }
}
