package cat.wars.handler.cmd;

import cat.wars.model.MessageProtocol;
import cat.wars.util.PackageUtil;
import com.google.protobuf.GeneratedMessageV3;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @program: netty-demo
 * @description:
 * @author: Wars
 * @created: 2020-07-29 16:41
 */
@Slf4j
public final class CmdHandlerFactory {

  private static final Map<Class<?>, CmdHandler<? extends GeneratedMessageV3>> handlerMap = new HashMap<>();

  private CmdHandlerFactory() {
  }

  public static void init() {
    Class<?>[] cmdClasses = MessageProtocol.class.getDeclaredClasses();
    Set<Class<?>> handlerClasses = PackageUtil.listSubClazz("cat.wars.handler.cmd", false, CmdHandler.class);
    // Make tmp handler Map<CmdClass, HandlerClass>
    Map<Class<?>, Class<?>> tmpHandlerMap = new HashMap<>();
    for (Class<?> handlerClass : handlerClasses) {
      if (Modifier.isAbstract(handlerClass.getModifiers())) continue;
      Method[] methods = handlerClass.getDeclaredMethods();

      for (Method method : methods) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (!"handle".equals(method.getName())) continue;
        if (2 > parameterTypes.length
                || !GeneratedMessageV3.class.isAssignableFrom(parameterTypes[1]))
          continue;

        tmpHandlerMap.put(parameterTypes[1], handlerClass);
      }
    }

    for (Class<?> cmdClass : cmdClasses) {
      if (!GeneratedMessageV3.class.isAssignableFrom(cmdClass)) continue;
      Class<?> handlerClass = tmpHandlerMap.get(cmdClass); // Try get handler
      if (null == handlerClass) continue;
      log.info("Scanned handler: {} <-> {}", cmdClass.getName(), handlerClass.getName());

      try {
        handlerMap.put(cmdClass, (CmdHandler<? extends GeneratedMessageV3>) handlerClass.getDeclaredConstructor().newInstance());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static CmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClass) {
    return handlerMap.get(msgClass);
  }
}
