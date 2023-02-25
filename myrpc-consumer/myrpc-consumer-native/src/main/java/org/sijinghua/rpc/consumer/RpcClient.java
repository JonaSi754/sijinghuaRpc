package org.sijinghua.rpc.consumer;

import org.apache.commons.lang3.StringUtils;
import org.sijinghua.rpc.common.exception.RegistryException;
import org.sijinghua.rpc.consumer.common.RpcConsumer;
import org.sijinghua.rpc.proxy.api.ProxyFactory;
import org.sijinghua.rpc.proxy.api.async.IAsyncObjectProxy;
import org.sijinghua.rpc.proxy.api.config.ProxyConfig;
import org.sijinghua.rpc.proxy.api.object.ObjectProxy;
import org.sijinghua.rpc.proxy.jdk.JdkProxyFactory;
import org.sijinghua.rpc.registry.api.RegistryService;
import org.sijinghua.rpc.registry.api.config.RegistryConfig;
import org.sijinghua.rpc.registry.zookeeper.ZookeeperRegistryService;
import org.sijinghua.rpc.spi.loader.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    /**
     * 服务版本
     */
    private String serviceVersion;

    /**
     * 服务分组
     */
    private String serviceGroup;

    /**
     * 序列化类型
     */
    private String serializationType;

    /**
     * 超时时间
     */
    private long timeout = 15000;

    /**
     * 服务注册与发现接口
     */
    private RegistryService registryService;

    /**
     * 动态代理类型
     */
    private String proxy;

    /**
     * 是否异步调用
     */
    private boolean async;

    /**
     * 是否单向调用
     */
    private boolean oneway;

    public RpcClient(String registryAddress, String registryType, String proxy, String serviceVersion, String serviceGroup,
                     String serializationType, long timeout, boolean async, boolean oneway) {
        this.serviceVersion = serviceVersion;
        this.serviceGroup = serviceGroup;
        this.proxy = proxy;
        this.serializationType = serializationType;
        this.timeout = timeout;
        this.registryService = this.getRegistryService(registryAddress, registryType);
        this.async = async;
        this.oneway = oneway;
    }

    private RegistryService getRegistryService(String registryAddress, String registryType) {
        if (StringUtils.isEmpty(registryType)) {
            throw new IllegalArgumentException("registry type is null");
        }
        // TODO 后续SPI扩展
        RegistryService registryService = new ZookeeperRegistryService();
        try {
            registryService.init(new RegistryConfig(registryAddress, registryType));
        } catch (Exception e) {
            logger.error("Rpc client init registry service throws exception: ", e);
            throw new RegistryException(e.getMessage(), e);
        }
        return registryService;
    }

    public <T> T create(Class<T> interfaceClass) {
        ProxyFactory proxyFactory = ExtensionLoader.getExtension(ProxyFactory.class, proxy);
        proxyFactory.init(new ProxyConfig<T>(interfaceClass, serviceVersion, serviceGroup, timeout,
                registryService, RpcConsumer.getInstance(), serializationType, async, oneway));
        return proxyFactory.getProxy(interfaceClass);
    }

    public <T>IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new ObjectProxy<T>(interfaceClass, serviceVersion, serviceGroup, timeout,
                registryService, RpcConsumer.getInstance(), serializationType, async, oneway);
    }

    public void shutdown() {
        RpcConsumer.getInstance().close();
    }
}
