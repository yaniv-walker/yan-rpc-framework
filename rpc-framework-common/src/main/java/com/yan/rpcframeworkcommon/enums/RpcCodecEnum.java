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

    JAVA_BASIC((byte) 0, "java"),

    KRYO((byte) 1, "kryo");

    private final byte code;

    private final String name;

    public static RpcCodecEnum getEnumByCode(final byte code) {
        for (RpcCodecEnum e : RpcCodecEnum.values()) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }
}
