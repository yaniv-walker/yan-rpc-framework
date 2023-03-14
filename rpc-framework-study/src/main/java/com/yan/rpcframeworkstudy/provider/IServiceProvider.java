package com.yan.rpcframeworkstudy.provider;

import com.yan.rpcframeworkcommon.extension.SPI;
import com.yan.rpcframeworkstudy.config.RpcServiceConfig;

/**
 * provide service to consumers.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-03-10
 * @since JDK 1.8.0
 */
@SPI
public interface IServiceProvider {

    /**
     * publish service to register.
     * @param rpcServiceConfig rpc service related attributions
     */
    void publishService(RpcServiceConfig rpcServiceConfig);

    /**
     * get service by name of rpc service.
     * @param rpcServiceName name of rpc service
     * @return the service of the rpcServiceName
     */
    Object getService(String rpcServiceName);

}
