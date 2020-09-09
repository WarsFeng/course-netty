package cat.wars.thread;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public final class AsyncOperationProcessor {

  @Getter private static final AsyncOperationProcessor instance = new AsyncOperationProcessor();

  private final ExecutorService[] pool = new ExecutorService[8];

  private AsyncOperationProcessor() {
    for (int i = 0; i < pool.length; i++) {
      String threadName = "AsyncOperationProcessor_" + i;
      pool[i] =
          Executors.newSingleThreadExecutor(
              r -> {
                Thread thread = new Thread(r);
                thread.setName(threadName);
                return thread;
              });
    }
  }

  public void submit(AsyncOperation asyncOperation) {
    int bindId = Math.abs(asyncOperation.getBindId());
    bindId %= pool.length;

    pool[bindId].submit(
        () -> {
          try {
            asyncOperation.doAsync();
            MainThreadProcessor.getInstance().process(asyncOperation::doFinish);
          } catch (Exception e) {
            log.error(e.getMessage(), e);
          }
        });
  }
}
