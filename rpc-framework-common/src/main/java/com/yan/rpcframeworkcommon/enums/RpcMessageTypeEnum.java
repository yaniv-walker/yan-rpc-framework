package com.yan.rpcframeworkcommon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * message type enum.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-02-24
 * @since JDK 1.8.0
 */
@AllArgsConstructor
@Getter
public enum RpcMessageTypeEnum {

    REQUEST((byte) 1, "request"),

    RESPONSE((byte) 2, "response");

    private byte code;

    private String name;
}
