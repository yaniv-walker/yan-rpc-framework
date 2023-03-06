package com.yan.rpcframeworkcommon.exception;

/**
 * .
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/3/3 0003
 * @since JDK 1.8.0
 */
public class RpcException extends RuntimeException {

    public RpcException(final String message) {
        super(message);
    }

    public RpcException(final String message, Throwable cause) {
        super(message, cause);
    }
}
