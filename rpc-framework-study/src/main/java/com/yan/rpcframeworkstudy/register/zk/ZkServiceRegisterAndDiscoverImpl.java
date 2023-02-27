package com.yan.rpcframeworkstudy.register.zk;

import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import com.yan.rpcframeworkstudy.register.IServiceRegisterAndDiscover;

import java.net.InetSocketAddress;

/**
 * zookeeper implementation.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-02-27
 * @since JDK 1.8.0
 */
public class ZkServiceRegisterAndDiscoverImpl implements IServiceRegisterAndDiscover {
    /**
     * register service.
     *
     * @param rpcServiceName    the name of rpc service will be registered
     * @param inetSocketAddress the server address
     */
    @Override
    public void register(final String rpcServiceName, final InetSocketAddress inetSocketAddress) {

    }

    /**
     * look up the service by rpcServiceName.
     *
     * @param rpcRequest rpc service request
     * @return the server address where the service is located
     */
    @Override
    public InetSocketAddress discover(RpcRequest rpcRequest) {
        return null;
    }
}