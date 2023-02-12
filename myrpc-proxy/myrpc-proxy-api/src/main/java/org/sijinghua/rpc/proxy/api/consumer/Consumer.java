package org.sijinghua.rpc.proxy.api.consumer;

import org.sijinghua.rpc.protocol.RpcProtocol;
import org.sijinghua.rpc.protocol.request.RpcRequest;
import org.sijinghua.rpc.proxy.api.future.RpcFuture;

public interface Consumer {
    /**
     * 消费者发送request请求
     */
    RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol) throws Exception;
}
