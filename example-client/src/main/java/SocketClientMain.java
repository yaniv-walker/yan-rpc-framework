import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import com.yan.rpcframeworkstudy.network.transport.IRpcRequestTransport;
import com.yan.rpcframeworkstudy.network.transport.socket.SocketRpcClient;

import java.util.UUID;

/**
 * .
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-02-22
 * @since JDK 1.8.0
 */
public class SocketClientMain {
    public static void main(String[] args) {

        final IRpcRequestTransport rpcRequestTransport = new SocketRpcClient();
//        final RpcRequest rpcRequest = new RpcRequest();

        for (int i = 0; i < 100; i++) {
            final RpcRequest rpcRequest = RpcRequest.builder().requestId(UUID.randomUUID().toString()).build();
            final Object result = rpcRequestTransport.sendRequest(rpcRequest);
            System.out.println(result);
        }
    }
}
