package org.sijinghua.rpc.consumer.common.context;

import org.sijinghua.rpc.proxy.api.future.RpcFuture;

/**
 * Rpc上下文
 */
public class RpcContext {

    private RpcContext(){
    }

    /**
     * Rpc实例
     */
    private static final RpcContext AGENT = new RpcContext();

    /**
     * 存放RpcFuture的InheritableThreadLocal
     */
    private static final InheritableThreadLocal<RpcFuture> RPC_FUTURE_INHERITABLE_THREAD_LOCAL = new InheritableThreadLocal<>();

    /**
     * 获取上下文
     * @return RPC服务的上下文信息
     */
    public static RpcContext getContext() {
        return AGENT;
    }

    public void setRPCFuture(RpcFuture rpcFuture) {
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.set(rpcFuture);
    }

    public RpcFuture getRPCFuture() {
        return RPC_FUTURE_INHERITABLE_THREAD_LOCAL.get();
    }

    public void removeRPCFuture() {
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.remove();
    }
}
