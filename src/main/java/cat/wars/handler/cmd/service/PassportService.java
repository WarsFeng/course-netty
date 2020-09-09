package cat.wars.handler.cmd.service;

import cat.wars.handler.cmd.dao.UserMapper;
import cat.wars.model.UserEntity;
import cat.wars.thread.AsyncOperation;
import cat.wars.thread.AsyncOperationProcessor;
import cat.wars.util.MySQLSessionFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.util.function.Function;

@Slf4j
public final class PassportService {

  private static final PassportService instance = new PassportService();

  private PassportService() {}

  public static PassportService getInstance() {
    return instance;
  }

  public void login(String username, String password, Function<UserEntity, Void> callback) {
    AsyncOperationProcessor.getInstance()
        .submit(
            new AsyncGetUserByUsernameAndPassword(username, password) {
              @Override
              public void doFinish() {
                callback.apply(this.getUserEntity());
              }
            });
  }

  @Getter
  @Setter
  private static class AsyncGetUserByUsernameAndPassword implements AsyncOperation {

    private String username;
    private String password;
    private UserEntity userEntity;

    public AsyncGetUserByUsernameAndPassword(String username, String password) {
      this.username = username;
      this.password = password;
    }

    @Override
    public int getBindId() {
      return username.charAt(username.length() - 1);
    }

    @Override
    public void doAsync() {
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
          return;
        }

        userEntity = user;
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }
}