package com.yan.rpcframeworkcommon.extension;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
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
    private final Map<String, Holder<T>> cachedInstances = new ConcurrentHashMap<>();

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
        Holder<T> holder = cachedInstances.get(name);
        if (null == holder) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }

        T instance = holder.get();
        // create a singleton extension if no instance exists
        if (null == instance) {
            synchronized(cachedInstances) {
                instance = holder.get();
                if (null == instance) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return instance;
    }

    /**
     * create extension by name.
     * need to load classes from setting file first.
     * @param name extension implementation name that is definition in setting file.
     * @return created extension instance
     */
    private T createExtension(final String name) {
        final Class<?> extensionClass = getExtensionClasses().get(name);
        if (null == extensionClass) {
            throw new IllegalArgumentException("Extension with the name [" + name + "] does not exist");
        }

        T instance = (T) EXTENSION_INSTANCES.get(extensionClass);
        try {
            if (null == instance) {
                EXTENSION_INSTANCES.putIfAbsent(extensionClass, extensionClass.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(extensionClass);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Extension with name [" + name + "] created failed", e);
        }

        return instance;
    }

    /**
     * get extension classes by type.
     * @return all extension classes that is implementation of the interface "type"
     */
    private Map<String, Class<?>> getExtensionClasses() {
        // first, get from cache, if not hit, create one
        Map<String, Class<?>> name2Classes = cachedClasses.get();

        // double check
        if (null == name2Classes) {
            synchronized (cachedClasses) {
                name2Classes = cachedClasses.get();
                if (null == name2Classes) {
                    name2Classes = new HashMap<>();
                    // load all extensions from our extension directory
                    loadDirectory(name2Classes);
                    cachedClasses.set(name2Classes);
                }
            }
        }

        return name2Classes;
    }

    /**
     * load all extensions of the interface of "type".
     * @param name2Classes loaded extensions
     */
    private void loadDirectory(final Map<String, Class<?>> name2Classes) {
        // get class loader of "type"
        final String path = ExtensionLoader.EXTENSION_DIRECTORY + this.type.getName();
        final ClassLoader classLoader = ExtensionLoader.class.getClassLoader();

        // get all resources url with name of interface "type"
        try {
            final Enumeration<URL> urls = classLoader.getResources(path);
            while (urls.hasMoreElements()) {
                // load resource by url
                final URL url = urls.nextElement();
                loadResource(name2Classes, classLoader, url);
            }

        } catch (IOException e) {
            throw new IllegalStateException("Get resources of extension with the path [" + path + "] failed", e);
        }
    }

    /**
     * load resource by url。
     * @param name2Classes loaded extensions
     * @param classLoader load resource by it
     * @param url resource location
     */
    private void loadResource(final Map<String, Class<?>> name2Classes, final ClassLoader classLoader, final URL url) {
        try(final BufferedReader reader =
                    new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            // read every line
            String line;
            while (null != (line = reader.readLine())) {
                // find comment and skip
                final int commentIndex = line.indexOf("#");
                if (commentIndex >= 0) {
                    line = line.substring(0, commentIndex).trim();
                }

                if (line.length() > 0) {
                    // split name and class from the line
                    final int equalIndex = line.indexOf("=");
                    if (equalIndex < 0) {
                        if (log.isErrorEnabled()) {
                            log.error("the resource file with the name [{}] has a line which have not equal sign(=)",
                                    url.getPath());
                        }
                        throw new IllegalStateException("the resource file with the name " + url.getPath()
                                + " has a line which have not equal sign(=)");
                    }
                    final String name = line.substring(0, equalIndex).trim();
                    final String clazzName = line.substring(equalIndex + 1).trim();

                    // our SPI use key-value pair so both of them must not be empty
                    if (name.length() > 0 && clazzName.length() > 0) {
                        final Class<?> clazz = classLoader.loadClass(clazzName);
                        name2Classes.put(name, clazz);
                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Load resource of extension with the url [" + url + "] failed", e);
        }
    }
}
