package cat.wars.mq.producer;

import cat.wars.util.RedisUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
public final class MQProducer {

  @Getter private static DefaultMQProducer producer;

  private MQProducer() {}

  public static void init() {
    Properties prop = new Properties();
    try {
      prop.load(RedisUtil.class.getClassLoader().getResourceAsStream("rocketmq.properties"));

      producer = new DefaultMQProducer(prop.getProperty("mq_group"));
      producer.setNamesrvAddr(prop.getProperty("nameserver_addr"));
      producer.start();
      producer.setRetryTimesWhenSendAsyncFailed(3);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public static void sendMessage(String topic, Object message) {
    if (isEmpty(topic) || null == message) return;

    if (null == producer) {
      RuntimeException e = new RuntimeException("RocketMQ producer uninitialized!");
      log.error(e.getMessage(), e);
      throw e;
    }

    Message targetMessage = new Message(topic, JSONObject.toJSONBytes(message));

    try {
      producer.send(targetMessage);
    } catch (Exception e) {
      RuntimeException ex =
          new RuntimeException("RocketMQ producer send message fail!", e);
      log.error(ex.getMessage(), ex);
      throw ex;
    }
  }
}
