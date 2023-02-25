package org.sijinghua.rpc.proxy.javassist;

import javassist.util.proxy.MethodHandler;
import org.sijinghua.rpc.proxy.api.BaseProxyFactory;
import org.sijinghua.rpc.proxy.api.ProxyFactory;
import org.sijinghua.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

@SPIClass
public class JavassistProxyFactory extends BaseProxyFactory implements ProxyFactory {
    private final Logger logger = LoggerFactory.getLogger(JavassistProxyFactory.class);
    private javassist.util.proxy.ProxyFactory proxyFactory = new javassist.util.proxy.ProxyFactory();

    @Override
    public <T> T getProxy(Class<T> clazz) {
        try {
            logger.info("使用基于Javassist的动态代理");
            // 设置代理类的父类
            proxyFactory.setInterfaces(new Class[]{clazz});
            proxyFactory.setHandler(new MethodHandler() {
                @Override
                public Object invoke(Object o, Method method, Method method1, Object[] objects) throws Throwable {
                    return objectProxy.invoke(o, method, objects);
                }
            });
            // 通过字节码技术动态创建子类实例
            return (T) proxyFactory.createClass().newInstance();
        } catch (Exception e) {
            logger.error("Javassist proxy throws exception: ", e);
        }
        return null;
    }
}
