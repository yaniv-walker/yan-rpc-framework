package com.yan.rpcframeworkstudy.network.transport.netty.server;

import com.yan.rpcframeworkcommon.extension.ExtensionLoader;
import com.yan.rpcframeworkcommon.factory.SingletonFactory;
import com.yan.rpcframeworkstudy.config.RpcServiceConfig;
import com.yan.rpcframeworkstudy.network.transport.IRpcServer;
import com.yan.rpcframeworkstudy.network.transport.netty.codec.RpcMessageDecoder;
import com.yan.rpcframeworkstudy.network.transport.netty.codec.RpcMessageEncoder;
import com.yan.rpcframeworkstudy.provider.IServiceProvider;
import com.yan.rpcframeworkstudy.provider.zk.ZkServiceProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import static com.yan.rpcframeworkstudy.network.contants.RpcConstants.SERVER_PORT;

/**
 * Netty implementation for the rpc server.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-02-23
 * @since JDK 1.8.0
 */
public class NettyRpcServer implements IRpcServer {

    public NettyRpcServer(final List<RpcServiceConfig> rpcServiceConfigs) {
        // TODO: when server start, register all service we need (could use Spring to scan)
//        final IServiceProvider serviceProvider = SingletonFactory.getInstance(ZkServiceProvider.class);
        final IServiceProvider serviceProvider =
                ExtensionLoader.getExtensionLoader(IServiceProvider.class).getExtension("zk");
        for (final RpcServiceConfig rpcServiceConfig : rpcServiceConfigs) {
            serviceProvider.publishService(rpcServiceConfig);
        }
    }

    /**
     * start RPC server.
     */
    @SneakyThrows
    @Override
    public void start() {
        final String host = InetAddress.getLocalHost().getHostAddress();
        // 1. We Need two "LoopGroup" objects, one called "boss" and the other called "worker",
        // to respectively receive and handle connections from clients.
        final EventLoopGroup boss = new NioEventLoopGroup();
        final EventLoopGroup worker = new NioEventLoopGroup();

        // 2. create thread pool executor of event and server bootstrap and initialize its options.
        final DefaultEventExecutorGroup eventExecutorGroup = new DefaultEventExecutorGroup(
                Runtime.getRuntime().availableProcessors() * 2,
                Executors.defaultThreadFactory()
        );

        try {
            final ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new RpcMessageDecoder())
                                    .addLast(new RpcMessageEncoder())
                                    .addLast(eventExecutorGroup, new NettyRpcServerHandler());
                        }
                    });

            // 3. bind port and start listening the port
            final ChannelFuture future = bootstrap.bind(host, SERVER_PORT).sync();

            // 4. waiting the close of listening port
            future.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
            eventExecutorGroup.shutdownGracefully();
        }
    }
}
