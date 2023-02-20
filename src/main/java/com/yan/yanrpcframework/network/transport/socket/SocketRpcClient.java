package com.yan.yanrpcframework.network.transport.socket;

import com.yan.yanrpcframework.network.dto.RpcRequest;
import com.yan.yanrpcframework.network.transport.IRpcRequestTransport;

/**
 * .
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/20 0020
 * @since JDK 1.8.0
 */
public class SocketRpcClient implements IRpcRequestTransport {
    /**
     * send request data to server.
     *
     * @param rpcRequest rpc request data
     * @return server response
     */
    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        // 1. Need to look up the connection info of the server


        // 2. Connect with the socket of the server using info 1.

        // 3. Send data to the server through the output stream.

        // 4. Read RpcResponse from the input stream.

        return null;
    }
}
