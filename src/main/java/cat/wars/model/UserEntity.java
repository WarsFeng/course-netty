package cat.wars.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserEntity {

  private Integer userId;

  private String userName;

  private String password;

  private String heroAvatar;
}
