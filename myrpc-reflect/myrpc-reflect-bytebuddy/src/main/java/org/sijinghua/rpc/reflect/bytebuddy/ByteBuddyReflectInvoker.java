package org.sijinghua.rpc.reflect.bytebuddy;

import net.bytebuddy.ByteBuddy;
import org.sijinghua.rpc.reflect.api.ReflectInvoker;
import org.sijinghua.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

@SPIClass
public class ByteBuddyReflectInvoker implements ReflectInvoker {
    private final Logger logger = LoggerFactory.getLogger(ByteBuddyReflectInvoker.class);

    @Override
    public Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName,
                               Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
        logger.info("use bytebuffy reflect type invoke method...");
        Class<?> childClass = new ByteBuddy().subclass(serviceClass).make()
                .load(ByteBuddyReflectInvoker.class.getClassLoader())
                .getLoaded();
        Object instance = childClass.getDeclaredConstructor().newInstance();
        Method method = childClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(instance, parameters);
    }
}
