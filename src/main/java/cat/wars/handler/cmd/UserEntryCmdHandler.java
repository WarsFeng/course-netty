package cat.wars.handler.cmd;

import cat.wars.handler.Broadcaster;
import cat.wars.model.MessageProtocol.UserEntryCmd;
import cat.wars.model.user.User;
import cat.wars.model.user.UserManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import static cat.wars.model.MessageProtocol.UserEntryResult;

/**
 * @program: netty-demo
 * @description: Game message handler
 * @author: Wars
 * @created: 2020-07-20 15:23
 */
public class UserEntryCmdHandler implements CmdHandler<UserEntryCmd> {

  public void handle(ChannelHandlerContext context, UserEntryCmd cmd) {
    int userId = cmd.getUserId();
    String heroAvatar = cmd.getHeroAvatar();
    context.channel().attr(AttributeKey.valueOf("userId")).set(userId); // Bind userId to channel

    UserManager.addUser(new User(userId, heroAvatar)); // Add to user map

    // Result and broadcast
    UserEntryResult.Builder resultBuilder = UserEntryResult.newBuilder();
    resultBuilder.setUserId(userId);
    resultBuilder.setHeroAvatar(heroAvatar);
    UserEntryResult result = resultBuilder.build();
    Broadcaster.broadcast(result);
  }
}
