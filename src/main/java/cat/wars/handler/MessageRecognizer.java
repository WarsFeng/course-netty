package cat.wars.handler;

import cat.wars.model.MessageProtocol;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static cat.wars.model.MessageProtocol.MsgCode;
import static cat.wars.model.MessageProtocol.MsgCode.values;

/**
 * @program: netty-demo
 * @description:
 * @author: Wars
 * @created: 2020-08-07 12:34
 */
@Slf4j
public final class MessageRecognizer {

  /**
   * Message decode map, message code to message instance
   */
  private static final Map<Integer, GeneratedMessageV3> MSG_CODE_TO_INSTANCE_MAP = new HashMap<>();

  /**
   * Message encode map, message class to message code
   */
  private static final Map<Class<?>, Integer> MSG_CLASS_TO_CODE_MAP = new HashMap<>();

  private MessageRecognizer() {
  }

  public static void init() {
    Class<?>[] classes = MessageProtocol.class.getDeclaredClasses();
    MsgCode[] msgCodes = values();
    Map<String, Integer> msgCodeMap = new HashMap<>();

    for (MsgCode msgCode : msgCodes) { // Slim msgCode name, put to codeMap
      String slimName = msgCode.name().replaceAll("_", "").toLowerCase();
      if (!"unrecognized".equals(slimName)) msgCodeMap.put(slimName, msgCode.getNumber());
    }

    for (Class<?> clazz : classes) { // Put to map
      if (!GeneratedMessageV3.class.isAssignableFrom(clazz)) continue;
      String slimName = clazz.getSimpleName().toLowerCase();
      Integer msgCode = msgCodeMap.get(slimName); // Try get code
      if (null == msgCode) continue;
      log.info("Scanned recognizer mapping: {} <-> {}", clazz.getName(), msgCode);

      try {
        if (slimName.endsWith("cmd")) // Put to decode map
          MSG_CODE_TO_INSTANCE_MAP.put(msgCode, (GeneratedMessageV3) clazz.getDeclaredMethod("getDefaultInstance").invoke(clazz));
        else if (slimName.endsWith("result")) // Put to encode map
          MSG_CLASS_TO_CODE_MAP.put(clazz, msgCode);
        else log.warn("Message class name invalid! class name: {}", clazz.getName());
      } catch (Exception e) {
        log.error("Init reflex error", e);
      }
    }
  }


  public static Message.Builder getMessageBuilderByCode(Integer msgCode) {
    if (null == msgCode || 0 > msgCode) return null;
    GeneratedMessageV3 msgInstance = MSG_CODE_TO_INSTANCE_MAP.get(msgCode);
    if (null == msgInstance) return null;

    return msgInstance.toBuilder();
  }

  public static Integer getMessageCodeByClass(Class<?> resultClass) {
    if (null == resultClass) return null;
    return MSG_CLASS_TO_CODE_MAP.get(resultClass);
  }
}
