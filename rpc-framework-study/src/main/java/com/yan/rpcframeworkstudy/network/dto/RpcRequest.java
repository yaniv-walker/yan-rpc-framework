package com.yan.rpcframeworkstudy.network.dto;

import lombok.*;

import java.io.Serializable;

/**
 * RPC request object.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/20 0020
 * @since JDK 1.8.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RpcRequest implements Serializable {

    /**
     * To identify the response that corresponds to the request.
     */
    private String requestId;

    /**
     * Remote interface name.
     */
    private String interfaceName;

    /**
     * Remote method name.
     */
    private String methodName;

    /**
     * Remote parameters.
     */
    private Object[] parameters;

    /**
     * Remote parameter types.
     */
    private Class<?>[] paramTypes;

    /**
     * To select one class from the group of classes that have implemented the same interface.
     */
    private String group;

    /**
     * To identify different version of project.
     */
    private String version;

    /**
     * get the name of rpc service.
     * @return the name of rpc service
     */
    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
