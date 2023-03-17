package com.yan.rpcframeworkstudy.proxy;

import com.yan.rpcframeworkcommon.enums.RpcErrorMessageEnum;
import com.yan.rpcframeworkcommon.enums.RpcResponseCodeEnum;
import com.yan.rpcframeworkcommon.exception.RpcException;
import com.yan.rpcframeworkstudy.config.RpcServiceConfig;
import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import com.yan.rpcframeworkstudy.network.dto.RpcResponse;
import com.yan.rpcframeworkstudy.network.transport.IRpcRequestTransport;
import com.yan.rpcframeworkstudy.network.transport.netty.client.NettyRpcClient;
import com.yan.rpcframeworkstudy.network.transport.socket.SocketRpcClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * rpc client proxy.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-03-16
 * @since JDK 1.8.0
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {
    /**
     * client.
     */
    private final IRpcRequestTransport rpcRequestTransport;

    /**
     * config.
     */
    private final RpcServiceConfig rpcServiceConfig;

    public RpcClientProxy(final IRpcRequestTransport rpcRequestTransport, final RpcServiceConfig rpcServiceConfig) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = rpcServiceConfig;
    }


    /**
     * Get the proxy of interfaceClazz.
     * @param interfaceClazz interface
     * @return proxy of interface
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(final Class<T> interfaceClazz) {
        return (T) Proxy.newProxyInstance(interfaceClazz.getClassLoader(), new Class<?>[]{interfaceClazz}, this);
    }

    /**
     * Processes the rpc client sending request for the invocation of method in server.
     *
     * @param proxy  the proxy instance that the method was invoked on
     * @param method the {@code Method} instance corresponding to
     *               the interface method invoked on the proxy instance.  The declaring
     *               class of the {@code Method} object will be the interface that
     *               the method was declared in, which may be a superinterface of the
     *               proxy interface that the proxy class inherits the method through.
     * @param args   an array of objects containing the values of the
     *               arguments passed in the method invocation on the proxy instance,
     *               or {@code null} if interface method takes no arguments.
     *               Arguments of primitive types are wrapped in instances of the
     *               appropriate primitive wrapper class, such as
     *               {@code java.lang.Integer} or {@code java.lang.Boolean}.
     * @return the method result in server.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (log.isInfoEnabled()) {
            log.info("invoked proxy method: [{}]", method.getName());
        }

        final RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .parameters(args)
                .group(rpcServiceConfig.getGroup())
                .version(rpcServiceConfig.getVersion())
                .build();

        final Object response = rpcRequestTransport.sendRequest(rpcRequest);
        RpcResponse<Object> rpcResponse = null;
        if (rpcRequestTransport instanceof NettyRpcClient) {
            final CompletableFuture<RpcResponse<Object>> resultFuture = (CompletableFuture<RpcResponse<Object>>) response;
            rpcResponse = resultFuture.get();
        } else if (rpcRequestTransport instanceof SocketRpcClient) {
            rpcResponse = (RpcResponse<Object>) response;
        }

        this.check(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }

    /**
     * check correction of result
     * @param rpcRequest request
     * @param rpcResponse response
     */
    private void check(final RpcRequest rpcRequest, final RpcResponse<Object> rpcResponse) {
        if (null == rpcResponse) {
            throw new RpcException(String.format("%s. Interface name: %s. client object: %s",
                    RpcErrorMessageEnum.UNKNOWN_CLIENT.getMessage(),
                    rpcRequest.getInterfaceName(),
                    rpcRequestTransport.getClass().getName()));
        }

        if (null == rpcResponse.getCode()) {
            throw new RpcException(String.format("%s. Interface name: %s",
                    RpcErrorMessageEnum.RESPONSE_HAVE_NOT_CODE.getMessage(), rpcRequest.getInterfaceName()));
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(String.format("%s. Interface name: %s",
                    RpcErrorMessageEnum.RESPONSE_NOT_MATCH.getMessage(), rpcRequest.getInterfaceName()));
        }

        if (RpcResponseCodeEnum.FAIL.getCode() == rpcResponse.getCode()) {
            throw new RpcException(String.format("%s. Interface name: %s",
                    RpcErrorMessageEnum.RESPONSE_FAILED.getMessage(), rpcRequest.getInterfaceName()));
        }
    }
}
