package cat.wars.handler;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: netty-demo
 * @description: Game message decoder
 * @author: Wars
 * @created: 2020-07-23 14:32
 */
@Slf4j
public class MessageDecoder extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (!(msg instanceof BinaryWebSocketFrame)) return;

    BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
    ByteBuf content = frame.content();

    content.readShort(); // Websocket message length
    int code = content.readShort(); // Message code

    // Decode
    Message.Builder messageBuilder = MessageRecognizer.getMessageBuilderByCode(code);
    if (null == messageBuilder) {
      log.error("Message decode, code invalid, code: {}", code);
      return;
    }
    // Read message
    byte[] body = new byte[content.readableBytes()];
    content.readBytes(body);
    // Build message
    messageBuilder.clear();
    messageBuilder.mergeFrom(body);
    Message message = messageBuilder.build();

    if (null != message) ctx.fireChannelRead(message);
  }
}
