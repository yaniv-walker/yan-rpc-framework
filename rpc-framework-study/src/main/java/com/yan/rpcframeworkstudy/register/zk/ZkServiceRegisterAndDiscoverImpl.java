package com.yan.rpcframeworkstudy.register.zk;

import com.yan.rpcframeworkcommon.exception.RpcException;
import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import com.yan.rpcframeworkstudy.register.IServiceRegisterAndDiscover;
import com.yan.rpcframeworkstudy.register.zk.util.CuratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;

import static com.yan.rpcframeworkstudy.register.zk.util.CuratorUtil.ZK_REGISTER_ROOT_PATH;

/**
 * zookeeper implementation.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-02-27
 * @since JDK 1.8.0
 */
@Slf4j
public class ZkServiceRegisterAndDiscoverImpl implements IServiceRegisterAndDiscover {
    /**
     * register service.
     *
     * @param rpcServiceName    the name of rpc service will be registered
     * @param inetSocketAddress the server address
     */
    @Override
    public void register(final String rpcServiceName, final InetSocketAddress inetSocketAddress) {
        final String path = String.format("%s/%s%s", ZK_REGISTER_ROOT_PATH, rpcServiceName, inetSocketAddress.toString());
        final CuratorFramework zkClient = CuratorUtil.getZkClient();
        CuratorUtil.createPersistentNode(zkClient, path);
    }

    /**
     * look up the service by rpcServiceName.
     *
     * @param rpcRequest rpc service request
     * @return the server address where the service is located
     */
    @Override
    public InetSocketAddress discover(final RpcRequest rpcRequest) {
        final String rpcServiceName = rpcRequest.getRpcServiceName();
        final String path = String.format("%s/%s", ZK_REGISTER_ROOT_PATH, rpcServiceName);
        final CuratorFramework zkClient = CuratorUtil.getZkClient();
        final List<String> serverAddressList = CuratorUtil.getChildrenNodes(zkClient, path);
        if (CollectionUtils.isEmpty(serverAddressList)) {
            throw new RpcException("service can not be found: " + path);
        }

        // TODO: load balance
        final String targetServiceAddress = serverAddressList.get(0);
        if (log.isInfoEnabled()) {
            log.info("the service address has been found: [{}]", targetServiceAddress);
        }

        final String[] socketAddressArr = targetServiceAddress.split(":");
        final String host = socketAddressArr[0];
        final int port = Integer.parseInt(socketAddressArr[1]);
        return new InetSocketAddress(host, port);
    }
}
