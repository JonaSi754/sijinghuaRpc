package org.sijinghua.rpc.proxy.api.async;

import org.sijinghua.rpc.proxy.api.future.RpcFuture;

public interface IAsyncObjectProxy {
    /**
     * 异步代理对象调用方法
     * @param funcName 方法名称
     * @param args 方法参数
     * @return 封装好的RpcFuture对象
     */
    RpcFuture call(String funcName, Object... args);
}
