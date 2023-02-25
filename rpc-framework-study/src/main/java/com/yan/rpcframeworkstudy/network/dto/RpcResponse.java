package com.yan.rpcframeworkstudy.network.dto;

import com.yan.rpcframeworkcommon.enums.RpcResponseCodeEnum;
import lombok.*;

import java.io.Serializable;

/**
 * RPC response object.
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
public class RpcResponse<T> implements Serializable {

    /**
     * To identify the response that corresponds to the request.
     */
    private String requestId;

    /**
     * The code for status of response.
     */
    private Integer code;

    /**
     * The message for error.
     */
    private String message;

    /**
     * Response body.
     */
    private T data;

    /**
     * Response successfully.
     * @param data response result
     * @param requestId the key to identify the response to which the request corresponds
     * @return RpcResponse<T>
     * @param <T> result type
     */
    public static <T> RpcResponse<T> success(final T data, final String requestId) {
        final RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setCode(RpcResponseCodeEnum.SUCCESS.getCode());
        rpcResponse.setMessage(RpcResponseCodeEnum.SUCCESS.getMessage());
        rpcResponse.setRequestId(requestId);
        if (null != data) {
            rpcResponse.setData(data);
        }

        return rpcResponse;
    }

    /**
     * Response fail.
     * @param rpcResponseCodeEnum describe how the server failed
     * @return RpcResponse<T>
     * @param <T> result type
     */
    public static <T> RpcResponse<T> fail(final RpcResponseCodeEnum rpcResponseCodeEnum) {
        final RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setCode(rpcResponseCodeEnum.getCode());
        rpcResponse.setMessage(rpcResponseCodeEnum.getMessage());

        return rpcResponse;
    }
}
