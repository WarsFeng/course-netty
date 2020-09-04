package cat.wars.model.user;

import lombok.Getter;
import lombok.Setter;

/**
 * @program: netty-demo
 * @description: User model
 * @author: Wars
 * @created: 2020-07-23 18:06
 */
@Getter
@Setter
public class User {

  public User(Integer userId, String heroAvatar) {
    this.userId = userId;
    this.heroAvatar = heroAvatar;
  }

  /**
   * 用户 Id
   */
  private Integer userId;

  /**
   * 英雄形象
   */
  private String heroAvatar;

  /**
   * 用户移动状态
   */
  private UserMoveState userMoveState;
}
