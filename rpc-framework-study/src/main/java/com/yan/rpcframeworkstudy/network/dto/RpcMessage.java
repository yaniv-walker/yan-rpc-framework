package com.yan.rpcframeworkstudy.network.dto;

import lombok.*;

import java.io.Serializable;

/**
 * RPC message for network transport.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/21 0021
 * @since JDK 1.8.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RpcMessage implements Serializable {

    /**
     * serialization type.
     */
    private byte codec;

    /**
     * request ID.
     */
    private byte[] requestId;

    /**
     * message body.
     */
    private Object data;

}
