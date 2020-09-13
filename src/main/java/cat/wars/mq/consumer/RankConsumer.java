package cat.wars.mq.consumer;

import cat.wars.handler.cmd.service.RankService;
import cat.wars.mq.message.RankKillUserMessage;
import cat.wars.util.RedisUtil;
import com.alibaba.fastjson.JSONObject;
import com.sun.tools.jconsole.JConsoleContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.Properties;

@Slf4j
public final class RankConsumer {

  private RankConsumer() {}

  public static void init() {
    Properties prop = new Properties();
    try {
      prop.load(RedisUtil.class.getClassLoader().getResourceAsStream("rocketmq.properties"));

      DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(prop.getProperty("mq_group"));
      consumer.setNamesrvAddr(prop.getProperty("nameserver_addr"));
      consumer.subscribe("Killed", "*");
      consumer.registerMessageListener(
          (MessageListenerConcurrently)
              (list, context) -> {
                for (MessageExt messageExt : list) {
                  if (null == messageExt) continue;
                  RankKillUserMessage message =
                      JSONObject.parseObject(messageExt.getBody(), RankKillUserMessage.class);

                  RankService.getInstance()
                      .refreshRank(message.getWinnerUserId(), message.getWinnerUserId());
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
              });

      consumer.start();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
