package cat.wars.model.user;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UserMoveState {

  /** 起始位置 Y */
  private BigDecimal fromPosX;
  /** 起始位置 Y */
  private BigDecimal fromPosY;
  /** 移动到位置 X */
  private BigDecimal toPosX;
  /** 移动到位置 Y */
  private BigDecimal toPosY;
  /** 启程时间戳 */
  private Long startTime;
}
