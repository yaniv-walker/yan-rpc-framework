package com.yan.rpcframeworkstudy.serializing.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.yan.rpcframeworkcommon.enums.RpcCodecEnum;
import com.yan.rpcframeworkcommon.exception.SerializeException;
import com.yan.rpcframeworkstudy.network.dto.RpcRequest;
import com.yan.rpcframeworkstudy.network.dto.RpcResponse;
import com.yan.rpcframeworkstudy.serializing.ISerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * kryo serialization.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/25 0025
 * @since JDK 1.8.0
 */
public class KryoSerializer implements ISerializer {

    /**
     * Use ThreadLocal to store Kryo objects, because Kryo is not thread safe.
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
       final Kryo kryo = new Kryo();
       kryo.register(RpcRequest.class);
       kryo.register(RpcResponse.class);
       return kryo;
    });

    /**
     * get codec enum of the serializer.
     *
     * @return codec enum
     */
    @Override
    public RpcCodecEnum getCodecEnum() {
        return RpcCodecEnum.KRYO;
    }

    /**
     * serialize the obj.
     *
     * @param obj to be serialized
     * @return bytes
     */
    @Override
    public byte[] serialize(Object obj) {
        try(final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final Output output = new Output(byteArrayOutputStream)) {

            final Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (IOException e) {
            throw new SerializeException("Kryo serialization failed", e);
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
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try(final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            final Input input = new Input(byteArrayInputStream)) {

            final Kryo kryo = kryoThreadLocal.get();
            final T result = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return result;
        } catch (IOException e) {
            throw new SerializeException("kryo deserialization failed", e);
        }
    }
}
