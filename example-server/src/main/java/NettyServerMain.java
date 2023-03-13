import com.google.common.collect.Lists;
import com.yan.rpcframeworkstudy.config.RpcServiceConfig;
import com.yan.rpcframeworkstudy.network.transport.IRpcServer;
import com.yan.rpcframeworkstudy.network.transport.netty.server.NettyRpcServer;
import hello.HelloServiceImpl1;
import hello.HelloServiceImpl2;

import java.util.Arrays;
import java.util.List;

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
        final RpcServiceConfig config2 = RpcServiceConfig.builder()
                .service(new HelloServiceImpl2())
                .group("test2")
                .version("version1")
                .build();
        final List<RpcServiceConfig> serviceConfigList = Lists.newArrayList(config);
        serviceConfigList.add(config2);
        final IRpcServer rpcServer = new NettyRpcServer(serviceConfigList);
        rpcServer.start();
    }
}
