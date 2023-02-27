package com.yan.rpcframeworkstudy.register;

import com.yan.rpcframeworkstudy.network.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * to register and discover the service.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-02-27
 * @since JDK 1.8.0
 */
public interface IServiceRegisterAndDiscover {

    /**
     * register service.
     * @param rpcServiceName the name of rpc service will be registered
     * @param inetSocketAddress the server address
     */
    void register(String rpcServiceName, InetSocketAddress inetSocketAddress);

    /**
     * look up the service by rpcServiceName.
     * @param rpcRequest rpc service request
     * @return the server address where the service is located
     */
    InetSocketAddress discover(RpcRequest rpcRequest);
}
