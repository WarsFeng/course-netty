package cat.wars.util;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.FileReader;
import java.util.Properties;

@Slf4j
public final class RedisUtil {

  private static JedisPool pool = null;

  /** Init jedis pool, config by redis.properties */
  public static void init() {
    Properties prop = new Properties();
    try {
      prop.load(new FileReader("redis.properties"));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("Redis pool init fail!");
    }
    pool = new JedisPool(prop.getProperty("host"), (Integer) prop.get("port"));
  }

  /**
   * Get jedis instance
   *
   * @return redis.clients.jedis.Jedis
   */
  public static Jedis getJedis() {
    if (null == pool) {
      String errorMessage = "Redis pool is uninitialized!";
      log.error(errorMessage);
      throw new RuntimeException(errorMessage);
    }

    return pool.getResource();
  }
}
