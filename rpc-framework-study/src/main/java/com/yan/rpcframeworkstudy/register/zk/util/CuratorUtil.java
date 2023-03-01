package com.yan.rpcframeworkstudy.register.zk.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

    private static CuratorFramework zkServer;

    private static final Set<String> REGISTERED_PATH_SET = new HashSet<>();

    /**
     * get zookeeper server.
     * @return zookeeper server
     */
    public static CuratorFramework getZkServer() {
        // if zookeeper client has been started, return directly
        if (null != zkServer && zkServer.getState() == CuratorFrameworkState.STARTED) {
            return zkServer;
        }

        // TODO: get zookeeper address by properties config
        final String zkServerAddress = "";
        final ExponentialBackoffRetry exponentialBackoffRetry = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);

        zkServer = CuratorFrameworkFactory.builder()
                .connectString(zkServerAddress)
                .retryPolicy(exponentialBackoffRetry)
                .build();

        zkServer.start();

        try {
            if (zkServer.blockUntilConnected(MAX_CONNECTED_WAIT_TIME, TimeUnit.SECONDS)) {
                if (log.isInfoEnabled()) {
                    log.info("zookeeper server start successfully");
                }
            } else {
                throw new IllegalStateException("zookeeper connected failed");
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("zookeeper connected failed");
        }

        return zkServer;
    }

    /**
     * create persistent node.
     * @param zkServer zookeeper server
     * @param path the node will be registered
     */
    public static void createPersistentNode(final CuratorFramework zkServer,final String path) {
        if (null == zkServer) {
            throw new IllegalArgumentException("zkServer is null");
        }

        try {
            // if the path has been registered, then return directly
            if (REGISTERED_PATH_SET.contains(path) || null != zkServer.checkExists().forPath(path)) {
                if (log.isInfoEnabled()) {
                    log.info("The node already exists. The node is [{}]", path);
                }
            } else {
                zkServer.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
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
}
