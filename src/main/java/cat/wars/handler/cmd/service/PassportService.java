package cat.wars.handler.cmd.service;

import cat.wars.handler.cmd.dao.UserMapper;
import cat.wars.model.UserEntity;
import cat.wars.thread.AsyncOperation;
import cat.wars.thread.AsyncOperationProcessor;
import cat.wars.util.MySQLSessionFactory;
import cat.wars.util.RedisUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import redis.clients.jedis.Jedis;

import java.util.function.Function;

@Slf4j
public final class PassportService {

  @Getter private static final PassportService instance = new PassportService();

  private PassportService() {}

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

  public void putUserBasicInfoToRedis(UserEntity user) {
    // Put user to redis
    try (Jedis jedis = RedisUtil.getJedis()) {
      JSONObject json = new JSONObject();
      json.put("userId", user.getUserId());
      json.put("userName", user.getUserName());
      json.put("heroAvatar", user.getHeroAvatar());

      jedis.hset("User_" + user.getUserId(), "BasicInfo", json.toJSONString());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("User basic info put to redis fail!");
    }
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
        PassportService.getInstance().putUserBasicInfoToRedis(user); // Put user basic info to redis
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }
}
