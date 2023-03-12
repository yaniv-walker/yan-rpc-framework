package com.yan.rpcframeworkstudy.provider.zk;

import com.yan.rpcframeworkcommon.exception.RpcException;
import com.yan.rpcframeworkstudy.config.RpcServiceConfig;
import com.yan.rpcframeworkstudy.provider.IServiceProvider;
import com.yan.rpcframeworkstudy.register.IServiceRegisterAndDiscover;
import com.yan.rpcframeworkstudy.register.zk.ZkServiceRegisterAndDiscoverImpl;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.yan.rpcframeworkstudy.network.contants.RpcConstants.SERVER_PORT;

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
    private final Map<String, Object> registeredServiceMap;

    private final IServiceRegisterAndDiscover serviceRegisterAndDiscover;

    public ZkServiceProvider() {
        registeredServiceMap = new ConcurrentHashMap<>();
        serviceRegisterAndDiscover = new ZkServiceRegisterAndDiscoverImpl();
    }

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
        if (log.isInfoEnabled()) {
            log.info("the [{}] is registered", rpcServiceName);
        }
        if (registeredServiceMap.containsKey(rpcServiceName)) {
            // the service has been exists, needn't register again.
            return;
        }

        registeredServiceMap.put(rpcServiceName, rpcServiceConfig.getService());

        try {
            final String host = InetAddress.getLocalHost().getHostAddress();
            serviceRegisterAndDiscover.register(rpcServiceName, new InetSocketAddress(host, SERVER_PORT));
        } catch (UnknownHostException e) {
            if (log.isErrorEnabled()) {
                log.error("occur exception when getHostAddress", e);
            }
        }
    }

    /**
     * get service by name of rpc service.
     *
     * @param rpcServiceName name of rpc service
     * @return the service of the rpcServiceName
     */
    @Override
    public Object getService(final String rpcServiceName) {
        final Object service = registeredServiceMap.get(rpcServiceName);
        if (null == service) {
            throw new RpcException("Service can not be found");
        }
        return service;
    }
}
