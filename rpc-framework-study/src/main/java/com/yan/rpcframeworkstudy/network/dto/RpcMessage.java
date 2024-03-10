package com.yan.rpcframeworkstudy.network.dto;

import lombok.*;

import java.io.Serializable;
import java.util.Optional;

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
     * message type such as 'ping', 'method result', etc.
     */
    private byte messageType;

    /**
     * serialization type.
     */
    private byte codec;

    /**
     * compress type.
     */
    private byte compress;

    /**
     * request ID.
     */
    private int requestId;

    /**
     * message body.
     */
    private Object data;

//    public static void main(String[] args) {
//        final RpcMessage rpcMessage = null;
//        // first
//        Optional.ofNullable(rpcMessage)
//                .map(it -> Optional.ofNullable(it.getData()).orElse("DefaultData"))
//                .ifPresent(data -> rpcMessage.setData(data));
//        System.out.println("1:" + rpcMessage);
//
//        // second
//        Optional.ofNullable(rpcMessage)
//                .map(it -> Optional.ofNullable(it.getData()).orElse("DefaultData"))
//                .ifPresent(rpcMessage::setData);
//        System.out.println("2:" + rpcMessage);
//    }

}
