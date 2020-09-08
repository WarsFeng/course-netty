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

  /** User id */
  private int userId;

  /** 用户昵称 */
  private String username;

  /** 英雄形象 */
  private String heroAvatar;

  /** 用户移动状态 */
  private UserMoveState userMoveState;

  /** 用户当前血量 */
  private int currHP;

  /** 用户总血量 */
  private int totalHP;
}
