package com.yan.rpcframeworkstudy.network.transport.netty.server;

import com.yan.rpcframeworkstudy.network.transport.IRpcServer;

/**
 * .
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-02-23
 * @since JDK 1.8.0
 */
public class NettypRpcServer implements IRpcServer {
    /**
     * start RPC server.
     */
    @Override
    public void start() {
        // 1. We Need two "LoopGroup" objects, one called "boss" and the other called "worker",
        // to respectively receive and handle connections from clients.

        // 2.
    }
}
