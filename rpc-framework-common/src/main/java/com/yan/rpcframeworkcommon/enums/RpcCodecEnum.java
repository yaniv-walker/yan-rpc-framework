package com.yan.rpcframeworkcommon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * codec.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-02-24
 * @since JDK 1.8.0
 */
@AllArgsConstructor
@Getter
public enum RpcCodecEnum {

    JAVA_BASIC((byte) 0, "basic");

    private byte code;

    private String name;
}
