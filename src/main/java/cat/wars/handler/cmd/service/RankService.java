package cat.wars.handler.cmd.service;

import cat.wars.model.rank.RankItem;
import cat.wars.model.user.UserBasicInfo;
import cat.wars.thread.AsyncOperation;
import cat.wars.thread.AsyncOperationProcessor;
import cat.wars.util.RedisUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Slf4j
public final class RankService {

  @Getter private static final RankService instance = new RankService();

  private RankService() {}

  public void getRank(Function<List<RankItem>, Void> callback) {
    AsyncOperation asyncGetRank =
        new AsyncGetRank() {
          @Override
          public void doFinish() {
            callback.apply(this.getRankItems());
          }
        };
    AsyncOperationProcessor.getInstance().submit(asyncGetRank);
  }

  private static class AsyncGetRank implements AsyncOperation {

    @Getter private final List<RankItem> rankItems = new ArrayList<>();

    @Override
    public void doAsync() {
      try (Jedis jedis = RedisUtil.getJedis()) {
        Set<Tuple> rankSet = jedis.zrevrangeWithScores("Rank", 0, 9);

        int rankId = 1;
        for (Tuple tuple : rankSet) {
          int userId = Integer.parseInt(tuple.getElement());
          String basicInfoJson = jedis.hget("User_" + userId, "BasicInfo");
          if (StringUtils.isBlank(basicInfoJson)) continue;
          // Create RankItem and append to rankItems
          UserBasicInfo basicInfo = JSONObject.parseObject(basicInfoJson, UserBasicInfo.class);
          RankItem rankItem = new RankItem();
          rankItem.setRankId(rankId++);
          rankItem.setUserId(basicInfo.getUserId());
          rankItem.setUserName(basicInfo.getUserName());
          rankItem.setHeroAvatar(basicInfo.getHeroAvatar());
          rankItem.setWin((int) tuple.getScore());
          rankItems.add(rankItem);
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }
}
