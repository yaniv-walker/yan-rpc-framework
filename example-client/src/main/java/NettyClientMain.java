import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import com.yan.rpcframeworkstudy.network.transport.IRpcRequestTransport;
import com.yan.rpcframeworkstudy.network.transport.netty.client.NettyRpcClient;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * .
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/25 0025
 * @since JDK 1.8.0
 */
public class NettyClientMain {
    /**
     * request id.
     */
    private static final AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void main(String[] args) {
        final IRpcRequestTransport rpcRequestTransport = new NettyRpcClient();
        final RpcRequest request = RpcRequest.builder()
                .requestId(String.valueOf(atomicInteger.getAndIncrement())).build();
        rpcRequestTransport.sendRequest(request);
    }
}
