package cat.wars.handler.cmd;

import cat.wars.model.MessageProtocol.WhoElseIsHereCmd;
import cat.wars.model.user.User;
import cat.wars.model.user.UserManager;
import cat.wars.model.user.UserMoveState;
import io.netty.channel.ChannelHandlerContext;

import static cat.wars.model.MessageProtocol.WhoElseIsHereResult;

public class WhoElseIsHereCmdHandler implements CmdHandler<WhoElseIsHereCmd> {

  @Override
  public void handle(ChannelHandlerContext context, WhoElseIsHereCmd cmd) {
    // Result
    WhoElseIsHereResult.Builder resultBuilder = WhoElseIsHereResult.newBuilder();
    for (User user : UserManager.listUser()) {
      WhoElseIsHereResult.UserInfo.Builder userInfoBuilder =
          WhoElseIsHereResult.UserInfo.newBuilder()
              .setUserId(user.getUserId())
              .setHeroAvatar(user.getHeroAvatar());
      // Set move state
      UserMoveState userMoveState = user.getUserMoveState();
      if (null != userMoveState) {
        WhoElseIsHereResult.UserInfo.MoveState.Builder moveStateBuilder = WhoElseIsHereResult.UserInfo.MoveState.newBuilder()
                .setFromPosX(userMoveState.getFromPosX().floatValue())
                .setFromPosY(userMoveState.getFromPosY().floatValue())
                .setToPosX(userMoveState.getToPosX().floatValue())
                .setToPosY(userMoveState.getToPosY().floatValue())
                .setStartTime(userMoveState.getStartTime());
        userInfoBuilder.setMoveState(moveStateBuilder);
      }

      resultBuilder.addUserInfo(userInfoBuilder);
    }
    WhoElseIsHereResult result = resultBuilder.build();
    // Broadcast
    context.writeAndFlush(result);
  }
}
