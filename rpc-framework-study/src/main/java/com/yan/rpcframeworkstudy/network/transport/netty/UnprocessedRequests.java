package com.yan.rpcframeworkstudy.network.transport.netty;

import com.yan.rpcframeworkstudy.network.dto.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Unprocessed requests by the server.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-02-23
 * @since JDK 1.8.0
 */
public class UnprocessedRequests {
    /**
     * Unprocessed response futures by the server.
     */
    private static final ConcurrentHashMap<String, CompletableFuture<RpcResponse<Object>>>
            UNPROCESSED_RESPONSE_FUTURES_MAP = new ConcurrentHashMap<>();

    /**
     * Put into the map.
     * @param requestId the key to identify the response to which the request corresponds.
     * @param future result
     */
    public void put(final String requestId, final CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_RESPONSE_FUTURES_MAP.put(requestId, future);
    }

    /**
     * Manually set the result of the future object.
     * @param rpcResponse server response
     */
    public void complete(final RpcResponse<Object> rpcResponse) {
        final CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURES_MAP.remove(rpcResponse.getRequestId());
        if (null != future) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException("Get response result failed，corresponding requestId：" + rpcResponse.getRequestId());
        }
    }
}
