package com.yan.rpcframeworkstudy.register.zk;

import com.yan.rpcframeworkcommon.extension.ExtensionLoader;
import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import com.yan.rpcframeworkstudy.register.IServiceRegisterAndDiscover;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZkServiceRegisterAndDiscoverImplTest {

    final IServiceRegisterAndDiscover registerAndDiscover =
            ExtensionLoader.getExtensionLoader(IServiceRegisterAndDiscover.class).getExtension("zk");
    final RpcRequest rpcRequest = RpcRequest.builder().interfaceName("").group("0").version("1").build();

    @Test
    void register() {
        registerAndDiscover.register(rpcRequest.getRpcServiceName(),
                new InetSocketAddress("192.168.31.54", 9001));
        final InetSocketAddress discover = registerAndDiscover.discover(rpcRequest);
        assertEquals("/192.168.31.54:9001", discover.toString());
    }
}