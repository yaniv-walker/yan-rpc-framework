package com.yan.rpcframeworkstudy.network.transport;

import com.yan.rpcframeworkcommon.extension.SPI;
import com.yan.rpcframeworkstudy.network.dto.RpcRequest;

/**
 * RPC request data transport to server.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/20 0020
 * @since JDK 1.8.0
 */
@SPI
public interface IRpcRequestTransport {

    /**
     * send request data to server.
     * @param rpcRequest rpc request data
     * @return server response
     */
    Object sendRequest(RpcRequest rpcRequest);
}
