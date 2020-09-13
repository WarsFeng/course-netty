package cat.wars.model.rank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RankItem {

  private int rankId;

  private int userId;

  private String userName;

  private String heroAvatar;

  private int win;
}
