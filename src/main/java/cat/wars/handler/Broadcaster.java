package cat.wars.handler;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @program: netty-demo
 * @description: Broadcast
 * @author: Wars
 * @created: 2020-07-24 22:46
 */
public final class Broadcaster {

  private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

  private Broadcaster() {
  }

  /**
   * Add channel to group
   *
   * @param channel {@link Channel}
   */
  public static void addChannel(Channel channel) {
    CHANNEL_GROUP.add(channel);
  }

  /**
   * Remove channel on group
   *
   * @param channel {@link Channel}
   */
  public static void removeChannel(Channel channel) {
    CHANNEL_GROUP.remove(channel);
  }

  /**
   * Broadcast message to all channel
   *
   * @param message Message object
   */
  public static void broadcast(Object message) {
    if (null == message) return;
    CHANNEL_GROUP.writeAndFlush(message);
  }
}
