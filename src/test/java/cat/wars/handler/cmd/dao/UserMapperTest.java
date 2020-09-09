package cat.wars.handler.cmd.dao;

import cat.wars.model.UserEntity;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserMapperTest {
  private static UserMapper mapper;

  @BeforeClass
  public static void setUpMybatisDatabase() {
    SqlSessionFactory builder =
        new SqlSessionFactoryBuilder()
            .build(UserMapperTest.class.getClassLoader().getResourceAsStream("SqlMapConfig.xml"));
    // you can use builder.openSession(false) to not commit to database
    mapper = builder.getConfiguration().getMapper(UserMapper.class, builder.openSession(true));
  }

  @Test
  public void testGetUserByName() {
    System.out.println(mapper.getUserByName("wars"));
  }

  @Test
  public void testAdd() {
    UserEntity target = new UserEntity();
    target.setUserName("wars");
    target.setPassword("wars");
    target.setHeroAvatar("wars");

    mapper.add(target);
  }
}
