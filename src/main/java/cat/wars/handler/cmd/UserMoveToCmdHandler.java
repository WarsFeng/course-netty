package cat.wars.handler.cmd;

import cat.wars.handler.Broadcaster;
import cat.wars.model.user.User;
import cat.wars.model.user.UserManager;
import cat.wars.model.user.UserMoveState;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.math.BigDecimal;

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
    User user = UserManager.getUserById(userId);
    UserMoveState moveState = user.getUserMoveState();
    if (null == moveState) user.setUserMoveState(moveState = new UserMoveState());

    moveState.setFromPosX(BigDecimal.valueOf(cmd.getMoveFromPosX()));
    moveState.setFromPosY(BigDecimal.valueOf(cmd.getMoveFromPosY()));
    moveState.setToPosX(BigDecimal.valueOf(cmd.getMoveToPosX()));
    moveState.setToPosY(BigDecimal.valueOf(cmd.getMoveToPosY()));
    moveState.setStartTime(System.currentTimeMillis());

    UserMoveToResult.Builder resultBuilder =
        UserMoveToResult.newBuilder()
            .setMoveUserId(userId)
            .setMoveFromPosX(moveState.getFromPosX().floatValue())
            .setMoveFromPosY(moveState.getFromPosY().floatValue())
            .setMoveToPosX(moveState.getToPosX().floatValue())
            .setMoveToPosY(moveState.getToPosY().floatValue())
            .setMoveStartTime(moveState.getStartTime());

    Broadcaster.broadcast(resultBuilder.build());
  }
}
