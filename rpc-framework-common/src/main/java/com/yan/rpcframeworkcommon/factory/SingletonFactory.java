package com.yan.rpcframeworkcommon.factory;

import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * to generate singleton object by this factory.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/2/23 0023
 * @since JDK 1.8.0
 */
@NoArgsConstructor
public final class SingletonFactory {

    private static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();

    /**
     * get singleton object by the class.
     * @param clazz class
     * @return singleton object
     * @param <T> class
     */
    public static <T> T getInstance(final Class<T> clazz) {
        if (null == clazz) {
            throw new IllegalArgumentException("get instance failed, class is null");
        }

        final String key = clazz.toString();
        if (OBJECT_MAP.containsKey(key)) {
            return clazz.cast(OBJECT_MAP.get(key));
        }

        return clazz.cast(OBJECT_MAP.computeIfAbsent(key, k -> {
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
