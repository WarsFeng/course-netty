package cat.wars.handler.cmd;

import cat.wars.model.MessageProtocol.WhoElseIsHereCmd;
import cat.wars.model.user.User;
import cat.wars.model.user.UserManager;
import io.netty.channel.ChannelHandlerContext;

import static cat.wars.model.MessageProtocol.WhoElseIsHereResult;

public class WhoElseIsHereCmdHandler implements CmdHandler<WhoElseIsHereCmd> {

  @Override
  public void handle(ChannelHandlerContext context, WhoElseIsHereCmd cmd) {
    // Result
    WhoElseIsHereResult.Builder resultBuilder = WhoElseIsHereResult.newBuilder();
    for (User user : UserManager.listUser()) {
      WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = WhoElseIsHereResult.UserInfo.newBuilder()
              .setUserId(user.getUserId())
              .setHeroAvatar(user.getHeroAvatar());
      resultBuilder.addUserInfo(userInfoBuilder);
    }
    WhoElseIsHereResult result = resultBuilder.build();
    // Broadcast
    context.writeAndFlush(result);
  }
}
