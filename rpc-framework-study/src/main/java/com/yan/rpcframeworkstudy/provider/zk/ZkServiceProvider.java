package com.yan.rpcframeworkstudy.provider.zk;

import com.yan.rpcframeworkstudy.config.RpcServiceConfig;
import com.yan.rpcframeworkstudy.provider.IServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The zookeeper implementation for provide service to consumers.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-03-10
 * @since JDK 1.8.0
 */
@Slf4j
public class ZkServiceProvider implements IServiceProvider {
    /**
     * the service is registered.
     * key: the rpc service name(interface name + group + version).
     * value: the interface service.
     */
    private Map<String, Object> registeredServiceMap = new ConcurrentHashMap<>();

    /**
     * publish service to register.
     *
     * @param rpcServiceConfig rpc service related attributions
     */
    @Override
    public void publishService(final RpcServiceConfig rpcServiceConfig) {
        if (log.isInfoEnabled()) {
            log.info("the param rpcServiceConfig is [{}]", rpcServiceConfig);
        }

        final String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        if (registeredServiceMap.containsKey(rpcServiceName)) {
            // the service has been exists, needn't register again.
            return;
        }
    }
}
