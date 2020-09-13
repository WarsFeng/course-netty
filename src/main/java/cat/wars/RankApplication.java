package cat.wars;

import cat.wars.mq.consumer.RankConsumer;
import cat.wars.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RankApplication {

  public static void main(String[] args) {
    RedisUtil.init();
    RankConsumer.init();

    log.info("Rank program started.");
  }
}
