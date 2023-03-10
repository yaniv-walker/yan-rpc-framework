package com.yan.rpcframeworkstudy.register.zk.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.yan.rpcframeworkstudy.network.contants.RpcConstants.SERVER_IP_ADDRESS;

/**
 * curator util to operate the zookeeper.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-02-27
 * @since JDK 1.8.0
 */
@Slf4j
public final class CuratorUtil {

    private static final int BASE_SLEEP_TIME = 1000;

    private static final int MAX_RETRIES = 3;

    private static final int MAX_CONNECTED_WAIT_TIME = 30;

    private static CuratorFramework zkClient;

    private static final Set<String> REGISTERED_PATH_SET = new HashSet<>();

    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();

    public static final String ZK_REGISTER_ROOT_PATH =  "/yan-rpc";

    /**
     * get zookeeper server.
     * @return zookeeper server
     */
    public static CuratorFramework getZkClient() {
        // if zookeeper client has been started, return directly
        if (null != zkClient && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }

        // TODO: get zookeeper address by properties config
        final String zkClientAddress = SERVER_IP_ADDRESS + ":2181";
        final ExponentialBackoffRetry exponentialBackoffRetry = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);

        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zkClientAddress)
                .retryPolicy(exponentialBackoffRetry)
                .build();

        zkClient.start();

        try {
            if (zkClient.blockUntilConnected(MAX_CONNECTED_WAIT_TIME, TimeUnit.SECONDS)) {
                if (log.isInfoEnabled()) {
                    log.info("zookeeper server start successfully");
                }
            } else {
                throw new IllegalStateException("zookeeper connected failed");
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("zookeeper connected failed");
        }

        return zkClient;
    }

    /**
     * create persistent node.
     * @param zkClient zookeeper client
     * @param path the node will be registered
     */
    public static void createPersistentNode(final CuratorFramework zkClient, final String path) {
        check(zkClient);

        try {
            // if the path has been registered, then return directly
            if (REGISTERED_PATH_SET.contains(path) || null != zkClient.checkExists().forPath(path)) {
                if (log.isInfoEnabled()) {
                    log.info("The node already exists. The node is [{}]", path);
                }
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                if (log.isInfoEnabled()) {
                    log.info("The node was created successfully. The node is [{}]", path);
                }
            }

            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("create persistent node for path [{}] failed", path);
            }
            throw new IllegalStateException("create persistent node failed", e);
        }
    }

    /**
     * Get the children nodes of the path.
     * @param zkClient zookeeper
     * @param path the node will be registered
     * @return the children nodes of the path
     */
    public static List<String> getChildrenNodes(final CuratorFramework zkClient, final String path) {
        check(zkClient);

        if (SERVICE_ADDRESS_MAP.containsKey(path)) {
            return SERVICE_ADDRESS_MAP.get(path);
        }

        try {
            final List<String> result = zkClient.getChildren().forPath(path);
            // need to listen the changes of service address in map, or the address is not always correct
            SERVICE_ADDRESS_MAP.put(path, result);
            registerWatcher(zkClient, path);
            return result;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("get children nodes of path [{}] failed", path);
            }
            throw new IllegalStateException("get children nodes failed", e);
        }
    }

    /**
     * register the watcher of services in SERVICE_ADDRESS_MAP.
     * @param zkClient zookeeper
     * @param path the node has been registered
     */
    public static void registerWatcher(final CuratorFramework zkClient, final String path) throws Exception {
        check(zkClient);

        final PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, path, true);
        final PathChildrenCacheListener pathChildrenCacheListener = (client, event) -> {
//            if (PathChildrenCacheEvent.Type.CHILD_UPDATED == event.getType()) {
//            }
            final List<String> addressList = client.getChildren().forPath(path);
            SERVICE_ADDRESS_MAP.put(path, addressList);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }

    /**
     * empty the registry of data
     * @param zkClient zookeeper client
     * @param inetSocketAddress server address
     */
    public static void clearRegistry(final CuratorFramework zkClient, final InetSocketAddress inetSocketAddress) {
        final Set<String> toBeRemoved;
        REGISTERED_PATH_SET.stream().parallel().forEach(path -> {
            try {
                if (path.endsWith(inetSocketAddress.toString())) {
                    zkClient.delete().forPath(path);

                }
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("empty the registry of path [{}] failed", path);
                }
                throw new IllegalStateException("empty the registry failed", e);
            }
        });
    }

    /**
     * check if the parameters is correct.
     * @param zkClient zookeeper
     */
    private static void check(final CuratorFramework zkClient) {
        if (null == zkClient) {
            throw new IllegalArgumentException("zkClient is null");
        }
    }
}
