package cat.wars.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

public final class MySQLSessionFactory {

  private static SqlSessionFactory sqlSessionFactory;

  private MySQLSessionFactory() {}

  public static void init() {
    try {
      sqlSessionFactory =
          new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("SqlMapConfig.xml"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static SqlSession openSession() {
    if (null == sqlSessionFactory) throw new RuntimeException("SQL session factory not init!");
    return sqlSessionFactory.openSession(true);
  }
}
