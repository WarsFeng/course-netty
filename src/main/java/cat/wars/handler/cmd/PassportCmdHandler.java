package cat.wars.handler.cmd;

import cat.wars.handler.cmd.service.PassportService;
import cat.wars.model.MessageProtocol.UserLoginCmd;
import cat.wars.model.MessageProtocol.UserLoginResult;
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

    service.login(
        cmd.getUserName(),
        cmd.getPassword(),
        userEntity -> {
          if (null == userEntity) {
            log.error("User login fail, username: {}", cmd.getUserName());
            return null;
          }

          context
              .channel()
              .attr(AttributeKey.valueOf("userId"))
              .set(userEntity.getUserId()); // Bind userId to channel
          User userSession = new User();
          userSession.setUserId(userEntity.getUserId());
          userSession.setUsername(userEntity.getUserName());
          userSession.setHeroAvatar(userEntity.getHeroAvatar());
          userSession.setTotalHP(100);
          userSession.setCurrHP(100);
          userSession.setLiving(true);
          UserManager.addUser(userSession); // Add to user map

          UserLoginResult result =
              UserLoginResult.newBuilder()
                  .setUserId(userEntity.getUserId())
                  .setUserName(userEntity.getUserName())
                  .setHeroAvatar(userEntity.getHeroAvatar())
                  .build();

          context.writeAndFlush(result);
          return null;
        });
  }
}
