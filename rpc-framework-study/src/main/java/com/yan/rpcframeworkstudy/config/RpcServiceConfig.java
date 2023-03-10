package com.yan.rpcframeworkstudy.config;

import lombok.*;

/**
 * configure the information that the rpc service needs for publishing and discovery.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-03-10
 * @since JDK 1.8.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class RpcServiceConfig {
    /**
     * the service will be provided.
     */
    private Object service;

    /**
     * when the interface has multiple implementation classes, distinguish by the group.
     */
    private String group;

    /**
     * the service version.
     */
    private String version;

    public String getServiceName() {
        return service.getClass().getInterfaces()[0].getCanonicalName();
    }

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }
}
