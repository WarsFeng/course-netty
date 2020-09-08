package cat.wars.handler.cmd.dao;

import cat.wars.model.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

  UserEntity getUserByName(String username);

  void add(UserEntity target);
}
