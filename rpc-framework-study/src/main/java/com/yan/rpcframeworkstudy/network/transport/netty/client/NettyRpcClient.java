package com.yan.rpcframeworkstudy.network.transport.netty.client;

import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import com.yan.rpcframeworkstudy.network.transport.IRpcRequestTransport;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Netty implementation for RPC client.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-02-22
 * @since JDK 1.8.0
 */
public class NettyRpcClient implements IRpcRequestTransport {

    private final EventLoopGroup eventLoopGroup;

    private final Bootstrap bootstrap;

    public NettyRpcClient() {
        // initialize resources such as eventLoopGroup, bootStrap, etc.
        this.eventLoopGroup = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(this.eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(final SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyRpcClientHandler());
                    }
                });
    }

    /**
     * send request data to server.
     *
     * @param rpcRequest rpc request data
     * @return server response
     */
    @Override
    public Object sendRequest(final RpcRequest rpcRequest) {
        // 1. We need "LoopGroup" object to handle the connection to server.

        // 2. create and option server bootstrap.

        // 3. create future for response.

        // 4. build a channel between the client and the server.

        // 5. if the channel is active,
        // then put the future into the unprocessedRequests and send the request to the server.

        return null;
    }
}
