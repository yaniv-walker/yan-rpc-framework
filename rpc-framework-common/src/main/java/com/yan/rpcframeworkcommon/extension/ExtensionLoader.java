package com.yan.rpcframeworkcommon.extension;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI extension loader.
 *
 * @author yanjiaqi
 * @version 1.0.0
 * @date 2023/3/13 0013
 * @since JDK 1.8.0
 */
@Slf4j
public class ExtensionLoader<T> {

    // first, get or create the extension loader of the extension.
    // second, get the extension of the extension loader.
    // third, create the extension of the extension loader.
    // fourth, get extension classes of the extension loader from the setting file.
    // finally, load resources of the extension loader from the setting file.


    private static final String EXTENSION_DIRECTORY = "META-INF/rpc-framework/";

    /**
     * key: extension interface.
     * value: extension loader of the extension.
     */
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    /**
     * key: extension implementation class.
     * value: extension instance.
     */
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    /**
     * purpose: bind name and extension instance to make it more efficient,
     * otherwise, you would need to get extension classes first.
     *
     * key: extension implementation name that is definition in setting file.
     * value: extension instance.
     */
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    /**
     * key: extension implementation name that is definition in setting file.
     * value: extension implementation class.
     */
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    /**
     * the interface of extension.
     */
    private final Class<?> type;

    public ExtensionLoader(final Class<?> type) {
        this.type = type;
    }

    /**
     * get the extension loader of the extension.
     * @param type extension interface
     * @param <S> the extension type
     * @return extension loader of the extension
     */
    public static <S> ExtensionLoader<S> getExtensionLoader(final Class<S> type) {
        if (null == type) {
            throw new IllegalArgumentException("Extension type should not be null.");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type must be an interface.");
        }
        if (null == type.getAnnotation(SPI.class)) {
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }

        // firstly get from cache, if not hit, create one
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        if (null == extensionLoader) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<S>(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }

        return extensionLoader;
    }


    /**
     * get the extension of the extension loader.
     * @param name extension implementation name that is definition in setting file.
     * @return extension of the extension loader
     */
    public T getExtension(final String name) {
        if (Strings.isBlank(name)) {
            throw new IllegalArgumentException("Extension name should not be null or empty.");
        }

        // first, get from cache, if not hit, create one
        Holder<Object> holder = cachedInstances.get(name);
        if (null == holder) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }

        Object instance = holder.get();
        // create a singleton extension if no instance exists
        if (null == instance) {
            synchronized(holder) {
                instance = holder;
                if (null == instance) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    /**
     * create extension by name.
     * need to load classes from setting file first.
     * @param name extension implementation name that is definition in setting file.
     * @return created extension instance
     */
    private Object createExtension(final String name) {

        return null;
    }

    /**
     * get extension classes by type.
     * @return all extension classes that is implementation of the interface "type"
     */
//    private Map<String, Class<?>> getExtensionClasses() {
//
//    }
}
