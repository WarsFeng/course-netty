package cat.wars.thread;

public interface AsyncOperation {

  default int getBindId() {
    return 0;
  }

  void doAsync();

  default void doFinish() {}
}
