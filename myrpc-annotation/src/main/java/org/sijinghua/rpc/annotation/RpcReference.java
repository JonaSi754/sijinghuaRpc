package org.sijinghua.rpc.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Autowired
public @interface RpcReference {
    /**
     * 版本号
     */
    String version() default "1.0.0";

    /**
     * 注册中心类型，包含zookeeper、nacos、etcd、consul
     */
    String registryType() default "zookeeper";

    /**
     * 注册中心地址
     */
    String registryAddress() default "127.0.0.1:2181";

    /**
     * 复杂均衡类型，默认zk的一致性Hash
     */
    String loadBalanceType() default "zkconsistenthash";

    /**
     * 序列化类型，包含protostuff、kryo、json、jdk、hessian2、fst
     */
    String serializationType() default "protostuff";

    /**
     * 超时时间，默认5秒
     */
    long timeout() default 5000;

    /**
     * 是否异步执行，默认同步
     */
    boolean async() default false;

    /**
     * 是否单向调用，默认否
     */
    boolean oneway() default false;

    /**
     * 代理类型，jdk：jdk代理，javassist：javassist代理，cglib：cglib代理
     */
    String proxy() default "jdk";

    /**
     * 服务分组，默认为空
     */
    String group() default "";
}
