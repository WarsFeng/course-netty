package cat.wars.mq.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RankKillUserMessage {

  private int winnerUserId;

  private int loserUserId;
}
