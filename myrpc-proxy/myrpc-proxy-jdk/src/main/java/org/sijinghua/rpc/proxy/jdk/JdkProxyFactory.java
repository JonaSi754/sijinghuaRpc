package org.sijinghua.rpc.proxy.jdk;

import org.sijinghua.rpc.proxy.api.BaseProxyFactory;
import org.sijinghua.rpc.proxy.api.ProxyFactory;
import org.sijinghua.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;

@SPIClass
public class JdkProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {

    private final Logger logger = LoggerFactory.getLogger(JdkProxyFactory.class);
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProxy(Class<T> clazz) {
        logger.info("使用基于JDK的动态代理方式...");
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                objectProxy
        );
    }
}
