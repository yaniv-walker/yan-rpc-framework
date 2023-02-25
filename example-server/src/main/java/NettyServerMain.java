import com.yan.rpcframeworkstudy.network.transport.IRpcServer;
import com.yan.rpcframeworkstudy.network.transport.netty.server.NettyRpcServer;

/**
 * .
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/25 0025
 * @since JDK 1.8.0
 */
public class NettyServerMain {
    public static void main(String[] args) {
        final IRpcServer rpcServer = new NettyRpcServer();
        rpcServer.start();
    }
}
