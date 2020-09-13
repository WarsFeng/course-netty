package cat.wars.handler.cmd;

import cat.wars.handler.cmd.service.RankService;
import cat.wars.model.MessageProtocol.GetRankCmd;
import cat.wars.model.MessageProtocol.GetRankResult;
import cat.wars.model.rank.RankItem;
import io.netty.channel.ChannelHandlerContext;

public class GetRankCmdHandler implements CmdHandler<GetRankCmd> {

  @Override
  public void handle(ChannelHandlerContext context, GetRankCmd cmd) {
    RankService.getInstance()
        .getRank(
            rankItems -> {
              GetRankResult.Builder resultBuilder = GetRankResult.newBuilder();
              for (RankItem rankItem : rankItems) {
                GetRankResult.RankItem.Builder itemResult =
                    GetRankResult.RankItem.newBuilder()
                        .setRankId(rankItem.getRankId())
                        .setUserId(rankItem.getUserId())
                        .setUserName(rankItem.getUserName())
                        .setHeroAvatar(rankItem.getHeroAvatar())
                        .setWin(rankItem.getWin());
                resultBuilder.addRankItem(itemResult);
              }

              context.writeAndFlush(resultBuilder.build());
              return null;
            });
  }
}
