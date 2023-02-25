package org.sijinghua.rpc.proxy.asm.proxy;

import org.sijinghua.rpc.proxy.asm.classloader.ASMClassLoader;
import org.sijinghua.rpc.proxy.asm.factory.ASMGenerateProxyFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.util.concurrent.atomic.AtomicInteger;

public class ASMProxy {
    protected InvocationHandler h;

    // 代理类名计数器
    private static final AtomicInteger PROXY_CNT = new AtomicInteger(0);

    private static final String PROXY_CLASS_NAME_PRE = "$Proxy";

    public ASMProxy(InvocationHandler var1) {
        this.h = var1;
    }

    public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler invocationHandler) throws Exception {
        // 生成代理类Class
        Class<?> proxyClass = generate(interfaces);
        Constructor<?> constructor = proxyClass.getConstructor(InvocationHandler.class);
        return constructor.newInstance(invocationHandler);
    }

    /**
     * 生成代理类Class
     * @param interfaces 接口的Class类型
     * @return 代理的Class对象
     * @throws ClassNotFoundException ClassNotFoundException
     */
    private static Class<?> generate(Class<?>[] interfaces) throws ClassNotFoundException {
        String proxyClassName = PROXY_CLASS_NAME_PRE + PROXY_CNT.getAndIncrement();
        byte[] codes = ASMGenerateProxyFactory.generateClass(interfaces, proxyClassName);
        // 使用自定义类加载器加载字节码
        ASMClassLoader asmClassLoader = new ASMClassLoader();
        asmClassLoader.add(proxyClassName, codes);
        return asmClassLoader.loadClass(proxyClassName);
    }
}
