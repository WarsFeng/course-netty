package cat.wars.thread;

import cat.wars.handler.cmd.CmdHandler;
import cat.wars.handler.cmd.CmdHandlerFactory;
import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public final class MainThreadProcessor {

  @Getter private static final MainThreadProcessor instance = new MainThreadProcessor();
  private final ExecutorService executorService =
      Executors.newSingleThreadExecutor(r -> new Thread(r, "MainThreadProcessor"));

  private MainThreadProcessor() {}

  public void process(Runnable runnable) {
    if (null == runnable) return;
    executorService.submit(runnable);
  }

  public void handler(ChannelHandlerContext context, Object msg) {
    executorService.submit(
        () -> {
          if (null == context || null == msg) return;

          CmdHandler<? extends GeneratedMessageV3> cmdHandler =
              CmdHandlerFactory.create(msg.getClass());
          if (null == cmdHandler) {
            log.error("Does Not match the handler!");
            return;
          }

          cmdHandler.handle(context, cast(msg));
        });
  }

  private static <Cmd extends GeneratedMessageV3> Cmd cast(Object msg) {
    if (null == msg) return null;
    return (Cmd) msg;
  }
}
