package cat.wars.handler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: netty-demo
 * @description: Game message decoder
 * @author: Wars
 * @created: 2020-07-23 15:24
 */
@Slf4j
public class MessageEncoder extends ChannelOutboundHandlerAdapter {

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    if (!(msg instanceof GeneratedMessageV3)) {
      super.write(ctx, msg, promise);
      return;
    }
    // Result code
    Integer code = MessageRecognizer.getMessageCodeByClass(msg.getClass());
    if (null == code || 0 > code) {
      log.error("Message encode, result class invalid, class: {}", msg.getClass().getName());
      return;
    }
    // Result buffer
    ByteBuf buffer = ctx.alloc().buffer();
    buffer.writeShort((short) 0);
    buffer.writeShort((short) (int) code);
    buffer.writeBytes(((GeneratedMessageV3) msg).toByteArray());
    // Write
    BinaryWebSocketFrame frame = new BinaryWebSocketFrame(buffer);
    super.write(ctx, frame, promise);
  }
}
