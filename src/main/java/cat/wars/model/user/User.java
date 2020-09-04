package cat.wars.model.user;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

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
    this.totalHP = new AtomicInteger(100);
    this.currHP = new AtomicInteger(100);
  }

  /** 用户 Id */
  private Integer userId;

  /** 英雄形象 */
  private String heroAvatar;

  /** 用户移动状态 */
  private UserMoveState userMoveState;

  /** 用户当前血量 */
  private AtomicInteger currHP;

  /** 用户总血量 */
  private AtomicInteger totalHP;
}
