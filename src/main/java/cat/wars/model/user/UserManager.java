package cat.wars.model.user;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: netty-demo
 * @description: User management util
 * @author: Wars
 * @created: 2020-07-24 22:57
 */
public final class UserManager {

  private static final Map<Integer, User> USER_MAP = new ConcurrentHashMap<>(); // User pool

  private UserManager() {}

  /**
   * Add user to pool
   *
   * @param user {@link User}
   */
  public static void addUser(User user) {
    if (null == user) return;

    USER_MAP.put(user.getUserId(), user);
  }

  /**
   * Remove user to pool
   *
   * @param userId User id
   */
  public static void removeUser(Integer userId) {
    if (null == userId) return;

    USER_MAP.remove(userId);
  }

  /**
   * Get user list by pool
   *
   * @return User list
   */
  public static Collection<User> listUser() {
    return USER_MAP.values();
  }

  public static User getUserById(Integer userId) {
    if (null == userId) return null;

    return USER_MAP.get(userId);
  }
}
