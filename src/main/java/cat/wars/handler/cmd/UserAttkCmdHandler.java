package cat.wars.handler.cmd;

import cat.wars.handler.Broadcaster;
import cat.wars.model.MessageProtocol;
import cat.wars.model.user.User;
import cat.wars.model.user.UserManager;
import cat.wars.mq.message.RankKillUserMessage;
import cat.wars.mq.producer.MQProducer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * @program: course-netty
 * @description:
 * @author: Wars
 * @created: 2020-08-08 16:06
 */
public class UserAttkCmdHandler implements CmdHandler<MessageProtocol.UserAttkCmd> {

  @Override
  public void handle(ChannelHandlerContext context, MessageProtocol.UserAttkCmd cmd) {
    if (null == context || null == cmd) return;
    Integer userId = (Integer) context.channel().attr(AttributeKey.valueOf("userId")).get();
    if (null == userId || 0 >= cmd.getTargetUserId()) return;
    User targetUser = UserManager.getUserById(cmd.getTargetUserId());

    int targetHP = targetUser.getCurrHP() - 10;
    targetUser.setCurrHP(targetHP);
    if (0 >= (targetHP) && targetUser.isLiving()) {
      MessageProtocol.UserDieResult userDieResult =
          MessageProtocol.UserDieResult.newBuilder()
              .setTargetUserId(targetUser.getUserId())
              .build();
      Broadcaster.broadcast(userDieResult);

      targetUser.setLiving(false);
      RankKillUserMessage killedMessage = new RankKillUserMessage();
      killedMessage.setWinnerUserId(userId);
      killedMessage.setLoserUserId(cmd.getTargetUserId());
      MQProducer.sendMessage("Killed", killedMessage);
    }

    MessageProtocol.UserAttkResult userAttkResult =
        MessageProtocol.UserAttkResult.newBuilder()
            .setAttkUserId(userId)
            .setTargetUserId(cmd.getTargetUserId())
            .build();
    Broadcaster.broadcast(userAttkResult);

    MessageProtocol.UserSubtractHpResult userSubtractHpResult =
        MessageProtocol.UserSubtractHpResult.newBuilder()
            .setTargetUserId(cmd.getTargetUserId())
            .setSubtractHp(10)
            .build();
    Broadcaster.broadcast(userSubtractHpResult);
  }
}
