package cat.wars.handler.cmd;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * @program: netty-demo
 * @description:
 * @author: Wars
 * @created: 2020-07-26 13:22
 */
public interface CmdHandler<Cmd extends GeneratedMessageV3> {

  void handle(ChannelHandlerContext context, Cmd cmd);
}
