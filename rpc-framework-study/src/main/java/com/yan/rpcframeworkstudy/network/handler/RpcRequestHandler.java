package com.yan.rpcframeworkstudy.network.handler;

import com.yan.rpcframeworkcommon.exception.RpcException;
import com.yan.rpcframeworkcommon.extension.ExtensionLoader;
import com.yan.rpcframeworkcommon.factory.SingletonFactory;
import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import com.yan.rpcframeworkstudy.provider.IServiceProvider;
import com.yan.rpcframeworkstudy.provider.zk.ZkServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * invoke the method of request.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/3/12 0012
 * @since JDK 1.8.0
 */
@Slf4j
public class RpcRequestHandler {

    private final IServiceProvider serviceProvider;

    public RpcRequestHandler() {
//        serviceProvider = SingletonFactory.getInstance(ZkServiceProvider.class);
        serviceProvider = ExtensionLoader.getExtensionLoader(IServiceProvider.class).getExtension("zk");
    }

    /**
     * Processing the rpcRequest: call the method corresponding to request and return its result.
     * @param rpcRequest request from client
     * @return method result
     */
    public Object handle(final RpcRequest rpcRequest) {
        final Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    /**
     * invoke target method corresponding to rpcRequest.
     * @param rpcRequest request from client
     * @param service the server provide
     * @return method result
     */
    private Object invokeTargetMethod(final RpcRequest rpcRequest, final Object service) {
        try {
            final Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            final Object result = method.invoke(service, rpcRequest.getParameters());
            if (log.isInfoEnabled()) {
                log.info("service:[{}] successful invoke the method:[{}]", rpcRequest.getInterfaceName(),
                        rpcRequest.getMethodName());
            }
            return result;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            if (log.isErrorEnabled()) {
                log.error("service:[{}] invoke the method:[{}] failed", rpcRequest.getInterfaceName(),
                        rpcRequest.getMethodName());
            }
            throw new RpcException(e.getMessage(), e);
        }
    }
}
