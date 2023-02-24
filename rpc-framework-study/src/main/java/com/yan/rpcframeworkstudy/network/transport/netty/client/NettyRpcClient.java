package com.yan.rpcframeworkstudy.network.transport.netty.client;

import com.yan.rpcframeworkcommon.enums.RpcCodecEnum;
import com.yan.rpcframeworkcommon.enums.RpcMessageTypeEnum;
import com.yan.rpcframeworkcommon.factory.SingletonFactory;
import com.yan.rpcframeworkstudy.network.contants.RpcConstants;
import com.yan.rpcframeworkstudy.network.dto.RpcMessage;
import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import com.yan.rpcframeworkstudy.network.dto.RpcResponse;
import com.yan.rpcframeworkstudy.network.transport.IRpcRequestTransport;
import com.yan.rpcframeworkstudy.network.transport.netty.codec.RpcMessageDecoder;
import com.yan.rpcframeworkstudy.network.transport.netty.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Netty implementation for RPC client.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-02-22
 * @since JDK 1.8.0
 */
@Slf4j
public class NettyRpcClient implements IRpcRequestTransport {
    /**
     * request id.
     */
    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    private final EventLoopGroup eventLoopGroup;

    private final Bootstrap bootstrap;

    private final UnprocessedRequests unprocessedRequests;

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
                    protected void initChannel(final SocketChannel ch) {
                        ch.pipeline().addLast(new RpcMessageEncoder())
                                .addLast(new RpcMessageDecoder())
                                .addLast(new NettyRpcClientHandler());
                    }
                });
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    /**
     * send request data to server.
     *
     * @param rpcRequest rpc request data
     * @return server response future
     */
    @Override
    public Object sendRequest(final RpcRequest rpcRequest) {
        // 1. We need "LoopGroup" object to handle the connection to server.
        // 2. create and option server bootstrap.

        // 3. create future for response.
        final CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();

        // 4. get server address and build a channel between the client and the server.
        try {
            final SocketAddress inetSocketAddress =
                    new InetSocketAddress(RpcConstants.SERVER_IP_ADDRESS, RpcConstants.SERVER_PORT);
            final Channel channel = this.bootstrap.connect(inetSocketAddress).sync().channel();

            if (channel.isActive()) {
                // 5. if the channel is active,
                // then put the future into the unprocessedRequests and send the request to the server.

                final int requestId = this.atomicInteger.getAndIncrement();
                this.unprocessedRequests.put(String.valueOf(requestId), resultFuture);

                final RpcMessage rpcMessage = RpcMessage.builder().messageType(RpcMessageTypeEnum.REQUEST.getCode())
                        .codec(RpcCodecEnum.JAVA_BASIC.getCode())
                        .requestId(requestId)
                        .data(rpcRequest)
                        .build();
                channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        if (log.isInfoEnabled()) {
                            log.info("client send message successful, message = [{}]", rpcMessage);
                        }
                    } else {
                        // handle exception
                        future.channel().close();
                        resultFuture.completeExceptionally(future.cause());
                        if (log.isErrorEnabled()) {
                            log.error("client send message failed ", future.cause());
                        }
                    }
                });

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return resultFuture;
    }
}
