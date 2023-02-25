package com.yan.rpcframeworkstudy.serializing.java;

import com.yan.rpcframeworkcommon.enums.RpcCodecEnum;
import com.yan.rpcframeworkcommon.exception.SerializeException;
import com.yan.rpcframeworkstudy.serializing.ISerializer;

import java.io.*;

/**
 * Java default implementation for serializer.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/25 0025
 * @since JDK 1.8.0
 */
public class JavaSerializer implements ISerializer {
    /**
     * get codec enum of the serializer.
     *
     * @return codec enum
     */
    @Override
    public RpcCodecEnum getCodecEnum() {
        return RpcCodecEnum.JAVA_BASIC;
    }

    /**
     * serialize the obj.
     *
     * @param obj to be serialized
     * @return bytes
     */
    @Override
    public byte[] serialize(final Object obj) {

        try(final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            outputStream.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new SerializeException("serialize failed");
        }
    }

    /**
     * deserialize the bytes to clazz.
     *
     * @param bytes to be deserialized
     * @param clazz representing the type of deserialized result
     * @return the deserialized result
     */
    @Override
    public <T> T deserialize(final byte[] bytes, final Class<T> clazz) {
        try(final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            final ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream)) {

            final Object object = inputStream.readObject();
            return clazz.cast(object);
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializeException("deserialize failed", e);
        }
    }
}
