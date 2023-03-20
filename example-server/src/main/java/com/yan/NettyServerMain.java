package com.yan;

import com.yan.rpcframeworkstudy.annotation.RpcScan;
import com.yan.rpcframeworkstudy.network.transport.IRpcServer;
import com.yan.rpcframeworkstudy.network.transport.netty.server.NettyRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * .
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/25 0025
 * @since JDK 1.8.0
 */
@RpcScan(basePackage = {"com.yan"})
public class NettyServerMain {
    public static void main(String[] args) {
//        final RpcServiceConfig config = RpcServiceConfig.builder()
//                .service(new HelloServiceImpl1())
//                .group("test1")
//                .version("version1")
//                .build();
//        final RpcServiceConfig config2 = RpcServiceConfig.builder()
//                .service(new HelloServiceImpl2())
//                .group("test2")
//                .version("version1")
//                .build();
//        final List<RpcServiceConfig> serviceConfigList = Lists.newArrayList(config);
//        serviceConfigList.add(config2);
//        final IRpcServer rpcServer = new NettyRpcServer(serviceConfigList);


//        final IRpcServer rpcServer = new NettyRpcServer();
//        rpcServer.start();

        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(NettyServerMain.class);
        final NettyRpcServer rpcServer = context.getBean(NettyRpcServer.class);
        rpcServer.start();
    }
}
