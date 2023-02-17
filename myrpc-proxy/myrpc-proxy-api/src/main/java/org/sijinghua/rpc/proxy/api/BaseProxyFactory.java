package org.sijinghua.rpc.proxy.api;

import org.sijinghua.rpc.proxy.api.config.ProxyConfig;
import org.sijinghua.rpc.proxy.api.object.ObjectProxy;

public abstract class BaseProxyFactory<T> implements ProxyFactory {
    protected ObjectProxy<T> objectProxy;

    @Override
    public <T> void init(ProxyConfig<T> proxyConfig) {
        this.objectProxy = new ObjectProxy(
                proxyConfig.getClazz(),
                proxyConfig.getServiceVersion(),
                proxyConfig.getServiceGroup(),
                proxyConfig.getTimeout(),
                proxyConfig.getRegistryService(),
                proxyConfig.getConsumer(),
                proxyConfig.getSerializationType(),
                proxyConfig.getAsync(),
                proxyConfig.getOneway()
        );
    }
}
