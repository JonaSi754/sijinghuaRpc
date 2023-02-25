package org.sijinghua.rpc.proxy.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import org.sijinghua.rpc.proxy.api.BaseProxyFactory;
import org.sijinghua.rpc.proxy.api.ProxyFactory;
import org.sijinghua.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SPIClass
public class ByteBuddyProxyFactory extends BaseProxyFactory implements ProxyFactory {
    private final Logger logger = LoggerFactory.getLogger(ByteBuddyProxyFactory.class);

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProxy(Class<T> clazz) {
        try {
            logger.info("使用基于ByteBuddy的动态代理...");
            return (T) new ByteBuddy().subclass(Object.class).implement(clazz)
                    .intercept(InvocationHandlerAdapter.of(objectProxy)).make()
                    .load(ByteBuddyProxyFactory.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            logger.error("ByteBuddy proxy throws exception: ", e);
        }
        return null;
    }
}
