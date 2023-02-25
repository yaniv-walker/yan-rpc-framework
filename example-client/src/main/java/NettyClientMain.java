import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import com.yan.rpcframeworkstudy.network.dto.RpcResponse;
import com.yan.rpcframeworkstudy.network.transport.IRpcRequestTransport;
import com.yan.rpcframeworkstudy.network.transport.netty.client.NettyRpcClient;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * .
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/25 0025
 * @since JDK 1.8.0
 */
@Slf4j
public class NettyClientMain {
    /**
     * request id.
     */
    private static final AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final IRpcRequestTransport rpcRequestTransport = new NettyRpcClient();
        for (int i = 0; i < 100; i++) {
            final RpcRequest request = RpcRequest.builder()
                    .requestId(String.valueOf(atomicInteger.getAndIncrement())).build();
            final Object obj = rpcRequestTransport.sendRequest(request);
            if (obj instanceof CompletableFuture) {
                final CompletableFuture<RpcResponse<Object>> future = (CompletableFuture<RpcResponse<Object>>) obj;
                final RpcResponse<Object> rpcResponse = future.get();
                if (log.isInfoEnabled()) {
                    log.info("business received rpcResponse is [{}]", rpcResponse);
                }
            }
        }
    }
}
