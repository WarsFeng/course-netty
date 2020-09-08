package cat.wars.handler.cmd.service;

import cat.wars.handler.cmd.dao.UserMapper;
import cat.wars.model.UserEntity;
import cat.wars.util.MySQLSessionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

@Slf4j
public final class PassportService {

  private static final PassportService instance = new PassportService();

  private PassportService() {}

  public static PassportService getInstance() {
    return instance;
  }

  public UserEntity login(String username, String password) {

    try (SqlSession SQLSession = MySQLSessionFactory.openSession()) {
      UserMapper mapper = SQLSession.getMapper(UserMapper.class);

      UserEntity user = mapper.getUserByName(username);
      if (null == user) { // User not exists, create user
        UserEntity target = new UserEntity();
        target.setUserName(username);
        target.setPassword(password);
        target.setHeroAvatar("Hero_Shaman");
        mapper.add(target);
        user = target;
      }
      if (!password.equals(user.getPassword())) {
        log.error(
            "Login user password invalid.userid: {}, username: {}", user.getUserId(), username);
        return null;
      }

      return user;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }
}
