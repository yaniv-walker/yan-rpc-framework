package com.yan.rpcframeworkstudy.serializing;

import com.yan.rpcframeworkcommon.enums.RpcCodecEnum;

/**
 * Serializer interface.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/25 0025
 * @since JDK 1.8.0
 */
public interface ISerializer {

    /**
     * get codec enum of the serializer.
     * @return codec enum
     */
    RpcCodecEnum getCodecEnum();

    /**
     * serialize the obj.
     * @param obj to be serialized
     * @return bytes
     */
    byte[] serialize(Object obj);

    /**
     * deserialize the bytes to clazz.
     * @param bytes to be deserialized
     * @param clazz representing the type of deserialized result
     * @return the deserialized result
     * @param <T> the type of clazz
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
