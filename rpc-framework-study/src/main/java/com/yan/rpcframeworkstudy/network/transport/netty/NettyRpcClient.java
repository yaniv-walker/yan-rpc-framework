package com.yan.rpcframeworkstudy.network.transport.netty;

import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import com.yan.rpcframeworkstudy.network.transport.IRpcRequestTransport;

/**
 * Netty implementation for RPC client.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-02-22
 * @since JDK 1.8.0
 */
public class NettyRpcClient implements IRpcRequestTransport {
    /**
     * send request data to server.
     *
     * @param rpcRequest rpc request data
     * @return server response
     */
    @Override
    public Object sendRequest(final RpcRequest rpcRequest) {
        return null;
    }
}
