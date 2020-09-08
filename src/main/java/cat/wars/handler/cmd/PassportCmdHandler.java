package cat.wars.handler.cmd;

import cat.wars.handler.cmd.service.PassportService;
import cat.wars.model.MessageProtocol.UserLoginCmd;
import cat.wars.model.MessageProtocol.UserLoginResult;
import cat.wars.model.UserEntity;
import cat.wars.model.user.User;
import cat.wars.model.user.UserManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.lang3.StringUtils.isAnyEmpty;

/** User passport related */
@Slf4j
public class PassportCmdHandler implements CmdHandler<UserLoginCmd> {

  @Override
  public void handle(ChannelHandlerContext context, UserLoginCmd cmd) {
    if (null == context || null == cmd || isAnyEmpty(cmd.getUserName(), cmd.getPassword())) return;
    log.info("User login, username: {}, password: {}", cmd.getUserName(), cmd.getPassword());
    PassportService service = PassportService.getInstance();

    UserEntity user = service.login(cmd.getUserName(), cmd.getPassword());
    if (null == user) return;

    context
        .channel()
        .attr(AttributeKey.valueOf("userId"))
        .set(user.getUserId()); // Bind userId to channel
    User userSession = new User();
    userSession.setUserId(user.getUserId());
    userSession.setUsername(user.getUserName());
    userSession.setHeroAvatar(user.getHeroAvatar());
    userSession.setTotalHP(100);
    userSession.setCurrHP(100);
    UserManager.addUser(userSession); // Add to user map

    UserLoginResult result =
        UserLoginResult.newBuilder()
            .setUserId(user.getUserId())
            .setUserName(user.getUserName())
            .setHeroAvatar(user.getHeroAvatar())
            .build();

    context.writeAndFlush(result);
  }
}
