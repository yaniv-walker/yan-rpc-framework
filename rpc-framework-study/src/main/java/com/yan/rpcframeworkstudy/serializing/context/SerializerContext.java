package com.yan.rpcframeworkstudy.serializing.context;

import com.yan.rpcframeworkcommon.enums.RpcCodecEnum;
import com.yan.rpcframeworkcommon.exception.SerializeException;
import com.yan.rpcframeworkstudy.serializing.ISerializer;
import com.yan.rpcframeworkstudy.serializing.java.JavaSerializer;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * serializer strategy context.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/25 0025
 * @since JDK 1.8.0
 */
public class SerializerContext {
    /**
     * store all serializer into the map.
     */
    private static final Map<RpcCodecEnum, ISerializer> SERIALIZER_STRATEGY_MAP = new ConcurrentHashMap<>();

    static {
//        final ServiceLoader<ISerializer> loaders = ServiceLoader.load(ISerializer.class);
//        loaders.iterator().forEachRemaining(serializer -> {
//            SERIALIZER_STRATEGY_MAP.put(serializer.getCodecEnum(), serializer);
//        });
        final ISerializer javaSerializer = new JavaSerializer();
        SERIALIZER_STRATEGY_MAP.put(javaSerializer.getCodecEnum(), javaSerializer);
    }

    /**
     * serialize the obj.
     * @param obj to be serialized
     * @return bytes
     */
    public byte[] serialize(final byte rpcCodecEnumCode, final Object obj) {
        final ISerializer serializer = getSerializer(rpcCodecEnumCode);
        return serializer.serialize(obj);
    }

    /**
     * deserialize the bytes to clazz.
     * @param bytes to be deserialized
     * @param clazz representing the type of deserialized result
     * @return the deserialized result
     * @param <T> the type of clazz
     */
    public <T> T deserialize(final byte rpcCodecEnumCode, byte[] bytes, Class<T> clazz) {
        final ISerializer serializer = getSerializer(rpcCodecEnumCode);
        return serializer.deserialize(bytes, clazz);
    }

    private ISerializer getSerializer(byte rpcCodecEnumCode) {
        final RpcCodecEnum rpcCodecEnum = RpcCodecEnum.getEnumByCode(rpcCodecEnumCode);
        if (null == rpcCodecEnum) {
            throw new SerializeException("serialization failed, codec type incorrect");
        }

        final ISerializer serializer = SERIALIZER_STRATEGY_MAP.get(rpcCodecEnum);
        if (null == serializer) {
            throw new SerializeException("serialization failed, codec type incorrect");
        }
        return serializer;
    }

}
