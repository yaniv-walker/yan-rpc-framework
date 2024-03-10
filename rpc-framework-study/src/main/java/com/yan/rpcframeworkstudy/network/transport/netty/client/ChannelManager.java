package com.yan.rpcframeworkstudy.network.transport.netty.client;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * netty channel manager.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/3/21 0021
 * @since JDK 1.8.0
 */
@Slf4j
public class ChannelManager {

    /**
     * key: client ip address
     * value: created channel between client and server
     */
    private final Map<String, Channel> cachedChannel = new ConcurrentHashMap<>();

    private final Set<Channel> activatedChannel = new HashSet<>();

    /**
     * get channel by socket address
     * @param socketAddress server socket address
     * @return channel between client and server
     */
    public Channel get(final SocketAddress socketAddress) {
        final Channel existChannel = cachedChannel.get(socketAddress.toString());
        if (null != existChannel && existChannel.isOpen()) {
            return existChannel;
        }

        return null;
    }

    /**
     * put channel corresponds to socketAddress.
     * @param socketAddress server socket address
     * @param channel channel between client and server
     */
    public void put(final SocketAddress socketAddress, final Channel channel) {
        cachedChannel.put(socketAddress.toString(), channel);
        activatedChannel.add(channel);
        if (log.isInfoEnabled()) {
            log.info("cachedChannel size: [{}]. activatedChannel size: [{}]", cachedChannel.size(), countActivatedChannel());
        }
    }

    /**
     * count activated channel.
     */
    public int countActivatedChannel() {
        return activatedChannel.size();
    }

}
