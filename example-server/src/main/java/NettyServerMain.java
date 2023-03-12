import com.yan.rpcframeworkstudy.config.RpcServiceConfig;
import com.yan.rpcframeworkstudy.network.transport.IRpcServer;
import com.yan.rpcframeworkstudy.network.transport.netty.server.NettyRpcServer;
import hello.HelloServiceImpl1;

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
        final RpcServiceConfig config = RpcServiceConfig.builder()
                .service(new HelloServiceImpl1())
                .group("test1")
                .version("version1")
                .build();
        final IRpcServer rpcServer = new NettyRpcServer(config);
        rpcServer.start();
    }
}
