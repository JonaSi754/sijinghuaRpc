package org.sijinghua.rpc.proxy.jdk;

import org.sijinghua.rpc.proxy.api.BaseProxyFactory;
import org.sijinghua.rpc.proxy.api.ProxyFactory;
import org.sijinghua.rpc.proxy.api.consumer.Consumer;
import org.sijinghua.rpc.proxy.api.object.ObjectProxy;

import java.lang.reflect.Proxy;

public class JdkProxyFactory<T> extends BaseProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                objectProxy
        );
    }
}
