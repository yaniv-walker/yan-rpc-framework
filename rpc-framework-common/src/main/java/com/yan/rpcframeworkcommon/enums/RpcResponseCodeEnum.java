package com.yan.rpcframeworkcommon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for RPC response.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/21 0021
 * @since JDK 1.8.0
 */
@AllArgsConstructor
@Getter
public enum RpcResponseCodeEnum {

    SUCCESS(200, "The remote call is successful"),

    FAIL(500, "The remote call is fail");

    /**
     * Represent the status of the response.
     */
    private final int code;

    /**
     * The message to which the code corresponds.
     */
    private final String message;
}
