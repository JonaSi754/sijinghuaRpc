package org.sijinghua.rpc.proxy.api.consumer;

import org.sijinghua.rpc.protocol.RpcProtocol;
import org.sijinghua.rpc.protocol.request.RpcRequest;
import org.sijinghua.rpc.proxy.api.future.RpcFuture;
import org.sijinghua.rpc.registry.api.RegistryService;

public interface Consumer {
    /**
     * 消费者发送request请求
     */
    RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception;
}
