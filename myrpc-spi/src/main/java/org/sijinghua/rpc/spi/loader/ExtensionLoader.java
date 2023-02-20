package org.sijinghua.rpc.spi.loader;

import org.apache.commons.lang3.StringUtils;
import org.sijinghua.rpc.spi.annotation.SPI;
import org.sijinghua.rpc.spi.annotation.SPIClass;
import org.sijinghua.rpc.spi.factory.ExtensionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class ExtensionLoader<T> {
    private static final Logger logger = LoggerFactory.getLogger(ExtensionLoader.class);

    private static final String SERVICE_DIRECTORY = "META-INF/services/";
    private static final String SIJINGHUA_DIRECTORY = "META-INF/sijinghua/";
    private static final String SIJINGHUA_DIRECTORY_EXTERNAL = "META-INF/sijinghua/external/";
    private static final String SIJINGHUA_DIRECTORY_INTERNAL = "META-INF/sijinghua/internal/";

    private static final String[] SPI_DIRECTORIES = new String[] {
            SERVICE_DIRECTORY,
            SIJINGHUA_DIRECTORY,
            SIJINGHUA_DIRECTORY_EXTERNAL,
            SIJINGHUA_DIRECTORY_INTERNAL
    };

    private static final Map<Class<?>, ExtensionLoader<?>> LOADERS = new ConcurrentHashMap<>();

    private final Class<T> clazz;

    private final ClassLoader classLoader;

    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    private final Map<Class<?>, Object> spiClassInstances = new ConcurrentHashMap<>();

    private String cachedDefaultName;

    /**
     * Instantiates a new Extension Loader.
     *
     * @param clazz the clazz
     * @param cl    the class loader
     */
    private ExtensionLoader(final Class<T> clazz, final ClassLoader cl) {
        this.clazz = clazz;
        this.classLoader = cl;
        if (!Objects.equals(clazz, ExtensionFactory.class)) {
            ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getExtensionClasses();
        }
    }

    /**
     * Gets a extension loader.
     *
     * @param clazz the clazz
     * @param cl    the class loader
     * @return      extension loader
     * @param <T>   the type parameter
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(final Class<T> clazz, final ClassLoader cl) {

        Objects.requireNonNull(clazz, "extension clazz is null");

        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("extension clazz (" + clazz + ") is not interface!");
        }
        if (!clazz.isAnnotationPresent(SPI.class)) {
            throw new IllegalArgumentException("extension clazz (" + clazz + ") without @" + SPI.class + " annotation");
        }
        ExtensionLoader<T> extensionLoader = (ExtensionLoader<T>) LOADERS.get(clazz);
        if (Objects.nonNull(extensionLoader)) {
            return extensionLoader;
        }
        LOADERS.putIfAbsent(clazz, new ExtensionLoader<>(clazz, cl));
        return (ExtensionLoader<T>) LOADERS.get(clazz);
    }

    /**
     * 直接获取想要获取的类实例
     *
     * @param clazz 接口的class实例
     * @param name SPI名称
     * @return 泛型实例
     * @param <T> 泛型类型
     */
    public static <T> T getExtension(final Class<T> clazz, String name) {
        return StringUtils.isEmpty(name) ?
                getExtensionLoader(clazz).getDefaultSpiClassInstance() : getExtensionLoader(clazz).getSpiClassInstance(name);
    }
    /**
     * 获取Extension的Loader
     *
     * @param clazz the clazz
     * @return the extension loader
     * @param <T> the type parameter
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(final Class<T> clazz) {
        return getExtensionLoader(clazz, ExtensionLoader.class.getClassLoader());
    }

    /**
     * 获取默认的SPI类实例
     *
     * @return default spi class instance
     */
    public T getDefaultSpiClassInstance() {
        getExtensionClasses();
        if (StringUtils.isBlank(cachedDefaultName)) {
            return null;
        }
        return getSpiClassInstance(cachedDefaultName);
    }

    /**
     * 根据类名获取SPI实例
     *
     * @param name the name
     * @return the spi class instance
     */
    public T getSpiClassInstance(final String name) {
        if (StringUtils.isBlank(name)) {
            throw new NullPointerException("get spi class name is null");
        }
        Holder<Object> objectHolder = cachedInstances.get(name);
        if (Objects.isNull(objectHolder)) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            objectHolder = cachedInstances.get(name);
        }
        Object value = objectHolder.getValue();
        if (Objects.isNull(value)) {
            synchronized (cachedInstances) {
                value = objectHolder.getValue();
                if (Objects.isNull(value)) {
                    value = createExtension(name);
                    objectHolder.setValue(value);
                }
            }
        }
        return (T) value;
    }

    /**
     * 获取所有的SPI实例
     *
     * @return list. spi instances
     */
    public List<T> getSpiClassInstances() {
        Map<String, Class<?>> extensionClasses = this.getExtensionClasses();
        if (extensionClasses.isEmpty()) {
            return Collections.emptyList();
        }
        if (Objects.equals(extensionClasses.size(), cachedInstances.size())) {
            return (List<T>) this.cachedInstances.values().stream().map(e -> {
                return e.getValue();
            }).collect(Collectors.toList());
        }
        List<T> instances = new ArrayList<>();
        extensionClasses.forEach((name, v) -> {
            T instance = getSpiClassInstance(name);
            instances.add(instance);
        });
        return instances;
    }

    /**
     * 根据Extension的名称创建一个Extension实例
     *
     * @param name 名称
     * @return spiClassInstances中取出的实例或是新建的实例
     */
    @SuppressWarnings("unchecked")
    private T createExtension(final String name) {
        // 调用getExtensionClasses方法，获取extension名称对应的class
        Class<?> aClass = getExtensionClasses().get(name);
        if (Objects.isNull(aClass)) {
            throw new IllegalArgumentException("name is error");
        }
        Object o = spiClassInstances.get(aClass);
        // 如果spiClassInstances中没有存相应的类实例，则创建一个并放进去
        if (Objects.isNull(o)) {
            try {
                spiClassInstances.putIfAbsent(aClass, aClass.newInstance());
                o = spiClassInstances.get(aClass);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("Extension instance (name: " + name + ", class: " + aClass +
                        ") could not be instantiated" + e.getMessage(), e);
            }
        }
        return (T) o;
    }

    /**
     * 获取类名到class的映射：classes
     * cachedClasses是classes的Holder，如果Holder值有则返回
     * 否则调用loadExtensionClasses获取classes，并装载到cachedClasses中
     *
     * @return classes
     */
    public Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.getValue();
        if (Objects.isNull(classes)) {
            synchronized (cachedClasses) {
                classes = cachedClasses.getValue();
                if (Objects.isNull(classes)) {
                    classes = loadExtensionClass();
                    cachedClasses.setValue(classes);
                }
            }
        }
        return classes;
    }

    /**
     * 把clazz上的annotation里的value作为默认名称cachedDefaultName
     * 建立SPI类名称到Class的映射，调用loadDirectory装载这个map，并返回
     *
     * @return 类名称到其Class的映射
     */
    private Map<String, Class<?>> loadExtensionClass() {
        SPI annotation = clazz.getAnnotation(SPI.class);
        if (Objects.nonNull(annotation)) {
            String value = annotation.value();
            if (StringUtils.isNotBlank(value)) {
                cachedDefaultName = value;
            }
        }
        Map<String, Class<?>> classes = new HashMap<>(16);
        loadDirectory(classes);
        return classes;
    }

    /**
     * 按文件名索引获得所需要的类的URL（目录）
     * 调用loadResources()装载loadExtensionClass中建立的map（classes）
     *
     * @param classes 类名称到其class的映射
     */
    private void loadDirectory(final Map<String, Class<?>> classes) {
        // 遍历所有的SPI目录
        for (String directory : SPI_DIRECTORIES) {
            // 我们需要的文件的完整路径是 目录 + 类名
            String fileName = directory + clazz.getName();
            try {
                // 有类加载器就用当前类加载器加载，否则调用系统方式加载
                Enumeration<URL> urls = Objects.nonNull(this.classLoader) ? classLoader.getResources(fileName)
                        : ClassLoader.getSystemResources(fileName);
                // 如果找到了对应的资源，按照url装填到classes中
                if (Objects.nonNull(urls)) {
                    while (urls.hasMoreElements()) {
                        URL url = urls.nextElement();
                        loadResources(classes, url);
                    }
                }
            } catch (IOException e) {
                logger.error("load extension class error: {}", fileName, e);
            }
        }
    }

    /**
     * 按loadDirectory()给出的url（目录）找到资源
     * 打开后获得文件的name和path，调用loadClass()装载到classes中
     *
     * @param classes 类名称到其class的映射
     * @param url 文件路径
     * @throws IOException IOException
     */
    private void loadResources(final Map<String, Class<?>> classes, URL url) throws IOException {
        // 使用Properties类对url给出的路径遍历访问
        try (InputStream inputStream = url.openStream()) {
            Properties properties = new Properties();
            properties.load(inputStream);
            properties.forEach((k, v) -> {
                String name = (String) k;
                String classPath = (String) v;
                if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(classPath)) {
                    try {
                        loadClass(classes, name, classPath);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException("load extension resource error", e);
                    }
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException("load extension resource error", e);
        }
    }

    /**
     * 使用反射创建class对象，并放入类名到class的映射中
     *
     * @param classes 类名到其class的映射
     * @param name class名称
     * @param classPath class路径
     * @throws ClassNotFoundException ClassNotFoundException
     */
    private void loadClass(final Map<String, Class<?>> classes,
                           final String name, final String classPath) throws ClassNotFoundException {
        // 根据路径，通过反射加载类实例
        Class<?> subClass = Objects.nonNull(this.classLoader) ?
                Class.forName(classPath, true, this.classLoader) : Class.forName(classPath);
        // 判断clazz是否是subclass的父接口
        if (!clazz.isAssignableFrom(subClass)) {
            throw new IllegalStateException("load extension resources error," + subClass + " subtype is not of " + clazz);
        }
        // 判断subclass是否被SPIClass修饰
        if (!subClass.isAnnotationPresent(SPIClass.class)) {
            throw new IllegalStateException("load extension resources error," + subClass + " without @" +
                    SPIClass.class + " annotation");
        }
        // 将建立好的class对象作为value放入classes中
        Class<?> oldClass = classes.get(name);
        if (Objects.isNull(oldClass)) {
            classes.put(name, subClass);
        } else if (!Objects.equals(oldClass, subClass)) {
            // 如果已经存在一个映射，且不相同，说明有一个名称对应着不同的class，class重复
            throw new IllegalStateException("load extension resources error, Duplicate class " + clazz.getName() +
                    " name " + name + " on " + oldClass);
        }
    }

    /**
     * The type holder
     *
     * @param <T> the type parameter
     */
    public static class Holder<T> {
        private volatile T value;

        /**
         * Gets value
         * @return the value
         */
        public T getValue() {
            return value;
        }

        /**
         * Sets value
         * @param value the value
         */
        public void setValue(final T value) {
            this.value = value;
        }
    }
}
