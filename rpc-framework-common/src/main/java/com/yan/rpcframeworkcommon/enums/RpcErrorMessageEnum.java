package com.yan.rpcframeworkcommon.enums;

import lombok.Getter;

/**
 * error message enum.
 *
 * @author yanjiaqi
 * @version 1.0.0 2023-03-16
 * @since JDK 1.8.0
 */
@Getter
public enum RpcErrorMessageEnum {

    RESPONSE_HAVE_NOT_CODE("Response haven't code"),

    RESPONSE_FAILED("Response failed"),

    RESPONSE_NOT_MATCH("Response not match"),

    UNKNOWN_CLIENT("Unknown client");

    private String message;

    RpcErrorMessageEnum(String message) {
        this.message = message;
    }

}
