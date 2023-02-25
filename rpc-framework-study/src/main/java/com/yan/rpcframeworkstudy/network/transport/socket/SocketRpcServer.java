package com.yan.rpcframeworkstudy.network.transport.socket;

import com.yan.rpcframeworkstudy.network.dto.RpcResponse;
import com.yan.rpcframeworkstudy.network.transport.IRpcServer;
import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Socket implementation for the rpc server.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/21 0021
 * @since JDK 1.8.0
 */
@Slf4j
public class SocketRpcServer implements IRpcServer {

    private static final int SERVER_PORT = 9998;

    /**
     * The number of threads to keep in the pool, even if they are idle.
     */
    private static final int CORE_POOL_SIZE = 10;

    /**
     * The maximum number of threads to allow in the pool.
     */
    private static final int MAXIMUM_POOL_SIZE = 20;

    /**
     * This is the maximum time that excess idle threads will wait for new tasks before terminating.
     */
    private static final long KEEP_ALIVE_TIME = 5;

    /**
     * KeepAliveTime unit.
     */
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;

    /**
     * Thread pool factory.
     */
    private static final ThreadFactory threadFactory;

    static {
        threadFactory = Executors.defaultThreadFactory();
    }

    /**
     * start RPC server.
     */
    @Override
    public void start() {
        // Use thread pool
        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TIME_UNIT,
                new ArrayBlockingQueue<>(100), threadFactory);

        try(final ServerSocket serverSocket = new ServerSocket()) {
            // 1. To Bind a local port.
            final String host = InetAddress.getLocalHost().getHostAddress();
            final InetSocketAddress serverSocketAddress = new InetSocketAddress(host, SERVER_PORT);
            serverSocket.bind(serverSocketAddress);

            if (log.isInfoEnabled()) {
                log.info("server address is [{}]", serverSocketAddress);
            }

            Socket socket;
            // 2. Waiting connection of the client.
            while ((socket = serverSocket.accept()) != null) {
                if (log.isInfoEnabled()) {
                    log.info("client connected [{}]", socket.getInetAddress());
                }
                threadPoolExecutor.execute(new SocketRpcRequestHandlerRunnable(socket));
            }

            threadPoolExecutor.shutdown();
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("The connection between the server and the client is failed.", e);
            }
        }


    }
}
